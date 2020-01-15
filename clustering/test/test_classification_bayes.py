from typing import List
from unittest import TestCase

from pandas import Series, DataFrame
from shapely.geometry import Polygon, Point
from shapely.ops import triangulate
from sklearn.naive_bayes import GaussianNB

from infrastructure.util import Utils
from test.builder import ClusterBuilder, ClusterPointBuilder
import numpy as np


class TestClassificationBayes(TestCase):

    def test_two_cluster_with_form_of_circle(self):
        # GIVEN

        cluster1: Polygon = ClusterBuilder().of_cluster(0).build()
        points_cluster1: Series = ClusterPointBuilder().with_polygon(cluster1).count(20).build()
        X_and_Target_1: DataFrame = DataFrame(data={'points': points_cluster1, 'target': np.repeat(1, points_cluster1.shape[0])})

        cluster2: Polygon = ClusterBuilder().of_cluster(2).build()
        points_cluster2: Series = ClusterPointBuilder().with_polygon(cluster2).count(20).build()
        X_and_Target_2: DataFrame = DataFrame(data={'points': points_cluster2, 'target': np.repeat(2, points_cluster2.shape[0])})

        X_and_target: DataFrame = X_and_Target_1.append(X_and_Target_2)

        X_train: np.array = Utils.to_2d_array(X_and_target.points)
        y: np.array = X_and_target.target.to_numpy()

        # WHEN
        gnb = GaussianNB()
        gnb.fit(X_train, y)
        print (gnb.predict_proba(np.array([[53.11, 8.1]])))
        # print (gnb.predict(Utils.to_2d_array([Point(55.11, 6.1)])))

        # THEN

    def test_prediction_of_point_in_different_cluster_must_fail(self):
        # GIVEN

        cluster1: Polygon = ClusterBuilder().of_cluster(1).build()
        cluster2: Polygon = ClusterBuilder().of_cluster(4).build()
        triangle_polygons: List[Polygon] = triangulate(cluster1)
        X: np.array = Utils.to_2d_array([triangle.representative_point() for _ in range(1, 4) for triangle in triangle_polygons])

        X_test: np.array = Utils.to_2d_array([cluster2.representative_point()])

        # WHEN
        gnb = GaussianNB()
        gnb.fit(X, np.repeat(1, X.shape[0]))

        # THEN
        print (gnb.predict_proba(X_test))

    def test_filtering_series(self):
        series = Series([5, 4, 3])

        mapped:Series = series[series.apply(lambda x: x > 3)]

        assert set([5, 4]) == set(mapped.tolist())


