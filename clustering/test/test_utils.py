from typing import List, Tuple
from unittest import TestCase
import numpy as np
from shapely.geometry import Point

from infrastructure.util import Utils


class TestUtils(TestCase):
    def test_flatten(self):
        listOfLists = [[a] for a in range(0, 6)]

        # WHEN
        expected: List[int] = Utils.flatten(listOfLists)

        # THEN
        self.assertEqual([0, 1, 2, 3, 4, 5], expected)

    def test_flat_map_with_tuple(self):
        listOfLists: List[Tuple[int]] = [(a, a) for a in range(0, 6)]

        # WHEN
        expected: List[int] = Utils.flatmap(listOfLists, lambda tuple: tuple)

        # THEN
        self.assertEqual([0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5], expected)

    def test_flat_map_with_list(self):
        listOfLists: List[List[int]] = [[a, a] for a in range(0, 6)]

        # WHEN
        expected: List[int] = Utils.flatmap(listOfLists, lambda list: list)

        # THEN
        self.assertEqual([0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5], expected)

    def test_(self):
        list_two_dim = [[1, '2'], [3, '4'], [3, '3']]
        array = np.array(list_two_dim)

        self.assertEqual((3, 2), array.shape)

    def test_to_2d_array(self):
        expected2dArray = np.array([[2.0, 1.0], [3.0, 2.0]])
        p1 = Point(1.0, 2.0)
        p2 = Point(2.0, 3.0)

        array_2d = Utils.to_2d_array([p1, p2])

        self.assertTrue(np.array_equal(array_2d, expected2dArray))
