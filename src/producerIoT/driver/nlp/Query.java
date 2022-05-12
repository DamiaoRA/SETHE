package producerIoT.driver.nlp;

import java.util.Map;
import java.util.PriorityQueue;

public class Query {
	private Map<String, Filter> mapFilter;
//	private SortedSet<Trajectory> finalResult;
	private PriorityQueue<Trajectory> finalResult;

	public Query() {
//		finalResult = new TreeSet<Trajectory>();
		finalResult = new PriorityQueue<Trajectory>();
	}

	public Map<String, Filter> getMapFilter() {
		return mapFilter;
	}
	
	public void setMapFilter(Map<String, Filter> mapFilter) {
		this.mapFilter = mapFilter;
	}

	public void addFilter(Filter f) {
		mapFilter.put(f.getName(), f);
	}

	public void add(Trajectory t) {
		if(t.getCoefficient() == 1) {
			finalResult.remove(t);
			finalResult.add(t);
		} else if(finalResult.contains(t)) {
			finalResult.remove(t);
			Trajectory bigger = t;
			for(Filter f : mapFilter.values()) {
				Trajectory o = f.getMapResultQuery().get(t.getId());
				if(o != null) {
					if(bigger.getCoefficient() < o.getCoefficient()) {
						bigger = o;
						f.getMapResultQuery().remove(t);
					}
				}
			}
			finalResult.add(bigger);
		} else {
			finalResult.add(t);
		}

//		if(!finalResult.add(t)) {
//			finalResult.remove(t);
//			finalResult.add(t);
//		}
	}

	public PriorityQueue<Trajectory> getFinalResult() {
		return finalResult;
	}

//	public SortedSet<Trajectory> getFinalResult() {
//		return finalResult;
//	}
//
//	public void setFinalResult(SortedSet<Trajectory> finalResult) {
//		this.finalResult = finalResult;
//	}
}

/**
 * Armazena e ordena os resultados
 *
 */
//class TrajectorySortedSet {
//	private SortedSet<Trajectory> set;
//	private List<Map<String, Trajectory>> maps;
//
//	public void add(Trajectory t) {
//		if(set.add(t)) {
//			Trajectory bigger = null;
//			for(Map<String, Trajectory> map:maps) {
//				Trajectory r = map.get(t.getId());
//				if(bigger == null) {
//					bigger = r;
//				} else if (bigger.compareTo(r) > 0){
//					bigger = r;
//				}
//				map.remove(r.getId());
//			}
//			if(bigger != null) {
//				set.remove(t);
//				set.add(bigger);
//			}
//		}
//	}
//}