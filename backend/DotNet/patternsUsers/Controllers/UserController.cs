using ClassLibrary;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using patternsUsers.Services;
using patternsUsers.UsersSetup;

namespace patternsUsers.Controllers
{
    [ApiController]
    [Route("patterns/api/v1/users")]
    public class UserController : ControllerBase
    {
        private readonly IUserService _userService;
        public UserController(IUserService userService)
        {
            _userService = userService;
        }

        [Authorize]
        [HttpGet("get-my-profile")]
        [ProducesResponseType(typeof(UserDB), 200)]
        public async Task<IActionResult> GetMyProfile()
        {
            return Ok(await _userService.GetUserById(UserDescriptor.GetUserId(User)));
        }

        //[Authorize(Roles = "Employee")]
        //[HttpGet("get-user/{id}")]
        //public async Task<IActionResult> EmployeeGetUser([FromRoute] Guid id)
        //{
        //    return Ok(await _userService.GetUserById(id));
        //}

        [Authorize(Roles = "EMPLOYEE")]
        [HttpGet("get-users")]
        [ProducesResponseType(typeof(List<UserDTO>), 200)]
        public async Task<IActionResult> GetUsers([FromQuery] bool? isClient)
        {
            return Ok(await _userService.GetUsers(UserDescriptor.GetUserId(User), isClient));
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpPost("ban-user/{id}")]
        [IdempotencyKeyUsers]
        public async Task<IActionResult> BanUser([FromRoute] Guid id)
        {
            string token = await HttpContext.GetTokenAsync("access_token");
            await _userService.BanUser(UserDescriptor.GetUserId(User), id, token);
            return Ok();
        }

        [Authorize(Roles = "EMPLOYEE")]
        [HttpPost("unban-user/{id}")]
        [IdempotencyKeyUsers]
        public async Task<IActionResult> UnbanUsers([FromRoute] Guid id)
        {
            string token = await HttpContext.GetTokenAsync("access_token");
            await _userService.UnbanUser(id, token);
            return Ok();
        }

    }
}
