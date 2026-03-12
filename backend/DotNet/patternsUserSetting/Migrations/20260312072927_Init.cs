using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace patternsUserSetting.Migrations
{
    /// <inheritdoc />
    public partial class Init : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "hiddenAccounts",
                columns: table => new
                {
                    HiddenAccountId = table.Column<Guid>(type: "uuid", nullable: false),
                    UserId = table.Column<Guid>(type: "uuid", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_hiddenAccounts", x => x.HiddenAccountId);
                });

            migrationBuilder.CreateTable(
                name: "userSettings",
                columns: table => new
                {
                    UserId = table.Column<Guid>(type: "uuid", nullable: false),
                    IsDarkMode = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_userSettings", x => x.UserId);
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "hiddenAccounts");

            migrationBuilder.DropTable(
                name: "userSettings");
        }
    }
}
