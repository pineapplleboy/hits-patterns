using ClassLibrary;
using Microsoft.AspNetCore.Mvc;

namespace patternsUsers.Controllers
{
    [ApiController]
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
