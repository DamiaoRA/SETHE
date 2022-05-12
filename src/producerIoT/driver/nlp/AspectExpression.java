package producerIoT.driver.nlp;

/**
 * Classe que respresenta a expressão em linguagem regular de uma aspecto da trajetória.
 *
 */
public class AspectExpression {
	private String value;
	private String type;
	private Double weight;
	/**
	 * Diz se a expressão contém um operador until, ou seja, o aspecto deve ser igual para todas as paradas anteriores até a penultima expressão antes desta.
	 */
	private boolean until; 
	private int order;

	public AspectExpression() {
		until = false;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public boolean isUntil() {
		return until;
	}
	public void setUntil(boolean until) {
		this.until = until;
	}
}