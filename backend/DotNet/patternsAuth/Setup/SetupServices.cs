

using patternsAuth.Services;
using patternsAuth.Services.Implementations;

namespace patternsAuth.Setup
{
    public class SetupServices
    {
        public static void AddServices(IServiceCollection services)
        {
            services.AddTransient<IAuthService, AuthServiceImpl>();

        }
    }
}
