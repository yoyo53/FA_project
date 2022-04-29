package com.company;

public class Main {

    public static void main(String[] args) throws Exception{
        Automata at1 = new Automata("Test.txt");
        at1.Complete();
        System.out.println(at1);
    }
}
