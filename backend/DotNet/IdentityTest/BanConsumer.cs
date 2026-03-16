using ClassLibrary;
using ClassLibrary.BaseSetup;
using Confluent.Kafka;
using IdentityTest.Models;
using Microsoft.AspNetCore.Identity;
using Serilog;
using System.Text.Json;

namespace IdentityTest
{
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
            Log.Logger = new LoggerConfiguration()
            .WriteTo.Console()
            .CreateBootstrapLogger();

            
            var _consumer = new ConsumerBuilder<string, string>(config).Build();
            _consumer.Subscribe(KafkaOptions.ban_user_auth);

            await Task.Run(async () =>
            {
                Log.Information("Я кафка");
                while (!stoppingToken.IsCancellationRequested)
                {
                    var consumeResult = _consumer.Consume(stoppingToken);
                    var userBanInfo = JsonSerializer.Deserialize<UserBanDTO>(consumeResult.Message.Value);

                    if (userBanInfo != null)
                    {
                        Log.Information("Есть пользователь");
                        using (var scope = _serviceProvider.CreateScope())
                        {
                            var _userManager = scope.ServiceProvider.GetRequiredService<UserManager<ApplicationUser>>();
                            var user = await _userManager.FindByIdAsync(userBanInfo.Id.ToString());

                            
                            if (user != null)
                            {
                                user.Ban = userBanInfo.Ban;
                                var updateResult = await _userManager.UpdateAsync(user);
                                _consumer.Commit(consumeResult);
                            }
                        }
                    }
                }
            }, stoppingToken);
        }
    }
}
