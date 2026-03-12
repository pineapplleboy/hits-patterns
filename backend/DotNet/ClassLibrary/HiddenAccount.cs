using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary
{
    public class HiddenAccount
    {
        [Key] public required Guid HiddenAccountId { get; set; }
        public required Guid UserId { get; set; }
    }
}
