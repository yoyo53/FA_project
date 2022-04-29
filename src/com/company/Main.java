package com.company;
import java.io.*;

public class Main {

    public static void main(String[] args) throws Exception{
        Automata at1 = new Automata("Test.txt");
        at1.Complete();
    }
}
