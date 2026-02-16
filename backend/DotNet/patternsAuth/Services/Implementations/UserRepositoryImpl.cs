using ClassLibrary;
using Microsoft.EntityFrameworkCore;
using patternsAuth.Constants;
using patternsAuth.Exceptions;

namespace patternsAuth.Services.Implementations
{
    public class UserRepositoryImpl : IUserRepository
    {
        private readonly DataContext _context;
        public UserRepositoryImpl(DataContext context)
        {
            _context = context;
        }
        public async Task<AuthUserDB> GetUserByPhone(string phone)
        {
            var foundUser = await _context.AuthUsers.FirstOrDefaultAsync(u => u.Phone == phone);
            if(foundUser == null) { throw new NotFoundException(ErrorMessages.USER_NOT_FOUND); }
            return foundUser;
        }
    }
}
