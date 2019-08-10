using System.IO;
using System.Threading.Tasks;
using ToBePrecise.Demo.DataApi.Models;

namespace ToBePrecise.Demo.DataApi.FileStorage
{
    public interface IFileStorage {
        Task<FileMetadata> Upload(Stream inputStream, string OriginalName);
    }
}