<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="MS932"%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="authentication.bean.Side" %>
<%@ page import="authentication.bean.Report" %>
<%@ page import="authentication.defines.*" %>

<%
	String dispside = (String)session.getAttribute(Consts.STR_SES_REPORT_SIDE);
	String dispno   = (String)session.getAttribute(Consts.STR_SES_REPORT_NO);
	String dispside_array = (String)session.getAttribute(Consts.STR_SES_REPORT_SIDE_ARRAY);
	String dispno_array   = (String)session.getAttribute(Consts.STR_SES_REPORT_NO_ARRAY);
	ArrayList report = null;
	ArrayList menu = (ArrayList)session.getAttribute(Consts.STR_SES_REPSIDE);
	Side m = (Side)menu.get(Integer.parseInt(dispside_array));
	report = m.getReport();
	Report rep = (Report)report.get(Integer.parseInt(dispno_array));
%>
<html lang="ja">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<meta http-equiv="content-language" content="ja">
<title>ÉåÉ|Å[ÉgÅ`<%=rep.getReport_name() %>Å`</title>

<link rel="stylesheet" type="text/css" href="../css/common.css" >
<link rel="stylesheet" type="text/css" href="../frame/css/report.css" >

<link rel="stylesheet" type="text/css" href="../themes/easyui/default/easyui.css">
<link rel="stylesheet" type="text/css" href="../themes/easyui/icon.css">
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
</head>
<body>

<!-- ***** container ***** -->
<div id="container">

	<!-- ===  header === -->
	<div id="header">
		<jsp:include page="header.jsp" flush="true" />
	</div>
	<!-- ===  /header=== -->

	<!-- ===  content === -->
	<div id="content">

		<jsp:include page="report_tabs.jsp">
			<jsp:param value="<%=dispside %>"  name="ReportSide" />
			<jsp:param value="<%=dispno %>"    name="ReportNo" />
			<jsp:param value="<%=dispside_array %>"  name="ReportSide_array" />
			<jsp:param value="<%=dispno_array %>"    name="ReportNo_array" />
			<jsp:param value="0" name="HiddenReport" />
		</jsp:include>
		<%-- error message area--%>
<%
if(request.getAttribute(Form.COMMON_MSG) != null){
%>
		<p class=<%= request.getAttribute(Form.COMMON_MSGTYPE).toString() %>><%=request.getAttribute(Form.COMMON_MSG).toString()%></p>
<%
}
%>
		<%-- /error message area --%>
	</div>
	<!-- ===  /content === -->

</div>
</body>
</html>