package edu.spbu.sort;

import java.util.List;

public class Sort {

    public static void qSortArrays (int array[]){
        qSortArrays(array, 0, array.length - 1);
    }

    public static void qSortArrays (int array[], int li, int ri){
        int i = li;
        int j = ri;
        int m = array[(i + j) / 2];
        do {
            while (array[i] < m) {
                i++;
            }
            while (array[j] > m) {
                j--;
            }
            if (i<=j) {
                int x = array[i];
                array[i] = array[j];
                array[j] = x;
                i++;
                j--;
            }
        } while (i<=j);
        if (li < j) {
            qSortArrays(array,li,j);
        }
        if (i < ri) {
            qSortArrays(array,i,ri);
        }
    }

    public static void qSortList (List<Integer> list){
        qSortList(list, 0, list.size() - 1);
    }

    public static void qSortList (List<Integer> list, int li, int ri){
        int i = li;
        int j = ri;
        int m = list.get((i + j) / 2);
        do {
            while (list.get(i) < m) {
                i++;
            }
            while (list.get(j) > m) {
                j--;
            }
            if (i<=j) {
                int x = list.get(i);
                list.set(i,list.get(j));
                list.set(j,x);
                i++;
                j--;
            }
        } while (i<=j);
        if (li < j) {
            qSortList(list,li,j);
        }
        if (i < ri) {
            qSortList(list,i,ri);
        }
    }
}
