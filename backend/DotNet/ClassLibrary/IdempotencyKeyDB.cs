using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary
{
    public class IdempotencyKeyDB
    {
        public required Guid Id { get; set; }
        public required string Content { get; set; }
        public required int StatusCode { get; set; }
    }
}
