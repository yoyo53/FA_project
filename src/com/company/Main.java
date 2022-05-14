package com.company;

// importations for the execution traces
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main {

    public static void main(String[] args) {
        testAutomaton("Test_automata/Int1-7-33.txt");
    }

    static public void testAutomaton(String name){
        Automaton at = new Automaton(name);
        System.out.println("initial");
        at.printTable();
        System.out.println("___________");
        System.out.println("determinized");
        at.determinize().printTable();
        System.out.println("___________");
        System.out.println("complete");
        at.determinize().complete().printTable();
        System.out.println("___________");
        System.out.println("minimized");
        at.determinize().complete().minimize().printTable();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        String[] names;
        if (at.determinize().isSynchronous())
            names = new String[at.getNB_LETTER() + 2];
        else {
            names = new String[at.getNB_LETTER() + 3];
            names[at.getNB_LETTER() + 2] = "ε";
        }
        names[0] = "";
        names[1] = "";
        for (int i = 0; i < at.determinize().getNB_LETTER(); i++) {
            names[i + 2] = String.valueOf((char) (97 + i));
        }

        DefaultTableModel tableModel = new DefaultTableModel(at.determinize().transitionTable(), names) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);

        frame.getContentPane().add(new JScrollPane(table));
        frame.setVisible(true);
        
        
        /* Make the execution traces
        StringBuilder sb = new StringBuilder();
        File file;
        FileWriter writer;
        for (int i = 1; i < 45; i++) {
            sb.setLength(0);

            at = new Automaton("Test_automata/Int1-7-" + i + ".txt");

            sb.append("Automaton #");
            sb.append(i);
            sb.append("\r\n");

            sb.append("Original automaton:\r\n");
            if (at.isSynchronous())
                sb.append("The automaton is synchronous.\r\n");
            else
                sb.append("The automaton is synchronous.\r\n");
            sb.append(at.asTable());
            sb.append("\r\n");

            sb.append("Determinized automaton:\r\n");
            if(at.isDeterministic())
                sb.append("The automaton is already deterministic.\r\n");
            sb.append(at.determinize().asTable());
            sb.append("\r\n");

            sb.append("Complete determinized automaton:\r\n");
            if(at.isComplete())
                sb.append("The automaton is already complete.\r\n");
            sb.append(at.determinize().complete().asTable());
            sb.append("\r\n");

            sb.append("Minimized complete determinized automaton:\r\n");
            if(at.isMinimal())
                sb.append("The automaton is already minimal.\r\n");
            sb.append(at.determinize().complete().minimize().asTable());
            sb.append("\r\n");

            sb.append("Complement of the minimized complete determinized automaton:\r\n");
            sb.append(at.determinize().complete().minimize().complement().asTable());
            file = new File("Execution_traces/Int1-7-trace" + i + ".txt");
            try {
                file.createNewFile();
                writer = new FileWriter(file);
                writer.write(sb.toString());
                writer.close();
            }
            catch (IOException exception) {
                System.out.println("error");
            }
        }
        */
    }
}