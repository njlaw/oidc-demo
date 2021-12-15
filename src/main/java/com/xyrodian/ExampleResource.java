package com.xyrodian;

import io.quarkus.oidc.IdToken;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RoutingContextImpl;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.NoCache;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/")
public class ExampleResource {

    private static final Set<String> DISALLOWED_CLAIM_NAMES = Set.of("raw_token");

    @Inject
    @IdToken
    JsonWebToken idToken;

    @GET
    @Path("/")
    @NoCache
    @Produces(MediaType.TEXT_HTML)
    public String hello() {
        return navBar() + "<p>Hello, " + idToken.getClaim("name") + "!</p>";
    }

    @GET
    @Path("headers")
    @NoCache
    @Produces(MediaType.TEXT_HTML)
    public String headers(HttpHeaders headers, RoutingContext rc) {
        RoutingContextImpl rc2 = (RoutingContextImpl)rc;
        return navBar() + "<h4>Request Headers:</h4><pre>" + headers.getRequestHeaders().entrySet().stream().map(e -> e.getKey() + ": " + String.join(",", e.getValue())).collect(Collectors.joining("\n")) + "</pre>" +
                "<h4>RoutingContext</h4><pre>" +
                "host: " + rc2.request().host() + "\n" +
                "absoluteURI: " + rc2.request().absoluteURI() + "\n" +
                "authority: " + URI.create(rc2.request().absoluteURI()).getAuthority() + "\n" +
                "</pre>";
    }

    @GET
    @Path("claims")
    @NoCache
    @Produces(MediaType.TEXT_HTML)
    public String claims() {
        return navBar() + "<p>" + idToken.getClaimNames().stream()
                .filter(claimName -> !DISALLOWED_CLAIM_NAMES.contains(claimName))
                .map(claimName -> "<a href=\"/claims/" + claimName + "\">" + claimName + "</a>")
                .collect(Collectors.joining(", ")) + "</p>";
    }

    @GET
    @Path("claims/{claimName}")
    @NoCache
    @Produces(MediaType.TEXT_HTML)
    public String claims(@PathParam("claimName") String claimName) {
        return navBar() + "<p>" + idToken.getClaim(claimName).toString() + "</p>";
    }

    @GET
    @Path("admin")
    @NoCache
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("admin")
    public String admin() {
        return navBar() + "<p>You are on the admin page.</p>";
    }

    protected String navBar() {
        return "<div><a href=\"/\">Hello</a> | <a href=\"/claims\">Claims</a> | <a href=\"/admin\">Admin</a> | <a href=\"/logout\">Logout</a></div>";
    }
}
