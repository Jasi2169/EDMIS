<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%
  if (session.getAttribute("User") == null) {
    String redirectURL = "nologin.jsp";
    response.sendRedirect(redirectURL);
    return;
  }
%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
  String op = request.getParameter("op");
%>
<HTML>
<HEAD>
<TITLE><%= labels.getString("edmis") %>: <%= labels.getString("docsearch") %></TITLE>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>@import url( styles/htmlarea.css );</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
</HEAD>
<BODY bgColor=#f7f6f1>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="com.progdan.edmis.model.document.Document"%> 
<%@page import="com.progdan.edmis.model.user.User"%> 
<%@page import="com.progdan.edmis.control.documents.DocumentGroupsByUserController"%> 
<%@page import="com.progdan.searchengine.search.*"%> 
<%@page import="com.progdan.searchengine.queryParser.*"%>
<%@page import="com.progdan.searchengine.analysis.*"%>
<%@page import="com.progdan.edmis.model.documents.DocumentGroup"%> 
<%@page import="com.progdan.edmis.control.document.*"%> 
 
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
              <a href="home.jsp"><%= labels.getString("home") %></a> &raquo; <%= labels.getString("docsearch") %>
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
                    <TD class=headingblock bgColor=#c6bda8><img src="portlets/icons/search.gif" width="16" height="16" border="0" align="absmiddle"> <%= labels.getString("docsearch") %></TD>
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
                                <FONT size=2><%= labels.getString("searchtext1") %>:</FONT></p>
							  <%
							    String indexPath = request.getParameter("group");
								String queryString = request.getParameter("search");
							  %>
                              <form action="docsearch.jsp" method="post" name="formSearch" id="formSearch">
                                <div align="center">
								  <%
								    if(queryString != null){
								  %>
                                  <input name="search" type="text" id="search" value="<%=queryString%>">
								  <%
								    } else{
								  %>
                                  <input name="search" type="text" id="search">
								  <%}%>
                                  <select name="group" id="group">
                                    <option value="all" selected>All</option>
                                    <%
                                      User user = (User) session.getAttribute("User");
									  DocumentGroupsByUserController groupsbyuser = new DocumentGroupsByUserController(user);
                                      Vector groups = groupsbyuser.getGroups();
									  DocumentGroup docGrp;
                                      for (int i = 0; i < groups.size(); i++) {
                                        docGrp = (DocumentGroup) groups.get(i);
                                    %> 
                                    <option value="<%=docGrp.getId()%>"><%=docGrp.getName()%></option> 
                                    <%}%> 
                                  </select>
                                  <input type="submit" name="Submit" value="Search">                            
                                </div>
                              </form>
                              <%
								  if(queryString != null){
								%>
                              <FONT size=2>
                                <HR>
                              </FONT>
                              <h2><%= labels.getString("doctitsearch") %></h2>
                              <FONT size=2><FONT size=2><table width="100%" border="0" align="center" cellpadding="0">
                                <tr align="center" valign="middle">
                                  <th><%= labels.getString("name") %></th>
                                  <th><%= labels.getString("format") %></th>
                                  <th><%= labels.getString("language") %></th>
                                </tr>
							    <%
								    DocumentReader read = new DocumentReader(user);
									Document doc;
									String id;
									Vector docs = read.searchDocuments(queryString);
									for(int i=0; i<docs.size(); i++){
									  doc = (Document)docs.get(i);
								  %>
                                <tr valign="middle" bgcolor="#f7f6f1">
                                  <td><%=doc.getName()%></td>
                                  <td><div align="center"><a href="docedit.jsp?doc=<%=doc.getId()%>"><img src="icons/<%=doc.getFormat()%>.gif" width="16" height="16" alt="[ <%=doc.getFormat()%> ]"></a></div></td>
                                  <td><div align="center"><%=doc.getLanguage()%></div></td>
                                </tr>
							    <%}%>
                                </table>
                              </FONT></FONT>
                              <h2><%= labels.getString("ftrsearch") %></h2>
                              <p><FONT size=2>							        <%
                                      Properties props = new Properties();
                                      String reppath = null;
                                      try {
                                        props.load(getClass().getResourceAsStream("/" + "db.properties"));
                                        reppath = props.getProperty("reppath", "C:\\EDMIS");
                                      } catch (IOException e) {}
									  indexPath = reppath + System.getProperty("file.separator") + "index" + System.getProperty("file.separator") + indexPath;
									  Searcher searcher = new IndexSearcher(indexPath);
									  Query query = QueryParser.parse(queryString, "body", new SimpleAnalyzer());
									  Hits hits = searcher.search(query);
								  %>
                                </FONT></p>
                              <FONT size=2>
                              <table width="100%" border="0" align="center" cellpadding="0">
                                <tr align="center" valign="middle">
                                  <th><%= labels.getString("name") %></th>
                                  <th><%= labels.getString("format") %></th>
                                  <th><%= labels.getString("language") %></th>
                                  <th><%= labels.getString("hits") %></th>
                                </tr>
							    <%
									for(int i=0; i<hits.length(); i++){
									  id = hits.doc(i).get("path");
									  doc = read.readDocument(id);
									  
								  %>
                                <tr valign="middle" bgcolor="#f7f6f1">
                                  <td><%=doc.getName()%></td>
                                  <td><div align="center"><a href="docedit.jsp?doc=<%=doc.getId()%>"><img src="icons/<%=doc.getFormat()%>.gif" width="16" height="16" alt="[ <%=doc.getFormat()%> ]"></a></div></td>
                                  <td><div align="center"><%=doc.getLanguage()%></div></td>
                                  <td><div align="center"><%=hits.score(i)%></div></td>
                                </tr>
							    <%}%>
                              </table>
                              </FONT>
							  <%}%>
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
