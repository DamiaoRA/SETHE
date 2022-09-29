package sethe.util;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class CsvToSql {

  private static final String TRAJECTORY_ID = "trajectory_id";
  private static final String TRAJECTORY_VALUE = "trajectory_value";
  private static final String DELIMIT = ",";
  private static final String DELIMIT_TRAJECTORY = ";";
  private static final short MAX_LINE = 500;

  public static void main(String[] args) throws IOException {
    boolean sanitize = true;

    if (args.length == 0) {
      System.out.println("Por favor, informe o caminho do arquivo CSV.");
    } else if (args.length < 2) {
      System.out.println("Por favor, informe o schema do banco de dados.");
    } else if (args.length == 3) {
      sanitize = !Objects.equals(args[2], "--no-sanitize");
    }

    String pathFile = args[0];
    String schema = args[1];

    csvToSql(pathFile, schema, sanitize);
  }

  private static void csvToSql(
    final String pathFile,
    final String schema,
    boolean sanitizeValue
  ) throws IOException {
    File fileCsv = new File(pathFile);
    String pathFileSql = fileCsv.getAbsolutePath().replace(".csv", ".sql");
    File fileSql = new File(pathFileSql);
    String tableName = schema + "." + fileCsv.getName().replace(".csv", "");

    BufferedReader bufferedReader = new BufferedReader(new FileReader(fileCsv));
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileSql));

    String createTableSql = String.format(
      "CREATE TABLE IF NOT EXISTS %s (%s varchar(30) UNIQUE NOT NULL, %s TEXT NOT NULL);\n",
      tableName,
      TRAJECTORY_ID,
      TRAJECTORY_VALUE
    );

    try {
      bufferedReader.readLine();
      bufferedWriter.write(createTableSql);
      bufferedWriter.flush();

      int countLine = 0;
      Properties current = getPointTrajectory(
        bufferedReader.readLine(),
        sanitizeValue
      );

      if (!current.isEmpty()) {
        String trajectoryId = current.getProperty(TRAJECTORY_ID);
        String trajectory = current.getProperty(TRAJECTORY_VALUE);

        Properties next = getPointTrajectory(
          bufferedReader.readLine(),
          sanitizeValue
        );

        while (!next.isEmpty()) {
          if (countLine > MAX_LINE) {
            bufferedWriter.flush();
            countLine = 0;
          }

          if (
            Objects.equals(
              current.getProperty(TRAJECTORY_ID),
              next.getProperty(TRAJECTORY_ID)
            )
          ) {
            trajectoryId = current.getProperty(TRAJECTORY_ID);
            trajectory +=
              DELIMIT_TRAJECTORY + next.getProperty(TRAJECTORY_VALUE);
            current = (Properties) next.clone();
          } else {
            bufferedWriter.write(
              createInsertSql(tableName, trajectoryId, trajectory)
            );

            current = (Properties) next.clone();
            trajectoryId = current.getProperty(TRAJECTORY_ID);
            trajectory = current.getProperty(TRAJECTORY_VALUE);
          }

          next = getPointTrajectory(bufferedReader.readLine(), sanitizeValue);
          countLine++;
        }

        bufferedWriter.write(
          createInsertSql(tableName, trajectoryId, trajectory)
        );
      }
    } finally {
      bufferedReader.close();
      bufferedWriter.close();
    }
  }

  private static Properties getPointTrajectory(String line, boolean sanitize) {
    Properties pointTrajectory = new Properties();

    if (line != null) {
      String[] point = line.split(DELIMIT);
      pointTrajectory.setProperty(
        TRAJECTORY_ID,
        StringUtils.sanitize(point[0])
      );
      pointTrajectory.setProperty(
        TRAJECTORY_VALUE,
        sanitize ? StringUtils.sanitize(point[1]) : point[1]
      );
    }

    return pointTrajectory;
  }

  private static String createInsertSql(
    String tableName,
    String trajectoryId,
    String trajectory
  ) {
    String insertSql = "INSERT INTO %s VALUES ('%s', '%s');\n";

    return String.format(insertSql, tableName, trajectoryId, trajectory);
  }
}
