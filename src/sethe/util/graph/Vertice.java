package sethe.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sethe.Expression;
import sethe.PoI;

public class Vertice {
	private Expression expression;
	private PoI poi;
	private List<Vertice> children;

	public Vertice() {
		this.children = new ArrayList<Vertice>();
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public List<Vertice> getChildren() {
		return children;
	}

	/**
	 * 
	 * @param e
	 * @param poiChild
	 * @return 	-1 no added and the expression is optional, <br/>
	 * 			0 no added and it is ok<br/>
	 * 			1 added <br/>
	 */
	public int createVertice(Expression e, PoI poiChild) {
		if(poiChild != null) {
			int fatherPosition = getPosition();
	
			if(poiChild.getPosition() < fatherPosition) { //this means that the poiChild is in a position before the poi in the trajectory 
				if(expression.isOptional())
					return -1;
				return 0;
			}

			if(poiChild.getPosition() == fatherPosition)
				return 0;
		}

		if(e.getOrder() == expression.getOrder() + 1) {
			Vertice v = new Vertice();
			v.setExpression(e);
			v.setPoi(poiChild);
			children.add(v);
			return 1;
		} else {
			for(Vertice v : children) {
				return v.createVertice(e, poiChild);
			}
			return 0;
		}
	}

	public void print() {
		String traco = "";
		for(int i = 0; i <= expression.getOrder(); i++) {
			traco += "-";
		}
		System.out.print(traco + " ");

		if(poi != null)
			System.out.print("[" + poi.getPosition() + " : " + poi.getCategory() + " : " +  poi.getName() + "] ");
		else
			System.out.println("null");

		System.out.println();
		for(Vertice v : children) {
			v.print();
		}
	}

	public boolean isComplete() {//int height) {
		if(children.isEmpty()) {
			return expression.isFinal();//expression.getOrder() == height;
		}
		for(Iterator<Vertice> it = children.iterator(); it.hasNext();) {
			Vertice v = it.next();
			if(!v.isComplete()) {//height)) {
				it.remove();
			}
		}

		if(children.isEmpty()) {
			return expression.isFinal();//expression.getOrder() == height;
		}
		return true;
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	public void listListPoIs(List<LinkedList<PoI>> ll, LinkedList<PoI> lk) {
		for(Vertice v : children) {
			if(children.size() > 1) {
				LinkedList<PoI> clone = (LinkedList<PoI>)(lk.clone());//uma nova lista para cada ramo da árvore
				clone.add(poi);
				v.listListPoIs(ll, clone);
			} else {
				lk.add(poi);
				v.listListPoIs(ll, lk);
			}
		}

		if(children.isEmpty()) {
			lk.add(poi);
			ll.add(lk);
		}
	}

	public void calcSubSequences(PoI[] subs, List<PoI[]> result) {
		subs[expression.getOrder()] = getPoi();
		for(Vertice child : children) {
			if(getChildren().size() > 1) {
				PoI[] clone = (PoI[])(subs.clone());//uma nova lista para cada ramo da árvore
				child.calcSubSequences(clone, result);
			} else {
				child.calcSubSequences(subs, result);
			}
		}
		if(children.isEmpty())
			result.add(subs);
	}

	public int getPosition() {
		return poi.getPosition();
	}

	public PoI getPoi() {
		return poi;
	}

	public void setPoi(PoI poi) {
		this.poi = poi;
	}

	public boolean poiIsNull() {
		return poi == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((poi == null) ? 0 : poi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertice other = (Vertice) obj;
		if (poi == null) {
			if (other.poi != null)
				return false;
		} else if (!poi.equals(other.poi))
			return false;
		return true;
	}

	public static void main(String[] args) {
		List<PoI[]> subs = new ArrayList<>();
		PoI p1 = new PoI();p1.setPosition(1);
		PoI p2 = new PoI();p2.setPosition(3);
		PoI p3 = new PoI();p3.setPosition(5);
		PoI p4 = new PoI();p4.setPosition(6);
		PoI p5 = new PoI();p5.setPosition(9);
		PoI p6 = new PoI();p6.setPosition(12);
		PoI[] arr1 = {p1,p2,p4};
		PoI[] arr2 = {p1,p3,p4};
		PoI[] arr3 = {p1,p5,p6};
		int orderPre = 0;
		int orderPos = 2;

		subs.add(arr1);subs.add(arr2);subs.add(arr3);
		List<Object[]> newSub = new ArrayList<>();
		//
		newSub = createNewSubsequence(subs, orderPre, orderPos);
		System.out.println(newSub);

//		int[][] r1 = {{1,2,10},{1,3,10},{1,2,6},{1,4,10}};
//		int[] E1 = {1};
//		int[] E3 = {4};
//		int orderE1 = 0;
//		int orderE2 = 1;
//		int orderE3 = 2;
//		
//		Map<String, Object[]> map = new HashMap<String, Object[]>();
//		
//		for(int[] pois : r1) {
//			int p1 = pois[orderE1];
//			int p3 = pois[orderE3];
//			createNewSubsequence(p1, pois[orderE2] ,p3, orderE2, map);
//		}
//
//		for(Object[] array : map.values()) {
//			System.out.println(array[0] + " " + array[1] + " "+ array[2]);
//		}
	}

	private static createNewSubsequence(List<PoI[]> subs, int orderPre, int orderPos) {
		Map<String, Object[]> map = new HashMap<>();
		return createNewSubsequence(subs, orderPre, orderPos, map);
	}

	private static createNewSubsequence(List<PoI[]> subs, int orderPre, int orderPos, Map<String, Object[]> map) {
		for(PoI[] pois : subs) {
			String key = pois[orderPre] + "_" + pois[orderPos];
			Object[] array = map.get(key);
			if(array == null) {
				List<PoI> points = new ArrayList<PoI>();
				array = new Object[pois.length];
				for (int i = 0; i < pois.length; i++) {
					array[i] = pois[i];
				}
				array[orderPos - 1] = points; 
				map.put(key, array);
			}

			((List<PoI>)pois[orderPre]).add();
		}
	}

	private static void createNewSubsequence(int p1, int i, int p3, int orderE2, Map<String, Object[]> map) {
		Object[] array = map.get(p1+"_"+p3);
		if(array == null) {
			List<Integer> pontos = new ArrayList<Integer>();
			array = new Object[3];
			array[0] = p1;
			array[1] = pontos;
			array[2] = p3;
			map.put(p1+"_"+p3, array);
		}
		((List<Integer>)array[orderE2]).add(i);
	}
}