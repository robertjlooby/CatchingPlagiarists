all: CatchingPlagiaristsDriver.class FileResults.class CatchPlagiaristsGUI.class ResultsGUI.class ResultsGUI$ResultsPanel.class

CatchingPlagiaristsDriver.class : CatchingPlagiaristsDriver.java
	javac CatchingPlagiaristsDriver.java

FileResults.class : FileResults.java
	javac FileResults.java

CatchPlagiaristsGUI.class : CatchPlagiaristsGUI.java
	javac CatchPlagiaristsGUI.java

ResultsGUI.class : ResultsGUI.java
	javac ResultsGUI.java
	
ResultsGUI$ResultsPanel.class : ResultsGUI.java
	javac ResultsGUI.java

clean :
	rm -f *.class *~
