package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class GUI extends JFrame {
    private JButton exitButton;
    private JPanel mainPanel;
    private JButton saveTheAutomatonButton;
    private JButton selectButton;
    private JButton minimizeButton;
    private JButton complementeButton;
    private JButton completeButton;
    private JButton determinizeButton;
    private JPanel Panel; //Ignore this one it's a background panel in the main panel with an absolute layout in order to have a homogeneous UI (display facility here) but has no use !
    private JTable table1;
    private JScrollPane pane;
    private JLabel label;
    private JTextField textField1;
    private JButton testWordButton;
    private JButton listOfActionsButton;

    private Automaton current_automaton;
    int result;

    public GUI() {

        //Creation of the window of the UI
        setContentPane(mainPanel);
        setTitle("Automaton's GUI");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        pack();
        setVisible(true);


        // ActionListener is the way to attribute an action to the Button, same process for each button
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Select automaton from our file containing the list of automaton
                if(e.getSource()==selectButton){
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("Test_automata"));
                    result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        current_automaton = new Automaton(fileChooser.getSelectedFile().toString());
                        display_automaton();
                    }
                }
            }
        });
        saveTheAutomatonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();      // Allow opening a 'save file' window
                FileNameExtensionFilter filter;                     // Store the extension filter for the 'save file window
                File file;                                          // Will store the file that we will create
                File temp_file;                                     // Store temporarily the new file when renaming it
                FileWriter writer;                                  // Allow writing in to the created file
                int save;                                           // Store the result of the 'save file' window

                // Set the default folder of the 'save file 'window
                fileChooser.setCurrentDirectory(new File("Test_automata"));
                fileChooser.setSelectedFile(new File("automaton.txt"));  // Set a default name for the file to save
                // Create a filter that display only the text file with extension '.txt'
                filter = new FileNameExtensionFilter("Text file", "txt");
                // Add the filter to the list of filters that can be selected in our save file 'window'
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);        // Apply the filter to our 'save file' window by default
                // Disable the possibility to display all type of files in the 'save file' window, so now the only available
                // filter is the one who display only files with extension '.txt'
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);   // Allow only the selection of files, not folders

                // Open the 'save file' window, and store the result in variable 'save'
                save = fileChooser.showSaveDialog(null);
                // If the user pressed save, create the file in the location chosen by the user
                if (save == JFileChooser.APPROVE_OPTION) {
                    try {   // Secure this portion of the code to handle the errors that can happen during its execution
                        // Get the path (location + name) chosen by the user to save the file
                        file = fileChooser.getSelectedFile();
                        // If the file don't end with the extension '.txt', add it to the name of the file
                        if (!file.toString().endsWith(".txt")) {
                            // Create a new file at the same location, with the same name and add the extension '.txt' at the end
                            temp_file = new File(file + ".txt");
                            Files.delete(file.toPath());      // Delete the old file that don't have the extension
                            file = temp_file;       // Get the new file which have the extension
                        }
                        writer = new FileWriter(file);      // Create the object that can write in the file 'file'
                        writer.write(current_automaton.saveToString());       // Save the information about the automaton in the file
                        writer.close();            // Close the writer now that all the content have been saved in the file
                    }
                    catch (IOException exception) {     // Display an error if the saving process fails
                        JFrame frame = new JFrame("Error");
                        JOptionPane.showConfirmDialog(frame, "Saving fail, remember to save only '.txt' file", "Error", JOptionPane.DEFAULT_OPTION);
                    }
                }
                if (save == JFileChooser.CANCEL_OPTION) {       // If the user pressed cancel, send a message to tell it
                    JFrame frame = new JFrame("Cancel");
                    JOptionPane.showConfirmDialog(frame, "You pressed cancel", "No file selected", JOptionPane.DEFAULT_OPTION);
                }
            }
        });
        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current_automaton = current_automaton.minimize();
                display_automaton();
            }
        });
        determinizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current_automaton = current_automaton.determinize();
                display_automaton();
            }
        });
        complementeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current_automaton = current_automaton.complement();
                display_automaton();
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Exit button code
                //Creation of a pop-up window to confirm the choice of the user
                JFrame frame = new JFrame("Exit");
                if (JOptionPane.showConfirmDialog(frame, "Confirm if you want Exit", "Exit",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION) {
                    System.exit(0);
                }
            }
        });

        completeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                current_automaton = current_automaton.complete();
                display_automaton();
            }
        });

        testWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (current_automaton.testWord(textField1.getText())) {
                    JFrame frame = new JFrame("Word recognized");
                    JOptionPane.showConfirmDialog(frame, "The word '" + textField1.getText() + "' is recognized", "Word recognized", JOptionPane.DEFAULT_OPTION);
                }
                else {
                    JFrame frame = new JFrame("Word not recognized");
                    JOptionPane.showConfirmDialog(frame, "The word '" + textField1.getText() + "' is not recognized", "Word recognized", JOptionPane.DEFAULT_OPTION);
                }
            }
        });


    }

    public void display_automaton() {
        String[] names;
        if (current_automaton.isSynchronous())
            names = new String[current_automaton.getNB_LETTER() + 2];
        else {
            names = new String[current_automaton.getNB_LETTER() + 3];
            names[current_automaton.getNB_LETTER() + 2] = "ε";
        }
        names[0] = "";
        names[1] = "";
        for (int i = 0; i < current_automaton.getNB_LETTER(); i++) {
            names[i + 2] = String.valueOf((char) (97 + i));
        }

        DefaultTableModel tableModel = new DefaultTableModel(current_automaton.transitionTable(), names) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table1.setModel(tableModel);
        display_name();
    }

    public void display_name() {
        StringBuilder sb = new StringBuilder();
        if (current_automaton.isComplete())
            sb.append("Complete, ");
        else
            sb.append("Not complete, ");
        if (current_automaton.isSynchronous())
            sb.append("synchronous, ");
        else
            sb.append("asynchronous, ");
        if (current_automaton.isDeterministic())
            sb.append("deterministic and ");
        else
            sb.append("non-deterministic and ");
        if (current_automaton.isMinimal())
            sb.append("minimal ");
        else
            sb.append("non-minimal ");
        sb.append("automaton");
        label.setText(sb.toString());
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
    }


}
