// Copyright (c) Duende Software. All rights reserved.
// See LICENSE in the project root for license information.


using ClassLibrary;
using Microsoft.AspNetCore.Identity;

namespace IdentityTest.Models
{
    // Add profile data for application users by adding properties to the ApplicationUser class
    public class ApplicationUser : IdentityUser
    {
        public required bool Ban { get; set; }
        public required UserRole UserRole { get; set; }
    }
}
