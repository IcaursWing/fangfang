package myutil;

import java.util.ArrayList;

public class SortUtil {

    // 定义遍历数组的方法
    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
        }
        System.out.println();
    }

    // 定义方法，实现冒泡排序
    public static void SortH2L(ArrayList<String> key, ArrayList<Integer> value) {
        for (int i = 0; i < value.size() - 1; i++) {
            // 每次内循环的比较，从0索引开始，每次都在递减。注意内循环的次数应该是(arr.length - 1 - i)。
            for (int j = 0; j < value.size() - 1 - i; j++) {
                // 比较的索引是j和j+1
                if (value.get(j) < value.get(j + 1)) {
                    int temp = value.get(j);
                    value.set(j, value.get(j + 1));
                    value.set(j + 1, temp);

                    String temp2 = key.get(j);
                    key.set(j, key.get(j + 1));
                    key.set(j + 1, temp2);
                }
            }
        }
    }

    public static void SortL2H(ArrayList<String> key, ArrayList<Integer> value) {
        for (int i = 0; i < value.size() - 1; i++) {
            // 每次内循环的比较，从0索引开始，每次都在递减。注意内循环的次数应该是(arr.length - 1 - i)。
            for (int j = 0; j < value.size() - 1 - i; j++) {
                // 比较的索引是j和j+1
                if (value.get(j) > value.get(j + 1)) {
                    int temp = value.get(j);
                    value.set(j, value.get(j + 1));
                    value.set(j + 1, temp);

                    String temp2 = key.get(j);
                    key.set(j, key.get(j + 1));
                    key.set(j + 1, temp2);
                }
            }
        }
    }
}
