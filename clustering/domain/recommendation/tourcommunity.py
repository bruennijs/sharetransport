from abc import ABC, abstractmethod
from functools import reduce
from typing import List, Set, Dict, Iterable

from geopy import Point
from pandas import DataFrame, Series, merge
from pandas.core.groupby import SeriesGroupBy, DataFrameGroupBy
from sklearn.cluster import AgglomerativeClustering

from domain.distance.metric import DefaultWgs84DistanceMetric, DistanceMatrixType
from domain.entities import Trip, Community, Hop
from infrastructure.util import Utils

import numpy as np

TourCommunityId = str

class TourCommunityRecommendation(ABC):
    """
    Interface for a tour recommendation service
    """

    __slots__ = ()

    @abstractmethod
    def recommend(self, trips: Set[Trip]) -> Dict[TourCommunityId, Set[Trip]]:
        pass

    @classmethod
    def __subclasshook__(cls, C):
        # if cls is TourCommunityRecommendation:
        #     return False #_check_methods(C, "recommend")
        return NotImplemented

class ClusteringTourCommunityRecommendation(TourCommunityRecommendation):
    """
    Finds trips in the entire community whose pickup and dropoff locations
    are in the same cluster so they are recommended for a shared transport.
    """

    def __init__(self) -> None:
        super().__init__()
        self.distance: DefaultWgs84DistanceMetric = DefaultWgs84DistanceMetric()
        self.clustering: AgglomerativeClustering = AgglomerativeClustering(n_clusters=None,
                                                  linkage='complete',
                                                  affinity='precomputed',
                                                  distance_threshold=500.0)


    def recommend(self, trips: Iterable[Trip]) -> Dict[TourCommunityId, Set[Trip]]:
        """
        Finds trips in the entire community whose pickup and dropoff locations
        are in the same cluster so they are recommended for a shared transport.
        :param trips: trips to cluster
        :return: List of multiple sets containing trips clustered and recommended for shared transport
        """

        # pointLists: List[List[Point]] = [[trip.pickup, trip.dropoff] for trip in community.trips]

        observations_list: List[List[Point, Hop, Trip]] = Utils.flatmap(trips, lambda trip: [[trip.pickup.location, trip.pickup, trip], [trip.dropoff.location, trip.dropoff, trip]])

        df: DataFrame = DataFrame(data=observations_list, columns=['point', 'hop', 'trip'])
        # df['tripuid'] = df['trip'].apply(lambda trip: trip.uid)

        distance_matrix_2d: np.array = self.distance.pairwise(df['point'], type=DistanceMatrixType.TWO_DIMENSIONAL)

        # cluster these points
        # lat_long_2d_array: np.array = Utils.to_2d_array(DefaultWgs84DistanceMetricTest.GS_POINTS)
        clusters: np.array = self.clustering.fit_predict(distance_matrix_2d)

        # add column with cluster numbers associated to each point
        df = df.assign(cluster_no=clusters)

        group:SeriesGroupBy = df.groupby(['trip']).cluster_no
        tourGroups: Series = group.aggregate(self.reduce_to_tourgroup_no) # index is the trip object

        # dfReindexed: DataFrame = df.set_index('trip')   # set trip to index
        dfTrip = DataFrame(data={'tour_group_no': tourGroups, 'trip': tourGroups.index})

        return dfTrip.groupby('tour_group_no')['trip'].aggregate(set).to_dict()

    def reduce_to_tourgroup_no(self, column: Series) -> int:
        reducedString: int = reduce(lambda agg, newItem: str(agg) + str(newItem), column)
        return reducedString
        # group by trip

# TourCommunityRecommendation.register(ClusteringTourCommunityRecommendation)
