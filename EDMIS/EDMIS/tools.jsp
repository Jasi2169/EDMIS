
<!-- #include file="portlets/information.jsp" -->
<jsp:include page="portlets/information.jsp" flush="true"/>
<BR>
<%if (session.getAttribute("User") != null) {%>
<!-- #include file="portlets/online.jsp" -->
<jsp:include page="portlets/online.jsp" flush="true"/>
<BR>
<!-- #include file="portlets/menu.jsp" -->
<jsp:include page="portlets/menu.jsp" flush="true"/>
<%}%>
