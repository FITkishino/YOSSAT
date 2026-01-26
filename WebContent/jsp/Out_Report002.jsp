
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

	String userTenpo = lusr.getTenpo();
	String userBumon = lusr.getBumon();

	// jsキャッシュ対応
	String prm = request.getSession().getId();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="ja" style="overflow-x: hidden;">
<head>
<title></title>
<meta http-equiv="content-language" content="ja">
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
</style>
</head>
<body style="overflow-x: hidden;">
<!-- ツールバー -->
<div id="tb" style="padding:2px;height:auto;display:none;">
	<form id="ff" method="post" style="display: inline">
	<table class="area_query">
		<tr>
			<td>
				<span><%=DefineReport.Select.YM_F.getTxt() %></span>
			</td>
			<td>
				<input id="<%=DefineReport.Select.YM_F.getObj() %>" style="width:134px">
			</td>
			<td class="BC_tenpo">
				<span><%=DefineReport.Select.TENPO.getTxt() %></span>
			</td>
			<td class="BC_tenpo">
				<input id="<%=DefineReport.Select.TENPO.getObj() %>" style="width:134px">
			</td>
			<td class="BC_class">
				<span><%=DefineReport.Select.BUMON.getTxt() %></span>
			</td>
			<td class="BC_class">
				<input id="<%=DefineReport.Select.BUMON.getObj() %>" style="width:134px">
			</td>
<!-- 			<td class="BC_class"> -->
<%-- 				<span><%=DefineReport.Text.BMN_ARA_RIT.getTxt() %></span> --%>
<!-- 			</td> -->
<!-- 			<td class="BC_class"> -->
<%-- 				<input type="text" id="<%=DefineReport.Text.BMN_ARA_RIT.getObj() %>" style="width:134px;" class="labelNum" readonly="readonly"> --%>
<!-- 			</td> -->
			<%-- レイアウト調整用 --%>
			<td><span id="nm_update" class="labelName" style="width:550px;text-align: right;"></span></td>
		</tr>
	</table>
	</form>
</div>

<div id="buttons" class="easyui-toolbar datagrid-toolbar" style="padding:2px;height:auto;display:none;border-top-width: 1px" border=true doSize=false>
	<a href="#" id="<%=DefineReport.Button.SEARCH.getObj()%>" title="<%=DefineReport.Button.SEARCH.getTxt()%>" class="easyui-linkbutton" iconCls="icon-search"><span class="btnTxt"><%=DefineReport.Button.SEARCH.getTxt()%></span></a>
	<a href="#" id="<%=DefineReport.Button.EXCEL.getObj()%>" title="<%=DefineReport.Button.EXCEL.getTxt()%>" class="easyui-linkbutton" iconCls="icon-excel"><span class="btnTxt"><%=DefineReport.Button.EXCEL.getTxt()%></span></a>
	<span style="margin-right: 10px;">&nbsp;</span>
	<a href="#" id="<%=DefineReport.Button.ENTRY.getObj()%>" title="<%=DefineReport.Label.REGIST.getTxt()%>" class="easyui-linkbutton" data-options="iconCls:'icon-save',disabled:true"><span class="btnTxt"><%=DefineReport.Label.REGIST.getTxt()%></span></a>
</div>

<form id="gf" class="e_grid">
<div id="data_panel" class="easyui-panel" data-options="border:false" style="overflow: hidden;">

<!-- EasyUI方式 -->
<div id="gridholder"  class="placeFace" ></div>

<!-- オリジナル方式 -->
<div id="view1" style="overflow: hidden;">
<table class="grid" style="border-collapse: collapse;" cellpadding="1">
<tr>
	<td style="border: 0px;">&nbsp;</td>
	<td style="text-align: right;"><span id="W2_T2"></span></td>
	<td style="border: 0px;" colspan="8"></td>
	<td style="text-align: right;"><span id="W6_T2"></span></td>
	<td style="border: 0px;" colspan="5"></td>
	<td style="text-align: right;"><span id="W8_T2"></span></td>
	<td style="text-align: right;"><span id="W11_T2"></span></td>
	<td style="border: 0px;" colspan="4"></td>
	<td style="text-align: right;"><span id="W14_T2"></span></td>
	<td style="text-align: right;"><span id="W15_T2"></span></td>
	<td style="text-align: right;"><span id="W16_T2"></span></td>
</tr>
<tr>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">日付</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">日別予算</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">予算累計</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">実績</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">実績累計</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">予算差異</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">予算比</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">累計<br>予算比</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">進行率</div></td>

	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">昨年日付</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">昨年実績</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">昨年実績<br>累計</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">昨年対比</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">累計<br>昨年対比</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width: 70px;">昨年<br>推行率</div></td>
	<td style="text-align: center;" rowspan="2" class="header"><div style="width:170px;">今年の要因</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">店客数</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">フロア客数</div></td>
	<td style="text-align: center;" colspan="2" class="orange"><div>天気</div></td>
	<td style="text-align: center;" colspan="2" class="orange"><div>気温</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">仕入高</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">差益高</div></td>
	<td style="text-align: center;" rowspan="2" class="orange"><div style="width: 70px;">差益率</div></td>
	<td style="border: 0px;" rowspan="2"><div style="width: 30px;"></div></td>
