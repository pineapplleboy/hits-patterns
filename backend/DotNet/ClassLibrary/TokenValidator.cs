using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace ClassLibrary
{
    public static class TokenValidator
    {
        //public const string URL = "https://localhost:5001";
        //public const string PUBLIC_KEY = "https://localhost:5001/.well-known/openid-configuration/jwks";

        public const string URL = "http://91.227.18.176/identity";
        public const string PUBLIC_KEY = "http://91.227.18.176/identity/.well-known/openid-configuration/jwks";

        private static async Task<string> GetJwksAsync(string jwksUri)
        {
            var handler = new HttpClientHandler();
            handler.ServerCertificateCustomValidationCallback =
                (message, cert, chain, errors) => true;

            using (HttpClient client = new HttpClient(handler))
            {
                HttpResponseMessage response = await client.GetAsync(jwksUri);
                if (response.IsSuccessStatusCode)
                {
                    string jwksJson = await response.Content.ReadAsStringAsync();
                    return jwksJson;
                }
                return null;
            }
        }
        public static async Task<bool> ValidateToken(string universalToken)
        {
            try
            {
                string token = universalToken;
                if (token.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase))
                {
                    token = token.Substring("Bearer ".Length);
                }

                string jwksJson = await GetJwksAsync(PUBLIC_KEY);

                if (string.IsNullOrEmpty(jwksJson))
                {
                    return false;
                }

                var jwks = JsonSerializer.Deserialize<JsonWebKeySet>(jwksJson);


                var tokenHandler = new JwtSecurityTokenHandler();
                var jwtToken = tokenHandler.ReadJwtToken(token);
                var kid = jwtToken.Header.Kid;

                var key = jwks.Keys.FirstOrDefault(k => k.Kid == kid);

                if (key == null)
                {
                    return false;
                }

                var validationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = true,
                    ValidIssuer = URL,

                    ValidateAudience = true,
                    ValidAudience = URL + "/resources",

                    ValidateLifetime = true,
                    ClockSkew = TimeSpan.FromMinutes(5),

                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = key,

                    ValidAlgorithms = new[] { "RS256" }
                };

                var principal = tokenHandler.ValidateToken(token, validationParameters, out _);
                return true;
            }
            catch (Exception ex)
            {
                return false;
            }

        }
    }
}
