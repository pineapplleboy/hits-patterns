using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Swashbuckle.AspNetCore.SwaggerGen;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary.BaseSetup
{
    public class SetupIdempotencyKey
    {
        public static void AddFilter<TFilter>(WebApplicationBuilder builder)
        where TFilter : IOperationFilter, new()
        {
            builder.Services.AddSwaggerGen(c =>
            {
                c.OperationFilter<TFilter>();
            });
        }
    }
}
