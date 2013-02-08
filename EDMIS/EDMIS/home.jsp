<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.*"%>
<%@page import="com.progdan.edmis.control.documents.DocumentGroupsByUserController"%>
<%@page import="com.progdan.edmis.model.documents.DocumentGroup"%>
<%@page import="com.progdan.edmis.control.documents.DocumentsStatisticsController"%>
<%@page import="com.progdan.edmis.model.document.Document"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<HTML>
<HEAD>
<TITLE><%= labels.getString("edmis") %>:
<%= labels.getString("home") %></TITLE>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>@import url( styles/htmlarea.css );</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
</HEAD>
<BODY bgColor=#f7f6f1>
<%@page import="com.progdan.edmis.error.user.*"%>
<%@page import="com.progdan.edmis.error.license.*"%>
<%@page import="com.progdan.edmis.control.user.LoginUserControl"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<%
  String login = request.getParameter("txtName");
  String passwd = request.getParameter("hidPass");
  boolean unknow = false;
  boolean inactive = false;
  boolean passerr = false;
  User user = (User)session.getAttribute("User");
  if (session.getAttribute("User") == null) {
    if ((login == null) && (passwd == null)) {
      String redirectURL = "nologin.jsp";
      response.sendRedirect(redirectURL);
      return;
    }
    LoginUserControl loginuser = new LoginUserControl();
    try {
      loginuser.login(session, login, passwd, request.getRemoteAddr());
      user = (User)session.getAttribute("User");
    }
    catch (LicenseException e) {
      String redirectURL = "nolicense.jsp";
      response.sendRedirect(redirectURL);
      return;
    }
    catch (UnknowUserException e) {
      unknow = true;
    }
    catch (PasswdErrorException e) {
      passerr = true;
    }
    catch (InactiveUserAccountException e) {
      inactive = true;
    }
  }
%>
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
<%= labels.getString("home") %>            </TD>
          </TR>
        </TBODY>
      </TABLE>
      <HR noShade SIZE=1>
      <TABLE cellSpacing=5 cellPadding=5 width="915" border=0>
        <TBODY>
          <TR>
            <TD width="175" align="center" style="VERTICAL-ALIGN: top; WIDTH: 180px">
              <!-- #include file="tools.jsp" -->
            <jsp:include page="tools.jsp" flush="true"/>
            </TD>
            <TD style="VERTICAL-ALIGN: top">
              <TABLE cellSpacing=0 cellPadding=5 width="100%" border=0>
                <TBODY>
                  <TR>
                    <TD class=headingblock bgColor=#c6bda8>
                    <%
                      if (session.getAttribute("User") == null) {
                        if (unknow) {
                    %>
<%= labels.getString("username") %> <%=login%> <%= labels.getString("unknow") %>!
                    <%} else if (inactive) {                    %>
<%= labels.getString("username") %> <%=login%> <%= labels.getString("inactive") %>!
                    <%} else if (passerr) {                    %>
<%= labels.getString("passworderr4") %> <%=login%>!
                    <%
                      }
                      } else {
                    %>
<img src="portlets/icons/docgrp.gif" border="0" align="absmiddle"> <%= labels.getString("mydocsgrps") %><%}                    %>
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
              <BR>
            <%if (session.getAttribute("User") == null) {            %>
              <TABLE class=coursebox cellSpacing=0 cellPadding=5 width="100%" align=center border=0>
                <TBODY>
                  <TR>
                    <TD class=courseboxcontent bgColor=#ffffff>
                      <table width="100%">
                        <tbody>
                          <tr valign=top>
                            <td width="50%" valign=top class=courseboxsummary>
                              <p>
                                <font size=2>
                                <%if (inactive) {                                %>


                              <p><%= labels.getString("inactiveaccount") %>                              </p>
                            <%}                            %>
<%= labels.getString("click") %>                              <a href="login.jsp"><%= labels.getString("here") %>                              </a>
<%= labels.getString("loginagain") %>                              !
</font>                            </td>

                        </tbody>
                      </table>
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
              <BR>
            <%
              } else {
                DocumentGroupsByUserController groups = new DocumentGroupsByUserController((User) session.getAttribute("User"));
                Vector admins;
				int id;
                Vector docgroups = groups.getGroups();
                int n = docgroups.size();
                for (int i = 0; i < n; i++) {
				  id = ((DocumentGroup)docgroups.get(i)).getId();
            %>
              <TABLE class=coursebox cellSpacing=0 cellPadding=5 width="100%" align=center border=0>
                <TBODY>
                  <TR>
                    <TD class=courseboxcontent bgColor=#ffffff>
                      <TABLE width="100%">
                        <TBODY>
                          <TR vAlign=top>
                            <TD class=courseboxinfo vAlign=top width="50%">
                              <P>
                                <FONT size=3>
                                  <B>
                                    <a href="alldocs.jsp?op=group&id=<%=((DocumentGroup)docgroups.get(i)).getId()%>&name=<%=((DocumentGroup)docgroups.get(i)).getName()%>"><%=((DocumentGroup)docgroups.get(i)).getName()%></a>
                                  </B>
                                </FONT>
                              </P>
                              <P>
                                <FONT size=1><%= labels.getString("manager") %>(s):
                                  <br>
                                <%
                                  admins = groups.getAdmins(id);
                                  int k = admins.size();
                                  for (int j = 0; j < k; j++) {
                                %>
                                  <IMG src="images/user.gif" width=16 height=16 vspace=4 border=0 align="absmiddle" alt="[ User ]">
<%=(String)admins.get(j)%>                                  <BR>
                                <%}%>
								<BR><%= labels.getString("lastupdate") %>: <%=((DocumentGroup)docgroups.get(i)).getLastupdate()%>
                                </FONT>
                              </P>
                            </TD>
                            <TD class=courseboxsummary vAlign=top width=\"50%\">
                              <P>
                                <FONT size=2>

                              <P>
                              <P class="calendarreferer">New Documents:</P>
                              <P>
<%
           DocumentsStatisticsController doc = DocumentsStatisticsController.getInstance();
		   Vector newDocs = doc.getNewDocsByGroup(id, user);
		   k = newDocs.size();
		   if (k == 0) {
              out.println(labels.getString("none"));
            }
            else {
			  Document document;
              for (int j = 0; j < k; j++) {
			    document = (Document)newDocs.get(j);
			    out.println("<a href=\"docedit.jsp?doc=" + document.getId() + "\"><IMG src=\"icons/" + document.getFormat() + ".gif\" width=16 height=16 vspace=4 border=0 align=\"absmiddle\"></a> " + document.getName() + "<br>");
			  }
			}
		%></P>
</FONT>                              <P>                              </P>
                            </TD>
                          </TR>
                        </TBODY>
                      </TABLE>
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
              <BR>
            <%}            %>
              <TABLE width="100%">
                <TBODY>
                  <TR align="center" valign="middle">
                    <TD>
                      <CENTER>
                      <FORM name=coursesearch action=docgrpedit.jsp method=post>
                        <input name="op" type="hidden" id="op" value="search">
                        <INPUT size=12 name=search>
                        <INPUT type=submit value="<%= labels.getString("searchdocgrps") %>">
                      </FORM>
                      </CENTER>
                    </TD>
                    <TD>
                    <FORM action=docgrpedit.jsp method=post>
                      <div align="center">
                        <INPUT type=submit value="<%= labels.getString("alldocgrps") %>">
                      </div>
                    </FORM>
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
            <%}            %>
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
</BODY>
</HTML>
