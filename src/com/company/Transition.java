package com.company;

// Object that will represent a transition
public class Transition {
    private State init;         // State from which the transition goes
    private State end;          // State to which the transition goes
    private char word;        // Name of the transition (part of the alphabet)

    public Transition (){}          // Default constructor

    public Transition(State init, char word, State end){          // Constructor with required parameters to initialize the automaton
        this.init = init;
        this.end = end;
        this.word = word;
    }

    public State getInit() {
        return init;
    }

    public void setInit(State init) {
        this.init = init;
    }

    public char getWord() {
        return word;
    }

    public void setWord(char word) {
        this.word = word;
    }

    public State getEnd() {
        return end;
    }

    public void setEnd(State end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "init=" + init +
                ", end=" + end +
                ", word=" + word +
                '}' + "\n";
    }
}
