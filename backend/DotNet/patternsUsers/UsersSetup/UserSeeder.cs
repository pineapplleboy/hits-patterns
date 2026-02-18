using ClassLibrary;
using Microsoft.EntityFrameworkCore;

namespace patternsUsers.UsersSetup
{
    public class UserSeeder
    {
        public async static Task Seed(UserDataContext context)
        {
            var employeeId = new Guid("6a541e68-cd4c-45bc-94fb-97634ef8a3ef");
            var employee = await context.users.FirstOrDefaultAsync(d => d.Id == employeeId);
            if (employee == null)
            {
                employee = new UserDB
                {
                    Id = employeeId,
                    Name = "Я работник",
                    Phone = "89992223311",
                    Ban = false,
                    UserRole = UserRole.EMPLOYEE,
                    Author = null

                };
                context.users.Add(employee);
            }

            var clientId = new Guid("660cb224-58bd-4298-87a9-6bd5fb451842");
            var client = await context.users.FirstOrDefaultAsync(c => c.Id == clientId);
            if (client == null)
            {
                client = new UserDB
                {
                    Id = clientId,
                    Name = "Я клиент",
                    Phone = "87772223311",
                    Ban = false,
                    UserRole = UserRole.CLIENT,
                    Author = null
                };
                context.users.Add(client);
            }



            await context.SaveChangesAsync();
        }
    }
}
