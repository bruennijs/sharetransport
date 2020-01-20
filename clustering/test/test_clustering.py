import os.path
import unittest

import geopandas.geoseries as gs
import numpy as np
from pandas import Series
from shapely.geometry import Polygon, Point
from sklearn.cluster import AgglomerativeClustering

from infrastructure.distance.metric import HarvesineWgs84DistanceMetric, DistanceMatrixType
from infrastructure.util import Utils


class CluseteringTest(unittest.TestCase):

    GS_POINTS: gs.GeoSeries  # static

    @classmethod
    def setUpClass(cls) -> None:
        super().setUpClass()

        cls.GS_POINTS = cls.load_geojson('files/oldenburg_11_hops.geojson')

        # def test_agglomerativ_clustering_scipy(self):
        #     distance_matrix: np.array = DefaultWgs84DistanceMetric().pairwise(CluseteringTest.GS_POINTS, type=DistanceMatrixType.CONDENSED)
        #
        #     # cluster these points
        #     clusters: np.array = scipy_agglo_complete(distance_matrix)
        #     print(clusters)
        #
        #     # THEN
        #     self.assertEqual(clusters.size, CluseteringTest.GS_POINTS.size)  ## this is still wrong cause returns the clusters of all processing generations

    def test_agglomerativ_clustering_sklearn(self):
        distance_matrix_2d: np.array = HarvesineWgs84DistanceMetric().cartesian_product(CluseteringTest.GS_POINTS, type=DistanceMatrixType.TWO_DIMENSIONAL)

        # cluster these points
        algo: AgglomerativeClustering = AgglomerativeClustering(n_clusters=None,
                                                                linkage='complete',
                                                                affinity='precomputed',
                                                                distance_threshold=500.0)
        # lat_long_2d_array: np.array = Utils.to_2d_array(DefaultWgs84DistanceMetricTest.GS_POINTS)
        clusters: np.array = algo.fit_predict(distance_matrix_2d)
        # THEN
        print(clusters)
        self.assertEqual(clusters.size, CluseteringTest.GS_POINTS.size)
        self.assertSequenceEqual(range(0, 6), np.unique(clusters).tolist())

    def test_create_polygon_from_clustered_points(self):
        cluster_points_2d = Utils.to_2d_array(CluseteringTest.GS_POINTS)
        polygon: Polygon = Polygon(cluster_points_2d)
        points = CluseteringTest.GS_POINTS.to_list()
        series = gs.GeoSeries([polygon] + points, crs='WGS84')
        series.convex_hull.to_file("./clusters_and_points.geojson", driver="GeoJSON")

    def test_create_cluster_with_bufferzones(self):
        center: Point = Point(8.228158950805664, 53.150270721810166)
        series: gs.GeoSeries = gs.GeoSeries(data=[center], crs='WGS84')

        # WHEN
        geometries: Series = series.buffer(500.0)

        # THEN
        self.assertEqual(type(geometries[0]), Polygon)

    def test_create_cluster_with_bufferzones(self):
        center: Point = Point(53.150270721810166, 8.228158950805664)
        series: gs.GeoSeries = gs.GeoSeries(data=[center], crs='WGS84')

        # WHEN
        geometries: Series[Polygon] = series.buffer(5000.0)

        # THEN
        buffer: Polygon = geometries[0]

        in_buffer_point: Point = buffer.representative_point()
        distance: float = HarvesineWgs84DistanceMetric().cartesian_product([center, in_buffer_point])[0]

        # THEN
        # representative point's distance must be lt polygon distance
        self.assertTrue(distance < 500.0)

    def test_create_point_within_convex_hull_polygon(self):
        polygon: Polygon = CluseteringTest.load_geojson('files/cluster_buergeresch.geojson')[0]

        in_cluster_points: gs.Series = gs.Series([polygon.convex_hull.representative_point() for _ in range(2)])

        distance_matrix_2d = HarvesineWgs84DistanceMetric().cartesian_product(in_cluster_points, type=DistanceMatrixType.TWO_DIMENSIONAL)

        # cluster these points
        algo: AgglomerativeClustering = AgglomerativeClustering(n_clusters=None,
                                                                linkage='complete',
                                                                affinity='precomputed',
                                                                distance_threshold=1000.0)

        clusters: np.array = algo.fit_predict(distance_matrix_2d)

        # THEN
        clusters = np.unique(clusters)
        self.assertEqual([0], clusters.tolist())

    @staticmethod
    def load_geojson(fileName: str) -> gs.GeoSeries:
        geoJsonFile: str = os.path.join(os.path.dirname(__file__), fileName)
        return gs.GeoSeries.from_file(geoJsonFile, driver='GeoJSON', crs='WGS84')


if __name__ == '__main__':
    unittest.main()
