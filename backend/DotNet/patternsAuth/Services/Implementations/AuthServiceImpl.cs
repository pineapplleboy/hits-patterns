using ClassLibrary;
using Microsoft.EntityFrameworkCore;
using ClassLibrary.Constants;
using ClassLibrary.Exceptions;
using System.Numerics;
using patternsAuth.Setup;

namespace patternsAuth.Services.Implementations
{
    public class AuthServiceImpl : IAuthService
    {
        private readonly AuthDataContext _context;
        private readonly ITokenService _tokenService;
        private readonly IUserRepository _userRepository;
        public AuthServiceImpl(AuthDataContext context, ITokenService tokenService, IUserRepository userRepository)
        {
            _context = context;
            _tokenService = tokenService;
            _userRepository = userRepository;
        }

        public async Task EmployeeRegisterUser(RegisterUserDTO user)
        {
            bool userExists = await _context.AuthUsers.AnyAsync(u => u.Phone == user.Phone);
            if(userExists) { throw new BadRequestException(ErrorMessages.PHONE_IS_ALREADY_IN_USE); }
            await _context.AuthUsers.AddAsync(new AuthUserDB
            {
                Id = Guid.NewGuid(),
                Phone = user.Phone,
                Password = Hasher.HashPassword(user.Password),
                Ban = false,
                UserRole = user.UserRole
            });
            await _context.SaveChangesAsync();

            //здесь нужно будет брокером переслать в другой сервис
        }

        public async Task<string> LoginEmployee(UserLoginDTO user)
        {
            var foundUser = await _userRepository.GetUserByPhone(user.Phone);
            if (foundUser.UserRole == UserRole.CLIENT) { throw new BadRequestException(ErrorMessages.YOU_ARE_NOT_EMPLOYEE); }
            if(foundUser.Ban == true) { throw new BadRequestException(ErrorMessages.YOU_HAVE_BAN); }

            if (Hasher.CheckPassword(foundUser.Password, user.Password))
            {
                List<string> roles = new List<string> { "Employee" };
                return _tokenService.CreateAccessTokenById(foundUser.Id, roles);
            }
            throw new BadRequestException(ErrorMessages.INVALID_CREDENTIALS);
        }

        public async Task<string> LoginСlient(UserLoginDTO user)
        {
            var foundUser = await _userRepository.GetUserByPhone(user.Phone); 
            if(foundUser.UserRole == UserRole.EMPLOYEE) { throw new BadRequestException(ErrorMessages.YOU_ARE_NOT_CLIENT); }
            if (foundUser.Ban == true) { throw new BadRequestException(ErrorMessages.YOU_HAVE_BAN); }

            if (Hasher.CheckPassword(foundUser.Password, user.Password))
            {
                List<string> roles = new List<string> { "Сlient" };
                string token = _tokenService.CreateAccessTokenById(foundUser.Id, roles);
                return token;
            }
            throw new BadRequestException(ErrorMessages.INVALID_CREDENTIALS);
        }
    }
}
