import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


public class TilePuzzle {

	private int boardSize;
	protected int count;
	protected State finalState;
	private State initialState;
	private State goalState;

	public TilePuzzle(int boardSize, State initialState, State goalState) {
		this.boardSize = boardSize;
		this.initialState = initialState;
		this.goalState = goalState;
	}

	public State[] getPossibleMoves(State state) {
		List<Direction> possibleDirections = new ArrayList<>(Arrays.asList(Direction.values()));
		int agentColumn = state.agent.x;
		int agentRow = state.agent.y;

		if (agentRow == 0) possibleDirections.remove(Direction.UP);
		if (agentRow == boardSize - 1) possibleDirections.remove(Direction.DOWN);
		if (agentColumn == 0) possibleDirections.remove(Direction.LEFT);
		if (agentColumn == boardSize - 1) possibleDirections.remove(Direction.RIGHT);

		State[] newStates = new State[possibleDirections.size()];
		for (int i = 0; i < possibleDirections.size(); i++) {
			newStates[i] = state.moveAgent(possibleDirections.get(i));
			//			System.out.printf("%s >>%s%n", newStates[i], possibleDirections.get(i));
		}
		return newStates;
	}

	public void breadthFirstSearch() {
		Set<State> visitedStates = new HashSet<>(500);
		Deque<State> nextStates = new LinkedList<>();
		nextStates.offer(initialState);
		visitedStates.add(initialState);
		State currentState;
		count = 0;
		while ((currentState = nextStates.poll()) != null) {
			if (currentState.equals(goalState)) {
				finalState = currentState;
				return;
			}
			for (State newState : getPossibleMoves(currentState)) {
				if (!visitedStates.contains(newState)) {
					nextStates.offer(newState);
					visitedStates.add(newState);
				}
			}
			count++;
		}
		finalState = null;
	}

	public void depthFirstSearch() {
		Set<State> visitedStates = new HashSet<>(500);
		Deque<State> nextStates = new ArrayDeque<>(500);
		nextStates.offer(initialState);
		State currentState;
		count = 0;
		while ((currentState = nextStates.pollFirst()) != null) {
			if (currentState.equals(goalState)) {
				finalState = currentState;
				return;
			}
			if (!visitedStates.contains(currentState)) {
				visitedStates.add(currentState);
				for (State newState : getPossibleMoves(currentState)) {
					nextStates.offerFirst(newState);
				}
				count++;
			}
		}
		finalState = null;
	}

	public void iterativeDeepening(int iterations) {
		try {
			count = 0;
			for (int i = 0; i < iterations; i++) {
				State current = depthBoundedSearch(i);
				if (current != null) {
					finalState = current;
					return;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		finalState = null;
	}

	private State depthBoundedSearch(int bound) throws Exception {
		Set<StateDepth> visitedStates = new HashSet<>(100);
		Deque<State> nextStates = new ArrayDeque<>(100);
		nextStates.offer(initialState);
		State currentState;
		while ((currentState = nextStates.pollFirst()) != null) {
			if (currentState.equals(goalState)) {
				return currentState;
			}
			if (currentState.depth == bound)
				continue;
			StateDepth current = currentState.toStateDepth();
			if (!visitedStates.contains(current)) {
				visitedStates.add(current);
				int neighbours = 0;
				for (State newState : getPossibleMoves(currentState)) {
					nextStates.offerFirst(newState);
					neighbours++;
				}
				count++;
				if (neighbours == 0 && nextStates.isEmpty())
					throw new Exception("Search finished before bound was met");
			}
		}
		return null;
	}

	public void aStar() {
		Set<State> visitedStates = new HashSet<>(500);
		Queue<State> nextStates = new PriorityQueue<>(100, new StateComparator(goalState));
		nextStates.offer(initialState);
		visitedStates.add(initialState);
		State currentState;
		count = 0;
		while ((currentState = nextStates.poll()) != null) {
			if (currentState.equals(goalState)) {
				finalState = currentState;
				return;
			}
			for (State newState : getPossibleMoves(currentState)) {
				if (!visitedStates.contains(newState)) {
					nextStates.offer(newState);
					visitedStates.add(newState);
				}
			}
			count++;
		}
		finalState = null;
	}

	private int manhattanDistance(State state, State goal) {
		int result = 0;
		for (int i = 0; i < state.blocks.length; i++) {
			result += state.blocks[i].distanceTo(goal.blocks[i]);
		}
		if (result == 0) result += state.agent.distanceTo(goal.agent);
		return result;
	}

	private class StateComparator implements Comparator<State> {

		State goal;

		public StateComparator(State goal) {
			this.goal = goal;
		}

		public int compare(State s1, State s2) {
			return (manhattanDistance(s1, goal) + s1.depth) - (manhattanDistance(s2, goal) + s2.depth);
		}

	}

	public List<Direction> backtrack(State finalState) {
		List<Direction> directions = new ArrayList<>(20);
		State current = finalState;
		State parent;
		while ((parent = current.parent) != null) {
			directions.add(current.deriveFromDirection());
			current = parent;
		}
		Collections.reverse(directions);
		return directions;
	}

	public void printResult() {
		if (finalState != null) {
			System.out.println("Solution found at depth " + finalState.depth);
			System.out.println("Expanded " + count + " states");
			System.out.println(backtrack(finalState));
		} else {
			System.out.println("No solution found");
			System.out.println("Expanded " + count + " states");
		}
	}

	public static void main(String[] args) {
			int boardSize = 4;
			
			int maxIndex = boardSize - 1;
			Position[] startBlocks = new Position[maxIndex];
			Position[] endBlocks = new Position[startBlocks.length];
			for (int j = 0; j < startBlocks.length; j++) {
				startBlocks[j] = new Position(j, maxIndex);
				endBlocks[j] = new Position(1, j + 1);
			}
			State initial = new State(new Position(maxIndex, maxIndex), startBlocks);
			State goal = new State(new Position(maxIndex, maxIndex), endBlocks);
			TilePuzzle puzzle = new TilePuzzle(boardSize, initial, goal);
			
			puzzle.depthFirstSearch();
			puzzle.printResult();
	}

}
