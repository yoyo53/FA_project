package com.company;

public class Main {

    public static void main(String[] args) throws Exception {
        //Automaton at1 = new Automaton("Test.txt");
        //System.out.println(at1.complete());
        //System.out.println(at1.complete().complement());


        Automaton at = new Automaton("determinize_test.txt");
        Automaton at2 = at.determinize();
        Automaton at3 = at2.Minimized();
        System.out.println(at);
        System.out.println(at2);
        System.out.println(at3);
    }

    public static boolean testWord(String input, Automaton automaton) {
        char[] word = input.toCharArray();

        if(automaton == null)
            return false;

        if(!automaton.isDeterminized()) // we work on determinized automaton to make it simpler
            automaton = automaton.determinize();

        State current_state = getInitial(automaton.getSTATES()); // we start at the first state

        for(char w:word){
            for (Transition tr: automaton.getTRANSITIONS()){
                if(tr.getSTART() == current_state && tr.getWORD() == w)
                    current_state = tr.getEND();
            }
        }

        return current_state.isFINAL();
    }

    public static State getInitial(State[] st){
        for (State state : st) {
            if (state.isINITIAL())
                return state;
        }
        return null;
    }
}

