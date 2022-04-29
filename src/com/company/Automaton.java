package com.company;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Arrays;


// Object that will represent an automaton
public class Automaton {
    private final State[] STATES;                             // List of states of the automaton
    private final int NB_STATES;                    // Maximum number of states of the automaton

    private final Transition[] TRANSITIONS;                   // List of transitions of the automaton
    private final int NB_TRANSITIONS;               // Maximum number of transitions of the automaton
    private final int NB_WORD;

    public Automaton(String address_file) throws Exception{
        String[] file = getFile(address_file); // the file is a list of string where each element of the list is a line

        NB_STATES = Integer.parseInt(file[1]);  // The second (index 1) line of the file contains the number of states, so we take it to initialize MAX_NB_STATES
        NB_TRANSITIONS = Integer.parseInt(file[4]); // The fifth line (index 4) of the file contains the number of transitions, so we take it to initialize MAX_NB_TRANSITIONS
        NB_WORD = Integer.parseInt(file[0]); //the first line gives the number of possible word

        STATES = new State[NB_STATES];
        TRANSITIONS = new Transition[NB_TRANSITIONS];

        setStates(file);
        setTransitions(file);
    }

    public Automaton(State[] states, int nb_states, Transition[] transitions, int nb_transitions, int nb_word){
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
            if (str.equals(state.getNAME()))
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

    public Automaton complete(){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray(); // alphabet of all possible inputs as an array of char
        int i, j;
        boolean[][] state_and_word = new boolean[NB_STATES][NB_WORD];
        boolean complete = true; // the automaton is considered complete until proven the reverse

        for(i=0;i<NB_STATES;i++) { // we set all values to false
            for (j = 1; j < NB_WORD; j++)
                state_and_word[i][j] = false;
        }

        for(j=0;j<NB_STATES;j++){
            for(i=0;i<NB_TRANSITIONS;i++){
                if(TRANSITIONS[i].getSTART() == STATES[j] && !String.valueOf(TRANSITIONS[i].getWORD()).equals("*")){ // if the state as a transition using a word we put true in the list because we know it's used
                    state_and_word[j][TRANSITIONS[i].getWORD() - 97] = true; // 97 is the int value of the ascii code for "a"
                }
            }
        }
        // check already complete or not
        for(boolean[] bls: state_and_word)
            for(boolean bl: bls)
                if(!bl)
                    complete++;

        if(complete == 0)
            return this; // if complete return this

        else {            // if not create the trash state, its transitions and the missing transitions

            int nb_transition = NB_TRANSITIONS; // index for the new transition list
            State[] new_states = new State[NB_STATES + 1]; // we need one more state to add the trash
            Transition[] new_transitions = new Transition[NB_TRANSITIONS + complete + NB_WORD]; //already existing transition + missing transition + transition for the trash to trash

            System.arraycopy(STATES, 0, new_states, 0, NB_STATES);
            System.arraycopy(TRANSITIONS, 0, new_transitions, 0,NB_TRANSITIONS);

            new_states[NB_STATES] = new State("Trash", false, false); // creation of trash

            for (i = 0; i < NB_WORD; i++){ // all possible transitions from trash to trash
                new_transitions[nb_transition] = new Transition(new_states[NB_STATES], alphabet[i], new_states[NB_STATES]);
                nb_transition ++;
            }

            for(i=0;i<NB_STATES;i++) { //of course, we won't check trash that was just done
                for(j=0;j<NB_WORD;j++){
                    if(!state_and_word[i][j]) {
                        new_transitions[nb_transition] = new Transition(STATES[i], alphabet[j], new_states[NB_STATES]); // we add the missing transitions
                        nb_transition++;
                    }
                }
            }

            return new Automaton(new_states, NB_STATES+1, new_transitions, new_transitions.length, NB_WORD); // return the new Automaton
        }
    }

    public Automaton complement(){
        State[] new_states = new State[NB_STATES];

        for(int i = 0; i<NB_STATES; i++){
            new_states[i] = new State(STATES[i].getNAME(), STATES[i].isINITIAL(), !STATES[i].isFINAL());
        }
        return new Automaton(new_states, NB_STATES, TRANSITIONS, NB_TRANSITIONS, NB_WORD);
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
