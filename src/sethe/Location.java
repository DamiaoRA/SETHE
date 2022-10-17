package sethe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Location {
	private List<PoI> pois;

	public Location() {
		pois = new ArrayList<>();
	}
	
	public List<PoI> getPois() {
		return pois;
	}

	public void setPois(List<PoI> pois) {
		this.pois = pois;
	}

	public void addAllPoIs(List<PoI> ps) {
		for(PoI p : ps) {
			if(!pois.contains(p))
				pois.add(p);
		}
	}

	public void addPoI(PoI p1) {
		pois.add(p1);
	}

	public String toString() {
		String s = "";
		for(PoI p : pois) {
			s += " " + p.getPosition();
		}
		return s;
	}

	public Map<String, String> getAspects() {
		return pois.get(0).getAspects();
	}
}