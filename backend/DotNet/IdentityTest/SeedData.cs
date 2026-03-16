using System.Security.Claims;
using ClassLibrary;
using IdentityModel;
using IdentityTest.Data;
using IdentityTest.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Serilog;

namespace IdentityTest
{
    public class SeedData
    {
        public static void EnsureSeedData(WebApplication app)
        {
            using (var scope = app.Services.GetRequiredService<IServiceScopeFactory>().CreateScope())
            {
                var context = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
                context.Database.Migrate();

                var userMgr = scope.ServiceProvider.GetRequiredService<UserManager<ApplicationUser>>();
                var employee = userMgr.FindByNameAsync("89992223311").Result;
                if (employee == null)
                {
                    employee = new ApplicationUser
                    {
                        Id = "6a541e68-cd4c-45bc-94fb-97634ef8a3ef",
                        UserName = "89992223311",
                        Email = "Работник",
                        EmailConfirmed = true,
                        Ban = false,
                        UserRole = UserRole.EMPLOYEE
                    };
                    var result = userMgr.CreateAsync(employee, "String1!").Result;
                    if (!result.Succeeded)
                    {
                        throw new Exception(result.Errors.First().Description);
                    }

                }
                else
                {
                    Log.Debug("89992223311 already exists");
                }

                var client = userMgr.FindByNameAsync("87772223311").Result;
                if (client == null)
                {
                    client = new ApplicationUser
                    {
                        Id = "660cb224-58bd-4298-87a9-6bd5fb451842",
                        UserName = "87772223311",
                        Email = "Клиент",
                        EmailConfirmed = true,
                        Ban = false,
                        UserRole = UserRole.CLIENT
                    };
                    var result = userMgr.CreateAsync(client, "String1!").Result;
                    if (!result.Succeeded)
                    {
                        throw new Exception(result.Errors.First().Description);
                    }
                }
                else
                {
                    Log.Debug("87772223311 already exists");
                }
            }
        }
    }
}
