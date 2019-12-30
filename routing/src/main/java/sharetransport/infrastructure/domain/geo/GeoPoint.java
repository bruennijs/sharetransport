package sharetransport.infrastructure.domain.geo;

import java.io.Serializable;

import sharetransport.infrastructure.domain.AbstractValueObject;

/**
 * A geopoint has a longitude and a latitude.
 *
 * @author Oliver Brüntje
 */
public class GeoPoint<T extends Serializable> extends AbstractValueObject {
	private final T latitude;
	private final T longitude;

	/**
	 * Constructor
	 * @param latitude
	 * @param longitude
	 */
	public GeoPoint(T latitude, T longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return String.format("[lat=%1s, long=%2s]", getLatitude(), getLongitude());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GeoPoint geoPoint = (GeoPoint) o;

		if (!latitude.equals(geoPoint.latitude)) return false;
		return longitude.equals(geoPoint.longitude);
	}

	@Override
	protected Object[] values() {
		return new Object[] {this.latitude, this.longitude};
	}

	@Override
	public int hashCode() {
		int result = latitude.hashCode();
		result = 31 * result + longitude.hashCode();
		return result;
	}

	public T getLatitude() {
		return latitude;
	}

	public T getLongitude() {
		return longitude;
	}
}
