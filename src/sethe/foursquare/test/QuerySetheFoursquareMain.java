package sethe.foursquare.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import sethe.CompositeQuery;
import sethe.tripbuilder.QuerySETHEMain;
import sethe.util.TimeQ;

public class QuerySetheFoursquareMain {

  private static QuerySETHEMain qt = null;

  public static void main(String[] args) throws Exception {
    qt =
      new QuerySETHEMain(
        "localhost",
        "5432",
        "juliafealves",
        "psql@bd",
        "foursquare",
        "sethe"
      );

    List<TimeQ> times = new ArrayList<>();
    times.add(queryQ1());
    times.add(queryQ2());
    times.add(queryQ3());
    times.add(queryQ6());
    times.add(queryQ7());
    System.out.println();

    for (TimeQ t : times) {
      System.out.println(t.toString());
    }
  }

  private static TimeQ queryQ1() throws Exception {
    Properties prop = new Properties();
    prop.setProperty("q1_asp_cat", "Residence ; Food");
    prop.setProperty("q1_proximity", ".* ; ~");

    return queryQ(prop);
  }

  /**
   * Consulta 02: trajetória onde pare na categoria "Food", e pare em um "Residence" ou "Shop & Service"
   * e finalize parando em um "Food".
   *
   * @return
   * @throws Exception
   */
  private static TimeQ queryQ2() throws Exception {
    Properties properties = new Properties();
    properties.setProperty(
      "q1_asp_cat",
      "Food ; (Residence|Shop Service) ; (Food)$"
    );
    properties.setProperty("q1_proximity", ".* ; ~ ; ~");

    return queryQ(properties);
  }

  private static TimeQ queryQ3() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("q1_asp_cat", "Shop Service ; Residence");
    properties.setProperty("q1_proximity", ".* ; ~");

    return queryQ(properties);
  }

  private static TimeQ queryQ6() throws Exception {
    Properties prop = new Properties();

    prop.setProperty(
      "q1_asp_cat",
      "Shop( (\\w*))*;((Travel Transport|Food)( (\\w*))*$)*"
    );
    prop.setProperty("q1_proximity", ".* ; .*");

    return queryQ(prop);
  }

  private static TimeQ queryQ7() throws Exception {
    Properties prop = new Properties();

    prop.setProperty("q1_asp_cat", "Shop;Food");
    prop.setProperty("q1_asp_day", "Monday;Sunday");
    prop.setProperty("q1_proximity", ".*;.*");

    prop.setProperty("weight_day", "1");
    prop.setProperty("distance_day", "equality");
    prop.setProperty("limit_day", "1");

    return queryQ(prop);
  }

  private static TimeQ queryQ(Properties prop) throws Exception {
    prop.setProperty("schema", "foursquare");
    prop.setProperty("dist_func", "jaccard");
    prop.setProperty("pk_column_name", "trajectory_id");
    prop.setProperty("value_column_name", "trajectory_value");
    prop.setProperty("delimiter", ";");

    CompositeQuery cq = QuerySETHEMain.loadQuery(prop);
    long count = 0l;

    long t1 = System.currentTimeMillis();
    qt.executeQuery(cq);
    while (!cq.getFinalResult().isEmpty()) {
      cq.getFinalResult().poll().print();
      count++;
    }

    long t2 = System.currentTimeMillis();

    System.out.println("count: " + count);
    TimeQ time = new TimeQ(t1, t2, "Q1", count);

    return time;
  }
}
