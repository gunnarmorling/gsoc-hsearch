# gsoc-hsearch

This project aims to provide an alternative to the current mass indexer 
implementation, using the Java Batch architecture as defined by JSR 352. This 
standardized tool JSR 352 provides task-and-chunk oriented processing, parallel 
execution and many other optimization features. This batch job should accept 
the entity type(s) to re-index as an input, load the relevant entities from the 
database and rebuild the full-text index from these.


## Run

You can install the project and see test cases using:

    mvn clean install


## Mechanism

This project redesigns the mass index job as a chunk-oriented, non-interactive,
long-running, background execution process. Execution contains operational
control (start/stop/restart), logging, checkpointing and parallelization.

*  **Parallelization**. The core step execution _produceLuceneDoc_ runs in
   parallel. It runs as multiple instance of the same step definition across
   multiple threads, one partition per thread. The number of partitions equals
   to the size of root entities selected before the job start.


## Context data

### JobContextData

`JobContextData` stands for context data in the job scope. It contains common
information potentially used across the whole job.

*  **entityClazzMap**, the map of key value pair (string, class-type),
   designed for storage of name and class type of all root entities. In JSR 352
   standard, only string values can be propagated using job properties, but
   class types are frequently used too. So this map facilites this kind of
   lookup.

   ```java
   Map<String, Class<?>> entityClazzMap;
   ```

Job context data is setup using the `JobContextSetupListener`.


### PartitionedContextData

`PartitionedContextData` stands for partitioned step context data in the step
_produceLuceneDoc_. Since the batch runtime maintains one context data per
partition, this class contains information only belonging to its own partition
progress, which are `chunkWorkCount` and `partitionWorkCount`.

*  **chunkWorkCount** is the elementary work count of the current chunk. A
   _chunk_ refers to items processed between checkpoints, which equals to
   workflow

   ``` 
   ( 1 item-read + 1 item-process ) * N + items-write * 1
   ```

*  **partitionWorkCount** is the sum of all the `chunkWorkCount`s is this
   partition. Partitioned context data is serializable. When job is stopped,
   it will be stored as the persistent user data in the file system through
   the batch runtime and re-used when job restarted. This mechanism make it
   possible to maintain the work progress in attribute `partitionWorkCount`.  

:warning: Please notice that **when method
`PartitionCollector#collectPartitionData` is called, the value returned by
the `PartitionedContextData` should be the `chunkWorkCount` and not
`partitionWorkCount`.** This is because the analyzer in main thread will
summarize itself all elementary count from each partition and compute the
progress. If `partitionWorkCount` is given, then the progress wil be a
double-summarized result, which is not desired.

