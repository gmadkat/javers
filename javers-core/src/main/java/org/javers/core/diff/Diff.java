package org.javers.core.diff;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Predicate;
import org.javers.common.exception.JaversException;
import org.javers.core.diff.changetype.PropertyChange;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Diff is a set of (atomic) changes between two graphs of objects.
 * <br><br>
 *
 * Typically it is used to capture and trace changes made by user on his domain data.
 * In this case diff is done between previous and current state of a bunch of domain objects.
 * <br><br>
 *
 * @author bartosz walacik
 */
public class Diff {
    private final List<Change> changes;

    Diff(List<Change> changes) {
        this.changes = changes;
    }

    /**
     * Selects new, removed or changed objects
     *
     * @throws JaversException AFFECTED_CDO_IS_NOT_AVAILABLE if diff is restored from repository,
     *         see {@link Change#getAffectedCdo()}
     */
    public <C extends Change> List getObjectsByChangeType(final Class<C> type) {
        argumentIsNotNull(type);

        return Lists.transform(getChangesByType(type), new Function<C, Object>() {
            public Object apply(C input) {
                return ((Change)input).getAffectedCdo();
            }
        });
    }

    /**
     * Selects objects
     * with changed property for given property name
     *
     * @throws JaversException AFFECTED_CDO_IS_NOT_AVAILABLE if diff is restored from repository,
     *         see {@link Change#getAffectedCdo()}
     */
    public List getObjectsWithChangedProperty(String propertyName){
        argumentIsNotNull(propertyName);
        return Lists.transform(getPropertyChanges(propertyName), new Function<PropertyChange, Object>() {
            public Object apply(PropertyChange input) {
                return input.getAffectedCdo();
            }
        });
    }

    /**
     * Full list of changes
     *
     * @return unmodifiable list
     */
    public List<Change> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    /**
     * Changes that satisfies given filter condition
     */
    public List<Change> getChanges(Predicate<Change> predicate) {
        return Lists.positiveFilter(changes,predicate);
    }

    public <C extends Change> List<C> getChangesByType(final Class<C> type) {
        argumentIsNotNull(type);
        return (List)getChanges(new Predicate<Change>() {
            public boolean apply(Change input) {
                return type.isAssignableFrom(input.getClass());
            }
        });
    }

    /**
     * Selects property changes for given property name
     */
    public List<PropertyChange> getPropertyChanges(final String propertyName) {
        argumentIsNotNull(propertyName);
        return (List)getChanges(new Predicate<Change>() {
            public boolean apply(Change input) {
                return input instanceof PropertyChange && ((PropertyChange)input).getProperty().getName().equals(propertyName);

            }
        });
    }

    public boolean hasChanges() {
        return !changes.isEmpty();
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();

        b.append("changes - ");
        for (Map.Entry<Class<? extends Change>, Integer> e : countByType().entrySet()){
            b.append(e.getKey().getSimpleName()+ ":"+e.getValue()+" ");
        }
        return b.toString().trim();
    }

    public Map<Class<? extends Change>, Integer> countByType(){
        Map<Class<? extends Change>, Integer> result = new HashMap<>();
        for(Change change : changes) {
            Class<? extends Change> key = change.getClass();
            if (result.containsKey(change.getClass())){
                result.put(key, (result.get(key))+1);
            }else{
                result.put(key, 1);
            }
        }
        return result;
    }
}
