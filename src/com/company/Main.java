package com.company;

public class Main {

    public static void main(String[] args) throws Exception{
        //Automaton at1 = new Automaton("Test.txt");
        //System.out.println(at1.complete());
        //System.out.println(at1.complete().complement());


        Automaton at = new Automaton("C:\\Users\\capel\\IdeaProjects\\FA_project\\src\\com\\company\\determinize_test.txt");
        Automaton at2 = at.determinize(at);
        System.out.println(at2);
    }
}
