using Confluent.Kafka;

using System.Text.Json;
using ClassLibrary;
using patternsAuth.Services;
using patternsAuth.Setup;
using ClassLibrary.BaseSetup;


public class BanConsumer : BackgroundService
{
    private readonly IServiceProvider _serviceProvider;

    public BanConsumer(IServiceProvider serviceProvider)
    {
        _serviceProvider = serviceProvider;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var config = new ConsumerConfig
        {
            GroupId = KafkaOptions.ban_group_id,
            BootstrapServers = KafkaOptions.bootstrapServer,
            AutoOffsetReset = AutoOffsetReset.Earliest,
        };

        var _consumer = new ConsumerBuilder<string, string>(config).Build();
        _consumer.Subscribe(KafkaOptions.ban_user_auth);

        await Task.Run(async () =>
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                var consumeResult = _consumer.Consume(stoppingToken);
                var userBanInfo = JsonSerializer.Deserialize<UserBanDTO>(consumeResult.Message.Value);

                if (userBanInfo != null)
                {
                    
                    using (var scope = _serviceProvider.CreateScope())
                    {
                        var userRepository = scope.ServiceProvider.GetRequiredService<IUserRepository>();
                        var authDataContext = scope.ServiceProvider.GetRequiredService<AuthDataContext>();

                        var user = await userRepository.GetUserById(userBanInfo.Id);
                        if (user != null)
                        {
                            user.Ban = userBanInfo.Ban;
                            await authDataContext.SaveChangesAsync();
                            _consumer.Commit(consumeResult);
                        }
                    }
                }
            }
        }, stoppingToken  );
    }
}




