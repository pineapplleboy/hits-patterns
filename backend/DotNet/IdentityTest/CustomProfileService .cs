using Duende.IdentityServer.Models;
using Duende.IdentityServer.Services;
using IdentityTest.Models;
using Microsoft.AspNetCore.Identity;
using System.Security.Claims;

namespace IdentityTest
{
    public class CustomProfileService : IProfileService
    {
        private readonly UserManager<ApplicationUser> _userManager;

        public CustomProfileService(UserManager<ApplicationUser> userManager)
        {
            _userManager = userManager;
        }

        public async Task GetProfileDataAsync(ProfileDataRequestContext context)
        {
            var user = await _userManager.GetUserAsync(context.Subject);

            if (user != null)
            {
                if (user.Ban == true)
                {
                    context.IssuedClaims = new List<Claim>();
                    return;
                }

                var claims = new List<System.Security.Claims.Claim>();

                claims.Add(new System.Security.Claims.Claim("nameid", user.Id.ToString()));


                claims.Add(new System.Security.Claims.Claim("role", user.UserRole.ToString()));
                //claims.Add(new Claim(ClaimTypes.Role, user.UserRole.ToString()));

                context.IssuedClaims = claims;
            }
        }

        public async Task IsActiveAsync(IsActiveContext context)
        {
            var user = await _userManager.GetUserAsync(context.Subject);
            context.IsActive = (user != null) && !user.Ban; // Проверяем бан при валидации
        }
    }
}
