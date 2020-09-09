package Search;

import java.util.Arrays;
import java.util.Scanner;
/**
 * 
 * @author Nikolo
 *
 */
class Puzzle{

	private static final boolean test = false;//Switch on or off the test mode
	private static final int levelLimit = 10;//set the limit of the search
	private static final int hashTableSize = 300;//set the size of Hash table
	private static final boolean greedy = false;//change A* search to greedy best-first search, just for fun...

	public static void main(String args[]) {

		if(test){//run the test
			test();
		}
		else {
			boolean isN = true;
			boolean flag = true;

			String input = "";
			Scanner s = new Scanner(System.in);
			int[] puzzle = new int[9];
			int[] goal = new int[9];
			if (args.length == 1 && args[0].equals("-v")) {
				isN = false;
			}
			System.out.println("Welcome to Puzzle Search v1.0\n\nTo begin you will need to provide a start state and goal state.\nStates must include 9 numbers, 0 through 8 with no repeats and no spaces.\nNote: 0 will represent the blank space.\n\n");
			System.out.println("Example: If you type 012345678 the puzzle will look like the following\n\n  1 2\n3 4 5\n6 7 8\n\n");
			if (args.length != 0 && !(args[0].equals("-v"))) {
				System.out.println("Invalid arguments.\nExiting...");
				System.exit(0);
			}
			while (flag) {
				System.out.print("Please enter puzzle start state: ");
				input = s.nextLine();
				if (checkValid(input)) {
					System.out.println("Invalid start state.\nRemember, states must include 9 numbers, 0 through 8 with no repeats and no spaces.\nNote: 0 will represent the blank space.\n");
				} else {
					puzzle = createArray(input);
					flag = false;
				}
			}
			flag = true;
			while (flag) {
				System.out.print("Please enter puzzle goal state: ");
				input = s.nextLine();
				if (checkValid(input)) {
					System.out.println("Invalid goal state.\nRemember, states must include 9 numbers, 0 through 8 with no repeats and no spaces.\nNote: 0 will represent the blank space.\n");
				} else {
					goal = createArray(input);
					flag = false;
				}
			}

			if (solveTest(puzzle, goal)) System.out.println("The puzzle can be solved");
			else System.out.println("The puzzle can't be solved, but still run the search...");

			makeBFSTree(puzzle,goal,true,isN);
			makeBFSTree(puzzle,goal,false,isN);
			makeDLSTree(puzzle,goal,true,isN);
			makeDLSTree(puzzle,goal,false,isN);
			makeASTree(puzzle,goal,isN);
		}

	}


