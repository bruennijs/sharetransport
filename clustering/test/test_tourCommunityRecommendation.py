import os
from unittest import TestCase

from geopandas import GeoSeries
from mockito import when, mock
from shapely.geometry import Point

from domain.entities import Community, Hop, Trip
from domain.recommendation.tourcommunity import TourCommunityRecommendation


class TestTourCommunityRecommendation(TestCase):

    @classmethod
    def setUpClass(cls) -> None:
        super().setUpClass()
        geojsonPath: str = os.path.join(os.path.dirname(__file__), 'files/oldenburg_11_hops.geojson')
        cls.GS_POINTS = GeoSeries.from_file(geojsonPath, driver='GeoJSON', crs='WGS84')

    def test_recommendWhenTwoTripInCluster(self):
        # GIVEN

        point1Pickup = Point(1.0, 2.0)
        point1Dropoff = Point(3.0, 4.0)
        hop1Pickup = Hop('hop1Pickup', point1Pickup)
        hop1Dropoff = Hop('hop1Dropoff', point1Dropoff)

        point2Pickup = Point(10.0, 11.0)
        point2Dropoff = Point(12.0, 13.0)
        hop2Pickup = Hop('hop2Pickup', point2Pickup)
        hop2Dropoff = Hop('hop2Dropoff', point2Dropoff)

        trip1 = Trip(hop1Pickup, hop1Dropoff)
        trip2 = Trip(hop2Pickup, hop2Dropoff)

        sut = TourCommunityRecommendation()
        sut.recommendTourCommunities(trips=[trip1, trip2])

        # THEN



