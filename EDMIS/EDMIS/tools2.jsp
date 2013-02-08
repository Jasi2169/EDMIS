
<!-- #include file="portlets/about.jsp" -->
<jsp:include page="portlets/about.jsp" flush="true"/>
<BR>
<%if (session.getAttribute("User") != null) {%>
<!-- #include file="portlets/alldocs.jsp" -->
<jsp:include page="portlets/alldocs.jsp" flush="true"/>
<BR>
<!-- #include file="portlets/newusers.jsp" -->
<jsp:include page="portlets/newusers.jsp" flush="true"/>
<BR>
<!-- #include file="portlets/newdocs.jsp" -->
<jsp:include page="portlets/newdocs.jsp" flush="true"/>
<%}%>
