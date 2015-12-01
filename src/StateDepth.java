
public class StateDepth extends State {

	public StateDepth(Position agent, Position[] blocks, int depth, State parent) {
		super(agent, blocks, depth, parent);
	}

	public int hashCode() {
		int result = super.hashCode();
		int prime = 31;
		result = prime * result + depth;
		return result;
	}
	
	public boolean equals(Object obj) {
		StateDepth other = (StateDepth) obj;
		return super.equals(obj) && (depth == other.depth);
	}

}
