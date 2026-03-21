using Duende.IdentityServer;
using Duende.IdentityServer.Models;

namespace IdentityTest
{
    public static class Config
    {
        private const int SwaggerAccessTokenLifetimeSeconds = 8 * 60 * 60;
        private const int MobileAccessTokenLifetimeSeconds = 60 * 60;
        private const int MobileIdentityTokenLifetimeSeconds = 60 * 60;
        private const int AuthorizationCodeLifetimeSeconds = 5 * 60;
        private const int SlidingRefreshTokenLifetimeSeconds = 15 * 24 * 60 * 60;
        private const int AbsoluteRefreshTokenLifetimeSeconds = 30 * 24 * 60 * 60;

        private static readonly string UsersSwaggerUrl = GetEnvironmentVariable("SWAGGER_USERS_URL", "https://localhost:7022");
        private static readonly string UserSettingsSwaggerUrl = GetEnvironmentVariable("SWAGGER_USER_SETTINGS_URL", "https://localhost:7209");
        private static readonly string AccountSwaggerUrl = GetEnvironmentVariable("SWAGGER_ACCOUNT_URL", "http://localhost:8082");
        private static readonly string TransfersSwaggerUrl = GetEnvironmentVariable("SWAGGER_TRANSFERS_URL", "http://localhost:8083");
        private static readonly string CurrencySwaggerUrl = GetEnvironmentVariable("SWAGGER_CURRENCY_URL", "http://localhost:8084");
        private static readonly string CreditSwaggerUrl = GetEnvironmentVariable("SWAGGER_CREDIT_URL", "http://localhost:8081");
        private static readonly string UsersSwaggerOrigin = GetOrigin(UsersSwaggerUrl);
        private static readonly string UserSettingsSwaggerOrigin = GetOrigin(UserSettingsSwaggerUrl);
        private static readonly string AccountSwaggerOrigin = GetOrigin(AccountSwaggerUrl);
        private static readonly string TransfersSwaggerOrigin = GetOrigin(TransfersSwaggerUrl);
        private static readonly string CurrencySwaggerOrigin = GetOrigin(CurrencySwaggerUrl);
        private static readonly string CreditSwaggerOrigin = GetOrigin(CreditSwaggerUrl);

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

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = true,
                    RequireClientSecret = false,
                    AccessTokenLifetime = SwaggerAccessTokenLifetimeSeconds,
                    AuthorizationCodeLifetime = AuthorizationCodeLifetimeSeconds,

                    RedirectUris = {$"{UsersSwaggerUrl}/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {UsersSwaggerOrigin},
                    AllowedScopes = new List<string>
                    {  
                        "SampleAPI"
                    }
                },
                
