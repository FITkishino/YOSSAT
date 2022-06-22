if ($.fn.pagination){
	$.fn.pagination.defaults.beforePageText = '頁';
	$.fn.pagination.defaults.afterPageText = '/ {pages}';
	$.fn.pagination.defaults.displayMsg = '{total} 行中 {from} ～ {to} 表示 ';
	$.fn.pagination.defaults.pageSize = 200;
	$.fn.pagination.defaults.pageList = [10,20,30,50,100,200];
}
if ($.fn.datagrid){
	$.fn.datagrid.defaults.loadMsg = 'しばらくお待ちください ...';
}
if ($.fn.treegrid && $.fn.datagrid){
	$.fn.treegrid.defaults.loadMsg = $.fn.datagrid.defaults.loadMsg;
}
if ($.messager){
	$.messager.defaults.ok = 'Ok';
	$.messager.defaults.cancel = 'Cancel';
}
if ($.fn.validatebox){
	$.fn.validatebox.defaults.missingMessage = 'この項目は、必須指定です。';
	$.fn.validatebox.defaults.rules.email.message = 'Please enter a valid email address.';
	$.fn.validatebox.defaults.rules.url.message = 'Please enter a valid URL.';
	$.fn.validatebox.defaults.rules.length.message = '{0} から {1} の文字数を入力してください。';
	$.fn.validatebox.defaults.rules.remote.message = 'Please fix this field.';
	$.extend($.fn.validatebox.defaults.rules, {
		intMaxLen: {
			validator: function(value, param){
				var str = value.replace(/-|,/g,"");
				var re = new RegExp("^[0-9]{0," + param[0] + "}$");
				if(str.match(re)){
					return true;
				}
				return false;
			},
			message: '{0}桁以下の半角数字で入力してください。'
		},
		floatMaxLen: {
			validator: function(value, param){
				// 少数点込み
				var re1 = new RegExp("^[0-9]{1," + param[0] + "}(\.[0-9]{1," + param[1] + "})?$");
				// 整数のみ
				var re2 = new RegExp("^[0-9]{0," + param[0] + "}$");
				return value.match(re1) && value.split('.')[0].match(re2);
			},
			message: '整数部{0}桁、小数部{1}桁の半角数字で入力してください。'
		},
		ym: {
			validator: function(value){
				return chkYm(value);
			},
			message: '正しい年月を西暦（YYYY/MM）で入力してください。'
		},
		yymmdd: {
			validator: function(value){
				return chkYmd(value);
			},
			message: '正しい日付を西暦（YYYY/MM/DD）で入力してください。'
		},
		yymmddAndNow: {
			validator: function(value){
				var rt = chkYmd(value);
				if(rt){
					rt = parseInt(value) >= parseInt(getToday().substr(2,6));
				}
				return rt;
			},
			message: '本日以降の日付を西暦（YYYY/MM/DD）で正しく入力してください。'
		},
		yymmddAndPast: {
			validator: function(value){
				var rt = chkYmd(value);
				if(rt){
					rt = parseInt(value) <= parseInt(getToday().substr(2,6));
				}
				return rt;
			},
			message: '本日以前の日付を西暦（YYYY/MM/DD）で正しく入力してください。'
		},
		yymmddAndPastZ: {
			validator: function(value){
				var rt = chkYmd(value);
				if(rt){
					rt = parseInt(value) < parseInt(getToday().substr(2,6));
				}
				return rt;
			},
			message: '前日以前の日付を西暦（YYYY/MM/DD）で正しく入力してください。'
		},
		intMaxLen: {
			validator: function(value, param){
				var str = value.replace(/-|,/g,"");
				var re = new RegExp("^[0-9]{0," + param[0] + "}$");
				if(str.match(re)){
					return true;
				}
				return false;
			},
			message: '{0}文字以下の半角数字で入力してください。'
		},
		intMinVal: {
			validator: function(value, param){
				var str = value.replace(/,/g,"");
				if(str.match(/^[0-9]+$/)){
					return parseInt(str) > param[0];
				}
				return false;
			},
			message: '{0}より大きい値を入力してください。'
		},
		intMinEqVal: {
			validator: function(value, param){
				var str = value.replace(/,/g,"");
				if(str.match(/^[0-9]+$/)){
					return parseInt(str) >= param[0];
				}
				return false;
			},
			message: '{0}以上の値を入力してください。'
		},
		int_range: {
			validator: function(value, param){
				var str = value.replace(/,/g,"");
				if(str.match(/^[0-9]+$/)){
					return parseInt(str) >= param[0] &&parseInt(str) <= param[1];
				}
				return false;
			},
			message: '{0}～{1}の範囲で入力してください。'
		},
		onlyHalfChar: {
			validator: function(value){
				return checkHalfChar(value);
			},
			message: '半角のみで入力してください。'
		},
		onlyFullChar: {
			validator: function(value){
				return checkFullChar(value);
			},
			message: '全角のみで入力してください。'
		}
	});
}
if ($.fn.textbox){
	$.fn.textbox.defaults.missingMessage = 'この項目は、必須指定です。';
}
if ($.fn.numberbox){
	$.fn.numberbox.defaults.missingMessage = 'この項目は、必須指定です。';
}
if ($.fn.numberspinner){
	$.fn.numberspinner.defaults.missingMessage = 'この項目は、必須指定です。';
}
if ($.fn.combobox){
	$.fn.combobox.defaults.missingMessage = 'この項目は、必須指定です。';
}
if ($.fn.combotree){
	$.fn.combotree.defaults.missingMessage = 'この項目は、必須指定です。';
}
if ($.fn.combogrid){
	$.fn.combogrid.defaults.missingMessage = 'この項目は、必須指定です。';
}
if ($.fn.calendar){
	$.fn.calendar.defaults.weeks = ['日','月','火','水','木','金','土'];
	$.fn.calendar.defaults.months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
}
if ($.fn.datebox){
	$.fn.datebox.defaults.currentText = '今日';
	$.fn.datebox.defaults.closeText = '閉じる';
	$.fn.datebox.defaults.okText = '適用';
	$.fn.datebox.defaults.missingMessage = 'この項目は、必須指定です。';
	$.fn.datebox.defaults.formatter = function(date){
		var y = date.getFullYear();
		var m = date.getMonth()+1;
		var d = date.getDate();
		return y+''+(m<10?('0'+m):m)+''+(d<10?('0'+d):d);
	};
	$.fn.datebox.defaults.parser = function(s){
		try {
			if (s.length == 8){
				var y = parseInt(s.substr(0,4),10);
				var m = parseInt(s.substr(4,2),10);
				var d = parseInt(s.substr(6,2),10);
				if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
					return new Date(y,m-1,d);
				}
			}
		} catch(e){}
		return new Date();
	};
}
if ($.fn.datetimebox && $.fn.datebox){
	$.extend($.fn.datetimebox.defaults,{
		currentText: $.fn.datebox.defaults.currentText,
		closeText: $.fn.datebox.defaults.closeText,
		okText: $.fn.datebox.defaults.okText,
		missingMessage: $.fn.datebox.defaults.missingMessage
	});
}

