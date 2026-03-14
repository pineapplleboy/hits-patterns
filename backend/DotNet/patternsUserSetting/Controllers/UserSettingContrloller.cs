using ClassLibrary;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using patternsUserSetting.Services;

namespace patternsUserSetting.Controllers
{
    [ApiController]
    [Route("patterns/api/v1/user/setting")]
    public class UserSettingContrloller : ControllerBase
    {
        private readonly IUserSettingService _userSettingService;
        public UserSettingContrloller(IUserSettingService userSettingService)
        {
            _userSettingService = userSettingService;
        }

        [Authorize]
        [HttpGet("my-settings")]
        [ProducesResponseType(typeof(UserSettingsDTO), 200)]
        public async Task<IActionResult> GetMySettings()
        {
            return Ok(await _userSettingService.GetUserSettings(UserDescriptor.GetUserId(User)));
        }


        [Authorize]
        [HttpPut("my-settings")]
        [ProducesResponseType(typeof(UserSettingsDTO), 200)]
        public async Task<IActionResult> ChangeMySettings()
        {
            return Ok(await _userSettingService.ChangeUserSettings(UserDescriptor.GetUserId(User)));
        }

        [Authorize]
        [HttpGet("hidden-accounts/{accountId}")]
        [ProducesResponseType(typeof(List<Guid>), 200)]
        public async Task<IActionResult> GetHiddenAccounts([FromRoute] Guid accountId)
        {
            return Ok(await _userSettingService.GetUserHiddenAccounts(accountId));
        }

        [Authorize(Roles = "Сlient")]
        [HttpPut("account-visibility/{accountId}")]
        public async Task<IActionResult> ChangeMyHiddenAccount([FromRoute] Guid accountId)
        {
            await _userSettingService.ChangeAccountVisibility(UserDescriptor.GetUserId(User), accountId);
            return Ok();
        }
    }
}
