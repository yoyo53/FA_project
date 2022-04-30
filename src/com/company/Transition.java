package com.company;

// Object that will represent a transition
public class Transition {
    private final State START;         // State from which the transition goes
    private final State END;          // State to which the transition goes
    private final char WORD;        // Name of the transition (part of the alphabet)

    public Transition(State start, char word, State end){          // Constructor with required parameters to initialize the automaton
        START = start;
        END = end;
        WORD = word;
    }

    public State getSTART() {
        return START;
    }

    public char getWORD() {
        return WORD;
    }

    public State getEND() {
        return END;
    }

    @Override
    public String toString() {
        return "Transition from state " + START.getNAME() + " to state " + END.getNAME() + " via word '" + WORD +"'\n";
    }
}
