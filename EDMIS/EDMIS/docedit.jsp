<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<HTML>
<HEAD>
<TITLE><%= labels.getString("edmis") %>: <%= labels.getString("docedit") %></TITLE>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>
@import url( styles/htmlarea.css );
</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
</HEAD>
<BODY bgColor=#f7f6f1> 
<%
  if (session.getAttribute("User") == null) {
    String redirectURL = "nologin.jsp";
    response.sendRedirect(redirectURL);
    return;
  }
%> 
<%@page import="java.util.*"%> 
<%@page import="java.text.*"%> 
<%@page import="com.progdan.edmis.model.user.User"%> 
<%@page import="com.progdan.edmis.control.document.*"%> 
<%@page import="com.progdan.edmis.control.language.LanguageReader"%> 
<%@page import="com.progdan.edmis.model.document.Document"%> 
<%@page import="com.progdan.edmis.control.relation.RelationControl"%> 
<%@page import="com.progdan.edmis.model.documents.DocumentGroup"%> 
<%@page import="com.progdan.edmis.model.language.Language"%> 
<%@page import="com.progdan.edmis.control.documents.DocumentGroupsByUserController"%> 
<table width="100%" border="0"> 
  <tr> 
    <td>&nbsp;</td> 
    <td width="927"> <!-- #include file="common/header.jsp" --> 
      <jsp:include page="common/header.jsp" flush="true"/> 
      <TABLE cellSpacing=0 cellPadding=3 width="915" border=0> 
        <TBODY> 
          <TR> 
            <TD class=navbar bgColor=#c6bda8> <a href="index.jsp" target=_top><%= labels.getString("edmis") %> </A> &raquo; <a href="home.jsp"><%= labels.getString("home") %> </a> &raquo; <%= labels.getString("docedit") %> </TD> 
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
                    <TD class=headingblock bgColor=#c6bda8><%= labels.getString("docedit") %> </TD> 
                  </TR> 
                </TBODY> 
              </TABLE> 
              <IMG height=8 alt="" src="images/spacer.gif" width=8> <BR> 
              <TABLE class=coursebox cellSpacing=0 cellPadding=5 width="100%" align=center border=0> 
                <TBODY> 
                  <TR> 
                    <TD class=courseboxcontent bgColor=#ffffff> <table width="100%"> 
                        <tbody> 
                          <tr valign=top> 
                            <TD class=courseboxsummary vAlign=top> <P> <FONT size=2> 
                                <%
                                  User user = (User) session.getAttribute("User");
                                  DocumentUpdater document = new DocumentUpdater(user);
                                  Document doc = (Document) session.getAttribute("Document");
								  String id = request.getParameter("id");
								  String name = request.getParameter("name");
								  if((id != null) && (name != null)){
									document.update(session, request);
								  }
                                  String file = request.getParameter("doc");
                                  if (file != null) {
                                    DocumentReader read = new DocumentReader(user);
                                    doc = read.readDocument(file);
                                    session.setAttribute("Document", doc);
                                  }
                                %> 
                                </FONT> 
                              <font size="2"><%= labels.getString("docdata") %>:</font>
