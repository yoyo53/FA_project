package com.company;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Object that will represent an automaton
public class Automaton {
    private final int NB_LETTER;

    private final State[] STATES;                             // List of states of the automaton
    private final int NB_STATES;                    // Maximum number of states of the automaton

    private final Transition[] TRANSITIONS;                   // List of transitions of the automaton
    private final int NB_TRANSITIONS;               // Maximum number of transitions of the automaton


    public Automaton(String address_file) {
        String[] file = getFile(address_file); // the file is a list of string where each element of the list is a line
        if (file != null) {
            NB_LETTER = Integer.parseInt(file[0]); //the first line gives the number of possible letters in the alphabet

            NB_STATES = Integer.parseInt(file[1]);  // The second (index 1) line of the file contains the number of states, so we take it to initialize MAX_NB_STATES
            STATES = new State[NB_STATES];
            setStates(file);

            NB_TRANSITIONS = Integer.parseInt(file[4]); // The fifth line (index 4) of the file contains the number of transitions, so we take it to initialize MAX_NB_TRANSITIONS
            TRANSITIONS = new Transition[NB_TRANSITIONS];
            setTransitions(file);
        }
        else {
            NB_LETTER = 0;

            NB_STATES = 0;
            STATES = new State[0];

            NB_TRANSITIONS = 0;
            TRANSITIONS = new Transition[0];
        }
    }

    public Automaton(int nb_letter, State[] states, int nb_states, Transition[] transitions, int nb_transitions) {
        NB_LETTER = nb_letter;
        STATES = states;
        NB_STATES = nb_states;
        TRANSITIONS = transitions;
        NB_TRANSITIONS = nb_transitions;
    }