//年月入力値チェック
function chkYm(str) {
	try{
		// 正規表現による書式チェック
		if(str.match(/^([0-9]{4})\/(0[1-9]|1[012])$/)){
			// 妥当性チェック
			return checkDate(str+'01');
		}
	} catch(e) {
		return false;
	}
	return false;
}
//年月日入力値チェック
function chkYmd(str) {
	try{
		// 正規表現による書式チェック
		if(str.match(/^([0-9]{4})\/(0[1-9]|1[012])\/(0[1-9]|[12][0-9]|3[01])$/)){
			// 妥当性チェック
			return checkDate(str);
		}
	} catch(e) {
		return false;
	}

	return false;
}
/**
 * 日付の妥当性チェック
 * year 年
 * month 月
 * day 日
 */
function checkDate(s) {
	var y = parseInt(s.substr(0,4),10);
	var m = parseInt(s.substr(4,2),10);
	var d = parseInt(s.substr(6,2),10);
	var dt = new Date(y, m-1, d);
	if(dt === null || dt.getFullYear() !== y || dt.getMonth() + 1 !== m || dt.getDate() !== d) {
		return false;
	}
	return true;
}

/**
 * システム日付
 */
function getToday() {
	var dt = new Date();
	var y = dt.getFullYear();
	var m = dt.getMonth() + 1;
	var d = dt.getDate();
	return y + '' + $.ex.lpad(m, 2,'0') + '' + $.ex.lpad(d, 2,'0');
}
/**
 * 文字列のバイト数取得
 * text 対象文字列
 */
function getByte(text)
{
	count = 0;
	for (var i=0; i<text.length; i++){
		var c = text.charCodeAt(i);
		if ( (c >= 0x0 && c < 0x81) || (c == 0xf8f0) || (c >= 0xff61 && c < 0xffa0) || (c >= 0xf8f1 && c < 0xf8f4)){
			count += 1;
		} else {
			count += 2;
		}
	}
	return count;
}

//半角文字入力チェック
function checkHalfChar(text){
	for (var i=0; i<text.length; i++){
		var c = text.charCodeAt(i);
		if ( (c >= 0x0 && c < 0x81) || (c == 0xf8f0) || (c >= 0xff61 && c < 0xffa0) || (c >= 0xf8f1 && c < 0xf8f4)){

		} else {
			return false;
		}
	}
	return true;
}

//全角文字入力チェック
function checkFullChar(text){
	for (var i=0; i<text.length; i++){
		var c = text.charCodeAt(i);
		if ( (c >= 0x0 && c < 0x81) || (c == 0xf8f0) || (c >= 0xff61 && c < 0xffa0) || (c >= 0xf8f1 && c < 0xf8f4)){
			return false;
		}
	}
	return true;
}
