using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.OpenApi.Models;
using System.Collections.Generic;

namespace ClassLibrary.BaseSetup
{
    public class SetupSwagger
    {
        public static void AddSwagger(WebApplicationBuilder builder)
        {
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen(options =>
            {
                options.AddSecurityDefinition("oauth2", new OpenApiSecurityScheme
                {
                    Type = SecuritySchemeType.OAuth2,
                    Flows = new OpenApiOAuthFlows
                    {
                        AuthorizationCode = new OpenApiOAuthFlow
                        {
                            AuthorizationUrl = new Uri("https://localhost:5001/connect/authorize"),
                            TokenUrl = new Uri("https://localhost:5001/connect/token"),
                            Scopes = new Dictionary<string, string>
                            {
                                {"SampleAPI", "Sample API - full access"}
                            }
                        },
                    }
                });

                // Apply Scheme globally
                options.AddSecurityRequirement(new OpenApiSecurityRequirement
                {
                    {
                        new OpenApiSecurityScheme
                        {
                            Reference = new OpenApiReference { Type = ReferenceType.SecurityScheme, Id = "oauth2" }
                        },
                        new[] { "SampleAPI" }
                    }
                });
            });
        }

        public static void UseSwagger(WebApplication app)
        {
            app.UseSwagger(options =>
            {
                options.PreSerializeFilters.Add((swaggerDoc, httpReq) =>
                {
                    var scheme = string.IsNullOrWhiteSpace(httpReq.Headers["X-Forwarded-Proto"])
                        ? httpReq.Scheme
                        : httpReq.Headers["X-Forwarded-Proto"].ToString();
                    var host = string.IsNullOrWhiteSpace(httpReq.Headers["X-Forwarded-Host"])
                        ? httpReq.Host.Value
                        : httpReq.Headers["X-Forwarded-Host"].ToString();
                    var pathBase = httpReq.Headers["X-Forwarded-Prefix"].ToString();

                    if (string.IsNullOrWhiteSpace(pathBase))
                    {
                        pathBase = httpReq.PathBase.Value ?? string.Empty;
                    }

                    swaggerDoc.Servers = new List<OpenApiServer>
                    {
                        new() { Url = $"{scheme}://{host}{pathBase}" }
                    };
                });
            });

            app.UseSwaggerUI(options =>
            {
                options.RoutePrefix = "swagger";
                options.SwaggerEndpoint("./v1/swagger.json", "API v1");

                options.OAuthUsePkce();
            });
        }
    }
}
