using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using ToBePrecise.Demo.DataApi.FileStorage;
using ToBePrecise.Demo.DataApi.Messaging;
using ToBePrecise.Demo.DataApi.Models;
using ToBePrecise.Demo.DataApi.Progress;
using Newtonsoft.Json;

namespace ToBePrecise.Demo.DataApi.Controllers
{
    [ApiController]
    [Produces("application/json")]
    [Route("api/data")]
    public class DataUploadController : ControllerBase
    {
        private readonly IFileStorage _storage;
        private readonly IFileMetadataSender _sender;
        private readonly IProgressProvider _progress;
        private readonly ILogger<DataUploadController> _logger;

        public DataUploadController(IFileStorage storage, IFileMetadataSender sender, IProgressProvider progress, ILogger<DataUploadController> logger)
        {
            _storage = storage;
            _sender = sender;
            _progress = progress;
            _logger = logger;
        }

        [HttpPost("ingest")]
        public async Task<FileMetadata> Ingest(IFormFile file)
        {
            var metadata = await _storage.Upload(file.OpenReadStream(), file.FileName);
            var success = await _sender.Send(metadata);
            if (!success) {
                throw new Exception("Failed upload");
            }
            return metadata;
        }

        [HttpGet("progress/{FileId}")]
        public async Task<IActionResult> Progress([FromRoute] string FileId) 
        {
            var result = await _progress.GetProgress(FileId);
            return Content(JsonConvert.SerializeObject(result), "application/json");
        }
    }
}
