using ClassLibrary;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace patternsUsers.Controllers
{
    [ApiController]
    [AllowAnonymous]
    [Route("actuator/health")]
    public class HealthUsersController : ControllerBase
    {
        [HttpGet()]
        public async Task<IActionResult> GetHealth()
        {
            return Ok(new Health { status = "UP" });
        }
    }
}
