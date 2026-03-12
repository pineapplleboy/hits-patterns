using ClassLibrary;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;

namespace patternsUserSetting.UserSettingSetup
{
    public class UserSettingDataContext(DbContextOptions<UserSettingDataContext> options) : DbContext(options)
    {
        public DbSet<UserSettingDB> userSettings { get; set; }
        public DbSet<HiddenAccount> hiddenAccounts { get; set; }
    }
}
