package com.company;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Object that represents an automaton
 */
public class Automaton {
    /**
     * Number of letters composing the alphabet of the automaton
     * */
    private final int NB_LETTER;

    /**
     * List of states of the automaton
     * */
    private final State[] STATES;
    /**
     * Number of states of the automaton
     * */
    private final int NB_STATES;

    /**
     * List of transitions of the automaton
     * */
    private final Transition[] TRANSITIONS;
    /**
     * Number of transitions of the automaton
     * */
    private final int NB_TRANSITIONS;


    /**
     * Initialize an Automaton object from a text file
     *
     * @param filepath The path of the text file containing the automaton
     * */
    public Automaton(String filepath) {
        // The variable file is a list of string where each element of the list is a line of the file
        String[] file = getFileContent(filepath);

        if (file != null) {     // Initialize the automaton from the file only if 'filepath' have been found
            // The first line (index 0) of the file is the number of letters in the alphabet of the automaton
            NB_LETTER = Integer.parseInt(file[0]);

            // The second line (index 1) of the file contains the number of states of the automaton (NB_STATES)
            NB_STATES = Integer.parseInt(file[1]);
            STATES = new State[NB_STATES];          // Create the list of states of the automaton
            createStates(file);         // Initialize the list of states from the content of the file

            // The fifth line (index 4) of the file contains the number of transitions of the automaton (NB_TRANSITIONS)
            NB_TRANSITIONS = Integer.parseInt(file[4]);
            TRANSITIONS = new Transition[NB_TRANSITIONS];   // Create the list of transitions of the automaton
            createTransitions(file);    // Initialize the list of transitions from the content of the file
        }
        else {      // If 'file' is null (i.e. 'filepath' not found), create an empty automaton instead
            NB_LETTER = 0;

            NB_STATES = 0;
            STATES = new State[0];

            NB_TRANSITIONS = 0;
            TRANSITIONS = new Transition[0];
        }
    }

    /**
     * Initialize an Automaton object from all its attributes
     *
     * @param nb_letter The number of letters composing the alphabet of the automaton
     * @param states The list of states of the automaton
     * @param nb_states The number of states of the automaton
     * @param transitions The list of transitions of the automaton
     * @param nb_transitions The number of transitions of the automaton
     * */
    public Automaton(int nb_letter, State[] states, int nb_states, Transition[] transitions, int nb_transitions) {
        // Initialize all the attributes of the automaton to their values passed as argument
        NB_LETTER = nb_letter;
        STATES = states;
        NB_STATES = nb_states;
        TRANSITIONS = transitions;
        NB_TRANSITIONS = nb_transitions;
    }

    /**
     * Get the whole content of the file passed as parameter
     *
     * @param filepath The path of the file
     *
     * @return a string containing all the content of the file or null if an error occur during the file importation
     * */
    private String[] getFileContent(String filepath) {
        try {       // Secure this portion of the code to handle the errors that can happen during its execution
            // If no error, return the content of the file as a list of string (each element is one line of the file)
            return Files.readString(Path.of(filepath)).split("\r\n");
        }
        catch (IOException exception) {     // Display an error and return value null if the file importation fail
            System.out.println("Error file not found");
            return null;
        }
    }

    /**
     * Initialize the list of states of the automaton from the file
     *
     * @param file The file containing the automaton as a list of string (each element of the list is one line)
     * */
    private void createStates(String[] file) {
        boolean is_initial, is_final;   // store respectively if the state that is going to be created is initial/final
        String name;    // Store the name of the state that is going to be created
        // The third line (index 2) of the file contains the list of initial states of the automaton
        String[] initials = file[2].split(" ");
        // The third line (index 2) of the file contains the list of final states of the automaton
        String[] finals = file[3].split(" ");

        for (int i = 0; i < NB_STATES; i++) {
            is_initial = false;     // A state is by default considered as non-initial
            is_final = false;       // A state is by default considered as non-final
            name = String.valueOf(i);       // The name of the state is his number, from 0 to 'NB_STATES' - 1

            // If we found our state in the list of initial states, the state is initial so 'is_initial' becomes true
            for (int j = 1; j < Integer.parseInt(initials[0]) + 1; j++)
                if (name.equals(initials[j]))  // Compare if the name of our state is equal to any of the initial states
                    is_initial = true;

            // If we found our state in the list of final states, the state is final so 'is_final' becomes true
            for (int j = 1; j < Integer.parseInt(finals[0]) + 1; j++)
                if (name.equals(finals[j]))     // Compare if the name of our state is equal to any of the final states
                    is_final = true;

            // We now have all the information about the state, we can create it and put it in the list
            STATES[i] = new State(name, is_initial, is_final);
        }
    }

    /**
     * Find a state of the automaton by its name
     *
     * @param name The name of the researched state
     *
     * @return the state that have the desired name
     * */
    private State getStateFromName(String name) {
        // Search through all the states if one have the desired name
        for (State state: STATES)
            if (name.equals(state.getNAME()))   // Compare the name of each state with the name we are searching
                return state;       // If a state with the good name is found then return it
        return null;        // If the desired state haven't been found (no state have this name) then return null value
    }

    /**
     * Initialize the list of transitions of the automaton from the file
     *
     * @param file The file containing the automaton as a list of string (each element of the list is one line)
     * */
    private void createTransitions(String[] file) {
        String tr;          // Store the line of the file containing the description of the transition to create
        String[] states_names;  // Store the names of the start and end states of the transition
        State start, end; // Store respectively the start and end states of the transition
        char letter;    // Store the letter recognized by the transition

        for (int i = 0; i < NB_TRANSITIONS; i++){
            tr = file[i + 5];       // The transitions are stored starting from the line 6 of the file (index 5+)
            // Split the string of the transition around the letter recognized, to have separately the name of the
            // start state and the name of the end state
            states_names = tr.split("[a-z*]");
            // Find the already created state that correspond to the name of the start state of the transition
            start = getStateFromName(states_names[0]);
            // The character located directly after the name of the start state is the letter recognized
            letter = tr.charAt(states_names[0].length());
            // Find the already created state that correspond to the name of the end state of the transition
            end = getStateFromName(states_names[1]);

            // We now have all the information about the transition, we can create it and put it in the list
            TRANSITIONS[i] = new Transition(start, letter, end);
        }
    }


    /**
     * Add a transition to a list of transitions
     *
     * @param old_tr The old list of transitions
     * @param tr The transition to add to the list
     *
     * @return a new list of transitions containing all the elements of the old list and the new transition
     * */
    private Transition[] addTransition(Transition[] old_tr, Transition tr) {
        // Create a new list that have the space to contain one more transition
        Transition[] new_tr = new Transition[old_tr.length + 1];
        // Copy the content of the old list in the new one
        System.arraycopy(old_tr, 0, new_tr, 0, old_tr.length);
        new_tr[old_tr.length] = tr;     // Add the new transition at the end of the new list
        return new_tr;      // Return the new list of transitions with the added transition
    }

    /**
     * Add a state to a list of states
     *
     * @param old_states The old list of states
     * @param state The state to add to the list
     *
     * @return a new list of states containing all the elements of the old list and the new state
     * */
    private State[] addState(State[] old_states, State state) {
        // Create a new list that have the space to contain one more state
        State[] new_states = new State[old_states.length + 1];
        // Copy the content of the old list in the new one
        System.arraycopy(old_states, 0, new_states, 0, old_states.length);
        new_states[old_states.length] = state;      // Add the new state at the end of the new list
        return new_states;      // Return the new list of states with the added state
    }


    /**
     * Check if the automaton is synchronous or not
     *
     * @return true if the automaton is synchronous and false otherwise
     */
    public boolean isSynchronous() {
        // To find if an automaton is synchronous or not, we need to loop through all its transition and check
        // if there are epsilon transitions
        for(Transition tr: TRANSITIONS)
            if (tr.getLETTER() == '*') {    // Check if the transition is an epsilon transition
                // If at least one epsilon transition have been found, the automaton is asynchronous so return false
                return false;
            }
        return true;    // If no epsilon transition have been found, the automaton is synchronous so return true
    }

