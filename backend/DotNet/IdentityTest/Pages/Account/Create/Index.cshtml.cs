using ClassLibrary;
using IdentityTest.Data;
using IdentityTest.Models;
using IdentityTest.Pages.Account.Register;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.Extensions.Logging;

namespace IdentityTest.Pages.Account.Create
{
    [Authorize(Roles = "EMPLOYEE")]
    public class IndexModel : PageModel
    {
        private readonly UserManager<ApplicationUser> _userManager;
        private readonly ILogger<IndexModel> _logger;

        public IndexModel(UserManager<ApplicationUser> userManager, SignInManager<ApplicationUser> signInManager, ILogger<IndexModel> logger)
        {
            _userManager = userManager;
            _logger = logger;
        }

        [BindProperty]
        public CreateViewModel Input { get; set; }

        public Task<IActionResult> OnGet()
        {
            var employeeCheck = IsEmployee();
            if (employeeCheck != null)
            {
                return Task.FromResult(employeeCheck);
            }

            return Task.FromResult<IActionResult>(Page());
        }

        public async Task<IActionResult> OnPost()
        {
            var employeeCheck = IsEmployee();
            if (employeeCheck != null)
            {
                return employeeCheck;
            }

            if (!ModelState.IsValid)
            {
                return Page();
            }

            var existingUser = await _userManager.FindByNameAsync(Input.Phone);
            if (existingUser != null)
            {
                ModelState.AddModelError("Input.Phone", "Этот телефон уже зарегистрирован");
                return Page();
            }

            var user = new ApplicationUser
            {
                UserName = Input.Phone,
                Email = Input.Name,
                Ban = false,
                UserRole = Input.UserRole
            };

            var result = await _userManager.CreateAsync(user, Input.Password);
            if (!result.Succeeded)
            {
                AddErrors(result);
                return Page();
            }

            var roleResult = await _userManager.AddToRoleAsync(user, user.UserRole.ToString());
            if (!roleResult.Succeeded)
            {
                await _userManager.DeleteAsync(user);
                AddErrors(roleResult);
                return Page();
            }

            try
            {
                await KafkaManager.SendNewUserMessage(new UserDB
                {
                    Id = Guid.Parse(user.Id),
                    Phone = Input.Phone,
                    Name = Input.Name,
                    UserRole = Input.UserRole,
                    Ban = false,
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to publish created user {Phone}", Input.Phone);
                await _userManager.DeleteAsync(user);
                ModelState.AddModelError(string.Empty, "Не удалось создать пользователя. Попробуйте еще раз позже.");
                return Page();
            }

            TempData["SuccessMessage"] = $"Пользователь {Input.Phone} успешно создан!";
            Input = new CreateViewModel();
            return RedirectToPage();
        }

        private IActionResult IsEmployee()
        {
            var roleClaim = User.Claims.FirstOrDefault(c => c.Type == "role" && c.Value == "EMPLOYEE");
            if (roleClaim == null)
            {
                return RedirectToPage("/Account/AccessDenied");
            }

            return null;
        }

        private void AddErrors(IdentityResult result)
        {
            foreach (var error in result.Errors)
            {
                ModelState.AddModelError(string.Empty, error.Description);
            }
        }
    }
}
