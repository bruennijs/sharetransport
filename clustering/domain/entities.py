from typing import List, Tuple

UID = str



class AbstractEntity(object):
    def __init__(self, uid: UID):
        self._uid = uid

    @property
    def uid(self):
        return self._uid


class Hop(AbstractEntity):
    def __init__(self, uid: UID):
        super().__init__(uid)


class Trip(AbstractEntity):
    def __init__(self, hops: Tuple[Hop, Hop]):
        self.hops = hops


class Community(AbstractEntity):
    def __init__(self, uid: UID, trips: List[Trip]):
        super().__init__(uid)
        self._trips = trips


