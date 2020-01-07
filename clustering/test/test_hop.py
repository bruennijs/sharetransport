from unittest import TestCase

from domain.entities import Hop


class TestHop(TestCase):
    def test_ctor(self):
        hop: Hop = Hop('uid')

        self.assertEqual('uid', hop.uid)
