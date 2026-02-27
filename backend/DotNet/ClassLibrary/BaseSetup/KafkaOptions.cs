using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary.BaseSetup
{
    public static class KafkaOptions
    {
        private static string Get(string key, string defaultValue)
            => Environment.GetEnvironmentVariable(key) ?? defaultValue;

        public static string bootstrapServer => Get("KAFKA_BOOTSTRAP_SERVERS", "localhost:29092");

        public static string ban_user_auth => Get("KAFKA_TOPIC_BAN_USER_AUTH", "00_ban_user_auth");
        public static string ban_user_bank_accounts => Get("KAFKA_TOPIC_BAN_USER_BANK_ACCOUNTS", "00_ban_user_bank_accounts");
        public static string create_auth_user => Get("KAFKA_TOPIC_CREATE_AUTH_USER", "00_create_user");

        public static string ban_group_id => Get("KAFKA_GROUP_BAN", "ban-group");
        public static string user_group_id => Get("KAFKA_GROUP_USER", "user-group");

    }
}