                new Client
                {
                    ClientId = "api_swagger_settings",
                    ClientName = "Swagger UI for Sample API",

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = true,
                    RequireClientSecret = false,
                    AccessTokenLifetime = SwaggerAccessTokenLifetimeSeconds,
                    AuthorizationCodeLifetime = AuthorizationCodeLifetimeSeconds,

                    RedirectUris = {$"{UserSettingsSwaggerUrl}/swagger/oauth2-redirect.html"},
                    AllowedCorsOrigins = {UserSettingsSwaggerOrigin},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "api_swagger_account",
                    ClientName = "Swagger UI for Account API",

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = true,
                    RequireClientSecret = false,
                    AccessTokenLifetime = SwaggerAccessTokenLifetimeSeconds,
                    AuthorizationCodeLifetime = AuthorizationCodeLifetimeSeconds,

                    RedirectUris = {$"{AccountSwaggerUrl}/swagger-ui/oauth2-redirect.html"},
                    AllowedCorsOrigins = {AccountSwaggerOrigin},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "api_swagger_transfers",
                    ClientName = "Swagger UI for Transfers API",

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = true,
                    RequireClientSecret = false,
                    AccessTokenLifetime = SwaggerAccessTokenLifetimeSeconds,
                    AuthorizationCodeLifetime = AuthorizationCodeLifetimeSeconds,

                    RedirectUris = {$"{TransfersSwaggerUrl}/swagger-ui/oauth2-redirect.html"},
                    AllowedCorsOrigins = {TransfersSwaggerOrigin},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "api_swagger_currency",
                    ClientName = "Swagger UI for Currency API",

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = true,
                    RequireClientSecret = false,
                    AccessTokenLifetime = SwaggerAccessTokenLifetimeSeconds,
                    AuthorizationCodeLifetime = AuthorizationCodeLifetimeSeconds,

                    RedirectUris = {$"{CurrencySwaggerUrl}/swagger-ui/oauth2-redirect.html"},
                    AllowedCorsOrigins = {CurrencySwaggerOrigin},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "api_swagger_credit",
                    ClientName = "Swagger UI for Credit API",

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = true,
                    RequireClientSecret = false,
                    AccessTokenLifetime = SwaggerAccessTokenLifetimeSeconds,
                    AuthorizationCodeLifetime = AuthorizationCodeLifetimeSeconds,

                    RedirectUris = {$"{CreditSwaggerUrl}/swagger-ui/oauth2-redirect.html"},
                    AllowedCorsOrigins = {CreditSwaggerOrigin},
                    AllowedScopes = new List<string>
                    {
                        "SampleAPI"
                    }
                },
                new Client
                {
                    ClientId = "android_client_app",
                    ClientName = "Mobile client app",
                    RedirectUris = { "com.client.android:/callback"},
                    PostLogoutRedirectUris = { "com.client.android:/logout" },

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = true,
                    RequireClientSecret = false,
                    AllowAccessTokensViaBrowser = true,
                    RequireConsent = false,
                    AccessTokenLifetime = MobileAccessTokenLifetimeSeconds,
                    IdentityTokenLifetime = MobileIdentityTokenLifetimeSeconds,
                    AuthorizationCodeLifetime = AuthorizationCodeLifetimeSeconds,

                    AllowOfflineAccess = true,
                    RefreshTokenUsage = TokenUsage.ReUse,
                    RefreshTokenExpiration = TokenExpiration.Sliding,
                    SlidingRefreshTokenLifetime = SlidingRefreshTokenLifetimeSeconds,
                    AbsoluteRefreshTokenLifetime = AbsoluteRefreshTokenLifetimeSeconds,
                    UpdateAccessTokenClaimsOnRefresh = true,

                    AllowedScopes = {
                            IdentityServerConstants.StandardScopes.OpenId,
                            IdentityServerConstants.StandardScopes.Profile,
                            "SampleAPI"
                        }
                },
                new Client
                {
                    ClientId = "android_employeeee_app",
                    ClientName = "Mobile employee app",
                    RedirectUris = { "com.employee.android:/callback"},
                    PostLogoutRedirectUris = { "com.employee.android:/logout" },

                    AllowedGrantTypes = GrantTypes.Code,
                    RequirePkce = true,
                    RequireClientSecret = false,
                    AllowAccessTokensViaBrowser = true,
                    RequireConsent = false,
                    AccessTokenLifetime = MobileAccessTokenLifetimeSeconds,
                    IdentityTokenLifetime = MobileIdentityTokenLifetimeSeconds,
                    AuthorizationCodeLifetime = AuthorizationCodeLifetimeSeconds,

                    AllowOfflineAccess = true,
                    RefreshTokenUsage = TokenUsage.ReUse,
                    RefreshTokenExpiration = TokenExpiration.Sliding,
                    SlidingRefreshTokenLifetime = SlidingRefreshTokenLifetimeSeconds,
                    AbsoluteRefreshTokenLifetime = AbsoluteRefreshTokenLifetimeSeconds,
                    UpdateAccessTokenClaimsOnRefresh = true,

                    AllowedScopes = {
                        IdentityServerConstants.StandardScopes.OpenId,
                        IdentityServerConstants.StandardScopes.Profile,
                        "SampleAPI"
                    }
                }

            };

        private static string GetEnvironmentVariable(string variableName, string fallback)
        {
            var value = Environment.GetEnvironmentVariable(variableName);
            return string.IsNullOrWhiteSpace(value) ? fallback : value.TrimEnd('/');
        }

        private static string GetOrigin(string url)
        {
            var uri = new Uri(url);
            return uri.GetLeftPart(UriPartial.Authority);
        }
    }
}
