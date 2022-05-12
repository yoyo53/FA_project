package com.company;

/**
 * Object that represents a state of an automaton
 * */
public class State {
    /**
     * Name of the state
     * */
    private final String NAME;
    /**
     * If the state is initial or not
     * */
    private final boolean INITIAL;
    /**
     * If the state is final or not
     * */
    private final boolean FINAL;

    /**
     * Initialize a State object
     *
     * @param name The name of the state to create
     * @param initial If the state is initial or not
     * @param terminal If the state is final or not
     * */
    public State(String name, boolean initial, boolean terminal){
        NAME = name;
        INITIAL = initial;
        FINAL = terminal;
    }

    /**
     * @return a string containing the name of the state
     * */
    public String getNAME() {
        return NAME;
    }

    /**
     * @return true if the state is initial and false otherwise
     * */
    public boolean isINITIAL() {
        return INITIAL;
    }

    /**
     * @return true if the state is final and false otherwise
     * */
    public boolean isFINAL() {
        return FINAL;
    }

    /**
     * Returns a string representing the state.
     *
     * @return a string containing the description of the state. This description is composed of:<br>
     * - the name of the state<br>
     * - if the state is initial or not<br>
     * - if the state is final or not<br>
     * */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("State ");
        sb.append(NAME);
        if (INITIAL)
            sb.append(": initial");
        else
            sb.append(": not initial");
        if (FINAL)
            sb.append(" and final");
        else
            sb.append(" and not final");
        return sb.toString();
    }
}