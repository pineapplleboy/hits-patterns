using ClassLibrary;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.EntityFrameworkCore;

namespace patternsUserSetting.UserSettingSetup
{
    public class IdempotencyKeyAttribute : Attribute, IAsyncActionFilter
    {
        private const string HeaderName = "idempotencyKey";
        


        public async Task OnActionExecutionAsync(
            ActionExecutingContext context,
            ActionExecutionDelegate next)
        {

            if (context.HttpContext.Request.Headers.TryGetValue(HeaderName, out var key))
            {
                var dbContext = context.HttpContext.RequestServices.GetRequiredService<UserSettingDataContext>();
                var headerKey = Guid.Parse(key);

                var existingKey = await dbContext.idempotencyKeys.Where(k => k.Id == headerKey).FirstOrDefaultAsync();
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
                else
                {
                    var actionContext = await next();

                    if (actionContext.Result is ObjectResult objectResult)
                    {
                        var newKey = new IdempotencyKeyDB()
                        {
                            Id = headerKey,
                            StatusCode = (int)objectResult.StatusCode,
                            Content = System.Text.Json.JsonSerializer.Serialize(objectResult.Value)
                        };
                        await dbContext.idempotencyKeys.AddAsync(newKey);
                        await dbContext.SaveChangesAsync();
                    }
                    return;
                    
                }
            }
            await next();
        }
    }
}
