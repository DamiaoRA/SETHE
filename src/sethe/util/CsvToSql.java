package sethe.util;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class CsvToSql {
  private static final String TRAJECTORY_ID = "trajectory_id";
  private static final String TRAJECTORY_VALUE = "trajectory_value";
  private static final String delimit = ",";
  private static final String delimitTrajectory = ";";
  private static final String schema = "foursquare";

  public static void main(String[] args) throws IOException {
    String pathFile = args[0];
    csvToSql(pathFile);
  }

  private static void csvToSql(String pathFile) throws IOException {
    File fileCsv = new File(pathFile);
    String pathFileSql = fileCsv.getAbsolutePath().replace(".csv", ".sql");
    File fileSql = new File(pathFileSql);
    String tableName = schema + "." + fileCsv.getName().replace(".csv", "");

    BufferedReader bufferedReader = new BufferedReader(new FileReader(fileCsv));
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileSql));

    String createTableSql = String.format(
      "CREATE TABLE %s (%s varchar(30) UNIQUE NOT NULL, %s TEXT NOT NULL);",
      tableName,
      TRAJECTORY_ID,
      TRAJECTORY_VALUE
    );

    try {
      bufferedReader.readLine();
      bufferedWriter.write(createTableSql);
      bufferedWriter.flush();

      Properties current = getPointTrajectory(bufferedReader.readLine());

      if (!current.isEmpty()) {
        String trajectoryId = current.getProperty(TRAJECTORY_ID);
        String trajectory = current.getProperty(TRAJECTORY_VALUE);

        Properties next = getPointTrajectory(bufferedReader.readLine());

        while (!next.isEmpty()) {
          if (Objects.equals( current.getProperty(TRAJECTORY_ID), next.getProperty(TRAJECTORY_ID))) {
            trajectoryId = current.getProperty(TRAJECTORY_ID);
            trajectory += delimitTrajectory + next.getProperty(TRAJECTORY_VALUE);
            current = (Properties) next.clone();
          } else {
            createInsertSql(tableName, trajectoryId, trajectory);
            current = (Properties) next.clone();
            trajectoryId = current.getProperty(TRAJECTORY_ID);
            trajectory = current.getProperty(TRAJECTORY_VALUE);
          }

          next = getPointTrajectory(bufferedReader.readLine());
        }

        createInsertSql(tableName, trajectoryId, trajectory);
      }
    } finally {
      bufferedReader.close();
      bufferedWriter.close();
    }
  }

  private static Properties getPointTrajectory(String line) {
    Properties pointTrajectory = new Properties();

    if (line != null) {
      String[] point = line.split(delimit);
      pointTrajectory.setProperty(TRAJECTORY_ID, point[0]);
      pointTrajectory.setProperty(TRAJECTORY_VALUE, point[1]);
    }

    return pointTrajectory;
  }

  private static String createInsertSql(String tableName, String trajectoryId, String trajectory) {
    String insertSql = "INSERT INTO %s VALUES ('%s', '%s');";

    return String.format(insertSql, tableName, trajectoryId, trajectory);
  }
}
