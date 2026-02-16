using Microsoft.IdentityModel.Tokens;
using System.Text;

namespace ClassLibrary.BaseSetup
{
    public class AuthOptions
    {
        public const string ISSUER = "companyEventsServer";
        public const string AUDIENCE = "companyEventsClient";
        public const string KEY = "superKeyPuPuPu_52#Peterburg-eeeAaALike";
        public const int LIFETIME_MINUTES = GeneralSettings.ACCESS_TOKEN_LIFETIME;

        public static SymmetricSecurityKey GetSymmetricSecurityKey() =>
            new SymmetricSecurityKey(Encoding.UTF8.GetBytes(KEY));
    }
}
