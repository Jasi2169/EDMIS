<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.*"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<HTML>
<HEAD>
<TITLE><%= labels.getString("edmis") %>:
<%= labels.getString("accountconfirm") %></TITLE>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>@import url( styles/htmlarea.css );</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
</HEAD>
<BODY bgColor=#f7f6f1>
<%@page import="com.progdan.edmis.control.user.ActivateUserControl"%>
<%@page import="com.progdan.edmis.error.user.UnknowUserException"%>
<%@page import="com.progdan.edmis.control.crypt.JCrypt"%>
<table width="100%" border="0">
  <tr>
    <td>&nbsp;</td>
    <td width="927">
      <!-- #include file="common/header.jsp" -->
    <jsp:include page="common/header.jsp" flush="true"/>
      <TABLE cellSpacing=0 cellPadding=3 width="915" border=0>
        <TBODY>
          <TR>
            <TD class=navbar bgColor=#c6bda8>
              <a href="index.jsp" target=_top><%= labels.getString("edmis") %>              </A>
              &raquo;
              <a href="login.jsp"><%= labels.getString("login") %>              </a>
              &raquo;
<%= labels.getString("newaccount") %>            </TD>
            <TD class=navbar vAlign=top align=right width=20 bgColor=#c6bda8>
              <DIV align=right>              </DIV>
            </TD>
          </TR>
        </TBODY>
      </TABLE>
      <IMG height=5 alt="" src="login_files/spacer.gif" width=1>
      <BR>
      <TABLE width="80%" align=center cellPadding=20>
        <TBODY>
          <TR>
            <TD class=generalbox bgColor=#c6bda8>
            <%
              String login = request.getParameter("login");
              if (login == null) {
                login = "";
              }
              String secret = request.getParameter("secret");
              if (secret == null) {
                secret = "";
              }
              if (secret.compareTo(JCrypt.crypt("any string", login)) == 0) {
                ActivateUserControl user = new ActivateUserControl();
                try {
                  user.activate(login);
            %>
              <H2><%= labels.getString("activation") %>              </H2>
              <P><%= labels.getString("click") %>                <a href="login.jsp"><%= labels.getString("here") %>                </a>
<%= labels.getString("loginnow") %>              </P>
            <%} catch (UnknowUserException e) {            %>
              <H2><%= labels.getString("username") %><%=login%><%= labels.getString("unknow") %>                .
</H2>
              <P><%= labels.getString("click") %>                <a href="signup.jsp"><%= labels.getString("here") %>                </a>
<%= labels.getString("signupnow") %>              </P>
            <%
              }
              } else {
                /**@todo Remover exibição do código de ativação do usuário!*/
                out.println(JCrypt.crypt("any string", login) + "<BR>");
            %>
              <h2><%= labels.getString("activationfail") %>              </h2>
              <p><%= labels.getString("noactivate") %>              </p>
            <%}            %>
            </TD>
          </TR>
        </TBODY>
      </TABLE>
      <!-- #include file="common/copyright.jsp" -->
    <jsp:include page="common/copyright.jsp" flush="true"/>
    </td>
    <td>&nbsp;</td>
  </tr>
</table>
</BODY>
</HTML>
