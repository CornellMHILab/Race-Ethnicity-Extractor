package listeners

import gov.va.vinci.ef.listeners.BasicDatabaseListener

int batchSize = 1000

// String url = "jdbc:sqlserver://<server_address>:1433;databasename=<database_name>;integratedSecurity=false"
// String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
// String dbUser = "<user>"
// String dbPwd = "<password>"
// String dbsName = "<database_name>"
// String tableName = "<table_name>"

// String url = "jdbc:mysql://localhost:3306/<database_name>?autoReconnect=true&useSSL=false"
// String driver = "com.mysql.jdbc.Driver"
// String dbUser = "<user>"
// String dbPwd = "<password>"
// String dbsName = "<database_name>"
// String tableName = "<table_name>"

// incomingTypes = "gov.va.vinci.ef.types.Relation"
incomingTypes = "gov.va.vinci.ef.types.Classify"

fieldList = [
        ["DocID", "0", "varchar(500)"],
        ["AfricanAmerican", "-1", "varchar(500)"],
        ["Hispanic", "-1", "varchar(500)"]
]

boolean dropExisting = false;
listener = BasicDatabaseListener.createNewListener(
        driver,
        url,
        dbUser,
        dbPwd,
        dbsName,
        tableName,
        batchSize,
        fieldList,
        incomingTypes)

// Comment out the statement below if you want to add to the existing table
listener.createTable(dropExisting);
