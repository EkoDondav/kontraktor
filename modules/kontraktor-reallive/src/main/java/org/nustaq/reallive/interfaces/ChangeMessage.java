package org.nustaq.reallive.interfaces;

import java.io.Serializable;
import java.util.*;

/**
 * Created by moelrue on 03.08.2015.
 */
public interface ChangeMessage<K> extends Serializable {

    int ADD = 0;
    int REMOVE = 1;
    int UPDATE = 2;
    int QUERYDONE = 3;
    int PUT = 4;

    int getType();

    K getKey();
    ChangeMessage reduced(String[] reducedFields);

    default Set<String> getForcedUpdateFields() {
        return null;
    }
    default boolean isDoneMsg() { return getType() == QUERYDONE; }
    default boolean isAdd() { return getType() == ADD; }
    default Record<K> getRecord() { return null; }

}
