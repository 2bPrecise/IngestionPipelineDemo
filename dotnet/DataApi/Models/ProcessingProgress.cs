using System;
using System.Collections.Generic;

namespace ToBePrecise.Demo.DataApi.Models
{
    public class ProcessingProgress {
        public string FileId { get; set; }
        public string FileName { get; set; }
        public IEnumerable<Boolean> Items { get; set; }
    }
}