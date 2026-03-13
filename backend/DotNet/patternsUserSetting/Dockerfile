FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

COPY ClassLibrary/ClassLibrary.csproj ClassLibrary/
COPY patternsUserSetting/patternsUserSetting.csproj patternsUserSetting/
RUN dotnet restore patternsUserSetting/patternsUserSetting.csproj

COPY . .
WORKDIR /src/patternsUserSetting
RUN dotnet publish patternsUserSetting.csproj -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .

EXPOSE 8080
ENTRYPOINT ["dotnet", "patternsUserSetting.dll"]
