<%@page import="java.util.*"%>
<%
  Locale currentLocale = request.getLocale();
  ResourceBundle labels = ResourceBundle.getBundle("MessagesBundle", currentLocale);
%>
<TABLE width="100%" align="center" cellPadding=5 cellSpacing=0 class=sideblock id=block_course_summary style="WIDTH: 100%">
  <TBODY style="BACKGROUND-COLOR: #c6bda8">
    <TR>
      <TD class=sideblockmain>
        <P align=left>
          <FONT color=#ff0000>
            <FONT size=2>
              <FONT face="Trebuchet MS,Verdana,Arial,Helvetica,sans-serif">
                <STRONG><%= labels.getString("edmis") %></STRONG>
                <FONT color=#000000><%= labels.getString("about") %>.</FONT>
              </FONT>
            </FONT>
          </FONT>
        </P>
      </TD>
    </TR>
  </TBODY>
</TABLE>
