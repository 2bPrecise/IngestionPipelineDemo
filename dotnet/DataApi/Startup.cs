using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using ToBePrecise.Demo.DataApi.Configurations;
using ToBePrecise.Demo.DataApi.FileStorage;
using ToBePrecise.Demo.DataApi.Messaging;
using ToBePrecise.Demo.DataApi.Progress;

namespace ToBePrecise.Demo.DataApi
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.Configure<KafkaOptions>(Configuration.GetSection("Kafka"));
            services.AddScoped<IFileMetadataSender, KafkaFileMetadataSender>();

            services.Configure<FileStorageOptions>(Configuration.GetSection("FileStorage"));
            services.AddScoped<IFileStorage, DiskFileStorage>();

            services.Configure<RedisOptions>(Configuration.GetSection("Redis"));
            services.AddScoped<IProgressProvider, RedisProgresProvider>();

            services.AddControllers();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseRouting();

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
}
