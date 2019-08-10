namespace ToBePrecise.Demo.DataApi.Configurations 
{
    public class KafkaOptions
    {
        public string Uri { get; set; }
        public string InboxTopic { get; set; }
        public int MessageTimeoutMs { get; set; } = 5000;

    }
}