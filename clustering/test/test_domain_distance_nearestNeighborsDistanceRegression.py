from unittest import TestCase

import numpy as np
from pandas import Series, DataFrame
from shapely.geometry import Polygon, Point

from domain.distance.metric import NearestNeighborsDistanceRegression
from infrastructure.distance.metric import HarvesineWgs84DistanceMetric, DistanceMetric
from test.builder import ClusterBuilder, ClusterPointBuilder


class TestNearestNeighborsDistanceRegression(TestCase):

    HARVESINE: DistanceMetric

    @classmethod
    def setUpClass(cls) -> None:
        super().setUpClass()
        cls.HARVESINE = HarvesineWgs84DistanceMetric()

    def test_predict_(self):
        cluster1: Polygon = ClusterBuilder().of_cluster(0).build()
        points_cluster1: Series = ClusterPointBuilder().with_polygon(cluster1).count(3).build()

        cluster2: Polygon = ClusterBuilder().of_cluster(5).build()
        points_cluster2: Series = ClusterPointBuilder().with_polygon(cluster2).count(3).build()

        X_train: DataFrame = DataFrame(data={'point_a': points_cluster1, 'point_b': points_cluster2})
        y = X_train.apply(axis=1, func=lambda row: TestNearestNeighborsDistanceRegression.HARVESINE.cartesian_product(row))

        sut  = NearestNeighborsDistanceRegression()
        sut.train_model(X_train, y)
