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
        <DIV style="FLOAT: left"><img src="icons/new.gif" border="0" align="absmiddle"> <%= labels.getString("newdocs") %>        </DIV>
      </TD>
    </TR>
  </THEAD>
  <TBODY style=\"BACKGROUND-COLOR: #c6bda8\">
    <TR>
      <TD class=sideblockmain>        <DIV style="FONT-SIZE: 0.8em">
		<%
		   Vector newDocs = doc.getNewDocs(user);
		   int n = newDocs.size();
		   if (n == 0) {
              out.println(labels.getString("none"));
            }
            else {
			  Document document;
              for (int i = 0; i < n; i++) {
			    document = (Document)newDocs.get(i);
			    out.println("<a href=\"docedit.jsp?doc=" + document.getId() + "\"><IMG src=\"icons/" + document.getFormat() + ".gif\" width=16 height=16 vspace=4 border=0 align=\"absmiddle\"></a> " + document.getName() + "<br>");
			  }
			}
		%></DIV>
        <CENTER>
          <FONT size=-2>
            <BR>
          </FONT>
        </CENTER>
      </TD>
    </TR>
  </TBODY>
</TABLE>
