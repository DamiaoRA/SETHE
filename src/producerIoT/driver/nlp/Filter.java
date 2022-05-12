package producerIoT.driver.nlp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Filter {
	
	private String name;
	private Query query;
	
	private Expression[] arrayExp; // Expressões regulares do PoI

	private Map<String, Double> mapWeight; 			// Peso dos aspectos
	private Map<String, String> mapDistanceFunc; 	// Função de distância
	private Map<String, Double> mapLimit; 			// limite máximo para a função de distância

	private Map<String, Trajectory> mapResultQuery;  //Armazena o resultado da consulta <id, trajetória>

	public Filter(String name) {
		this.name = name;
		mapWeight = new HashMap<String, Double>();
		mapDistanceFunc = new HashMap<String, String>();
		mapLimit = new HashMap<String, Double>();
		mapResultQuery = new HashMap<String, Trajectory>();
	}

	public void init(int arraySize) {
		arrayExp = new Expression[arraySize];
	}

	public void addExpression(int index, String catExp, String poiExp) {
		Expression exp = new Expression();
		exp.setOrder(index);//listExp.size());
		exp.setValueCategory(catExp);
		exp.setValuePoi(poiExp);
		if(catExp == null || catExp.isEmpty() || catExp.equals(".*"))
			exp.setCategory(false);
		else
			exp.setCategory(true);

		arrayExp[index] = exp;
	}

	public void addAspectExpression(int order, String asp, String value) {
		Expression exp = arrayExp[order];
		exp.addAspect(asp, value);
	}

	public void addProximityExpression(int i, String value) {
		Expression exp = arrayExp[i];
		if(value.trim().equals(Constant.NEAR))
			exp.setCheckProximity(true);
	}

	public boolean checkProximity(int index) {
		Expression exp = arrayExp[index];
		return exp.isCheckProximity();
	}

	public void addWeight(String asp, Double value) {
		mapWeight.put(asp, value);
	}

	public void addDistanceFunction(String asp, String value) {
		mapDistanceFunc.put(asp, value);
	}

	public void addLimitAspValue(String asp, Double value) {
		mapLimit.put(asp, value);
	}

	public String regexPoi() {
		String regex = null;
		String expPoi = null;
		for(Expression exp : arrayExp) {
			expPoi = exp.getRegexPoi();
			if(expPoi != null) {
				if(regex != null) {
					regex += "(.)*" + exp.getRegexPoi();
				} else {
					regex = exp.getRegexPoi();
				}
			}
		}

		return regex;
	}

	public String regexCat() {
		String regex = null;
		String expCat = null;
		for (Expression exp : arrayExp) {
			expCat = exp.getRegexCategory();
			if(expCat != null) {
				if(regex != null) {
					regex += "(.)*" + expCat;
				} else {
					regex = expCat;
				}
			}
		}
		return regex;
	}

	public Double weightPoI() {
		return 0d;//TODO colocar peso nos PoI na próxima versão
	}

	public Double distance(String aspectType, String aspectValue, int positionExp, PoI p1, PoI p2, Trajectory trajectory) {
		String function = mapDistanceFunc.get(aspectType);
		Double limit = mapLimit.get(aspectType);
		Double weight = mapWeight.get(aspectType);

		Double result = 0d;

//		if(arrayExp[positionExp].isUntilValue()) {
////		if(isUntilValue(aspectValue)) {
//			int pos1 = p1.getPosition();
//			int pos2 = p2.getPosition();
//
//			Double value = 0d;
//			String tempAspectValue = "";
//			for(int i = pos1; i <= pos2; i++) {
//				tempAspectValue = trajectory.getAspectValuePoI(pos1, aspectValue);
//				value += arrayExp[positionExp].
//							   distance(aspectType, tempAspectValue, function, limit, weight);
//			}
//
//			result = value/((pos2-pos1));
//		} else {
//			result = arrayExp[positionExp].distance(aspectType, aspectValue, function, limit, weight);
//		}

		result = arrayExp[positionExp].distance(aspectType, aspectValue, function, limit, weight, p1, p2, trajectory);
		return result;
	}

//	private boolean isUntilValue(String aspectValue) {
//		if(aspectValue != null && !aspectValue.isEmpty()) {
//			if(aspectValue.trim().charAt(0) == '-')
//				return true;
//		}
//		return false;
//	}

	public int getNumAspects() {
		return arrayExp[0].getMapAspects().keySet().size();
	}

	public Double weight(String key) {
		return mapWeight.get(key);
	}

	public Set<String> getAspects() {
		return arrayExp[0].getMapAspects().keySet();
	}

	@Override
	public String toString() {
		String result = "Query: ";

		for (Expression exp : arrayExp) {
			result += exp.getValue() + ";";
		}
		result += "\n";

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public String createSqlQuery(String schema) {
		String regexPoi = regexPoi();
		String regexCat = regexCat();

		// regex sql
		String regexSqlPoi = regexPoi == null ? "" : "regexp_matches(p.values, '" + regexPoi + "') as rxp";
		String regexSqlCat = regexCat == null ? "" : "regexp_matches(c.values, '" + regexCat + "') as rxc";
		String regex = "";
		if (regexCat != null && regexPoi != null) {
			regex = regexSqlPoi + ", " + regexSqlCat;
		} else {
			regex = regexSqlPoi.isEmpty() ? regexSqlCat : regexSqlPoi;
		}

		// column traj_fk
		String trajfk = regexPoi == null ? "c.traj_fk" : "p.traj_fk";

		// column values
		String values = "p.values AS values_poi, c.values AS values_cat";

		// from
		String tablePoi = schema + ".tb_poi p";
		String tableCat = schema + ".tb_category c";
		String from = " FROM " + tablePoi + "," + tableCat;

		// where
		String where = " WHERE p.traj_fk = c.traj_fk";

		String query = "SELECT " + trajfk + ", " + values + ", " + regex  + " " + from + " " + where;

		if(!regexSqlPoi.isEmpty() && !regexSqlCat.isEmpty()) {
			query = "SELECT * from (" + query + ") as temp WHERE rxp IS NOT NULL AND rxc IS NOT NULL";
			
		}

		return query;
		
//		select * from (SELECT p.traj_fk, 
//				regexp_matches(p.values, '(.)*(.)*((Torre_pendente_di_Pisa)$)') as rxp, 
//				regexp_matches(c.values, '(^(((w*);)*(cappelledipisa|chiesedipisa)))(.)*(.)+') as rxc, 
//				p.values AS values_poi, c.values AS values_cat  
//				FROM tripbuilder.tb_poi p,tripbuilder.tb_category c  WHERE p.traj_fk = c.traj_fk
//				) as temp where rxp is not null and rxc is not null
	}

	public Expression[] getArrayExp() {
		return arrayExp;
	}

	public void setArrayExp(Expression[] arrayExp) {
		this.arrayExp = arrayExp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public Map<String, Trajectory> getMapResultQuery() {
		return mapResultQuery;
	}

	public void setMapResultQuery(Map<String, Trajectory> mapResulQuery) {
		this.mapResultQuery = mapResulQuery;
	}

	public void addTrajectory(Trajectory t) {
		mapResultQuery.put(t.getId(), t);
	}
}