using System.Threading.Tasks;
using ToBePrecise.Demo.DataApi.Models;

namespace ToBePrecise.Demo.DataApi.Progress
{
    public interface IProgressProvider {
        Task<ProgressReport> GetProgress(string FileId);
    }
}