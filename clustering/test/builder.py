import os
from abc import ABC

from geopandas import GeoSeries
from numpy import random
from pandas import DataFrame
from shapely.geometry import Point, Polygon

from domain.entities import Trip, Hop


def load_geojson(fileName: str) -> GeoSeries:
    geoJsonFile: str = os.path.join(os.path.dirname(__file__), fileName)
    return GeoSeries.from_file(geoJsonFile, driver='GeoJSON', crs='WGS84')

def find_polygon(series: GeoSeries) -> Polygon:
    return series[series.apply(lambda geom: isinstance(geom, Polygon))].head(n=1)


class TestDataBase(object):

    def __init__(self) -> None:
        self.clusters: DataFrame = DataFrame(data={
            'filename': ['files/cluster_bahnhof.geojson', 'files/cluster_buergeresch.geojson', 'files/cluster_innenstadt.geojson',
                         'files/cluster_ziegelhof.geojson', 'files/cluster_haarenesch_flat_polygon.geojson']}, index=range(1, 6))
        self.clusters['polygon'] = self.clusters['filename'].apply(lambda fileName: find_polygon(load_geojson(fileName)).convex_hull)

    def create_new_point_in_cluster(self, clusterNo: int) -> Point:
        polygon: Polygon = self.clusters['polygon'].iloc[clusterNo]
        return polygon.representative_point()


class TripBuilder(TestDataBase):

    def __init__(self) -> None:
        super().__init__()

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


class ClusterBuilder(TestDataBase):
    def __init__(self) -> None:
        super().__init__()

    def of_cluster(self, no: int):
        self._cluster_no = no
        return self

    def build(self) -> Polygon:
        return self.clusters['polygon'].iloc[self._cluster_no]
