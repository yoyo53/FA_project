package com.company;
import java.awt.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Arrays;
import java.io.File;
import java.util.Scanner;


// Object that will represent an automaton
public class Automata {
    private final State[] STATES;                             // List of states of the automaton
    private final int NB_STATES;                    // Maximum number of states of the automaton

    private final Transition[] TRANSITIONS;                   // List of transitions of the automaton
    private final int NB_TRANSITIONS;               // Maximum number of transitions of the automaton
    private final int NB_WORD;

    public Automata(String address_file) throws Exception{
        String[] file = getFile(address_file); // the file is a list of string where each element of the list is a line

        NB_STATES = Integer.parseInt(file[1]);  // The second (index 1) line of the file contains the number of states, so we take it to initialize MAX_NB_STATES
        NB_TRANSITIONS = Integer.parseInt(file[4]); // The fifth line (index 4) of the file contains the number of transitions, so we take it to initialize MAX_NB_TRANSITIONS
        NB_WORD = Integer.parseInt(file[0]); //the first line gives the number of possible word

        STATES = new State[NB_STATES];
        TRANSITIONS = new Transition[NB_TRANSITIONS];

        setStates(file);
        setTransitions(file);
    }

    public Automata(State[] states, int nb_states, Transition[] transitions, int nb_transitions, int nb_word){
        STATES = states;
        NB_STATES = nb_states;
        TRANSITIONS = transitions;
        NB_TRANSITIONS = nb_transitions;
        NB_WORD = nb_word;
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

    public Automata Complete(){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz*".toCharArray(); // alphabet of all possible inputs as an array of char
        int i,j,k,w;
        boolean word_used;
        String[][] step_word = new String[NB_STATES+1][NB_WORD+1];

        for(i=0;i<NB_STATES;i++)
            step_word[i][0]=  String.valueOf(i);
            for(j=1;j<NB_WORD+1;j++)
                step_word[i][j] = "";

        for(i=0;i<NB_TRANSITIONS;i++){
            for(j=0;j<NB_STATES;j++){
                if(TRANSITIONS[i].getInit() == STATES[j]){

                    word_used =false;
                    for(k=1;k<NB_WORD+1;k++){
                        if(String.valueOf(TRANSITIONS[i].getWord()) == step_word[j][k])
                            word_used = true;
                    }
                    for(w=1;w<NB_WORD+1;w++)
                        if(!word_used)
                            if(step_word[j][w] == null) {

                                step_word[j][w] = String.valueOf(TRANSITIONS[i].getWord());
                                word_used = true;
                            }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for(i=0;i<NB_STATES;i++){
            for (j=0;j<NB_WORD+1;j++) {
                sb.append(step_word[i][j]);
                sb.append(" ");
            }
            sb.append("\n---------\n");
        }
        System.out.println(sb.toString());
        return this;
    }
/*
    public Automata Complete(){

        //We create a new automaton and we make it complete


        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray(); // alphabet of all possible inputs as an array of char
        int i, j;

        String[][] check_states = new String[NB_WORD][NB_TRANSITIONS+1]; // (+1 word) this array will allow us to check for each input if each state use it at least once
        StringBuilder sb = new StringBuilder(); // optimized way to build a string because string are easier to cut

        for(i=0; i<NB_WORD;i++) {
            sb.setLength(0);
            sb.append(String.valueOf(alphabet[i])); // add the input as the first element of each array inside the check_states array
            sb.append(" ");// for split
            for (j = 0; j < NB_TRANSITIONS; j++) {
                if (TRANSITIONS[j].getWord() == alphabet[i]){
                    sb.append(String.valueOf(TRANSITIONS[j].getInit().getName())); // add states using the input alphabet[i]
                    sb.append(" ");

                }
            }
            System.out.println(sb.toString());
            check_states[i] = sb.toString().split(" ");
        }
        return CompleteAddTrashAndTransitions(check_states);
    }

    private Automata CompleteAddTrashAndTransitions(String[][] check_states){

        //Add the Trash state and the necessary transitions

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int i,j,step;
        boolean trash = false; // trash state not created yet
        State[] new_states = new State[NB_STATES+1]; // current state plus the space for the trash
        Transition[] new_transitions = new Transition[NB_TRANSITIONS]; // current transitions, size doesn't matter

        for(i = 0; i<NB_WORD; i++){
            step = 0; // step represent the current state we are looking for according to the word


            for(j = 1; j<check_states[i].length; j++) {
                if (step == Integer.parseInt(check_states[i][j])) //if the state already use the word we go on
                    step += 1;

                else {// if not we add the transition
                    if (!trash) { // if trash isn't already created we make it and its transitions
                        for (int s = 0; s < NB_STATES; s++)
                            new_states[s] = STATES[s];

                        new_states[NB_STATES] = new State("Trash", false, false);

                        for (int k = 0; k < NB_WORD; k++) // transitions of trash state
                            if(k==0) // first one based on the current transitions
                                new_transitions = addTransition(TRANSITIONS, new_states[NB_STATES], alphabet[k], new_states[NB_STATES]);
                            else
                                new_transitions = addTransition(new_transitions, new_states[NB_STATES], alphabet[k], new_states[NB_STATES]);

                        trash = true;
                    }
                    new_transitions = addTransition(new_transitions, new_states[step], alphabet[i], new_states[NB_STATES]); // we add the missing transitions
                    step+= 1;
                }
            }


        }
        return new Automata(new_states, new_states.length, new_transitions, new_transitions.length, NB_WORD);
    }*/

    private Transition[] addTransition(Transition[] old_tr, State init, char word, State end){
        Transition[] new_tr = new Transition[old_tr.length+1];
        for(int i = 0; i<old_tr.length; i++)
            new_tr[i] = old_tr[i];

        new_tr[old_tr.length] = new Transition(init,word,end);
        return new_tr;
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
