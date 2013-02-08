<%@page contentType="text/html; charset=iso-8859-1" language="java"%>
<%@page import="java.util.*"%>
<%@page import="com.progdan.edmis.control.user.UserWriter"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<html>
<head>
<title><%= labels.getString("edmis") %></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<STYLE type=text/css>@import url( styles/htmlarea.css );</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
<style type="text/css">
  <!--
    .style1 {color: #FF0000}
  -->
</style>
</head>
<body bgColor=#f7f6f1>
<%
  if (request.getParameter("logout") != null) {
    UserWriter writer = new UserWriter();
    writer.logout((User) session.getAttribute("User"));
    session.removeAttribute("User");
  }
%>
<table width="100%" border="0">
  <tr>
    <td>&nbsp;</td>
    <td width="927">
      <!-- #include file="common/header.jsp" -->
    <jsp:include page="common/header.jsp" flush="true"/>
      <HR noShade SIZE=1>
      <TABLE cellSpacing=5 cellPadding=5 width="915" border=0>
        <TBODY>
          <TR>
            <TD width="175" align="center" style="VERTICAL-ALIGN: top; WIDTH: 180px">
              <!-- #include file="tools.jsp" -->
            <jsp:include page="tools.jsp" flush="true"/>
            </TD>
            <TD width="495" style="VERTICAL-ALIGN: top">
              <TABLE cellSpacing=0 cellPadding=5 width="100%" border=0>
                <TBODY>
                  <TR>
                    <TD class=headingblock bgColor=#c6bda8><%= labels.getString("indextitle") %></TD>
                  </TR>
                </TBODY>
              </TABLE>
              <IMG height=8 alt="" src="index_files/spacer.gif" width=1>
              <BR>
              <TABLE class=generalbox cellSpacing=0 cellPadding=5 width="100%" align=center border=0>
                <TBODY>
                  <TR>
                    <TD class=generalboxcontent bgColor=#ffffff>
                      <TABLE cellSpacing=0 cellPadding=3 width="100%" border=0>
                        <TBODY>
                          <TR>
                            <TD align="center" vAlign=middle class=sideblockmain>
                              <img src="images/GED.jpg" width="458" height="306" alt="[ GED ]">
                            </TD>
                          </TR>
                          <TR>
                            <TD width="100%" vAlign=top class=sideblockmain>
                              <p align="justify">
                                <FONT color=#ff0000>
                                  <FONT size=2>
                                    <FONT face="Trebuchet MS,Verdana,Arial,Helvetica,sans-serif">
                                      <FONT color=#000000><%= labels.getString("indextext1") %>                                      </FONT>
                                    </FONT>
                                  </FONT>
                                </FONT>
                              </p>
                              <p align="justify">
                                <font color="#000000" size="2" face="Trebuchet MS,Verdana,Arial,Helvetica,sans-serif"><%= labels.getString("indextext2") %>                                </font>
                              </p>
                              <ul>
                                <li><%= labels.getString("indextext3") %></li>
                                <li><%= labels.getString("indextext4") %></li>
                                <li><%= labels.getString("indextext5") %></li>
                              </ul>
                              <p>
                                <font color="#000000" size="2" face="Trebuchet MS,Verdana,Arial,Helvetica,sans-serif"><%= labels.getString("indextext6") %>                                </font>
                              </p>
                              <font color="#000000" size="2" face="Trebuchet MS,Verdana,Arial,Helvetica,sans-serif">
                                <ol>
                                  <li><%= labels.getString("indextext7") %></li>
                                  <li><%= labels.getString("indextext8") %></li>
                                  <li><%= labels.getString("indextext9") %></li>
                                  <li><%= labels.getString("indextext10") %></li>
                                  <li><%= labels.getString("indextext11") %></li>
                                  <li><%= labels.getString("indextext12") %></li>
                                  <li><%= labels.getString("indextext13") %></li>
                                </ol>
                              </font>
                              <p>
                                <font color="#000000" size="2" face="Trebuchet MS,Verdana,Arial,Helvetica,sans-serif"><%= labels.getString("src") %>                                  :
                                  <a href="http://www.cenadem.com.br" title="CENADEM" target="_blank">CENADEM</a>
                                </font>
                              </p>
                            </TD>
                          </TR>
                          <TR>
                            <TD vAlign=top class=sideblockmain>&nbsp;</TD>
                          </TR>
                          <TR>
                            <TD vAlign=top class=sideblockmain>
                              <div align="justify">
                                <span class="style1">
                                  <span class="calendarreferer"><%= labels.getString("atention") %>                                    :
</span>
                                </span>
<%= labels.getString("savetree") %>                              </div>
                            </TD>
                          </TR>
                        </TBODY>
                      </TABLE>
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
              <CENTER>              </CENTER>
            </TD>
            <TD width="207" align="center" style="VERTICAL-ALIGN: top; WIDTH: 210px">
              <!-- #include file="tools2.jsp" -->
            <jsp:include page="tools2.jsp" flush="true"/>
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
</body>
</html>
