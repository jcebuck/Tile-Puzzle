import java.util.Arrays;


public class State {

	protected Position agent;
	protected Position[] blocks;
	protected int depth;
	protected State parent;

	public State(Position agent, Position[] blocks) {
		this(agent, blocks, 0, null);
	}
	
	public State(Position agent, Position[] blocks, int depth, State parent) {
		this.agent = agent;
		this.blocks = blocks;
		this.depth = depth;
		this.parent = parent;
	}

	public State moveAgent(Direction d) {
		Position newAgent = null;

		switch (d) {

		case UP:
			newAgent = new Position(agent.x, agent.y - 1);
			break;

		case DOWN:
			newAgent = new Position(agent.x, agent.y + 1);
			break;

		case LEFT:
			newAgent = new Position(agent.x - 1, agent.y);
			break;

		case RIGHT:
			newAgent = new Position(agent.x + 1, agent.y);
			break;
			
		}

		Position[] newBlocks = swapBlocks(newAgent);
		return new State(newAgent, newBlocks, depth + 1, this);
	}

	public Position[] swapBlocks(Position newAgent) {
		Position[] newBlocks = new Position[blocks.length];
		System.arraycopy(blocks, 0, newBlocks, 0, blocks.length);
		int i = 0;
		for (Position block : newBlocks) {
			if (newAgent.x == block.x && newAgent.y == block.y){
				newBlocks[i] = agent;
				break;
			}
			i++;
		}
		return newBlocks;
	}
	
	public StateDepth toStateDepth() {
		return new StateDepth(agent, blocks, depth, this);
	}
	
	public Direction deriveFromDirection() {
		if (agent.x < parent.agent.x) return Direction.LEFT;
		if (agent.x > parent.agent.x) return Direction.RIGHT;
		if (agent.y < parent.agent.y) return Direction.UP;
		if (agent.y > parent.agent.y) return Direction.DOWN;
		return null;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + Arrays.hashCode(blocks);
		return result;
	}
	
	public boolean equals(Object obj) {
		State other = (State) obj;
		if (!agent.equals(other.agent))
			return false;
		if (!Arrays.equals(blocks, other.blocks))
			return false;
		return true;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < blocks.length; i++) {
			sb.append(" ").append((char) ('A' + i)).append(":").append(blocks[i]);
		}
		return String.format("Agent:%s,%s", agent, sb.toString());
	}

}
