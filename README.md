AUTHOR: 
	Nikolo Sperberg and Ruiyang Yu

VERSION:
	1.0

CONTENTS: 
	HashChain.java - class to create hash table 
	HashNode.java - nodes to be used for HashChain.java
	List.java - interface of SLList.java
	MaxHeap.java - class to create a maxheap
	Node.java - class to create nodes to be used in SLList.java, QueueList.java StackList.java
	Puzzle.java - driver class
	Queue.java - class to create the interface of QueueList.java
	QueueList.java - class to create a queue
	SLList.java - class to create a single linked list
	Stack.java - class to create the interface of the StackList.java
	TNode.java - class to create nodes that hold puzzle data
	

DISCRIPTION: 
	This program prompts the user for a start state and a goal state for a 
	9 tile sliding puzzle*.  If given states are in the correct format the 
	program will check if the goal state is reachable with the given start 
	state**.  In both cases a message will be printed to the console 
	informing the user if the goal can be reached.  Regardless of this, 
	the code will then run all five sorts and print the moves needed to 
	get to the goal state.  If the program is ran in verbose mode the 
	program will also print out each state of the puzzle***.  

	*States must include 9 numbers, 0 through 8 with no repeats and no spaces. 0 will represent the blank space.
	**This code is for the extra credit.
	***See compile and run for information on how to run verbose mode.
	
COMPILE AND RUN:
	To run this program, you first must compile the .java files.  
	Use command prompt and type the following: javac *.java
	This will compile the code.
	To run the code, use the command prompt again. Type: java Puzzle
	If you wish to run verbose mode, please include -v as an argument in 
	the command prompt

	Example: java Puzzle -v
	
	Note:  This code was written in Eclipse, please remove package lines
	before compiling. 
	
OTHER:
	If you wish to run our test method, please change the global boolean 
	test to true.

	If you wish to change the level limit, please set the global int 
	levelLimit to any desired value.

	If you wish to run a greedy best first search instead of an A* search, 
	please change the global boolean greedy to true.
