package com.company;

/**
 * Object that represents a transition of an automaton
 * */
public class Transition {
    /**
     * State from which the transition goes
     * */
    private final State START;
    /**
     * Name of the transition (part of the alphabet of the automaton)
     * */
    private final char LETTER;
    /**
     * State to which the transition goes
     * */
    private final State END;

    /**
     * Initialize a Transition object
     *
     * @param start The state from which the transition goes
     * @param word The letter recognized by the transition (part of the alphabet of the automaton)
     * @param end The state to which the transition goes
     * */
    public Transition(State start, char word, State end){          // Constructor with required parameters to initialize the automaton
        START = start;
        END = end;
        LETTER = word;
    }

    /**
     * @return the state from which the transition goes
     * */
    public State getSTART() {
        return START;
    }

    /**
     * @return the letter recognized by the transition
     * */
    public char getLETTER() {
        return LETTER;
    }

    /**
     * @return the state to which the transition goes
     * */
    public State getEND() {
        return END;
    }

    /**
     * Returns a string representing the transition.
     *
     * @return a string containing the description of the transition. This description is composed of:<br>
     * - the name of the state from which the transition goes<br>
     * - the letter recognized by the transition<br>
     * - the name of the state to which the transition goes<br>
     * */
    @Override
    public String toString() {
        return "Transition from state " + START.getNAME() + " to state " + END.getNAME() + " via word '" + LETTER + "'";
    }
}