using ClassLibrary;

namespace patternsUsers.Services
{
    public interface IUserService
    {
        public Task<UserDB> GetUserById(Guid id);
        public Task<List<UserDTO>> GetUsers(Guid userRequesterId, bool? isClient);
        public Task UnbanUser(Guid userId, string token);
        public Task BanUser(Guid userId, Guid userIdToBan, string token);

    }
}
