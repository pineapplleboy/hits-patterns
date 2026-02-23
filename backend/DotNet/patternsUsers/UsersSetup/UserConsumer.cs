using ClassLibrary;
using ClassLibrary.BaseSetup;
using Confluent.Kafka;
using System.Text.Json;

namespace patternsUsers.UsersSetup
{
    public class UserConsumer : BackgroundService
    {
        private readonly IServiceProvider _serviceProvider;

        public UserConsumer(IServiceProvider serviceProvider)
        {
            _serviceProvider = serviceProvider;
        }
        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            var config = new ConsumerConfig
            {
                GroupId = KafkaOptions.user_group_id,
                BootstrapServers = KafkaOptions.bootstrapServer,
                AutoOffsetReset = AutoOffsetReset.Earliest,
            };

            var _consumer = new ConsumerBuilder<string, string>(config).Build();
            _consumer.Subscribe(KafkaOptions.create_auth_user);

            await Task.Run(async () =>
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    var consumeResult = _consumer.Consume(stoppingToken);
                    var newUser = JsonSerializer.Deserialize<UserDB>(consumeResult.Message.Value);

                    if (newUser != null)
                    {

                        using (var scope = _serviceProvider.CreateScope())
                        {
                            var _context = scope.ServiceProvider.GetRequiredService<UserDataContext>();
                            await _context.users.AddAsync(newUser);
                            await _context.SaveChangesAsync();

                        }
                    }
                }
            }, stoppingToken);
        }
    }
}
