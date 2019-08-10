using System.Threading.Tasks;
using Microsoft.Extensions.Options;
using ToBePrecise.Demo.DataApi.Configurations;
using ToBePrecise.Demo.DataApi.Models;

namespace ToBePrecise.Demo.DataApi.Messaging
{
public class KafkaFileMetadataSender : IFileMetadataSender
{
    public KafkaFileMetadataSender(IOptions<KafkaOptions> kafkaOptions) 
    {

        }
        public Task Send(FileMetadata message)
        {
            throw new System.NotImplementedException();
        }
    }
}