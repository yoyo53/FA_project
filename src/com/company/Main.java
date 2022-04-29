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

    public static boolean testWord(String input, Automaton automaton) {
        int i,j, nb_initials;
        char[] word = input.toCharArray();
        State[] states = automaton.getSTATES(); // not necessary but simplify the reading
        int nb_states = automaton.getNB_STATES(); // not necessary but simplify the reading
        Transition[] transitions= automaton.getTRANSITIONS();

        nb_initials = 0; // it would be troublesome and not useful to put that parameter as a variable of Automaton thus I calculate it first
        for (i = 0; i < nb_states; i++)
            if (states[i].isINITIAL()){
                nb_initials++;
            }

        State[] initials = new State[nb_initials];
        boolean[] accepted = new boolean[nb_initials];
        j=0;
        for(i=0;i<nb_states;i++) // create the list of initial state
            if (states[i].isINITIAL()) {
                initials[j] = states[i];
                j++;
            }

        State current_state;

        for(i=0; i<nb_initials; i++){
            current_state = initials[i];
            for(char w: word){
                current_state = getNextState(current_state, transitions, w);
            }
            accepted[i] = current_state.isFINAL();
        }
        for(boolean ok: accepted)
            if(ok)
                return true;
        return false;
    }

    public static State getNextState(State s, Transition[] transition, char input){ // to do + what to do when multiple direction for a single input ???
        return s;
    }

}
