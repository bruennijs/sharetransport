from itertools import groupby
from typing import List, Set

from geopandas import GeoSeries
from geopy import Point
from pandas import DataFrame, Series
from sklearn.cluster import AgglomerativeClustering

from domain.distance.metric import DefaultWgs84DistanceMetric, DistanceMatrixType
from domain.entities import Trip, Community, Hop
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

    def recommendTourCommunities(self, trips: List[Trip]) -> List[Set[Trip]]:
        """
        Finds trips in the entire community whose pickup and dropoff locations
        are in the same cluster so they are recommended for a shared transport.
        :param trips: trips to cluster
        :return: List of multiple sets containing trips clustered and recommended for shared transport
        """

        # pointLists: List[List[Point]] = [[trip.pickup, trip.dropoff] for trip in community.trips]

        observations_list: List[List[Point, Hop, Trip]] = Utils.flatmap(trips, lambda trip: [[trip.pickup.location, trip.pickup, trip], [trip.dropoff.location, trip.dropoff, trip]])

        df: DataFrame = DataFrame(data=observations_list, columns=['point', 'hop', 'trip'])

        distance_matrix_2d: np.array = self.distance.pairwise(df['point'], type=DistanceMatrixType.TWO_DIMENSIONAL)

        # cluster these points
        # lat_long_2d_array: np.array = Utils.to_2d_array(DefaultWgs84DistanceMetricTest.GS_POINTS)
        clusters: np.array = self.clustering.fit_predict(distance_matrix_2d)

        # add column with cluster numbers associated to each point
        df2 = df.assign(cluster_no=clusters)


        return df2

        # group by trip


