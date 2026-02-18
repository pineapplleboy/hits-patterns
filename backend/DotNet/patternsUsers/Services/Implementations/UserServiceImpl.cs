using ClassLibrary;
using ClassLibrary.Constants;
using ClassLibrary.Exceptions;
using Microsoft.EntityFrameworkCore;
using patternsUsers.UsersSetup;

namespace patternsUsers.Services.Implementations
{
    public class UserServiceImpl : IUserService
    {
        private readonly UserDataContext _context;
        public UserServiceImpl(UserDataContext context)
        {
            _context = context;
        }
        public async Task BanUser(Guid userId, Guid userIdToBan)
        {
            if(userId == userIdToBan) { throw new BadRequestException(ErrorMessages.YOU_CANT_BAN_THIS_USER); }
            
            var user = await GetUserById(userIdToBan);
            if(user.Ban == true) {throw new BadRequestException(ErrorMessages.USER_ALREADY_HAS_BAN);}
            user.Ban = true;
            await _context.SaveChangesAsync();
        }

        public async Task UnbanUser(Guid userId)
        {
            var user = await GetUserById(userId);
            if (user.Ban == false) { throw new BadRequestException(ErrorMessages.USER_HAS_NO_BAN); }
            user.Ban = false;
            await _context.SaveChangesAsync();
        }

        public async Task<UserDB> GetUserById(Guid id)
        {
            var foundUser = await _context.users.FirstOrDefaultAsync(u => u.Id == id);
            if (foundUser == null) { throw new NotFoundException(ErrorMessages.USER_NOT_FOUND); }
            return foundUser;
        }

        public async Task<List<UserDTO>> GetUsers(Guid userRequesterId, bool? isClient)
        {
            var users = _context.users.AsQueryable();
            if(isClient == false)
            {
                users = users.Where(u => u.UserRole == UserRole.EMPLOYEE);
            }
            else if(isClient == true)
            {
                users = users.Where(u => u.UserRole == UserRole.CLIENT);
            }
            var res =  await users.Select(
                u => new UserDTO
                {
                    Id = u.Id,
                    Name = u.Name,
                    Phone = u.Phone,
                    Ban = u.Ban,
                    UserRole = u.UserRole,
                    Author = u.Author,
                    IsBannable = u.Id == userRequesterId ? false : true
                }).OrderBy(u => u.Name).ToListAsync();
            return res;
        }


    }
}
