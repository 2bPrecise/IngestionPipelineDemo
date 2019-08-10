using System;

namespace ToBePrecise.Demo.DataApi.Models
{
    public class FileMetadata {
        public string FileId { get; set; }
        public string OriginalName { get; set; }
        public string RelativePath { get; set; }
        public long? Size { get; set; }
        public DateTime UploadedTime { get; set; }
    }
}