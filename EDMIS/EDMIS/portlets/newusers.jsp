<%@page import="java.util.*"%>
<%@page import="com.progdan.edmis.control.users.NewUsersController"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
  User user = (User)session.getAttribute("User");
%>
<TABLE class=sideblock id=block_calendar_upcoming style="WIDTH: 100%" cellSpacing=0 cellPadding=5>
  <THEAD>
    <TR>
      <TD class=sideblockheading>
        <DIV style="FLOAT: left"><img src="images/user.gif" border="0" align="absmiddle"> <%= labels.getString("newusers") %>        </DIV>
      </TD>
    </TR>
  </THEAD>
  <TBODY style="BACKGROUND-COLOR: #c6bda8">
    <TR>
      <TD class=sideblockmain>
        <DIV style="FONT-SIZE: 0.8em">
          <p>
          <%
            NewUsersController newusers = NewUsersController.getInstance();
            Vector users = newusers.getNewUsers(user);
            int n = users.size();
            if (n == 0) {
              out.println(labels.getString("none"));
            }
            else {
              for (int i = 0; i < n; i++) {
                out.println("<IMG src=\"images/user.gif\" width=16 height=16 vspace=4 border=0 align=\"absmiddle\"> " + users.get(i) + "<br>");
              }
            }
          %>
          </p>
        </DIV>
        <CENTER>        </CENTER>
      </TD>
    </TR>
  </TBODY>
</TABLE>
