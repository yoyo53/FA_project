package com.company;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition that = (Transition) o;
        return WORD == that.WORD && Objects.equals(START, that.START) && Objects.equals(END, that.END);
    }

    @Override
    public int hashCode() {
        return Objects.hash(START, END, WORD);
    }
}
