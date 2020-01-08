from functools import reduce
from typing import List, Callable, Sequence, TypeVar, Iterator, Iterable

from shapely.geometry import Point
import numpy as np


T = TypeVar('T')
S = TypeVar('S')

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

    @staticmethod
    def flatten(list: List[List[T]]) -> List[T]:
        return reduce(lambda acc, item: acc + item, list, [])

    @classmethod
    def flatmap(cls, mappingList: List[S], flatMapFunc: Callable) -> List[T]:
        return list(cls._flatmap(mappingList, flatMapFunc))

    @staticmethod
    def _flatmap(list: List[T], flatMapFunc: Callable) -> Iterator[S]:
        for item in list:
            mapped = flatMapFunc(item)
            if isinstance(mapped, Sequence):
                for m in mapped:
                    yield m
            else:
                yield mapped



