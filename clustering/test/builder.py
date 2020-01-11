import os

from geopandas import GeoSeries
from numpy import random
from pandas import DataFrame
from shapely.geometry import Point, Polygon

from domain.entities import Trip, Hop


class TripBuilder(object):

    def __init__(self) -> None:
        self.clusters: DataFrame = DataFrame(data={'filename': ['files/cluster_bahnhof.geojson', 'files/cluster_buergeresch.geojson', 'files/cluster_innenstadt.geojson', 'files/cluster_ziegelhof.geojson']}, index=range(1, 5))
        self.clusters['polygon'] = self.clusters['filename'].apply(lambda fileName: TripBuilder.load_cluster_polygon(fileName))

    def pickupCluster(self, no: int):
        self._pickupCluster = no
        return self

    def dropoffCluster(self, no: int):
        self._dropoffCluster = no
        return self

    def build(self) -> Trip:
        new_point: Point = self.create_new_point_in_cluster(self._pickupCluster)
        pickup = Hop(str(random.randint(1000000)), new_point)

        new_point: Point = self.create_new_point_in_cluster(self._dropoffCluster)
        dropOff = Hop(str(random.randint(1000000)), new_point)
        return Trip(pickup=pickup, dropoff=dropOff)

    @staticmethod
    def load_geojson(fileName: str) -> GeoSeries:
        geoJsonFile: str = os.path.join(os.path.dirname(__file__), fileName)
        return GeoSeries.from_file(geoJsonFile, driver='GeoJSON', crs='WGS84')

    @classmethod
    def load_cluster_polygon(cls, fileName: str) -> Polygon:
        return cls.load_geojson(fileName).head(n=1)

    def create_new_point_in_cluster(self, clusterNo: int) -> Point:
        polygon: Polygon = self.clusters['polygon'].iloc[clusterNo]
        return polygon.convex_hull.representative_point()
