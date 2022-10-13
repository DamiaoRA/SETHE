package sethe;

public class SubTrajectory implements Comparable<SubTrajectory> {

  private Trajectory trajectory;
//  private List<PoI> pois;
  private PoI[] pois;
  private Double[] vector;
  private Double[] vectorQuery; //vector of the query
  private Double coefficient;
  private String distanceFunction;

  public SubTrajectory() {
//    pois = new ArrayList<PoI>();
    coefficient = 0d;
  }

//  public List<PoI> getPois() {
//    return pois;
//  }
//
//  public void setPois(List<PoI> pois) {
//    this.pois = pois;
//  }

	public Double getCoefficient() {
		return coefficient;
	}

	public PoI[] getPois() {
		return pois;
	}

	public void setPois(PoI[] pois) {
		this.pois = pois;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}

  public Trajectory getTrajectory() {
    return trajectory;
  }

  public void setTrajectory(Trajectory trajectory) {
    this.trajectory = trajectory;
  }

  @Override
  public int compareTo(SubTrajectory o) {
    return this.coefficient.compareTo(o.coefficient);
  }

  public void calcCoefficient(Query query) throws Exception {
    createVector(query);
    coefficient = Distance.similarity(vector, vectorQuery, distanceFunction);
  }

  /**
   * Cria o vetor que representa essa subtrajetória
   * @param filter
   * @return
   */
  public Double[] createVector(Query filter) {
	    vector = new Double[pois.length * (filter.getNumAspects()) + pois.length];
	    vectorQuery = new Double[vector.length];
	    int k = 0;
	    for (int indexPoiSub = 0; indexPoiSub < pois.length; indexPoiSub++) {
	      PoI p2 = pois[indexPoiSub];
	      if(p2 != null) {
		      PoI p1 = indexPoiSub > 0 ? pois[indexPoiSub - 1] : null;
	
		      //calculating aspects coef
		      for (String aspectType : p2.getAspects().keySet()) {
		        String aspect = p2.getAspects().get(aspectType);
		        vector[k] =
		          filter.distance(aspectType, aspect, indexPoiSub, p1, p2, trajectory); //calcula a distancia entre o aspecto e o valor fornecido na consulta
		        vectorQuery[k] = filter.weight(aspectType);
		        k++;
		      }
	
		      //proximity coef
		      int dist = filter.checkProximity(indexPoiSub) && indexPoiSub > 0
		        ? (p2.getPosition() - pois[indexPoiSub - 1].getPosition())
		        : 1;
	
		      vector[k] = (1d / dist);
	      } else {
	    	  vector[k] = 0d;
	      }
	      vectorQuery[k] = 1d;
	      k++;
	    }
	    return vector;
	  }
  
//  public Double[] createVector(Query filter) {
//    vector = new Double[pois.size() * (filter.getNumAspects()) + pois.size()];
//    vectorQuery = new Double[vector.length];
//    int k = 0;
//    for (int indexPoiSub = 0; indexPoiSub < pois.size(); indexPoiSub++) {
//      PoI p2 = pois.get(indexPoiSub);
//      PoI p1 = indexPoiSub > 0 ? pois.get(indexPoiSub - 1) : null;
//
//      //calculating aspects coef
//      for (String aspectType : p2.getAspects().keySet()) {
//        String aspect = p2.getAspects().get(aspectType);
//        vector[k] =
//          filter.distance(aspectType, aspect, indexPoiSub, p1, p2, trajectory); //calcula a distancia entre o aspecto e o valor fornecido na consulta
//        vectorQuery[k] = filter.weight(aspectType);
//        k++;
//      }
//
//      //proximity coef
//      int dist = filter.checkProximity(indexPoiSub) && indexPoiSub > 0
//        ? (p2.getPosition() - pois.get(indexPoiSub - 1).getPosition())
//        : 1;
//
//      vector[k] = (1d / dist);
//      vectorQuery[k] = 1d;
//      k++;
//    }
//    return vector;
//  }

  public void calcCoefficient(double[] vectorCoef) {
    double sumMin = 0;
    double sumMax = 0;
    for (int i = 0; i < vector.length; i++) {
      sumMin += vector[i] <= vectorCoef[i] ? vector[i] : vectorCoef[i];
      sumMax += vector[i] <= vectorCoef[i] ? vectorCoef[i] : vector[i];
    }
    coefficient = sumMin / sumMax;
  }

  public String getDistanceFunction() {
    return distanceFunction;
  }

  public void setDistanceFunction(String distanceFunction) {
    this.distanceFunction = distanceFunction;
  }

  public void print() {
    System.out.println("PoI:");
    for (PoI p : pois) {
      System.out.print(p.toStringPoI());
    }

    System.out.println("\nCat:");
    for (PoI p : pois) {
      System.out.print(p.toStringCategory());
    }

    System.out.println("\n" + coefficient);
    for (double v : vector) {
      System.out.print(v + " ");
    }
    System.out.println();
  }
}
