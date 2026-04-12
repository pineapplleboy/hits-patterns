using ClassLibrary;
using System;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

public static class LogSender
{
    private static readonly HttpClient _httpClient = new HttpClient();
    private static readonly string _baseUrl = "http://91.227.18.176/monitoring/patterns/api/v2/add-data/log";

    static LogSender()
    {
        _httpClient.DefaultRequestHeaders.Add("Accept", "*/*");
    }

    public static async Task SendLogAsync(string serviceId, LogStatusEnum status, string message)
    {
        try
        {
            var logData = new
            {
                serviceId = serviceId,
                status = status,
                message = message,
                logTime =  DateTime.UtcNow
            };

            var json = JsonSerializer.Serialize(logData);
            var content = new StringContent(json, Encoding.UTF8, "application/json");

            var response = await _httpClient.PostAsync(_baseUrl, content);

        }
        catch (Exception ex)
        {

            Console.WriteLine($"Ошибка при отправке лога: {ex.Message}");
        }
    }


}