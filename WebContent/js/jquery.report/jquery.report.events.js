/**
 * jQuery : 1.6.2
 * レポート情報の初期化と共通イベント定義
 * 個別レポートに対応したイベントを作成する場合は、別ファイルに定義してください。
 */
$(function(){

	/**
	 * 初期化
	 * 事前読込 jquery.report.nn.js , jquery.report.control.js
	 * @param {Object} $.reportOption
	 */
	(function() {

		// レポートオプションの設定
		$.report($.reportOption);

		// レポート番号取得
		var reportno = $($.id.hidden_reportno).val();

		// レポート定義位置
		var reportNumber = $.getReportNumber(reportno);
		if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

		// マスク追加
		$.appendMask();

		// 検索条件検索
		$.ajax({
			url: "../EasyToJSON",
			type: 'POST',
			async: false,
			data: {
				"action": "autoQuery",
				"obj"	: "autoQuery",
				"target": "load",
				"sel"	: "json",
				"json"	: ""
			},
			success: function(data){
				var json = JSON.parse(data);
				try{
					if (json.rows[0]["SNAPSHOT"][0]["value"]==="1"){
						parent.$("#autoQuery").prop('checked',true);
					}else{
						parent.$("#autoQuery").prop('checked',false);
					}
				} catch(e){
				}
			}
		});
		parent.$("#autoQuery").on('click', function(event){

			// チェック状況取得
			var checked = parent.$('#autoQuery:checked').val();
			if (typeof checked === "undefined") checked = "";

			var jsonString = [];
			jsonString.push({
				"id":	"autoQuery",
				"value":checked,
				"text":	checked
			});

			// 検索条件保持
			$.ajax({
				url: "../EasyToJSON",
				type: 'POST',
				async: false,
				data: {
					"action": "autoQuery",
					"obj"	: "autoQuery",
					"target": "save",
					"sel"	: "json",
					"json"	: JSON.stringify(jsonString)
				}
			});
		});

		// initialize
		if(typeof $.report[reportNumber].initialize != "function") return true;
		$.report[reportNumber].initialize(reportno);

		// 	コールバック関数の紐付け
		// 検索  クリックイベント
		$('#'+$.id.btn_search).on("click", $.pushSearch);
		// Excel クリックイベント
		$('#'+$.id.btn_excel).on("click", $.pushExcel);

		// 登録(DB更新処理) クリックイベント
		$('#'+$.id.btn_entry).on("click", $.pushUpd);
		// 削除(DB更新処理) クリックイベント
		$('#'+$.id.btn_delete).on("click", $.pushDel);

//		// 条件リセット：戻る クリックイベント
//		$('#'+$.id.btn_reset).on("click", $.pushReset);
//
//		// 更新 クリックイベント
//		$('#'+$.id.btn_reload).on("click", $.pushReload);
//		// 追加 クリックイベント
//		$('#'+$.id.btn_add).on("click", $.pushAdd);
//		// 削除 クリックイベント
//		$('#'+$.id.btn_delete).on("click", $.pushDelete);
//		// 保存 クリックイベント
//		$('#'+$.id.btn_entry).on("click", $.pushEntry);
//		// 戻す クリックイベント
//		$('#'+$.id.btn_undo).on("click", $.pushUndo);

		// 定義保存：適用ボタン クリックイベント
		$('#'+$.id.btn_view_shiori).on("click", $.pushViewShiori);
		// 定義保存：保存ボタン クリックイベント
		$('#'+$.id.btn_entry_shiori).on("click", $.pushEntryShiori);
		// 定義保存：削除ボタン クリックイベント
		$('#'+$.id.btn_delete_shiori).on("click", $.pushDeleteShiori);

		// 登録処理系での帳票移動の警告メッセージ（タブは各JS内で呼出）
		// ヘッダー内リンク(閉じる除く)
		$('#header a', window.parent.document).filter("[id!='hlnk_close']").click(function(){
			// 登録系の場合、変更があった場合に確認メッセージ
			return $.confirmUnregist();
		});

		// ラジオボタン
		$(":radio").on("change",
			function(e){
				$('input[name=' + e.target.name + ']').closest("label").removeClass("selected_radio");
				$(e.target).closest("label").addClass("selected_radio");
		});
		$(":radio:checked").closest("label").addClass("selected_radio");

		shortcut.add("F6",function() { $('#'+$.id.btn_search).click(); });
		shortcut.add("F7",function() { $('#'+$.id.btn_excel).click(); });

		// EasyUI Combobox focus時にキャレット位置を先頭に設定
		$('.textbox-text.validatebox-text').on('focus', function() {
			if ($(this).attr('readonly')) {
				var pos = 0;
				var item = $(this).get(0);
				if (item.setSelectionRange) {  // Firefox, Chrome
					item.focus();
					item.setSelectionRange(pos, pos);
				} else if (item.createTextRange) { // IE
					var range = item.createTextRange();
					range.collapse(true);
					range.moveEnd("character", pos);
					range.moveStart("character", pos);
					range.select();
				}
			} else {
				$(this).select();
			}
		});

	})();

	/**
	 * resize Event
	 */
	var ResizeWindows=function(){
		// レポート番号取得
		var reportno=$($.id.hidden_reportno).val();

		// レポート定義位置
		var reportNumber = $.getReportNumber(reportno);
		if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

		// リサイズ処理
		$.report[reportNumber].setResize();
	};
	var resizeTimer = null;
	$(window).on('resize', function() {
		if ($.reg.resize) {
			if (resizeTimer) clearTimeout(resizeTimer);
			resizeTimer = setTimeout(ResizeWindows, 200);
		}
	});

	// backspaceキーによる「戻る」機能の無効化
	var ctrlBackSpace = function(e) {
		var code = e.which ? e.which : e.keyCode;
		if (code === 8) {
			var target = $(e.target);
			if ((!target.is('input:text') && !target.is('input:password') && !target.is('textarea')) || target.attr('readonly') || target.is(':disabled')) {
				return false;
			}
		}
		return true;
	};
	$(document).keydown(function(e) {
		return ctrlBackSpace(e);
	});
	$(window.parent.document).keydown(function(e) {
		return ctrlBackSpace(e);
	});
});

//EasyUI Combobox focus時にキャレット位置を先頭に設定
function ctrlFocus(obj){
	if ($(obj).attr('readonly')) {
		var pos = 0;
		var item = $(obj).get(0);
		if (item.setSelectionRange) {  // Firefox, Chrome
			item.focus();
			item.setSelectionRange(pos, pos);
		} else if (item.createTextRange) { // IE
			var range = item.createTextRange();
			range.collapse(obj);
			range.moveEnd("character", pos);
			range.moveStart("character", pos);
			range.select();
		}
	} else {
		$(obj).select();
	}
}
