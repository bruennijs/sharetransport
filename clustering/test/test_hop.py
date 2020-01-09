from unittest import TestCase

from geopy import Point

from domain.entities import Hop


class TestHop(TestCase):
    def test_ctor(self):
        point = Point(8.2, 56.2)
        hop: Hop = Hop('uid', point)

        self.assertEqual('uid', hop.uid)
        self.assertEqual(Point(8.2, 56.2), hop.location)
