<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.*"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<%@page import="com.progdan.edmis.control.user.UpdateUserController"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
  int minPasswordLength = 6;
%>
<HTML>
<HEAD>
<TITLE><%= labels.getString("edmis") %>:<%= labels.getString("docupload") %></TITLE>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>
@import url( styles/htmlarea.css );
</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
<style type="text/css">
  <!--
    .style1 {color: #FF0000}
  -->
</style>
<script language="javascript" type="text/JavaScript">
      function update()
      {
          if(document.formSignup.txtPassword.value.length < <%=minPasswordLength%>)
          {
            alert('<%= labels.getString("passworderr1") %> <%=minPasswordLength%> <%= labels.getString("passworderr2") %>')
          }
          else
          {
            if(document.formSignup.txtPassword.value == document.formSignup.txtConfirmPassword.value)
            {
              window.document.formSignup.action = "usredit.jsp";
              window.document.formSignup.method = "post";
              window.document.formSignup.hidPass.value = crypt.encryptPassword(document.formSignup.txtPassword.value);
              window.document.formSignup.hidOldPass.value = crypt.encryptPassword(document.formSignup.txtOldPassword.value);
              window.document.formSignup.txtPassword.value = "";
              window.document.formSignup.txtOldPassword.value = "";
              window.document.formSignup.txtConfirmPassword.value = "";
              window.document.formSignup.submit();
            }
            else
            {
              alert('<%= labels.getString("passworderr3") %>');
            }
          }
      }
    </script></HEAD>
<BODY bgColor=#f7f6f1> 
<%
  if (session.getAttribute("User") == null) {
    String redirectURL = "nologin.jsp";
    response.sendRedirect(redirectURL);
    return;
  }
%> 
<table width="100%" border="0"> 
  <tr> 
    <td>&nbsp;</td> 
    <td width="927"> <!-- #include file="common/header.jsp" --> 
      <jsp:include page="common/header.jsp" flush="true"/> 
      <TABLE cellSpacing=0 cellPadding=3 width="915" border=0> 
        <TBODY> 
          <TR> 
            <TD class=navbar bgColor=#c6bda8> <a href="index.jsp" target=_top><%= labels.getString("edmis") %> </A> &raquo; <a href="home.jsp"><%= labels.getString("home") %></a> &raquo; <%= labels.getString("passwordchange") %></TD> 
          </TR> 
        </TBODY> 
      </TABLE> 
      <HR noShade SIZE=1> 
      <TABLE cellSpacing=5 cellPadding=5 width="915" border=0> 
        <TBODY> 
          <TR> 
            <TD width="175" align="center" style="VERTICAL-ALIGN: top; WIDTH: 180px"> <!-- #include file="tools.jsp" --> 
              <jsp:include page="tools.jsp" flush="true"/> </TD> 
            <TD style="VERTICAL-ALIGN: top"> <TABLE cellSpacing=0 cellPadding=5 width="100%" border=0> 
                <TBODY> 
                  <TR> 
                    <TD class=headingblock bgColor=#c6bda8><img src="icons/conf.gif" width="15" height="15" border="0" align="absmiddle"> <%= labels.getString("passwordchange") %></TD> 
                  </TR> 
                </TBODY> 
              </TABLE> 
              <IMG height=8 alt="" src="images/spacer.gif" width=1> <BR> 
              <TABLE class=coursebox cellSpacing=0 cellPadding=5 width="100%" align=center border=0> 
                <TBODY> 
                  <TR> 
                    <TD class=courseboxcontent bgColor=#ffffff>
					<%
					  User user = (User)session.getAttribute("User");
                      String passwd = request.getParameter("hidPass");
                      String oldpasswd = request.getParameter("hidOldPass");
					  if(passwd != null){
					    if(oldpasswd.compareTo(user.getPassword()) == 0){
					      UpdateUserController control = new UpdateUserController();
						  control.update(user, passwd);
						  out.println("<p>Password sucessfuly updated!</p>");
					    }
						else{
						  out.println("<p>Old password is incorrect!</p>");
						}
					  }
					%>
					<TABLE width="80%" align=center cellPadding=20>
                      <TBODY>
                        <TR>
                          <TD class=generalbox bgColor=#c6bda8>
                            <form id=formSignup name=formSignup>
                              <TABLE width="80%" align="center">
                                <TBODY>
                                  <TR vAlign=top>
                                    <TD align=right>
                                      <P><%= labels.getString("username") %>: </P></TD>
                                    <TD><%=user.getLogin()%></TD>
                                  </TR>
                                  <TR vAlign=top>
                                    <TD align=right><%= labels.getString("passwordold") %>:
                                        <input name="hidOldPass" type="hidden"></TD>
                                    <TD><input type=password name=txtOldPassword></TD>
                                  </TR>
                                  <TR vAlign=top>
                                    <TD align=right><%= labels.getString("password") %>:
                                        <input name="hidPass" type="hidden">
                                    </TD>
                                    <TD>
                                      <input type=password name=txtPassword>
                                    </TD>
                                  </TR>
                                  <TR vAlign=top>
                                    <TD align=right>
                                      <P><%= labels.getString("passwordconfirm") %>: </P></TD>
                                    <TD>
                                      <input type=password name=txtConfirmPassword>
                                    </TD>
                                  </TR>
                                  <TR vAlign=top>
                                    <TD align=right>
                                      <P><%= labels.getString("email") %>: </P></TD>
                                    <TD><%=user.getEmail()%></TD>
                                  </TR>
                                  <TR vAlign=top>
                                    <TD align=right>
                                      <P><%= labels.getString("name") %>: </P></TD>
                                    <TD><%=user.getName()%></TD>
                                  </TR>
                                  <TR>
                                    <TD>
                                      <applet code="com.progdan.edmis.control.crypt.EncryptApplet.class" archive="cryptapplet.jar" name="crypt" width=0 height=0>
                                      </applet>
                                    </TD>
                                    <TD>
                                      <INPUT name="create" type=button onClick="update()" value="<%= labels.getString("passwordchange") %>">
                                    </TD>
                                  </TR>
                                </TBODY>
                              </TABLE>
                          </FORM></TD>
                        </TR>
                      </TBODY>
                    </TABLE> </TD> 
                  </TR> 
                </TBODY> 
              </TABLE> 
              <BR> </TD> 
            <TD width="207" align="center" style="VERTICAL-ALIGN: top; WIDTH: 210px"> <!-- #include file="tools2.jsp" --> 
              <jsp:include page="tools2.jsp" flush="true"/> </TD> 
          </TR> 
        </TBODY> 
      </TABLE> 
      <!-- #include file="common/copyright.jsp" --> 
      <jsp:include page="common/copyright.jsp" flush="true"/> </td> 
    <td>&nbsp;</td> 
  </tr> 
</table> 
</BODY>
</HTML>
