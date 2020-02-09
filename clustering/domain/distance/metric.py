from itertools import product
from typing import Iterable, Tuple, Sequence, List

import numpy as np
from pandas import DataFrame, Series
from scipy.spatial.distance import squareform
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
        self._regressor = RadiusNeighborsRegressor(radius=100000.0, weights='distance', metric=self.trip_distance)
        self._harvesine = HarvesineWgs84DistanceMetric()

    def cartesian_product(self, x: Iterable[Point], type: DistanceMatrixType = DistanceMatrixType.VECTOR) -> np.array:

        points_product: List[Tuple[Point, Point]] = list(product(x, x))

        df_points: DataFrame = DataFrame.from_records(points_product)

        X_test_latlong: DataFrame = df_points.apply(axis=1, func=self.convert_points_to_2d_flattened, result_type='expand')

        # product_points.apply(lambda row: Utils.to_lat_long_2d_point(row[0]), )

        prediction_vector: np.array = self._regressor.predict(X_test_latlong.to_numpy())
        if type is DistanceMatrixType.MATRIX:
            return squareform(prediction_vector, 'tomatrix', True)
        else:
            return prediction_vector

    def pair(self, a: Sequence[float], b: Sequence[float]) -> float:
        raise NotImplementedError

    def train_model(self, X: DataFrame, y: Series):
        """

        :param X: columns must contain shapely.Point for pickup & dropoff points in WGS84 lat-long coordinate system
        :param y: Target is distance for each pickup & dropoff point tuple
        :return: void
        """

        X_latlong: DataFrame = X.apply(axis=1, func=self.convert_points_to_2d_flattened, result_type='expand')

        self._regressor.fit(X_latlong.to_numpy(dtype=float), y.to_numpy(dtype=float))

    def score(self, X: DataFrame, y_true: Series):
        """
            Calculates score depending on regressor.
        :param X: columns must contain shapely.Point for pickup & dropoff points in WGS84 lat-long coordinate system
        :param y_true: Target is the true distance for each pickup & dropoff point tuple
        :return: void
        """

        X_latlong: DataFrame = X.apply(axis=1, func=self.convert_points_to_2d_flattened, result_type='expand')

        return self._regressor.score(X_latlong.to_numpy(dtype=float), y_true.to_numpy(dtype=float))

    def trip_distance(self, coords_a: np.array, coords_b: np.array) -> float:
        return self._harvesine.pair(coords_a[0:2], coords_b[0:2]) + self._harvesine.pair(coords_a[2:], coords_b[2:])

    def convert_points_to_2d_flattened(self, points: Series) -> List[float]:
        return Utils.to_2d_array(points).reshape(1, -1).flatten().tolist()


