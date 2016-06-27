package org.hibernate.search.jsr352;

import java.util.Set;

public interface MassIndexer {
    
    public long start();
    public void stop(long executionId);
    
    public MassIndexer arrayCapacity(int arrayCapacity);
    public MassIndexer fetchSize(int fetchSize);
    public MassIndexer maxResults(int maxResults);
    public MassIndexer optimizeAfterPurge(boolean optimizeAfterPurge);
    public MassIndexer optimizeAtEnd(boolean optimizeAtEnd);
    public MassIndexer partitionCapacity(int partitionCapacity);
    public MassIndexer partitions(int partitions);
    public MassIndexer purgeAtStart(boolean purgeAtStart);
    public MassIndexer rootEntities(Set<Class<?>> rootEntities);
    public MassIndexer threads(int threads);
    
    public int getArrayCapacity();
    public int getFetchSize();
    public int getMaxResults();
    public boolean isOptimizeAfterPurge();
    public boolean isOptimizeAtEnd();
    public int getPartitionCapacity();
    public int getPartitions();
    public boolean isPurgeAtStart();
    public Set<Class<?>> getRootEntities();
    public int getThreads();
}