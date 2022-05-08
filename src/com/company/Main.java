package com.company;

public class Main {

    public static void main(String[] args) {
        Automaton FA = new Automaton("automata/automata_35.txt");
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
            System.out.println("The word '" + word + "' is not recognized by the automaton");

    }

    public static boolean testAutomaton(Automaton at, String[] words) {

        boolean word_accepted;
         for(String i: words){
             word_accepted = at.testWord(i);
             if(at.complete().testWord(i) != word_accepted ){
                 System.out.println("complete failed at word : "+ i);
                 return false;
             }
             if(at.complete().determinize().testWord(i) != word_accepted) {
                 System.out.println("determinization failed at word : "+ i);
                 return false;
             }
             if(at.complete().complete().determinize().minimize().testWord(i) != word_accepted) {
                 System.out.println("minization failed at word : "+ i);
                 return false;
             }
             if(at.complement().testWord(i) == word_accepted) {
                 System.out.println("completent failed at word : "+ i);
                 return false;
             }
             System.out.println(i+" : word recongnized ");
         }
         return true;
    }

}