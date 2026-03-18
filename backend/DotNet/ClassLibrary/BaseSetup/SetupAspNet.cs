using ClassLibrary;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.HttpOverrides;
using Microsoft.Extensions.DependencyInjection;
using System.Text.Json.Serialization;

namespace ClassLibrary.BaseSetup
{
    public class SetupAspNet
    {
        public static void AddAspNet(WebApplicationBuilder builder)
        {
            builder.Services.AddExceptionHandler<ExceptionHandler>();
            builder.Services.AddProblemDetails();
            builder.Services.Configure<ForwardedHeadersOptions>(options =>
            {
                options.ForwardedHeaders =
                    ForwardedHeaders.XForwardedFor |
                    ForwardedHeaders.XForwardedProto |
                    ForwardedHeaders.XForwardedHost;
                options.KnownNetworks.Clear();
                options.KnownProxies.Clear();
            });

            builder.Services.AddControllers()
                .AddJsonOptions(options => options.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter()));

            //builder.Services.Configure<ApiBehaviorOptions>(options =>
            //{
            //    options.SuppressModelStateInvalidFilter = true;
            //});
        }

        public static void UseAspNet(WebApplication app)
        {
            app.UseExceptionHandler();
            app.UseForwardedHeaders();
            app.Use((context, next) =>
            {
                if (context.Request.Headers.TryGetValue("X-Forwarded-Prefix", out var prefix)
                    && !string.IsNullOrWhiteSpace(prefix))
                {
                    context.Request.PathBase = prefix.ToString();
                }

                return next();
            });

            app.UseHttpsRedirection();

            app.MapControllers().RequireAuthorization("ApiScope"); ;
        }
    }
}
