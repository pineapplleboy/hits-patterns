
using ClassLibrary;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using patternsAuth.Services;

namespace patternsAuth.Controllers
{
    [ApiController]
    [Route("patterns/api/v1/auth")]
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
        [ProducesResponseType(typeof(string), 200)]
        public async Task<IActionResult> LoginEmployee(UserLoginDTO user)
        {
            return Ok(await _authService.LoginEmployee(user));
        }

        [HttpPost("client-login")]
        [ProducesResponseType(typeof(string), 200)]
        public async Task<IActionResult> LoginСlient(UserLoginDTO user)
        {
            return Ok(await _authService.LoginСlient(user));
        }

        [HttpPost("employee-register")]
        [ProducesResponseType(typeof(string), 200)]
        public async Task<IActionResult> RegisterEmployee(RegisterDTO user)
        {
            return Ok(await _authService.UserRegister(user, UserRole.EMPLOYEE));
        }

        [HttpPost("client-register")]
        [ProducesResponseType(typeof(string), 200)]
        public async Task<IActionResult> RegisterСlient(RegisterDTO user)
        {
            return Ok(await _authService.UserRegister(user, UserRole.CLIENT));
        }

        [HttpPost("actuator/health")]
        public async Task<IActionResult> GetHealth()
        {
            return Ok(new Health { status = "UP" });
        }
    }
}