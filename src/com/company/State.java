package com.company;

import java.util.Objects;

// Object that will represent a state
public class State {
    private final String NAME;
    private final boolean INITIAL;
    private final boolean FINAL;


    public State(String name, boolean initial, boolean terminal){
        this.NAME = name;
        this.INITIAL = initial;
        this.FINAL = terminal;
    }

    public String getNAME() {
        return NAME;
    }

    public boolean isINITIAL() {
        return INITIAL;
    }

    public boolean isFINAL() {
        return FINAL;
    }

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
            sb.append(" and final\n");
        else
            sb.append(" and not final\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return INITIAL == state.INITIAL && FINAL == state.FINAL && Objects.equals(NAME, state.NAME);
    }

    @Override
    public int hashCode() {
        return Objects.hash(NAME, INITIAL, FINAL);
    }
}
