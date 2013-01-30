/**
 * This class contains the main method used to run the Catching Plagiarists project.
 * Whether the project uses a text based interface or a GUI is selected by which line
 * in the main method is uncommented.  If the text interface is being used this class
 * gets the user input for the directory of files to read, the number of consecutive
 * words to compare, and the minimum number of hits to be included in the results.
 * If the GUI is being used an instance of CatchPlagiristsGUI is created to collect
 * this input.  Whichever one is used then creates an instance of this class which 
 * reads the files from the specified directory and constructs a TreeSet of 
 * FileResults which contain pairs of file names and the number of matches between
 * them.  These results are then displayed both by printing to the terminal and 
 * graphically by an instance of ResultsGUI.
 * 
 * @author Robert Looby 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Scanner;

public class CatchingPlagiaristsDriver
{
    /**
     * a TreeSet of FileResuls objects that will hold the file pairs and the number of matches beteen them
     */
    public TreeSet<FileResults> results;

    /**
     * The main method creates a variable of type CatchingPlagiaristsDriver and uses either a text based interface or
     * a GUI to interact with the user and instantiate a CatchingPlagiaristsDriver with the desired directory, 
     * number of consecutive words to compare, and minimum number of matches to display.  The choice is made by which
     * group/line is commented out in the source code.  After creating the object, the results are printed to the terminal
     * and then displayed graphically using the ResultsGUI class.
     */
    public static void main(String[] args){
        CatchingPlagiaristsDriver currentDriver = null;

        //*********************************************************
        //Uncomment one of the next 2 lines. 
        //First one for text interface, second one for GUI.
        //*********************************************************
        
        //currentDriver = getUserTextInput();
        //currentDriver.printResults();
        //new ResultsGUI(currentDriver.results);

        new CatchPlagiaristsGUI(currentDriver);
    }

    /**
     * This constructor takes a directory and two integers as arguments and goes through the process of reading the files 
     * and constructing the results for the given directory.
     * It calls in order the readFiles(), constructHashes(), and constructTree() methods to analyze the input.  When done
     * the results field will contain all the file pairs with at least minHits matches of n-word phrases.
     * 
     * @param dir should be a directory containing text files to be analyzed
     * @param n the number of consecutive words to compare when searching for plagiarized files
     * @param minHits the minimum number of matches between two files in order to be included in the results
     */
    public CatchingPlagiaristsDriver(File dir, int n, int minHits){
        //open the desired directory and read in the text from all the files
        ArrayList<ArrayList<String>> files = readFiles(dir);//fills 'files'

        //for each file, make a HashSet with all the  n-word phrases from it
        ArrayList<HashSet<String>> fileHashes = constructHashes(n, files);

        //make a TreeSet with all the file pairs, ordered by most matches
        results = constructTree(minHits, fileHashes, files);
    }

    /**
     * This function uses the terminal to get the dir, n and minHits from the user.  
     * If the user does not enter a value or enters an invalid input for any of them, they will be set to 
     * default values of "med", 6, 2 respectively and a message will inform the user of this.
     * After collecting the input, an instance of CatchingPlagiaristsDriver is constructed with those values.
     * 
     * @return an instance of CatchingPlagiaristsDriver that has been constructed with the dir, n, and minHits collected by the function
     */
    private static CatchingPlagiaristsDriver getUserTextInput(){
        Scanner input = new Scanner(System.in);
        System.out.print("Please input the directory of files to search(\"big\", \"med\", or \"sm\"): ");
        String temp = input.nextLine().toLowerCase();
        String dirInput;
        if(temp.equals("big") || temp.equals("med") || temp.equals("sm")){//if they did not make a valid selection, open the "med" set
            dirInput = temp;
        } else{
            System.out.println("Using default choice of \"med\"");
            dirInput = "med";
        }
        File dir = new File("documentSets/"+dirInput+"_doc_set");

        System.out.print("Please input the number of consecutive words to compare: ");
        temp = input.nextLine();
        int n;
        try{//if they did not make a valid selection, make n=6
            n = Integer.parseInt(temp);
        } catch(NumberFormatException e){
            System.out.println("Using default choice of 6");
            n = 6;
        }

        System.out.print("Please input the lower limit of matches for a relation to be shown: ");
        temp = input.nextLine();
        int minHits;
        try{//if they did not make a valid selection, make minHits=2
            minHits = Integer.parseInt(temp);
        } catch(NumberFormatException e){
            System.out.println("Using default choice of 2");
            minHits = 2;
        }

        //construct and return an instance of CatchingPlagiaristsDriver with the dir, n, and minHits set above
        return new CatchingPlagiaristsDriver(dir, n, minHits);
    }

    /**
     *  This method constructs and initializes an ArrayList which holds ArrayLists of Strings, each of which holds the text from one file from the input directory.
     *  The first entry in each of the inner ArrayLists is the file name.  The text of the file, with all non-alphabetic characters removed, follows.
     *  
     *  @param dir the directory to read the text files from
     *  
     *  @return an ArrayList holding ArrayLists of Strings each of which is the file name followed by the text of that file
     */
    private ArrayList<ArrayList<String>> readFiles(File dir){
        String[] tempFileNames = dir.list();//string array of all files in the directory
        ArrayList<ArrayList<String>> files = new ArrayList<ArrayList<String>>();//store in arraylist of arraylists of strings 

        int approxNumFiles = tempFileNames.length;//approximate number since some files will not open
        int displayStep = approxNumFiles/10;//after displayStep many files put out a progress marker
        System.out.print("Reading files.\nProgress: ");

        for(int i=0; i<tempFileNames.length; i++){
            if(i%displayStep == 0 && i/displayStep>0){//print out a progress bar each 10%
                System.out.print((char)0x25AE);
            }
            if(!tempFileNames[i].startsWith(".")){//ignore files starting with '.'
                Scanner fileReader;
                try{//try to open the file, if it fails print error message and continue to next file
                    fileReader = new Scanner(new File(dir.getPath() + "/" + tempFileNames[i]), "ISO-8859-1");
                } catch(FileNotFoundException e){
                    System.out.println(e + "Could not read file \"" + tempFileNames[i] + "\"");
                    continue;
                }

                files.add(new ArrayList<String>());//make new arraylist to hold file's words
                int currentFileIndex = files.size() - 1;//current file might not be i if some failed to read
                files.get(currentFileIndex).add(tempFileNames[i].substring(0,tempFileNames[i].indexOf('.')));//add file name to files as first item, without ".shtml.txt"
                while(fileReader.hasNext()){//read all tokens and add to arraylist if not just punctuation/whitespace after removing numbers/punctuation
                    String word = fileReader.next().replaceAll("[^A-z]", "").toUpperCase();
                    if(!word.equals("")){
                        files.get(currentFileIndex).add(word);
                    }
                }
                fileReader.close();//close the file when done
            }
        }
        System.out.println("...Done!");
        return files;
    }

    /**
     * Takes the number of consecutive words to compare and the files created by the readFiles method and returns and ArrayList of HashSets of the n-word phrases
     * in those files.  After constructing the HashSet for each file, the ArrayList holding the text of that file is set to just hold the file name since the text
     * is no longer needed.
     * 
     * @param n the number of consecutive words to construct each phrase out of
     * @param files an ArrayList of ArrayLists of Strings, each containing the file name followed by the text of the file
     * @return an ArrayList of HashSets of Strings, each HashSet contains all the n-word phrases from the given file
     */
    private ArrayList<HashSet<String>> constructHashes(int n, ArrayList<ArrayList<String>> files){
        int displayStep = files.size()/10;//after displayStep many files put out a progress marker
        System.out.print("Constructing hash sets.\nProgress: ");

        ArrayList<HashSet<String>> fileHashes = new ArrayList<HashSet<String>>();
        for(int i=0; i<files.size(); i++){
            if(i%displayStep == 0 && i/displayStep>0){//print out a progress bar each 10%
                System.out.print((char)0x25AE);
            }

            fileHashes.add(new HashSet<String>());
            for(int pos=1; pos<=files.get(i).size() - n; pos++){//construct and add each n-word phrase
                String phrase = "";
                for(int word = 0; word<n; word++){
                    phrase += files.get(i).get(pos + word);
                }
                fileHashes.get(i).add(phrase);
            }
            ArrayList<String> fileName = new ArrayList<String>();
            fileName.add(files.get(i).get(0));
            files.set(i,fileName);//don't need the raw file anymore, make just have the file name as it's only entry
        }
        System.out.println("...Done!");
        return fileHashes;
    }

    /**
     * Takes the minimum number of matches for a pair of files to be included in the results as well as the ArrayList of HashSets of the 
     * n-word phrases in each file and the ArrayList of ArrayLists of Strings containing the file names and constructs and returns 
     * a TreeSet of FileResults containing all of the file pairs with at least minHits matches between them.
     * 
     * @param minHits the minimum number of matches between two files to be included in the results
     * @param fileHashes an ArrayList of HashSets of Strings. Each HashSet contains all the n-word phrases that appear in a single file
     * @param fileNames an ArrayList of ArrayLists of Strings each of which contains only the name of that file
     * @return a TreeSet of FileResults containing all the file pairs with at least minHits matches of n-word phrases sorted by number of matches
     */
    private TreeSet<FileResults> constructTree(int minHits, ArrayList<HashSet<String>> fileHashes, ArrayList<ArrayList<String>> fileNames){
        int displayStep = (fileHashes.size()-1)/10;//after displayStep many files put out a progress marker
        System.out.print("Constructing tree set of results.\nProgress: ");

        TreeSet<FileResults> results = new TreeSet<FileResults>();
        for(int i=0; i<fileHashes.size()-1; i++){
            if(i%displayStep == 0 && i/displayStep>0){//print out a progress bar each 10%
                System.out.print((char)0x25AE);
            }

            for(int j=i+1; j<fileHashes.size(); j++){
                int hits = findMatches(fileHashes.get(i), fileHashes.get(j));
                if(hits>=minHits){
                    results.add(new FileResults(fileNames.get(i).get(0), fileNames.get(j).get(0), hits));
                }
            }
        }
        System.out.println("...Done!");
        return results;
    }

    /**
     * Finds and returns the number of matches between two HashSets of Strings.
     * 
     * @param set1 a HashSet of Strings
     * @param set2 a HashSet of Strings
     * @return the number of Strings that are the same in set1 and set2
     */
    private static int findMatches(HashSet<String> set1, HashSet<String> set2){
        Set<String> temp = new HashSet<String>(set1);//make a copy of set1
        temp.retainAll(set2);//only keep Strings that are in set1 and set2
        return temp.size();//return the number of Strings remaining
    }

    /**
     * Prints the results field to the terminal in descending order of number of matches
     */
    public void printResults(){
        //print out results
        System.out.println("\nRESULTS:");
        for(FileResults f: results){
            System.out.println(f);
        }
    }
}
