  using ClassLibrary;
using Microsoft.EntityFrameworkCore;

namespace patternsUsers.UsersSetup
{

    public class UserDataContext(DbContextOptions<UserDataContext> options) : DbContext(options)
    {
        public DbSet<UserDB> users { get; set; }

    }
}
