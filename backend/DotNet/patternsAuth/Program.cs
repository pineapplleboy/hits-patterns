using patternsAuth.Setup;

var builder = WebApplication.CreateBuilder(args);

ClassLibrary.BaseSetup.SetupAspNet.AddAspNet(builder);
ClassLibrary.BaseSetup.SetupSwagger.AddSwagger(builder);

SetupDatabases.AddDatabases(builder);
SetupServices.AddServices(builder.Services);
SetupRepositories.AddRepositories(builder.Services);

ClassLibrary.BaseSetup.SetupAuth.AddAuth(builder);

var app = builder.Build();


ClassLibrary.BaseSetup.SetupSwagger.UseSwagger(app);

await SetupDatabases.RunMigrations(app);

ClassLibrary.BaseSetup.SetupAuth.UseAuth(app);
ClassLibrary.BaseSetup.SetupAspNet.UseAspNet(app);

app.Run();
