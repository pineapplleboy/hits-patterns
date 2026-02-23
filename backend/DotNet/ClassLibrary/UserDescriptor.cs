using System.Security.Claims;

namespace ClassLibrary
{
    public class UserDescriptor
    {
        public static Guid GetUserId(ClaimsPrincipal principal)
        {
            string? userId = principal.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            if (userId == null)
                throw new UnauthorizedAccessException();

            return new Guid(userId);
        }

        public static string GetUserRole(ClaimsPrincipal principal)
        {
            string? userRole = principal.FindFirst(ClaimTypes.Role)?.Value;

            if (userRole == null)
                throw new UnauthorizedAccessException();

            return userRole;
        }
        //public static List<string> GetAllUserRoles(ClaimsPrincipal principal)
        //{
        //    var roles = principal.Claims.Where(c => c.Type == ClaimTypes.Role).Select(c => c.Value);
        //    return roles.ToList(); 
        //}
    }
}
