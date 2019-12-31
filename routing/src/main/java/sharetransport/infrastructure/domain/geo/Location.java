package sharetransport.infrastructure.domain.geo;

/**
 * Geo point with long/lat.
 *
 * @author Oliver Brüntje
 */
public class Location extends AbstractLocation<Double> {
    /**
     * Constructor
     *
     * @param latitude
     * @param longitude
     */
    public Location(Double latitude, Double longitude) {
        super(latitude, longitude);
    }
}
