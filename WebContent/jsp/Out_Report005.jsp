
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ page import="java.util.Date"%>
<%@ page import="common.Defines" %>
<%@ page import="common.DefineReport" %>
<%@ page import="authentication.bean.*" %>
<%@ page import="authentication.defines.*" %>

<%
	// レポート番号
	String reportNo		=	(String)request.getSession().getAttribute(Defines.ID_REQUEST_JSP_REPORT);

	// ユーザ・セッション取得
	String userId		=	(String)request.getSession().getAttribute(Defines.ID_REQUEST_USER_ID);

	// 制限値取得
	String ro			=	(String)request.getSession().getAttribute(Defines.ID_REQUEST_CUSTOM_REPORT);

	// レポート名取得
	String reportName	=	(String)request.getSession().getAttribute(Defines.ID_REQUEST_TITLE_REPORT);

	// タイトル
	String titleName	=	"【 " + reportName + " 】";

	// 初期検索条件
	String initParam	= (String)request.getSession().getAttribute(Defines.ID_REQUEST_INIT_PARAM);

	// 親画面からの引き継ぎ情報
	String sendParam	= "";
	if (request.getSession().getAttribute(Defines.ID_REQUEST_SEND_PARAM)!=null) {
		sendParam	=	(String)request.getSession().getAttribute(Defines.ID_REQUEST_SEND_PARAM);
	}
	// ユーザID
	String user		=	(String)request.getSession().getAttribute(Defines.ID_REQUEST_CUSTOM_USER);
	// レポートID
	String report	=	(String)request.getSession().getAttribute(Defines.ID_REQUEST_REPORT_NO);

	User lusr = (User)request.getSession().getAttribute(Consts.STR_SES_LOGINUSER);

	// jsキャッシュ対応
	String prm = request.getSession().getId();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html style="overflow-x: hidden;">
<head>
<title></title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
<link rel="stylesheet" type="text/css" href="../css/report.css?v=<%=prm %>">
<link rel="stylesheet" type="text/css" href="../themes/easyui/default/easyui.css?v=<%=prm %>">
<link rel="stylesheet" type="text/css" href="../themes/easyui/default/custom_easyui.css?v=<%=prm %>">
<link rel="stylesheet" type="text/css" href="../themes/easyui/icon.css?v=<%=prm %>">
<style type="text/css">
table.grid td {
	border: 1px solid;
}

tr.ctrlRow td {
	border-top: 0px;
}

#data_panel .datagrid-header {
  border-color: #000000;
  background-image:none;
  background-color: #efefef;
}
#data_panel .datagrid-header td,#data_panel .datagrid-body td{
  border-color: #000000;
}
#data_panel .panel-body, #data_panel .panel-header{
  border-width: 1px 0px 0px 0px;
  border-color: #000000;
}
</style>
</head>
<body style="overflow-x: hidden;">
<!-- ツールバー -->
<div id="tb" style="padding:2px;height:auto;display:none;">
	<form id="ff" method="post" style="display: inline">
	<table class="area_query">
		<tr>
			<td class="BC_tenpo">
				<span><%=DefineReport.Label.SIN_TEN.getTxt() %></span>
			</td>
			<td class="BC_tenpo">
				<input id="<%=DefineReport.Select.TENPO.getObj() %>" style="width:134px">
			</td>
			<td >
				<div style="display:none">
				<span><%=DefineReport.Select.M_TENPO.getTxt() %></span>
				</div>
			</td>
			<td>
				<div style="display:none">
				<input id="<%=DefineReport.Select.M_TENPO.getObj() %>" style="width:134px">
				</div>
			</td>
			<%-- レイアウト調整用 --%>
			<td></td>
		</tr>
	</table>
	</form>
</div>

