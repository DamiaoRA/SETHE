package producerIoT.driver.nlp.tripbuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import producerIoT.driver.nlp.Filter;
import producerIoT.driver.nlp.Query;
import producerIoT.driver.nlp.Trajectory;
import sethe.utilities.StringUtils;

/**
 * Consulta a trajetória com base nos PoI ou (categoria de PoI) e usa o contexto para ordenar os resultados
 *
 */
public class QueryTrajectory {
	private Connection con;
	private Statement st;
	private String schema = "trajSem";

	public QueryTrajectory(String url1, String port, String user, String pass, String schema) throws SQLException {
		String url = "jdbc:postgresql://" + url1 + ":" + port + "/trajetoria";
		this.schema = schema;
		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", pass);
		con = DriverManager.getConnection(url, props);
		st = con.createStatement();
	}

	/**
	 * Retorna lista de trajetória como resultado da consulta
	 * @param query
	 * @return
	 * @throws SQLException
	 */
//	private List<Trajectory> executeQuery(Filter query) throws Exception {
//		List<Trajectory> listTraj = searchTrajectories(query);//TODO trocar List por Map
//		for(Trajectory t : listTraj) {
//			t.calcSubtrajectory();
//			t.printGraph();
//		}
//		Collections.sort(listTraj);
//		return listTraj;
//	}

	private void executeQuery(Query query) throws Exception {
		for(Filter filter : query.getMapFilter().values()) {
			searchTrajectories(filter);
			for(Trajectory t : filter.getMapResultQuery().values()) {
//				t.printGraph();
				query.add(t);
			}
		}
	}

	/**
	 * Pesquisa as trajetórias que obedecem o regex descrito na query
	 * @param filter
	 * @return
	 * @throws SQLException
	 */
	private void searchTrajectories(Filter filter) throws Exception {
		String sql = filter.createSqlQuery(schema);

		ResultSet rs = st.executeQuery(sql); //TODO pensar em como fazer paginação
		while(rs.next()) {
			Trajectory t = new Trajectory();
			t.setId(rs.getString(1));
			t.loadText(rs.getString(2), rs.getString(3));//Poi values, category values
			t.setQuery(filter);
			searchAspects(t, filter.getAspects());

			//Calculando as subtrajetórias
			t.calcSubtrajectory();
			//

			filter.addTrajectory(t);
			filter.getQuery().add(t);
		}

//		List<Trajectory> result = new ArrayList<Trajectory>();
//		String sql = filter.createSqlQuery(schema);
//
//		ResultSet rs = st.executeQuery(sql); //TODO pensar em como fazer paginação
//		while(rs.next()) {
//			Trajectory t = new Trajectory();
//			t.setId(rs.getString(1));
//			t.loadText(rs.getString(3), rs.getString(4));//Poi values, category values
//			t.setQuery(filter);
//			searchAspects(t, filter.getAspects());
//			result.add(t);
//		}
//
//		return result;
	}

	private void searchAspects(Trajectory t, Set<String> aspects) throws Exception {
		for(String a : aspects) {
			String text = queryAspect(t.getId(), a);
			t.addAspect(a, text.split(" "));
		}
	}

	private String queryAspect(String trajId, String a) throws SQLException {
		String table = schema + ".tb_" + a;
		String sql = "SELECT values FROM " + table + " WHERE traj_fk = '" + trajId + "'";
		ResultSet rs = st.executeQuery(sql);
		if(rs.next()) {
			return rs.getString(1);
		}
		return "";
	}

	private void close() throws SQLException {
		con.close();
	}

	public static void main(String[] args) throws Exception {

		//Properties file
		InputStream is = QueryTrajectory.class.getResourceAsStream("semantic.properties");
		Properties properties = new Properties();
		properties.load(new InputStreamReader(is, Charset.forName("UTF-8")));

		String schema = properties.getProperty("schema");
		Query query = loadQuery(properties);

		QueryTrajectory qt = null;

		try {
			qt = new QueryTrajectory("localhost", "25432", "postgres", "postgres", schema);
			long t1 = System.currentTimeMillis();

			qt.executeQuery(query);

//			for(Trajectory t : query.getFinalResult()) {
//				t.print();
//			}
			int count = 0;
			while(!query.getFinalResult().isEmpty()) {
				query.getFinalResult().poll().print();
				count++;
			}
			long t2 = System.currentTimeMillis();

			System.out.println("Total: " + count + "\nTempo total: " +  (t2 - t1));
		} finally{
			if(qt != null)
				qt.close();

		}

//		//Properties file
//		InputStream is = QueryTrajectory.class.getResourceAsStream("semantic.properties");
//		Properties properties = new Properties();
//		properties.load(new InputStreamReader(is, Charset.forName("UTF-8")));
//
//		String schema = properties.getProperty("schema");
//
//		Filter query = new Filter();
//		loadQuery(query, properties);
//
//		QueryTrajectory qt = null;
//		try {
//			qt = new QueryTrajectory("localhost", "25432", "postgres", "postgres", schema);
//			List<Trajectory> result = qt.executeQuery(query);
//
//			long t2 = System.currentTimeMillis();
//
//			System.out.println(query);
//			for(Trajectory t : result) {
//				t.print();
//			}
//
//			System.out.println("Tempo total: " + (t2 - t1));
//		} finally{
//			if(qt != null)
//				qt.close();
//
//		}
	}

