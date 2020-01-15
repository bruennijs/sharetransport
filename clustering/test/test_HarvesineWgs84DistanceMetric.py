import os.path
import unittest

import geopandas.geoseries as gs
import numpy as np

from infrastructure.distance.metric import HarvesineWgs84DistanceMetric


class HarvesineWgs84DistanceMetricTest(unittest.TestCase):

    GS_POINTS: gs.GeoSeries  # static

    @classmethod
    def setUpClass(cls) -> None:
        super().setUpClass()
        geojsonPath: str = os.path.join(os.path.dirname(__file__), 'files/oldenburg_11_hops.geojson')
        cls.GS_POINTS = gs.GeoSeries.from_file(geojsonPath, driver='GeoJSON', crs='WGS84')

    def test_harvesine_metric(self):

        for item in HarvesineWgs84DistanceMetricTest.GS_POINTS:
                print("item {}".format(item))

        distance_matrix: np.array = HarvesineWgs84DistanceMetric().pairwise(HarvesineWgs84DistanceMetricTest.GS_POINTS)

        print("{}".format(distance_matrix))

        # THEN
        # Expect sparce condensed matrix only upper part of distance matrix returned as single array
        countOfPoints = HarvesineWgs84DistanceMetricTest.GS_POINTS.size
        countOfDistances: int = ((countOfPoints * countOfPoints - countOfPoints) / 2)
        self.assertEqual(distance_matrix.shape[0], countOfDistances)


if __name__ == '__main__':
    unittest.main()
