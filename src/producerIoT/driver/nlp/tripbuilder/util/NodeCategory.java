package producerIoT.driver.nlp.tripbuilder.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NodeCategory {
	private String text;
	private String value;
	private List<NodeCategory> children;

	public NodeCategory() {
		this.text = "";
		this.value = "";
		children = new ArrayList<NodeCategory>();
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void addCategory(List<String> categories) {
		if(children.isEmpty()) {
			String espaco = value.isEmpty() ? "" : " ";
			for(String category : categories) {
				NodeCategory node = new NodeCategory();
				node.setText(category);
				node.setValue(value + espaco + category);
				children.add(node);
			}
			return;
		}
		for(NodeCategory c : children) {
			c.addCategory(categories);
		}
	}

	public void posOrdem(String id) {
		if(children.isEmpty()) {
			//salvar no banco
			String split[] = getValue().split(" ");
			System.out.println(id + " " + split.length + " " + getValue());
			return;
		}
		for(NodeCategory nc : children) {
			nc.posOrdem(id);
		}
	}

	public void posOrdem(PreparedStatement psCategory, String id) throws SQLException {
		if(children.isEmpty()) {
			psCategory.setString(1, id);
			psCategory.setString(2, getValue());
			psCategory.execute();
			return;
		}
		for(NodeCategory nc : children) {
			nc.posOrdem(psCategory, id);
		}
	}
}