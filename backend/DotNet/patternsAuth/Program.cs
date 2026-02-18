using patternsAuth.Services.Implementations;
using patternsAuth.Services;
using patternsAuth.Setup;

var builder = WebApplication.CreateBuilder(args);

ClassLibrary.BaseSetup.SetupAspNet.AddAspNet(builder);
ClassLibrary.BaseSetup.SetupSwagger.AddSwagger(builder);
ClassLibrary.BaseSetup.SetupDatabases.AddDatabases<AuthDataContext>(builder);

//сервисы
builder.Services.AddTransient<IAuthService, AuthServiceImpl>();

//репозитории
builder.Services.AddScoped<ITokenService, TokenServiceImpl>();
builder.Services.AddScoped<IUserRepository, UserRepositoryImpl>();

ClassLibrary.BaseSetup.SetupAuth.AddAuth(builder);

var app = builder.Build();


ClassLibrary.BaseSetup.SetupSwagger.UseSwagger(app);

await ClassLibrary.BaseSetup.SetupDatabases.RunMigrations<AuthDataContext>(app, AuthDataSeeder.Seed);

ClassLibrary.BaseSetup.SetupAuth.UseAuth(app);
ClassLibrary.BaseSetup.SetupAspNet.UseAspNet(app);

app.Run();
