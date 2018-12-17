package readers

import gov.va.vinci.leo.cr.BatchDatabaseCollectionReader;

String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
String url = "jdbc:sqlserver://vits-archsqlp03.med.cornell.edu;databasename=DM_NLP;integratedSecurity=false"
String username="usr_dm_nlp";
String password="us3r_83_Nlp";
String query = "SELECT top 1000 CONCAT(PAT_ID, '|', PAT_ENC_CSN_ID, '|', NOTE_ID, '|', NOTE_CSN_ID, '|', NOTE_TYPE, '|', CONVERT(VARCHAR, CONTACT_DATE,121) ) AS ID, NOTE_TEXT FROM DM_NLP.dbo.PDEX_EVAL_NOTES"


// String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
// String url = "jdbc:sqlserver://<server_address>:1433;databasename=<database_name>;integratedSecurity=false"
// String username="<user>";
// String password="<password>";
// String query = "SELECT top 1000 ID, NOTE_TEXT FROM <table_name>"

// String driver = "com.mysql.jdbc.Driver"
// String url = "jdbc:mysql://localhost:3306/<database_name>?autoReconnect=true&useSSL=false"
// String username="<user>";
// String password="<password>";
// String query = "SELECT ID, NOTE_TEXT FROM <table_name> limit 1000"


int startingIndex = 0;
int endingIndex = 10;
int batch_size = 10000;

reader = new BatchDatabaseCollectionReader(
        driver,
        url,
        username,
        password,
        query,
        "id","note_text",   /// Make sure that the names of the fields are low case.
        startingIndex , endingIndex
        , batch_size)
