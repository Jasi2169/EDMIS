<%@page import="java.util.*"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<TABLE class=sideblock id=block_online_users style="WIDTH: 100%" cellSpacing=0 cellPadding=5>
  <THEAD>
    <TR>
      <TD class=sideblockheading>
        <DIV style="FLOAT: left"><img src="icons/rar.gif" border="0" align="absmiddle"> <%= labels.getString("dm") %>        </DIV>
      </TD>
    </TR>
  </THEAD>
  <TBODY style="BACKGROUND-COLOR: #c6bda8">
    <TR>
      <TD class=sideblockmain>
        <TABLE cellSpacing=0 cellPadding=2 width="100%" border=0>
          <TBODY>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top><div align="center"><a href=docupload.jsp><img src="icons/upload.gif" width="16" height="16" border="0"></a></div></TD>
              <TD class=sideblocklinks vAlign=top><a href=docupload.jsp><%= labels.getString("docupload") %></a></TD>
            </TR>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top><div align="center"><A href=alldocs.jsp><img src="icons/docs.gif" width="16" height="15" border="0"></A></div></TD>
              <TD class=sideblocklinks vAlign=top><A href=alldocs.jsp><%= labels.getString("alldocs") %></A></TD>
            </TR>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top width=16><div align="center"><A href=docsearch.jsp><img src="icons/search.gif" width="16" height="16" border="0"></A></div></TD>
              <TD class=sideblocklinks vAlign=top width=*>
                <FONT size=-1>
                  <A href=docsearch.jsp><%= labels.getString("docsearch") %></A>
                </FONT>
              </TD>
            </TR>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top><div align="center"><A href=docgrpedit.jsp><img src="icons/docgrp.gif" width="16" height="16" border="0"></A></div></TD>
                  
              <TD class=sideblocklinks vAlign=top><A href=docgrpedit.jsp><%= labels.getString("docgrpedit") %></A></TD>
            </TR>
          </TBODY>
        </TABLE>
      </TD>
    </TR>
  </TBODY>
</TABLE>
<br>
<TABLE class=sideblock id=block_online_users style="WIDTH: 100%" cellSpacing=0 cellPadding=5>
  <THEAD>
    <TR>
      <TD class=sideblockheading>
        <DIV style="FLOAT: left"><img src="images/user.gif" width="16" height="16" border="0" align="absmiddle"> <%= labels.getString("myaccount") %>        </DIV>
      </TD>
    </TR>
  </THEAD>
  <TBODY style="BACKGROUND-COLOR: #c6bda8">
    <TR>
      <TD class=sideblockmain><TABLE cellSpacing=0 cellPadding=2 width="100%" border=0>
        <TBODY>
          <TR bgColor=#c6bda8>
            <TD width="16" vAlign=top class=sideblocklinks><div align="center"><A href=docgrpedit.jsp><img src="icons/conf.gif" width="15" height="15" border="0"></A></div></TD>
            <TD class=sideblocklinks vAlign=top><A href=usredit.jsp><%= labels.getString("passwordchange") %></A></TD>
          </TR>
        </TBODY>
      </TABLE></TD>
    </TR>
  </TBODY>
</TABLE>
