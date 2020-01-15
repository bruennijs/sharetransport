from typing import Tuple
from unittest import TestCase

import numpy as np
from pandas import Series, DataFrame
from shapely.geometry import Polygon
from sklearn.neighbors import KNeighborsClassifier

from infrastructure.distance.metric import HarvesineWgs84DistanceMetric
from infrastructure.util import Utils
from test.builder import ClusterBuilder, ClusterPointBuilder


class TestClassificationNearestNeighbors(TestCase):



    def setUp(self) -> None:
        super().setUp()
        self.metric = HarvesineWgs84DistanceMetric()

    def test_two_cluster_distance_calculation_by_classifier(self):
        # GIVEN

        X_train, y_train = self.load_training_data()

        X_train_latlong = Utils.to_2d_array(X_train)

        # WHEN
        classifier = KNeighborsClassifier(n_neighbors=3, weights='distance', p=1, metric=self.calculate_distance)
        classifier.fit(X_train_latlong, y_train)

        # THEN
        X_test = np.array([[43.11, 8.1]])
        print (classifier.predict_proba(X_test))
        print (classifier.kneighbors_graph(X_test))

    def test_prediction_of_point_in_not_in_any_cluster_must_fail(self):
        # GIVEN
        pass

    def calculate_distance(self, a: np.array, b: np.array) -> float:
        return self.metric.pair(a.tolist(), b.tolist())

    def load_training_data(self) -> Tuple[np.array, np.array]:
        cluster1: Polygon = ClusterBuilder().of_cluster(0).build()
        points_cluster1: Series = ClusterPointBuilder().with_polygon(cluster1).count(20).build()
        X_and_Target_1: DataFrame = DataFrame(data={'points': points_cluster1, 'target': np.repeat(1, points_cluster1.shape[0])})

        cluster2: Polygon = ClusterBuilder().of_cluster(1).build()
        points_cluster2: Series = ClusterPointBuilder().with_polygon(cluster2).count(20).build()
        X_and_Target_2: DataFrame = DataFrame(data={'points': points_cluster2, 'target': np.repeat(2, points_cluster2.shape[0])})

        X_and_target: DataFrame = X_and_Target_1.append(X_and_Target_2)
        y: np.array = X_and_target.target.to_numpy()
        return (X_and_target['points'], y)

