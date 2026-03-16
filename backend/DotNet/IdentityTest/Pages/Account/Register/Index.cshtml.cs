using ClassLibrary;
using Confluent.Kafka;
using IdentityTest.Data;
using IdentityTest.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using System.Text.Json;

namespace IdentityTest.Pages.Account.Register
{
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
        public RegisterViewModel Input {  get; set; }

        public async Task<IActionResult> OnGet(string returnUrl)
        {
            Input = new RegisterViewModel { ReturnUrl = returnUrl };
            return Page();
        }

        public async Task<IActionResult> OnPost()
        {
            if(ModelState.IsValid)
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
                    UserRole = UserRole.CLIENT
                };
                var result = await _userManager.CreateAsync(user, Input.Password);

                await KafkaManager.SendNewUserMessage(new UserDB
                {
                    Id = Guid.Parse(user.Id),
                    Phone = Input.Phone,
                    Name = Input.Name,
                    UserRole = UserRole.CLIENT,
                    Ban = false,
                    //Author = 
                });

                if (result.Succeeded)
                {
                    await _userManager.AddClaimsAsync(user, new System.Security.Claims.Claim[]
                    {
                        new System.Security.Claims.Claim("nameid", user.Id.ToString()),
                        new System.Security.Claims.Claim("role", user.UserRole.ToString())
                    });

                    var loginResult = await _signInManager.PasswordSignInAsync(
                        Input.Phone, Input.Password, false, lockoutOnFailure: true);
                    if (loginResult.Succeeded) 
                    {
                        if(Url.IsLocalUrl(Input.ReturnUrl))
                        {
                            return Redirect(Input.ReturnUrl);
                        }
                        else if(string.IsNullOrEmpty(Input.ReturnUrl))
                        {
                            return Redirect("~/");
                        }
                        else
                        {
                            throw new Exception("invalid return url");
                        }
                    }
                }
            }
            return Page();
        }


    }
}
