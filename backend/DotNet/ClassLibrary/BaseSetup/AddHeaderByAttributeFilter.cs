using Microsoft.OpenApi.Models;
using Swashbuckle.AspNetCore.SwaggerGen;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassLibrary.BaseSetup
{
    public class AddHeaderByAttributeFilter<TAttribute> : IOperationFilter where TAttribute : Attribute
    {
        public void Apply(OpenApiOperation operation, OperationFilterContext context)
        {
            var hasAttribute = context.MethodInfo
                .GetCustomAttributes(true)
                .Any(attr => attr is TAttribute);

            if (hasAttribute)
            {
                operation.Parameters ??= new List<OpenApiParameter>();
                operation.Parameters.Add(new OpenApiParameter
                {
                    Name = "idempotencyKey",
                    In = ParameterLocation.Header,
                    Required = false,
                    Schema = new OpenApiSchema { Type = "string", Format = "uuid" }
                });
            }
        }
    }
}
