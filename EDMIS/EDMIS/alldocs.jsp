<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%
  if (session.getAttribute("User") == null) {
    String redirectURL = "nologin.jsp";
    response.sendRedirect(redirectURL);
    return;
  }
%>
<%@page import="java.text.*"%> 
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
  String op = request.getParameter("op");
  NumberFormat nf = NumberFormat.getNumberInstance(request.getLocale());
  DecimalFormat df = (DecimalFormat) nf;
  df.applyPattern("###,###");%>
<HTML>
<HEAD>
<%
  DocumentGroup docgrp = new DocumentGroup();
  if (op == null) {%>
<TITLE><%= labels.getString("edmis") %>: <%= labels.getString("alldocs") %></TITLE>
<%
  } else if (op.compareTo("group") == 0) {
    docgrp.setId(request.getParameter("id"));
    docgrp.setName(request.getParameter("name"));
%>
<TITLE><%= labels.getString("edmis") %>:
<%= labels.getString("docgrp") %>:
<%= docgrp.getName() %></TITLE>
<%}%>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>@import url( styles/htmlarea.css );</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
</HEAD>
<BODY bgColor=#f7f6f1>
<%@page import="java.util.*"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<%@page import="com.progdan.edmis.model.document.Document"%>
<%@page import="com.progdan.edmis.model.documents.DocumentGroup"%>
<%@page import="com.progdan.edmis.control.document.DocumentReader"%>
<%@page import="com.progdan.edmis.control.relation.RelationControl"%>
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
              <a href="home.jsp"><%= labels.getString("home") %>              </a>
              &raquo;
            <%if (op == null) {            %>
<%= labels.getString("alldocs") %>            <%} else if (op.compareTo("group") == 0) {            %>
              <a href="docgrpedit.jsp"><%= labels.getString("docgrp") %>              </a>
              &raquo;
<%= docgrp.getName() %>            <%}            %>
            </TD>
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
                    <%if (op == null) {                    %>
                    <img src="portlets/icons/docs.gif" width="16" height="16" border="0" align="absmiddle"> <%= labels.getString("alldocs") %>                    <%} else if (op.compareTo("group") == 0) {                    %>
<%= labels.getString("docgrp") %>                      :
<%= docgrp.getName() %>                    <%}                    %>
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
              <IMG height=8 alt="" src="images/spacer.gif" width=1>
              <BR>
              <TABLE class=coursebox cellSpacing=0 cellPadding=5 width="100%" align=center border=0>
                <TBODY>
                  <TR>
                    <TD class=courseboxcontent bgColor=#ffffff>
                      <table width="100%">
                        <tbody>
                          <tr valign=top>
                            <TD class=courseboxsummary vAlign=top>
                              <P>
                                <FONT size=2>
                                <%
                                  User user = (User) session.getAttribute("User");
                                  DocumentReader read = new DocumentReader(user);
                                  RelationControl rel = new RelationControl(user);
                                  Vector rels;
                                  Vector docs = new Vector();
                                  if (op == null) {
                                    docs = read.getAllDocuments();
                                  }
                                  else if (op.compareTo("group") == 0) {
                                    docs = read.getAllDocumentsByGroup(docgrp.getId());
                                  }
                                %>
                                </FONT>

                              <p>
                                <font size="2"><%= labels.getString("docview") %>                                </font>
                              </p>
                              <FONT size=2>
                                <HR>
                                <table width="100%" border="0" align="center" cellpadding="0">
                                  <tr align="center" valign="middle">
                                    <th><%= labels.getString("name") %></th>
                                    <th><%= labels.getString("docgrp") %></th>
                                    <th><%= labels.getString("format") %></th>
                                    <th><%= labels.getString("language") %></th>
                                    <th><%= labels.getString("size") %></th>
                                    <th><%= labels.getString("date") %></th>
                                    <th colspan="2"><%= labels.getString("operations") %></th>
                                  </tr>
                                <%
                                  Document doc;
                                  String docid;
                                  int n = docs.size();
                                  for (int i = 0; i < n; i++) {
                                    doc = (Document) docs.get(i);
                                    docid = doc.getId();
                                %>
                                  <tr valign="middle" bgcolor="#f7f6f1">
                                    <td><%=doc.getName()%>                                    </td>
                                    <td>
                                    <%
                                      rels = rel.getRelations(doc.getId());
                                      int k = rels.size();
                                      for (int j = 0; j < k; j++) {
                                    %>
<a href="alldocs.jsp?op=group&id=<%=((DocumentGroup)rels.get(j)).getId()%>&name=<%=((DocumentGroup)rels.get(j)).getName()%>"><%=((DocumentGroup)rels.get(j)).getName()%></a><BR>
                                    <%}                                    %>
                                    </td>
                                    <td align="center">
                                      <img src="icons/<%=doc.getFormat()%>.gif" width="16" height="16" alt="[ <%=doc.getFormat()%> ]">
                                    </td>
                                    <td align="center"><%=doc.getLanguage()%></td>
                                    <td align="center"><%=df.format(doc.getSize())%></td>
                                    <td align="center"><%=doc.getDate()%></td>
                                    <td align="center">
                                      <div align="center">
                                        <a href="docedit.jsp?doc=<%=doc.getId()%>">
                                          <img src="icons/open.gif" width="16" height="16" border="0" alt="[  ]">
                                        </a>
                                      </div>
                                    </td>
                                    <td align="center">
                                      <div align="center">                                      </div>
                                    </td>
                                  </tr>
                                <%}                                %>
                                </table>
                              </FONT>
</P>                            </TD>
                          </tr>
                        </tbody>
                      </table>
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
              <BR>
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
