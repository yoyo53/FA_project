import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
    private JButton listOfActionsButton;

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
                    fileChooser.setCurrentDirectory(new File("automata"));
                    fileChooser.showOpenDialog(null);
                }
                JFrame frame = new JFrame("My First GUI");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300,300);
                String[] names;
                Object at = null;
                if (at.isSynchronous())
                    names = new String[at.getNB_LETTER()];
                else {
                    names = new String[at.getNB_LETTER() + 1];
                    names[at.getNB_LETTER()] = "ε";
                }
                for (int i = 0; i < at.getNB_LETTER(); i++) {
                    names[i] = String.valueOf((char) (97 + i));
                }
                JTable table = new JTable(at.transitionTable(), names);
                frame.getContentPane().add(new JScrollPane(table));
                frame.setVisible(true);
        });
        saveTheAutomatonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        determinizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        complementeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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

            }
        });



    }

    public static void main(String[] args) {
        GUI gui = new GUI();
    }


}