    /**
     * Get the epsilon closure of a state recursively
     *
     * @param state The state to analyse
     * @param old_closure The names of the states that are already in the closure
     *
     * @return a string containing all the states in the epsilon closure of this state
     */
    private String epsilonClosure(State state, String old_closure) {
        // Store the epsilon closure of the state as the names of all states composing this closure separated
        // by '.' (initialized with the existing input closure)
        StringBuilder closure = new StringBuilder(old_closure);
        // Store the names of the states composing the epsilon closure as a list of strings (each element contain
        // the name of one state)
        String[] state_names;
        boolean found;      // Store the result of a condition that we will explain later
        int i;      // Counter for the loop

        // If there are already states in the closure, add a dot to separate the newly added state
        if (closure.length() != 0)
            closure.append(".");
        closure.append(state.getNAME());    // Add the name of the current state to it's closure

        for (Transition tr: TRANSITIONS) {
            // Search only the epsilon transitions starting from our current state
            if(tr.getLETTER() == '*' && tr.getSTART() == state){
                // Get the list of names of the states that are already in the closure
                state_names = closure.toString().split("\\.");
                i = 0;
                found = false;      // We consider by default that the end state is not in the epsilon closure
                while (!found && i < state_names.length) {
                    // Search if the end state of the epsilon transition is already in the epsilon closure
                    if (tr.getEND().getNAME().equals(state_names[i])) {
                        // If the end state is already in the epsilon closure then set found at true to save the result
                        found = true;
                    }
                    i++;
                }
                // If the end state of the transition is not in the epsilon closure (i.e. found is false),
                // we add the epsilon closure of this state to our epsilon closure
                if (!found)
                    closure = new StringBuilder(epsilonClosure(tr.getEND(), closure.toString()));
            }
        }
        // Once we checked all the transitions, we can finally convert our epsilon closure as a string and return it
        return closure.toString();
    }

    /**
     * Allows to get the synchronous version of a given asynchronous automaton
     *
     * @return An equivalent new synchronous automaton
     */
    public Automaton synchronize() {
        Transition[] new_transitions = new Transition[0];   // The list of transitions of the new automaton
        State[] new_states = new State[0];          // The list of states of the new automaton
        int nb_new_transitions = 0, nb_new_states = 0;    // Store respectively the number of new transitions and states
        boolean initial;                // Determine if the new state we are crating is initial
        boolean found;                  // Store the result of a condition that we will explain later
        State start_tr, end_tr;     // Store respectively the start state and end state of the transition to create
        String[] closure;         // Will contain the epsilon closures of the different states
        int i, j;             // Counters for the different loops

        if (isSynchronous())        // If the original automaton is already synchronous then do nothing
            System.out.println("This automaton is already synchronous");
        else{       // Else (if it's asynchronous), synchronize it
            // Loop through each state of the original to create their equivalent in the synchronized automaton
            for (State state: STATES) {
                // If the current state is already initial, we don't have to do anything, just store that the state
                // is initial
                if (state.isINITIAL())
                    initial = true;
                // Else (if the state is not initial), check if the state is in the closure of at least one of the
                // original initial states, if yes the state have to be initial in the synchronized automaton
                else {
                    i = 0;
                    initial = false;        // by default the state is non-initial here
                    // Loop through all the states to compute the epsilon closure of the initial states
                    while (!initial && i < NB_STATES) {
                        if(STATES[i].isINITIAL()) {      // Check if the state is initial
                            // If the state is initial, compute its epsilon closure and search if our current state
                            // is in it
                            closure = epsilonClosure(STATES[i], "").split("\\.");
                            j = 0;
                            // Loop through all the states contained in the epsilon closure to check if our current
                            // state is one of them
                            while (!initial && j < closure.length) {
                                // If the current state is in the epsilon closure, then it has to be initial
                                if (state.getNAME().equals(closure[j]))
                                    initial = true;     // Store that the current state has to be initial
                                j++;
                            }
                        }
                        i++;
                    }
                }

                // Now that we know if it has to be initial or not, we can create the new state and add it to the list
                new_states = addState(new_states, new State(state.getNAME(), initial, state.isFINAL()));
                nb_new_states++;    // Store that now there is one more new state
            }

            // Add the non-epsilon transitions of the original automaton in the new transitions one by one
            for (Transition tr: TRANSITIONS) {
                // Add the current transition only if it is not an epsilon transition
                if (tr.getLETTER() != '*') {
                    i = 0;
                    start_tr = null;
                    end_tr = null;
                    // Loop through the state to find the new states corresponding to the start and end states of the
                    // transition we want to create
                    while ((start_tr == null || end_tr == null) && i < nb_new_states) {
                        if (tr.getSTART().getNAME().equals(new_states[i].getNAME())) {
                            // When the new state corresponding to the start state is found store it in 'start_tr'
                            start_tr = new_states[i];
                        }
                        if (tr.getEND().getNAME().equals(new_states[i].getNAME())) {
                            // When the new state corresponding to the end state is found store it in 'end_tr'
                            end_tr = new_states[i];
                        }
                        i++;
                    }
                    // Now that we have all the information about the new transition, we can create it and add it to
                    // the list of new transitions
                    new_transitions = addTransition(new_transitions, new Transition(start_tr, tr.getLETTER(), end_tr));     // We add the non-epsilon transition to the list new_transitions
                    nb_new_transitions++;   // Store that now we have one more transition
                }
            }

            // Loop through all the states to replace all the epsilon transitions by non-epsilon ones
            for (i = 0; i < nb_new_states; i++) {
                // Get the epsilon closure of the current state
                closure = epsilonClosure(STATES[i], "").split("\\.");
                // Loop through the transitions to find all the non-epsilon transitions that ends at the current state
                for (Transition tr: TRANSITIONS) {
                    // Check if the current transition ends at the current state and recognize a non-epsilon letter
                    if (tr.getEND().getNAME().equals(new_states[i].getNAME()) && tr.getLETTER() != '*') {
                        // Loop through all the state names contained in the epsilon closure of the current states to
                        // create a new transition for each one of them
                        for (String name: closure) {
                            j = 0;
                            found = false;      // Store if the transition we want to create already exists or not
                            // Loop through all the new transitions to search if the transition we want to create
                            // already exists
                            while (!found && j < nb_new_transitions) {
                                // Check if the current new transition is the same as the one we want to create
                                if (new_transitions[j].getSTART().getNAME().equals(tr.getSTART().getNAME())
                                        && new_transitions[j].getLETTER() == tr.getLETTER()
                                        && new_transitions[j].getEND().getNAME().equals(name))
                                    found = true;   // Store that the transition already exists
                                j++;
                            }

                            // If the transitions doesn't already exist (i.e. if found is still false), then create it
                            if (!found) {
                                j = 0;
                                start_tr = null;
                                end_tr = null;
                                // Loop through the state to find the new states corresponding to the start and end
                                // states of the transition we want to create
                                while ((start_tr == null || end_tr == null) && j < nb_new_states) {
                                    if (tr.getSTART().getNAME().equals(new_states[j].getNAME())) {
                                        // When the new state corresponding to the start state is found store
                                        // it in 'start_tr'
                                        start_tr = new_states[j];
                                    }
                                    if (name.equals(new_states[j].getNAME())) {
                                        // When the new state corresponding to the end state is found store it
                                        // in 'end_tr'
                                        end_tr = new_states[j];
                                    }
                                    j++;
                                }
                                // Now that we have all the information about the new transition, we can create it and add it to
                                // the list of new transitions
                                new_transitions = addTransition(new_transitions, new Transition(start_tr, tr.getLETTER(), end_tr));
                                nb_new_transitions++;   // Store that now we have one more transition
                            }
                        }
                    }
                }
            }

            // We now have all the information about the equivalent synchronous automaton, we can create and return it
            return new Automaton(NB_LETTER, new_states, nb_new_states, new_transitions, nb_new_transitions);
        }
        // Return by default the original automaton if nothing else have been returned
        return this;
    }


