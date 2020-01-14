from typing import List
from unittest import TestCase

from pandas import Series
from shapely.geometry import Polygon, Point
from shapely.ops import triangulate
from sklearn.naive_bayes import GaussianNB

from infrastructure.util import Utils
from test.builder import ClusterBuilder
import numpy as np


class TestTourCommunityRecommendation(TestCase):

    def test_cluster_with_form_of_circle(self):
        # GIVEN

        polygon: Polygon = ClusterBuilder().of_cluster(2).build()
        triangle_polygons: List[Polygon] = triangulate(polygon)
        X: np.array = Utils.to_2d_array([triangle.representative_point() for _ in range(1, 4) for triangle in triangle_polygons])

        X_test: np.array = Utils.to_2d_array([triangle.representative_point() for _ in range(1, 2) for triangle in triangle_polygons])

        # WHEN
        gnb = GaussianNB()
        gnb.fit(X, np.repeat(1, X.shape[0]))
        print (gnb.predict_proba(X_test))

        # THEN

    def test_cluster_45degree_cluster_where_width_largerthan_height(self):
        # GIVEN

        polygon: Polygon = ClusterBuilder().of_cluster(5).build()
        triangle_polygons: List[Polygon] = triangulate(polygon)
        X: np.array = Utils.to_2d_array([triangle.representative_point() for _ in range(1, 4) for triangle in triangle_polygons])

        X_test: np.array = Utils.to_2d_array([triangle.representative_point() for _ in range(1, 2) for triangle in triangle_polygons])

        # WHEN
        gnb = GaussianNB()
        gnb.fit(X, np.repeat(1, X.shape[0]))
        print (gnb.predict_proba(X_test))

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


