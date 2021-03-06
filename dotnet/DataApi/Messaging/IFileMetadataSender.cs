using System.Threading.Tasks;
using ToBePrecise.Demo.DataApi.Models;

namespace ToBePrecise.Demo.DataApi.Messaging
{
    public interface IFileMetadataSender {
        Task<bool> Send(FileMetadata message);
    }
}