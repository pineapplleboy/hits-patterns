

using patternsAuth.Services;
using patternsAuth.Services.Implementations;

namespace patternsAuth.Setup
{
    public class SetupRepositories
    {
        public static void AddRepositories(IServiceCollection services)
        {
            services.AddScoped<ITokenService, TokenServiceImpl>();
            services.AddScoped<IUserRepository, UserRepositoryImpl>();

        }
    }
}
