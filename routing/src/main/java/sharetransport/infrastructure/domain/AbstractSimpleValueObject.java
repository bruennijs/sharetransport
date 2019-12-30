package sharetransport.infrastructure.domain;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A base class for simple value objects like Geo point.
 */
public abstract class AbstractSimpleValueObject<V extends Serializable & Comparable<? super V>>
        implements Serializable, Comparable<AbstractSimpleValueObject<V>> {

    public static final String VALUE = "value";

    private V value;

    protected AbstractSimpleValueObject() {
        // required for proxying
    }

    public AbstractSimpleValueObject(V initialValue) {
        value = validateAndNormalize(initialValue);
    }

    /**
     * Validates and normalizes the constructor parameter of this simple value object.
     * This implementation checks the value to be non-null.
     * Subclasses may override this method to alter validation and normalization.
     *
     * @param value The constructor value.
     * @return The validated and normalized value.
     */
    protected V validateAndNormalize(final V value) {
        return requireNonNull(value, "value must not be null");
    }

    /**
     * Returns the simple value of this value object.
     * May be overridden by
     *
     * @return the simple value
     */
    @JsonValue
    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof AbstractSimpleValueObject && object.getClass().isAssignableFrom(getClass())) {
            return getValue().equals(((AbstractSimpleValueObject) object).getValue());
        }
        return false;
    }

    @Override
    public int compareTo(AbstractSimpleValueObject<V> object) {
        return getValue().compareTo(object.getValue());
    }
}
