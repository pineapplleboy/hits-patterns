using System.ComponentModel.DataAnnotations;

namespace ClassLibrary
{
    public class AuthUserDB
    {
        public required Guid Id { get; set; }
        [Phone] public required string Phone { get; set; }
        public required string Password { get; set; }
        public required bool Ban { get; set; }
        public required UserRole UserRole { get; set; }
    }
}
