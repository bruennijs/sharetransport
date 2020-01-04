from typing import List

from scipy.spatial.distance import pdist
import numpy as np
import geopandas as gp
from shapely.geometry import Point as sl_Point
from geopy.distance import geodesic, Point as gp_Point

from infrastructure.util import Utils


class DefaultWgs84DistanceMetric(object):
    def __init__(self):
        return

    def pairwise(self, x: gp.GeoSeries) -> np.array:
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
        lat_long_2d_array: np.array = Utils.to_2d_array(x)

        # https://docs.scipy.org/doc/scipy/reference/generated/scipy.spatial.distance.pdist.html#scipy.spatial.distance.pdist
        return pdist(lat_long_2d_array, metric=lambda u, v:
            geodesic(DefaultWgs84DistanceMetric.to_geopy_point(u),
                               DefaultWgs84DistanceMetric.to_geopy_point(v)).meters)

    @classmethod
    def to_geopy_point(cls, point: List[float]) -> gp_Point:
            return gp_Point(point[0], point[1]) #  [0]=lat / [1]=long
