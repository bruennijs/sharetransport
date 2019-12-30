package sharetransport.infrastructure.domain.geo;

/**
 * Geo point with long/lat.
 *
 * @author Oliver Brüntje
 */
public class GeoPointDouble extends GeoPoint<Double> {
    /**
     * Constructor
     *
     * @param latitude
     * @param longitude
     */
    public GeoPointDouble(Double latitude, Double longitude) {
        super(latitude, longitude);
    }
}