    /**
     * Check if a given automaton is deterministic or not
     *
     * @return boolean
     */
    public boolean isDeterministic() {
        int nb_initials = 0;    // Store the number of initial states of the automaton
        // Store the number of transitions for each state of the automaton and each letter of its alphabet
        int[][] nb_transitions = new int[NB_STATES][NB_LETTER];

        if (!isSynchronous())   // If the automaton is asynchronous, it can't be deterministic, so return false
            return false;

        // Check all the states of the automaton one by one
        for (int i = 0; i < NB_STATES; i++) {
            if (STATES[i].isINITIAL()) {    // Check if the current state is initial
                if (nb_initials != 0) {   // Check if there are already other initial states
                    // If there are already other initial states, this means that there are multiple initial states
                    // (= automaton is non-deterministic), so return false
                    return false;
                }
                nb_initials = 1;  // Else store that there is one initial state
            }

            // For each state of the automaton, check if there is at most only one transition per letter
            for (int j = 0; j < NB_LETTER; j++) {
                // We initialize the number of transitions of for this state and this letter at 0
                nb_transitions[i][j] = 0;
                for (int k = 0; k < NB_TRANSITIONS; k++) {  // Search through all the transitions of the automaton
                    // Search a transition starting from the current state and recognizing the current letter
                    if (TRANSITIONS[k].getSTART() == STATES[i] && TRANSITIONS[k].getLETTER() == (char)(97 + j)) {
                        // Check if there are already transitions starting from this state and recognizing this letter
                        if (nb_transitions[i][j] != 0) {
                            // If yes, it means that there are multiple paths starting from the same state with the
                            // same letter (= automaton is non-deterministic), so return false
                            return false;
                        }
                        // Else store that there is one transition for this state and this letter
                        nb_transitions[i][j] = 1;
                    }
                }
            }
        }
        return true; // If all the tests have been validated, then return true
    }

    /**
     * Sort a list of state names in ascending order
     *
     * @param states_names A list of state names (as strings)
     *
     * @return the sorted version (in ascending order) of the list of states
     */
   private String[] sortStateList(String[] states_names) {
       int min_pos;     // Store the index of the current minimum of the list
       String temp;     // Store temporarily the name of a state when inverting two of them in the list

       // Loop through all positions of the list to sort it element by element
       for (int i = 0; i < states_names.length; i++) {
           min_pos = i;     // initialize the minimum as the first element of the remaining unsorted list
           // Search for the minimum value in the remaining unsorted list
           for (int j = i + 1; j < states_names.length; j++)
               if (Integer.parseInt(states_names[j]) < Integer.parseInt(states_names[min_pos]))
                   min_pos = j;
           // If the current value is not the minimum value of the rest of the unsorted list, reverse it
           // with the previously found minimum value.
           if (min_pos != i) {
               temp = states_names[i];
               states_names[i] = states_names[min_pos];
               states_names[min_pos] = temp;
           }
       }
       // return the list once it's totally sorted
       return states_names;
   }

    /**
     * Determinize the automaton
     *
     * @return a deterministic equivalent of the automaton
     * */
    public Automaton determinize() {
        Transition[] new_transitions = new Transition[0];  // Store the transitions of the new automaton
        State[] new_states = new State[0];                 // Store the states of the new automaton
        int nb_new_transitions = 0;                        // Store the number of new transitions
        int nb_new_states = 0;                             // Store the number of new states
        StringBuilder state_name = new StringBuilder();    // Store the names of the state when creating it
        boolean term;                                      // Store if the state we are creating is final
        boolean found;                                     // Store  the result of a condition that we will detail later
        int i, j, k, l, m;                                 // Counters for the different loops
        State end_tr;                                      // Store the end state of the transition when creating it
        String[] current_state_names;                      // Store the names of the states composing the current state
        String[] next_state_names;                         // Store the names of the states composing the next state


        if (isDeterministic())      // Do nothing if the automaton is already deterministic
            System.out.println("This automaton is already deterministic.");
        else if (isSynchronous()) {  // Case where the automaton is synchronous (i.e. no epsilon transitions)

            // Construct the composed initial state of the deterministic automaton
            term = false;       // Determine if the initial state is also final, by default it's not
            for (i = 0; i < NB_STATES; i++) {
                if (STATES[i].isINITIAL()) {   // Add all the original initial states to our new composed initial state
                    // If there are already states in our composed state name, add a dot to separate the newly added state
                    if (state_name.length() != 0)
                        state_name.append('.');
                    // Add the name of the state to the name of the composed initial state
                    state_name.append(STATES[i].getNAME());
                    // If at least one of the original initial states is also final, then the composed initial state
                    // of the deterministic automaton will also be final
                    if (STATES[i].isFINAL())
                        term = true;
                }
            }

            // Sort the names of states composing the composed initial state in the ascending order
            state_name = new StringBuilder(String.join(".", sortStateList(state_name.toString().split("\\."))));

            // We now have all the information about the composed initial state, we can create it and add it to the list
            new_states = addState(new_states, new State(state_name.toString(), true, term));
            nb_new_states++;    // Store that there is now one more state


            i = 0;
            // Execute until all necessary states have been added to the deterministic automaton
            while (i < nb_new_states) {
                // Get the names of all states composing the current new state
                current_state_names = new_states[i].getNAME().split("\\.");
                // Go through all the letters of the alphabet of the automaton, to find and create all the transitions
                // of the current new state
                for (j = 0; j < NB_LETTER; j++) {
                    term = false;  // Determine if the end state of the transition is final or not
                    // Clear the content of the 'state_name' variable, to store the name of the end state of the
                    // transition we are creating
                    state_name.setLength(0);
                    // Go through all the transitions of the original automaton to find the ones that start by one of
                    // the states composing our current new state
                    for (k = 0; k < NB_TRANSITIONS; k++) {
                        // Go through all the states composing the current new state to find if the current transition
                        // start by one of them
                        for (l = 0; l < current_state_names.length; l++) {
                            // Check if the current transition start from one of the states composing the current new
                            // state and recognize the current letter
                            if (TRANSITIONS[k].getSTART().getNAME().equals(current_state_names[l]) && TRANSITIONS[k].getLETTER() == (char) (97 + j)) {
                                // Get the names of all states composing the end state of the transition we are creating
                                next_state_names = state_name.toString().split("\\.");
                                // Check if the end state of the current original transition is equal to one of the
                                // names of the states composing the new composed state
                                m = 0;
                                // Determine if the end state of the current original transition is already added to the
                                // name of the new composed state
                                found = false;
                                while (!found && m < next_state_names.length) {
                                    if (TRANSITIONS[k].getEND().getNAME().equals(next_state_names[m])) {
                                        // If the end state of the current original transition is already added to the
                                        // name of the new composed state, save it in variable 'found'
                                        found = true;
                                    }
                                    m++;
                                }
                                // If the end state of the current original transition haven't been found in the
                                // name of the new composed state, add it to this new composed state
                                if (!found) {
                                    // If there are already states in our composed state name, add a dot to separate
                                    // the newly added state
                                    if (state_name.length() != 0)
                                        state_name.append('.');
                                    // Add the name of the end state of the current original transition to the name of
                                    // new composed state (the end state of the new transition)
                                    state_name.append(TRANSITIONS[k].getEND().getNAME());
                                }
                                // If at least one of the original states composing our composed state is also final,
                                // then this composed state will also be final
                                if (TRANSITIONS[k].getEND().isFINAL())
                                    term = true;
                            }
                        }
                    }

                    // Sort the names of states composing the composed initial state in the ascending order to be sure
                    // that we won't have 2 composed states composed of the same original states in different order
                    state_name = new StringBuilder(String.join(".", sortStateList(state_name.toString().split("\\."))));

                    // If our composed state is not empty, we have to add the transition (and the end state if it isn't
                    // already created)
                    if (state_name.length() != 0) {
                        k = 0;
                        end_tr = null;  // Contain the end state of the new transition we will create
                        while (end_tr == null && k < nb_new_states) {
                            // Search if this composed state have already been created
                            if (state_name.toString().equals(new_states[k].getNAME())) {
                                // If the state already exist, store it in 'end_tr' to then create the new transition
                                end_tr = new_states[k];
                            }
                            k++;
                        }
                        // If the composed state don't already exist, it needs to be created
                        if (end_tr == null) {
                            // Create the new composed state and add it to the list of new states
                            new_states = addState(new_states, new State(state_name.toString(), false, term));
                            // Store the newly created state in 'end_tr' to then create the new transition
                            end_tr = new_states[nb_new_states];
                            nb_new_states++; // Store that now we have one more new state
                        }

                        // We now have all the information about the new transition, we can create it and add it to
                        // the list
                        new_transitions = addTransition(new_transitions, new Transition(new_states[i], (char) (97 + j), end_tr));
                        nb_new_transitions++;   // Store that now we have one more transition
                    }
                }
                i++;
            }

            // We now have all the information about the deterministic automaton equivalent to the original one,
            // we can create it and return it
            return new Automaton(NB_LETTER, new_states, nb_new_states, new_transitions, nb_new_transitions);
        }

        else{
            // If the automaton is asynchronous (i.e. there are epsilon transitions), we first synchronize the
            // automaton, and then we determinize it as any other synchronous automaton before returning it
            return synchronize().determinize();
        }
        // Return by default the original automaton if nothing else have been returned
        return this;
    }


