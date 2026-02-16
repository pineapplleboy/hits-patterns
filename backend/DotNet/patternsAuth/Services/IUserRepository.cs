using ClassLibrary;

namespace patternsAuth.Services
{
    public interface IUserRepository
    {
        public Task<AuthUserDB> GetUserByPhone(string phone);
    }
}
