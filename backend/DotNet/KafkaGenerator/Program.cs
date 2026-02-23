using ClassLibrary.BaseSetup;
using Confluent.Kafka.Admin;
using Confluent.Kafka;


List<string> topics = [KafkaOptions.ban_user_auth, KafkaOptions.ban_user_bank_accounts, KafkaOptions.create_auth_user];

using (var adminClient = new AdminClientBuilder(new AdminClientConfig { BootstrapServers = KafkaOptions.bootstrapServer }).Build())
{
    foreach (var topic in topics )
    {
        try
        {
            await adminClient.CreateTopicsAsync(new TopicSpecification[] {
                    new TopicSpecification { Name = topic, ReplicationFactor = 1, NumPartitions = 1 } });
            Console.WriteLine($"Topic {topic} has been createt");
        }
        catch (CreateTopicsException e)
        {
            Console.WriteLine($"An error occured creating {topic} {e.Results[0].Topic}: {e.Results[0].Error.Reason}");
        }
    }

}