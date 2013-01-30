/**
 * This class displays the results in a GUI.  A group of files that are linked to each other
 * are displayed in a "ring".  Each ring is in a "box".  The display will be up to 6
 * boxes wide and as long as necessary to include all the rings.  If the display is larger
 * than 1250x650, scroll bars will allow the whole panel to be seen.  File names are connected
 * with a line which is labeled with the number of matching n-word phrases between them.  
 * The lines are collored from red to green according to most to least number of matches.
 * There will occasionally be collisions between two different file boxes or line labels.
 * In this case you must consult the terminal output to determine the name/number.
 * 
 * @author Robert Looby 
 */
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class ResultsGUI
{
    private JFrame frame;
    private ResultsPanel resultsPan;
    private JScrollPane scrollPan;
    private ArrayList<ArrayList<String>> rings;
    private TreeSet<FileResults> results;
    private static final int BOX_SIZE = 200;//size of the box each ring is in
    private static final int BOXES_WIDE = 6;//display is this many rings across
    private static final int RADIUS = 80;//size of the radius of the ring
    private final int FRAME_WIDTH;//frame width determined by number of rings/boxes_wide if there are few rings;
    private final int FRAME_HEIGHT;//frame height determined by number of rings
    private static final int FILE_BOX_HEIGHT = 15;//box where the file name is displayed
    private static final int FILE_BOX_WIDTH = 65;

    /**
     * Constructor takes a TreeSet of FileResults objects and displays them in a GUI.
     */
    public ResultsGUI(TreeSet<FileResults> resultsInput){
        results = resultsInput;
        rings = createResults(results);//takes the results generated by the CatchingPlagiaristsDriver and converts them to a form for display
        FRAME_WIDTH = Math.min(BOXES_WIDE * BOX_SIZE, BOX_SIZE * rings.size());//display is only as wide as it needs to be
        FRAME_HEIGHT = BOX_SIZE * (int)Math.ceil(rings.size()/(double)BOXES_WIDE);//display is only as tall as it needs to be
        //generic frame setup
        frame = new JFrame();
        frame.setTitle("Catching Plagiarists");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        
        //create the results panel to display the results on, it will be no bigger than 1250x650 and will scroll if necessary
        resultsPan = new ResultsPanel();
        resultsPan.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        scrollPan = new JScrollPane(resultsPan);
        scrollPan.setPreferredSize(new Dimension(Math.min(1250, FRAME_WIDTH + 50), Math.min(650, FRAME_HEIGHT + 50)));
        frame.add(scrollPan);

        frame.pack();
        frame.setVisible(true);
        resultsPan.paintComponent(resultsPan.getGraphics());//draw all the rings onto the panel
    }

    /**
     * Takes a TreeSet of FileResults and converts them to a form to be displayed.
     * It is returned as an ArrayList of ArrayLists of Strings.  Each of the inner ArrayLists
     * is what I have referred to as a "ring" and contains all the file names from the input
     * that have matches with each other.  
     */
    private ArrayList<ArrayList<String>> createResults(TreeSet<FileResults> results){
        rings = new ArrayList<ArrayList<String>>();
        if(results.size() < 1){
            return null;
        }

        for(FileResults f : results){//for each FileResults, if one of the files is already in a ring, add the other.  Otherwise add both to a new ring.
            boolean notListed = true;
            for(ArrayList<String> ring: rings){//search through the existing rings
                if(ring.contains(f.getFile1()) || ring.contains(f.getFile2())){//if a ring contains one of the file names
                    notListed = false;
                    if(!ring.contains(f.getFile1())){//add the other
                        ring.add(f.getFile1());
                    } else if(!ring.contains(f.getFile2())){
                        ring.add(f.getFile2());
                    }
                    break;//go on to next FileResult
                }
            }
            if(notListed){//if neither file is in any ring, start a new one and add both file names to it
                rings.add(new ArrayList<String>());
                rings.get(rings.size()-1).add(f.getFile1());
                rings.get(rings.size()-1).add(f.getFile2());
            }
        }
        return rings;
    }

    /**
     * Inner private class used to draw the results to a panel.
     */
    private class ResultsPanel extends JPanel{
        int minHits;//minimum hits between any file pairs
        double scalingFactor;//scaling factor used to spread line colors from red to green
        
        /**
         * constructor takes no arguments and initializes minHits and scalingFactor.
         */
        public ResultsPanel(){
            //first get the minimum and maximum number of hits in order to scale the line coloring
            minHits = Integer.MAX_VALUE;
            int maxHits = 0;
            for(FileResults filePair: results){
                int hits = filePair.getHits();
                if(hits<minHits){
                    minHits = hits;
                }
                if(hits>maxHits){
                    maxHits = hits;
                }
            }
            double range = maxHits - minHits;
            scalingFactor = 255/range;
        }
        
        /**
         * This method draws each ring in the correct box.
         */
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            int ringNum = 0;
            for(ArrayList<String> ring : rings){//for each "ring", draw it in the appropriate box
                Point center = new Point(BOX_SIZE/2 + BOX_SIZE * (ringNum%BOXES_WIDE), 
                        BOX_SIZE/2 + BOX_SIZE * (ringNum/BOXES_WIDE));
                drawRing(g, ring, center);
                ringNum++;
            }

        }
        
        /**
         * This method is used by the paintComponent method to paint the individual rings to the panel.
         * It takes the Graphics component of the panel as well as the ArrayList of file names and the 
         * center of the ring as a Point.  
         */
        private void drawRing(Graphics g, ArrayList<String> ring, Point center){
            int numFiles = ring.size();
            double angSep = Math.PI * 2 / numFiles;//angular separation between files in the ring. Spreads files around evenly in a circle.
            for(int i=0; i<ring.size()-1; i++){//for each possible pair of file names, if they are in this ring, draw the line between them and label it
                for(int j=i+1; j<ring.size(); j++){
                    for(FileResults file: results){
                        int hits = file.getHits(ring.get(i), ring.get(j));
                        if(hits>0){
                            g.setColor(new Color((int)((hits-minHits)*scalingFactor), (int)(255 - (hits-minHits)*scalingFactor), 0));
                            //set the x,y coordinates of the start and end of the line relative to the center of the ring
                            int relX1 = (int)(RADIUS * Math.sin(i * angSep));
                            int relY1 = (int)(-RADIUS * Math.cos(i * angSep));
                            int relX2 = (int)(RADIUS * Math.sin(j * angSep));
                            int relY2 = (int)(-RADIUS * Math.cos(j * angSep));
                            g.drawLine(center.x + relX1, center.y + relY1, center.x + relX2, center.y + relY2);//draw the line
                            
                            Point midPoint = new Point(center.x + (relX1+relX2)/2, center.y + (relY1+relY2)/2);//the label will be at the midpoint
                            g.setColor(Color.BLACK);
                            g.drawString(""+hits, midPoint.x, midPoint.y);//draw the number of hits to the middle of the line
                        }
                    }
                }
            }
            for(int i=0; i<ring.size(); i++){//for each file in the ring draw a box with the file name in it 
                int relX = (int)(RADIUS * Math.sin(i * angSep));
                int relY = (int)(-RADIUS * Math.cos(i * angSep));
                g.setColor(Color.WHITE);
                g.fillRect(center.x + relX - FILE_BOX_WIDTH/2, center.y + relY - FILE_BOX_HEIGHT/2, FILE_BOX_WIDTH, FILE_BOX_HEIGHT);//draw a white box
                g.setColor(Color.BLACK);
                g.drawString(ring.get(i), center.x + relX - FILE_BOX_WIDTH/2 + 2, center.y + relY + FILE_BOX_HEIGHT/2 - 2);//draw the file name in the box
            }
        }
    }
}
