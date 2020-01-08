from typing import List, Tuple

from shapely.geometry import Point

UID = str


class AbstractEntity(object):
    def __init__(self, uid: UID):
        self._uid = uid

    @property
    def uid(self) -> UID:
        return self._uid


class Hop(AbstractEntity):
    def __init__(self, uid: UID, location: Point):
        super().__init__(uid)
        self._location = location

    @property
    def location(self) -> Point:
        return self._location

class Trip(AbstractEntity):
    def __init__(self, pickup: Hop, dropoff: Hop):
        self._dropoff = dropoff
        self._pickup = pickup

    @property
    def pickup(self) -> Hop:
        return self._pickup

    @property
    def dropoff(self) -> Hop:
        return self._dropoff

class Community(AbstractEntity):
    def __init__(self, uid: UID, trips: List[Trip]):
        super().__init__(uid)
        self._trips = trips

    @property
    def trips(self) -> List[Trip]:
        return self._trips
