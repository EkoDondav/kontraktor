package org.nustaq.reallive.interfaces;


import org.nustaq.reallive.impl.storage.StorageStats;

import java.util.stream.Stream;

/**
 * Created by moelrue on 03.08.2015.
 */
public interface RecordStorage {

    RecordStorage put( String key, Record value );
    Record get( String key );
    Record remove( String key );
    long size();
    StorageStats getStats();
    Stream<Record> stream();

}
