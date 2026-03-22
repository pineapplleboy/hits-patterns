FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

COPY ClassLibrary/ClassLibrary.csproj ClassLibrary/
COPY IdentityTest/IdentityTest.csproj IdentityTest/
RUN dotnet restore IdentityTest/IdentityTest.csproj

COPY . .
WORKDIR /src/IdentityTest
RUN dotnet publish IdentityTest.csproj -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .

EXPOSE 5001
ENTRYPOINT ["dotnet", "IdentityTest.dll"]
