<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="MS932" %>
<%@ page import="authentication.defines.*" %>
<%@ page import="authentication.bean.*" %>

<%
	String path = request.getContextPath();
	String logo = "../frame/img/logo_sato.gif";
	User lusr = (User)session.getAttribute(Consts.STR_SES_LOGINUSER);
	if (lusr != null){
		if (!"".equals(lusr.getLogo_())){
			logo = lusr.getLogo_();
		}
	}
	String icon = "../frame/img/ICON_AQUA.ico";
 %>
<link rel="icon" type="image/x-icon" href="<%=icon %>">

<div id="top_title">
<a href="https://www.sato-kyoto.com/"><img src="<%=logo %>" alt="社名ロゴ"></a>
</div>

<%
if ((request.getParameter(Form.LOGIN_VIEW) == null) && (session.getAttribute(Consts.STR_SES_LOGINUSER) != null)){
	// ログオン画面表示時のパラメータ情報を取得
	String User = session.getAttribute("_"+Form.LOGIN_USER)==null ? "" : (String)session.getAttribute("_"+Form.LOGIN_USER);
	String Pass = session.getAttribute("_"+Form.LOGIN_PASS)==null ? "" : (String)session.getAttribute("_"+Form.LOGIN_PASS);
	String View = session.getAttribute("_"+Form.LOGIN_VIEW)==null ? "" : (String)session.getAttribute("_"+Form.LOGIN_VIEW);
	String Parameter = "";
	if (!"".equals(User)){
		// ログアウト時に初期化するパラメータ情報
		Parameter = "?"+Form.LOGIN_USER+"="+User+"&"+Form.LOGIN_PASS+"="+Pass+"&"+Form.LOGIN_VIEW+"="+View;
	}
%>
<div id="top_navi">
	<span>　</span>
	<a href="<%=path%>/Servlet/Menu.do">メニュー</a>
	<span>　</span>
	<%--
	<a href="<%=path%>/Servlet/Login.do<%=Parameter %>">ログアウト</a>
	--%>
	<a href="#" onclick="window.close(); return false;">閉じる</a>
</div>
<div id="infomation">
	<div id="user_info" style="display:block;">
		<label><input type="checkbox" id="autoQuery" value="1" checked="checked">自動検索</label>
		<%--
		ようこそ、<%= lusr.getName() %>様 <br>
		<a href="<%=path%>/Servlet/PasswordChange.do?"+ <%=Form.MTN_SIDE%> + "','"+ <%=Consts.SYSTEM_MENTENANCE%> +"'>パスワード変更</a>
		 --%>
	</div>
</div>
<%
} else {
%>
<div id="top_navi">
	<span>　</span>
	<a href="#" onclick="window.close(); return false;">閉じる</a>
</div>
<%
}
%>