<P align="right">						        <a href="zip/<%=doc.getId()%>.zip"><img src="icons/<%=doc.getFormat()%>-big.gif" alt="[ <%=doc.getFormat()%> ]" border="0" align="absmiddle"></a> 
<FONT size=2> 
                              <HR> 
                              <form name="document" method="post" action="docedit.jsp"> 
                                <table border="0" width="100%" cellpadding="0"> 
                                  <tr> 
                                    <td><%= labels.getString("id") %> : </td> 
                                    <td> <input name="id" type="hidden" id="id" value="<%=doc.getId()%>"> 
                                      <%=doc.getId()%> </td> 
                                  </tr> 
                                  <tr> 
                                    <td><%= labels.getString("name") %> : </td> 
                                    <td> <input type="text" name="name" value="<%=doc.getName()%>"> </td> 
                                  </tr> 
                                  <tr> 
                                    <td><%= labels.getString("language") %> : </td> 
                                    <td> <select name="language"> 
                                        <%
                                        LanguageReader lang = new LanguageReader(user);
                                        Vector languages = lang.getLanguages();
										Language language;
                                        for (int i = 0; i < languages.size(); i++) {
										  language = (Language)languages.get(i);
										  if(doc.getLanguage().compareTo(language.getId()) == 0){
                                      %> 
                                        <option value="<%=language.getId()%>" selected><%=language.getName()%></option> 
                                        <%} 
									    else{ %> 
                                        <option value="<%=language.getId()%>"><%=language.getName()%></option> 
                                        <%}}%> 
                                      </select> </td> 
                                  </tr> 
                                  <tr> 
                                    <td> <%= labels.getString("pages") %> : </td> 
                                    <td> <input type="text" name="pages" value="<%=doc.getPages()%>"> </td> 
                                  </tr> 
                                  <tr> 
                                    <td> <%= labels.getString("size") %> : </td> 
                                    <td>
                                      <%        NumberFormat nf = NumberFormat.getNumberInstance(request.getLocale());
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("###,###");
%> 
									 <input name="size" type="hidden" id="size" value="<%=doc.getSize()%>"> 
                                      <%=df.format(doc.getSize())%> bytes </td> 
                                  </tr> 
                                  <tr> 
                                    <td> <%= labels.getString("format") %> : </td> 
                                    <td> <input name="format" type="hidden" id="format" value="<%=doc.getFormat()%>"> 
                                      <img src="icons/<%=doc.getFormat()%>.gif" width="16" height="16" align="absmiddle" alt="[ <%=doc.getFormat()%> ]"> - <%=doc.getFormat()%> </td> 
                                  </tr> 
                                  <tr> 
                                    <td> <%= labels.getString("date") %> : </td> 
                                    <td> <input name="date" type="hidden" id="date" value="<%=doc.getDate()%>"> 
                                      <%=doc.getDate()%> </td> 
                                  </tr> 
                                  <tr> 
                                    <td colspan="2">&nbsp; </td> 
                                  </tr> 
                                  <tr> 
                                    <td> <input type="submit" name="Submit" value="<%= labels.getString("register") %>"> </td> 
                                    <td> <input type="reset" name="Reset" value="<%= labels.getString("reset") %>"> </td> 
                                  </tr> 
                                </table> 
                              </form> 
                              </FONT> 
                              </P> 
                              <TABLE cellSpacing=0 cellPadding=5 width="100%" border=0> 
                                <TBODY> 
                                  <TR> 
                                    <TD class=headingblock bgColor=#c6bda8> <%
                                      String op = request.getParameter("op");
                                      RelationControl rel = new RelationControl(user);
                                      if (op != null) {
                                        if (op.compareTo("add") == 0) {
                                          String group = request.getParameter("group");
                                          rel.add(doc.getId(), group);
                                        }
                                        if (op.compareTo("del") == 0) {
                                          int group = Integer.parseInt(request.getParameter("id"));
                                          rel.remove(doc.getId(), group);
                                        }
                                      }
                                    %> 
                                      <%= labels.getString("docgrp") %> </TD> 
                                  </TR> 
                                </TBODY> 
                              </TABLE> 
                              <IMG height=8 alt="" src="images/spacer.gif" width=8> <BR> 
                              <TABLE class=coursebox cellSpacing=0 cellPadding=5 width="100%" align=center border=0> 
                                <TBODY> 
                                  <TR> 
                                    <TD class=courseboxcontent bgColor=#ffffff> <table width="100%"> 
                                        <tbody> 
                                          <tr valign=top> 
                                            <TD class=courseboxsummary vAlign=top> <FONT size=2> 
                                              <table border="0" width="100%" cellpadding="0"> 
                                                <%
                                                  Vector rels = rel.getRelations(doc.getId());
                                                  DocumentGroup docGrp;
                                                  for (int i = 0; i < rels.size(); i++) {
                                                    docGrp = (DocumentGroup) rels.get(i);
                                                %> 
                                                <form action="docedit.jsp" method="post" name="formDocumentGroup<%=docGrp.getId()%>" id="formDocumentGroup<%=docGrp.getId()%>"> 
                                                  <tr> 
                                                    <td> <input name="op" type="hidden" id="op" value="del"> 
                                                      <input name="id" type="hidden" id="id" value="<%=docGrp.getId()%>"> 
                                                      <%=docGrp.getName()%> </td> 
                                                    <td> <div align="center"> 
                                                        <input name="imageField" type="image" src="icons/sub.gif"> 
                                                      </div></td> 
                                                    <td> <div align="center"> <img src="icons/advanced.gif" width="16" height="16" alt="[ ... ]"> </div></td> 
                                                  </tr> 
                                                </form> 
                                                <%}                                                %> 
                                                <form action="docedit.jsp" method="post" name="formDocumentGroupNew" id="formDocumentGroupNew"> 
                                                  <tr> 
                                                    <td> <input name="op" type="hidden" id="op" value="add"> 
                                                      <select name="group" id="group"> 
                                                        <%
                                                        DocumentGroupsByUserController groupsbyuser = new DocumentGroupsByUserController(user);
                                                        Vector groups = groupsbyuser.getGroups();
                                                        for (int i = 0; i < groups.size(); i++) {
                                                          docGrp = (DocumentGroup) groups.get(i);
                                                      %> 
                                                        <option><%=docGrp.getName()%> </option> 
                                                        <%}%> 
                                                      </select> </td> 
                                                    <td colspan="2"> <div align="center"> 
                                                        <input name="add" type="image" id="add" src="icons/add.gif" alt="Add"> 
                                                      </div></td> 
                                                  </tr> 
                                                </form> 
                                              </table> 
                                              </FONT> </TD> 
                                          </tr> 
                                        </tbody> 
                                      </table></TD> 
                                  </TR> 
                                </TBODY> 
                              </TABLE></TD> 
                          </tr> 
                        </tbody> 
                      </table></TD> 
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
