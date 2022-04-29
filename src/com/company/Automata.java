package com.company;
import java.io.FileReader;
import java.io.BufferedReader;

// Object that will represent an automaton
public class Automata {
    private final int NB_WORD;

    private final State[] STATES;                             // List of states of the automaton
    private final int NB_STATES;                    // Maximum number of states of the automaton

    private final Transition[] TRANSITIONS;                   // List of transitions of the automaton
    private final int NB_TRANSITIONS;               // Maximum number of transitions of the automaton

    public Automata(String address_file) throws Exception{
        String[] file = getFile(address_file); // the file is a list of string where each element of the list is a line

        NB_WORD = Integer.parseInt(file[0]); //the first line gives the number of possible word

        NB_STATES = Integer.parseInt(file[1]);  // The second (index 1) line of the file contains the number of states, so we take it to initialize MAX_NB_STATES
        STATES = new State[NB_STATES];
        setStates(file);

        NB_TRANSITIONS = Integer.parseInt(file[4]); // The fifth line (index 4) of the file contains the number of transitions, so we take it to initialize MAX_NB_TRANSITIONS
        TRANSITIONS = new Transition[NB_TRANSITIONS];
        setTransitions(file);
    }

    public Automata(int nb_word, State[] states, int nb_states, Transition[] transitions, int nb_transitions){
        NB_WORD = nb_word;
        STATES = states;
        NB_STATES = nb_states;
        TRANSITIONS = transitions;
        NB_TRANSITIONS = nb_transitions;
    }

    private String[] getFile(String address_file) throws Exception {
        StringBuilder sb = new StringBuilder();
        String st;
        BufferedReader br = new BufferedReader(new FileReader(address_file)); //BufferedReader is an optimized way to read the file opened with FileReader
        while ((st = br.readLine()) != null){
            sb.append(st);   //append the line
            sb.append("\n"); // the \n is erased with the readLine, but we need it to split the String in an array of Strings
        }
        return sb.toString().split("\n");
    }

    private void setStates(String[] file) {
        boolean is_initial, is_final;
        String name;
        String[] initials = file[2].split(" ");
        String[] finals = file[3].split(" ");

        for (int i = 0; i < NB_STATES; i++) {
            is_initial = false;
            is_final = false;
            name = String.valueOf(i);

            for(int j = 1; j < Integer.parseInt(initials[0]) + 1; j++)
                if(name.equals(initials[j]))
                    is_initial = true;

            for(int j = 1; j < Integer.parseInt(finals[0]) + 1; j++)
                if(name.equals(finals[j]))
                    is_final = true;

            STATES[i] = new State(name, is_initial, is_final);
        }
    }

    private State getStateFromName(String str) {
        for(State state: STATES)
            if (str.equals(state.getNAME()))
                return state;
        return null;
    }

    private void setTransitions(String[] file){
        State start, end;
        char word;
        for(int i = 0; i < NB_TRANSITIONS; i++){
            String[] tr = file[i + 5].split("[a-z*]");
            start = getStateFromName(tr[0]);
            word = file[i+5].charAt(tr[0].length());
            end = getStateFromName(tr[1]);
            TRANSITIONS[i] = new Transition(start, word, end);
        }
    }

    public State[] getSTATES() {
        return STATES;
    }

    public Transition[] getTRANSITIONS() {
        return TRANSITIONS;
    }

    public Automata Complete(){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray(); // alphabet of all possible inputs as an array of char
        int i, j;
        boolean[][] step_word = new boolean[NB_STATES][NB_WORD];

        for(i = 0; i < NB_STATES; i++)
            for (j = 0; j < NB_WORD; j++)
                step_word[i][j] = false;

        for(i = 0; i < NB_STATES; i++){
            for(j = 0; j < NB_TRANSITIONS; j++){
                if(TRANSITIONS[j].getSTART() == STATES[i] && !String.valueOf(TRANSITIONS[j].getWORD()).equals("*")){
                    step_word[i][TRANSITIONS[j].getWORD() - 97] = true; // 97 is the int value of the ascii code for "a"
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for(i = 0; i < NB_STATES; i++){
            for (j = 0; j < NB_WORD; j++) {
                sb.append(step_word[i][j]);
                sb.append(" ");
            }
            sb.append("\n---------\n");
        }
        System.out.println(sb);
        return this;
    }

    private Transition[] addTransition(Transition[] old_tr, State start, char word, State end){
        Transition[] new_tr = new Transition[old_tr.length + 1];

        System.arraycopy(old_tr, 0, new_tr, 0, old_tr.length);
        new_tr[old_tr.length] = new Transition(start, word, end);

        return new_tr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Automata:\n");
        sb.append(NB_STATES);
        sb.append(" states:\n");
        for (State state: STATES)
            sb.append(state);
        sb.append(NB_TRANSITIONS);
        sb.append(" transitions:\n");
        for (Transition tr: TRANSITIONS)
            sb.append(tr);
        return sb.toString();
    }
}