    /**
     * Check if the automaton is complete or not
     *
     * @return true if the automaton is complete and false otherwise
     */
    public boolean isComplete() {
        boolean found;      // Store the result of a condition that we will detail later
        int k;      // Counter for the while loop

        // Loop through all states to check if they are all complete (= have transitions for each letter)
        for (int i = 0; i < NB_STATES; i++) {
            // Loop through each letter to check if the state have transition for each of those letters
            for (int j = 0; j < NB_LETTER; j++) {
                // Loop through the transition to check if at least one of them start by the current state and
                // recognize the current letter
                k = 0;
                // Determine if the current state have at least one transition for the current letter, by default
                // it's false
                found = false;
                while (!found && k < NB_TRANSITIONS) {
                    if (TRANSITIONS[k].getSTART() == STATES[i] && TRANSITIONS[k].getLETTER() == (char)(97 + j)) {
                        // Save the result in variable 'found' if the current state have at least one transition for
                        // the current letter
                        found = true;
                    }
                    k++;
                }
                // If the current state don't have any transition for the current letter, then it's not complete,
                // so return false
                if (!found)
                    return false;
            }
        }
        return true; // If all the states are complete, then the automaton is complete, so return true
    }

    /**
     * Complete the automaton if it's not already the case
     *
     * @return a complete equivalent of the automaton
     */
    public Automaton complete() {
        // Each state as a list of boolean that indicates whether the letter is used by at least one transition or not
        boolean[][] state_and_letter = new boolean[NB_STATES][NB_LETTER];
        State[] new_states;         // The new list of state
        Transition[] new_transitions;       // The new list of transitions
        int nb_new_transition;      // The number of new transitions
        int nb_missing;     // The total number of missing transitions
        int i, j;       // Counters for the loops


        if (isComplete()) // If the automaton is already complete, then do nothing
            System.out.println("This automaton is already complete.");
        else {      // Else complete it by creating the trash state, its transitions and the missing transitions

            // By default, we set all the letters for all words as not used
            for (i = 0; i < NB_STATES; i++) {
                for (j = 1; j < NB_LETTER; j++)
                    state_and_letter[i][j] = false;
            }

            // We initialize the number of missing transition as if all transitions were missing
            nb_missing = NB_STATES * NB_LETTER;
            for (j = 0; j < NB_STATES; j++) {
                for (i = 0; i < NB_TRANSITIONS; i++) {
                    // if the current state has a non-epsilon transition using a letter that is still classed as
                    // non-used, we put true in the list to say that now the letter is used (for this state)
                    if (TRANSITIONS[i].getSTART() == STATES[j] && !(TRANSITIONS[i].getLETTER() == '*' && !state_and_letter[j][TRANSITIONS[i].getLETTER() - 97])) {
                        // 97 is the int value of the ascii code for character 'a', so 'a' - 97 = 0, 'b' - 97 = 1 ...
                        state_and_letter[j][TRANSITIONS[i].getLETTER() - 97] = true;
                        nb_missing--;       // We store that there is one transition less missing now
                    }
                }
            }

            new_states = new State[NB_STATES + 1]; // We need one more state to add the trash state
            // Copy all the old states to the new list of states
            System.arraycopy(STATES, 0, new_states, 0, NB_STATES);
            // Create the trash state and add it at the end of the new list of states
            new_states[NB_STATES] = new State("T", false, false);

            // already existing transition (not modified) + missing transition + transition from trash to trash
            new_transitions = new Transition[NB_TRANSITIONS + nb_missing + NB_LETTER];
            // Copy all the old transitions to the new list of transitions
            System.arraycopy(TRANSITIONS, 0, new_transitions, 0, NB_TRANSITIONS);
            nb_new_transition = NB_TRANSITIONS; // Save that there is now as much new transitions as old ones

            // Add all possible transitions from trash to trash
            for (i = 0; i < NB_LETTER; i++) {
                // Create a transition from trash to trash recognizing the current letter and add it to the list of
                // new transitions
                new_transitions[nb_new_transition] = new Transition(new_states[NB_STATES], (char) (97 + i), new_states[NB_STATES]);
                nb_new_transition++;
            }

            for (i = 0; i < NB_STATES; i++) { // Loop only through original states, as trash have already been done
                for (j = 0; j < NB_LETTER; j++) {
                    if (!state_and_letter[i][j]) {
                        // we add the missing transitions, all directed to the trash
                        new_transitions[nb_new_transition] = new Transition(STATES[i], (char) (97 + j), new_states[NB_STATES]);
                        nb_new_transition++;        // Store that there is now one more transition
                    }
                }
            }

            // We now have all the information about the completed automaton, we can create it and return it
            return new Automaton(NB_LETTER, new_states, NB_STATES + 1, new_transitions, new_transitions.length); // return the new Automaton
        }
        // Return by default the original automaton if nothing else have been returned
        return this;
    }


    /**
     * Find the end state of the transition starting from a specific state and with a specific letter
     *
     * @param state The start state of the transition
     * @param letter The letter of the transition
     *
     * @return the end state of the transition
     */
    private State getTransition(State state, char letter) {
        // Loop through all the transitions of the automaton to search for the one that have the desired start state
        // and the desired recognized letter (assuming that the automaton is deterministic)
        for (Transition tr: TRANSITIONS) {
            // If the matching transition is found (good start state and recognized letter), then return its end state
            if (tr.getSTART() == state && tr.getLETTER() == letter)
                return tr.getEND();
        }
        return null;        // If no transition have been found then return null
    }

