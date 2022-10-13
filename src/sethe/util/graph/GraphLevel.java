package sethe.util.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sethe.Expression;
import sethe.PoI;

public class GraphLevel {
	private GraphLevel fatherLevel;
	private Expression exp;
	private List<Vertice> levelVertices;
	
	private Vertice vnull;

	public GraphLevel(Expression exp) {
		this.exp = exp;
		levelVertices = new ArrayList<Vertice>();
		if(exp.isOptional()) {
			vnull = new Vertice();
			vnull.setExpression(exp);
			vnull.setPoi(null);
			levelVertices.add(vnull);
		}
	}

	public void createVertice(PoI p) {
		if(p != null) {
			Vertice v = new Vertice();
			v.setExpression(exp);
			v.setPoi(p);
			if(!levelVertices.contains(v)) {
				levelVertices.add(v);
				if(exp.getOrder() > 0)
					fatherLevel.addChild(v);
			}
		}
	}

	public void addChild(Vertice vChild) {
		boolean added = false;
		for(Vertice vfather : levelVertices) {
			if(vfather.poiIsNull())
				continue;
			if(vfather.getPosition() < vChild.getPosition()) {
				if(!vfather.poiIsNull()) {
					vfather.getChildren().add(vChild);
					added = true;
				}
			}
		}
		if(!added && exp.isOptional())
			vnull.getChildren().add(vChild);
	}

	public void fix(){
		for(Iterator<Vertice> it = levelVertices.listIterator();it.hasNext();) {
			Vertice raiz = it.next();
			if(!raiz.isComplete()) {
				if(raiz.getChildren().isEmpty())
					it.remove();
			}
		}
	}

	public GraphLevel getFatherLevel() {
		return fatherLevel;
	}

	public void setFatherLevel(GraphLevel fatherLevel) {
		this.fatherLevel = fatherLevel;
	}

	public void print() {
		for(Vertice v : levelVertices) {
			v.print();
		}
	}

	public void calcSubSequences(PoI[] subs, List<PoI[]> result) {
		for(Vertice v : levelVertices) {
			v.calcSubSequences(subs, result);
		}
	}
}