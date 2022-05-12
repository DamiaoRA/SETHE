package producerIoT.driver.nlp.tripbuilder.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Deprecated
/**
 * Era usado para criar várias trajetórias de categorias para um mesmo idTraj.
 * 
 * @author lsi
 */
public class TreeCategory {
		private NodeCategory root;

		public TreeCategory() {
			root = new NodeCategory();
		}
		
		public NodeCategory getRoot() {
			return root;
		}

		public void setRoot(NodeCategory root) {
			this.root = root;
		}

		public void addCategory(List<String> categories) {
			root.addCategory(categories);
		}

		public void posOrdem(String id) {
			root.posOrdem(id);
		}

		public void posOrdem(PreparedStatement psCategory, String id) throws SQLException {
			root.posOrdem(psCategory, id);
		}
}