    /**
     * Check if the automaton is minimal or not
     *
     * @return true if the automaton is minimal and false otherwise
     */
    public boolean isMinimal() {
        // Respectively the table containing the list of states composing each group and a temporary version used to
        // store the value of this variable when modifying the groups
        State[][] groups, groups_temp;
        // Respectively the list containing the names of the groups and a temporary version used to store the value
        // of this variable when modifying the groups
        String[] groups_name, groups_name_temp;
        // Respectively the list containing the number of states in each group and a temporary version used to store
        // the value of this variable when modifying the groups
        int[]  groups_size, groups_size_temp;
        // Respectively the number of group and a temporary version used to store the value of this variable when
        // modifying the groups
        int nb_groups, nb_groups_temp;
        // The type of the state used to make the groups (the states with same type are in the same groups)
        StringBuilder state_type;
        boolean found;      // Store the result of a condition that we will detail later
        int i, j, k, l, m;      // Counters for the loops

        // If the automaton is not complete deterministic then it can't be minimal, so return false
        if (!isDeterministic() || !isComplete())
            return false;
        else {
            // initialise the variables
            nb_groups = 0;
            groups = new State[NB_STATES][NB_STATES];
            groups_name = new String[NB_STATES];
            groups_size = new int[NB_STATES];
            groups_temp = new State[NB_STATES][NB_STATES];
            groups_name_temp = new String[NB_STATES];
            groups_size_temp= new int[NB_STATES];
            state_type = new StringBuilder();

            // Add the states into the first 2 groups - terminal (T) or non-terminal (NT) - one by one
            for (i = 0; i < NB_STATES; i++) {
                // Clear the content of the StringBuilder to be able to make a new state
                state_type.setLength(0);
                if (STATES[i].isFINAL())        // If the state is final, set its type as terminal (T)
                    state_type.append("T");
                else                // Else (if the state is not final), set its type as non-terminal (NT)
                    state_type.append("NT");

                j = 0;
                // Determine if the group corresponding to the current state type already exists, by default it's false
                found = false;
                // Loop through all the groups and search if the group corresponding to the type of the current state
                // already exist
                while (!found && j < nb_groups) {
                    // If the group corresponding to the type of the current state is found, then add the current state
                    // to this group
                    if (state_type.toString().equals(groups_name[j])) {
                        groups[j][groups_size[j]] = STATES[i];      // Add the current state at the end of the group
                        groups_size[j]++;       // Store that now there is one more state in this group
                        found = true;       // Store that the group of the current state have already been found
                    }
                    j++;
                }
                // If the group corresponding to the type of the current state haven't been found (i.e. 'found' is
                // still false), create a new group for the current state
                if (!found) {
                    groups[nb_groups][0] = STATES[i];       // Add the current state to a new group
                    // Set the name of this new group as the type of the current state
                    groups_name[nb_groups] = state_type.toString();
                    groups_size[nb_groups] = 1;         // Store that now there is one state in this group
                    nb_groups++;        // Store that now there is one more group
                }
            }

            // Execute this code at least once, and stop only when the groups don't change anymore or when each state
            // is in a separated group
            do {
                // Loop through all the groups to rename and copy them
                for (i = 0; i < nb_groups; i++) {
                    // Rename each group by just it's number to prevent having longer and longer names at each iteration
                    // of the do while loop
                    groups_name[i] = String.valueOf(i);
                    // Copy the group in variable 'group_temp' to save the actual content of each group
                    System.arraycopy(groups[i], 0, groups_temp[i], 0, groups_size[i]);
                }
                // Copy the names of all the groups in the variable 'groups_name_temp' to save their actual values
                System.arraycopy(groups_name, 0, groups_name_temp, 0, nb_groups);
                // Copy the sizes of all the groups in the variable 'groups_size_temp' to save their actual values
                System.arraycopy(groups_size, 0, groups_size_temp, 0, nb_groups);
                nb_groups_temp = nb_groups;     // Save the actual number of groups in the variable 'nb_groups_temp'

                for (i = 0; i < nb_groups; i++) {     // Loop through all the groups to check if they need to be split
                    // Clear the content of the StringBuilder to be able to make a new state
                    state_type.setLength(0);
                    state_type.append(groups_name_temp[i]); // Add the name of the current group to its own type
                    // Loop through all letters of the alphabet of the automaton to determine the type of the current
                    // group (which will be the type of its first state)
                    for (j = 0; j < NB_LETTER; j++) {
                        k = 0;
                        // Determine if the group containing the end state of the transition starting from the first
                        // state of the current group and recognizing the current letter have already been found or not
                        found = false;
                        // Loop through the old groups to find the one that contain the end state of the transition
                        // starting from the first state of the current group and recognizing the current letter
                        while (!found && k < nb_groups_temp) {
                            l = 0;
                            // Loop through the state of the current old group to check if one of them is the state we
                            // are searching
                            while (!found && l < groups_size_temp[k]) {
                                // If the corresponding state is found, add its group to the type of the current group
                                if (groups_temp[k][l] == getTransition(groups[i][0], (char) (97 + j))) {
                                    // Add a dot to separate the newly added group name from the ones already added
                                    state_type.append(".");
                                    state_type.append(groups_name_temp[k]);     // Add the name of the group we found
                                    found = true;       // Store that the group we were searching have been found
                                }
                                l++;
                            }
                            k++;
                        }
                    }
                    // Rename the current group by its type (the type of its first state)
                    groups_name[i] = state_type.toString();

                    j = 1;
                    // Loop through all the states of the current groups to check if they need to be split in another
                    // group (i.e. if they have a type different from the one of the group)
                    while (j < groups_size[i]) {
                        // Clear the content of the StringBuilder to be able to make a new state
                        state_type.setLength(0);
                        state_type.append(groups_name_temp[i]); // Add the name of its group to the type of the state
                        // Loop through all letters of the alphabet of the automaton to determine the type of the
                        // current state
                        for (k = 0; k < NB_LETTER; k++) {
                            l = 0;
                            // Determine if the group containing the end state of the transition starting from the
                            // current state and recognizing the current letter have already been found or not
                            found = false;
                            // Loop through the old groups to find the one that contain the end state of the transition
                            // starting from the current state and recognizing the current letter
                            while (!found && l < nb_groups_temp) {
                                m = 0;
                                // Loop through the state of the current old group to check if one of them is the state
                                // we are searching
                                while (!found && m < groups_size_temp[l]) {
                                    // If the corresponding state is found, add its group to the type of the current
                                    // state
                                    if (groups_temp[l][m] == getTransition(groups[i][j], (char) (97 + k))) {
                                        // Add a dot to separate the newly added group name from the ones already added
                                        state_type.append(".");
                                        state_type.append(groups_name_temp[l]);  // Add the name of the group we found
                                        found = true;       // Store that the group we were searching have been found
                                    }
                                    m++;
                                }
                                l++;
                            }
                        }

                        // If the type of the current state is the same as the one of its group, do nothing and go to
                        // the next state
                        if (groups_name[i].equals(state_type.toString()))
                            j++;
                        else {      // Else, this state have to bo removed from its group and put in a new one
                            k = 0;
                            // Determine if the group corresponding to the current state type already exists, by
                            // default it's false
                            found = false;
                            // Loop through all the groups and search if the group corresponding to the type of the
                            // current state already exist
                            while (!found && k < nb_groups) {
                                // If the group corresponding to the type of the current state is found, then add the
                                // current state to this group
                                if (groups_name[k].equals(state_type.toString())) {
                                    // Add the current state at the end of the group
                                    groups[k][groups_size[k]] = groups[i][j];
                                    groups_size[k]++;   // Store that now there is one more state in this group
                                    found = true;     // Store that the researched group have been found
                                }
                                k++;
                            }

                            // If the group corresponding to the type of the current state haven't been found
                            // (i.e. 'found' is still false), create a new group for the current state
                            if (!found) {
                                groups[nb_groups][0] = groups[i][j];    // Add the current state to a new group
                                // Set the name of this new group as the type of the current state
                                groups_name[nb_groups] = state_type.toString();
                                groups_size[nb_groups] = 1;     // Store that now there is one state in this group
                                nb_groups++;        // Store that now there is one more group
                            }
                            for (k = j; k < groups_size[i]; k++)      // Delete the current state from its old group
                                groups[i][j] = groups[i][j + 1];
                            groups_size[i]--;       // Store that now there is one less group
                        }
                    }
                }
            }
            while (nb_groups != NB_STATES && nb_groups != nb_groups_temp);

            // If each state is in a separated group (i.e. there is as many groups as states), then the automaton is
            // minimal, so return true, else return false
            return nb_groups == NB_STATES;
        }
    }

