using ClassLibrary;
using Microsoft.EntityFrameworkCore;
using ClassLibrary.Constants;
using ClassLibrary.Exceptions;
using System.Numerics;
using patternsAuth.Setup;
using Confluent.Kafka;
using System.Text.Json;
using ClassLibrary.BaseSetup;

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

        public async Task EmployeeRegisterUser(Guid employeeId, RegisterUserDTO user)
        {
            bool userExists = await _context.AuthUsers.AnyAsync(u => u.Phone == user.Phone);
            if(userExists) { throw new BadRequestException(ErrorMessages.PHONE_IS_ALREADY_IN_USE); }
            var newUser = new AuthUserDB {
                Id = Guid.NewGuid(),
                Phone = user.Phone,
                Password = Hasher.HashPassword(user.Password),
                Ban = false,
                UserRole = user.UserRole
            };
            await _context.AuthUsers.AddAsync(newUser);

            await SendNewUserMessage(new UserDB
            {
                Id = newUser.Id,
                Name = user.Name,
                Phone = newUser.Phone,
                Ban = newUser.Ban,
                UserRole = newUser.UserRole,
                Author = employeeId
            });
            
            await _context.SaveChangesAsync();

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

        private async Task SendNewUserMessage(UserDB newUser)
        {
            var config = new ProducerConfig { BootstrapServers = KafkaOptions.bootstrapServer };
            using (var p = new ProducerBuilder<Null, string>(config).Build())
            {
                var dr = await p.ProduceAsync(KafkaOptions.create_auth_user, new Message<Null, string>
                {
                    Value = JsonSerializer.Serialize(newUser)
                });
            }
        }
    }
}
