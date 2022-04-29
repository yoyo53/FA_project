package com.company;

public class Main {

    public static void main(String[] args) throws Exception{
        Automaton at1 = new Automaton("Test.txt");
        at1.Complete();
        System.out.println(at1);
    }
}
