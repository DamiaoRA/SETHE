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
        "m4c0$",
        "foursquare",
        "sethe"
      );

    List<TimeQ> times = new ArrayList<>();
//    times.add(queryQ1());
//    times.add(queryQ2());
//    times.add(queryQ3());
//    times.add(queryQ4());
//    times.add(queryQ5());
//    times.add(queryQ6());
//    times.add(queryQ7());
    times.add(queryQ8());
    System.out.println();

    for (TimeQ time : times) {
      System.out.println(time.toString());
      System.out.println("------");
    }
  }

  private static TimeQ queryQ1() throws Exception {
    Properties prop = new Properties();
    prop.setProperty("q1_asp_cat", "Residence;Food");
    prop.setProperty("q1_proximity", ".*;~");

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
    properties.setProperty("q1_asp_cat", "Food;(Residence|Shop Service);(Food)$");
    properties.setProperty("q1_proximity", ".*; ~;~");

    return queryQ(properties);
  }

  /**
   * Consulta 3: trajetória que pare em um POI Hotel qualquer, pare em qualquer POI denominado de Bar e finalize em um
   * POI Hotel.
   *
   * Observação: na categoria do POI não explicita se é um hotel ou não, sendo categorizado como "Travel & Transport".
   *
   * @todo Dúvida: Pensei se seria interessante uma pesquisa onde o usuário inicia e finalize no mesmo hotel, sem
   * necessariamente informar um POI em específico. Seria possível?
   * Exemplos de resultados para a mesma consulta:
   * Hotel Guanabara > Manos Bar > Hotel Guanabara
   * Hotel do Bairro > Bar > Hotel do Bairro
   *
   * @return
   * @throws Exception
   */
  private static TimeQ queryQ3() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("q1_asp_poi", "Hotel( (\\w*))*;((\\w*) )*Bar;Hotel( (\\w*))*$");
    properties.setProperty("q1_proximity", "~;.*;~");

    return queryQ(properties);
  }

  /**
   * Consulta 4: trajetória que parou num POI Hospital, onde posteriormente passou por um local que pode ser Food ou
   * Shop & Service ou Residence e podendo ou não finalizar em outros locais.
   *
   * @return
   * @throws Exception
   */
  private static TimeQ queryQ4() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("q1_asp_poi", "(\\w*)*Hospital;(\\w*)*;(\\w*)*");
    properties.setProperty("q1_asp_cat", "(\\w*)*;(Food|Shop Service|Residence);(\\w*)*");
    properties.setProperty("q1_proximity", "~;~;.*");

    return queryQ(properties);
  }

  /**
   * Consulta 05: trajetória que pare em um Shop e finalize Food, esta trajetória passa pelo o dia Sunday e Monday.
   *
   * @return
   * @throws Exception
   */
  private static TimeQ queryQ5() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("q1_asp_cat", "Shop;(Food)$");
    properties.setProperty("q1_asp_day", "Sunday;Monday");
    properties.setProperty("q1_proximity", ".*;~");
    properties.setProperty("weight_day", "1");
    properties.setProperty("distance_day", "equality");
    properties.setProperty("limit_day", "1");

    return queryQ(properties);
  }

  /**
   * Consulta 6: trajetória que pare um local Arts, passe em outros locais, pare em Event com clima Clear, e
   * posteriormente no Nightlife Spot em clima qualquer.
   *
   * @return
   * @throws Exception
   */
  private static TimeQ queryQ6() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("q1_asp_cat","Arts(\\w*)*;(\\w*)*;Event;Nightlife Spot");
    properties.setProperty("q1_asp_weather", ".*;Rain;Clear;.*");
    properties.setProperty("weight_weather", "1");
    properties.setProperty("distance_weather", "equality");
    properties.setProperty("limit_weather", "1");

    return queryQ(properties);
  }

  /**
   * Consulta 7: trajetória pare no POI da categoria de Shop, e finalize no Travel & Transport ou Food ou outros locais.
   *
   * @return
   * @throws Exception
   */
  private static TimeQ queryQ7() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("q1_asp_cat","Shop( (\\w*))*;((Travel Transport|Food)( (\\w*))*$)*");
    properties.setProperty("q1_proximity", "~;.*");

    return queryQ(properties);
  }

  /**
   * Consulta 8: trajetória que inicial no Travel & Transport e finaliza no Shop & Service.
   *
   * @return
   * @throws Exception
   */
  private static TimeQ queryQ8() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("q1_asp_cat", "^(Travel Transport);(\\w*)*;(Shop Service)$");
    properties.setProperty("q1_proximity", "~;.*;~");

    return queryQ(properties);
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