    private String[] getFile(String address_file) {
        try {
            return Files.readString(Path.of(address_file)).split("\r\n");
        }
        catch (IOException exception) {
            System.out.println("Error file not found");
            return null;
        }
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

    private void setTransitions(String[] file) {
        State start, end;
        char letter;
        for (int i = 0; i < NB_TRANSITIONS; i++){
            String[] tr = file[i + 5].split("[a-z*]");
            start = getStateFromName(tr[0]);
            letter = file[i+5].charAt(tr[0].length());
            end = getStateFromName(tr[1]);
            TRANSITIONS[i] = new Transition(start, letter, end);
        }
    }


    private Transition[] addTransition(Transition[] old_tr, Transition tr) {
        Transition[] new_tr = new Transition[old_tr.length + 1];
        System.arraycopy(old_tr, 0, new_tr, 0, old_tr.length);
        new_tr[old_tr.length] = tr;
        return new_tr;
    }

    private State[] addState(State[] old_st, State state) {
        State[] new_st = new State[old_st.length + 1];
        System.arraycopy(old_st, 0, new_st, 0, old_st.length);
        new_st[old_st.length] = state;
        return new_st;
    }


    public boolean isSynchronous() {          // Allows us to check if a given automaton is synchronous or not
        for(Transition tr: TRANSITIONS)
            if (tr.getLETTER() == '*')
                return false;
        return true;
    }

    private String epsilonClosure(State state, String old_closure) {
        StringBuilder closure = new StringBuilder();
        closure.append(old_closure);
        String[] state_names;
        boolean found;
        int i;
        closure.append(state.getNAME());
        for (Transition tr: TRANSITIONS) {
            if(tr.getLETTER() == '*' && tr.getSTART() == state){
                state_names = closure.toString().split("\\.");
                i = 0;
                found = false;
                while (!found && i < state_names.length) {
                    if (tr.getEND().getNAME().equals(state_names[i]))
                        found = true;
                    i++;
                }
                if (!found) {
                    closure.append(".");
                    closure.append(epsilonClosure(tr.getEND(), closure.toString()));
                }
            }
        }
        return closure.toString();
    }

    /**
     * Allows to get the synchronous version of a given asynchronous automaton
     *
     * @return An equivalent new synchronous automaton
     */

    public Automaton synchronize() {
        Transition[] new_transitions = new Transition[0];       // Will contain the transitions of the new automaton
        State[] new_states = new State[0];                      // Will contain the states of the new automaton
        int nb_new_transitions = 0, nb_new_states = 0;          // Allow respectively to keep track of the number of new transitions and states
        boolean initial,                                        //
                found;                                          //
        int i, j;                                               // Counters for the different loops
        State start_tr, end_tr;                                 // Will respectively store the start state and end state of the studied transition
        String[] closure;                                       // Will contain the espilon closures of the different states

        if (isSynchronous())        // Check if the original automaton is not already synchronous
            System.out.println("This automaton is already synchronous");
        else{
            // This part allows to change, if need be, the initial argument of each state of the original automaton, by studying its epsilon closure
            for (State state: STATES) {     // This loop goes through all the states of the original automaton
                if (state.isINITIAL())
                    initial = true;     // If the state is initial, we don't do anything
                else {  // If the state is not initial, we should check if it is contained in the epsilon closure of an initial state
                    i = 0;
                    initial = false;
                    while (!initial && i < NB_STATES) { // This loop will go through all the list of states, unless initial change to true
                        if(STATES[i].isINITIAL()) { // If we find an initial state, we will compute its epsilon closure
                            closure = epsilonClosure(STATES[i], "").split("\\.");
                            j = 0;
                            while (!initial && j < closure.length) { // This loop will go through all the states contained in the epsilon closure, unless initial change to true
                                if (state.getNAME().equals(closure[j])) // if we find the state we are studying in the epsilon closure of one initial state, we change initial to true
                                    initial = true;
                                j++;
                            }
                        }
                        i++;
                    }
                }
                new_states = addState(new_states, new State(state.getNAME(), initial, state.isFINAL()));    // We add the studied state to the list new_states
                nb_new_states++;    // We increment the number of new states
            }

            // Now, we want to add in the list new_transitions only the useful transitions for the synchronous automaton
            for (Transition tr: TRANSITIONS) {  // We go through the list of all transitions
                if (tr.getLETTER() != '*') {    // We check if the studied transition is an epsilon transition or not
                    i = 0;  // If it is not the case, we won't need to modify it, so we should find its start and end state in the list new_states, and add it to the list new_transitions
                    start_tr = null;
                    end_tr = null;
                    while ((start_tr == null || end_tr == null) && i < nb_new_states) {
                        if (tr.getSTART().getNAME().equals(new_states[i].getNAME()))    // This condition allows to check if we found the start state or not
                            start_tr = new_states[i];
                        if (tr.getEND().getNAME().equals(new_states[i].getNAME()))      // This condition allows to check if we found the final state or not
                            end_tr = new_states[i];
                        i++;
                    }
                    new_transitions = addTransition(new_transitions, new Transition(start_tr, tr.getLETTER(), end_tr));     // We add the non-epsilon transition to the list new_transitions
                    nb_new_transitions++;   // We increment the number of new transitions
                }
            }

            // Now we want to replace the epsilon transitions
            for (i = 0; i < nb_new_states; i++) {   //
                closure = epsilonClosure(STATES[i], "").split("\\.");
                for (Transition tr: TRANSITIONS) {
                    if (tr.getEND().getNAME().equals(new_states[i].getNAME()) && tr.getLETTER() != '*') {
                        for (String name: closure) {
                            j = 0;
                            found = false;
                            while (!found && j < nb_new_transitions) {
                                if (new_transitions[j].getSTART().getNAME().equals(tr.getSTART().getNAME())
                                        && new_transitions[j].getLETTER() == tr.getLETTER()
                                        && new_transitions[j].getEND().getNAME().equals(name))
                                    found = true;
                                j++;
                            }

                            if (!found) {
                                j = 0;
                                start_tr = null;
                                end_tr = null;
                                while ((start_tr == null || end_tr == null) && j < nb_new_states) {
                                    if (tr.getSTART().getNAME().equals(new_states[j].getNAME()))
                                        start_tr = new_states[j];
                                    if (name.equals(new_states[j].getNAME()))
                                        end_tr = new_states[j];
                                    j++;
                                }
                                new_transitions = addTransition(new_transitions, new Transition(start_tr, tr.getLETTER(), end_tr));
                                nb_new_transitions++;
                            }
                        }
                    }
                }
            }

            // We have now the equivalent synchronous automaton
            return new Automaton(NB_LETTER, new_states, nb_new_states, new_transitions, nb_new_transitions);
        }
        return this;
    }

    /**
     * Check if a given automaton is deterministic or not
     *
     * @return boolean
     */

    public boolean isDeterministic() {
        int nb_initials = 0;    // Will keep track of the number of initial states of the automaton
        int[][] nb_transitions = new int[NB_STATES][NB_LETTER];     // Will keep track of the number of transitions of the automaton, and their associated letters
        if (!isSynchronous())   // If the automaton is asynchronous, it cannot be deterministic, so we return false
            return false;
        for (int i = 0; i < NB_STATES; i++) {   // We will go through all the states of the automaton
            if (STATES[i].isINITIAL()) {    // Check if the state is initial
                if (nb_initials > 0)    // Check if nb_initials is strictly higher than 0 or not
                    return false;   // If it is the case, it means there are multiple initial states and so, we return false
                nb_initials++;  // If it is not the case, we increment nb_initials
            }
            for (int j = 0; j < NB_LETTER; j++) {   // We will go through all the letters of the alphabet of the original automaton
                nb_transitions[i][j] = 0;       // We set all values of the list to 0
                for (int k = 0; k < NB_TRANSITIONS; k++) {  // We go through all the transitions of the automaton
                    if (TRANSITIONS[k].getSTART() == STATES[i] && TRANSITIONS[k].getLETTER() == (char)(97 + j)) {   // We check if the start state of the studied transition equals the studied state
                        if (nb_transitions[i][j] > 0)   // If it is the case, we check if we already encountered such transition
                            return false;   // If it is the case, it means that there are multiple paths with the same letter from the same state so, we return false
                        nb_transitions[i][j]++;     // If it is not the case, we increment nb_transitions[i][j]
                    }
                }
            }
        }
        return true; // All the tests were validated, so we return true
    }

    /**
     * Determinize the automaton and return its determinized version (which is a new automaton)
     *
     * @return Determinized automaton

    * */
    public Automaton determinize() {
        Transition[] new_transitions = new Transition[0];       // Will contain the transitions of the new automaton
        State[] new_states = new State[0];                      // Will contain the states of the new automaton
        int nb_new_transitions = 0, nb_new_states = 0;          // Allow respectively to keep track of the number of new transitions and states
        StringBuilder state_name = new StringBuilder();         // Will contain the names of the states
        boolean term,                                           // term will be used to determine if an initial state is also final
                found;                                          // found will allow to check a condition that will be detailed later
        int i, j, k, l, m;                                      // Counters for the different loops
        State end_tr;
        String[] current_state_names, next_state_names;
        int min_pos;
        String temp;

        if (isDeterministic())
            System.out.println("This automaton is already deterministic.");
        else if (isSynchronous()) {  // Case where there are no epsilon transitions

            // This part allows to construct the composed initial state of the determinized automaton
            term = false;       // Will determine if the initial state is also final
            for (i = 0; i < NB_STATES; i++) {
                if (STATES[i].isINITIAL()) {
                    if (state_name.length() != 0)
                        state_name.append('.');
                    state_name.append(STATES[i].getNAME());
                    if (STATES[i].isFINAL())     // If one of the original automaton's initial states is also final, then the composed initial state of the determinized automaton will also be final
                        term = true;
                }
            }

            // This part allows to reorder the states composing the composed initial state in the ascending order
            current_state_names = state_name.toString().split("\\.");   // Contains a list of characters, corresponding to the states composing the initial state of the determinized automaton
            for (i = 0; i < current_state_names.length; i++) {
                min_pos = i;
                for (j = i + 1; j < current_state_names.length; j++) {  // Search for the current minimum value in the list
                    if (Integer.parseInt(current_state_names[j]) < Integer.parseInt(current_state_names[min_pos]))
                        min_pos = j;
                }
                if (min_pos != i) { // If the current value is not the minimum value, it will be reversed with the previously found minimum value.
                    temp = current_state_names[i];
                    current_state_names[i] = current_state_names[min_pos];
                    current_state_names[min_pos] = temp;
                }

            }

            new_states = addState(new_states, new State(String.join(".", current_state_names), true, term));    // We add the composed initial state to the list of new_states
            nb_new_states++;    // Increment the number of new states


            i = 0;
            while (i < nb_new_states) { // This loop will run until we are sure there are no new states to add to the determinized automaton
                current_state_names = new_states[i].getNAME().split("\\.");         // We put the names of the states composing the last state we added in the new_state list, so that we can find its associated transitions
                for (j = 0; j < NB_LETTER; j++) {   // This loop will get through all the letters of the alphabet of the original automaton, to find all the transitions of the current new state
                    term = false;  // Determine if the end state of the transition is terminal or not
                    state_name.setLength(0);    // We clear the content of the state name variable, to be able to use it again
                    for (k = 0; k < NB_TRANSITIONS; k++) {  // This loop will get through all the transitions of the existing automaton, to find the ones that start by one of the states composing our current new state
                        for (l = 0; l < current_state_names.length; l++) {  // This loop go through all the states composing the current new state
                            if (TRANSITIONS[k].getSTART().getNAME().equals(current_state_names[l]) && TRANSITIONS[k].getLETTER() == (char) (97 + j)) {  // This condition checks if the start state of the transition we are looking at equals the current state we are looking at
                                next_state_names = state_name.toString().split("\\.");  // Put the list of the states currently composing the new composed state in next_state_names
                                m = 0;
                                found = false;  // Now, we want to check if we already added the state we are looking at to the name of the new composed state or not
                                while (!found && m < next_state_names.length) {
                                    if(TRANSITIONS[k].getEND().getNAME().equals(next_state_names[m]))
                                        found = true;
                                    m++;
                                }
                                if (!found) {
                                    if (state_name.length() != 0)   // If it is not the first state we are adding to the name of the new composed state, we must put a point to separate the states
                                        state_name.append('.');
                                    state_name.append(TRANSITIONS[k].getEND().getNAME());   // This line will add the state we are looking at to the name of the new composed state
                                }
                                if (TRANSITIONS[k].getEND().isFINAL())  // This condition checks if the state we are adding was final or not
                                    term = true;
                            }
                        }
                    }

                    // Now that we have the list of states composing our new composed state, we must order them in the ascending order (it is the same procedure as the one above)
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
                    state_name.append(String.join(".", next_state_names));  // Here, we update the content of state name, to have the states in the ascending order

                    // Now, we want to know what is the end state of the transition we are creating
                    if (state_name.length() != 0) {
                        k = 0;
                        end_tr = null;  // This variable will contain the end state
                        while (end_tr == null && k < nb_new_states) {
                            if (state_name.toString().equals(new_states[k].getNAME()))
                                end_tr = new_states[k];     // Here is the case where the end state already exists in the list new states
                            k++;
                        }
                        if (end_tr == null) {       // Here is the case where we should add the end state to the list new states
                            new_states = addState(new_states, new State(state_name.toString(), false, term));
                            end_tr = new_states[nb_new_states];
                            nb_new_states++;
                        }

                        new_transitions = addTransition(new_transitions, new Transition(new_states[i], (char) (97 + j), end_tr));   // Now we can add the new transition to the list new transitions
                        nb_new_transitions++;   // We shouldn't forget to increment the number of new transitions
                    }
                }
                i++;
            }
            return new Automaton(NB_LETTER, new_states, nb_new_states, new_transitions, nb_new_transitions);    // This line will instantiate and return the determinized version of the original automaton
        }

        else{
            // We first synchronize the automaton, and then we determinize it as any other synchronous automaton
            return synchronize().determinize();
        }
        return this;
    }


    public boolean isComplete() {
        int k;
        boolean found;
        for (int i = 0; i < NB_STATES; i++) {
            for (int j = 0; j < NB_LETTER; j++) {
                k = 0;
                found = false;
                while (!found && k < NB_TRANSITIONS) {
                    if (TRANSITIONS[k].getSTART() == STATES[i] && TRANSITIONS[k].getLETTER() == (char)(97 + j))
                        found = true;
                    k++;
                }
                if (!found)
                    return false;
            }
        }
        return true;
    }

    public Automaton complete() {
        int i, j;
        boolean[][] state_and_letter = new boolean[NB_STATES][NB_LETTER]; // each state as a list of boolean that indicates whether the letter is used or not, first element is for 'a', last is for 'z'
        int nb_missing, nb_transition;
        State[] new_states;
        Transition[] new_transitions;

        if (isComplete()) // check if the automaton is already complete
            System.out.println("This automaton is already complete.");
        else {            // if not create the trash state, its transitions and the missing transitions

            for (i = 0; i < NB_STATES; i++) { // we set all values to false
                for (j = 1; j < NB_LETTER; j++)
                    state_and_letter[i][j] = false;
            }

            nb_missing = NB_STATES * NB_LETTER;
            for (j = 0; j < NB_STATES; j++) {
                for (i = 0; i < NB_TRANSITIONS; i++) {
                    if (TRANSITIONS[i].getSTART() == STATES[j] && !(TRANSITIONS[i].getLETTER() == '*' && !state_and_letter[j][TRANSITIONS[i].getLETTER() - 97])) { // if the state has a transition using a letter we put true in the list because we know it's used
                        state_and_letter[j][TRANSITIONS[i].getLETTER() - 97] = true; // 97 is the int value of the ascii code for "a"
                        nb_missing--;
                    }
                }
            }

            nb_transition = NB_TRANSITIONS; // index for the new transition list
            new_states = new State[NB_STATES + 1]; // we need one more state to add the trash
            new_transitions = new Transition[NB_TRANSITIONS + nb_missing + NB_LETTER]; //already existing transition + missing transition + transition for the trash to trash

            System.arraycopy(STATES, 0, new_states, 0, NB_STATES);
            System.arraycopy(TRANSITIONS, 0, new_transitions, 0, NB_TRANSITIONS);

            new_states[NB_STATES] = new State("Trash", false, false); // creation of trash

            for (i = 0; i < NB_LETTER; i++) { // all possible transitions from trash to trash
                new_transitions[nb_transition] = new Transition(new_states[NB_STATES], (char) (97 + i), new_states[NB_STATES]);
                nb_transition++;
            }

            for (i = 0; i < NB_STATES; i++) { //of course, we won't check trash that was just done
                for (j = 0; j < NB_LETTER; j++) {
                    if (!state_and_letter[i][j]) {
                        new_transitions[nb_transition] = new Transition(STATES[i], (char) (97 + j), new_states[NB_STATES]); // we add the missing transitions
                        nb_transition++;
                    }
                }
            }
            return new Automaton(NB_LETTER, new_states, NB_STATES + 1, new_transitions, new_transitions.length); // return the new Automaton
        }
        return this;
    }


    public boolean isMinimized() {
        State[][] groups, groups_temp;
        String[] groups_name, groups_name_temp;
        int[]  groups_size, groups_size_temp;
        int nb_groups, nb_groups_temp;
        StringBuilder state_type;
        boolean found;
        int i, j, k, l, m;

        if (!isDeterministic() || !isComplete())
            return false;
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
                    for (j = 0; j < NB_LETTER; j++) {
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
                        for (k = 0; k < NB_LETTER; k++) {
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
                                if (groups_name[k].equals(state_type.toString()) && groups[k][0].isFINAL() == groups[i][j].isFINAL()) {
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
            return nb_groups == NB_STATES;
        }
    }

    private State getTransition(State state, char letter) {
        for (Transition tr: TRANSITIONS)
            if (tr.getSTART() == state && tr.getLETTER() == letter)
                return tr.getEND();
        return null;
    }

    public Automaton minimize() {
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

        if (!isDeterministic())
            System.out.println("This automaton is not deterministic.");
        else if (!isComplete())
            System.out.println("This automaton is not complete.");
        else if (isMinimized())
            System.out.println("This automaton is already minimized.");
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
                    for (j = 0; j < NB_LETTER; j++) {
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
                        for (k = 0; k < NB_LETTER; k++) {
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
                                if (groups_name[k].equals(state_type.toString()) && groups[k][0].isFINAL() == groups[i][j].isFINAL()) {
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

            new_states = new State[nb_groups];
            new_transitions = new Transition[nb_groups * NB_LETTER];
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
                for (j = 0; j < NB_LETTER; j++) {
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
                    new_transitions[i * NB_LETTER + j] = new Transition(new_states[i], (char)(97 + j), end);
                }
            }

            return new Automaton(NB_LETTER, new_states, nb_groups, new_transitions, nb_groups * NB_LETTER);
            }
        return this;
    }


    public Automaton complement() {
        State[] new_states = new State[NB_STATES];
        Transition[] new_transitions = new Transition[NB_TRANSITIONS];
        State start_tr, end_tr;
        int i, j;

        for (i = 0; i < NB_STATES; i++){
            new_states[i] = new State(STATES[i].getNAME(), STATES[i].isINITIAL(), !STATES[i].isFINAL());
        }

        for (i = 0; i < NB_TRANSITIONS; i++) {
            j = 0;
            start_tr = null;
            end_tr = null;
            while ((start_tr == null || end_tr == null) && j < NB_STATES) {
                if (TRANSITIONS[i].getSTART().getNAME().equals(new_states[j].getNAME()))
                    start_tr = new_states[j];
                if (TRANSITIONS[i].getEND().getNAME().equals(new_states[j].getNAME()))
                    end_tr = new_states[j];
                j++;
            }
            new_transitions[i] = new Transition(start_tr, TRANSITIONS[i].getLETTER(), end_tr);
        }

        return new Automaton(NB_LETTER, new_states, NB_STATES, new_transitions, NB_TRANSITIONS);
    }


    private boolean testWordByState(String word, State state, Transition[] old_transitions) {
        /*
        Return true is there is at least path starting from the state 'state' that recognize the word and false otherwise
        */
        int i, j;
        boolean found;

        if (word.isEmpty())   // recognize the word if we reach its end and if the current state is final
            return state.isFINAL();
        for (i = 0; i < NB_TRANSITIONS; i++) {
            // if there are transitions starting from this state with the fist character of the word, check if at least
            // one of the end states of those transitions recognize the rest of the word, if yes the word is recognized
            if (TRANSITIONS[i].getLETTER() == word.charAt(0) && TRANSITIONS[i].getSTART() == state)
                if (testWordByState(word.substring(1), TRANSITIONS[i].getEND(), addTransition(old_transitions, TRANSITIONS[i])))
                    return true;
            // if there are epsilon transitions starting from this state, check if the at least one of the end states
            // of those transitions recognize the word, if yes the word is recognized
            if (TRANSITIONS[i].getLETTER() == '*' && TRANSITIONS[i].getSTART() == state) {
                j = old_transitions.length - 1;
                found = false;                   // check if we are not looping on the epsilon transitions
                while (!found && j >= 0 && old_transitions[j].getLETTER() == '*') {
                    if (TRANSITIONS[i].getEND() == old_transitions[j].getSTART())
                        found = true;
                    j--;
                }
                if (!found && testWordByState(word, TRANSITIONS[i].getEND(), addTransition(old_transitions, TRANSITIONS[i])))
                    return true;
            }
        }
        return false; // return false if none of the paths starting from this state recognize the word
    }

    public boolean testWord(String word) {
        /*
        Return true if the automaton recognize the word and false otherwise
        */
        for (int i = 0; i < NB_STATES; i++) {
            if (STATES[i].isINITIAL() && testWordByState(word, STATES[i], new Transition[0]))
                return true;    // recognize the word if at least one of the initial states recognize it
        }
        return false;    // If none of the initial states recognize the word then it's not recognized by the automaton
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Automata:\n");
        sb.append(NB_STATES);
        sb.append(" states:\n");
        for (State state: STATES) {
            sb.append(state);
            sb.append("\n");
        }
        sb.append(NB_TRANSITIONS);
        sb.append(" transitions:\n");
        for (Transition tr: TRANSITIONS) {
            sb.append(tr);
            sb.append("\n");
        }
        return sb.toString();
    }
}