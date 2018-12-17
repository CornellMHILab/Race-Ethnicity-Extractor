package readers

import gov.va.vinci.leo.cr.SQLServerPagedDatabaseCollectionReader;
String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
String url = "jdbc:sqlserver://<database_engine>:1433;databasename=<database_name>;integratedSecurity=true"
String query = "SELECT  DocID, DocText   FROM <database_name>.<schema>.<table_name> order by DocID"

// SQLServerPagedDatabaseCollectionReader(String driver, String url, String username, String password, String query, String idColumn, String noteColumn, int pageSize, int firstOffset, int maxOffset)

reader = new SQLServerPagedDatabaseCollectionReader(
        driver,
        url,
        "", "",
        query,
        "docid","doctext",   /// Make sure that these fields are low case.
       20000, 0, 100000)
