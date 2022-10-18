package sethe;

import java.util.HashMap;
import java.util.Map;

import sethe.util.Constants;
import sethe.util.StringUtils;


/**
 * An expression is an annotation in regular language with the aim of finding a 
 * point or set of points on a trajectory.
 *
 */
public class Expression {
	private String valueCategory;
	private String valuePoi;
	private int order;
	private boolean isCategory;
	private double weight;
	private boolean isFinal = false;
	private boolean isOptional = false;
	private boolean isPlus = false;
	private String cleanValue;

	private Map<String, AspectExpression> mapAspects;
	private Map<String, String> mapProximity;

	private boolean checkProximity;

	public Expression(String cat, String namePoi) {
		this.valueCategory = cat;
		this.valuePoi = namePoi;

		if(cat == null || cat.isEmpty() || cat.equals(".*"))
			this.isCategory = false;
		else
			this.isCategory = true;

		check(cat, namePoi);

//		if (isCategory)
//			cleanValue = valueCategory;
//		else 
//			cleanValue = valuePoi;
//
//		if(isOptional) {
//			cleanValue = cleanValue.substring(0, cleanValue.length()-1);
//		}

		mapAspects = new HashMap<String, AspectExpression>();
		mapProximity = new HashMap<String, String>();
		checkProximity = false;
		isFinal = false;
	}

	private void check(String cat, String namepoi) {
		String text = StringUtils.isAnyValue(cat) && namepoi !=null ? namepoi : cat;

		if(!StringUtils.isAnyValue(text)) {
			if (text.contains("?")) {//(c == '?')  {
				isOptional = true;
				cleanValue = text.replaceAll("\\?", "");//text.substring(0, text.length()-1);
			} 
			if (text.contains("+")) {//if(c == '+') {
				int i = text.lastIndexOf("+");
				if(!text.substring(i-3).contains("\\w")) {
					isPlus = true;
					cleanValue = text.replaceAll("\\+", ""); //text.substring(0, text.length()-1);
				}
			} 
			if(text.contains("*")) {
				int i = text.lastIndexOf("*");
				int k = text.lastIndexOf("\\");
				if(!text.substring(k, i).contains("\\w")) {
					isOptional = true;
					isPlus = true;
					cleanValue = text.replaceAll("\\*", ""); //text.substring(0, text.length()-1);
				}
			}
		}

		if(cleanValue == null) {
			cleanValue = text;
		}
	}

	public String getCleanValue() {
		return cleanValue;
	}

	public void addProximity(String aspectName, String value) {
		mapProximity.put(aspectName, value.trim());
	}

	public String getValueCategory() {
		return valueCategory;
	}

	public void setValueCategory(String valueCategory) {
		this.valueCategory = valueCategory;
	}

	public String getValuePoi() {
		return valuePoi;
	}

	public void setValuePoi(String valuePoi) {
		this.valuePoi = valuePoi;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getValue() {
		if(this.isCategory)
			return valueCategory;
		return valuePoi;
	}

	public void addAspect(String aspName, String value) {
		
		AspectExpression exp = new AspectExpression();
		exp.setType(aspName);
		if(isUntilValue(value)) exp.setUntil(true);
		value = value.replaceAll(Constants.REPEATED_PATTERN, "").trim();
		exp.setValue(value);
		exp.setOrder(order);

		mapAspects.put(aspName, exp);
	}

	public String getRegexPoi() {
		if(getValuePoi() == null)
			return null;
		if(getValuePoi().trim().equals(".*") || getValuePoi().trim().equals("."))
			return "(.)*";
		return "(" + getValuePoi() + ")";
	}

	public String getRegexCategory() {
		if(getValueCategory() == null)
			return null;
		if(getValueCategory().trim().equals(".*") || getValueCategory().trim().equals("."))
			return "(.)*";
		return "(" + getValueCategory() + ")";
	}

	public boolean isCategory() {
		return isCategory;
	}

	public void setCategory(boolean isCategory) {
		this.isCategory = isCategory;
	}

	public Map<String, AspectExpression> getMapAspects() {
		return mapAspects;
	}

	public void setMapAspects(Map<String, AspectExpression> mapAspects) {
		this.mapAspects = mapAspects;
	}

	public Map<String, String> getMapProximity() {
		return mapProximity;
	}

	public void setMapProximity(Map<String, String> mapProximity) {
		this.mapProximity = mapProximity;
	}
	
	public String aspectValue(String aspectType) {
		AspectExpression aspQuery = mapAspects.get(aspectType);
		return aspQuery.getValue();
	}
	
	public AspectExpression searchAspectExpression(String aspectType) {
		AspectExpression aspQuery = mapAspects.get(aspectType);
		return aspQuery;
	}

//	public Double distance(String aspectType, String function, 
//			Double limit, Double weight, PoI p1, PoI p2, Trajectory trajectory) {
//
//		AspectExpression aspQuery = mapAspects.get(aspectType);
//		String idealValue = aspQuery.getValue();
//		
//		String p2AspectValue = p2.getAspects().get(aspectType);
//
//		if(idealValue.equals(Constants.ANY_VALUE))
//			return 1d;
//
//		double result = 0d;
//
//		//
//		if(aspQuery.isUntil()) {
//			int pos1 = p1.getPosition();
//			int pos2 = p2.getPosition();
//
//			Double value = 0d;
//			String tempAspectValue = "";
//			for(int i = pos1+1; i <= pos2; i++) {
//				tempAspectValue = trajectory.getAspectValuePoI(i, aspectType);
//				value += Distance.calc(function, tempAspectValue, idealValue, weight, limit);
//			}
//
//			result = value/((pos2-pos1));
//		//
//		} else {
//			result = Distance.calc(function, p2AspectValue, idealValue, weight, limit);
//		}
//		return result;
//	}
//
	private boolean isUntilValue(String aspect) {
		if(aspect != null && !aspect.isEmpty()) {
			if(aspect.trim().startsWith(Constants.REPEATED))
				return true;
		}
		return false;
	}

	public boolean isCheckProximity() {
		return checkProximity;
	}

	public void setCheckProximity(boolean checkProximity) {
		this.checkProximity = checkProximity;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setIsFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public boolean isPlus() {
		return isPlus;
	}

	public void setPLus(boolean isPLus) {
		this.isPlus = isPLus;
	}

	public boolean isAnyValue() {
		return StringUtils.isAnyValue(cleanValue);
	}

//	public boolean isOptional() {
//		if(isCategory) {
//			if (valueCategory.charAt(valueCategory.length() - 1) == '?')
//				return true;
//		} else if (valuePoi.charAt(valuePoi.length() - 1) == '?') {
//			return true;
//		}
//		return false;
//	}
}