	@SuppressWarnings("rawtypes")
	public static Query loadQuery(Properties properties) {
		Query query = new Query();
		String split = properties.getProperty("split");
		int arraySize = 0;

		Map<String, Filter> filters = new HashMap<String, Filter>();

		//Getting filters name
		for(Enumeration e = properties.keys();e.hasMoreElements();) {
			String key = e.nextElement().toString();
			if(key.contains("_asp")) {
				String filterName = key.substring(0, key.indexOf("_"));
				filters.put(filterName, null);
			}
		}

		for(String fname : filters.keySet()) {
			Filter f = new Filter(fname);
			f.setQuery(query);

			//Getting PoIs
			String p = properties.getProperty(fname + "_asp_poi");
			String[] arrayPoi = p != null ? p.split(split) : new String[0];
			if(p != null ) arraySize = arrayPoi.length;

			//Getting category
			String c = properties.getProperty(fname + "_asp_cat");
			String[] arrayCat = c != null? c.split(split) : new String[0];
			if(c != null ) arraySize = arrayCat.length;
			
			f.init(arraySize);

			for(int i = 0; i < arraySize; i++) {
				String cat = arrayCat.length > 0 ? arrayCat[i].trim() : null;
				String poi = arrayPoi.length > 0 ? arrayPoi[i].trim() : null;
				f.addExpression(i, cat, poi);
			}

			//Getting proximity
			String proximity = properties.getProperty(fname + "_proximity");
			if(!StringUtils.isEmpty(proximity)) {
				String[] proximityValues = proximity.split(split);
				for(int i = 0; i < proximityValues.length; i++) {
					f.addProximityExpression(i, proximityValues[i]);
				}
			}

			//Getting aspects
			for(Enumeration e = properties.keys();e.hasMoreElements();) {
				String key = e.nextElement().toString();
				if(key.startsWith(fname + "_asp_") && !key.contains("asp_poi") && !key.contains("asp_cat")) {
					String[] aspValues = properties.getProperty(key).split(";");
					key = key.replace(fname + "_asp_", "");
					for(int i = 0; i < aspValues.length; i++) {
						f.addAspectExpression(i, key, aspValues[i].trim());
					}
				} else if (key.startsWith("weight_")) {
					String asp = key.substring(key.indexOf("_") + 1);
					Double value = Double.parseDouble(properties.getProperty(key));
					f.addWeight(asp, value);
				} else if (key.startsWith("distance_")) {
					String asp = key.substring(key.indexOf("_") + 1);
					String value = properties.getProperty(key);
					f.addDistanceFunction(asp, value.trim());
				} else if (key.startsWith("limit_")) {
					String asp = key.substring(key.indexOf("_") + 1);
					Double value = Double.parseDouble(properties.getProperty(key));
					f.addLimitAspValue(asp, value);
				}
			}

			filters.put(fname, f);
		}

		query.setMapFilter(filters);

		return query;
	}
	

	@SuppressWarnings("rawtypes")
	public static void loadQuery_old(Filter query, Properties properties) {
		String split = properties.getProperty("split");
		int arraySize = 0;
		
		//Getting PoIs
		String p = properties.getProperty("asp_poi");
		String[] arrayPoi = p != null ? p.split(split) : new String[0];
		if(p != null ) arraySize = arrayPoi.length;

		//Getting category
		String c = properties.getProperty("asp_cat");
		String[] arrayCat = c != null? c.split(split) : new String[0];
		if(c != null ) arraySize = arrayCat.length;

		query.init(arraySize);

		for(int i = 0; i < arraySize; i++) {
			String cat = arrayCat.length > 0 ? arrayCat[i].trim() : null;
			String poi = arrayPoi.length > 0 ? arrayPoi[i].trim() : null;
			query.addExpression(i, cat, poi);
		}

		//Getting proximity
		String proximity = properties.getProperty("proximity");
		String[] proximityValues = proximity.split(split);
		for(int i = 0; i < proximityValues.length; i++) {
			query.addProximityExpression(i, proximityValues[i]);
		}

		//Getting aspects
		for(Enumeration e = properties.keys();e.hasMoreElements();) {
			String key = e.nextElement().toString();
			if(key.startsWith("asp_") && !key.equals("asp_poi") && !key.equals("asp_cat")) {
				String[] aspValues = properties.getProperty(key).split(";");
				key = key.replace("asp_", "");
				for(int i = 0; i < aspValues.length; i++) {
					query.addAspectExpression(i, key, aspValues[i]);
				}
			} else if (key.startsWith("weight_")) {
				String asp = key.substring(key.indexOf("_") + 1);
				Double value = Double.parseDouble(properties.getProperty(key));
				query.addWeight(asp, value);
			} else if (key.startsWith("distance_")) {
				String asp = key.substring(key.indexOf("_") + 1);
				String value = properties.getProperty(key);
				query.addDistanceFunction(asp, value);
			} else if (key.startsWith("limit_")) {
				String asp = key.substring(key.indexOf("_") + 1);
				Double value = Double.parseDouble(properties.getProperty(key));
				query.addLimitAspValue(asp, value);
			}
		}
	}
}