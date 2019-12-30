/*
 * @COPYRIGHT (C) 2017 Schenker AG
 *
 * All rights reserved.
 */
package sharetransport.infrastructure.domain;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A base class for value objects like a geopoint (see DDD Value object))
 */
public abstract class AbstractValueObject implements Serializable {

    private transient Object[] values;

    public int hashCode() {
        return Arrays.hashCode(getValues());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AbstractValueObject && object.getClass().isAssignableFrom(getClass()))) {
            return false;
        }
        AbstractValueObject valueObject = (AbstractValueObject) object;
        return Arrays.equals(getValues(), valueObject.getValues());
    }

    /**
     * Returns all values of this value object
     * Subclasses have to implement this method
     * and return the actual values that make up this value object.
     */
    protected abstract Object[] values();

    private Object[] getValues() {
        if (values == null) {
            values = values();
        }
        return values;
    }
}
