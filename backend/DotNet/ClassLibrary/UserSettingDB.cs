using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary
{
    public class UserSettingDB
    {
        [Key] public required Guid UserId { get; set; }
        public required bool IsDarkMode { get; set; }
    }
}
