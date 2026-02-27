FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

COPY ClassLibrary/ClassLibrary.csproj ClassLibrary/
COPY patternsAuth/patternsAuth.csproj patternsAuth/
RUN dotnet restore patternsAuth/patternsAuth.csproj

COPY . .
WORKDIR /src/patternsAuth
RUN dotnet publish patternsAuth.csproj -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .

EXPOSE 8080
ENTRYPOINT ["dotnet", "patternsAuth.dll"]
