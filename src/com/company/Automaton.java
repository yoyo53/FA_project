package com.company;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Objects;

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
        int i, j;
        boolean[][] state_and_word = new boolean[NB_STATES][NB_WORD]; // each state as a list of boolean that indicates whether the word is used or not, first element is for 'a', last is for 'z'
        int complete = 0;// the automaton is considered complete until proven the reverse


        for (i = 0; i <NB_STATES; i++) { // we set all values to false
            for (j = 1; j < NB_WORD; j++)
                state_and_word[i][j] = false;
        }

        for(j = 0; j < NB_STATES; j++){
            for(i = 0; i < NB_TRANSITIONS; i++){
                if (TRANSITIONS[i].getSTART() == STATES[j] && !String.valueOf(TRANSITIONS[i].getWORD()).equals("*")){ // if the state as a transition using a word we put true in the list because we know it's used
                    state_and_word[j][TRANSITIONS[i].getWORD() - 97] = true; // 97 is the int value of the ascii code for "a"
                }
            }
        }
        // check already complete or not
        for (boolean[] bls: state_and_word)
            for (boolean bl: bls)
                if (!bl)
                    complete++;

        if (complete == 0)
            return this; // if complete return this

        else {            // if not create the trash state, its transitions and the missing transitions

            int nb_transition = NB_TRANSITIONS; // index for the new transition list
            State[] new_states = new State[NB_STATES + 1]; // we need one more state to add the trash
            Transition[] new_transitions = new Transition[NB_TRANSITIONS + complete + NB_WORD]; //already existing transition + missing transition + transition for the trash to trash

            System.arraycopy(STATES, 0, new_states, 0, NB_STATES);
            System.arraycopy(TRANSITIONS, 0, new_transitions, 0,NB_TRANSITIONS);

            new_states[NB_STATES] = new State("Trash", false, false); // creation of trash

            for (i = 0; i < NB_WORD; i++){ // all possible transitions from trash to trash
                new_transitions[nb_transition] = new Transition(new_states[NB_STATES], (char)(97 + i), new_states[NB_STATES]);
                nb_transition ++;
            }

            for (i = 0; i < NB_STATES; i++) { //of course, we won't check trash that was just done
                for (j = 0; j < NB_WORD; j++){
                    if (!state_and_word[i][j]) {
                        new_transitions[nb_transition] = new Transition(STATES[i], (char)(97 + j), new_states[NB_STATES]); // we add the missing transitions
                        nb_transition++;
                    }
                }
            }

            return new Automaton(NB_WORD, new_states, NB_STATES + 1, new_transitions, new_transitions.length); // return the new Automaton
        }
    }

    public Automaton complement(){
        State[] new_states = new State[NB_STATES];

        for (int i = 0; i < NB_STATES; i++){
            new_states[i] = new State(STATES[i].getNAME(), STATES[i].isINITIAL(), !STATES[i].isFINAL());
        }
        return new Automaton(NB_WORD, new_states, NB_STATES, TRANSITIONS, NB_TRANSITIONS);
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
        boolean found, is_initial;
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
                for (i = 0; i < nb_groups; i++) {
                    groups_name[i] = String.valueOf(i);
                    for (j = 0; j < groups_size[i]; j++)
                        groups_temp[i][j] = groups[i][j];
                }
                System.arraycopy(groups_name, 0, groups_name_temp, 0, nb_groups);
                System.arraycopy(groups_size, 0, groups_size_temp, 0, nb_groups);
                nb_groups_temp = nb_groups;

                for (i = 0; i < nb_groups; i++) {
                    state_type.setLength(0);
                    for (j = 0; j < NB_WORD; j++) {
                        k = 0;
                        found = false;
                        while (!found && k < nb_groups_temp) {
                            l = 0;
                            while (!found && l < groups_size_temp[k]) {
                                if (groups_temp[k][l] == getTransition(groups[i][0], (char) (97 + j))) {
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

                    j = 1;
                    while (j < groups_size[i]) {
                        state_type.setLength(0);
                        for (k = 0; k < NB_WORD; k++) {
                            l = 0;
                            found = false;
                            while (!found && l < nb_groups_temp) {
                                m = 0;
                                while (!found && m < groups_size_temp[l]) {
                                    if (groups_temp[l][m] == getTransition(groups[i][j], (char) (97 + k))) {
                                        state_type.append(groups_name_temp[l]);
                                        state_type.append(".");
                                        found = true;
                                    }
                                    m++;
                                }
                                l++;
                            }
                        }
                        if (groups_name[i].equals(state_type.toString()))
                            j++;
                        else {
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
                            for (k = j; k < groups_size[i]; k++)
                                groups[i][j] = groups[i][j + 1];
                            groups_size[i]--;
                        }
                    }
                }
            }
            while (nb_groups != NB_STATES && nb_groups != nb_groups_temp);
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
                                if (getTransition(groups[i][0], (char)(97 + j)) == groups[k][l]) {
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

    public Transition[] addTransition(Transition[] old_tr, State start, char word, State end){
        Transition[] new_tr = new Transition[old_tr.length + 1];
        System.arraycopy(old_tr, 0, new_tr, 0, old_tr.length);
        new_tr[old_tr.length] = new Transition(start, word, end);
        return new_tr;
    }

    public State[] addState(State[] old_st, String name, boolean initial, boolean terminal){
        State[] new_st = new State[old_st.length + 1];
        System.arraycopy(old_st, 0, new_st, 0, old_st.length);
        new_st[old_st.length] = new State(name, initial, terminal);
        return new_st;
    }

    public boolean isSynchronous(){          // Allows us to check if a given automaton is synchronous or not
        for(Transition tr: TRANSITIONS)
            if (tr.getWORD() == '*')
                return false;
        return true;
    }

    public String get_epsilon_closure(State ste){
        StringBuilder closure = new StringBuilder();
        closure.append(ste.getNAME());
        for (Transition tr:
             TRANSITIONS) {
            if(tr.getWORD() == '*' && Objects.equals(tr.getSTART().getNAME(), ste.getNAME()) && !(closure.toString().contains(tr.getEND().getNAME()))){
                if(closure.toString().length() != 0){
                    closure.append(".");
                }
                closure.append(get_epsilon_closure(tr.getEND()));
            }
        }
        return closure.toString();
    }

    public boolean isIn(Object[] list, Object word){
        for (int i = 0; i < list.length; i++) {
            if(Objects.equals(word, list[i])){
                return true;
            }
        }
        return false;
    }


    public Automaton determinize(){
        Transition[] new_transitions = new Transition[0];
        State[] new_states = new State[0];
        int nb_new_transitions = 0;
        int nb_new_states = 0;
        StringBuilder state_name = new StringBuilder();         // Will contain the name of the states
        boolean term, found;
        int i, j, k, l;
        State end_tr = null;
        String[] current_state_names, next_state_names;
        int min_pos;
        String temp;

        if (isSynchronous()) {  // Case where there are no epsilon transitions

            term = false;       // Will determine if the initial state is also final
            for (i = 0; i < NB_STATES; i++) {
                if (STATES[i].isINITIAL()) {
                    if (state_name.length() != 0)
                        state_name.append('.');
                    state_name.append(STATES[i].getNAME());
                    if (STATES[i].isFINAL())     // If one of the original automaton's initial new_states is also final, then the composed initial state will also be final
                        term = true;
                }
            }

            current_state_names = state_name.toString().split("\\.");
            for (i = 0; i < current_state_names.length; i++) {
                min_pos = i;
                for (j = i + 1; j < current_state_names.length; j++) {
                    if (Integer.parseInt(current_state_names[j]) < Integer.parseInt(current_state_names[min_pos]))
                        min_pos = j;
                }
                if (min_pos != i) {
                    temp = current_state_names[i];
                    current_state_names[i] = current_state_names[min_pos];
                    current_state_names[min_pos] = temp;
                }

            }

            new_states = addState(new_states, String.join(".", current_state_names), true, term);    // We add the composed initial state to the list of new_states
            nb_new_states++;

            i = 0;
            while (i < nb_new_states) {
                current_state_names = new_states[i].getNAME().split("\\.");
                for (j = 0; j < NB_WORD; j++) {
                    term = false;  // Determine if the end state of the transition is terminal or not
                    state_name.setLength(0);
                    for (k = 0; k < NB_TRANSITIONS; k++) {
                        for (l = 0; l < current_state_names.length; l++) {
                            if (TRANSITIONS[k].getSTART().getNAME().equals(current_state_names[l]) && TRANSITIONS[k].getWORD() == (char) (97 + j)) {
                                if (state_name.length() != 0)
                                    state_name.append('.');
                                state_name.append(TRANSITIONS[k].getEND().getNAME());
                                if (TRANSITIONS[k].getEND().isFINAL())
                                    term = true;
                            }
                        }
                    }

                    next_state_names = state_name.toString().split("\\.");
                    for (k = 0; k < next_state_names.length; k++) {
                        min_pos = k;
                        for (l = k + 1; l < next_state_names.length; l++) {
                            if (Integer.parseInt(next_state_names[l]) < Integer.parseInt(next_state_names[min_pos]))
                                min_pos = l;
                        }
                        if (min_pos != k) {
                            temp = next_state_names[k];
                            next_state_names[k] = next_state_names[min_pos];
                            next_state_names[min_pos] = temp;
                        }
                    }
                    state_name.setLength(0);
                    state_name.append(String.join(".", next_state_names));

                    if (state_name.length() != 0) {
                        k = 0;
                        found = false;
                        while (!found && k < nb_new_states) {
                            if (state_name.toString().equals(new_states[k].getNAME())) {
                                end_tr = new_states[k];
                                found = true;
                            }
                            k++;
                        }
                        if (!found) {
                            new_states = addState(new_states, state_name.toString(), false, term);
                            end_tr = new_states[nb_new_states];
                            nb_new_states++;
                        }

                        new_transitions = addTransition(new_transitions, new_states[i], (char) (97 + j), end_tr);
                        nb_new_transitions++;
                    }
                }
                i++;
            }
            return new Automaton(NB_WORD, new_states, nb_new_states, new_transitions, nb_new_transitions);
        }

        else{                   // Case where there are epsilon transitions

            // Get all the states that have incoming or outgoing non epsilon transitions
            for (Transition tr:
                    TRANSITIONS) {
                if(tr.getWORD() != '*' && !(isIn(new_states, tr.getEND()))){
                    new_states = addState(new_states, tr.getEND().getNAME(), tr.getEND().isINITIAL(), tr.getEND().isFINAL());
                    nb_new_states++;
                    if(!(isIn(new_states, tr.getSTART()))){
                        new_states = addState(new_states, tr.getSTART().getNAME(), tr.getSTART().isINITIAL(), tr.getSTART().isFINAL());
                        nb_new_states++;
                    }
                }
            }

            // Adjust the initial and final states
            for (State st:
                 STATES) {
                if(st.isINITIAL()){
                    String[] closure = get_epsilon_closure(st).split("\\.");
                    for (int m = 1; m < closure.length; m++) {
                        if(isIn(new_states, getStateFromName(closure[m]))){
                            for (State ste:
                                 new_states) {
                                if(Objects.equals(ste.getNAME(), closure[m])){
                                    ste.setINITIAL(true);
                                }
                            }
                        }
                    }
                }
                if(st.isFINAL()){
                    String[] closure = get_epsilon_closure(st).split("\\.");
                    for (int m = 0; m < new_states.length; m++) {
                        if(isIn(get_epsilon_closure(new_states[m]).split("\\."), st.getNAME())){
                            new_states[m].setFINAL(true);
                        }
                    }
                }
            }

            // Keep only useful transitions
            for (Transition tr:
                 TRANSITIONS) {
                if(tr.getWORD() != '*'){
                    new_transitions = addTransition(new_transitions, tr.getSTART(), tr.getWORD(), tr.getEND());
                    nb_new_transitions++;
                }
            }

            // We now have the equivalent synchronous automaton. We will then determinize it using the method presented above
            Automaton synchronous_equivalent_at = new Automaton(NB_WORD, new_states, nb_new_states, new_transitions, nb_new_transitions);
            return synchronous_equivalent_at.determinize();
        }
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