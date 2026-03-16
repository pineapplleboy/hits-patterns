using ClassLibrary;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace patternsUserSetting.Controllers
{
    [ApiController]
    [AllowAnonymous]
    [Route("actuator/health")]
    public class HealthUserSettingContrloller : ControllerBase
    {
        [HttpGet()]
        public async Task<IActionResult> GetHealth()
        {
            return Ok(new Health { status = "UP" });
        }
    }
}
