import unittest

import numpy as np
import geopandas.geoseries as gs
import os.path
from domain.distance.metric import DefaultWgs84DistanceMetric

class DefaultWgs84DistanceMetricTest(unittest.TestCase):
    def test_two_points_expect_distance_calculated_by_euclidean_metric(self):

        geojsonPath: str = os.path.join(os.path.dirname(__file__), 'files/oldenburg_11_hops.geojson')

        gpSeries: gs.GeoSeries = gs.GeoSeries.from_file(geojsonPath, driver='GeoJSON', crs='WGS84')

        for item in gpSeries:
                print("item {}".format(item))

        distance_matrix: np.array = DefaultWgs84DistanceMetric().pairwise(gpSeries)

        print("{}".format(distance_matrix))
        self.assertEqual(distance_matrix.shape, (gpSeries.shape[0], gpSeries.size))

if __name__ == '__main__':
    unittest.main()
