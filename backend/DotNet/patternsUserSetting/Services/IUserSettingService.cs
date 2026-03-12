using ClassLibrary;

namespace patternsUserSetting.Services
{
    public interface IUserSettingService
    {
        public Task<UserSettingsDTO> GetUserSettings(Guid userId);
        public Task<UserSettingsDTO> ChangeUserSettings(Guid userId);
        public Task<List<Guid>> GetUserHiddenAccounts(Guid userId);
        public Task ChangeAccountVisibility(Guid userId, Guid accountId);
    }
}
