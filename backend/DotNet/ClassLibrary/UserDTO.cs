using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary
{
    public class UserDTO
    {
        public required Guid Id { get; set; }
        public required string Name { get; set; }
        public required string Phone { get; set; }
        public required bool Ban { get; set; }
        public required UserRole UserRole { get; set; }
        public Guid? Author { get; set; }
        public bool IsBannable { get; set; }
    }
}
