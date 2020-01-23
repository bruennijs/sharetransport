import os
from typing import List

from geopandas import GeoSeries
from numpy import random
from pandas import DataFrame, Series
from shapely.geometry import Point, Polygon
from shapely.ops import triangulate

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
                         'files/cluster_ziegelhof.geojson', 'files/cluster_haarenesch_flat_polygon.geojson', 'files/cluster_wahnbek.geojson']}, index=range(1, 7))
        self.clusters['polygon'] = self.clusters['filename'].apply(lambda fileName: find_polygon(load_geojson(fileName)).convex_hull)

    def create_new_point_in_cluster(self, clusterNo: int) -> Point:
        n_samples = 10
        polygon: Polygon = self.clusters['polygon'].iloc[clusterNo]
        points: Series = ClusterPointBuilder().with_polygon(polygon).count(n_samples).build()
        rand_index: int = random.randint(n_samples)
        return points[rand_index]

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


class ClusterPointBuilder(object):

    def __init__(self) -> None:
        super().__init__()

    def with_polygon(self, polygon: Polygon):
        self._polygon = polygon
        return self

    def count(self, count: int):
        self._count = count
        return self

    def build(self) -> Series:
        polygons: List = triangulate(self._polygon)
        polygons_series_times_count: Series = Series([polygons[i % len(polygons)] for i in range(0, self._count)])
        return polygons_series_times_count.apply(lambda p: p.representative_point())
