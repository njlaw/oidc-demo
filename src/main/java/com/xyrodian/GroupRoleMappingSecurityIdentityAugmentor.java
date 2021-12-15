package com.xyrodian;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jwt.MalformedClaimException;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.function.Supplier;

@ApplicationScoped
public class GroupRoleMappingSecurityIdentityAugmentor implements SecurityIdentityAugmentor {
    @ConfigProperty(name = "demo.role-group-mapping.admin")
    List<String> adminRoleGroups;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        return Uni.createFrom().item(build(identity));
    }

    private Supplier<SecurityIdentity> build(SecurityIdentity identity) {
        if (identity.isAnonymous()) {
            return () -> identity;
        } else {
            // create a new builder and copy principal, attributes, credentials and roles from the original identity
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);

            try {
                if (identity.getPrincipal() instanceof OidcJwtCallerPrincipal) {
                    OidcJwtCallerPrincipal jwt = (OidcJwtCallerPrincipal) identity.getPrincipal();
                    if (jwt.getClaims().getStringListClaimValue("groups").stream().anyMatch(group -> adminRoleGroups.contains(group)))
                        builder.addRole("admin");
                }
            } catch (MalformedClaimException ignore) {
            }

            return builder::build;
        }
    }
}
