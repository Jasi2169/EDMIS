<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.*"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<%int minPasswordLength = 6;%>
<HTML>
<HEAD>
<TITLE><%= labels.getString("edmis") %>:
<%= labels.getString("newaccount") %></TITLE>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>@import url( styles/htmlarea.css );</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
<script language="javascript" type="text/JavaScript">
      function checkEmail()
      {
        if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(document.formSignup.txtEmail.value))
        {
          return (true)
        }
        alert("<%= labels.getString("emailinvalid") %>")
        return (false)
      }
    </script><script language="javascript" type="text/JavaScript">
      function signup()
      {
        if(checkEmail())
        {
          if(document.formSignup.txtPassword.value.length < <%=minPasswordLength%>)
          {
            alert('<%= labels.getString("passworderr1") %> <%=minPasswordLength%> <%= labels.getString("passworderr2") %>')
          }
          else
          {
            if(document.formSignup.txtPassword.value == document.formSignup.txtConfirmPassword.value)
            {
              window.document.formSignup.action = "registeruser.jsp";
              window.document.formSignup.method = "post";
              window.document.formSignup.hidPass.value = document.crypt.encryptPassword(document.formSignup.txtPassword.value);
              window.document.formSignup.txtPassword.value = "";
              window.document.formSignup.txtConfirmPassword.value = "";
              window.document.formSignup.submit();
            }
            else
            {
              alert('<%= labels.getString("passworderr3") %>');
            }
          }
        }
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
            <form id=formSignup name=formSignup>
              <TABLE width="80%" align="center">
                <TBODY>
                  <TR vAlign=top>
                    <TD colSpan=2>
                      <P align="center">
                        <B><%= labels.getString("signuptext1") %></B>
                      </P>
                    </TD>
                  </TR>
                  <TR vAlign=top>
                    <TD align=right>
                      <P><%= labels.getString("username") %>:
</P>
                    </TD>
                    <TD>
                      <INPUT name=txtLogin>
                    </TD>
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
                      <P><%= labels.getString("passwordconfirm") %>:
</P>
                    </TD>
                    <TD>
                      <input type=password name=txtConfirmPassword>
                    </TD>
                  </TR>
                  <TR vAlign=top>
                    <TD colSpan=2>
                      <P align="center">
                        <BR>
                        <B><%= labels.getString("signuptext2") %></B>
                        <BR>
                        (<%= labels.getString("signuptext3") %>)
                      </P>
                    </TD>
                  </TR>
                  <TR vAlign=top>
                    <TD align=right>
                      <P><%= labels.getString("email") %>:
</P>
                    </TD>
                    <TD>
                      <INPUT name=txtEmail>
                    </TD>
                  </TR>
                  <TR vAlign=top>
                    <TD align=right>
                      <P><%= labels.getString("name") %>:
</P>
                    </TD>
                    <TD>
                      <INPUT name=txtName>
                    </TD>
                  </TR>
                  <TR>
                    <TD>
                      <applet code="com.progdan.edmis.control.crypt.EncryptApplet.class" archive="cryptapplet.jar" name="crypt" width=0 height=0>                      </applet>
                    </TD>
                    <TD>
                      <INPUT name="create" type=button onClick="signup()" value="<%= labels.getString("signupsubmit") %>">
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
            </FORM>
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
