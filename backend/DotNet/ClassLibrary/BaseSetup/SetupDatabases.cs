using Microsoft.AspNetCore.Builder;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary.BaseSetup
{
    public class SetupDatabases
    {
        public static void AddDatabases<TContext>(WebApplicationBuilder builder) where TContext : DbContext
        {
            var applicationsConnection = builder.Configuration.GetConnectionString("DbConnection");

            builder.Services.AddDbContext<TContext>(options => options.UseNpgsql(applicationsConnection));
        }


        public static async Task RunMigrations<TContext>(
            WebApplication app,
            Func<TContext, Task> seederAction
        ) where TContext : DbContext
        {
            using var serviceScope = app.Services.CreateScope();

            var applicationContext = serviceScope.ServiceProvider.GetRequiredService<TContext>();

            applicationContext?.Database.Migrate();
            if (applicationContext != null)
            {
                await seederAction(applicationContext);
            }



        }

    }
}