	/**
	 * test if the puzzle can be solved
	 * @param puzzle
	 * @param goal
	 * @return can be solved or not.
	 *
	 * Explain how this method works
	 *
	 * for example, the given puzzle is: 	{1, 4, 3, 2, -1, 8, 6, 7, 5}
	 * 				and the goal puzzle is:	{4, 1, 3, -1, 2, 5, 6, 7, 8}
	 *
	 * first remove the blank, which is -1
	 * so now, the given puzzle is: 	{1, 4, 3, 2, 8, 6, 7, 5}
	 * 			and the goal puzzle is:	{4, 1, 3, 2, 5, 6, 7, 8}
	 *
	 * give the given puzzle an order:	{1, 4, 3, 2, 8, 6, 7, 5}
	 * 									 0  1  2  3  4  5  6  7
	 *
	 * and try to change the goal puzzle into the order number: (1-0) (4-1) (3-2) (2-3) (8-4) ....
	 *
	 * now the goal puzzle changed: {4, 1, 3, 2, 5, 6, 7, 8} ===> {1, 0, 2, 3, 7, 5, 6, 4}
	 * and store this into test[]
	 *
	 * 	test[]:
	 * 	 actual order: {1, 0, 2, 3, 7, 5, 6, 4}
	 * 	 correct order:	0  1  2  3  4  5  6  7
	 *
	 * 	 Subtract from the first column, and the result is the offset of the first element in test[].
	 * 	 if result > 0, than need to move the element to the correct position because it breaks the order.
	 * 	 if result <= 0, do nothing because this element doesn't break the correct order.
	 *
	 * 	 1 - 0 = 1 > 0  move 1 step to the right
	 * 	 actual order: {0, 1, 2, 3, 7, 5, 6, 4}
	 * 	 correct order:	0  1  2  3  4  5  6  7
	 *
	 * 	 7 - 4 = 3 > 0 move 3 step to the right
	 * 	 actual order: {0, 1, 2, 3, 5, 6, 4, 7}
	 * 	 correct order:	0  1  2  3  4  5  6  7
	 *
	 * 	 and so on...
	 * 	 ...
	 * 	 ...
	 *
	 * 	 now the order has been fixed to the correct order
	 *
	 * 	 add all steps moved, in this example, it's 8
	 *
	 * 	 if the sum is odd, the puzzle can't be solved
	 * 	 if the sum is even, the puzzle can be solved
	 *
	 *	in this example, the puzzle can be solved: L -> U -> R -> D -> R -> D -> L -> L -> U -> R -> U ->
	 *	L -> D -> D -> R -> R -> U -> L -> L -> U -> R -> D -> R -> D -> L -> U -> L -> D -> R -> R -> U
	 *	-> L -> D -> L -> U -> R -> D -> R -> U -> L -> D -> L -> U -> R -> D -> L -> U -> R -> R -> D ->
	 *	L -> L -> U -> R -> D -> R -> U -> L -> L
	 *
	 */
	private static boolean solveTest(int[] puzzle, int[] goal) {
		int [] order = new int[8];//store the original order
		for(int i = 0, j = 0; i < 9; i++){// copy the puzzle without blank from puzzle[]
			if(puzzle[i] != -1){
				order[j] = puzzle[i];
				j++;
			}
		}

		int [] newOrder = new int[8];//store the goal state order
		for(int i = 0, j = 0; i < 9; i++){// copy the puzzle without blank from goal[]
			if(goal[i] != -1){
				newOrder[j] = goal[i];
				j++;
			}
		}

		int [] test = new int[8];
		int inverseNum = 0;//the distance of the inverse element (from the current position to the correct position)
		int totalInverse = 0;//total distance																												{-1,1, 2}
		boolean stop = false;//for stop the loop																											{3, 4, 5}
		for(int i = 0; i < 8; i++){//give a number to each element from the input state, so that this method can deal with any given puzzle instead of only {6, 7, 8}
			test[i] = getIndex(order, newOrder[i]);//take the value of one element in goal state(without blank), check the index of this value in order[]
		}

		while(!stop){
			int pos = 0;
			for (; pos < 8; pos++) {//find the element that is inverted and get the number
				if ((test[pos] - pos) > 0) {
					inverseNum = test[pos] - pos;
					break;
				}
			}
			for(int k = 0; k < 7; k++){
				stop = true;
				if(test[k] > test[k+1]) {
					stop = false;
					break;
				}
			}
			if(!stop){
				for (int i = pos; i < (pos + inverseNum); i++) {
					int temp = test[i];
					test[i] = test[i + 1];
					test[i + 1] = temp;
				}
				//System.out.println(Arrays.toString(test));
				totalInverse += inverseNum;
			}
		}

		if((totalInverse % 2) == 0) {
			//System.out.println("Y" + totalInverse);
			return true;
		}
		else {
			//System.out.println("N" + totalInverse);
			return false;
		}
	}

	/**
	 * helper method for solveTest()
	 * @param array
	 * @param value
	 * @return
	 */
	public static int getIndex(int[] array, int value){
		for(int i = 0; i < array.length; i++){
			if(array[i] == value) return i;
		}
		return -1;
	}

	/**
	 *
	 * @param input
	 * @return
	 */
	public static int[] createArray(String input) {
		int [] puzzle = {0, 0, 0, 0, 0, 0, 0, 0, 0};
		char num;
		for(int i = 0; i < 9; i++) {
			num = input.charAt(i);
			puzzle[i] = Character.getNumericValue(num);
			if(puzzle[i] == 0) {
				puzzle[i] = -1;
			}
		}
		return puzzle;
	}

