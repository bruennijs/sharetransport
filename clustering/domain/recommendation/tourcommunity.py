from abc import ABC, abstractmethod
from functools import reduce
from typing import List, Set, Dict, Iterable

import numpy as np
from geopandas import GeoDataFrame
from geopy import Point
from pandas import DataFrame, Series
from pandas.core.groupby import SeriesGroupBy
from sklearn.cluster import AgglomerativeClustering

from domain.clustering import HopClusterService
from domain.entities import Trip, Hop
from infrastructure.distance.metric import HarvesineWgs84DistanceMetric, DistanceMatrixType
from infrastructure.util import Utils

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

    def __init__(self, clustering = None) -> None:
        """

        :param clustering: clustering service to use for calculating cluster numbers; if none default one will be taken
        """
        super().__init__()
        if clustering is not None:
            self.clustering_service = clustering
        else:
            self.clustering_service = HopClusterService()


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

        cluster_no: Series = self.clustering_service.cluster(points=df['point'])

        # add column with cluster numbers associated to each point
        df['cluster_no'] = cluster_no

        GeoDataFrame(geometry=df['point']).to_file("./points.geojson", driver="GeoJSON")

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
