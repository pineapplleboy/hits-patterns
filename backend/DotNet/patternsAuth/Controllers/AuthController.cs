
using ClassLibrary;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using patternsAuth.Services;

namespace patternsAuth.Controllers
{
    [ApiController]
    [Route("auth")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [Authorize(Roles = "Employee")]
        [HttpPost("employee-register-user")]
        public async Task<IActionResult> EmployeeRegisterUser(RegisterUserDTO user)
        {
            await _authService.EmployeeRegisterUser(UserDescriptor.GetUserId(User),user);
            return Ok();
        }

        [HttpPost("employee-login")]
        public async Task<IActionResult> LoginEmployee(UserLoginDTO user)
        {
            return Ok(await _authService.LoginEmployee(user));
        }

        [HttpPost("clien-login")]
        public async Task<IActionResult> LoginСlient(UserLoginDTO user)
        {
            return Ok(await _authService.LoginСlient(user));
        }

    }
}