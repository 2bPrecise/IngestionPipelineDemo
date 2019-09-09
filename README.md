# IngestionPipelineDemo
A demo application for a data ingestion pipeline

The goal of the pipeline is to ingest CSV and JSON files from a web-service API to mongo DB.

The pipeline stages are:

1. API (.Net Core 3) receives the file, stores in a directory, and sends a notification to an Apache Kafka topic. (POST /api/data/ingest)
2. Apache Storm Parser topology parses the file, deconstructing each to multiple DTO records. Each record is sent to an Apache Kafka topic.
3. Apache Storm Processor topology processes the DTO and saves it in MongoDB.
4. The processing status is saved in Redis, and can be accessed from the API. (GET /api/data/progress/[file id])

![Architecture diagram](https://raw.githubusercontent.com/2bPrecise/IngestionPipelineDemo/master/Architecture.png)


## Parser topology

![Parser Topology](https://raw.githubusercontent.com/2bPrecise/IngestionPipelineDemo/master/parserTopologyStructure.png)

## Processor toplogy

![Processor Topology](https://raw.githubusercontent.com/2bPrecise/IngestionPipelineDemo/master/processorTopologyStructure.png)

## What features are demonstrated here

1. Interoperability: Communicate between components using JSON objects
2. Cross topology context
3. Examples of unit and integration testing

## What you will not find here, but you should consider doing

1. Logging and monitoring
2. Extensibility (for example: plugins)
3. Priority queues
4. Handle timezones

