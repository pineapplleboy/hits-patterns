
using ClassLibrary;
using Microsoft.EntityFrameworkCore;

namespace patternsAuth
{
    public class DataContext(DbContextOptions<DataContext> options) : DbContext(options)
    {
        public DbSet<AuthUserDB> AuthUsers { get; set; }

    }
}
