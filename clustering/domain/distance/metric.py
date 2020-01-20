from itertools import product
from typing import Iterable, Tuple, Sequence, List

import numpy as np
from pandas import DataFrame, Series
from shapely.geometry import Point
from sklearn.neighbors import RadiusNeighborsRegressor

from infrastructure.distance.metric import DistanceMetric, DistanceMatrixType, HarvesineWgs84DistanceMetric
from infrastructure.util import Utils


class NearestNeighborsDistanceRegression(DistanceMetric):
    """
    Uses KNN regression to predict distances from a learned model learned from previous trips.
    Takes only query points within a given radius of the pickup
    """
    def __init__(self) -> None:
        super().__init__()
        self._regressor = RadiusNeighborsRegressor(radius=200.0, weights='distance', metric=self.trip_distance)
        self._harvesine = HarvesineWgs84DistanceMetric()

    def cartesian_product(self, x: Iterable[Point], type: DistanceMatrixType = DistanceMatrixType.CONDENSED) -> np.array:

        product_points: DataFrame = DataFrame(product(x, x))

        # product_points.apply(lambda row: Utils.to_lat_long_2d_point(row[0]), )

        self._regressor.predict(np.array(product_points))

    def pair(self, a: Sequence[float], b: Sequence[float]) -> float:
        raise NotImplementedError

    def train_model(self, X: DataFrame, y: Series):
        """

        :param X: columns must contain shapely.Point for origin and destination points in WGS84 lat-long coordinate system
        :param y: Target is distance
        :return:
        """

        X_latlong: DataFrame = X.apply(axis=1, func=self.convert_point_row, result_type='expand')

        self._regressor.fit(X_latlong.to_numpy(dtype=float), y.to_numpy(dtype=float))

    def trip_distance(self, coords_a: Sequence[float], coords_b: Sequence[float]) -> float:
        return self._harvesine.pair(coords_a, coords_b)

    def convert_point_row(self, row) -> List[float]:
        return Utils.to_2d_array(row).reshape(1, -1).flatten().tolist()



