package org.nustaq.reallive.messages;

import org.nustaq.kontraktor.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ruedi on 03/08/15.
 *
 */
public class Diff implements Serializable {

    final String changedFields[];
    final Object oldValues[];

    public Diff(String[] changedFields, Object[] oldValues) {
        this.changedFields = changedFields;
        this.oldValues = oldValues;
    }

    public String[] getChangedFields() {
        return changedFields;
    }

    public Object[] getOldValues() {
        return oldValues;
    }

    @Override
    public String toString() {
        return "Diff{" +
                "changedFields=" + Arrays.toString(changedFields) +
                ", oldValues=" + Arrays.toString(oldValues) +
                '}';
    }

    public Diff reduced(String[] reducedFields) {
        ArrayList<Pair<String,Object>> newDiff = new ArrayList<>();
        for (int i = 0; i < changedFields.length; i++) {
            String changedField = changedFields[i];
            for (int j = 0; j < reducedFields.length; j++) {
                String reducedField = reducedFields[j];
                if ( changedField.equals(reducedField) ) {
                    newDiff.add(new Pair<>(reducedField,oldValues[i]));
                }
            }
        }
        String newChanged[] = new String[newDiff.size()];
        Object newVals[] = new Object[newDiff.size()];
        for (int i = 0; i < newDiff.size(); i++) {
            Pair<String, Object> stringObjectPair = newDiff.get(i);
            newChanged[i] = stringObjectPair.car();
            newVals[i] = stringObjectPair.cdr();
        }
        return new Diff(newChanged,newVals);
    }
}
