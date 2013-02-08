<%@page import="java.util.*"%>
<%@page import="com.progdan.edmis.control.documents.DocumentsStatisticsController"%>
<%@page import="com.progdan.edmis.model.document.Document"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
  User user = (User)session.getAttribute("User");
  DocumentsStatisticsController doc = DocumentsStatisticsController.getInstance();
%>
<TABLE class=sideblock id=block_calendar_upcoming style="WIDTH: 100%" cellSpacing=0 cellPadding=5>
  <THEAD>
    <TR>
      <TD class=sideblockheading>
        <DIV style="FLOAT: left"><a href="alldocs.jsp"><img src="icons/docs.gif" border="0" align="absmiddle"></a> <%= labels.getString("alldocs") %>        </DIV>
      </TD>
    </TR>
  </THEAD>
  <TBODY style=\"BACKGROUND-COLOR: #c6bda8\">
    <TR>
      <TD class=sideblockmain>
        <DIV style="FONT-SIZE: 0.8em; TEXT-ALIGN: center"><%= doc.getNumDocs(currentLocale, user) + " " + labels.getString("documents") %><br>
<%= doc.getTotalSize(currentLocale, user) %> bytes
</DIV>
      </TD>
    </TR>
  </TBODY>
</TABLE>
