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

        trip1: Trip = TripBuilder().pickupCluster(2).dropoffCluster(5).build()
        trip2: Trip = TripBuilder().pickupCluster(2).dropoffCluster(5).build()

        sut = ClusteringTourCommunityRecommendation()
        tripsGroupedByTourComunity: Dict[TourCommunityId, Set[Trip]] = sut.recommend(trips=[trip1, trip2])

        # THEN
        self.assertEqual(1, len(tripsGroupedByTourComunity))
        keys_0 = list(tripsGroupedByTourComunity.keys())[0]
        assert tripsGroupedByTourComunity[keys_0] == set([trip2, trip1])

    def test_recommendWhenThreeTripsWithTwoTourGroups(self):
        # GIVEN

        trip1: Trip = TripBuilder().pickupCluster(2).dropoffCluster(5).build()
        trip2: Trip = TripBuilder().pickupCluster(2).dropoffCluster(5).build()
        trip3: Trip = TripBuilder().pickupCluster(5).dropoffCluster(2).build()
        trip4: Trip = TripBuilder().pickupCluster(5).dropoffCluster(2).build()

        sut = ClusteringTourCommunityRecommendation()
        tripsGroupedByTourComunity: Dict[TourCommunityId, Set[Trip]] = sut.recommend(trips=[trip1, trip2, trip3, trip4])

        # THEN
        self.assertEqual(2, len(tripsGroupedByTourComunity))
        keys_0 = list(tripsGroupedByTourComunity.keys())[0]
        keys_1 = list(tripsGroupedByTourComunity.keys())[1]
        self.assertEqual(tripsGroupedByTourComunity[keys_0], set([trip1, trip2]))
        self.assertEqual(tripsGroupedByTourComunity[keys_1], set([trip3, trip4]))

    def test_subclass(self):
        assert issubclass(ClusteringTourCommunityRecommendation, TourCommunityRecommendation)

    def test_instance(self):
        assert isinstance(ClusteringTourCommunityRecommendation(), TourCommunityRecommendation)



