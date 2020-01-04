from typing import List, Callable

from shapely.geometry import Point
import numpy as np

class Utils(object):
    @staticmethod
    def to_2d_array(series: List[Point], itemCreator: Callable = None) -> np.array:
        """

        Parameters
        ----------
        x : GeoSeries
            A series of shapely.Point instances
        :returns
        numpy array with m samples (same count as points in arg) with column 0 = latitide, column 1 = longitude
        """
        x_points: List[Point] = list(filter(lambda geometry: isinstance(geometry, Point), series))

        # convert list of shapely Point to list of tuples with long/lat
        # x_points_2d: List[List[float]] = list(map(lambda point: DefaultWgs84DistanceMetric., x_points))

        lat_long_2d_array: np.array = np.array([Utils.to_lat_long_2d_point(p) for p in x_points])

        return lat_long_2d_array

    @staticmethod
    def to_lat_long_2d_point(point: Point) -> List[float]:
        return [point.y, point.x] # x=long / y=lat

