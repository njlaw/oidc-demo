# oidc-demo Project

This demo project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/.

## Azure AD Setup

1. Create an app registration
2. Configure a redirect URI: `http://localhost:8080/auth/callback`
3. In `Authentication`, enable ID tokens
4. Generate a client secret on the `Certificates & secrets` page.
5. In `Token configuration`, add the following claims using the default
   settings and grant Graph API access when it prompts:
   - email
   - family_name
   - given_name
   - groups
   - preferred_username
6. Create an AD group for admin access to the demo app
7. Add a test user to the group you created in (5).

## Running the application in dev mode

Set the following environment variables:

 - `QUARKUS_OIDC_CREDENTIALS_SECRET=<clientSecret>` 
 
   Secret `value` from App Registration Certificates & secrets page
 

 - `QUARKUS_OIDC_CLIENT_ID=<clientId>`

   `Application (client) ID` on the App Registration Overview page


 - `QUARKUS_OIDC_AUTH_SERVER_URL=https://login.microsoftonline.com/<tenantId>/v2.0/`

   `Directory (tenant) ID` on the App Registration Overview page


 - `MY_APP_ADMIN_GROUP_OBJECT_ID=<objectId>`

   `Object Id` on the Azure AD group page


You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

You should now be able to access `http://localhost:8080/` in a web browser and login via Azure AD.
All users in the directory will have access, but only members of the Admin group you created can
see the admin page.

## Packaging and deploying the application

Note that this demo was prepared to be deployed onto a kubernetes cluster in AWS.  If you are deploying
locally or to another environment, there may be some changes necessary.  In particular the ingress 
configuration will be different.

The application can be packaged using:

```shell script
./mvnw clean package \
  -Dquarkus.container-image.push=true \
  -Dquarkus.container-image.group=<repositoryPrefix> \
  -Dquarkus.container-image.registry=<registry>
```

This will build `linux/amd64` and `linux/arm64/v8` docker images and push them to
`<registry>/<repositoryPrefix>/oidc-demo:<version>`.  It will also generate a base
Kubernetes manifest in `target/kubernetes/kubernetes.yml` that you can customize
prior to deploying.

If you do not want to build multi-platform images, you can set 

```
-Dquarkus.jib.platforms=linux/amd64
```

### Environment setup

These steps can be done manually or via GitOps-style configuration. 

1. Create a namespace into which to deploy the oidc-demo
2. Create a ConfigMap called `oidc-demo-config` with the key-value pair `admin-group-object-id: <objectId>`
3. Create a Secret called `oidc-config` with the key-value pairs:
   - `auth-server-url: https://login.microsoftonline.com/<tenantId>/v2.0/`
   - `client-id: <clientId>`
   - `client-secret: <clientSecret>`
   
   The values in angle brackets can be found in the same location as for the dev mode setup.
   Please note that in a real production deployment, you would not typically share the same
   App Registration.
4. Create an appropriate Ingress configuration with SSL and DNS.
5. Add the chosen DNS name to the App Registration authentication callback URIs.
   E.g,. `https://oidc-demo.example.com/auth/callback`.

You should now be able to access `https://oidc-demo.example.com/` (obviously replace this with your real
DNS name) in a web browser and login via Azure AD.
