from scipy.spatial.distance import cdist
import numpy as np
import geopandas as gp
from shapely.geometry import Point
from geopy.distance import geodesic

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
        x_points: list = list(filter(lambda geometry: isinstance(geometry, Point), x))

        # convert list of shapely Point to list of tuples with long/lat
        points_ = [self.point_to_list(point) for point in x_points]
        x_longlat_array: np.array = np.array(points_)

        # https://docs.scipy.org/doc/scipy/reference/generated/scipy.spatial.distance.pdist.html#scipy.spatial.distance.pdist
        return cdist(x_longlat_array, x_longlat_array, metric=lambda u, v: geodesic(u, v).meters)


    def point_to_list(self, point: Point) -> list:
        return [point.y, point.x] # long/lat
