
using ClassLibrary.Exceptions;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;


namespace ClassLibrary
{
    public class ExceptionHandler : IExceptionHandler
    {
        public async ValueTask<bool> TryHandleAsync(
            HttpContext httpContext,
            Exception exception,
            CancellationToken cancellationToken)
        {
            ProblemDetails problemDetails = new ProblemDetails();

            if (exception is CustomException customException)
                problemDetails = new ProblemDetails
                {
                    Status = customException.Code,
                    Detail = customException.Message
                };
            else if (exception is UnauthorizedAccessException)
                problemDetails = new ProblemDetails
                {
                    Status = 401,
                    Detail = "Токен невалиден!"
                };
            else
                problemDetails = new ProblemDetails
                {
                    Status = StatusCodes.Status500InternalServerError,
                    Detail = "Ошибка сервера"
                };

            httpContext.Response.StatusCode = problemDetails.Status.Value;

            await httpContext.Response
                .WriteAsJsonAsync(problemDetails, cancellationToken);

            return true;
        }
    }
}
