using Duende.IdentityServer.Models;

namespace IdentityTest
{
    public static class Config
    {
        public static IEnumerable<IdentityResource> IdentityResources =>
            new IdentityResource[]
            {
                new IdentityResources.OpenId(),
                new IdentityResources.Profile(),
            };

        public static IEnumerable<ApiScope> ApiScopes =>
            new ApiScope[]
            {
                new ApiScope("SampleAPI"),
            };

        public static IEnumerable<Client> Clients =>
            new Client[]
            {
                new Client
                {
                    ClientId = "api_swagger_users",
                    ClientName = "Swagger UI for Sample API",
                    ClientSecrets = {new Secret("secret".Sha256())}, // change me!

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = false,

                    RedirectUris = {"https://localhost:7022/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {"https://localhost:7022"},
                    AllowedScopes = new List<string>
                    {  
                        "SampleAPI"
                    }
                },
                
                new Client
                {
                    ClientId = "api_swagger_settings",
                    ClientName = "Swagger UI for Sample API",
                    ClientSecrets = {new Secret("secret".Sha256())}, // change me!

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = false,

                    RedirectUris = {"https://localhost:7209/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {"https://localhost:7209"},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },

            };
    }
}
