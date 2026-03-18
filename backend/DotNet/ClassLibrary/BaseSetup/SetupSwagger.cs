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
            var authorizationUrl = builder.Configuration["Swagger:OAuth:AuthorizationUrl"]
                ?? "https://localhost:5001/connect/authorize";
            var tokenUrl = builder.Configuration["Swagger:OAuth:TokenUrl"]
                ?? "https://localhost:5001/connect/token";
            var scope = builder.Configuration["Swagger:OAuth:Scope"] ?? "SampleAPI";
            var scopeDescription = builder.Configuration["Swagger:OAuth:ScopeDescription"] ?? "Sample API - full access";

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
                            AuthorizationUrl = new Uri(authorizationUrl),
                            TokenUrl = new Uri(tokenUrl),
                            Scopes = new Dictionary<string, string>
                            {
                                {scope, scopeDescription}
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
                        new[] { scope }
                    }
                });
            });
        }

        public static void UseSwagger(WebApplication app)
        {
            app.Use((context, next) =>
            {
                if (context.Request.Headers.TryGetValue("X-Forwarded-Prefix", out var prefix)
                    && !string.IsNullOrWhiteSpace(prefix))
                {
                    context.Request.PathBase = prefix.ToString();
                }

                return next();
            });

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
                var clientId = app.Configuration["Swagger:OAuth:ClientId"];
                var redirectUrl = app.Configuration["Swagger:OAuth:RedirectUrl"];
                options.RoutePrefix = "swagger";
                options.SwaggerEndpoint("./v1/swagger.json", "API v1");

                if (!string.IsNullOrWhiteSpace(clientId))
                {
                    options.OAuthClientId(clientId);
                }

                if (!string.IsNullOrWhiteSpace(redirectUrl))
                {
                    options.OAuth2RedirectUrl(redirectUrl);
                }

                options.OAuthUsePkce();
            });
        }
    }
}
