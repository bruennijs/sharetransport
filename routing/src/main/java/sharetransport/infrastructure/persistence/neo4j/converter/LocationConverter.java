package sharetransport.infrastructure.persistence.neo4j.converter;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.typeconversion.CompositeAttributeConverter;

import sharetransport.domain.routing.Hop;
import sharetransport.infrastructure.domain.geo.Location;

/**
 * This class maps latitude and longitude properties onto a Location type that encapsulates both of these attributes.
 *
 * @author Oliver Brüntje
 */
public class LocationConverter implements CompositeAttributeConverter<Location> {

  @Override
  public Map<String, ?> toGraphProperties(Location location) {
    Map<String, Double> properties = new HashMap<>();
    if (location != null)  {
      properties.put(Hop.PROPERTY_LATITUDE, location.getLatitude());
      properties.put(Hop.PROPERTY_LONGITUDE, location.getLongitude());
    }
    return properties;
  }

  @Override
  public Location toEntityAttribute(Map<String, ?> map) {
    Double latitude = (Double) map.get(Hop.PROPERTY_LATITUDE);
    Double longitude = (Double) map.get(Hop.PROPERTY_LONGITUDE);
    if (latitude != null && longitude != null) {
      return new Location(latitude, longitude);
    }
    return null;
  }

}
