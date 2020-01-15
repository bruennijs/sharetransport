from typing import Iterable

from pandas import Series
from shapely.geometry import Point
from sklearn.cluster import AgglomerativeClustering
import numpy as np

from infrastructure.distance.metric import HarvesineWgs84DistanceMetric, DistanceMatrixType


class HopClusterService(object):
    """
    Calculates cluster numbers by usingf agglomerative clustering and using a abstract distance metric.
    The threshold for the agglomerative algorithm is fixed and can be changed as parameter.
    """
    def __init__(self, distance_threshold: float = 1500.0) -> None:
        """
        Constructor
        :param distance_threshold: depends on the unit of the distance algorithm, e.g. meters or minutes.
        """
        super().__init__()
        self.distance: HarvesineWgs84DistanceMetric = HarvesineWgs84DistanceMetric()
        self.clustering: AgglomerativeClustering = AgglomerativeClustering(n_clusters=None,
                                                                       linkage='complete',
                                                                       affinity='precomputed',
                                                                       distance_threshold=distance_threshold)


    def cluster(self, points: Iterable[Point]) -> Series:
        """
        Calculates cluster numbers for each point.
        :param points: points to calculate cluster for
        :return: Series cluster no as data. Index maps to correspoding index in points iterable
        """
        distance_matrix_2d: np.array = self.distance.pairwise(points, type=DistanceMatrixType.TWO_DIMENSIONAL)

        # cluster these points
        # lat_long_2d_array: np.array = Utils.to_2d_array(DefaultWgs84DistanceMetricTest.GS_POINTS)
        clusters: np.array = self.clustering.fit_predict(distance_matrix_2d)

        return Series(data=clusters)
