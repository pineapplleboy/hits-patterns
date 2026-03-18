using ClassLibrary;
using IdentityTest.Data;
using IdentityTest.Models;
using IdentityTest.Pages.Account.Register;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using System.Security.Claims;

namespace IdentityTest.Pages.Account.Create
{

    [Authorize(Roles = "EMPLOYEE")]
    public class IndexModel : PageModel
    {

        private readonly UserManager<ApplicationUser> _userManager;
        private readonly SignInManager<ApplicationUser> _signInManager;
        public IndexModel(UserManager<ApplicationUser> userManager, SignInManager<ApplicationUser> signInManager)
        {
            _userManager = userManager;
            _signInManager = signInManager;
        }

        
        [BindProperty]
        public CreateViewModel Input { get; set; }
        public async Task<IActionResult> OnGet()
        {
            IsEmployee();
            return Page();
        }

        public async Task<IActionResult> OnPost()
        {
            IsEmployee();
            if (ModelState.IsValid)
            {
                var existingUser = await _userManager.FindByNameAsync(Input.Phone);
                if (existingUser != null)
                {
                    ModelState.AddModelError("Input.Phone", "Этот телефон уже зарегистрирован");
                    return Page();
                }

                var user = new ApplicationUser()
                {
                    UserName = Input.Phone,
                    Email = Input.Name,
                    Ban = false,
                    UserRole = Input.UserRole
                };
                var result = await _userManager.CreateAsync(user, Input.Password);
                await _userManager.AddToRoleAsync(user, user.UserRole.ToString());
                if (result.Succeeded)
                {
                    await KafkaManager.SendNewUserMessage(new UserDB
                    {
                        Id = Guid.Parse(user.Id),
                        Phone = Input.Phone,
                        Name = Input.Name,
                        UserRole = Input.UserRole,
                        Ban = false,
                        //Author = 
                    });
                    TempData["SuccessMessage"] = $"Пользователь {Input.Phone} успешно создан!";
                    Input = new CreateViewModel();
                    
                    return RedirectToPage();
                }
            }

            return Page();
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
    }
}
 