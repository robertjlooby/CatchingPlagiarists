/**
 * This class contains two Strings holding file names and 
 * an int holding the number of matched phrases between them.
 * It implements the Comparable interface so that they
 * can be added to a TreeSet and will be automatically sorted
 * by most matches.
 * 
 * @author Robert Looby
 */
public class FileResults implements Comparable<FileResults>{
        /**
         * Two strings to hold the two file names
         */
        private String file1, file2;
        /**
         * int to hold the number of mathches between the two files
         */
        private int hits;
        
        /**
         * This constructor takes two strings and an integer and 
         * creates an instance of FileResults with file1, file2,
         * and hits equal to the respective inputs.
         * 
         * @param f1 file name to be assigned to file1
         * @param f2 file name to be assigned to file2
         * @param h number of matches to be assigned to hits
         */
        public FileResults(String f1, String f2, int h){
            file1 = f1;
            file2 = f2;
            hits = h;
        }
        
        /**
         * Implementing the compareTo method from the Comparable
         * interface that orders FileResults objects by most hits.
         * 
         * @param other the other FileResults object to compare to
         * @return a negative/0/positive int of other comes after/equal/before in order
         */
        public int compareTo(FileResults other){
            return other.hits - hits;
        }
        
        /**
         * Formats a FileResults as a String as "file1 and file2: #hits"
         * 
         * @return String version of FileResults
         */
        public String toString(){
            return file1 + " and " + file2 + ": " + hits;
        }
        
        /**
         * Returns the name of file1
         * 
         * @return name of file1
         */
        public String getFile1(){return file1;}
        
        /**
         * Returns the name of file2
         * 
         * @return name of file2
         */
        public String getFile2(){return file2;}
        
        /**
         * Returns the number of matches between file1 and file2
         * 
         * @return number of hits
         */
        public int getHits(){return hits;}
        
        /**
         * Returns the number of hits if this object's file1 and file2 match the arguments.
         * The order does not matter.  Returns -1 if the file names do not match.
         * 
         * @return number of hits if file1 and file2 equal f1 and f2 (independant of order) or -1 otherwise
         */
        public int getHits(String f1, String f2){
            if((f1.equals(file1) && f2.equals(file2)) || (f1.equals(file2) && f2.equals(file1))){
                return hits;
            }else{
                return -1;
            }
        }
    }