</tr>
<tr>
  <td style="text-align: center;" class="orange"><div style="width: 70px;">午前</div></td>
	<td style="text-align: center;" class="orange"><div style="width: 70px;">午後</div></td>
	<td style="text-align: center;" class="orange"><div style="width: 35px;">最高</div></td>
	<td style="text-align: center;" class="orange"><div style="width: 35px;">最低</div></td>
</tr>

</table>
</div>

<div id="view2" style="overflow: auto;">
<table id="ctrlTbl" class="grid" style="border-collapse: collapse;" cellpadding="1">

<%for(int i = 0; i < 31; i++){ %>
<tr class="ctrlRow" style="height: 23px;">
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
</tr>
<%} %>

<%-- 空白行 --%>
<tr id="emptyRow" style="height: 20px;">
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
  <td style="border: 0px;"><div style="width:171px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 35px;"></div></td>
	<td style="border: 0px;"><div style="width: 34px;"></div></td>
	<td style="border: 0px;"><div style="width: 35px;"></div></td>
	<td style="border: 0px;"><div style="width: 34px;"></div></td>
	<td style="border: 0px;"><div style="width: 36px;"></div></td>
	<td style="border: 0px;"><div style="width: 36px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
	<td style="border: 0px;"><div style="width: 71px;"></div></td>
</tr>

<tr id="info_sum_r0">
	<td style="text-align: center;" class="header" colspan="4"><div>週間計</div></td>
	<td style="text-align: center;" class="header"><div>週間予算</div></td>
	<td style="text-align: center;" class="header"><div>週間実績</div></td>
	<td style="text-align: center;" class="header"><div>差異</div></td>
	<td style="text-align: center;" class="header"><div>予算比</div></td>
	<td style="text-align: center;" class="header"><div>昨年実績</div></td>
	<td style="text-align: center;" class="header"><div>昨年対比</div></td>
	<td style="text-align: center;" class="header"><div>客数</div></td>
	<td style="text-align: center;" class="header"><div>昨年客数</div></td>
	<td style="text-align: center;" class="header"><div>昨年対比</div></td>
	<td style="text-align: center;" class="header"><div>フロア客数</div></td>
	<td style="text-align: center;" class="header"><div>昨年<br>フロア客数</div></td>
	<td style="text-align: center;" class="header"><div>昨年対比</div></td>
	<td style="text-align: center;" class="header"><div>週間仕入高</div></td>
	<td style="text-align: center;" class="header"><div>週間差益高</div></td>
	<td style="text-align: center;" class="header" colspan="2"><div>週間差益率</div></td>
</tr>
<%for(int i = 0; i < 6; i++){ %>
<tr id="info_sum_r<%=i+1 %>">
	<td style="text-align: center;" colspan="4"><span id="W1_<%=i %>">&nbsp;</span></td>
	<td style="text-align: right;"><span id="W2_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W3_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W4_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W5_<%=i %>" format="#,##0.0"></span></td>
	<td style="text-align: right;"><span id="W6_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W7_<%=i %>" format="#,##0.0"></span></td>
	<td style="text-align: right;"><span id="W8_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W9_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W10_<%=i %>" format="#,##0.0"></span></td>
	<td style="text-align: right;"><span id="W11_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W12_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W13_<%=i %>" format="#,##0.0"></span></td>
	<td style="text-align: right;"><span id="W14_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"><span id="W15_<%=i %>" format="#,##0"></span></td>
	<td style="text-align: right;"colspan="2"><span id="W16_<%=i %>" format="#,##0.0"></span></td>
</tr>
<%} %>
<tr id="info_sum_r7">
	<td class="green" style="text-align: center;" colspan="4"><span>月間計</span></td>
	<td class="green" style="text-align: right;"><span id="W2_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W3_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W4_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W5_T" format="#,##0.0"></span></td>
	<td class="green" style="text-align: right;"><span id="W6_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W7_T" format="#,##0.0"></span></td>
	<td class="green" style="text-align: right;"><span id="W8_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W9_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W10_T" format="#,##0.0"></span></td>
	<td class="green" style="text-align: right;"><span id="W11_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W12_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W13_T" format="#,##0.0"></span></td>
	<td class="green" style="text-align: right;"><span id="W14_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"><span id="W15_T" format="#,##0"></span></td>
	<td class="green" style="text-align: right;"colspan="2"><span id="W16_T" format="#,##0.0"></span></td>
</tr>
</table>
</div>
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
	<input type="hidden" name="userTenpo" id="userTenpo" value="<%=userTenpo %>"/>
	<input type="hidden" name="userBumon" id="userBumon" value="<%=userBumon %>"/>
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
<script type="text/javascript" src="../js/jquery.report/jquery.report.option.002.js?v=<%=prm %>"></script>
<script type="text/javascript" src="../js/jquery.report/jquery.report.common.js?v=<%=prm %>"></script>
<script type="text/javascript" src="../js/jquery.report/jquery.report.control.js?v=<%=prm %>"></script>
<script type="text/javascript" src="../js/jquery.report/jquery.report.events.js?v=<%=prm %>"></script>
</html>