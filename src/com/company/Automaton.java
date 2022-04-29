package com.company;
import java.io.FileReader;
import java.io.BufferedReader;

// Object that will represent an automaton
public class Automaton {
    private final int NB_WORD;

    private final State[] STATES;                             // List of states of the automaton
    private final int NB_STATES;                    // Maximum number of states of the automaton

    private final Transition[] TRANSITIONS;                   // List of transitions of the automaton
    private final int NB_TRANSITIONS;               // Maximum number of transitions of the automaton

    public Automaton(String address_file) throws Exception{
        String[] file = getFile(address_file); // the file is a list of string where each element of the list is a line

        NB_WORD = Integer.parseInt(file[0]); //the first line gives the number of possible word

        NB_STATES = Integer.parseInt(file[1]);  // The second (index 1) line of the file contains the number of states, so we take it to initialize MAX_NB_STATES
        STATES = new State[NB_STATES];
        setStates(file);

        NB_TRANSITIONS = Integer.parseInt(file[4]); // The fifth line (index 4) of the file contains the number of transitions, so we take it to initialize MAX_NB_TRANSITIONS
        TRANSITIONS = new Transition[NB_TRANSITIONS];
        setTransitions(file);
    }

    public Automaton(int nb_word, State[] states, int nb_states, Transition[] transitions, int nb_transitions){
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

            for (int j = 1; j < Integer.parseInt(initials[0]) + 1; j++)
                if (name.equals(initials[j]))
                    is_initial = true;

            for (int j = 1; j < Integer.parseInt(finals[0]) + 1; j++)
                if (name.equals(finals[j]))
                    is_final = true;

            STATES[i] = new State(name, is_initial, is_final);
        }
    }

    private State getStateFromName(String str) {
        for (State state: STATES)
            if (str.equals(state.getNAME()))
                return state;
        return null;
    }

    private void setTransitions(String[] file){
        State start, end;
        char word;
        for (int i = 0; i < NB_TRANSITIONS; i++){
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



    public Automaton Minimized() {
        State[][] groups, groups_temp;
        String[] groups_name, groups_name_temp;
        int[]  groups_size, groups_size_temp;
        int nb_groups, nb_groups_temp;
        StringBuilder state_type;
        State[] new_states;
        Transition[] new_transitions;
        State end = null;
        boolean found, equals, is_initial;
        int i, j, k, l, m;

        if (!isDeterminized())
            System.out.println("This automaton is not determinized.");
        else if (!isComplete())
            System.out.println("This automaton is not complete.");

        else {
            nb_groups = 0;
            groups = new State[NB_STATES][NB_STATES];
            groups_name = new String[NB_STATES];
            groups_size = new int[NB_STATES];
            groups_temp = new State[NB_STATES][NB_STATES];
            groups_name_temp = new String[NB_STATES];
            groups_size_temp= new int[NB_STATES];
            state_type = new StringBuilder();

            for (i = 0; i < NB_STATES; i++) {
                state_type.setLength(0);
                if (STATES[i].isFINAL())
                    state_type.append("T");
                else
                    state_type.append("NT");

                j = 0;
                found = false;
                while (!found && j < nb_groups) {
                    if (state_type.toString().equals(groups_name[j])) {
                        groups[j][groups_size[j]] = STATES[i];
                        groups_size[j]++;
                        found = true;
                    }
                    j++;
                }
                if (!found) {
                    groups[nb_groups][0] = STATES[i];
                    groups_name[nb_groups] = state_type.toString();
                    groups_size[nb_groups] = 1;
                    nb_groups++;
                }
            }

            do {
                for (i = 0; i < nb_groups; i++)
                    groups_name[i] = String.valueOf(i);

                System.arraycopy(groups, 0, groups_temp, 0, nb_groups);
                System.arraycopy(groups_name, 0, groups_name_temp, 0, nb_groups);
                System.arraycopy(groups_size, 0, groups_size_temp, 0, nb_groups);
                nb_groups_temp = nb_groups;

                i = 0;
                equals = true;
                while (i < nb_groups) {
                    state_type.setLength(0);
                    for (j = 0; j < NB_WORD; j++) {
                        k = 0;
                        found = false;
                        while (!found && k < nb_groups_temp) {
                            l = 0;
                            while (!found && l < groups_size_temp[k]) {
                                if (groups_temp[k][l] == getTransition(groups[i][0], (char) (97 + NB_WORD))) {
                                    state_type.append(groups_name_temp[k]);
                                    state_type.append(".");
                                    found = true;
                                }
                                l++;
                            }
                            k++;
                        }
                    }
                    groups_name[i] = state_type.toString();

                    for (j = 1; j < groups_size[i]; j++) {
                        for (k = 0; k < NB_WORD; k++) {
                            l = 0;
                            found = false;
                            while (!found && l < nb_groups_temp) {
                                m = 0;
                                while (!found && m < groups_size_temp[l]) {
                                    if (groups_temp[l][m] == getTransition(groups[i][j], (char) (97 + NB_WORD))) {
                                        state_type.append(groups_name_temp[l]);
                                        state_type.append(".");
                                        found = true;
                                    }
                                    l++;
                                }
                                k++;
                            }
                        }
                        if (groups_name[i].equals(state_type.toString()))
                            i++;
                        else {
                            equals = false;
                            k = 0;
                            found = false;
                            while (!found && k < nb_groups) {
                                if (groups_name[k].equals(state_type.toString())) {
                                    groups[k][groups_size[k]] = groups[i][j];
                                    groups_size[k]++;
                                    found = true;
                                }
                                k++;
                            }
                            if (!found) {
                                groups[nb_groups][0] = groups[i][j];
                                groups_name[nb_groups] = state_type.toString();
                                groups_size[nb_groups] = 1;
                                nb_groups++;
                            }
                            System.arraycopy(groups[i], j, groups[i], j - 1, groups_size[i] - j);
                            groups_size[i]--;
                        }

                    }
                }
            }
            while (nb_groups != NB_STATES && !equals);
            if (nb_groups == NB_STATES)
                System.out.println("This automaton is already minimized.");
            else {
                new_states = new State[nb_groups];
                new_transitions = new Transition[nb_groups * NB_WORD];
                for (i = 0; i < nb_groups; i++) {
                    is_initial = false;
                    j = 0;
                    while (!is_initial && j < groups_size[i]) {
                        if (groups[i][j].isINITIAL())
                            is_initial = true;
                        j++;
                    }
                    new_states[i] = new State(String.valueOf(i), is_initial, groups[i][0].isFINAL());
                }
                for (i = 0; i < nb_groups; i++) {
                    for (j = 0; j < NB_WORD; j++) {
                        k = 0;
                        found = false;
                        while (!found && k < nb_groups) {
                            l = 0;
                            while (!found && l < groups_size[k]) {
                                if (getTransition(new_states[i], (char)(97 + j)) == groups[k][l]) {
                                    end = new_states[k];
                                    found = true;
                                }
                                l++;
                            }
                            k++;
                        }
                        new_transitions[i * NB_WORD + j] = new Transition(new_states[i], (char)(97 + j), end);
                    }
                }
                return new Automaton(NB_WORD, new_states, nb_groups, new_transitions, nb_groups * NB_WORD);
            }
        }
        return this;
    }

    public boolean isDeterminized() {
        return true;
    }

    public boolean isComplete() {
        return true;
    }

    public State getTransition(State state, char word) {
        for (Transition tr: TRANSITIONS)
            if (tr.getSTART() == state && tr.getWORD() == word)
                return tr.getEND();
        return null;
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