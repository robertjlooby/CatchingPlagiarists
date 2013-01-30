/**
 * This class displays a GUI where the user can pick the directory 
 * of text files to analyze as well as the number of consecutive words
 * to compare and the minimum number of matches in order to be
 * included in the results.  If any field is not selected by the user,
 * default values of medium, 6, and 5 are used respectively.  When the
 * start button is clicked it instantiates a new CatchingPlagiaristsDriver
 * and assigns it to the instance of CatchingPlagiaristsDriver it was
 * passed as a parameter.  It then displays the results both in the 
 * terminal and in a GUI display.
 * 
 * @author Robert Looby
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class CatchPlagiaristsGUI implements ActionListener
{
    private CatchingPlagiaristsDriver currentDriver;
    
    private JFrame frame;
    
    private JButton dirNameBtn;
    private JFileChooser dirNameCho;
    private JLabel dirNameLbl;
    private File dirToOpen;
    
    private JComboBox nBox;
    private JLabel nLbl;
    private int n;
    
    private JTextField minHitsBox;
    private JLabel minHitsLbl;
    private int minHits;

    private JButton startBtn;
    
    public CatchPlagiaristsGUI(CatchingPlagiaristsDriver currentDriver){
        //set the frame properties
        this.currentDriver = currentDriver;
        frame = new JFrame();
        frame.setTitle("Catching Plagiarists");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.setPreferredSize(new Dimension(600, 200));
        
        //set up the file chooser, initially set to the documentSets directory
        //initialize dirToOpen to the medium set until changed by the user
        dirNameCho = new JFileChooser("./documentSets");
        dirNameCho.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirNameBtn = new JButton("Open a directory of files...");
        dirNameBtn.addActionListener(this);
        dirToOpen = new File("./documentSets/med_doc_set");
        dirNameLbl = new JLabel("med_doc_set selected by default");
        JPanel dirPanel = new JPanel(new GridLayout(1,2));
        dirPanel.add(dirNameLbl);
        dirPanel.add(dirNameBtn);
        frame.add(dirPanel);
        
        //set up combo box for user to select the number of words in a phrase 
        //to compare. Initialize to 6 until change by user.
        nLbl = new JLabel("Consecutive words to compare:");
        String[] possN = new String[49];
        for(int i=0; i<possN.length; i++){
            possN[i] = "" + (i+2);
        }
        nBox = new JComboBox(possN);
        nBox.setSelectedIndex(4);
        JPanel nPanel = new JPanel(new GridLayout(1,2));
        nPanel.add(nLbl);
        nPanel.add(nBox);
        frame.add(nPanel);
        
        //text field for user to input minimum number of hits in order
        //for a file pair to be included in the results. Will set to 5
        //by default if the user's input is bad or missing.
        minHitsBox = new JTextField(6);
        minHitsLbl = new JLabel("Minimum hits to show in results:");
        JPanel minHitsPanel = new JPanel(new GridLayout(1,2));
        minHitsPanel.add(minHitsLbl);
        minHitsPanel.add(minHitsBox);
        frame.add(minHitsPanel);
        
        //button to read the fields and create the driver to find the results
        startBtn = new JButton("Find the Plagiarists!");
        startBtn.addActionListener(this);
        frame.add(startBtn);
        
        
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * If the "Open a directory of files..." button is clicked, this method 
     * opens a JFileChooser dialog box for the user to select a directory.
     * If they select a new file dirToOpen and dirNameLbl will be updated.
     * If the "Find the Plagiarists!" button is clicked, this method
     * reads the fields in the frame when the start button is clicked and
     * instantiates a new CatchingPlagiaristsDriver with the selected parameters.  It
     * then displays the result both in the terminal and in a GUI display.  If directory,
     * phrase length, or minimum hits have not been set they are set to default values
     * of medium, 6, and 5 respectively.
     */
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == dirNameBtn){//show the file chooser interface
            int returnVal = dirNameCho.showOpenDialog(frame);
            if(returnVal == JFileChooser.APPROVE_OPTION){//if the user selects a file, update dirToOpen and label
                dirToOpen = dirNameCho.getSelectedFile();
                dirNameLbl.setText("Directory to search: " + dirToOpen.getName());
                frame.validate();
                frame.pack();
            }
        }else if(e.getSource() == startBtn){//collect state of other 3 fields
            frame.setVisible(false);
            n = Integer.parseInt(nBox.getSelectedItem().toString());
            String minHitsString = minHitsBox.getText();
            try{
                minHits = Integer.parseInt(minHitsString);
            }catch(Exception ex){
                System.out.println("Invalid input. Using default minimun hits value of 5.");
                minHits = 5;
            }
            currentDriver = new CatchingPlagiaristsDriver(dirToOpen, n, minHits);
            currentDriver.printResults();//display the results
            new ResultsGUI(currentDriver.results);
        }
    }
}
