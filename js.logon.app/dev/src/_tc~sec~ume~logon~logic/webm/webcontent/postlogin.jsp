<%@ page session="false"%>
<%@ page import="com.sap.engine.applications.security.logon.tags.FormTag"%>
<%@ page import="com.sap.engine.interfaces.security.auth.WebCallbackHandler"%>

<html>

<head>
<script language="JavaScript">
  function autoSubmit() {
    document.postLogin.submit();
  }
</script>
</head>

<body onLoad="autoSubmit()">

<%
  String url = (String) request.getAttribute(WebCallbackHandler.ORIGINAL_URL_ATTRIBUTE_NAME);
%>
<form name="postLogin" method="POST" action="<%=url%>">
</form>
</body>
</html>
