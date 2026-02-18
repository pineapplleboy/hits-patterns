

using patternsUsers.Services;
using patternsUsers.Services.Implementations;
using patternsUsers.UsersSetup;

var builder = WebApplication.CreateBuilder(args);

ClassLibrary.BaseSetup.SetupAspNet.AddAspNet(builder);
ClassLibrary.BaseSetup.SetupSwagger.AddSwagger(builder);
ClassLibrary.BaseSetup.SetupDatabases.AddDatabases<UserDataContext>(builder);

//сервисы
builder.Services.AddTransient<IUserService, UserServiceImpl>();

//репозитории


ClassLibrary.BaseSetup.SetupAuth.AddAuth(builder);

var app = builder.Build();


ClassLibrary.BaseSetup.SetupSwagger.UseSwagger(app);

await ClassLibrary.BaseSetup.SetupDatabases.RunMigrations<UserDataContext>(app, UserSeeder.Seed);

ClassLibrary.BaseSetup.SetupAuth.UseAuth(app);
ClassLibrary.BaseSetup.SetupAspNet.UseAspNet(app);

app.Run();