    /**
     * Minimize the automaton if it is complete deterministic
     *
     * @return a minimal equivalent of the automaton
     * */
    public Automaton minimize() {
        // Respectively the table containing the list of states composing each group and a temporary version used to
        // store the value of this variable when modifying the groups
        State[][] groups, groups_temp;
        // Respectively the list containing the names of the groups and a temporary version used to store the value
        // of this variable when modifying the groups
        String[] groups_name, groups_name_temp;
        // Respectively the list containing the number of states in each group and a temporary version used to store
        // the value of this variable when modifying the groups
        int[]  groups_size, groups_size_temp;
        // Respectively the number of group and a temporary version used to store the value of this variable when
        // modifying the groups
        int nb_groups, nb_groups_temp;
        // The type of the state used to make the groups (the states with same type are in the same groups)
        StringBuilder state_type;
        State[] new_states;     // The new list of states
        Transition[] new_transitions;       // The new list of transitions
        State end_tr;           // Store the end state of the transition to create
        boolean is_initial;     // Determine is the composed state to create is initial
        boolean found;      // Store the result of a condition that we will detail later
        int i, j, k, l, m;      // Counters for the loops


        if (!isDeterministic())     // If the automaton is not deterministic then it can't be minimized, so do nothing
            System.out.println("This automaton is not deterministic.");
        else if (!isComplete())     // If the automaton is not complete then it can't be minimized, so do nothing
            System.out.println("This automaton is not complete.");
        else if (isMinimal())     // Do nothing if the automaton is already minimal
            System.out.println("This automaton is already minimal.");
        else {
            // initialise the variables
            nb_groups = 0;
            groups = new State[NB_STATES][NB_STATES];
            groups_name = new String[NB_STATES];
            groups_size = new int[NB_STATES];
            groups_temp = new State[NB_STATES][NB_STATES];
            groups_name_temp = new String[NB_STATES];
            groups_size_temp= new int[NB_STATES];
            state_type = new StringBuilder();

            // Add the states into the first 2 groups - terminal (T) or non-terminal (NT) - one by one
            for (i = 0; i < NB_STATES; i++) {
                // Clear the content of the StringBuilder to be able to make a new state
                state_type.setLength(0);
                if (STATES[i].isFINAL())        // If the state is final, set its type as terminal (T)
                    state_type.append("T");
                else                // Else (if the state is not final), set its type as non-terminal (NT)
                    state_type.append("NT");

                j = 0;
                // Determine if the group corresponding to the current state type already exists, by default it's false
                found = false;
                // Loop through all the groups and search if the group corresponding to the type of the current state
                // already exist
                while (!found && j < nb_groups) {
                    // If the group corresponding to the type of the current state is found, then add the current state
                    // to this group
                    if (state_type.toString().equals(groups_name[j])) {
                        groups[j][groups_size[j]] = STATES[i];      // Add the current state at the end of the group
                        groups_size[j]++;       // Store that now there is one more state in this group
                        found = true;       // Store that the group of the current state have already been found
                    }
                    j++;
                }
                // If the group corresponding to the type of the current state haven't been found (i.e. 'found' is
                // still false), create a new group for the current state
                if (!found) {
                    groups[nb_groups][0] = STATES[i];       // Add the current state to a new group
                    // Set the name of this new group as the type of the current state
                    groups_name[nb_groups] = state_type.toString();
                    groups_size[nb_groups] = 1;         // Store that now there is one state in this group
                    nb_groups++;        // Store that now there is one more group
                }
            }

            // Execute this code at least once, and stop only when the groups don't change anymore or when each state
            // is in a separated group
            do {
                // Loop through all the groups to rename and copy them
                for (i = 0; i < nb_groups; i++) {
                    // Rename each group by just it's number to prevent having longer and longer names at each iteration
                    // of the do while loop
                    groups_name[i] = String.valueOf(i);
                    // Copy the group in variable 'group_temp' to save the actual content of each group
                    System.arraycopy(groups[i], 0, groups_temp[i], 0, groups_size[i]);
                }
                // Copy the names of all the groups in the variable 'groups_name_temp' to save their actual values
                System.arraycopy(groups_name, 0, groups_name_temp, 0, nb_groups);
                // Copy the sizes of all the groups in the variable 'groups_size_temp' to save their actual values
                System.arraycopy(groups_size, 0, groups_size_temp, 0, nb_groups);
                nb_groups_temp = nb_groups;     // Save the actual number of groups in the variable 'nb_groups_temp'

                for (i = 0; i < nb_groups; i++) {     // Loop through all the groups to check if they need to be split
                    // Clear the content of the StringBuilder to be able to make a new state
                    state_type.setLength(0);
                    state_type.append(groups_name_temp[i]); // Add the name of the current group to its own type
                    // Loop through all letters of the alphabet of the automaton to determine the type of the current
                    // group (which will be the type of its first state)
                    for (j = 0; j < NB_LETTER; j++) {
                        k = 0;
                        // Determine if the group containing the end state of the transition starting from the first
                        // state of the current group and recognizing the current letter have already been found or not
                        found = false;
                        // Loop through the old groups to find the one that contain the end state of the transition
                        // starting from the first state of the current group and recognizing the current letter
                        while (!found && k < nb_groups_temp) {
                            l = 0;
                            // Loop through the state of the current old group to check if one of them is the state we
                            // are searching
                            while (!found && l < groups_size_temp[k]) {
                                // If the corresponding state is found, add its group to the type of the current group
                                if (groups_temp[k][l] == getTransition(groups[i][0], (char) (97 + j))) {
                                    // Add a dot to separate the newly added group name from the ones already added
                                    state_type.append(".");
                                    state_type.append(groups_name_temp[k]);     // Add the name of the group we found
                                    found = true;       // Store that the group we were searching have been found
                                }
                                l++;
                            }
                            k++;
                        }
                    }
                    // Rename the current group by its type (the type of its first state)
                    groups_name[i] = state_type.toString();

                    j = 1;
                    // Loop through all the states of the current groups to check if they need to be split in another
                    // group (i.e. if they have a type different from the one of the group)
                    while (j < groups_size[i]) {
                        // Clear the content of the StringBuilder to be able to make a new state
                        state_type.setLength(0);
                        state_type.append(groups_name_temp[i]); // Add the name of its group to the type of the state
                        // Loop through all letters of the alphabet of the automaton to determine the type of the
                        // current state
                        for (k = 0; k < NB_LETTER; k++) {
                            l = 0;
                            // Determine if the group containing the end state of the transition starting from the
                            // current state and recognizing the current letter have already been found or not
                            found = false;
                            // Loop through the old groups to find the one that contain the end state of the transition
                            // starting from the current state and recognizing the current letter
                            while (!found && l < nb_groups_temp) {
                                m = 0;
                                // Loop through the state of the current old group to check if one of them is the state
                                // we are searching
                                while (!found && m < groups_size_temp[l]) {
                                    // If the corresponding state is found, add its group to the type of the current
                                    // state
                                    if (groups_temp[l][m] == getTransition(groups[i][j], (char) (97 + k))) {
                                        // Add a dot to separate the newly added group name from the ones already added
                                        state_type.append(".");
                                        state_type.append(groups_name_temp[l]);  // Add the name of the group we found
                                        found = true;       // Store that the group we were searching have been found
                                    }
                                    m++;
                                }
                                l++;
                            }
                        }

                        // If the type of the current state is the same as the one of its group, do nothing and go to
                        // the next state
                        if (groups_name[i].equals(state_type.toString()))
                            j++;
                        else {      // Else, this state have to bo removed from its group and put in a new one
                            k = 0;
                            // Determine if the group corresponding to the current state type already exists, by
                            // default it's false
                            found = false;
                            // Loop through all the groups and search if the group corresponding to the type of the
                            // current state already exist
                            while (!found && k < nb_groups) {
                                // If the group corresponding to the type of the current state is found, then add the
                                // current state to this group
                                if (groups_name[k].equals(state_type.toString())) {
                                    // Add the current state at the end of the group
                                    groups[k][groups_size[k]] = groups[i][j];
                                    groups_size[k]++;   // Store that now there is one more state in this group
                                    found = true;     // Store that the researched group have been found
                                }
                                k++;
                            }

                            // If the group corresponding to the type of the current state haven't been found
                            // (i.e. 'found' is still false), create a new group for the current state
                            if (!found) {
                                groups[nb_groups][0] = groups[i][j];    // Add the current state to a new group
                                // Set the name of this new group as the type of the current state
                                groups_name[nb_groups] = state_type.toString();
                                groups_size[nb_groups] = 1;     // Store that now there is one state in this group
                                nb_groups++;        // Store that now there is one more group
                            }
                            for (k = j; k < groups_size[i]; k++)      // Delete the current state from its old group
                                groups[i][j] = groups[i][j + 1];
                            groups_size[i]--;       // Store that now there is one less group
                        }
                    }
                }
            }
            while (nb_groups != NB_STATES && nb_groups != nb_groups_temp);

            // Initialize the list of states, there will be as many states as there are groups
            new_states = new State[nb_groups];
            // Initialize the list of transitions, as minimization is made only on complete deterministic automata,
            // the number of transitions is equal to the number of groups multiplied by the number of letters in
            // the alphabet of the automaton
            new_transitions = new Transition[nb_groups * NB_LETTER];

            for (i = 0; i < nb_groups; i++) {   // Loop through all the groups to create all the new states
                // Determine if the state corresponding to the current group have to be initial, by default it's false
                is_initial = false;
                j = 0;
                // Loop through all the states composing the current group to search if at least one of the is initial,
                // if yes, the state corresponding to this group will also be initial
                while (!is_initial && j < groups_size[i]) {
                    if (groups[i][j].isINITIAL())     // Check if the state is initial
                        is_initial = true;      // Store that this group have to be initial
                    j++;
                }

                // If the group is final, then all the state composing it are also final, and reversely if the group
                // is non-final, then all the state composing it are non-final. Therefore, it's sufficient to test only
                // the first state of the group.
                // We now have all the information about the new composed state, we can create it and add it to the list
                new_states[i] = new State(String.valueOf(i), is_initial, groups[i][0].isFINAL());
            }

            for (i = 0; i < nb_groups; i++) {   // Now loop through all the groups to create all the new transitions
                // Loop through all the letters of the alphabet of the automaton to create all the transitions starting
                // from this state
                for (j = 0; j < NB_LETTER; j++) {
                    k = 0;
                    end_tr = null;    // Store the new composed state that will be the end state of the new transition
                    // As the states that are in the same group have all their transitions going to the same groups,
                    // it is sufficient to just test the first state of each group to find the end group
                    // So loop through all the groups to find the group containing the end state of the transition
                    // starting from the first state of the current group and recognizing the current letter
                    while (end_tr == null && k < nb_groups) {
                        l = 0;
                        // Loop through all the states of the group to check if one of them is the researched state
                        while (end_tr == null && l < groups_size[k]) {
                            // If the desired state have been found, then we can save its group as being the end state
                            // of the transition we are creating
                            if (getTransition(groups[i][0], (char)(97 + j)) == groups[k][l]) {
                                // Save the new state corresponding to the group containing this researched state
                                // in 'end_tr' to be able to construct the new transition
                                end_tr = new_states[k];
                            }
                            l++;
                        }
                        k++;
                    }

                    // We now have all the information about the new transition, we can create it and add it to the list
                    new_transitions[i * NB_LETTER + j] = new Transition(new_states[i], (char)(97 + j), end_tr);
                }
            }

            // We now have all the information about the minimal automaton equivalent to the original one, we can
            // create it and return it
            return new Automaton(NB_LETTER, new_states, nb_groups, new_transitions, nb_groups * NB_LETTER);
            }
        // Return by default the original automaton if nothing else have been returned
        return this;
    }


