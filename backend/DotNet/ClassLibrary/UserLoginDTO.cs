using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary
{
    public class UserLoginDTO
    {
        [Phone] public required string Phone { get; set; }
        public required string Password { get; set; }
    }
}
