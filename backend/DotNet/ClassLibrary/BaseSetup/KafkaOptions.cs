using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary.BaseSetup
{
    public static class KafkaOptions
    {
        public const string bootstrapServer = "localhost:9092";
        
        public const string ban_user_auth = "00_ban_user_auth";
        public const string ban_user_bank_accounts = "00_ban_user_bank_accounts";
        public const string create_auth_user = "00_create_user";
        
        public const string ban_group_id = "ban-group";
        public const string user_group_id = "user-group";

    }
}
