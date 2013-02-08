<%@page contentType="text/html; charset=iso-8859-1" language="java"%>
<%@page import="java.util.*"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<script language="JavaScript" type="text/JavaScript">
window.moveTo(0,0);
//window.resizeTo(screen.width,screen.height-30);
window.resizeTo(self.screen.availWidth,self.screen.availHeight);
</script><table width="915" border="0">
  <tr>
    <td width="109">
      <div align="center">
        <a href="http://progdan.no-ip.org:25000" target="_blank">
          <img src="images/progdan.gif" alt="[ ProgDan ]" width="107" height="93" border="0">
        </a>
      </div>
    </td>
    <td>
      <div align="center">
        <img src="images/title.gif" alt="[ EDMIS ]" width="600" height="60">
      </div>
    </td>
    <td width="85" class=headerhomemenu>
      <div>
        <FONT size=2>
        <%if (session.getAttribute("User") == null) {        %>
          <A href="login.jsp"><%= labels.getString("login") %>          </A>
        <%} else {        %>
<%= labels.getString("logged") + " " + ((User)session.getAttribute("User")).getLogin() %>.
          <A href="index.jsp?logout=1"><%= labels.getString("logout") %>          </A>
        <%}        %>
        </FONT>
      </div>
    </td>
  </tr>
</table>
