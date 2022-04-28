package com.company;

// Object that will represent a state
public class State {
    private String name;
    private boolean initial;
    private boolean terminal;

    public State(){}

    public State(String name, boolean initial, boolean terminal){
        this.name = name;
        this.initial = initial;
        this.terminal = terminal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isInitial() {
        return initial;
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return "State{" +
                "name='" + name + '\'' +
                ", initial=" + initial +
                ", terminal=" + terminal +
                '}' + "\n";
    }
}
