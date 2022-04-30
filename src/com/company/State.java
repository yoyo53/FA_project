package com.company;

// Object that will represent a state
public class State {
    private final String NAME;
    private final boolean INITIAL;
    private final boolean FINAL;

    public State(String name, boolean initial, boolean terminal){
        NAME = name;
        INITIAL = initial;
        FINAL = terminal;
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
            sb.append(" and final");
        else
            sb.append(" and not final");
        return sb.toString();
    }
}