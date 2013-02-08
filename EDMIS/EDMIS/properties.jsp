<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.*"%>
<%@page import="java.sql.*"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="java.lang.reflect.Field"%>
<%!
  static Map map;
  // This method returns the name of a JDBC type.
  // Returns null if jdbcType is not recognized.
  public static String getJdbcTypeName(int jdbcType) {
    // Use reflection to populate a map of int values to names
    if (map == null) {
      map = new HashMap();
      // Get all field in java.sql.Types
      Field[] fields = java.sql.Types.class.getFields();
      for (int i = 0; i < fields.length; i++) {
        try {
          // Get field name
          String name = fields[i].getName();
          // Get field value
          Integer value = (Integer) fields[i].get(null);
          // Add to map
          map.put(value, name);
        }
        catch (IllegalAccessException e) {
        }
      }
    }
    // Return the JDBC type name
    return (String) map.get(new Integer(jdbcType));
  }
%>
<html>
<head>
<title>EDMIS - Parameters Testing</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body>
<h1>Language Detection</h1>
<script language="Javascript" type="text/JavaScript">
document.write("<p>Browser Name: " + navigator.appname + "</p>");
document.write("<p>Browser Language: " + navigator.browserLanguage + "</p>");
document.write("<p>System Language: " + navigator.systemLanguage + "</p>");
document.write("<p>User Language: " + navigator.userLanguage + "</p>");
  </script><h2>JSP</h2>
<p>  Default Language:
<%=request.getLocale()%></p>
<p>  Languages enabled:
  <br>
<%
  Enumeration e = request.getLocales();
  while (e.hasMoreElements()) {
%>
<%=e.nextElement()%>  <br>
<%}%>
</p>
<h1>Available SQL Types Used by a Database</h1>
<%
  try {
    InitialContext ctx = new InitialContext();
    DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/EDMIS");
    Connection connection = ds.getConnection();
    // Get database meta data
    DatabaseMetaData dbmd = connection.getMetaData();
    // Get type info
    ResultSet resultSet = dbmd.getTypeInfo();
    out.println("<p>MySQL Type Name, JDBC Type Name</p>");
    // Retrieve type info from the result set
    while (resultSet.next()) {
      // Get the database-specific type name
      String typeName = resultSet.getString("TYPE_NAME");
      // Get the java.sql.Types type to which this database-specific type is mapped
      short dataType = resultSet.getShort("DATA_TYPE");
      // Get the name of the java.sql.Types value.
      // This method is implemented in e291 Getting the Name of a JDBC Type
      String jdbcTypeName = getJdbcTypeName(dataType);
      out.println(typeName + ", " + jdbcTypeName + "<br>");
    }
  }
  catch (SQLException ex) {
  }
  catch (NamingException ex) {
  }
%>
<h1>System Properties</h1>
<p>
<%
  String prop;
  e = System.getProperties().propertyNames();
  while (e.hasMoreElements()) {
    prop = (String) e.nextElement();
%>
<%=prop%>  =
<%=System.getProperty(prop)%>  <br>
<%}%>
</p>
</body>
</html>
