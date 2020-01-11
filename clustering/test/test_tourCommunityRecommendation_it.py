import os
from typing import Set, Dict
from unittest import TestCase

from geopandas import GeoSeries
from shapely.geometry import Point

from domain.entities import Hop, Trip
from domain.recommendation.tourcommunity import ClusteringTourCommunityRecommendation, TourCommunityRecommendation, TourCommunityId
from test.builder import TripBuilder


class TestTourCommunityRecommendation(TestCase):

    def test_recommendWhenTwoTripInCluster(self):
        # GIVEN

        trip1: Trip = TripBuilder().pickupCluster(1).dropoffCluster(2).build()
        trip2: Trip = TripBuilder().pickupCluster(1).dropoffCluster(2).build()

        sut = ClusteringTourCommunityRecommendation()
        tripsGroupedByTourComunity: Dict[TourCommunityId, Set[Trip]] = sut.recommend(trips=[trip1, trip2])

        # THEN
        assert len(tripsGroupedByTourComunity) == 1
        keys_0 = list(tripsGroupedByTourComunity.keys())[0]
        assert tripsGroupedByTourComunity.get(keys_0) == set([trip2, trip1])

    def test_recommendWhenThrreTripsWithTwoTourGroups(self):
        # GIVEN

        trip1: Trip = TripBuilder().pickupCluster(1).dropoffCluster(2).build()
        trip2: Trip = TripBuilder().pickupCluster(1).dropoffCluster(2).build()
        trip3: Trip = TripBuilder().pickupCluster(2).dropoffCluster(1).build()
        trip4: Trip = TripBuilder().pickupCluster(2).dropoffCluster(1).build()

        sut = ClusteringTourCommunityRecommendation()
        tripsGroupedByTourComunity: Dict[TourCommunityId, Set[Trip]] = sut.recommend(trips=[trip1, trip2, trip3, trip4])

        # THEN
        assert len(tripsGroupedByTourComunity) == 2
        keys_0 = list(tripsGroupedByTourComunity.keys())[0]
        keys_1 = list(tripsGroupedByTourComunity.keys())[1]
        self.assertEqual(tripsGroupedByTourComunity.get(keys_0), set([trip2, trip1]))
        self.assertEqual(tripsGroupedByTourComunity.get(keys_1), set([trip3, trip4]))

    def test_subclass(self):
        assert issubclass(ClusteringTourCommunityRecommendation, TourCommunityRecommendation)

    def test_instance(self):
        assert isinstance(ClusteringTourCommunityRecommendation(), TourCommunityRecommendation)



