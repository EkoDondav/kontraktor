package org.nustaq.reallive.impl.tablespace;

import org.nustaq.kontraktor.Actor;
import org.nustaq.kontraktor.Callback;
import org.nustaq.kontraktor.IPromise;
import org.nustaq.kontraktor.Promise;
import org.nustaq.reallive.impl.storage.StorageStats;
import org.nustaq.reallive.interfaces.RealLiveTable;
import org.nustaq.reallive.interfaces.TableDescription;
import org.nustaq.reallive.interfaces.TableSpace;
import org.nustaq.reallive.messages.StateMessage;

import java.util.List;

/**
 * Created by ruedi on 15.08.2015.
 */
public class ClusteredTableSpaceClient<T extends ClusteredTableSpaceClient> extends Actor<T> implements TableSpace {

    protected TableSpaceSharding tableSharding;

    @Override
    public IPromise<RealLiveTable> createOrLoadTable(TableDescription desc) {
        return tableSharding.createOrLoadTable(desc);
    }

    @Override
    public IPromise dropTable(String name) {
        return tableSharding.dropTable(name);
    }

    @Override
    public IPromise<List<TableDescription>> getTableDescriptions() {
        return tableSharding.getTableDescriptions();
    }

    public IPromise<List<StorageStats>> getStats() {
        return resolve(tableSharding.getStats());
    }

    @Override
    public IPromise<List<RealLiveTable>> getTables() {
        return tableSharding.getTables();
    }

    @Override
    public IPromise<RealLiveTable> getTable(String name) {
        return tableSharding.getTable(name);
    }

    @Override
    public IPromise shutDown() {
        return tableSharding.shutDown();
    }

    @Override
    public void stateListener(Callback<StateMessage> stateListener) {
        tableSharding.stateListener(stateListener);
    }

}
