<%@page contentType="text/html; charset=iso-8859-1" language="java"%>
<%@page import="java.util.*"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<HR noShade SIZE=1>
<TABLE cellSpacing=0 cellPadding=5 width="100%" border=0>
  <TBODY>
    <TR>
      <TD align=center>
      <%if (session.getAttribute("User") == null) {      %>
        <P class=logininfo><%= labels.getString("nologin") %>        <%} else {        %>

        <P class=logininfo><%= labels.getString("logged") + " " + ((User)session.getAttribute("User")).getLogin() %>          .
        <%}        %>
          <a href="login.jsp" target=_top><%= labels.getString("login") %>          </A>
        </P>
        <P align=center><%= labels.getString("copyright") %>          <br>
<%= labels.getString("copy") %>        </P>
      </TD>
    </TR>
  </TBODY>
</TABLE>
