using System.Linq;
using System.Threading.Tasks;
using Microsoft.Extensions.Options;
using StackExchange.Redis;
using ToBePrecise.Demo.DataApi.Configurations;
using ToBePrecise.Demo.DataApi.Models;

namespace ToBePrecise.Demo.DataApi.Progress
{
    public class RedisProgresProvider : IProgressProvider
    {
        private readonly ConnectionMultiplexer _redis;

        public RedisProgresProvider(IOptions<RedisOptions> redisOptions) {
            _redis = ConnectionMultiplexer.Connect(redisOptions.Value.ConnectionString);
        }
        public async Task<ProgressReport> GetProgress(string FileId)
        {
            IDatabase db = _redis.GetDatabase();
            var contextIds = db.HashGetAll(RedisKey(FileId))
                .Where(entry => entry.Name.Equals("contextId"))
                .Select(entry => entry.Value);
            var report = new ProgressReport();
            
            foreach (var contextId in contextIds) {

                var progressEntries = await db.HashGetAllAsync(RedisKey(contextId));
                var progress = progressEntries.ToDictionary();
                var fileId = progress["fileId"].IsNullOrEmpty ? "" : progress["fileId"].ToString();
                var fileName = progress["fileName"].IsNullOrEmpty ? "" : progress["fileName"].ToString();
                int numberOfItems = progress["items"].IsInteger ? (int)progress["items"] : 0;
                var items = new bool[numberOfItems];
                for (int i = 0; i < numberOfItems; i++) {
                    items[i] = progress[i].IsInteger ? ((int)progress[i] > 0) : false;
                }
                report.Add(new ProcessingProgress() {
                    FileId = fileId,
                    FileName = fileName,
                    Items = items
                });                
            }
            return report;
        }

        private string RedisKey(string id) {
            return $"context{id}";
        }
    }
}