import java.util.ArrayList;

public class MCTNode {
	public ArrayList<MCTNode> children;
	public ArrayList<Move> edges;
	public boolean endpt;
	public double w;
	public double n;
	public boolean turn;
	public MCTNode parent;
	//public boolean turn; //MAYBE???
	
	public MCTNode(ArrayList<Move> edges, MCTNode parent, boolean turn) {
		endpt = false;
		w = 0;
		n = 0;
		this.turn = turn; //represents who's turn it is when moving out from node
		this.parent = parent;
		
		this.edges = edges;
		children = new ArrayList<MCTNode>();
		for (int i = 0; i < edges.size(); i++) {
			children.add(null);
		}
	}
}