	/**
	 *
	 * @param input
	 * @return
	 */
	public static boolean checkValid(String input) {
		if((!input.matches("[0-9]+"))) {
			return true;
		}
		if(input.length() != 9) {
			return true;
		}
		else {
			int [] array = createArray(input);
			int[] valid = {-1, 1, 2, 3, 4, 5, 6, 7, 8};
			Arrays.sort(array);
			for(int i = 0; i < 9; i++) {
				if(array[i] != valid[i]) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * This method finds the pos in the array that is blank
	 * @param puzzle
	 * @return the position of the blank
	 */
	public static int findBlank(int[] puzzle) {
		int pos = 0;
		for(int i = 0; i < puzzle.length; i++) {
			if(puzzle[i] == -1) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	/**
	 * This method returns an array of possble moves to be made
	 * @param puzzle
	 * @return an array of possible movements, -1 should be ignored while building the search tree
	 */
	public static int[] possibleMoves(int[] puzzle) {
		int pos = findBlank(puzzle);
		if(pos == 0) {
			int moves [] = {-1,1,3,-1};
			return moves;
		}
		else if(pos == 1) {
			int moves [] = {-1,2,4,0};
			return moves;
		}
		else if(pos == 2) {
			int moves [] = {-1,-1,5,1};
			return moves;
		}
		else if(pos == 3) {
			int moves [] = {0, 4, 6, -1};
			return moves;
		}
		else if(pos == 4) {
			int moves [] = {1,5,7,3};
			return moves;
		}
		else if(pos == 5) {
			int moves [] = {2,-1,8,4};
			return moves;
		}
		else if(pos == 6) {
			int moves [] = {3, 7, -1, -1};
			return moves;
		}
		else if(pos == 7) {
			int moves [] = {4,8,-1,6};
			return moves;
		}
		else if(pos == 8) {
			int moves [] = {5,-1,-1,7};
			return moves;
		}
		return null;
	}

	/**
	 * This method swaps two elements in the puzzle array
	 * @param puzzle
	 * @param pos target position
	 * @return swapped puzzle
	 */
	public static int [] swapPos(int [] puzzle, int pos) {
		int [] array = new int[9];
		System.arraycopy(puzzle, 0, array, 0, 9);//copy from puzzle[]
		int blank = findBlank(array);//get the position of blank element

		//swap elements
		int temp = array[pos];
		array[pos] = array[blank];
		array[blank] = temp;

		return array;
	}

	/**
	 * This method prints the puzzle in a 3*3 square
	 * @param state
	 */
	public static void printPuzzle(int[] state) {
		System.out.print("\n\t" + "|\t" + showBlank(state[0]) +" \t" + showBlank(state[1]) + "\t" + showBlank(state[2]) + "\t|");
		System.out.print("\n\t" + "|\t" + showBlank(state[3]) +" \t" + showBlank(state[4]) + "\t" + showBlank(state[5]) + "\t|");
		System.out.print("\n\t" + "|\t" + showBlank(state[6]) +" \t" + showBlank(state[7]) + "\t" + showBlank(state[8]) + "\t|");
	}

	/**
	 * Helper method for print puzzles, change the blank from "-1" to " "
	 * @param input an element from the array of the puzzle
	 * @return a String to print out
	 */
	public static String showBlank(int input){
		if(input != -1){
			return String.valueOf(input);
		}
		else{
			return " ";
		}
	}

	/**
	 * get the movement between two puzzles
	 * @param lastPuzzle
	 * @param puzzle
	 * @return the movement information 'U', 'D', 'L', 'R'
	 */
	public static char findMove(int[] lastPuzzle, int[] puzzle){
		int move = findBlank(puzzle) - findBlank(lastPuzzle);
		char m = 'E';//Error
		if (move == 3) {
			m = 'D';
		}
		else if(move == -3) {
			m = 'U';
		}
		else if(move == 1) {
			m = 'R';
		}
		else if(move == -1) {
			m = 'L';
		}
		return m;
	}

	/**
	 * check the repeat state of the puzzle
	 * @param n the state foe checking
	 * @param hash hash table that store all existed states
	 * @return true if it is not repeated
	 */
	public static boolean isNotHashed(int[] n, HashChain<String, String> hash) {
		String temp = Arrays.toString(n);
		if (hash.search(temp,temp) != null) return false;
		else return true;
	}

	/**
	 * Helper method for insert new states to hash table
	 * @param n the state to be stored
	 * @param hash hash table
	 */
	public static void hashInsert(int[] n, HashChain<String, String> hash){
		String temp = Arrays.toString(n);
		hash.insert(new HashNode<>(temp,temp));
	}

	/**
	 * Print the steps in normal or verbose mode and show other information of the search
	 * @param target the node that has the goal state
	 * @param puzzle the original state for verbose mode
	 * @param isNormal normal or verbose
	 * @param countState amount of state stored in tree
	 * @param countAllState amount of state created while searching.
	 */
	public static void printResult(TNode target, int[] puzzle, boolean isNormal, int countState, int countAllState){
		System.out.println("\t" + countState + " States stored.");
		System.out.println("\t" + countAllState + " States created.");
		int countStep = 0;
		if(target == null){
			System.out.println("\tCan't solve the problem ...");
			countStep = -1;
		}
		else{//go from leaf to root, the step is in reverse order, so use stack to change it back.
			StackList<TNode> steps = new StackList<TNode>();//for showing the result.
			countStep = target.getLevel();
			//Normal mode
			if(isNormal){
				while(target.getParent() != null){//track the parent back to root
					steps.push(target);//push the steps into stack
					target = target.getParent();
				}
				if(countStep == 1) System.out.print("\t1 Step: ");
				else System.out.print("\t" + countStep + " Steps: ");
				System.out.print(steps.pop().getMove());//pop the steps out of stack one by one
				while(!steps.isEmpty()){
					System.out.print(" -> " + steps.pop().getMove());
				}
			}

			//Verbose
			else{
				StackList<TNode> verboseSteps = new StackList<TNode>();//for showing the verbose result.
				while(target.getParent() != null){//track the parent back to root
					steps.push(target);//push the steps into stacks
					verboseSteps.push(target);
					target = target.getParent();
				}
				countStep = steps.size();
				System.out.print("\t" + countStep + " Steps: ");
				System.out.print(steps.pop().getMove());//pop the steps out of stack one by one
				while(!steps.isEmpty()){
					System.out.print(" -> " + steps.pop().getMove());
				}

				//show the states
				int i = 0;
				System.out.print("\n\t----Original-----");
				printPuzzle(puzzle);
				while(!verboseSteps.isEmpty()){
					System.out.print("\n\t-----Step " + (i + 1) + "------");
					i++;
					printPuzzle((int[])verboseSteps.pop().getElement());
				}
				System.out.print("\n\t-----------------");
			}
		}
	}

	/**
	 * Create BFS tree for searching
	 * @param puzzle input puzzle
	 * @param goal goal state
	 * @param checkRepeat true if need avoid repeat
	 * @param isNormal true if the output need to be normal, false if need to be verbose
	 * @return an array for report table
	 * 				steps
	 * 		 		time spent in ms
	 * 		 		amount of states stored in the tree
	 * 		 		amount of states created while searching
	 */
	public static int[] makeBFSTree(int [] puzzle, int [] goal, boolean checkRepeat, boolean isNormal) {
		System.out.print("\n\nStart breadth-first search: ");
		if(checkRepeat) System.out.print("(Avoid repeat)");
		else System.out.print("(Allow repeat)");
		if(isNormal) System.out.println("(Normal mode)");
		else System.out.println("(Verbose mode)");

		long time = System.currentTimeMillis();//start recording the time spent
		int countState = 0;//amount of states stored in the tree
		int countAllState = 0;//amount of states created while searching

		QueueList<TNode> q = new QueueList<TNode>();//for create BFS tree
		HashChain<String, String> repeat = new HashChain<String, String>(hashTableSize);//Store all the states in the BFS

		int [] cur = new int[9];//store the current state of the puzzle
		System.arraycopy(puzzle, 0, cur, 0, 9);//copy from puzzle[]
		int [] newState = new int[9];//store the new state of the puzzle

		TNode newNode = new TNode();//Store the new child node temporarily
		TNode target = null;//store the node that has the goal state
		TNode node = new TNode(cur);//root of the BFS tree
		node.setLevel(0);
		q.enqueue(node); //add the first node in the queue to start BFS

		int[] moves =  possibleMoves(cur);//set the first possible movements array
		hashInsert(cur,repeat);//add to the hash table if not repeated
		boolean found = false;//the flag for find the goal state.
		int level = 0;//the maximum number of levels that can be opened by the search

		while((!found) && level <= levelLimit){
			//change the current node to the next node in the queue
			if(!q.isEmpty()){//queue is not empty
				node = q.dequeue();//take the next node in the queue for building BFS tree
				level = node.getLevel();
			}else break;//queue is empty, finish building the BFS tree

			int [] nextMove = possibleMoves((int[])node.getElement());//find possible moves for next state
			System.arraycopy(nextMove, 0, moves, 0, 4);//copy to moves[]
			System.arraycopy(node.getElement(),0,cur,0,9);//change cur[] to the current state

			//Create all possible child notes
			for(int i = 0; i < moves.length; i++) {//find nodes by possible movements
				if(moves[i] != -1){//no need to use "-1"
					int [] temp = new int[9];
					System.arraycopy(cur,0,temp,0,9);//get the current state avoid change it, so copy from cur[]
					newState = swapPos(temp, moves[i]);//get a new state by swap to possible position
					countAllState++;
					if(checkRepeat){//check repeat states while searching
						if(isNotHashed(newState,repeat)){//check repeat by hash table
							newNode = new TNode(newState, node, findMove(cur, newState));//create new TNode for the tree, set their parent to the current node
							newNode.setLevel(node.getLevel() + 1);
							q.enqueue(newNode);//add to the queue if not repeated
							countState++;

							hashInsert(newState,repeat);//add to the hash table if not repeated
							if (Arrays.equals(newState,goal)){//check if the search tree has got the goal state
								target = newNode;//copy the note to target
								found = true;//change the flag to stop the while loop
								break;//stop for loop
							}
						}
					}
					else{//do not check repeat states while searching
						newNode = new TNode(newState, node, findMove(cur, newState));//create new TNode for the tree, set their parent to the current node
						newNode.setLevel(node.getLevel() + 1);
						q.enqueue(newNode);//add to the queue
						countState++;
						if (Arrays.equals(newState,goal)){//check if the search tree has got the goal state
							target = newNode;//copy the note to target
							found = true;//change the flag to stop the while loop
							break;//stop for loop
						}
					}
				}
			}
		}
		//print the movements
		int timeSpend = (int)(System.currentTimeMillis()-time);
		System.out.println("\n\tTime spent: " + timeSpend + "ms");
		printResult(target, puzzle, isNormal,countState,countAllState);
		int step = 0;
		if(target == null) step = -1;
		else step = target.getLevel();
		int[] report = {step, timeSpend, countState, countAllState,q.size()};
		return report;
	}

	/**
	 * Create DLS tree for searching
	 * @param puzzle input puzzle
	 * @param goal
	 * @param checkRepeat true if need avoid repeat
	 * @param isNormal true if the output need to be normal, false if need to be verbose
	 * @return an array for report table
	 * 				steps
	 * 		 		time spent in ms
	 * 		 		amount of states stored in the tree
	 * 		 		amount of states created while searching
	 */
	public static int[] makeDLSTree(int [] puzzle, int [] goal, boolean checkRepeat, boolean isNormal) {
		System.out.print("\n\nStart depth-limit search: ");
		if(checkRepeat) System.out.print("(Avoid repeat)");
		else System.out.print("(Allow repeat)");
		if(isNormal) System.out.println("(Normal mode)");
		else System.out.println("(Verbose mode)");

		long time = System.currentTimeMillis();//start recording the time spent
		int countState = 0;//amount of states stored in the tree
		int countAllState = 0;//amount of states created while searching

		StackList<TNode> s = new StackList<TNode>();//for create DLS tree
		HashChain<String, String> repeat = new HashChain<String, String>(hashTableSize);//Store all the states in the DLS

		int [] cur = new int[9];//store the current state of the puzzle
		System.arraycopy(puzzle, 0, cur, 0, 9);//copy from puzzle[]
		int [] newState = new int[9];//store the new state of the puzzle

		TNode newNode = new TNode();//Store the new child node temporarily
		TNode target = null;//store the node that has the goal state
		TNode node = new TNode(cur);//root of the DLS tree
		node.setLevel(0);
		s.push(node); //add the first node in the stack to start DLS
		int[] moves =  possibleMoves(cur);//set the first possible movements array
		hashInsert(cur,repeat);//add to the hash table if not repeated
		boolean found = false;//the flag for find the goal state.
		int level = 0;//the maximum number of levels that can be opened by the search

		while((!found)) {
				//change the current node to the next node in the stack
				if (!s.isEmpty()) {//stack is not empty
					node = s.pop();//take the next node in the stack for building DLS tree
					level = node.getLevel();
					if(level < levelLimit){
						int[] nextMove = possibleMoves((int[]) node.getElement());//find possible moves for next state
						System.arraycopy(nextMove, 0, moves, 0, 4);//copy to moves[]
						System.arraycopy(node.getElement(), 0, cur, 0, 9);//change cur[] to the current state

						//Create all possible child notes
						for (int i = (moves.length - 1); i >= 0 ; i--) {//find nodes by possible movements
							if (moves[i] != -1) {//no need to use "-1"
								int[] temp = new int[9];
								System.arraycopy(cur, 0, temp, 0, 9);//get the current state avoid change it, so copy from cur[]
								newState = swapPos(temp, moves[i]);//get a new state by swap to possible position
								countAllState++;
								if (checkRepeat) {//check repeat states while searching
									if (isNotHashed(newState, repeat)) {//check repeat by hash table
										newNode = new TNode(newState, node, findMove(cur, newState));//create new TNode for the tree, set their parent to the current node
										newNode.setLevel(node.getLevel() + 1);
										s.push(newNode);//add to the stack if not repeated
										countState++;
										hashInsert(newState, repeat);//add to the hash table if not repeated
										if (Arrays.equals(newState, goal)) {//check if the search tree has got the goal state
											target = newNode;//copy the note to target
											found = true;//change the flag to stop the while loop
											break;//stop for loop
										}
									}
								} else {//do not check repeat states while searching
									newNode = new TNode(newState, node, findMove(cur, newState));//create new TNode for the tree, set their parent to the current node
									newNode.setLevel(node.getLevel() + 1);
									s.push(newNode);//add to the stack
									countState++;
									if (Arrays.equals(newState, goal)) {//check if the search tree has got the goal state
										target = newNode;//copy the note to target
										found = true;//change the flag to stop the while loop
										break;//stop for loop
									}
								}
							}
						}
					}
				} else break;//stack is empty, finish building the DLS tree
		}
		//print the movements
		int timeSpend = (int)(System.currentTimeMillis()-time);
		System.out.println("\n\tTime spent: " + timeSpend + "ms");
		printResult(target, puzzle, isNormal,countState,countAllState);
		int step = 0;
		if(target == null) step = -1;
		else step = target.getLevel();
		int[] report = {step, timeSpend, (step + 1), countAllState,step*3};
		return report;
	}

	/**
	 * Calculate Heuristic value
	 * @param n a node that will be inserted into the heap
	 * @param goal goal state
	 * @return Heuristic value of the node. (How similar to the goal state)
	 */
	public static int calcH(int[] n, int[] goal, int level){
		int count = 0;
		for(int i = 0; i < 9; i++){
			if(n[i] == goal[i]){
				count++;
			}
		}
		if(greedy) return count;
		else return (count + level);
	}

	/**
	 * Create A* search tree for searching
	 * @param puzzle input puzzle
	 * @param goal
	 * @param isNormal true if the output need to be normal, false if need to be verbose
	 * @return an array for report table
	 * 				steps
	 * 		 		time spent in ms
	 * 		 		amount of states stored in the tree
	 * 		 		amount of states created while searching
	 */
	public static int[] makeASTree(int [] puzzle, int [] goal, boolean isNormal)
	{
		System.out.print("\n\nStart A* search: ");
		if(isNormal) System.out.println("(Normal mode)");
		else System.out.println("(Verbose mode)");

		long time = System.currentTimeMillis();//start recording the time spent
		int countState = 0;
		int countAllState = 0;

		MaxHeap heap = new MaxHeap();
		HashChain<String, String> repeat = new HashChain<String, String>(hashTableSize);//Store all the states in the A* Search

		int [] cur = new int[9];//store the current state of the puzzle
		System.arraycopy(puzzle, 0, cur, 0, 9);//copy from puzzle[]
		int [] newState = new int[9];//store the new state of the puzzle

		TNode newNode = new TNode();//Store the new child node temporarily
		TNode target = null;//store the node that has the goal state
		TNode node = new TNode(cur);//root of the A* Search tree
		node.setLevel(0);
		heap.insert(node); //add the first node in the heap to start search
		hashInsert(cur,repeat);//add to the hash table if not repeated
		int[] moves =  possibleMoves(cur);//set the first possible movements array

		boolean found = false;//the flag for find the goal state.
		int level = 0;//the maximum number of levels that can be opened by the search

		while((!found)){
			//change the current node to the next node in the queue
			if(!heap.isEmpty()){//queue is not empty
				node = heap.extractMax();//take the next node in the queue for building tree
				//printPuzzle((int[])node.getElement());
			}else {
				break;//heap is empty, finish building the Search tree
			}

			int [] nextMove = possibleMoves((int[])node.getElement());//find possible moves for next state
			System.arraycopy(nextMove, 0, moves, 0, 4);//copy to moves[]
			System.arraycopy(node.getElement(),0,cur,0,9);//change cur[] to the current state

			//Create all possible child notes
			for(int i = 0; i < moves.length; i++) {//find nodes by possible movements
				if(moves[i] != -1){//no need to use "-1"
					int [] temp = new int[9];
					System.arraycopy(cur,0,temp,0,9);//get the current state avoid change it, so copy from cur[]
					newState = swapPos(temp, moves[i]);//get a new state by swap to possible position
					countAllState++;

					if(isNotHashed(newState,repeat)) {//check repeat by hash table
						newNode = new TNode(newState, node, findMove(cur, newState));//create new TNode for the tree, set their parent to the current node
						newNode.setLevel(node.getLevel() + 1);
						newNode.setHeu(calcH(newState,goal,newNode.getLevel()));

						heap.insert(newNode);
						countState++;
						hashInsert(newState,repeat);//add to the hash table if not repeated

						if (Arrays.equals(newState, goal)) {//check if the search tree has got the goal state
							target = newNode;//copy the note to target
							found = true;//change the flag to stop the while loop
							break;//stop for loop
						}
					}
				}
			}
		}
		//print the movements
		int timeSpend = (int)(System.currentTimeMillis()-time);
		System.out.println("\n\tTime spent: " + timeSpend + "ms");
		printResult(target, puzzle, isNormal,countState,countAllState);
		int step = 0;
		if(target == null) step = -1;
		else step = target.getLevel();
		int[] report = {step, timeSpend, countState, countAllState,heap.size()};
		return report;
	}

	/**
	 * For testing
	 */
	public static void test(){
		long time = System.currentTimeMillis();//start recording the time spent

		int [] puzzle = {1,2,3,4,-1,5,6,7,8};
		int [][] goal = {
				//1 step
				{1,-1,3,4,2,5,6,7,8},
				{1,2,3,4,7,5,6,-1,8},
				{1,2,3,-1,4,5,6,7,8},
				{1,2,3,4,5,-1,6,7,8},
				//2 steps
				{1,3,-1,4,2,5,6,7,8},
				{1,2,3,4,5,8,6,7,-1},
				{1,2,3,4,7,5,-1,6,8},
				{-1,2,3,1,4,5,6,7,8},
				//3 steps
				{4,1,3,-1,2,5,6,7,8},
				{1,3,5,4,2,-1,6,7,8},
				{1,2,3,4,7,-1,6,8,5},
				{1,2,3,6,4,5,7,-1,8},
				//4 steps
				{4,1,3,6,2,5,-1,7,8},
				{-1,1,2,4,5,3,6,7,8},
				{1,2,-1,4,7,3,6,8,5},
				{1,2,3,6,4,5,7,8,-1},
				//10 steps
				{4,2,1,6,-1,3,7,8,5},
				{4,1,2,6,-1,5,7,8,3},
				{4,1,2,6,-1,3,8,7,5},
				{6,1,2,4,-1,3,7,8,5}
		};

		boolean isN = true;
		int[][] table = new int[100][5];
		int j = 0;
		//BFS
		for(int i = 0; i < 20; i++,j++){
			table[j] = makeBFSTree(puzzle, goal[i], true, isN);// Avoid repeat
		}
		for(int i = 0; i < 20; i++,j++){
			table[j] = makeBFSTree(puzzle, goal[i], false, isN);// Allow repeat
		}

		//DLS
		for(int i = 0; i < 20; i++,j++) {
			table[j] = makeDLSTree(puzzle, goal[i], true, isN);// Avoid repeat
		}
		for(int i = 0; i < 20; i++,j++) {
			table[j] = makeDLSTree(puzzle, goal[i], false, isN);// Allow repeat
		}

		//A* search
		for(int i = 0; i < 20; i++,j++) {
			table[j] = makeASTree(puzzle, goal[i], isN);
		}

		j = 0;
		int n=0;


		System.out.println("\n\n\nReport:");
		//BFS avoid repeat
		System.out.println("\n\n\t\t\tSearch type\t\t\tSteps\t\t\tTime(ms)\t\tState stored\tState created\tState not used");

		for(;j<4;j++) {
			System.out.print("\n\t\tBFS Avoid repeat(1)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<8;j++) {
			System.out.print("\n\t\tBFS Avoid repeat(2)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<12;j++) {
			System.out.print("\n\t\tBFS Avoid repeat(3)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<16;j++) {
			System.out.print("\n\t\tBFS Avoid repeat(4)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<20;j++) {
			System.out.print("\n\t\tBFS Avoid repeat(10)\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}

		//BFS allow repeat
		System.out.println("\n\n\t\t\tSearch type\t\t\tSteps\t\t\tTime(ms)\t\tState stored\tState created\tState not used");
		n += 20;
		for(;j<(4 + n);j++) {
			System.out.print("\n\t\tBFS Allow repeat(1)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(8 + n);j++) {
			System.out.print("\n\t\tBFS Allow repeat(2)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(12 + n);j++) {
			System.out.print("\n\t\tBFS Allow repeat(3)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(16 + n);j++) {
			System.out.print("\n\t\tBFS Allow repeat(4)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(20 + n);j++) {
			System.out.print("\n\t\tBFS Allow repeat(10)\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}

		//DLS avoid repeat
		System.out.println("\n\n\t\t\tSearch type\t\t\tSteps\t\t\tTime(ms)\t\tState stored\tState created\tExpected space");
		n += 20;
		for(;j<(4 + n);j++) {
			System.out.print("\n\t\tDLS Avoid repeat(1)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(8 + n);j++) {
			System.out.print("\n\t\tDLS Avoid repeat(2)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(12 + n);j++) {
			System.out.print("\n\t\tDLS Avoid repeat(3)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(16 + n);j++) {
			System.out.print("\n\t\tDLS Avoid repeat(4)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(20 + n);j++) {
			System.out.print("\n\t\tDLS Avoid repeat(10)\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}


		//DLS allow repeat
		System.out.println("\n\n\t\t\tSearch type\t\t\tSteps\t\t\tTime(ms)\t\tState stored\tState created\tExpected space");
		n += 20;
		for(;j<(4 + n);j++) {
			System.out.print("\n\t\tDLS Allow repeat(1)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(8 + n);j++) {
			System.out.print("\n\t\tDLS Allow repeat(2)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(12 + n);j++) {
			System.out.print("\n\t\tDLS Allow repeat(3)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(16 + n);j++) {
			System.out.print("\n\t\tDLS Allow repeat(4)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(20 + n);j++) {
			System.out.print("\n\t\tDLS Allow repeat(10)\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}


		//A* search
		System.out.println("\n\n\t\t\tSearch type\t\t\tSteps\t\t\tTime(ms)\t\tState stored\tState created\tState not used");
		n += 20;
		for(;j<(4 + n);j++) {
			System.out.print("\n\t\tA* search       (1)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(8 + n);j++) {
			System.out.print("\n\t\tA* search       (2)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(12 + n);j++) {
			System.out.print("\n\t\tA* search       (3)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(16 + n);j++) {
			System.out.print("\n\t\tA* search       (4)\t\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}
		System.out.println();
		for(;j<(20 + n);j++) {
			System.out.print("\n\t\tA* search       (10)\t");
			for (int i = 0; i < 5; i++)
				System.out.print(table[j][i] + "\t\t\t\t");
		}

		//total time cost
		int totalTime = 0;
		for(int k = 0; k < 100; k++){
			totalTime += table[k][1];
		}

		int timeSpend = (int)(System.currentTimeMillis()-time);
		System.out.println("\n\n\nTotal time for searching:\t" + totalTime + " ms.");
		System.out.println("Total time for testing:  \t" + timeSpend + " ms.");
	}
}

