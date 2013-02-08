<%@page import="java.util.*"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>

<TABLE class=sideblock id=block_site_main_menu style="WIDTH: 100%" cellSpacing=0 cellPadding=5>
  <THEAD>
    <TR>
      <TD class=sideblockheading>
        <DIV style="FLOAT: left"><%= labels.getString("info") %>        </DIV>
      </TD>
    </TR>
  </THEAD>
  <TBODY style="BACKGROUND-COLOR: #c6bda8">
    <TR>
      <TD class=sideblockmain>
        <TABLE width="100%" border=0 align="center" cellPadding=2 cellSpacing=0>
          <TBODY>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top width=16>
                <IMG height=16 alt=Fórum src="images/icon.gif" width=16>
              </TD>
              <TD class=sideblocklinks vAlign=top width=*>
                <FONT size=-1><strong>01/02/2005</strong> - Deployment Testing</FONT></TD>
            </TR>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top>&nbsp;</TD>
              <TD class=sideblocklinks vAlign=top><FONT size=-1><strong>23/02/2005</strong> - Database Connection Pool Implementation</FONT></TD>
            </TR>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top>&nbsp;</TD>
              <TD class=sideblocklinks vAlign=top><FONT size=-1><strong>25/02/2005</strong> - Database Connection Pool Optimization</FONT></TD>
            </TR>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top>&nbsp;</TD>
              <TD class=sideblocklinks vAlign=top><FONT size=-1><strong>28/02/2005</strong> - Database Security Enhancements</FONT></TD>
            </TR>
            <TR bgColor=#c6bda8>
              <TD class=sideblocklinks vAlign=top>&nbsp;</TD>
              <TD class=sideblocklinks vAlign=top><FONT size=-1><strong>28/02/2005</strong> - ParserServer Performance Tunning</FONT></TD>
            </TR>
          </TBODY>
        </TABLE>
      </TD>
    </TR>
  </TBODY>
</TABLE>
