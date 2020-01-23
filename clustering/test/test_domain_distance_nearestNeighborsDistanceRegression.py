from unittest import TestCase

import numpy as np
from pandas import Series, DataFrame
from shapely.geometry import Polygon

from domain.distance.metric import NearestNeighborsDistanceRegression
from infrastructure.distance.metric import HarvesineWgs84DistanceMetric, DistanceMetric, DistanceMatrixType
from test.builder import ClusterBuilder, ClusterPointBuilder


class TestNearestNeighborsDistanceRegression(TestCase):

    HARVESINE: DistanceMetric

    @classmethod
    def setUpClass(cls) -> None:
        super().setUpClass()
        cls.HARVESINE = HarvesineWgs84DistanceMetric()

    def test_predict_distance_from_two_clusters(self):
        cluster1: Polygon = ClusterBuilder().of_cluster(0).build()
        X_points_cluster1: Series = ClusterPointBuilder().with_polygon(cluster1).count(3).build()
        X_test_points_cluster1: Series = ClusterPointBuilder().with_polygon(cluster1).count(1).build()

        cluster2: Polygon = ClusterBuilder().of_cluster(5).build()
        X_points_cluster2: Series = ClusterPointBuilder().with_polygon(cluster2).count(3).build()
        X_test_points_cluster2: Series = ClusterPointBuilder().with_polygon(cluster2).count(1).build()

        X_train: DataFrame = DataFrame(data={'point_a': X_points_cluster1, 'point_b': X_points_cluster2})
        y = X_train.apply(axis=1, func=lambda row: TestNearestNeighborsDistanceRegression.HARVESINE.cartesian_product(row))

        sut  = NearestNeighborsDistanceRegression()
        sut.train_model(X_train, y)

        # WHEN
        distance_matrix_: np.array = sut.cartesian_product(x=X_test_points_cluster1.append(X_test_points_cluster2), type=DistanceMatrixType.MATRIX)

        # THEN
        self.assertEqual((2, 2), distance_matrix_.shape)

    def test_nparray_slice_1d(self):
        array = np.array([1, 2, 3, 4])
        array_slice = array[0:2]
        array_slice_2 = array[2:]
        self.assertEqual(np.array([1,2]).tolist(), array_slice.tolist())
        self.assertEqual(np.array([3,4]).tolist(), array_slice_2.tolist())
