package com.company;

public class Main {

    public static void main(String[] args) throws Exception{
        Automaton at1 = new Automaton("Test.txt");

        System.out.println(at1.complete());

    }
}
