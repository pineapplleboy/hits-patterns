using ClassLibrary;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore.Metadata.Internal;
using patternsUserSetting.Migrations;
using patternsUserSetting.Services;
using patternsUserSetting.UserSettingSetup;

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
        [IdempotencyKey]
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

        [Authorize(Roles = "CLIENT, EMPLOYEE")]
        [HttpPut("account-visibility/{accountId}")]
        [IdempotencyKey]
        public async Task<IActionResult> ChangeMyHiddenAccount([FromRoute] Guid accountId)
        {
            await _userSettingService.ChangeAccountVisibility(UserDescriptor.GetUserId(User), accountId);
            return Ok();
        }
    }
}

