using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary
{

    public class RegisterUserDTO
    {
        public required string Name { get; set; }
        [Phone] public required string Phone { get; set; }
        public required string Password { get; set; }
        public required UserRole UserRole { get; set; }
    }
}
