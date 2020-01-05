import unittest

import numpy as np
import geopandas.geoseries as gs
import os.path
from domain.distance.metric import DefaultWgs84DistanceMetric, DistanceMatrixType
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

    def test_agglomerativ_clustering_scipy(self):
        distance_matrix: np.array = DefaultWgs84DistanceMetric().pairwise(DefaultWgs84DistanceMetricTest.GS_POINTS, type=DistanceMatrixType.CONDENSED)

        # cluster these points
        clusters: np.array = scipy_agglo_complete(distance_matrix)
        print(clusters)

        # THEN
        self.assertEqual(clusters.size, DefaultWgs84DistanceMetricTest.GS_POINTS.size)  ## this is still wrong cause returns the clusters of all processing generations

    def test_agglomerativ_clustering_sklearn(self):
        distance_matrix_2d: np.array = DefaultWgs84DistanceMetric().pairwise(DefaultWgs84DistanceMetricTest.GS_POINTS, type=DistanceMatrixType.TWO_DIMENSIONAL)

        # cluster these points
        algo: AgglomerativeClustering = AgglomerativeClustering(n_clusters=None,
                                                                linkage='complete',
                                                                affinity='precomputed',
                                                                distance_threshold=1000.0)
        # lat_long_2d_array: np.array = Utils.to_2d_array(DefaultWgs84DistanceMetricTest.GS_POINTS)
        clusters = algo.fit_predict(distance_matrix_2d)
        # THEN
        print(clusters)
        self.assertEqual(clusters.size, DefaultWgs84DistanceMetricTest.GS_POINTS.size)


if __name__ == '__main__':
    unittest.main()