    /**
     * Make the complement of the automaton if it is complete deterministic
     *
     * @return the complement of the automaton
     */
    public Automaton complement() {
        State[] new_states = new State[NB_STATES];      // The new list of states
        Transition[] new_transitions = new Transition[NB_TRANSITIONS];          // The new list of transitions
        State start_tr, end_tr;         // Store respectively the start of end state of the transition to create
        int i, j;           // Counters for the loops

        if (!isDeterministic())   // If the automaton is not deterministic the algorithm can't be applied, so do nothing
            System.out.println("This automaton is not deterministic.");
        else if (!isComplete())     // If the automaton is not complete the algorithm can't be applied, so do nothing
            System.out.println("This automaton is not complete.");
        else {      // Else (if the automaton is complete deterministic), then make its complement and return it

            // Copy all the states of the original automaton by just inverting the final states: final states become
            // non-final and non-final states become final
            for (i = 0; i < NB_STATES; i++) {
                new_states[i] = new State(STATES[i].getNAME(), STATES[i].isINITIAL(), !STATES[i].isFINAL());
            }

            // Copy all the transitions of the original automaton (but they are on the new states)
            for (i = 0; i < NB_TRANSITIONS; i++) {
                j = 0;
                start_tr = null;
                end_tr = null;
                // Loop through the state to find the new states corresponding to the start and end states of the
                // transition to be able to create the new transition on the new states
                while ((start_tr == null || end_tr == null) && j < NB_STATES) {
                    if (TRANSITIONS[i].getSTART().getNAME().equals(new_states[j].getNAME())) {
                        // When the new state corresponding to the start state is found store it in 'start_tr'
                        start_tr = new_states[j];
                    }
                    if (TRANSITIONS[i].getEND().getNAME().equals(new_states[j].getNAME())) {
                        // When the new state corresponding to the end state is found store it in 'end_tr'
                        end_tr = new_states[j];
                    }
                    j++;
                }

                // We now have all the information about the new transition, we can create it and put it in the list
                new_transitions[i] = new Transition(start_tr, TRANSITIONS[i].getLETTER(), end_tr);
            }

            // We now have all the information about the complement of our automaton, we can create it and return it
            return new Automaton(NB_LETTER, new_states, NB_STATES, new_transitions, NB_TRANSITIONS);
        }
        // Return by default the original automaton if nothing else have been returned
        return this;
    }


    /**
     * Create a table representing the transitions of the automaton
     *
     * @return the 2D list of strings that represents the transitions of the automaton
     */
    public String[][] toTable(){
        String[][] table;       // The table of transitions of the automaton
        // Save the name of the end states of the transition starting from a state and recognizing a specific letter
        StringBuilder transitions = new StringBuilder();
        int i, j; // Counters for the loops

        if (isSynchronous())   // If the automaton is synchronous, we take one line per state and one column per letter
            table = new String[NB_STATES][NB_LETTER];
        else        // If the automaton is asynchronous we need one more column to add the epsilon transitions
            table = new String[NB_STATES][NB_LETTER + 1];

        for (i = 0; i < NB_STATES; i++){    // Fill all the table state by state
            for (j = 0; j < NB_LETTER; j++){    // Fill the line corresponding to the current state letter by letter
                transitions.setLength(0); // Clear the content of the StringBuilder to create a new cell of the table
                // Loop through all the transitions to find the ones of the current state recognizing the current letter
                for (Transition tr : TRANSITIONS){
                    // If we find a transition matching (with the good start state and recognized letter), add the end
                    // state of this transition to 'transitions'
                    if (tr.getLETTER() == (char) (97 + j) && tr.getSTART() == STATES[i]) {
                        // If there are already states in 'transitions', put a separator before adding the new state
                        if (transitions.length() > 0)
                            transitions.append(", ");
                        // Add the end state of the transition
                        transitions.append(tr.getEND().getNAME());
                    }
                }
                if (transitions.length() == 0)      // if no transition have been found then put null in the cell
                    table[i][j] = null;
                else                // Else enter the value of 'transitions' in the table
                    table[i][j] = transitions.toString();
            }
        }

        // If the automaton is asynchronous we have also to add the epsilon transitions
        if (!isSynchronous())
            for (i = 0; i < NB_STATES; i++) {
                transitions.setLength(0); // Clear the content of the StringBuilder to create a new cell of the table
                // Loop through all the transitions to find the epsilon transitions of the current state
                for (Transition tr : TRANSITIONS){
                    // If we find an epsilon transition matching, add the end state of this transition to 'transitions'
                    if (tr.getLETTER() == '*' && tr.getSTART() == STATES[i]) {
                        // If there are already states in 'transitions', put a separator before adding the new state
                        if (transitions.length() > 0)
                            transitions.append(", ");
                        // Add the end state of the transition
                        transitions.append(tr.getEND().getNAME());
                    }
                }
                if (transitions.length() == 0)      // if no transition have been found then put null in the cell
                    table[i][NB_LETTER] = null;
                else                // Else enter the value of 'transitions' in the table
                    table[i][NB_LETTER] = transitions.toString();

            }


            // Return the table once all the cells have been filled with the transitions
        return table;
    }

    public void printTable() {
        String[][] table = toTable();
        int is_asynchronous = 0;

        System.out.print("     ");
        for (int i = 0; i < NB_LETTER; i++)
            System.out.print("|" + (char) (97 + i));
        if (!isSynchronous()) {
            is_asynchronous = 1;
            System.out.print("|*");
        }
        System.out.println();

        for (int i = 0; i < NB_STATES; i++){
            if (STATES[i].isINITIAL() && STATES[i].isFINAL())
                System.out.print("<->");
            else if (STATES[i].isINITIAL())
                System.out.print("-->");
            else if (STATES[i].isFINAL())
                System.out.print("<--");
            else
                System.out.print("   ");
            System.out.print("|" + STATES[i].getNAME());
            for (int j = 0; j < NB_LETTER + is_asynchronous; j++)
                if (table[i][j] == null)
                    System.out.print("|-");
                else
                    System.out.print("|" + table[i][j]);
            System.out.println();
        }
    }


