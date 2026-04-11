using ClassLibrary;
using ClassLibrary.Exceptions;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.EntityFrameworkCore;

namespace patternsUsers.UsersSetup
{
    public class IdempotencyKeyUsersAttribute : Attribute, IAsyncResultFilter, IAsyncActionFilter  // здесь теперьдргуо фильтро
    {
        private const string HeaderName = "idempotencyKey";

        public async Task OnActionExecutionAsync(
            ActionExecutingContext context,
            ActionExecutionDelegate next)
        {

            if (context.HttpContext.Request.Headers.TryGetValue(HeaderName, out var key))
            {
                var dbContext = context.HttpContext.RequestServices.GetRequiredService<UserDataContext>();
                var headerKey = Guid.Parse(key);

                var existingKey = await dbContext.idempotencyKeys2.Where(k => k.Id == headerKey).FirstOrDefaultAsync();
                if (existingKey != null)
                {
                    context.Result = new ContentResult
                    {
                        StatusCode = existingKey.StatusCode,
                        Content = existingKey.Content,
                        ContentType = "application/json"
                    };
                    return;
                }
  
            }
            await next();
        }
    

        public async Task OnResultExecutionAsync(
            ResultExecutingContext context,
            ResultExecutionDelegate next)
        {

            if (context.HttpContext.Request.Headers.TryGetValue(HeaderName, out var key))
            {
                var dbContext = context.HttpContext.RequestServices.GetRequiredService<UserDataContext>();
                var headerKey = Guid.Parse(key);
                var existingKey = await dbContext.idempotencyKeys2.Where(k => k.Id == headerKey).FirstOrDefaultAsync();

                if (existingKey == null)
                {
                    int statusCode = 500;
                    string content = "";
    
                        await next();


                    if (context.Result is ObjectResult objectResult)
                    {
                        statusCode = (int)objectResult.StatusCode;
                        content = System.Text.Json.JsonSerializer.Serialize(objectResult.Value);
                    }
                    else if (context.Result is StatusCodeResult statusCodeResult)
                    {
                        statusCode = statusCodeResult.StatusCode;
                        content = "";
                    }
                    if (context.Result is ContentResult contentResult)
                    {
                        statusCode = contentResult.StatusCode ?? 200;
                        content = contentResult.Content ?? "";
                    }






                    var newKey = new IdempotencyKeyDB()
                    {
                        Id = headerKey,
                        StatusCode = statusCode,
                        Content = content
                    };

                    await dbContext.idempotencyKeys2.AddAsync(newKey);
                    await dbContext.SaveChangesAsync();
                    return;

                }
            }
            await next();
        }
    }
}
