package cave.neuralnetwork;

import java.util.LinkedList;

public class Engine {
	private LinkedList<Transform> transforms = new LinkedList<>();
	
	public void add(Transform transform) {
		transforms.add(transform);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(var t: transforms) {
			sb.append(t).append("\n");
		}
		
		return sb.toString();
	}
}
