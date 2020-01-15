from abc import ABCMeta, abstractmethod, ABC
from enum import Enum
from typing import Iterable, Sequence

import numpy as np
from geopy.distance import geodesic, Point as gp_Point
from scipy.spatial.distance import pdist, squareform
from shapely.geometry import Point

from infrastructure.util import Utils


class DistanceMatrixType(Enum):
    CONDENSED = 1,
    TWO_DIMENSIONAL = 2


class DistanceMetric(ABC):
    @abstractmethod
    def pairwise(self, x: Iterable[Point], type: DistanceMatrixType = DistanceMatrixType.CONDENSED) -> np.array:
        """
        Compute distance between each pair of the geoseries object. it filters only shapely.Point instances for calculation.
        The result is a sparse distance  matrix. See https://docs.scipy.org/doc/scipy/reference/generated/scipy.spatial.distance.pdist.html#scipy.spatial.distance.pdist
        for details.
        Thr used distance matrix is the harvesine algorithm.



        Parameters
        ----------
        x : GeoSeries
            A series of shapely.Point instances
        """
        pass

    @abstractmethod
    def pair(self, a, b):
        """
        One distance from a -> b
        :param points: tuple of points
        :return: Distance in meters
        """
        pass


class HarvesineWgs84DistanceMetric(DistanceMetric):
    def __init__(self):
        return

    def pairwise(self, x: Iterable[Point], type: DistanceMatrixType = DistanceMatrixType.CONDENSED) -> np.array:
        """
        Compute distance between each pair of the geoseries object. it filters only shapely.Point instances for calculation.
        The result is a sparse distance  matrix. See https://docs.scipy.org/doc/scipy/reference/generated/scipy.spatial.distance.pdist.html#scipy.spatial.distance.pdist
        for details.
        Thr used distance matrix is the harvesine algorithm.

        Parameters
        ----------
        x : iterable containing all points to calculate distances pairwise
            A series of shapely.Point instances
        """
        lat_long_2d_array: np.array = Utils.to_2d_array(x)

        # https://docs.scipy.org/doc/scipy/reference/generated/scipy.spatial.distance.pdist.html#scipy.spatial.distance.pdist
        condensed_distance_matrix: np.array = pdist(lat_long_2d_array, metric=self.pair)

        if type == DistanceMatrixType.TWO_DIMENSIONAL:
            return squareform(condensed_distance_matrix, 'tomatrix', True)
        else:
            return condensed_distance_matrix

    def pair(self, a: Sequence[float], b: Sequence[float]) -> float:
        """
        One distance from a -> b
        :param points: tuple of points
        :return: Distance in meters
        """
        return geodesic(HarvesineWgs84DistanceMetric.to_geopy_point(a),
                        HarvesineWgs84DistanceMetric.to_geopy_point(b)).meters


    @classmethod
    def to_geopy_point(cls, point: Sequence[float]) -> gp_Point:
            return gp_Point(point[0], point[1]) #  [0]=lat / [1]=long
