using Duende.IdentityServer.Models;

namespace IdentityTest
{
    public static class Config
    {
        private static readonly string UsersSwaggerUrl = GetEnvironmentVariable("SWAGGER_USERS_URL", "https://localhost:7022");
        private static readonly string UserSettingsSwaggerUrl = GetEnvironmentVariable("SWAGGER_USER_SETTINGS_URL", "https://localhost:7209");
        private static readonly string AccountSwaggerUrl = GetEnvironmentVariable("SWAGGER_ACCOUNT_URL", "http://localhost:8082");
        private static readonly string TransfersSwaggerUrl = GetEnvironmentVariable("SWAGGER_TRANSFERS_URL", "http://localhost:8083");
        private static readonly string CurrencySwaggerUrl = GetEnvironmentVariable("SWAGGER_CURRENCY_URL", "http://localhost:8084");
        private static readonly string CreditSwaggerUrl = GetEnvironmentVariable("SWAGGER_CREDIT_URL", "http://localhost:8081");

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

                    RedirectUris = {$"{UsersSwaggerUrl}/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {UsersSwaggerUrl},
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

                    RedirectUris = {$"{UserSettingsSwaggerUrl}/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {UserSettingsSwaggerUrl},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "api_swagger_account",
                    ClientName = "Swagger UI for Account API",
                    ClientSecrets = {new Secret("secret".Sha256())},

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = false,

                    RedirectUris = {$"{AccountSwaggerUrl}/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {AccountSwaggerUrl},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "api_swagger_transfers",
                    ClientName = "Swagger UI for Transfers API",
                    ClientSecrets = {new Secret("secret".Sha256())},

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = false,

                    RedirectUris = {$"{TransfersSwaggerUrl}/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {TransfersSwaggerUrl},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "api_swagger_currency",
                    ClientName = "Swagger UI for Currency API",
                    ClientSecrets = {new Secret("secret".Sha256())},

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = false,

                    RedirectUris = {$"{CurrencySwaggerUrl}/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {CurrencySwaggerUrl},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "api_swagger_credit",
                    ClientName = "Swagger UI for Credit API",
                    ClientSecrets = {new Secret("secret".Sha256())},

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = false,

                    RedirectUris = {$"{CreditSwaggerUrl}/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {CreditSwaggerUrl},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },

            };

        private static string GetEnvironmentVariable(string variableName, string fallback)
        {
            var value = Environment.GetEnvironmentVariable(variableName);
            return string.IsNullOrWhiteSpace(value) ? fallback : value.TrimEnd('/');
        }
    }
}
