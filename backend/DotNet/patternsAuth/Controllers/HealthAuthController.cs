using ClassLibrary;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace patternsAuth.Controllers
{
    [ApiController]
    [Route("actuator/health")]
    public class HealthAuthController : ControllerBase
    {
        [HttpGet()]
        public async Task<IActionResult> GetHealth()
        {
            return Ok(new Health { status = "UP" });
        }
    }
}
