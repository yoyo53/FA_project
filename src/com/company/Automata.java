package com.company;
import java.io.*;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;


// Object that will represent an automaton
public class Automata {
    private final State[] STATES;                             // List of states of the automaton
    private final int NB_STATES;                    // Maximum number of states of the automaton

    private final Transition[] TRANSITIONS;                   // List of transitions of the automaton
    private final int NB_TRANSITIONS;               // Maximum number of transitions of the automaton


    public Automata(String address_file) throws Exception{
        String[] file = getFile(address_file); // the file is a list of string where each element of the list is a line

        NB_STATES = Integer.parseInt(file[1]);  // The second (index 1) line of the file contains the number of states, so we take it to initialize MAX_NB_STATES
        NB_TRANSITIONS = Integer.parseInt(file[4]); // The fifth line (index 4) of the file contains the number of transitions, so we take it to initialize MAX_NB_TRANSITIONS

        STATES = new State[NB_STATES];
        TRANSITIONS = new Transition[NB_TRANSITIONS];

        setStates(file);
        setTransitions(file);
    }


    /*
    private String[] getFile() throws Exception {
        Scanner scanner = new Scanner(new File(file_address));
        return scanner.useDelimiter("\\A").next().split("\\n");
    }
    */

    private String[] getFile(String address_file) throws Exception {
        StringBuilder sb = new StringBuilder();
        String st;
        BufferedReader br = new BufferedReader(new FileReader(address_file)); //BufferedReader is an optimized way to read the file opened with FileReader
        while ((st = br.readLine()) != null){
            sb.append(st);   //append the line
            sb.append("\n"); // the \n is erased with the readline, but we need it to split the String in an array of Strings
        }
        return sb.toString().split("\n");
    }

    private void setStates(String[] file) {
        boolean initial = false;
        boolean final_state = false;
        String[] initials = file[2].split(" ");
        String[] finals = file[3].split(" ");

        for (int i = 0; i < NB_STATES; i++) {
            String name = String.valueOf(i);

            for(int j=1; j < Integer.parseInt(initials[0]) + 1;j++)
                if(name.equals(initials[j]))
                    initial = true;

            for(int j=1; j< Integer.parseInt(finals[0]) + 1;j++)
                if(name.equals(finals[j]))
                    final_state = true;

            State S = new State(name, initial, final_state);
            STATES[i] = S;

            initial = false;
            final_state = false;
        }
    }

    private State getStateFromName(String str) {
        for(State state: STATES)
            if (str.equals(state.getName()))
                return state;
        return null;
    }

    private void setTransitions(String[] file){
        for(int i=0; i < NB_TRANSITIONS; i++){
            String[] tr = file[i + 5].split("[a-z*]");
            TRANSITIONS[i] = new Transition(getStateFromName(tr[0]), file[i+5].charAt(tr[0].length()), getStateFromName(tr[1]));
        }
    }

    public State[] getSTATES() {
        return STATES;
    }

    public Transition[] getTRANSITIONS() {
        return TRANSITIONS;
    }

    @Override
    public String toString() {
        return "Automata{" +
                "STATES=" + Arrays.toString(STATES) + "\n"+
                ", NB_STATES=" + NB_STATES +
                ", TRANSITIONS=" + Arrays.toString(TRANSITIONS) + "\n"+
                ", NB_TRANSITIONS=" + NB_TRANSITIONS +
                '}' + "\n";
    }
}
