package myutil;

import java.util.Random;

public class RandomSort {

    private Random random = new Random();


    private final int SIZE;
    private int[] positions;

    public RandomSort(int mSIZE) {
        SIZE = mSIZE;
        positions = new int[SIZE];

        for (int index = 0; index < SIZE; index++) {

            positions[index] = index + 1;

        }


        printPositions();

    }

    public String[] getStringPositions() {
        String[] temp = new String[SIZE];
        for (int i = 0; i < positions.length; i++) {
            temp[i] = String.valueOf(positions[i]);
        }
        return temp;
    }

    public int[] getIntPositions() {
        return positions;
    }


    public void changePosition() {

        for (int index = SIZE - 1; index >= 0; index--) {


            exchange(random.nextInt(index + 1), index);

        }
        printPositions();

    }


    private void exchange(int p1, int p2) {

        int temp = positions[p1];

        positions[p1] = positions[p2];

        positions[p2] = temp;

    }


    public void printPositions() {

        for (int index = 0; index < SIZE; index++) {


            System.out.print(positions[index] + " ");

        }

        System.out.println();

    }

}