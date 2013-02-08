<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.*"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<HTML>
<HEAD>
<TITLE><%= labels.getString("edmis") %>:
<%= labels.getString("login") %></TITLE>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>@import url( styles/htmlarea.css );</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
</style><script language="javascript" type="text/JavaScript">
    function login(){
      window.document.formLogin.action = "home.jsp";
      window.document.formLogin.method = "post";
      window.document.formLogin.hidPass.value = document.crypt.encryptPassword(document.formLogin.txtPassword.value);
      window.document.formLogin.txtPassword.value = "";
      window.document.formLogin.submit();
    }
    </script></HEAD>
<BODY bgColor=#f7f6f1>
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
              <A href="index.jsp"><%= labels.getString("edmis") %>              </A>
              &raquo;
<%= labels.getString("sitelogin") %>            </TD>
            <TD class=navbar vAlign=top align=right width=20 bgColor=#c6bda8>&nbsp;</TD>
          </TR>
        </TBODY>
      </TABLE>
      <IMG height=5 alt="" src="login_files/spacer.gif" width=1>
      <BR>
      <TABLE style="FONT-SIZE: small" cellSpacing=10 cellPadding=5 width="90%" align=center border=0>
        <TBODY>
          <TR>
            <TD class=headingblock width="50%" bgColor=#849dbc>
              <P align=center>
                <B>
                  <FONT size=3><%= labels.getString("return") %>                  </FONT>
                </B>
              </P>
            </TD>
            <TD class=headingblock width="50%" bgColor=#849dbc>
              <P align=center>
                <B>
                  <FONT size=3><%= labels.getString("firsttime") %>                  </FONT>
                </B>
              </P>
            </TD>
          </TR>
          <TR>
            <TD class=generalbox vAlign=top align=center width="50%" bgColor=#c6bda8>
              <P><%= labels.getString("logininstr") %>                <BR>
              </P>
            <form name=formLogin id="formLogin">
              <TABLE style="FONT-SIZE: small" align=center border=0>
                <TBODY>
                  <TR>
                    <TD width="80%">
                      <TABLE style="FONT-SIZE: small" align=center>
                        <TBODY>
                          <TR>
                            <TD align=right>
                              <P><%= labels.getString("username") %>:
</P>
                            </TD>
                            <TD>
                              <INPUT size=15 name=txtName>
                            </TD>
                          </TR>
                          <TR>
                            <TD align=right>
                              <P><%= labels.getString("password") %>:
                                <input name="hidPass" type="hidden">
                              </P>
                            </TD>
                            <TD>
                              <INPUT type=password size=15 name=txtPassword>
                            </TD>
                          </TR>
                        </TBODY>
                      </TABLE>
                    </TD>
                    <TD width="20%">
                      <INPUT type=button value="<%= labels.getString("loginsubmit") %>" onClick="login()">
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
            </FORM>
              <applet code="com.progdan.edmis.control.crypt.EncryptApplet.class" archive="cryptapplet.jar" name="crypt" width=0 height=0>              </applet>
            </TD>
            <TD class=generalbox vAlign=top width="50%" bgColor=#c6bda8>
              <P><%= labels.getString("logintext1") %>
              <OL>
                <LI><%= labels.getString("logintext2") %> <A href="signup.jsp"><%= labels.getString("signupform") %></A> <%= labels.getString("logintext3") %></LI>
                <LI><%= labels.getString("logintext4") %></LI>
                <LI><%= labels.getString("logintext5") %></LI>
                <LI><%= labels.getString("logintext6") %></LI>
                <LI><%= labels.getString("logintext7") %></LI>
                <LI><%= labels.getString("logintext8") %></LI>
                <LI><%= labels.getString("logintext9") %></LI>
              </OL>
              <P>              </P>
              <DIV align=center>
              <FORM name=signup action=signup.jsp method=post>
                <INPUT type=submit value="<%= labels.getString("loginregister") %>">
              </FORM>
              </DIV>
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
