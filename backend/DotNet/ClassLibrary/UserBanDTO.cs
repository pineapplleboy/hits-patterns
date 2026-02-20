using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary
{
    public class UserBanDTO
    {
        public required Guid Id { get; set; }
        public required bool Ban { get; set; }
    }
}
