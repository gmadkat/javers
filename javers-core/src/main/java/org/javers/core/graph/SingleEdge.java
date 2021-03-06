package org.javers.core.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

/**
 * OneToOne or ManyToOne relation
 * <br>
 * Immutable
 *
 * @author bartosz walacik
 */
class SingleEdge extends AbstractSingleEdge {
    private final ObjectNode referencedNode;

    SingleEdge(Property property, ObjectNode referencedNode) {
        super(property);
        Validate.argumentsAreNotNull(referencedNode);
        this.referencedNode = referencedNode;
    }

    @Override
    public GlobalId getReference() {
        return referencedNode.getGlobalId();
    }
}
