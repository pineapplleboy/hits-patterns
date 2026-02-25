using ClassLibrary;

namespace patternsAuth.Services
{
    public interface IAuthService
    {
        public Task<string> LoginСlient(UserLoginDTO user);
        public Task<string> LoginEmployee(UserLoginDTO user);
        public Task EmployeeRegisterUser(Guid employeeId, RegisterUserDTO user);
        public Task<string> UserRegister(RegisterDTO user, UserRole userRole);
    }
}