<div id="buttons" class="easyui-toolbar datagrid-toolbar" style="padding:2px;height:auto;display:none;border-top-width: 1px" border=true doSize=false>
	<a href="#" id="<%=DefineReport.Button.SEARCH.getObj()%>" title="<%=DefineReport.Button.SEARCH.getTxt()%>" class="easyui-linkbutton" iconCls="icon-search"><span class="btnTxt"><%=DefineReport.Button.SEARCH.getTxt()%></span></a>
	<span style="margin-right: 10px;">&nbsp;</span>
	<a href="#" id="<%=DefineReport.Button.ENTRY.getObj()%>" title="<%=DefineReport.Label.REGIST.getTxt()%>" class="easyui-linkbutton" data-options="iconCls:'icon-save',disabled:true"><span class="btnTxt"><%=DefineReport.Label.REGIST.getTxt()%></span></a>
	<a href="#" id="<%=DefineReport.Button.DELETE.getObj()%>" title="<%=DefineReport.Button.DELETE.getTxt()%>" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',disabled:true"><span class="btnTxt"><%=DefineReport.Button.DELETE.getTxt()%></span></a>
</div>

<form id="gf" class="e_grid">
<div id="data_panel">

<!-- EasyUI方式 -->
<div id="gridholder"  class="placeFace" ></div>

<!-- オリジナル方式 -->

</div>
<input type="hidden" name="<%=DefineReport.Hidden.CHANGED_IDX.getObj()%>" id="<%=DefineReport.Hidden.CHANGED_IDX.getObj()%>" />
</form>

<div id="debug">
	<!-- report 情報 -->
	<input type="hidden" name="reportno" id="reportno" value="<%=reportNo %>"/>
	<!-- レポート名 情報 -->
	<input type="hidden" name="reportname" id="reportname" value="<%=reportName %>"/>
	<!-- ユーザー 情報 -->
	<input type="hidden" name="userid" id="userid" value="<%=userId %>"/>
	<!-- 引き継ぎ情報(JSON文字列変換済 "'" で囲む) -->
	<input type="hidden" name="hiddenParam" id="hiddenParam" value='<%=sendParam %>' />
	<!-- ユーザID -->
	<input type="hidden" name="hiddenUser" id="hiddenUser" value="<%=user %>" />
	<!-- レポートID -->
	<input type="hidden" name="hiddenReport" id="hiddenReport" value="<%=report %>" />
	<!-- 初期条件情報(JSON文字列変換済 "'" で囲む) -->
	<input type="hidden" name="hiddenInit" id="hiddenInit" value='<%=initParam %>' />
</div>
</body>

<!-- Load jQuery and JS files -->
<script type="text/javascript" src="../js/jquery.min.js?v=<%=prm %>"></script>			<!-- jquery -->
<script type="text/javascript" src="../js/jquery.easyui.min.js?v=<%=prm %>"></script>	<!-- EasyUI framework -->
<script type="text/javascript" src="../js/easyui-lang-ja.js?v=<%=prm %>"></script>

<script type="text/javascript" src="../js/json2.min.js"></script>			<!-- json plugin -->
<script type="text/javascript" src="../js/exdate.js"></script>				<!-- exdate plugin -->

<script type="text/javascript" src="../js/jshashtable-2.1.js"></script>		<!-- jshashset plugin -->
<script type="text/javascript" src="../js/jquery.numberformatter.min.js"></script><!-- numberformatter plugin -->

<script type="text/javascript" src="../js/shortcut.js"></script>			<!-- shortcut plugin -->

<!-- Report Option & Control & Event  -->
<script type="text/javascript" src="../js/jquery.report/jquery.report.option.005.js?v=<%=prm %>"></script>
<script type="text/javascript" src="../js/jquery.report/jquery.report.control.js?v=<%=prm %>"></script>
<script type="text/javascript" src="../js/jquery.report/jquery.report.ItemSets.js?v=<%=prm %>"></script>
<script type="text/javascript" src="../js/jquery.report/jquery.report.events.js?v=<%=prm %>"></script>
</html>