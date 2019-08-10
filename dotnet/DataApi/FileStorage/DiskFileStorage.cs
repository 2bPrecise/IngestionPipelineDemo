using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.Extensions.Options;
using ToBePrecise.Demo.DataApi.Configurations;
using ToBePrecise.Demo.DataApi.Models;

namespace ToBePrecise.Demo.DataApi.FileStorage
{
    public class DiskFileStorage : IFileStorage
    {
        private readonly string BasePath;

        public DiskFileStorage(IOptions<FileStorageOptions> fileStorageOptions) {
            BasePath = fileStorageOptions.Value.BasePath;
        }
        public async Task<FileMetadata> Upload(Stream inputStream, string OriginalName)
        {
            string fileId = Guid.NewGuid().ToString();
            string relativePath = fileId + Path.GetExtension(OriginalName);
            string fullPath = Path.Combine(BasePath, relativePath);
            using (Stream file = File.Create(fullPath)) {
                await inputStream.CopyToAsync(file);
            };
            return new FileMetadata()
            {
                FileId = fileId,
                OriginalName = OriginalName,
                RelativePath = relativePath,
                UploadedTime = DateTime.Now,
                Size = inputStream.Position
            };
        }

    }
}