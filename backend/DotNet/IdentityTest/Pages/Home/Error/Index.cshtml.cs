// Copyright (c) Duende Software. All rights reserved.
// See LICENSE in the project root for license information.

using Duende.IdentityServer.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace IdentityTest.Pages.Error
{
    [AllowAnonymous]
    [SecurityHeaders]
    public class Index : PageModel
    {
        private readonly IIdentityServerInteractionService _interaction;
        public ViewModel View { get; set; } = new();

        public Index(IIdentityServerInteractionService interaction)
        {
            _interaction = interaction;
        }

        public async Task OnGet(string? errorId)
        {
            // retrieve error details from identityserver
            var message = await _interaction.GetErrorContextAsync(errorId);
            if (message != null)
            {
                View.Error = message;
            }
        }
    }
}


