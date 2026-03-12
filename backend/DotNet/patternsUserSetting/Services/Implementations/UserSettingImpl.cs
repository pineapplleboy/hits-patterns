using ClassLibrary;
using Microsoft.EntityFrameworkCore;
using patternsUserSetting.UserSettingSetup;

namespace patternsUserSetting.Services.Implementations
{
    public class UserSettingImpl : IUserSettingService
    {
        private readonly UserSettingDataContext _context;
        public UserSettingImpl(UserSettingDataContext context)
        {
            _context = context;
        }

        public async Task ChangeAccountVisibility(Guid userId, Guid accountId)
        {
            var account = await GetAccountById(accountId);
            if (account == null)
            {
                var newHiddenAccount = new HiddenAccount { UserId = userId, HiddenAccountId = accountId };
                await _context.hiddenAccounts.AddAsync(newHiddenAccount);
            }
            else
            {
                _context.hiddenAccounts.Remove(account);
            }
            await _context.SaveChangesAsync();
        }

        public async Task<List<Guid>> GetUserHiddenAccounts(Guid userId)
        {
            var hiddenAccounts = await _context.hiddenAccounts.Where(a => a.UserId == userId).Select(a => a.HiddenAccountId).ToListAsync();
            return hiddenAccounts;
        }

        public async Task<UserSettingsDTO> ChangeUserSettings(Guid userId)
        {
            var userSetting = await GetOrCreateGetUserSetting(userId);
            userSetting.IsDarkMode = !userSetting.IsDarkMode;
            await _context.SaveChangesAsync();

            return new UserSettingsDTO { IsDarkMode = userSetting.IsDarkMode };
        }

        public async Task<UserSettingsDTO> GetUserSettings(Guid userId)
        {
            var userSetting = await GetOrCreateGetUserSetting(userId);
            return new UserSettingsDTO { IsDarkMode = userSetting.IsDarkMode};
        }


        private async Task<UserSettingDB> GetOrCreateGetUserSetting(Guid userId)
        {
            var userSetting = await _context.userSettings.Where(s => s.UserId == userId).FirstOrDefaultAsync();
            if (userSetting == null)
            {
                var newUserSetting = new UserSettingDB { UserId = userId, IsDarkMode = false };
                await _context.userSettings.AddAsync(newUserSetting);
                await _context.SaveChangesAsync();
                return newUserSetting;
            }
            return userSetting;

        }

        private async Task<HiddenAccount?> GetAccountById( Guid id)
        {
            var account = await _context.hiddenAccounts.Where(a => a.HiddenAccountId == id).FirstOrDefaultAsync();

            return account;

        }


    }
}
