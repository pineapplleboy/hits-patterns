using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace patternsUsers.Migrations
{
    /// <inheritdoc />
    public partial class indkeySec : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_idempotencyKeys",
                table: "idempotencyKeys");

            migrationBuilder.RenameTable(
                name: "idempotencyKeys",
                newName: "idempotencyKeys2");

            migrationBuilder.AddPrimaryKey(
                name: "PK_idempotencyKeys2",
                table: "idempotencyKeys2",
                column: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_idempotencyKeys2",
                table: "idempotencyKeys2");

            migrationBuilder.RenameTable(
                name: "idempotencyKeys2",
                newName: "idempotencyKeys");

            migrationBuilder.AddPrimaryKey(
                name: "PK_idempotencyKeys",
                table: "idempotencyKeys",
                column: "Id");
        }
    }
}
