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

    public Automaton Complete(){
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray(); // alphabet of all possible inputs as an array of char
        int i, j;
        boolean[][] step_word = new boolean[NB_STATES][NB_WORD];

        for (i = 0; i < NB_STATES; i++)
            for (j = 0; j < NB_WORD; j++)
                step_word[i][j] = false;

        for (i = 0; i < NB_STATES; i++)
            for (j = 0; j < NB_TRANSITIONS; j++)
                if (TRANSITIONS[j].getSTART() == STATES[i] && !String.valueOf(TRANSITIONS[j].getWORD()).equals("*"))
                    step_word[i][TRANSITIONS[j].getWORD() - 97] = true; // 97 is the int value of the ascii code for "a"

        StringBuilder sb = new StringBuilder();
        for (i = 0; i < NB_STATES; i++){
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