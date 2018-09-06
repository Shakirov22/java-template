package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void sort (int array[]) {
    Sort.qSortArrays(array);
  }

  public static void sort (List<Integer> list) {
    Sort.qSortList(list);
  }
}
