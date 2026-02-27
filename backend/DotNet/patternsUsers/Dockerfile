FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

COPY ClassLibrary/ClassLibrary.csproj ClassLibrary/
COPY patternsUsers/patternsUsers.csproj patternsUsers/
RUN dotnet restore patternsUsers/patternsUsers.csproj

COPY . .
WORKDIR /src/patternsUsers
RUN dotnet publish patternsUsers.csproj -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .

EXPOSE 8080
ENTRYPOINT ["dotnet", "patternsUsers.dll"]
