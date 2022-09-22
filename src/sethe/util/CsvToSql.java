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
    writeCsv(pathFile);
  }

  private static void writeCsv(String pathFile) throws IOException {
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

      Properties currentPoint = getPointTrajectory(bufferedReader.readLine());
      Properties nextPoint = getPointTrajectory(bufferedReader.readLine());

      int countLine = 2;
      String trajectory = "";
      String trajectoryId = "";

      while (!currentPoint.isEmpty() && !nextPoint.isEmpty()) {
        if (
          Objects.equals(
            currentPoint.getProperty(TRAJECTORY_ID),
            nextPoint.getProperty(TRAJECTORY_ID)
          )
        ) {
          trajectoryId = currentPoint.getProperty(TRAJECTORY_ID);
          trajectory +=
            currentPoint.getProperty(TRAJECTORY_VALUE) +
            delimitTrajectory +
            nextPoint.getProperty(TRAJECTORY_VALUE) +
            delimitTrajectory;

          currentPoint = getPointTrajectory(bufferedReader.readLine());
          nextPoint = getPointTrajectory(bufferedReader.readLine());

          if (currentPoint.isEmpty()) {
            createInsertSql(tableName, trajectoryId, trajectory);
          }
        } else {
          trajectory += currentPoint.getProperty(TRAJECTORY_VALUE);
          createInsertSql(
            tableName,
            currentPoint.getProperty(TRAJECTORY_ID),
            trajectory
          );
          trajectory = "";
          currentPoint = (Properties) nextPoint.clone();
        }
      }

      if (currentPoint.isEmpty() && !nextPoint.isEmpty()) {
        createInsertSql(
          tableName,
          nextPoint.getProperty(TRAJECTORY_ID),
          nextPoint.getProperty(TRAJECTORY_VALUE)
        );
      } else if (!currentPoint.isEmpty() && nextPoint.isEmpty()) {
        createInsertSql(
          tableName,
          currentPoint.getProperty(TRAJECTORY_ID),
          trajectory + currentPoint.getProperty(TRAJECTORY_VALUE)
        );
      } else if (!currentPoint.isEmpty() && !nextPoint.isEmpty()) {
        createInsertSql(
          tableName,
          currentPoint.getProperty(TRAJECTORY_ID),
          trajectory
        );
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

  private static String createInsertSql(
    String tableName,
    String trajectoryId,
    String trajectory
  ) {
    String insertSql = "INSERT INTO %s VALUES ('%s', '%s');";
    System.out.println(
      String.format(insertSql, tableName, trajectoryId, trajectory)
    );
    return "";
  }
}
