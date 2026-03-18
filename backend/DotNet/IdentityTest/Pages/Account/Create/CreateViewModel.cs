using ClassLibrary;
using System.ComponentModel.DataAnnotations;

namespace IdentityTest.Pages.Account.Create
{
    public class CreateViewModel
    {
        public string Name { get; set; }
        [Phone] public string Phone { get; set; }
        public string Password { get; set; }
        //public string ReturnUrl { get; set; }
        public UserRole UserRole { get; set; }
    }
}
