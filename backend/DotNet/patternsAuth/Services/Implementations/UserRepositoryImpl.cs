using ClassLibrary;
using Microsoft.EntityFrameworkCore;
using ClassLibrary.Constants;
using ClassLibrary.Exceptions;
using patternsAuth.Setup;

namespace patternsAuth.Services.Implementations
{
    public class UserRepositoryImpl : IUserRepository
    {
        private readonly AuthDataContext _context;
        public UserRepositoryImpl(AuthDataContext context)
        {
            _context = context;
        }
        public async Task<AuthUserDB> GetUserByPhone(string phone)
        {
            var foundUser = await _context.AuthUsers.FirstOrDefaultAsync(u => u.Phone == phone);
            if(foundUser == null) { throw new NotFoundException(ErrorMessages.USER_NOT_FOUND); }
            return foundUser;
        }

        public async Task<AuthUserDB> GetUserById(Guid id)
        {
            var foundUser = await _context.AuthUsers.FirstOrDefaultAsync(u => u.Id == id);
            if (foundUser == null) { throw new NotFoundException(ErrorMessages.USER_NOT_FOUND); }
            return foundUser;
        }
    }
}
