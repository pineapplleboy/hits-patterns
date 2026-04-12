using ClassLibrary;
using ClassLibrary.BaseSetup;
using IdentityTest.Data;
using IdentityTest.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.Extensions.Logging;

namespace IdentityTest.Pages.Account.Register
{
    public class IndexModel : PageModel
    {
        private readonly UserManager<ApplicationUser> _userManager;
        private readonly SignInManager<ApplicationUser> _signInManager;
        private readonly ILogger<IndexModel> _logger;

        public IndexModel(
            UserManager<ApplicationUser> userManager,
            SignInManager<ApplicationUser> signInManager,
            ILogger<IndexModel> logger)
        {
            _userManager = userManager;
            _signInManager = signInManager;
            _logger = logger;
        }

        [BindProperty]
        public RegisterViewModel Input { get; set; }

        public Task<IActionResult> OnGet(string returnUrl)
        {
            Input = new RegisterViewModel { ReturnUrl = returnUrl };
            return Task.FromResult<IActionResult>(Page());
        }

        public async Task<IActionResult> OnPost()
        {
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
                UserRole = UserRole.CLIENT
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
                    UserRole = UserRole.CLIENT,
                    Ban = false,
                });
                await LogSender.SendLogAsync(ServiceId.AUTH_SERVICE, LogStatusEnum.INFO, $"Зарегестировался пользователь{user.Id}");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to publish new user registration for {Phone}", Input.Phone);
                await _userManager.DeleteAsync(user);
                ModelState.AddModelError(string.Empty, "Не удалось завершить регистрацию. Попробуйте еще раз позже.");
                return Page();
            }

            var claimResult = await _userManager.AddClaimsAsync(user, new System.Security.Claims.Claim[]
            {
                new("role", user.UserRole.ToString())
            });
            if (!claimResult.Succeeded)
            {
                await _userManager.DeleteAsync(user);
                AddErrors(claimResult);
                return Page();
            }

            var loginResult = await _signInManager.PasswordSignInAsync(
                Input.Phone,
                Input.Password,
                false,
                lockoutOnFailure: true);

            if (!loginResult.Succeeded)
            {
                ModelState.AddModelError(string.Empty, "Не удалось выполнить вход после регистрации.");
                return Page();
            }

            if (Url.IsLocalUrl(Input.ReturnUrl))
            {
                return Redirect(Input.ReturnUrl);
            }

            if (string.IsNullOrEmpty(Input.ReturnUrl))
            {
                return Redirect("~/");
            }

            throw new Exception("invalid return url");
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
