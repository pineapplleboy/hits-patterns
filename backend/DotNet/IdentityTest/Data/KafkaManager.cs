using ClassLibrary;
using ClassLibrary.BaseSetup;
using Confluent.Kafka;
using System.Text.Json;

namespace IdentityTest.Data
{
    public static class KafkaManager
    {
        public static async Task SendNewUserMessage(UserDB newUser)
        {
            var config = new ProducerConfig { BootstrapServers = KafkaOptions.bootstrapServer };
            using (var p = new ProducerBuilder<Null, string>(config).Build())
            {
                var dr = await p.ProduceAsync(KafkaOptions.create_auth_user, new Message<Null, string>
                {
                    Value = JsonSerializer.Serialize(newUser)
                });
            }
        }
    }
}
