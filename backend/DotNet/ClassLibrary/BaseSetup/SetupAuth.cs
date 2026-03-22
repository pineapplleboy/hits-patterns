using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.IdentityModel.Tokens;

namespace ClassLibrary.BaseSetup
{
    public class SetupAuth
    {
        public static void AddAuth(WebApplicationBuilder builder)
        {
            var authority = builder.Configuration["Auth:Authority"] ?? "https://localhost:5001";
            var requireHttpsMetadataRaw = builder.Configuration["Auth:RequireHttpsMetadata"];
            var requireHttpsMetadata = bool.TryParse(requireHttpsMetadataRaw, out var parsedRequireHttpsMetadata)
                && parsedRequireHttpsMetadata;

            builder.Services.AddAuthentication("Bearer")
                .AddJwtBearer("Bearer", options =>
                {
                    options.Authority = authority;
                    options.RequireHttpsMetadata = requireHttpsMetadata;
                    options.BackchannelHttpHandler = new HttpClientHandler
                    {
                        ServerCertificateCustomValidationCallback =
                    HttpClientHandler.DangerousAcceptAnyServerCertificateValidator // Только для разработки!
                    };

                    options.TokenValidationParameters = new TokenValidationParameters()
                    {
                        ValidateAudience = false, // Validate 
                        ValidateIssuer = false,
                        ValidateIssuerSigningKey = false,
                    };
                });
            //.AddJwtBearer(options =>
            //{
            //    options.RequireHttpsMetadata = false;
            //    options.SaveToken = true;
            //    options.TokenValidationParameters = new TokenValidationParameters
            //    {
            //        ValidateIssuer = true,
            //        ValidIssuer = AuthOptions.ISSUER,
            //        ValidateAudience = true,
            //        ValidAudience = AuthOptions.AUDIENCE,
            //        ValidateLifetime = true,
            //        IssuerSigningKey = AuthOptions.GetSymmetricSecurityKey(),
            //        ValidateIssuerSigningKey = true,
            //    };
            //});

            //builder.Services.AddAuthorization();
            builder.Services.AddAuthorization(options =>
            {
                options.AddPolicy("ApiScope", policy =>
                {
                    policy.RequireAuthenticatedUser();
                    policy.RequireClaim("scope", "SampleAPI");
                });
            });
            builder.Services.AddCors(options =>
            {
                options.AddDefaultPolicy(policy =>
                {
                    policy.AllowAnyOrigin();
                    policy.AllowAnyHeader();
                    policy.AllowAnyMethod();
                });
            });

        }

        public static void UseAuth(WebApplication app)
        {
            app.UseAuthentication();
            app.UseAuthorization();
            app.UseCors();
        }
    }
}
