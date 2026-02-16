using Microsoft.EntityFrameworkCore;

namespace patternsAuth.Setup
{
    public class SetupDatabases
    {
        public static void AddDatabases(WebApplicationBuilder builder)
        {
            AddDb(builder);

        }

        public static void AddDb(WebApplicationBuilder builder)
        {
            var applicationsConnection = builder.Configuration.GetConnectionString("DbConnection");

            builder.Services.AddDbContext<DataContext>(options => options.UseNpgsql(applicationsConnection));
        }



        public async static Task RunMigrations(WebApplication app)
        {
            using var serviceScope = app.Services.CreateScope();

            var applicationContext = serviceScope.ServiceProvider.GetService<DataContext>();

            applicationContext?.Database.Migrate();
            if (applicationContext != null)
                await DataSeeder.Seed(applicationContext);
        }
    }
}
