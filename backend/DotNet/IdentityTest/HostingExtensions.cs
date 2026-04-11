using Duende.IdentityServer;
using IdentityTest.Data;
using IdentityTest.Models;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.DataProtection;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.HttpOverrides;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Serilog;
using System.IO;

namespace IdentityTest
{
    internal static class HostingExtensions
    {
        public static WebApplication ConfigureServices(this WebApplicationBuilder builder)
        {
            builder.Services.AddRazorPages();
            builder.Services.Configure<ForwardedHeadersOptions>(options =>
            {
                options.ForwardedHeaders =
                    ForwardedHeaders.XForwardedFor |
                    ForwardedHeaders.XForwardedProto |
                    ForwardedHeaders.XForwardedHost;
                options.KnownNetworks.Clear();
                options.KnownProxies.Clear();
            });

            builder.Services.AddDbContext<ApplicationDbContext>(options =>
                options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

            string dataProtectionKeysPath = builder.Configuration["DataProtection:KeysPath"]
                                            ?? "/var/app/data-protection-keys";

            builder.Services.AddDataProtection()
                .PersistKeysToFileSystem(new DirectoryInfo(dataProtectionKeysPath))
                .SetApplicationName("patterns-identity-server");

            builder.Services.AddIdentity<ApplicationUser, IdentityRole>()
                .AddEntityFrameworkStores<ApplicationDbContext>()
                .AddDefaultTokenProviders();

            builder.Services.ConfigureApplicationCookie(options =>
            {
                options.Cookie.Name = "patterns.identity";
                options.Cookie.Path = "/";
                options.Cookie.SameSite = SameSiteMode.Lax;
                options.Cookie.SecurePolicy = CookieSecurePolicy.SameAsRequest;
            });

            builder.Services.Configure<CookiePolicyOptions>(options =>
            {
                options.MinimumSameSitePolicy = SameSiteMode.Lax;
                options.OnAppendCookie = cookieContext =>
                {
                    if (cookieContext.CookieOptions.SameSite == SameSiteMode.None)
                    {
                        cookieContext.CookieOptions.SameSite = SameSiteMode.Lax;
                    }
                };
                options.OnDeleteCookie = cookieContext =>
                {
                    if (cookieContext.CookieOptions.SameSite == SameSiteMode.None)
                    {
                        cookieContext.CookieOptions.SameSite = SameSiteMode.Lax;
                    }
                };
            });

            builder.Services
                .AddIdentityServer(options =>
                {
                    options.Events.RaiseErrorEvents = true;
                    options.Events.RaiseInformationEvents = true;
                    options.Events.RaiseFailureEvents = true;
                    options.Events.RaiseSuccessEvents = true;

                    // see https://docs.duendesoftware.com/identityserver/v6/fundamentals/resources/
                    options.EmitStaticAudienceClaim = true;
                })
                .AddInMemoryIdentityResources(Config.IdentityResources)
                .AddInMemoryApiScopes(Config.ApiScopes)
                .AddInMemoryClients(Config.Clients)
                .AddAspNetIdentity<ApplicationUser>()
                .AddProfileService<CustomProfileService>();

            builder.Services.AddHostedService<BanConsumer>();

            builder.Services.AddAuthentication()
                .AddGoogle(options =>
                {
                    options.SignInScheme = IdentityServerConstants.ExternalCookieAuthenticationScheme;

                    // register your IdentityServer with Google at https://console.developers.google.com
                    // enable the Google+ API
                    // set the redirect URI to https://localhost:5001/signin-google
                    options.ClientId = "copy client ID from Google here";
                    options.ClientSecret = "copy client secret from Google here";
                });

            return builder.Build();
        }

        public static WebApplication ConfigurePipeline(this WebApplication app)
        {
            app.UseSerilogRequestLogging();
            app.UseForwardedHeaders();
            app.UseCookiePolicy();
            app.Use(async (context, next) =>
            {
                if (HttpMethods.IsOptions(context.Request.Method))
                {
                    var origin = context.Request.Headers.Origin.ToString();
                    if (!string.IsNullOrWhiteSpace(origin))
                    {
                        context.Response.Headers.TryAdd("Access-Control-Allow-Origin", origin);
                        context.Response.Headers.TryAdd("Vary", "Origin");
                        context.Response.Headers.TryAdd("Access-Control-Allow-Headers", "*");
                        context.Response.Headers.TryAdd("Access-Control-Allow-Methods", "*");
                        context.Response.StatusCode = StatusCodes.Status204NoContent;
                        return;
                    }
                }

                await next();

                var requestOrigin = context.Request.Headers.Origin.ToString();
                if (!string.IsNullOrWhiteSpace(requestOrigin)
                    && !context.Response.Headers.ContainsKey("Access-Control-Allow-Origin"))
                {
                    context.Response.Headers.TryAdd("Access-Control-Allow-Origin", requestOrigin);
                    context.Response.Headers.TryAdd("Vary", "Origin");
                    context.Response.Headers.TryAdd("Access-Control-Allow-Headers", "*");
                    context.Response.Headers.TryAdd("Access-Control-Allow-Methods", "*");
                }
            });
            app.Use((context, next) =>
            {
                if (context.Request.Headers.TryGetValue("X-Forwarded-Prefix", out var prefix)
                    && !string.IsNullOrWhiteSpace(prefix))
                {
                    context.Request.PathBase = prefix.ToString();
                }

                return next();
            });

            if (app.Environment.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseStaticFiles();
            app.UseRouting();
            app.UseAuthentication();
            app.UseIdentityServer();
            app.UseAuthorization();

            app.MapRazorPages();
                //.RequireAuthorization();

            return app;
        }
    }
}
