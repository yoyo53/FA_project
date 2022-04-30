package com.company;

public class Main {

    public static void main(String[] args) throws Exception{
        Automaton at = new Automaton("determinize_test.txt");
        Automaton at2 = at.determinize();
        Automaton at3 = at2.Minimized();
        System.out.println(at);
        System.out.println(at2);
        System.out.println(at3);

        Automaton at1 = new Automaton("Test.txt"); // test of testWord function
        System.out.println(at1.complete());
        System.out.println(at1.complete().complement());

        System.out.println(at1.complete().testWord("a")); // should return false
        System.out.println(at1.complete().testWord("")); // should return true

    }


}

