package com.manoelcampos.sample;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    private final ArrayList<Integer> arrayList;

    public Main(){
        System.out.printf("%nStarting %s%n", Main.class.getName());
        this.arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrayList.add(i);
        }

        final Random rand = new Random();
        for (int i = 0; i < 200; i++) {
            arrayList.get(rand.nextInt(arrayList.size()));
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
