using ClassLibrary;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using patternsUsers.Services;

namespace patternsUsers.Controllers
{
    [ApiController]
    [Route("users")]
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

        [Authorize(Roles = "Employee")]
        [HttpGet("get-users")]
        [ProducesResponseType(typeof(List<UserDTO>), 200)]
        public async Task<IActionResult> GetUsers([FromQuery] bool? isClient)
        {
            return Ok(await _userService.GetUsers(UserDescriptor.GetUserId(User), isClient));
        }

        [Authorize(Roles = "Employee")]
        [HttpPost("ban-user/{id}")]
        public async Task<IActionResult> BanUser([FromRoute] Guid id)
        {
            await _userService.BanUser(UserDescriptor.GetUserId(User), id);
            return Ok();
        }

        [Authorize(Roles = "Employee")]
        [HttpPost("unban-user/{id}")]
        public async Task<IActionResult> UnbanUsers([FromRoute] Guid id)
        {
            await _userService.UnbanUser(id);
            return Ok();
        }
    }
}
