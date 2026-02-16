using Microsoft.IdentityModel.Tokens;
using patternsAuth.Setup;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace patternsAuth.Services.Implementations
{
    public class TokenServiceImpl : ITokenService
    {
        private JwtSecurityTokenHandler tokenHandler = new JwtSecurityTokenHandler();


        public string CreateAccessTokenById(Guid id, List<string> roles)
        {
            ClaimsIdentity claims = new ClaimsIdentity(new Claim[]
                {
                    new Claim(ClaimTypes.NameIdentifier, id.ToString()),
                });

            foreach (var role in roles)
                claims.AddClaim(new Claim(ClaimTypes.Role, role));

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = claims,
                Issuer = AuthOptions.ISSUER,
                Audience = AuthOptions.AUDIENCE,
                Expires = DateTime.UtcNow.AddMinutes(AuthOptions.LIFETIME_MINUTES),
                SigningCredentials = new(AuthOptions.GetSymmetricSecurityKey(), SecurityAlgorithms.HmacSha256Signature)
            };

            var token = tokenHandler.CreateToken(tokenDescriptor);

            return tokenHandler.WriteToken(token);
        }
    }
}
