using System.ComponentModel.DataAnnotations;

namespace IdentityTest.Pages.Account.Register
{
    public class RegisterViewModel
    {
        public  string Name { get; set; }
        [Phone] public  string Phone { get; set; }
        public  string Password { get; set; }
        public string ReturnUrl { get; set; }
    }
}
