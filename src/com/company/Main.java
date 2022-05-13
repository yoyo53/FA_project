package com.company;

public class Main {

    public static void main(String[] args) {
        testAutomaton("automata/automaton_12.txt");
        /*Automaton FA = new Automaton("automata/automaton_22.txt");

        Automaton DFA = FA.determinize();
        Automaton CDFA = DFA.complete();
        Automaton MCDFA = CDFA.minimize();
        Automaton MCDFAcomp = MCDFA.complement();
        String word;

        System.out.println("----------------------------------------------------------------------");
        System.out.println("Initial automaton:");
        System.out.println(FA);
        System.out.println("----------------------------------------------------------------------\n");

        System.out.println("----------------------------------------------------------------------");
        System.out.println("Deterministic automaton:");
        System.out.println(DFA);
        System.out.println("----------------------------------------------------------------------\n");

        System.out.println("----------------------------------------------------------------------");
        System.out.println("Complete deterministic automaton:");
        System.out.println(CDFA);
        System.out.println("----------------------------------------------------------------------\n");

        System.out.println("----------------------------------------------------------------------");
        System.out.println("Minimal complete deterministic automaton:");
        System.out.println(MCDFA);
        System.out.println("----------------------------------------------------------------------\n");

        System.out.println("----------------------------------------------------------------------");
        System.out.println("Complement of the minimal complete deterministic automaton::");
        System.out.println(MCDFAcomp);
        System.out.println("----------------------------------------------------------------------\n");

        word = "aaaaaaaaaa";
        if (MCDFA.testWord(word))
            System.out.println("The word '" + word + "' is recognized by the automaton.");
        else
            System.out.println("The word '" + word + "' is not recognized by the automaton");

        word = "ba";
        if (MCDFA.testWord(word))
            System.out.println("The word '" + word + "' is recognized by the automaton.");
        else
            System.out.println("The word '" + word + "' is not recognized by the automaton");*/
    }

    static public void testAutomaton(String name){
        Automaton at = new Automaton(name);
        System.out.println("initial");
        at.printTable();
        System.out.println("___________");
        System.out.println("determinized");
        at.determinize().printTable();
        System.out.println("___________");
        System.out.println("complete");
        at.determinize().complete().printTable();
        System.out.println("___________");
        System.out.println("minimized");
        at.determinize().complete().minimize().printTable();

    }

}