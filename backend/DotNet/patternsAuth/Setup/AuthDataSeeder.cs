
using ClassLibrary;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;

namespace patternsAuth.Setup
{
    public static class AuthDataSeeder
    {
        public async static Task Seed(AuthDataContext context)
        {

            var employeeId = new Guid("6a541e68-cd4c-45bc-94fb-97634ef8a3ef");
            var employee = await context.AuthUsers.FirstOrDefaultAsync(d => d.Id == employeeId);
            if (employee == null)
            {
                employee = new AuthUserDB
                {
                    Id = employeeId,
                    Phone = "89992223311",
                    Password = Hasher.HashPassword("string"),
                    Ban = false,
                    UserRole = UserRole.EMPLOYEE
                };
                context.AuthUsers.Add(employee);
            }

            var clientId = new Guid("660cb224-58bd-4298-87a9-6bd5fb451842");
            var client = await context.AuthUsers.FirstOrDefaultAsync(c => c.Id == clientId);
            if (client == null)
            {
                client = new AuthUserDB
                {
                    Id = clientId,
                    Phone = "87772223311",
                    Password = Hasher.HashPassword("string"),
                    Ban = false,
                    UserRole = UserRole.CLIENT
                };
                context.AuthUsers.Add(client);
            }



            await context.SaveChangesAsync();
        }
    }
}
