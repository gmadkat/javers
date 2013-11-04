package org.javers.model.domain;

import org.javers.model.mapping.Property;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract class PropertyChange extends Change {
    private final Property property;

    protected PropertyChange(GlobalCdoId globalCdoId, Diff parent, Property property) {
        super(globalCdoId, parent);
        argumentIsNotNull(property);
        this.property = property;
    }

    /**
     * Affected property
     */
    public Property getProperty() {
        return property;
    }
}
