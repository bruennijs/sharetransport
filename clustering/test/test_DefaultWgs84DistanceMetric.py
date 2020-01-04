import unittest

import numpy as np
import geopandas.geoseries as gs
import os.path
from domain.distance.metric import DefaultWgs84DistanceMetric
from scipy.cluster.hierarchy import complete as scipy_agglo_complete
from sklearn.cluster import AgglomerativeClustering

from infrastructure.util import Utils


class DefaultWgs84DistanceMetricTest(unittest.TestCase):

    GS_POINTS: gs.GeoSeries  # static

    @classmethod
    def setUpClass(cls) -> None:
        super().setUpClass()
        geojsonPath: str = os.path.join(os.path.dirname(__file__), 'files/oldenburg_11_hops.geojson')
        cls.GS_POINTS = gs.GeoSeries.from_file(geojsonPath, driver='GeoJSON', crs='WGS84')

    def test_harvesine_metric(self):

        for item in DefaultWgs84DistanceMetricTest.GS_POINTS:
                print("item {}".format(item))

        distance_matrix: np.array = DefaultWgs84DistanceMetric().pairwise(DefaultWgs84DistanceMetricTest.GS_POINTS)

        print("{}".format(distance_matrix))

        # THEN
        # Expect sparce condensed matrix only upper part of distance matrix returned as single array
        countOfPoints = DefaultWgs84DistanceMetricTest.GS_POINTS.size
        countOfDistances: int = ((countOfPoints * countOfPoints - countOfPoints) / 2)
        self.assertEqual(distance_matrix.shape[0], countOfDistances)

    def test_agglomerativ_clustering_scipy(self):
        distance_matrix: np.array = DefaultWgs84DistanceMetric().pairwise(DefaultWgs84DistanceMetricTest.GS_POINTS)

        # cluster these points
        clusters: np.array = scipy_agglo_complete(distance_matrix)
        print(clusters)

        # THEN
        self.assertEqual(clusters.size, DefaultWgs84DistanceMetricTest.GS_POINTS.size)

    def test_agglomerativ_clustering_sklearn(self):
        distance_matrix: np.array = DefaultWgs84DistanceMetric().pairwise(DefaultWgs84DistanceMetricTest.GS_POINTS)

        # cluster these points
        algo: AgglomerativeClustering = AgglomerativeClustering(n_clusters=None,
                                                                linkage='complete',
                                                                affinity=distance_matrix,
                                                                distance_threshold=200.0)
        lat_long_2d_array = Utils.to_2d_array(DefaultWgs84DistanceMetricTest.GS_POINTS)
        clusters = algo.fit_predict(lat_long_2d_array)
        # THEN
        print(clusters)
        self.assertEqual(clusters.size, DefaultWgs84DistanceMetricTest.GS_POINTS.size)


if __name__ == '__main__':
    unittest.main()
