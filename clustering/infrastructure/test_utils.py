from typing import List, Tuple
from unittest import TestCase

from infrastructure.util import Utils


class TestUtils(TestCase):
    def test_flatten(self):
        listOfLists = [[a] for a in range(0, 6)]

        # WHEN
        expected: List[int] = Utils.flatten(listOfLists)

        # THEN
        self.assertEqual([0, 1, 2, 3, 4, 5], expected)

    def test_flat_map_with_tuple(self):
        listOfLists: List[Tuple[int]] = [ (a, a) for a in range(0, 6)]

        # WHEN
        expected: List[int] = Utils.flatmap(listOfLists, lambda tuple: tuple)

        # THEN
        self.assertEqual([0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5], expected)

    def test_flat_map_with_list(self):
        listOfLists: List[List[int]] = [ [a, a] for a in range(0, 6)]

        # WHEN
        expected: List[int] = Utils.flatmap(listOfLists, lambda list: list)

        # THEN
        self.assertEqual([0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5], expected)
