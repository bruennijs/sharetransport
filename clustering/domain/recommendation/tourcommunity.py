from typing import List, Set

from geopandas import GeoSeries
from geopy import Point
from sklearn.cluster import AgglomerativeClustering

from domain.distance.metric import DefaultWgs84DistanceMetric, DistanceMatrixType
from domain.entities import Trip, Community
from infrastructure.util import Utils

import numpy as np

class TourCommunityRecommendation(object):



    def __init__(self) -> None:
        super().__init__()
        self.distance: DefaultWgs84DistanceMetric = DefaultWgs84DistanceMetric()
        self.clustering: AgglomerativeClustering = AgglomerativeClustering(n_clusters=None,
                                                  linkage='complete',
                                                  affinity='precomputed',
                                                  distance_threshold=500.0)

    def recommendTourCommunities(self, community: Community) -> List[Set[Trip]]:
        """
        Finds trips in the entire community whose pickup and dropoff locations
        are in the same cluster so they are recommended for a shared transport.
        :param community: community to cluster and find sets of trips
        :return: List of multiple sets containing trips clustered and recommended for shared transport
        """

        # pointLists: List[List[Point]] = [[trip.pickup, trip.dropoff] for trip in community.trips]
        points: List[Point] = Utils.flatmap(community.trips, lambda trip: (trip.pickup, trip.dropoff))

        gsPoints: GeoSeries = GeoSeries(points)

        distance_matrix_2d: np.array = self.distance.pairwise(gsPoints, type=DistanceMatrixType.TWO_DIMENSIONAL)

        # cluster these points
        # lat_long_2d_array: np.array = Utils.to_2d_array(DefaultWgs84DistanceMetricTest.GS_POINTS)
        clusters: np.array = self.clustering.fit_predict(distance_matrix_2d)
