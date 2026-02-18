
using ClassLibrary;
using Microsoft.EntityFrameworkCore;

namespace patternsAuth.Setup
{
    public class AuthDataContext(DbContextOptions<AuthDataContext> options) : DbContext(options)
    {
        public DbSet<AuthUserDB> AuthUsers { get; set; }

    }
}
