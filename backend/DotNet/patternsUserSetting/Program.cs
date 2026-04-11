



using ClassLibrary.BaseSetup;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using patternsUserSetting.Services;
using patternsUserSetting.Services.Implementations;
using patternsUserSetting.UserSettingSetup;
using Swashbuckle.AspNetCore.SwaggerGen;

var builder = WebApplication.CreateBuilder(args);

ClassLibrary.BaseSetup.SetupAspNet.AddAspNet(builder);
ClassLibrary.BaseSetup.SetupSwagger.AddSwagger(builder);


ClassLibrary.BaseSetup.SetupIdempotencyKey.AddFilter<AddHeaderByAttributeFilter<IdempotencyKeyAttribute>>(builder);

ClassLibrary.BaseSetup.SetupDatabases.AddDatabases<UserSettingDataContext>(builder);

//сервисы
builder.Services.AddTransient<IUserSettingService, UserSettingImpl>();



ClassLibrary.BaseSetup.SetupAuth.AddAuth(builder);

var app = builder.Build();


ClassLibrary.BaseSetup.SetupSwagger.UseSwagger(app);

await ClassLibrary.BaseSetup.SetupDatabases.RunMigrations<UserSettingDataContext>(app);

ClassLibrary.BaseSetup.SetupAuth.UseAuth(app);
ClassLibrary.BaseSetup.SetupAspNet.UseAspNet(app);

app.Run();
