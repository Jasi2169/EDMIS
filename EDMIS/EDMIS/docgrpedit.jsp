<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page import="java.util.*"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
  if (session.getAttribute("User") == null) {
    String redirectURL = "nologin.jsp";
    response.sendRedirect(redirectURL);
    return;
  }
  else {
%>
<HTML>
<HEAD>
<TITLE><%= labels.getString("edmis") %>:
<%= labels.getString("docgrpedit") %></TITLE>
<META http-equiv=content-type content="text/html; charset=iso-8859-1">
<STYLE type=text/css>@import url( styles/htmlarea.css );</STYLE>
<LINK href="styles/styles.css" type=text/css rel=stylesheet>
<script language="javascript" type="text/JavaScript">
      function New()
      {
        if(document.formDocumentGroupNew.name.value.length == 0)
        {
          alert('The Document Group Name is required!')
        }
        else
        {
          window.document.formDocumentGroupNew.action = "docgrpedit.jsp";
          window.document.formDocumentGroupNew.method = "post";
          window.document.formDocumentGroupNew.op.value = "new";
        }
      }
    </script></HEAD>
<BODY bgColor=#f7f6f1>
<%@page import="java.util.Vector"%>
<%@page import="com.progdan.edmis.model.user.User"%>
<%@page import="com.progdan.edmis.control.documents.*"%>
<%@page import="com.progdan.edmis.model.documents.DocumentGroup"%>
<%@page import="com.progdan.edmis.control.users.UserGroupReader"%>
<%@page import="com.progdan.edmis.control.permissions.PermissionWriter"%>
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
<%= labels.getString("docgrpedit") %>            </TD>
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
                    <TD class=headingblock bgColor=#c6bda8><img src="portlets/icons/docgrp.gif" width="16" height="16" border="0" align="absmiddle"> <%= labels.getString("docgrpedit") %>                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
              <IMG height=8 alt="" src="images/spacer.gif" width=1>
              <BR>
              <TABLE class=coursebox cellSpacing=0 cellPadding=5 width="100%" align=center border=0>
                <TBODY>
                  <TR>
                    <TD class=courseboxcontent bgColor=#ffffff>
                      <font size="2">                      </font>
                      <table width="100%">
                        <tbody>
                          <tr valign=top>
                            <TD class=courseboxsummary vAlign=top>
                              <P>
                                <font size="2"><%= labels.getString("docgrpdata") %>                                  :
</font>
                                <FONT size=2>

                              <HR>
                              <table border="0" width="100%" cellpadding="0">
                                <tr>
                                  <th colspan="4"><%= labels.getString("mydocsgrps") %>                                  </th>
                                </tr>
                                <tr>
                                  <font size="2">
                                    <th width="76%"><%= labels.getString("docgrpname") %></th>
                                    <th colspan="3"><%= labels.getString("operations") %></th>
                                  </font>
                                </tr>
                              <%
                                User user = (User) session.getAttribute("User");
                                String op = request.getParameter("op");
                                if (op != null) {
                                  if (op.compareTo("new") == 0) {
                                    DocumentGroupRegister reg = new DocumentGroupRegister();
                                    String name = request.getParameter("name");
                                    reg.register(name, user);
                                  }
                                  if (op.compareTo("save") == 0) {
                                    DocumentGroupUpdater update = new DocumentGroupUpdater();
                                    String name = request.getParameter("name");
                                    int id = Integer.parseInt(request.getParameter("id"));
                                    update.update(id, name, user);
                                  }
                                  if (op.compareTo("delete") == 0) {
                                    DocumentGroupRemover remove = new DocumentGroupRemover();
                                    int id = Integer.parseInt(request.getParameter("id"));
                                    remove.remove(id, user);
                                  }
                                  if (op.compareTo("enter") == 0) {
                                    PermissionWriter perm = new PermissionWriter(user);
                                    UserGroupReader usrGrp = new UserGroupReader(user);
                                    int id = Integer.parseInt(request.getParameter("id"));
                                    int usrid = usrGrp.getUserGroup(user.getLogin());
                                    perm.addPermission(usrid, id);
                                  }
                                  if (op.compareTo("exit") == 0) {
                                    PermissionWriter perm = new PermissionWriter(user);
                                    UserGroupReader usrGrp = new UserGroupReader(user);
                                    int id = Integer.parseInt(request.getParameter("id"));
                                    int usrid = usrGrp.getUserGroup(user.getLogin());
                                    perm.removePermission(usrid, id);
                                  }
                                }
                                DocumentGroupsByUserController usrgrpctrl = new DocumentGroupsByUserController(user);
                                DocumentGroup group;
                                Vector userGroups;
								if((op != null) && (op.compareTo("search") == 0)){
								userGroups = usrgrpctrl.getGroups(request.getParameter("search"));
								} else{
								  userGroups = usrgrpctrl.getGroups();
								}
                                for (int i = 0; i < userGroups.size(); i++) {
                                  group = (DocumentGroup) userGroups.get(i);
                              %>
<script language="javascript" type="text/JavaScript">
                                  function Save<%=group.getId()%>()
                                  {
                                    if(document.formDocumentGroup<%=group.getId()%>.name.value.length == 0)
                                    {
                                      alert('The Document Group Name is required!')
                                    }
                                    else
                                    {
                                      window.document.formDocumentGroup<%=group.getId()%>.action = "docgrpedit.jsp";
                                      window.document.formDocumentGroup<%=group.getId()%>.method = "post";
                                      window.document.formDocumentGroup<%=group.getId()%>.op.value = "save";
									}
                                  }
                                  function Del<%=group.getId()%>()
                                  {
                                    window.document.formDocumentGroup<%=group.getId()%>.action = "docgrpedit.jsp";
                                    window.document.formDocumentGroup<%=group.getId()%>.method = "post";
                                    window.document.formDocumentGroup<%=group.getId()%>.op.value = "delete";
                                  }
                                  function Exit<%=group.getId()%>()
                                  {
                                    window.document.formDocumentGroup<%=group.getId()%>.action = "docgrpedit.jsp";
                                    window.document.formDocumentGroup<%=group.getId()%>.method = "post";
                                    window.document.formDocumentGroup<%=group.getId()%>.op.value = "exit";
                                  }
                                </script>                                <tr>
                                <form name="formDocumentGroup<%=group.getId()%>">
                                  <td>
                                    <input name="id" type="hidden" value="<%=group.getId()%>">
                                    <input name="name" type="text" value="<%=group.getName()%>" size="50">
                                    <input name="op" type="hidden" id="op">
                                  </td>
                                  <td>
                                    <div align="center">
                                      <input name="save" type="image" onclick="javascript:Save<%=group.getId()%>();" src="icons/save.gif" alt="Save" align="middle">
                                    </div>
                                  </td>
                                  <td>
                                    <div align="center">
                                      <input name="delete" type="image" onClick="javascript:Del<%=group.getId()%>();" src="icons/delete.gif" alt="Delete" align="middle">
                                    </div>
                                  </td>
                                  <td>
                                    <div align="center">
                                      <input name="delete2" type="image" onClick="javascript:Exit<%=group.getId()%>();" src="icons/sub.gif" alt="Exit" align="middle">
                                    </div>
                                  </td>
                                </form>
                                </tr>
                              <%}                              %>
                                <tr>
                                <form name="formDocumentGroupNew" id="formDocumentGroupNew">
                                  <td>
                                    <input name="name" type="text" id="name" size="50">
                                    <input name="op" type="hidden" id="op">
                                  </td>
                                  <td>
                                    <div align="center">                                    </div>

                                  <td>
                                    <div align="center">
                                      <input name="new" type="image" onClick="javascript:New();" src="icons/new.gif" alt="New" align="middle">
                                    </div>
                                  </td>
                                  <td>
                                    <div align="center">                                    </div>
                                  </td>
                                </form>
                                </tr>
                              </table>
                              <table border="0" width="100%" cellpadding="0">
                                <tr>
                                  <th colspan="3"><%= labels.getString("docgrp") %>                                  </th>
                                </tr>
                                <tr>
                                  <font size="2">
                                    <th width="76%"><%= labels.getString("docgrpname") %>                                    </th>
                                    <th colspan="2"><%= labels.getString("operations") %>                                    </th>
                                  </font>
                                </tr>
                              <%
								if((op != null) && (op.compareTo("search") == 0)){
								userGroups = usrgrpctrl.getOtherGroups(request.getParameter("search"));
								} else{
								  userGroups = usrgrpctrl.getOtherGroups();
								}
                                for (int i = 0; i < userGroups.size(); i++) {
                                  group = (DocumentGroup) userGroups.get(i);
                              %>
<script language="javascript" type="text/JavaScript">
                                  function Enter<%=group.getId()%>()
                                  {
                                    window.document.formDocumentGroup<%=group.getId()%>.action = "docgrpedit.jsp";
                                    window.document.formDocumentGroup<%=group.getId()%>.method = "post";
                                    window.document.formDocumentGroup<%=group.getId()%>.op.value = "enter";
                                  }
                                </script>                                <tr>
                                <form name="formDocumentGroup<%=group.getId()%>">
                                  <td>
                                    <input name="id" type="hidden" value="<%=group.getId()%>">
<%=group.getName()%>                                    <input name="op" type="hidden" id="op">
                                  </td>
                                  <td>
                                    <div align="center">
                                      <input name="save" type="image" onclick="javascript:Enter<%=group.getId()%>();" src="icons/add.gif" alt="Enter" align="middle">
                                    </div>
                                  </td>
                                  <td>
                                    <div align="center">                                    </div>
                                  </td>
                                </form>
                                </tr>
                              <%}                              %>
                              </table>
</FONT></P>                            </TD>
                          </tr>
                        </tbody>
                      </table>
                    </TD>
                  </TR>
                </TBODY>
              </TABLE>
              <BR>
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
<%}%>
