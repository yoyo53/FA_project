package com.company;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Objects;


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

    public int getNB_STATES() {
        return NB_STATES;
    }

    public Automaton complete(){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray(); // alphabet of all possible inputs as an array of char
        int i, j;
        boolean[][] state_and_word = new boolean[NB_STATES][NB_WORD]; // each state as a list of boolean that indicates whether the word is used or not, first element is for a, last is for z
        int complete = 0;// the automaton is considered complete until proven the reverse


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

    public Transition[] addTransition(Transition[] old_tr, State start, char word, State end){
        Transition[] new_tr = new Transition[old_tr.length+1];
        System.arraycopy(old_tr, 0, new_tr, 0, old_tr.length);
        new_tr[old_tr.length] = new Transition(start, word, end);
        return new_tr;
    }

    public State[] addState(State[] old_st, String name, boolean initial, boolean terminal){
        State[] new_st = new State[old_st.length+1];
        System.arraycopy(old_st, 0, new_st, 0, old_st.length);
        new_st[old_st.length] = new State(name, initial, terminal);
        return new_st;
    }

    public String[] addString(String[] list, String str){
        String[] new_list = new String[list.length+1];
        System.arraycopy(list, 0, new_list, 0, list.length);
        new_list[list.length] = str;
        return new_list;
    }

    public boolean isSynchronous(Automaton at){          // Allows to check if a given automaton is synchronous or not
        for(int i = 0; i<at.NB_TRANSITIONS;i++){
            if(at.getTRANSITIONS()[i].getWORD() == '*'){
                return false;
            }
        }
        return true;
    }

    public boolean isIn(Object[] list, Object word){
        for (int i = 0; i < list.length; i++) {
            if(Objects.equals(word, list[i])){
                return true;
            }
        }
        return false;
    }

    public String[] getAlphabet(Automaton at){
        String[] alphabet = new String[0];
        for (Transition tr:
                at.getTRANSITIONS()) {
            if(!(isIn(alphabet, String.valueOf(tr.getWORD())))){
                alphabet = addString(alphabet, String.valueOf(tr.getWORD()));
            }
        }
        return alphabet;
    }

    public State[] getStateListFromTransitions(Transition[] tr_list){
        State[] result = new State[0];
        for (Transition tr:
                tr_list) {
            if(!(isIn(result, tr.getSTART()))){
                result = addState(result, tr.getSTART().getNAME(), tr.getSTART().isINITIAL(), tr.getSTART().isFINAL());
            }
            if(!(isIn(result, tr.getEND()))){
                result = addState(result, tr.getEND().getNAME(), tr.getEND().isINITIAL(), tr.getEND().isFINAL());
            }
        }
        return result;
    }


    public Automaton determinize(Automaton at){
        Transition[] tr = new Transition[0];
        State[] st = new State[0];
        int tr_size = 0;
        int st_size = 0;

        if(isSynchronous(at)){  // Case where there are no epsilon transitions

            StringBuilder sb = new StringBuilder();         // Will contain the name of the initial state
            boolean term = false;       // Will determine if the initial state is also final
            for(int i = 0 ; i<at.NB_STATES ; i++){
                if(at.getSTATES()[i].isINITIAL()){
                    sb.append(at.getSTATES()[i].getNAME());
                    if(at.getSTATES()[i].isFINAL()){     // If one of the original automaton's initial states is also final, then the composed initial state will also be final
                        term = true;
                    }
                }
            }
            st = addState(st, sb.toString(),true, term);    // We add the composed initial state to the list of states
            st_size ++;

            String[] alphabet = getAlphabet(at);       // an array containing each element of the alphabet of the initial automaton
            for(int i = 0; i<alphabet.length;i++){
                boolean termi = false;  // Determine if the end state of the transition is terminal or not
                StringBuilder sb2 = new StringBuilder();
                for(int j = 0; j < at.NB_TRANSITIONS ; j++){
                    if(at.getTRANSITIONS()[j].getSTART().isINITIAL() && String.valueOf(at.getTRANSITIONS()[j].getWORD()).equals(alphabet[i])){
                        sb2.append(at.getTRANSITIONS()[j].getEND().getNAME());
                        if(at.getTRANSITIONS()[j].getEND().isFINAL()){
                            termi = true;
                        }
                    }
                }
                State end_transi;
                if(sb2.toString().equals(st[0].getNAME())){
                    end_transi = new State(sb2.toString(), true, termi);
                }
                else{
                    end_transi = new State(sb2.toString(), false, termi);
                }
                tr = addTransition(tr, st[0], alphabet[i].charAt(0), end_transi);
                tr_size++;
            }

            State[] all_current_states = getStateListFromTransitions(tr);
            while(!(Arrays.equals(st, all_current_states))){
                for (State ste:
                        all_current_states) {
                    if (!(isIn(st, ste))) {
                        st = addState(st, ste.getNAME(), ste.isINITIAL(), ste.isFINAL());
                        st_size++;

                        for (int i = 0; i < alphabet.length; i++) {
                            StringBuilder sb3 = new StringBuilder();
                            term = false;
                            for (Transition tre :
                                    TRANSITIONS) {
                                if (ste.getNAME().contains(tre.getSTART().getNAME()) && String.valueOf(tre.getWORD()).equals(alphabet[i]) && !(sb3.toString().contains(tre.getEND().getNAME()))) {
                                    sb3.append(tre.getEND().getNAME());
                                    if (tre.getEND().isFINAL()) {
                                        term = true;
                                    }
                                }
                            }
                            State end_transi;
                            if (sb3.toString().equals(st[0].getNAME())) {
                                end_transi = new State(sb3.toString(), true, term);
                            } else {
                                end_transi = new State(sb3.toString(), false, term);
                            }
                            tr = addTransition(tr, ste, alphabet[i].charAt(0), end_transi);
                            tr_size++;
                        }
                    }
                }
                all_current_states = getStateListFromTransitions(tr);
            }

        }

        else{                   // Case where there are epsilon transitions
            return at;
        }
        return new Automaton(st, st_size, tr, tr_size, 3);
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