    /**
     * Check recursively if there is at least one path starting from the current state that recognize the
     * word passed as argument
     *
     * @param word The word to be tested
     * @param state The current state
     * @param old_transitions The path used to arrive to this state
     *
     * @return true is there is at least path starting from the state that recognize the word and false otherwise
     */
    private boolean testWordByState(String word, State state, Transition[] old_transitions) {
        boolean found;      // Check if we are not going in an epsilon transitions loop
        int i, j;       // Counters for the loops

        if (word.isEmpty())   // Recognize the word if we reach its end and if the current state is final
            return state.isFINAL();
        for (i = 0; i < NB_TRANSITIONS; i++) {
            // If there are transitions starting from this state with the fist character of the word, check if at least
            // one of the end states of those transitions recognize the rest of the word, if yes the word is recognized
            if (TRANSITIONS[i].getLETTER() == word.charAt(0) && TRANSITIONS[i].getSTART() == state)
                if (testWordByState(word.substring(1), TRANSITIONS[i].getEND(), addTransition(old_transitions, TRANSITIONS[i])))
                    return true;
            // If there are epsilon transitions starting from this state, check if at least one of the end states
            // of those transitions recognize the word, if yes the word is recognized
            if (TRANSITIONS[i].getLETTER() == '*' && TRANSITIONS[i].getSTART() == state) {
                j = old_transitions.length - 1;     // Start our loop from the end of the already done path
                found = false;        // Check if we are not looping on the epsilon transitions, by default it's false
                while (!found && j >= 0 && old_transitions[j].getLETTER() == '*') {
                    // If the end state of the current transition is on our path, and we only used epsilon transition
                    // to go from this state to the current one, then don't go back to it because that would make our
                    // path loop on epsilon transitions forever
                    if (TRANSITIONS[i].getEND() == old_transitions[j].getSTART())
                        found = true;
                    j--;
                }
                // If we are not looping on the epsilon transition, we can check if the end state of the epsilon
                // transition recognize the same word, if yes the word is recognized, so return true
                if (!found && testWordByState(word, TRANSITIONS[i].getEND(), addTransition(old_transitions, TRANSITIONS[i])))
                    return true;
            }
        }
        // If none of the paths starting from this state recognize the word, then the state don't recognize the word,
        // so return false
        return false;
    }

    /**
     * Check if the automaton recognize the word passed as argument
     *
     * @param word The word to be tested
     *
     * @return true if the automaton recognize the word and false otherwise
     */
    public boolean testWord(String word) {
        // Loop through all the states to find the initial ones and test if at least one of them recognize the word
        for (int i = 0; i < NB_STATES; i++) {
            // If the state is initial, test the recognition of the word by this state
            if (STATES[i].isINITIAL() && testWordByState(word, STATES[i], new Transition[0])) {
                // If at least one of the initial states recognize the word then it's recognized by the automaton,
                // so return true
                return true;
            }
        }
        // If none of the initial states recognize the word then it's not recognized by the automaton, so return false
        return false;
    }


    /**
     * Save the automaton in a string using the same format as the text files
     *
     * @return a string containing all the information about the automaton with the format of the text files
     */
    public String saveToString() {
        StringBuilder content = new StringBuilder();        // Store the information about the automaton
        int nb_initial = 0, nb_final = 0;     // Respectively the numbers of initial and final states of the automaton
        int start_tr, end_tr;       // Respectively the start and end state of the transition to add
        int i, j;       // Counters for the loops

        // The first line of the file is the number of letters in the alphabet of the automaton
        content.append(NB_LETTER);
        content.append("\r\n");

        // The second line of the file is the number of states of the automaton
        content.append(NB_STATES);
        content.append("\r\n");

        // The third line of the file is the number of initial states followed by the list of initial states
        for (State state: STATES) {     // count the number of initial states
            if (state.isINITIAL())
                nb_initial++;
        }
        content.append(nb_initial);      // Add the number of initial states to the beginning of the line
        // Loop through all the state to add the list of initial states
        for (i = 0; i < NB_STATES; i++) {
            // if the state is initial, add the number of the state to the line
            if (STATES[i].isINITIAL()) {
                content.append(" ");     // Add a space to separate the newly added state from the rest of the line
                content.append(i);
            }
        }
        content.append("\r\n");          // Go to the next line

        // The fourth line of the file is the number of final states followed by the list of final states
        for (State state: STATES) {     // count the number of final states
            if (state.isFINAL())
                nb_final++;
        }
        content.append(nb_final);      // Add the number of final states to the beginning of the line
        // Loop through all the state to add the list of final states
        for (i = 0; i < NB_STATES; i++) {
            // if the state is final, add the number of the state to the line
            if (STATES[i].isFINAL()) {
                content.append(" ");     // Add a space to separate the newly added state from the rest of the line
                content.append(i);
            }
        }
        content.append("\r\n");          // Go to the next line

        // The fifth line of the file is the number of transitions of the automaton
        content.append(NB_TRANSITIONS);
        content.append("\r\n");

        // The rest of the file (lines 6+) contains the list of all the transitions, with one transition on each line
        // Loop through all the transitions of the automaton and add them one by one to the file
        for (i = 0; i < NB_TRANSITIONS; i++) {
            start_tr = -1;  // Set by default the number of the start state of the transition at -1 (means not found)
            end_tr = -1;    // Set by default the number of the end state of the transition at -1 (means not found)
            j = 0;
            // Loop through all the states to find the numbers of the start and end states of the current transition
            while ((start_tr == -1 && end_tr == -1) || j < NB_STATES) {
                if (STATES[j] == TRANSITIONS[i].getSTART())
                    start_tr = j;       // When the number of the start state is found, save it in 'start_tr'
                if (STATES[j] == TRANSITIONS[i].getEND())
                    end_tr = j;       // When the number of the end state is found, save it in 'end_tr'
                j++;
            }
            // Add the transition to 'content'
            content.append(start_tr);       // Add first the number of the start state of the transition
            content.append(TRANSITIONS[i].getLETTER());     // Add then the letter recognized by the transition
            content.append(end_tr);     // Add finally the number of the end state of the transition
            // Go to the next line (except for the last transition, because no new line at the end of file)
            if (i != NB_TRANSITIONS - 1)
                content.append("\r\n");
        }

        // Return all the information contained in the variable 'content' as a string
        return content.toString();
    }

    /**
     * Save the automaton in a text file at the location chosen by the user
     */
    public void saveInFile() {
        JFileChooser fileChooser = new JFileChooser();      // Allow opening a 'save file' window
        FileNameExtensionFilter filter;                     // Store the extension filter for the 'save file window
        File file;                                          // Will store the file that we will create
        File temp_file;                                     // Store temporarily the new file when renaming it
        FileWriter writer;                                  // Allow writing in to the created file
        JPanel panel = new JPanel();                        // Support for the 'save file' window
        int save;                                           // Store the result of the 'save file' window

        // Set the default folder of the 'save file 'window
        fileChooser.setCurrentDirectory(new File("automata"));
        fileChooser.setSelectedFile(new File("automaton.txt"));  // Set a default name for the file to save
        // Create a filter that display only the text file with extension '.txt'
        filter = new FileNameExtensionFilter("Text file", "txt");
        // Add the filter to the list of filters that can be selected in our save file 'window'
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);        // Apply the filter to our 'save file' window by default
        // Disable the possibility to display all type of files in the 'save file' window, so now the only available
        // filter is the one who display only files with extension '.txt'
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);   // Allow only the selection of files, not folders

        // Open the 'save file' window, and store the result in variable 'save'
        save = fileChooser.showSaveDialog(panel);
        // If the user pressed save, create the file in the location chosen by the user
        if (save == JFileChooser.APPROVE_OPTION) {
            try {   // Secure this portion of the code to handle the errors that can happen during its execution
                // Get the path (location + name) chosen by the user to save the file
                file = fileChooser.getSelectedFile();
                // If the file don't end with the extension '.txt', add it to the name of the file
                if (!file.toString().endsWith(".txt")) {
                    // Create a new file at the same location, with the same name and add the extension '.txt' at the end
                    temp_file = new File(file + ".txt");
                    Files.delete(file.toPath());      // Delete the old file that don't have the extension
                    file = temp_file;       // Get the new file which have the extension
                }
                writer = new FileWriter(file);      // Create the object that can write in the file 'file'
                writer.write(saveToString());       // Save the information about the automaton in the file
                writer.close();            // Close the writer now that all the content have been saved in the file
            }
            catch (IOException exception) {     // Display an error if the saving process fails
                System.out.println("error");
            }
        }
        if (save == JFileChooser.CANCEL_OPTION) {       // If the user pressed cancel, send a message to tell it
            System.out.println("you pressed cancel");
        }
    }

    /**
     * Returns a string representing the automaton.
     *
     * @return a string containing the description of the automaton. This description is composed of:<br>
     * - the number of letters in the alphabet of the automaton<br>
     * - the number and the list of states<br>
     * - the number and the list of transitions<br>
     * */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Automata:\n");
        sb.append(NB_LETTER);
        sb.append(" letters in the alphabet.\n");
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