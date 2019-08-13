using System;
using System.Text;
using System.Threading.Tasks;
using Confluent.Kafka;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using ToBePrecise.Demo.DataApi.Configurations;
using ToBePrecise.Demo.DataApi.Models;

namespace ToBePrecise.Demo.DataApi.Messaging
{
    public class KafkaFileMetadataSender : IFileMetadataSender
    {
        private readonly ProducerConfig _config;
        private readonly string _topic;
        private readonly JsonSerializerSettings _jsonSerializerConfig;

        public KafkaFileMetadataSender(IOptions<KafkaOptions> kafkaOptions) 
        {
            _config = new ProducerConfig() 
            { 
                BootstrapServers = kafkaOptions.Value.Uri,
                MessageTimeoutMs = kafkaOptions.Value.MessageTimeoutMs
            };
            _topic = kafkaOptions.Value.InboxTopic;
            _jsonSerializerConfig = new JsonSerializerSettings
            {
                ContractResolver = new CamelCasePropertyNamesContractResolver()
            };
        }
        public async Task<bool> Send(FileMetadata message)
        {
            var messageString = JsonConvert.SerializeObject(message, _jsonSerializerConfig);
            /* 
            Example message:
            {
                "fileId": "c883d507-f0ff-4c07-b60a-a14ebc390e07",
                "originalName": "ABC_FN.csv",
                "relativePath": "c883d507-f0ff-4c07-b60a-a14ebc390e07.csv",
                "size": 838,
                "uploadedTime": "2019-08-13T07:59:21.418516+03:00"
            }
            */
            using (var producer = new ProducerBuilder<Null, string>(_config).Build())
            {
                    var deliveryResult = await producer.ProduceAsync(_topic, new Message<Null, string> { Value = messageString });
                    return deliveryResult.Status == PersistenceStatus.Persisted;
            }
        }
    }
}