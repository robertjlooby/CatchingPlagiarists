This was the final project for CSPP 50101 - Immersion Programming in Java in Summer 2012
A Makefile is included (though admittedly I know little about Java makefiles)
To run first 'make', then run 'java CatchingPlagiaristsDriver'
This project is all in Java and uses Swing to implement a GUI (though some
results and progress are displayed in the terminal as well)

PROJECT TITLE: Catching Plagiarists
AUTHOR: Robert Looby
DESCRIPTION: This project allows a user to compare all the text files in a directory
to see which ones may have been plagiarized.  The program uses either a text based
interface or GUI for the user to select the directory to load the files from, the 
number of consecutive words to compare, and the minimum number of matches between two
files in order to be included in the results.  To run with the default selections the
user simply presses return at each of the prompts for the text based interface or 
hits the "Find Plagiarists" button in the GUI version.
OTHER USER INSTRUCTIONS:  After inputing the information for the program, the terminal
will display each step of finding the results.  First it will read the files (outputting
an error message for any files that fail to read), then it will construct hash sets of
all the n-word phrases in each file, and finally it will construct a tree set of all the
file pairs with at least the minimum number of matches between them.  Each step
outputs a progress bar as it goes.  When the results are found the are first printed to 
the terminal in order of descending matches and then displayed in a GUI.  The GUI 
displays all the files with at least the minimum number of matches grouped by which files
had matches with each other.  The lines connecting the file names are colored red for most
number of matches down to green for least.  Each line has the number of matches displayed
next to it.  Occasionally there will be collisions between file names or numbers in the
GUI.  In the even this happens you may consult the terminal output to find the file name
or number of hits.
DESCRIPTION OF YOUR ALGORITHM: The algorithm I used first reads in all the text files, 
then creates a hash set of all the n-word phrases in each of the text files.  Both of these
processes should be O(N) for both time and memory.  The total memory is still O(N) because
after each hash set is created, the word list used to create it is reduced to just the file
name.  After the hash sets are created, each one is compared to all the others and any pairs
of files with at least the minimum number of matches is added to the results.  
ESTIMATED RUNNING TIME OF YOUR ALGORITHM:  The process of comparing all the hash sets 
is O(N^2) in time (and is the overall limiting factor) and a maximum of O(N) in space.  For
the large document set the whole program executes in a little under 3 minutes on my (relatively
modest) laptop. The smaller document sets run much faster.  

SUGGESTION:  To see how the GUI handles displaying groups of several related files try the medium
document set with n=3 and minHits=20.
