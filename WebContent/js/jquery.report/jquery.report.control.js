/**
 * レポート情報の共通部分と固有定義の初期化
 * $.exDate は、exdate.js の読み込みが必須。
 */

/**
 * デバッグ用
 **/
if (!('console' in window)) {
	window.console = {};
	window.console.log = function(str){
		return str;
	};
}

// 配列用 indexOf() 実装（IE8）
if (!Array.indexOf) {
	Array.prototype.indexOf = function(o) {
		for (var i in this) {
			if (this[i] == o) {
				return i;
			}
		}
		return -1;
	}
}
;(function($) {

$.report = function(options) {

	var defaults = {
		reg : {
			excel	:	"../ExcelGenerate",	// Excel出力用
			jqgrid	:	"../JQGridJSON",	// jqGrid 用
			jqeasy	:	"../JQEasyJSON",	// jquery.easyUI 用
			jqutil	:	"../JQEasyUtil",	// jqGrid util 用
			easy	:	"../EasyToJSON",	// jquery.easyUI 用
			debug 	:	true,				// デバッグ（有効:true, 無効:false)
			search	:	true,				// 初期表示時、検索実行（有効:true, 無効:false)
			resize	:	true,				// 画面リサイズ処理(有効:true, 無効:false)
			changeReportByTabs 	:	false	// タブ遷移時の条件引継ぎ機能（有効:true, 無効:false)
		},

		_ua : (function(){
			// version support
			return {
				ltIE6:typeof window.addEventListener == "undefined" && typeof document.documentElement.style.maxHeight == "undefined",
				ltIE7:typeof window.addEventListener == "undefined" && typeof document.querySelectorAll == "undefined",
				ltIE8:typeof window.addEventListener == "undefined" && typeof document.getElementsByClassName == "undefined",
				ie:document.uniqueID,
				firefox:window.globalStorage,
				opera:window.opera,
				webkit:!document.uniqueID && !window.opera && !window.globalStorage && window.localStorage,
				mobile:/android|iphone|ipad|ipod/i.test(navigator.userAgent.toLowerCase())
			};
		})(),

		log : function(baseTime, comment){
			if ($.reg.debug) {
				var message = comment + ((new Date()).getTime() - baseTime) + ' ms';
				status = message;
				console.log(message);
			}
		},

		getReportNumber : function (reportName) {
			// レポート名からレポート定義配列の位置取得
			for (var i=0; i < this.report.length; i++) {
				if ($.report[i].name === reportName) {
					return i;
				}
			}
		},

		getFormat : function(value, format){
			// 数値->文字フォーマット変換
			if (value == null || value.length < 1) return '';
			// 形式未指定時の初期値
			if (format == null) format='#,###';
			return $.formatNumber(value, {format:format, locale:"jp"});
		},

		getFormatDt : function(value, add20){
			// 数値->文字フォーマット変換
			if (undefined===add20) add20 = false;
			if (undefined===value) return '';
			if (value==='') return '';
			if(add20){
				if(value.match(/(^[0-9]{4}$)/)){
					return value.substr(0,2) + "/" + value.substr(2,2);
				}else if(value.match(/(^[0-9]{6}$)/)){
					return value.substr(0,2) + "/" + value.substr(2,2) + "/" + value.substr(4,2);
				}else{
					return value;
				}
			}
			if(value.match(/(^[0-9]{6}$)/)){
				return value.substr(2,2) + "/" + value.substr(4,2);
			}else if(value.match(/(^[0-9]{8}$)/)){
				return value.substr(2,2) + "/" + value.substr(4,2) + "/" + value.substr(6,2);
			}else if(value.match(/(^[0-9]{14}$)/)){
				return value.substr(2,2) + "/" + value.substr(4,2) + "/" + value.substr(6,2) + " " + value.substr(8,2) + ":" + value.substr(10,2);
			}else{
				return value;
			}
		},


		getJSONObject : function (jsonArray, idValue) {
			// JSON 配列から指定されたIDのオブジェクトを戻す
			// 該当がない場合は、該当なし。
			// jsonArray 配列判定
			if(jsonArray) {
				for (var i = 0; i < jsonArray.length; i++) {
					if (jsonArray[i].id === idValue) {
						return json = jsonArray[i];
					}
				}
			}
		},

		getJSONValue : function(jsonArray, id){
			var json = $.getJSONObject(jsonArray, id);
			if(json){
				return json.value;
			}
			return "";
		},

		getJSONText : function(jsonArray, id){
			var json = $.getJSONObject(jsonArray, id);
			if(json){
				return json.text;
			}
			return "";
		},

		setJSONObject : function (jsonArray, idValue, value, text) {
			// JSON 配列から指定されたIDにデータをセット
			// 該当がない場合は、追加する。
			// jsonArray 配列判定
			var flag = true;
			if(jsonArray) {
				for (var i = 0; i < jsonArray.length; i++) {
					if (jsonArray[i].id === idValue) {
						jsonArray[i].value = value;
						jsonArray[i].text = text;
						flag = false;
						break;
					}
				}
				if (flag) {
					// 存在しない場合に作成
					var json = {
						"id"	:	idValue,
						"value"	:	value,
						"text"	:	text
					};
					jsonArray.push(json);
				}
			}
		},

		setRadioInit : function(jsonHidden, id, that) {
			// Radio 要素の初期化
			// 初期化情報取得
			var json = $.getJSONObject(jsonHidden, id);
			if (json){
				// 初期化
				$('input[name="'+id+'"]').val([json.value]);
			}
			$('input[name="'+id+'"]').change(function() {
				// 検索ボタン有効化
				$.setButtonState('#'+$.id.btn_search, true, id);
			});

			if(that){
				if ($.inArray(id, that.initedObject) < 0){
					that.initedObject.push(id);
				}
				// 初期表示検索処理
				$.initialSearch(that);
			}
		},

		setCheckboxInit : function(jsonHidden, id, that) {
			// Checkbox 要素の初期化
			// 初期化情報取得
			var json = $.getJSONObject(jsonHidden, id);
			if (json && json.value.length > 0){
				// 初期化
				$('#'+id).attr('checked','checked');
			}
			$('#'+id).change(function() {
				// 検索ボタン有効化
				$.setButtonState('#'+$.id.btn_search, true, id);
			});

			if(that){
				if ($.inArray(id, that.initedObject) < 0){
					that.initedObject.push(id);
				}
				// 初期表示検索処理
				$.initialSearch(that);
			}
		},

		setCombogridValue : function(jsonArray, id) {

			var num = 0;
			var rows = $('#'+id).combogrid('grid').datagrid('getRows');
			var val = $.getJSONValue(jsonArray, id);
			for (var i=0; i<rows.length; i++){
				if (rows[i].VALUE == val){	// 値比較
					num = i;
					break;
				}
			}
			$('#'+id).combogrid('grid').datagrid('selectRow', num);
		},

		setCombogrid : function(id, val) {

			var num = 0;
			var rows = $('#'+id).combogrid('grid').datagrid('getRows');
			for (var i=0; i<rows.length; i++){
				if (rows[i].VALUE == val){	// 値比較
					num = i;
					break;
				}
			}
			$('#'+id).combogrid('grid').datagrid('selectRow', num);
		},

		setCombogridMultiple : function(jsonArray, id) {

			var dg = $('#'+id).combogrid('grid');
			dg.datagrid('uncheckAll');
			var data = dg.datagrid('getData');

			// 選択値設定
			var val = null;
			if ($.inArray(id, jsonArray) < 0){
				var json = $.getJSONObject(jsonArray, id);
				if(json && json.value!=""){
					val = new Array();
					for (var i=0; i<data.rows.length; i++){
						if ($.inArray(data.rows[i].VALUE, json.value)!=-1){
							val.push(data.rows[i].VALUE);
						}
					}
				}
			}
			if (val){
				$('#'+id).combogrid('setValues',val);
			}else{
				dg.datagrid('checkAll');
			}
		},

		setCombogridData : function(jsonArray, id) {

			var dg = $('#'+id).combogrid('grid');
			dg.datagrid('uncheckAll');
			var oldData = $.getJSONObject(jsonArray, id+'DATA');
			var data = dg.datagrid('getData');

			for(var i=0;i<oldData.value.total;i++){
				// データをセットする
				if (data.rows[i]['TY'] !== undefined) data.rows[i]['TY'] = oldData.value.rows[i]['TY']==='' ? '' : $.id.checkBoxOnData;
				if (data.rows[i]['ZY'] !== undefined) data.rows[i]['ZY'] = oldData.value.rows[i]['ZY']==='' ? '' : $.id.checkBoxOnData;
				if (data.rows[i]['ZC'] !== undefined) data.rows[i]['ZC'] = oldData.value.rows[i]['ZC']==='' ? '' : $.id.checkBoxOnData;
				if (data.rows[i]['ZS'] !== undefined) data.rows[i]['ZS'] = oldData.value.rows[i]['ZS']==='' ? '' : $.id.checkBoxOnData;

				if (data.rows[i]['TP'] !== undefined) data.rows[i]['TP'] = oldData.value.rows[i]['TP']==='' ? '' : $.id.checkBoxOnData;
				if (data.rows[i]['ZP'] !== undefined) data.rows[i]['ZP'] = oldData.value.rows[i]['ZP']==='' ? '' : $.id.checkBoxOnData;

				dg.datagrid('refreshRow', i);	// 変更内容を反映する
			}
		},

		getTargetValue : function() {
			// 指定要素の初期化
			try{
				var sendParam = $($.id.hiddenSendParam).val();
				if (typeof sendParam === 'string') {
					return JSON.parse(sendParam);
				}
			} catch(e){
			}
		},

		getInitValue : function() {
			// 指定要素の初期化
			try{
				var initParam = $($.id.hiddenInit).val();
				if (typeof initParam === 'string') {
					return JSON.parse(initParam);
				}
			} catch(e){
			}
		},

		tryClickSearch : function(){
			// 初期表示時に検索実行
			if ($.reg.search) {
				// 検索ボタン押下
				$('#'+$.id.btn_search).trigger('click');
			}
			// 検索ボタンにフォーカス
			$('#'+$.id.btn_search).focus();
		},

		tryShowToolbar : function(id,that){
			// （オプション）ツールバーの表示 Event
			// ツールバーが非表示の場合、1度だけ表示＆リサイズ
			// combbox,combgrid 構築時の要素変動を非表示に設定
			var toolbar = $(id);
			if ((toolbar.is(':hidden'))) {
				toolbar.show();
				that.setResize();
			}
		},

		tryChangeURL : function(url){
			// datagrid.url 定義
			// Load処理回避
			var options = $($.id.gridholder).datagrid('options');
			if(options) options.url = url;
		},

		setButtonState : function(id,status,call){
			// EasyUIのLinkButton enable / disable 切替
			$.log((new Date()).getTime(), call + '(' +id + '=' + status + '):');
			if (status) {
				// status == true >> enable
				$(id).linkbutton('enable');
			} else {
				// status == false >> disable
				$(id).linkbutton('disable');
			}
		},

		getDefaultPageSize: function(pageSize, pageList) {
			// 指定頁サイズが頁リストに存在するか確認
			if (pageSize==='' || !isFinite(pageSize)){
				pageSize = 0;
			}
			if ($.inArray(pageSize, pageList)===-1) {
				pageSize=pageList[0];
			}
			return pageSize;
		},

		/**
		 * datagrid sortable=ture のタイトルにアンダーバー
		 * @param id datagrid
		 * @param SortName field情報
		 * @param SortOrder "asc" | "desc" (default : asc)
		 */
		getDecorationUnderline : function(Title){
			if (Title === null || Title === undefined) {
				return '';
			}
			return "<span style='text-decoration:underline;'>"+Title+"</span>";
		},

		/**
		 * datagridの初期ソートアイコン表示
		 * @param id datagrid
		 * @param SortName field情報
		 * @param SortOrder "asc" | "desc" (default : asc)
		 */
		setDefaultSortColumnCSS : function (id, SortName, SortOrder) {
			// カラム情報取得
			var header = $(".datagrid-header-row").find('[field="'+SortName+'"]');
			if (header.length===0) return false;

			// 並び替え名称の確認
			if ($(id).datagrid('options').sortName === null) {
				// 初期ソートカラムのアイコン追加
				SortOrder=SortOrder||"asc";
				var cls='datagrid-sort-'+SortOrder;
				$(header).addClass(cls);
			} else {
				// 初期ソートカラムのアイコン削除
				$(header).removeClass("datagrid-sort-asc datagrid-sort-desc");
			}
		},

		/**
		 * Excel出力用タイトル作成
		 * @param {Object} title
		 * @param {Object} columns
		 * @return {Object} rtn
		 */
		outputExcelTitle : function(title, columns){
			var rtn = [];

			// カラム情報の読込
			var colIdx = -2;	// 列番号
			var flag = true;	// 次の列の処理続行フラグ
			var esc = [];		// 行単位で、作業済みのcolIdx+1を保持
			var org = [];		// 行単位で、columns配列indexを保持

			while (flag){
				colIdx++;
				flag = false;

				for (var rowIdx=0; rowIdx<columns.length; rowIdx++){
					if (colIdx == -1){
						// 初期設定
						flag = true;
						esc[rowIdx] = 0;
						org[rowIdx] = 0;
						rtn[rowIdx] = [];

						// title配列が空でない場合、先にrtn配列に入れておく
						if (title.length){
							if (rowIdx < title.length){
								rtn[rowIdx] = title[rowIdx].slice(0);
							} else {
								rtn[rowIdx] = title[title.length-1].slice(0);
							}
						}

					} else if (org[rowIdx] < columns[rowIdx].length && esc[rowIdx] <= colIdx){
						flag = true;

						if (columns[rowIdx][org[rowIdx]].title != null){
							var str = columns[rowIdx][org[rowIdx]].title.replace(/\<[\/]?(div|span|a)[^\<]*\>/g, '').replace(/\<br[^\<]*\>/g, '\n');
							// data 属性に置換
							if (columns[rowIdx][org[rowIdx]].data){
								str = columns[rowIdx][org[rowIdx]].data;
							}
							var colspan = columns[rowIdx][org[rowIdx]].colspan;
							var rowspan = columns[rowIdx][org[rowIdx]].rowspan;
							rtn[rowIdx].push(str);
							esc[rowIdx] += 1;
							org[rowIdx] += 1;

							if (colspan != null && rowspan != null){
								for (var col=1; col<colspan; col++){
									rtn[rowIdx].push(str);
									esc[rowIdx] += 1;
								}
								for (var row=1; row<rowspan && rowIdx+row<columns.length; row++){
									for (var col=0; col<colspan; col++){
										rtn[rowIdx+row].push(str);
										esc[rowIdx+row] += 1;
									}
								}
							} else if (colspan != null){
								for (var col=1; col<colspan; col++){
									rtn[rowIdx].push(str);
									esc[rowIdx] += 1;
								}
							} else if (rowspan != null){
								for (var row=1; row<rowspan && rowIdx+row<columns.length; row++){
									rtn[rowIdx+row].push(str);
									esc[rowIdx+row] += 1;
								}
							}
						}
					}
				}
			}
			return rtn;
		},

		/**
		 * Excel出力用レコード作成
		 * @param {Object} loadData
		 * @param {Object} options
		 * @param {Object} level
		 * @param {Object} frow
		 * @param {Object} crow
		 * @return {Object} data
		 */
		outputExcelRows : function(loadData, options, level, frow, crow){
			var data = [];
			var levelSpace=new Array(level).join('　');
			for ( var row in loadData ) {
				if (row.match(/^[0-9]+$/)) {
					var rowData = [];
					// 固定カラム情報
					for (var column in options.frozenColumns[frow]) {
						if (column.match(/^[0-9]+$/)) {
							if (options.treeField===options.frozenColumns[frow][column].field){
								rowData.push(levelSpace+loadData[row][options.frozenColumns[frow][column].field]);
							} else {
								rowData.push(loadData[row][options.frozenColumns[frow][column].field]);
							}
						}
					}
					// カラム情報
					for (var column in options.columns[crow]) {
						if (column.match(/^[0-9]+$/)) {
							rowData.push(loadData[row][options.columns[crow][column].field]);
						}
					}
					data.push(rowData);
				}
				// 下層情報
				if (loadData[row]['children']!=undefined){
					var rows = $.outputExcelRows(loadData[row]['children'],options, (level+1), frow, crow);
					for (var i=0, l=rows.length; i<l; i++) {
						data.push(rows[i]);
					}
				}
			}
			return data;
		},


		/**
		 * Excel出力用中段テーブルレコード作成
		 * @param {Object} loadData
		 * @param {Object} options
		 * @param {Object} level
		 * @param {Object} frow
		 * @param {Object} crow
		 * @return {Object} data
		 */
		outputExcelAddDataTable : function(selectors){
			var data = [];
			var rows = $(selectors);
			rows.each(function(i, row){
				var rowData = [];
				var colIdx = 0;
				$(row).find("td").each(function(j, col){
					var typ = "";
					var cellType = "";
					var format="";
					var val = "";
					var colspan = 0, rowspan = 0;
					if($(col).is(".header")){
						typ = "title";
						format = "@";
						val = $(col).text();
					}else if($(col).find("span").length > 0){
						typ = "data";
						format = $(col).find("span").attr('format');
						if(!format) format = "@";
						val = $(col).find("span").text();
						if(format!=="@"){
							val = val.replace(/,/g, '').replace("%", "");
						}
					}else if($(col).find("textarea").length > 0){
						typ = "data";
						format = "@\n";
						val = $(col).find("textarea").val();
					}else if($(col).find(":input").length > 0){
						typ = "data";
						format = $(col).find(":input").attr('format');
						if(!format) format = "@";
						val = $(col).find(":input").val();
						if(format!=="@"){
							val = val.replace(/,/g, '').replace("%", "");
						}
					}
					colspan = $(col).attr("colspan");
					rowspan = $(col).attr("rowspan");
					if (!colspan) colspan = 1;
					if (!rowspan) rowspan = 1;
					rowData.push({
						colNo:	colIdx,
						type:	typ,
						value:	val,
						format:	format,
						colspan:colspan,
						rowspan:rowspan
					});
					colIdx++;
				});
				data.push({
					rowNo:	i,
					data:	rowData
				});
			});
			return data;
		},

		/**
		 * タイトル数が規定値（255）超えていないか確認
		 * @param title タイトル情報
		 * @return true: 規定数を超えている、false: 規定数以内
		 */
		checkExcelTitle: function(title){
			return false;
//			if (this.getExcelTitle(title)>255){
//				alert("列数が255を超えているためExcel出力できません。");
//				return true;
//			}else{
//				return false;
//			}
		},
		/**
		 * タイトル数取得
		 */
		getExcelTitle: function(title){
			var titles = title.length;
			if (titles===0) return 0;
			return title[titles-1].length;
		},
		/**
		 * （必須）初期表示完了時の検索実行処理
		 * @param {Object} that	- 各画面用Js
		 */
		initialSearch : function(that){
			console.log(that.initedObject.length + ' = ' + that.initedObject[that.initedObject.length-1] + ", that.initObjNum="+that.initObjNum);
			if (that.initedObject.length == that.initObjNum){
				that.initObjNum = -1;
				// パネル表示状況取得
				var panelState = $.getJSONValue(that.jsonHidden, $.id.panelState);
				if (panelState===""){
					panelState=true;
				}
				if (!panelState) {
					// 検索条件エリアを縮小
					$($.id.toolbar).panel('collapse', false);
				} else {
					// ツールバー表示
					$.tryShowToolbar($.id.toolbar, that);
				}
				if (typeof that.setInitSets === 'function'){
					that.setInitSets();	// 期間初期値取得
				}

				// 自動検索＝有効またはChangeReport経由の場合
				if(parent.$('#autoQuery:checked').val()==="1" || that.onChangeReport){
					that.onChangeReport = true;
					setTimeout(function(){
						// 検索ボタン押下
						$.tryClickSearch();
					}, 100);
				}else{
					// マスク削除
					$.removeMask();
				}
			}
		},

		/**
		 * （必須）セッションタイムアウト、利用時間外の確認
		 * @param {Object} that	- 各画面用Js
		 */
		checkIsTimeout : function(that){
			if(that != null && that.initObjNum != null && that.initObjNum > 0){
				// 帳票初期表示時は、確認しない
				return false;
			}
			var rt = false;
			$.ajax({
				url: $.reg.easy,
				type: 'POST',
				async: false,
				data: {
					"page"	: $($.id.hidden_reportno).val(),
					"action": $.id.action_get,
					"sel"	: (new Date()).getTime(),
					"json"	: "",
					"obj"	: $.id.btn_search,
					"userid": $($.id.hidden_userid).val()
				},
				success: function(json){
					var data = JSON.parse(json);
					if (data.rows[0][1]==="1") {
						// 頁のリフレッシュ
						window.parent.location = window.parent.location;
						rt = true;
					}
				}
			});
			return rt;
		},

		/**
		 * 検索ボタンイベント
		 * @param {Object} e
		 */
		pushSearch : function(e){
			if ($(this).linkbutton('options').disabled)	return false;

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// マスク削除
			$.removeMask();

			// フォーム情報取得
			$.report[reportNumber].getEasyUI();

			if ($.report[reportNumber].validation()) {
				// 検索ボタン無効化
				$.setButtonState('#'+$.id.btn_search, false, 'success');

				// マスク追加
				$.appendMask();

				// セッションタイムアウト、利用時間外の確認
				var isTimeout = $.checkIsTimeout();
				if (! isTimeout) {
					// 検索条件保持
					$.ajax({
						url: $.reg.easy,
						type: 'POST',
						async: false,
						data: {
							"page"	: reportno,
							"obj"	: $.id.btn_search,
							"sel"	: "json",
							"userid": $($.id.hidden_userid).val(),
							"user"	: $($.id.hiddenUser).val(),
							"report": $($.id.hiddenReport).val(),
							"json"	: JSON.stringify($.report[reportNumber].getJSONString())
						},
						success: function(json){
							// 検索実行
							$.report[reportNumber].success(reportno);
						}
					});
				}
				return true;
			} else {
				return false;
			}
		},

		/**
		 * 検索処理エラー判定
		 * @param {String} json
		 */
		searchError : function(json){
			if (json == null || json.length < 1){
				// メッセージ表示
				$.messager.alert($.message.ID_MESSAGE_TITLE_ERR,'検索処理でエラーが発生しました。','error');
				// マスク削除
				$.removeMask();
				if($($.id.gridholder).hasClass("datagrid-f")){
					$($.id.gridholder).datagrid('loaded');
				}else{
					$.removeMaskMsg();
				}
				return true;
			}
			return false;
		},

		/**
		 * 帳票の状態保持（ページサイズ、パネル表示on/off、検索条件）
		 */
		saveState : function(reportno, jsonString, datagrid){
			if (jsonString == null) return false;
			// ページサイズの取得
			var options = $(datagrid).datagrid('options');
			// ページサイズの保持
			var pageSize = $.getJSONValue(jsonString, $.id.pageSize);
			if (pageSize===""){
				// 未定義の場合、新規追加
				jsonString.push({
					id:		$.id.pageSize,
					value:	options.pageSize,
					text:	options.pageSize
				});
			} else {
				// 定義済の場合、最新情報の設定
				$.getJSONObject(jsonString, $.id.pageSize).value = options.pageSize;
			}

			// 検索条件パネルの表示状況
			var panelState = this.getToolbarState($.id.toolbar);
			var collapsible = $.getJSONValue(jsonString, $.id.panelState);
			if (collapsible===""){
				// 未定義の場合、新規追加
				jsonString.push({
					id:		$.id.panelState,
					value:	panelState,
					text:	panelState
				});
			} else {
				// 定義済の場合、最新情報の設定
				$.getJSONObject(jsonString, $.id.panelState).value = panelState;
			}

			// 検索条件保持
			$.ajax({
				url: $.reg.easy,
				type: 'POST',
				async: false,
				data: {
					"page"	: reportno,
					"obj"	: $.id.btn_search,
					"target":"state",
					"sel"	: "json",
					"userid": $($.id.hidden_userid).val(),
					"user"	: $($.id.hiddenUser).val(),
					"report": $($.id.hiddenReport).val(),
					"json"	: JSON.stringify(jsonString)
				},
				success: function(){
					// マスク削除
					$.removeMask();
				},
				error: function(){
					// マスク削除
					$.removeMask();
				}
			});
		},

		/**
		 * 帳票の状態保持（ページサイズ、パネル表示on/off、検索条件）
		 */
		saveState2 : function(reportno, jsonString){
			if (jsonString == null) return false;

			// 検索条件保持
			$.ajax({
				url: $.reg.easy,
				type: 'POST',
				async: false,
				data: {
					"page"	: reportno,
					"obj"	: $.id.btn_search,
					"target":"state",
					"sel"	: "json",
					"userid": $($.id.hidden_userid).val(),
					"user"	: $($.id.hiddenUser).val(),
					"report": $($.id.hiddenReport).val(),
					"json"	: JSON.stringify(jsonString)
				},
				success: function(){
					// Loading非表示
					$.removeMaskMsg();
					// マスク削除
					$.removeMask();
				},
				error: function(){
					// Loading非表示
					$.removeMaskMsg();
					// マスク削除
					$.removeMask();
				}
			});
		},

		/**
		 * マスク表示
		 */
		appendMask: function(){
			// マスク追加
			var _1ac=parent.$("#container");
			$("<div class=\"datagrid-mask\" style=\"display:block\"></div>").appendTo(_1ac);
		},

		/**
		 * マスク削除
		 */
		removeMask: function(){
			// マスク追加
			var _1ad=parent.$("#container");
			_1ad.children("div.datagrid-mask").remove();
		},

		/**
		 * マスクメッセージ表示(datagridが無いページ用)
		 */
		appendMaskMsg: function(loadMsg){
			if( loadMsg===undefined || loadMsg===null ){
				loadMsg = $.fn.datagrid.defaults.loadMsg;
			}
			var panel = parent.$("#container");
			var msg=$("<div class=\"datagrid-mask-msg\" style=\"display:block;left:50%;font-size: 12px;\"></div>").html(loadMsg).appendTo(panel);
			msg._outerHeight(40);
			msg.css({marginLeft:(-msg.outerWidth()/2),lineHeight:(msg.height()+"px")});
		},

		/**
		 * マスクメッセージ削除(datagridが無いページ用)
		 */
		removeMaskMsg: function(){
			var panel = parent.$("#container");
			panel.children("div.datagrid-mask-msg").remove();
		},

		getKikanYM: function(id,target,flag){
			// grid 取得
			var grid = $(id).combogrid('grid');
			if (grid == undefined)	return false;
			var data = grid.datagrid('getData');
			if (flag){
				for(var i=0;i<data.rows.length;i++){
					if(data.rows[i].NENDO_Y===target){
						return data.rows[i].VALUE;
					}
				}
			}else{
				for(var i=data.rows.length-1;i>=0;i--){
					if(data.rows[i].NENDO_Y===target){
						return data.rows[i].VALUE;
					}
				}
			}
			return "";
		},
		/**
		 * ツールバーの表示状況
		 */
		getToolbarState : function(id){
			// ツールバーが表示中は、true, 非表示中は、false
			return !$(id).is(':hidden');
		},

		/**
		 * showPanel action
		 */
		setScrollGrid : function(that){
			// grid 取得
			var grid = $(that).combogrid('grid');
			if (grid == undefined)	return false;

			// 選択済情報取得
			var row = grid.datagrid('getSelected');
			if (row == undefined)	return false;
			var rows = grid.datagrid('getRows');

			// DataGrid の onSelect イベントを無効化（塗りつぶし）
			var onSelect = grid.datagrid('options').onSelect;
			grid.datagrid('options').onSelect = function(){};

			// DataGrid の onChange イベントを無効化（塗りつぶし）
			var onChange = $(that).combo('options').onChange;
			$(that).combo('options').onChange = function(){};

			// DataGrid の onSelect イベントを元に戻す
			grid.datagrid('options').onSelect = onSelect;

			// Panel 表示後に選択
			grid.datagrid('scrollTo', grid.datagrid('getRowIndex', row[0]));

			// DataGrid の onChange イベントを元に戻す
			$(that).combo('options').onChange = onChange;
		},

		/**
		 * form.submit() 画面遷移用
		 */
		SendForm : function(s){
			var def = {
				type: 'get',
				url: location.href,
				data: {}
			};

			s = jQuery.extend(true, s, jQuery.extend(true, {}, def, s));

			var form = $('<form>')
				.attr({
					'method': s.type,
					'action': s.url
				})
				.appendTo(top.document.body);

			for (var a in s.data) {
				$('<input>')
					.attr({
						'name': a,
						'value': s.data[a]
					})
					.appendTo(form[0]);
			};
			form[0].submit();
		},

		/**
		 * タブによる帳票移動（検索条件保持）
		 * @param that
		 */
		changeReportByTabs : function(that){
			if ($.reg.changeReportByTabs) {
				$('#tabs a', window.parent.document).click(function(){
					// タブ要素(a)取得
					var elems = $('#tabContent', window.parent.document).map(
						function(i,e) {
							return e;
						}).get();
					var href = elems[0].value.split(',');

					// 遷移判定
					var index = $('#tabs a', window.parent.document).index(this);
					var childurl = href[index+1];

					// JSON Object Clone ()
					var sendJSON = JSON.parse( JSON.stringify( that.jsonString ) );
					$.setJSONObject(sendJSON, 'sendMode',	0,	0);	// 日付初期化のため

					$.SendForm({
						type: 'post',
						url: childurl,
						data: {
							sendMode:	0,
							sendParam:	JSON.stringify( sendJSON )
						}
					});

					return false;
				});
			}else{
				$('#tabs a', window.parent.document).click(function(){
					if($(this).parent($.id.hiddenChangedIdx).length < 1){
						// 登録系の場合、変更があった場合に確認メッセージ
						return $.confirmUnregist();
					}
				});
			}
		},

		/**
		 * 帳票移動の警告処理
		 * @param msg
		 */
		confirmUnregist : function(msg){
			// 登録系の場合、変更があった場合に確認メッセージ
			var rt = true;
			msg = $.getConfirmUnregistMsg(msg);
			if(msg !== ""){
				rt = confirm(msg);
			}
			if(rt){
				$($.id.hiddenChangedIdx).val("");
			}
			return rt;
		},
		/**
		 * グリッド更新がない場合の警告処理
		 * @param msg
		 */
		confirmUnregist2 : function(msg){
			// 登録系の場合、変更があった場合に確認メッセージ
			var rt = true;
			msg = $.getConfirmUnregistMsg(msg);
			if(msg !== ""){
				rt = confirm(msg);
			}
			return rt;
		},


		/**
		 * 帳票移動の警告メッセージ取得
		 * @param msg
		 */
		getConfirmUnregistMsg : function(msg){
			// 登録系の場合、変更があった場合に確認メッセージ
			msg = msg!==undefined && msg!=="" ? msg : '未登録ですが移動してもよろしいでしょうか？';
			var changedIdxVal = $($.id.hiddenChangedIdx).val();
			if(changedIdxVal !== undefined && changedIdxVal !== ""){
				return msg;
			}
			return "";
		},

		/**
		 * 変更
		 * @param msg
		 */
		setChangeIdx:function (index){		// 変更行Index保持
			// 変更行Index保持
			var changedIndex = $($.id.hiddenChangedIdx).val().split(",");
			if($.inArray(index+'',changedIndex)===-1){
				changedIndex.push(index);
				$($.id.hiddenChangedIdx).val(changedIndex.join(",").replace(/^,/,""));
			}
		},

		/**
		 * Excelボタンイベント
		 * @param {Object} e
		 */
		pushExcel : function(e){
			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// 検索結果の再確認
			if ($.report[reportNumber].getRecord() <= 0){
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,$.message.ID_MESSAGE_WARNING_EXCEL_OUTPUT,'warning');
				return false;
			}
			// 変更内容の確認
			var rt = $.confirmUnregist2('未登録の内容は出力されませんが、実行してもよろしいでしょうか？');
			if(!rt){ return false;}

			// Excel出力ボタン無効化
			$.setButtonState('#'+$.id.btn_excel, false, $.id.btn_excel);
			// マスク追加
			$.appendMask();
			$.appendMaskMsg();

			// ログ情報の格納
			$.post(
				$.reg.easy ,
				{
					"page"	: reportno ,
					"obj"	: $.id.btn_excel ,
					"sel"	: new Date().getTime(),
					"userid": $($.id.hidden_userid).val(),
					"user"	: $($.id.hiddenUser).val(),
					"report": $($.id.hiddenReport).val(),
					"json"	: ""
				},
				function(json){}
			);

			// Excel 出力
			if ($.report[reportNumber].excel != undefined) {
				$.report[reportNumber].excel(reportno);
			}else{
				$.outputExcel(reportno);
			}
			return false;
		},

		/**
		 * Excel出力実行
		 * @param {Object} reportno
		 * @param {Object} kbn
		 */
		outputExcel : function(reportno, kbn){
			if(kbn == null) kbn = 0;
			window.parent.frames['blank'].open($.reg.excel+'?report='+reportno+'&kbn='+kbn+'&ts='+(new Date()).getTime(),'_self','width=400, height=300, menubar=no, toolbar=no, scrollbars=yes');
			// Excel出力ボタン有効化
			$.setButtonState('#'+$.id.btn_excel, true, $.id.btn_excel);
			// マスク削除
			$.removeMask();
			$.removeMaskMsg();
			return false;
		},

		/**
		 * Excel出力エラー
		 */
		outputExcelError : function(){
			$.messager.alert($.message.ID_MESSAGE_TITLE_ERR,'Excel出力に失敗しました。','error');
			// Excel出力ボタン有効化
			$.setButtonState('#'+$.id.btn_excel, true, $.id.btn_excel);
			// マスク削除
			$.removeMask();
			$.removeMaskMsg();
		},

		/**
		 * 登録(DB更新)ボタンイベント
		 * @param {Object} e
		 */
		pushUpd:function(e){
			if ($(this).linkbutton('options').disabled)	return false;

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();
			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// JS情報取得
			var that = $.report[reportNumber];
			var id = $(this).attr('id');

			// チェック・確認処理
			var rtn = false;
			if($($.id.hiddenChangedIdx).val() !== undefined ){
				// Grid内変更情報取得
				var changedIndex = $($.id.hiddenChangedIdx).val().split(",");
				rtn = changedIndex.length > 0 && changedIndex[0] !== undefined && changedIndex[0] !== "";
				if(!rtn){
					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"更新対象データはありません。",'warning');
					return false;
				}
			}
			if($.isFunction(that.updValidation)) { rtn = that.updValidation(id);}
			if(rtn){
				var func = function(r){
					if (r) {
						// マスク追加
						$.appendMask();

						// セッションタイムアウト、利用時間外の確認
						if ($.checkIsTimeout()) return false;

						// ログ情報の格納
						$.post(
							$.reg.easy ,
							{
								"page"	: reportno ,
								"obj"	: id,
								"sel"	: new Date().getTime(),
								"userid": $($.id.hidden_userid).val(),
								"user"	: $($.id.hiddenUser).val(),
								"report": $($.id.hiddenReport).val(),
								"json"	: ""
							},
							function(json){}
						);
						that.updSuccess(id);

						return true;
					} else {
						return false;
					}
				};
				$.messager.confirm($.message.ID_MESSAGE_TITLE_CONF,'登録します。よろしいでしょうか？', func);
			}
		},

		/**
		 * 登録(DB更新)処理エラー判定
		 * @param {String} json
		 */
		updError : function(id, data){
			var isErr = false;
			var json = JSON.parse(data);
			if (json == null || json.length < 1 || json.opts === null || json.opts === undefined || (json.opts.E_MSG === undefined && json.opts.S_MSG === undefined)){
				// メッセージ表示
				$.messager.alert($.message.ID_MESSAGE_TITLE_ERR,'登録処理でエラーが発生しました。','error');
				isErr = true;
			}else if(json.opts.E_MSG !== undefined){
				var msg = "";
				if($.isArray(json.opts.E_MSG)){
					$.each(json.opts.E_MSG, function() {
						msg += this.MSG + "\n";
					});
				}else{
					msg = json.opts.E_MSG;
				}
				$.messager.alert($.message.ID_MESSAGE_TITLE_ERR,msg,'error');
				isErr = true;
			}
			if(isErr){
				// マスク削除
				$.removeMask();
				if($($.id.gridholder).hasClass("datagrid-f")){
					$($.id.gridholder).datagrid('loaded');
				}else{
					$.removeMaskMsg();
				}
				return true;
			}
			return false;
		},

		/**
		 * 削除ボタンイベント
		 * @param {Object} e
		 */
		pushDel:function(e){
			if ($(this).linkbutton('options').disabled)	return false;

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();
			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// JS情報取得
			var that = $.report[reportNumber];
			var id = $(this).attr('id');

			// チェック・確認処理
			var rtn = false;

			if($.isFunction(that.delValidation)) { rtn = that.delValidation(id);}
			if(rtn){
				var func = function(r){
					if (r) {
						// マスク追加
						$.appendMask();

						// セッションタイムアウト、利用時間外の確認
						if ($.checkIsTimeout()) return false;

						// ログ情報の格納
						$.post(
							$.reg.easy ,
							{
								"page"	: reportno ,
								"obj"	: id,
								"sel"	: new Date().getTime(),
								"userid": $($.id.hidden_userid).val(),
								"user"	: $($.id.hiddenUser).val(),
								"report": $($.id.hiddenReport).val(),
								"json"	: ""
							},
							function(json){}
						);
						that.delSuccess(id);

						return true;
					} else {
						return false;
					}
				};
				$.messager.confirm($.message.ID_MESSAGE_TITLE_CONF,'削除します。よろしいでしょうか？', func);
			}
		},
		/**
		 * 削除(DB更新)処理エラー判定
		 * @param {String} json
		 */
		delError : function(id, data){
			var isErr = false;
			var json = JSON.parse(data);
			if (json == null || json.length < 1 || json.opts === null || json.opts === undefined|| (json.opts.E_MSG === undefined && json.opts.S_MSG === undefined)){
				// メッセージ表示
				$.messager.alert($.message.ID_MESSAGE_TITLE_ERR,'削除処理でエラーが発生しました。','error');
				isErr = true;
			}else if(json.opts.E_MSG !== undefined){
				var msg = "";
				if($.isArray(json.opts.E_MSG)){
					$.each(json.opts.E_MSG, function() {
						msg += this.MSG + "\n";
					});
				}else{
					msg = json.opts.E_MSG.MSG;
				}
				$.messager.alert($.message.ID_MESSAGE_TITLE_ERR,msg,'error');
				isErr = true;
			}
			if(isErr){
				// マスク削除
				$.removeMask();
				if($($.id.gridholder).hasClass("datagrid-f")){
					$($.id.gridholder).datagrid('loaded');
				}else{
					$.removeMaskMsg();
				}
				return true;
			}
			return false;
		},

		/**
		 * 更新ボタンイベント
		 * @param {Object} e
		 */
		pushReload:function(e){

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// ボタン押下処理
			$.report[reportNumber].pushReload(e.id);

		},
		/**
		 * 追加ボタンイベント
		 * @param {Object} e
		 */
		pushAdd:function(e){

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// ボタン押下処理
			$.report[reportNumber].pushAdd(e.id);

		},
		/**
		 * 削除ボタンイベント
		 * @param {Object} e
		 */
		pushDelete:function(e){

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

//			// メッセージ表示
//			alert('削除の確定は保存ボタンを押してください。\r\n \r\n戻るボタンで取消が出来ます。\r\n');
			// ボタン押下処理
			$.report[reportNumber].pushDelete(e.id);

		},
		/**
		 * 保存ボタンイベント
		 * @param {Object} e
		 */
		pushEntry:function(e){

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// ボタン押下処理
			$.report[reportNumber].pushEntry(e.id);

		},
		/**
		 * 戻すボタンイベント
		 * @param {Object} e
		 */
		pushUndo:function(e){

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// ボタン押下処理
			$.report[reportNumber].pushUndo(e.id);

		},


		/**
		 * 定義保存：適用ボタンイベント
		 * @param {Object} e
		 */
		pushViewShiori:function(e){

			if ($(this).linkbutton('options').disabled)	return false;

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// ボタン押下処理
			$.report[reportNumber].pushViewShiori(e.id);

		},
		/**
		 * 定義保存：保存ボタンイベント
		 * @param {Object} e
		 */
		pushEntryShiori:function(e){

			if ($(this).linkbutton('options').disabled)	return false;

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// フォーム情報取得
			$.report[reportNumber].getEasyUI();

			// ボタン押下処理
			$.report[reportNumber].pushEntryShiori(e.id);

		},
		/**
		 * 定義保存：削除ボタンイベント
		 * @param {Object} e
		 */
		pushDeleteShiori:function(e){

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// ボタン押下処理
			$.report[reportNumber].pushDeleteShiori(e.id);

		},

		/**
		 * 条件リセット：戻るボタンイベント
		 * @param {Object} e
		 */
		pushReset:function(e){

			// レポート番号取得
			var reportno=$($.id.hidden_reportno).val();

			// レポート定義位置
			var reportNumber = $.getReportNumber(reportno);
			if (typeof(reportNumber) !== 'number') { alert("レポート定義が見つかりません。"); return false;}

			// 条件エリア表示
			$.tryShowToolbar($.id.toolbar, $.report[reportNumber]);

			// 条件初期値セット処理
			$.report[reportNumber].initCondition(e.id);
		},

		/**
		 * Combobox、Combogridにフォーカスを移動時、全選択状態にする
		 * @param {Object} id
		 */
		setFocusEvent : function(id){
			var combotext = $('#'+id).combo('textbox');
			combotext.focus(function(){
				$(this).select();
			});
			combotext.mouseup(function(e){
				e.preventDefault();
			});
		},

		/**
		 * ComboBox共通作成用
		 * @param {Object} jsonHidden
		 * @param {Object} id
		 */
		setCombobox : function(jsonHidden, id, that){
			$('#'+id).combobox({
				required:false,
				editable:false,
				panelHeight:'auto',
				onLoadSuccess:function(data){
					// 初期化
					var num = 0;
					var val = $.getJSONValue(jsonHidden, id);
					for (var i=0; i<data.length; i++){
						if (data[i].value == val){
							num = i;
							break;
						}
					}
					if (num > 0){
						$(this).combobox('setValue',val);
					}
					if(that){
						if ($.inArray(id, that.initedObject) < 0){
							that.initedObject.push(id);
						}
						// 初期表示検索処理
						$.initialSearch(that);
					}
				},
				onSelect:function(record){
					// 検索ボタン有効化
					$.setButtonState('#'+$.id.btn_search, true, id);
				}
			});
		},

		/**
		 * NumberSpinner共通作成用
		 * @param {Object} jsonHidden
		 * @param {Object} id
		 * @param {Object} min
		 * @param {Object} max
		 */
		setNumberspinner : function(jsonHidden, id, min, max, that){
			$('#'+id).numberspinner({
				required:true,
				editable:true,
				min: min,
				max: max,
				onSpinUp:function(){
					// 検索ボタン有効化
					$.setButtonState('#'+$.id.btn_search, true, id);
				},
				onSpinDown:function(){
					// 検索ボタン有効化
					$.setButtonState('#'+$.id.btn_search, true, id);
				},
				onChange:function(newValue,oldValue){
					// 検索ボタン有効化
					$.setButtonState('#'+$.id.btn_search, true, id);
				}
			});
			// 初期化
			var json = $.getJSONObject(jsonHidden, id);
			if (json){
				$('#'+id).numberspinner('setValue',json.value);
			}
			if(that){
				if ($.inArray(id, that.initedObject) < 0){
					that.initedObject.push(id);
				}
				// 初期表示検索処理
				$.initialSearch(that);
			}
		},

		/**
		 * データテーブル（ヘッダー/フッターなど）値セット
		 * @param {Object} selectors	- 設定エリアのセレクター
		 * @param {Object} prefix		- 値設定SPANのID
		 * @param {Object} data			- データ
		 * @param {Object} len			- データのサイズ
		 */
		setDataTable : function(selectors, prefix, data, len){
			// 値セット
			var target = $(selectors + ' *[id^="' + prefix + '"]');
			target.text("");
			if(!data) return false;
			if(Object.keys(data).length===0) return false;
			for(var i=1; i <= len; i++){
				var obj = target.filter('#'+prefix+i);
				if(obj.length === 0){ continue; }
				if(obj.is("span") && prefix+i in data){
					var format = obj.attr('format');
					if(format!==undefined){
						if(format==="date"){
							obj.text($.getFormatDt(data[prefix+i]));
						}else{
							obj.text($.getFormat(data[prefix+i], format));
						}
					}else{
						obj.text(data[prefix+i]);
					}
				}
			}
		},
		/**
		 * 閏年チェック
		 * @param y
		 * @returns {Boolean}
		 */
		isLeapYear : function isLeapYear(y) {
			return !(y % 4) && (y % 100) || !(y % 400) ? true : false;
		},

		/**
		 * 期間指定チェック
		 * @param kikanFrom
		 * @param kikanTo
		 * @param kbnKikan
		 * @returns {Boolean}
		 */
		checkKikan : function(kikanFrom, kikanTo, kbnKikan){
			if (kikanFrom > kikanTo){
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"検索期間を正しく指定してください。",'warning');
				return false;
			}
			if (kbnKikan === $.id.valueKikan_FYear){
				// 年
				var limit = 7;
				var dt1 = kikanFrom.substring(0,4) * 1;
				var dt2 = kikanTo.substring(0,4) * 1;
				if (dt2 - dt1 > limit - 1) {
					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'検索期間は' + limit + '年以内で指定してください。','warning');
					return false;
				}
			} else if (kbnKikan === $.id.valueKikan_Month){
				// 月
				var limit =26;	// 2016/05/30 要望により変更 13 → 26
				var dt1 = kikanFrom.substring(0,4) * 12 + kikanFrom.substring(4,6) * 1;
				var dt2 = kikanTo.substring(0,4) * 12 + kikanTo.substring(4,6) * 1;
				if (dt2 - dt1 > limit - 1) {
					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'検索期間は' + limit + 'ヶ月以内で指定してください。','warning');
					return false;
				}
			} else if (kbnKikan === $.id.valueKikan_Week){
				// 週 (日数で判定する)
				var limit = 55;	// 2016/05/30 要望により変更 30 → 55週
				var dt1 = new Date(kikanFrom.substring(0,4), kikanFrom.substring(4,6) - 1, kikanFrom.substring(6,8));
				var dt2 = new Date(kikanTo.substring(0,4), kikanTo.substring(4,6) - 1, kikanTo.substring(6,8));
				var diff = (dt2.getTime() - dt1.getTime()) / (24 * 3600 * 1000);
				if (diff > limit * 7 - 1) {
					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'検索期間は' + limit + '週間以内で指定してください。','warning');
					return false;
				}
			} else {
				// 日
				var limit = 62;	// 2016/05/30 要望により変更 31 → 42 → 62
				var dt1 = new Date(kikanFrom.substring(0,4), kikanFrom.substring(4,6) - 1, kikanFrom.substring(6,8));
				var dt2 = new Date(kikanTo.substring(0,4), kikanTo.substring(4,6) - 1, kikanTo.substring(6,8));
				var diff = (dt2.getTime() - dt1.getTime()) / (24 * 3600 * 1000);
				if (diff > limit - 1) {
					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'検索期間は' + limit + '日以内で指定してください。','warning');
					return false;
				}
			}
			return true;
		},

		/**
		 * 店舗指定チェック
		 * @param Tenpo_ck
		 * @returns {Boolean}
		 */
		checkTenpo : function(Tenpo_ck){
			var arryTenpo = String(Tenpo_ck).split(",");
			var num = 0;
			for (var i = 0; i < arryTenpo.length; ++i ) {
				if(arryTenpo[i] < 0){
					num = num +1;
				}
			}
			if (num >= 1 && arryTenpo.length > 1 ){
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'全店,既存店等指定時は複数指定できません。','warning');
				return false;
			}
			return true;
		},

		/**
		 * 店舗指定チェック
		 * @param Tenpo_ck
		 * @returns {Boolean}
		 */
		checkTenpoRfm : function(Tenpo_ck){
			var limit = 10;
			var arryTenpo = String(Tenpo_ck).split(",");
			if ( arryTenpo.length > limit ){
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'店舗は' + limit + '店舗以内で指定してください。','warning');
				return false;
			}
			return true;
		},

		/**
		 * セルの縦マージ
		 * @param id
		 * @param data
		 * @param column
		 */
		mergeVerticallCells : function(id, data, column){
			var $id = $(id);
			// 開始位置
			var data = $id.datagrid('getRows');
			var startIndex = $id.datagrid('getRowIndex', data[0]);
			// セルのマージ
			var befData = "";
			var cnt = 0;
			for(i = 0, len = data.length; i <= len; i++){
				if(i!=0){ befData = $.trim(data[i-1][column]);}
				if(i==len || (i!=0 && befData != $.trim(data[i][column]))){
					$id.datagrid('mergeCells',{index:startIndex+i-cnt,field:column,rowspan:cnt});
					cnt = 0;
				}
				cnt++;
			}
		},
		/**
		 * セルの縦マージ（縦軸１：商品、縦軸2：なし、横軸：表示項目）
		 * @param id
		 * @param data
		 * @param column
		 */
		mergeVerticallCells2Item : function(id, data, column, cehckColumn){
			var $id = $(id);
			try{
				var d=data.rows[0][cehckColumn];
			}catch(e){
				return;
			}
			// 開始位置
			var data = $id.datagrid('getRows');
			var startIndex = $id.datagrid('getRowIndex', data[0]);
			// セルのマージ
			var befData = "";
			var nowData = "";
			var cnt = 1;
			for(var i = 0, len = data.length; i <= len; i++){
				if(i!=0){// 前行値
					befData = $.trim(data[i-1][column]);
				}
				if (i<len){	// 現行値＆商品名列の「計」確認
					nowData = $.trim(data[i][column]);
				}
				// ページの最終行　又は　前行値の異なる
				if( (nowData!=='' && befData != nowData) || i===len){
					$id.datagrid('mergeCells',{index:startIndex+i-cnt,field:column,rowspan:cnt});
					cnt = 0;
				}
				cnt++;
			}
		},
		/** 店舗グループの選択値によるラベル切替
		 * val 店舗グループの選択値
		 */
		getLabelTenpoG: function(val){
			switch (val) {
			case $.id.valueTenpoG_Han:	// 販売統括部選択
				return $.id.labelHonbu;
			case $.id.valueTenpoG_Sei:	// 青果市場選択
				return $.id.labelIchiba;
			default:	// その他選択時
				return "　　　　";
			}
		},
		/**
		 * row の最大カラム番号取得
		 */
		getMaxColumnNo: function(row){
			var columnNo = -1;
			var dummy="";
			for (var i=1;i<1048;i++){
				try {
					if (typeof row['F'+i] === "undefined") {
						break;
					}
					columnNo = i;
				} catch(e) {
					break;
				}
			}
			return columnNo;
		},
		/**
		 * 列最大チェック
		 * @param ids 項目のID(※上段→下段となるように指定すること) もしくは 数値
		 * @param msg
		 * @returns {Boolean}
		 */
		checkColumnSize : function(that, ids){
			var limit = 1000;
			var size = 1;
			// 可変列算出
			for (var i = 0; i < ids.length; ++i ) {
				var num = 1;
				if(isFinite(ids[i])){									// 列数直接指定
					num = ids[i]*1;
				}else if($('#'+ids[i]).combogrid('options').multiple){	// 複数選択可
					num = $('#'+ids[i]).combogrid('getValues').length;
					// 最上段は総合計考慮
					if(i==0) num = num + 1;
				}else if($('#'+ids[i]).combogrid('getValue')==""){		// 複数選択不可で「全て」選択
					num = $('#'+ids[i]).combogrid('grid').datagrid('getRows').length;
					// 最上段は総合計考慮
					if(i==0) num = num - 1;
				}else if(i==0){											// 複数選択不可で「全て」以外選択、かつ最上段の場合
					num = 2;
				}
				size = size * num;
			}
			// レポートごとの特殊計算
			if(that.name=="livins_rep09"){	// 加盟店別商品別在庫表
				var row = $('#'+ids[1]).combogrid('getValues');
				if($.inArray("1",row)!= -1) size = size -1;
				if($.inArray("2",row)!= -1) size = size -1;
				if($.inArray("3",row)!= -1) size = size -1;
				if($.inArray("4",row)!= -1) size = size -1;
			}
			// 固定列情報取得
			var fColumns = $($.id.gridholder).datagrid('options').frozenColumns;
			if(fColumns){
				size = size + fColumns[0].length;
			}
			if(size > limit){
				// Load処理回避
				$.tryChangeURL(null);
				$($.id.gridholder).datagrid({data: []});
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'列が最大表示(' + limit + '列)を超えています。\n検索条件を指定していただき、表示件数の絞込みを行ってください。','warning');
				return false;
			}
			return true;
		},

		/**
		 * 行最大チェック
		 * @param total
		 * @returns {Boolean}
		 */
		checkRowSize : function(total){
			var limit = 60000;
			if(total > limit){
				// Load処理回避
				$.tryChangeURL(null);
				$($.id.gridholder).datagrid({data: []});
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'検索結果が最大表示(' + limit + '件)を超えています。\n検索条件を指定していただき、表示件数の絞込みを行ってください。','warning');
				return false;
			}
			return true;
		},

		/**
		 * combogrid 入力値チェック
		 * @param array
		 * @param id
		 * @param msg
		 * @returns {Boolean}
		 */
		checkCombogrid : function(array, id, msg){
			var row = $('#'+id).combogrid('grid').datagrid('getSelected');
			var val = $.getJSONObject(array, id).value;
			var txt = $.getJSONObject(array, id).text;

			if (val == '' && txt == ''){
				return true;

			} else if (row != null && row.VALUE == val && row.TEXT == txt){
				return true;

			} else {
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,msg + 'を正しく指定してください。','warning');
				return false;
			}
		},

		/**
		 * 文字バイト数チェック
		 * @param str
		 * @param byte
		 * @returns {Boolean}
		 */
		checkByte : function(str, byte){
			var num = 0;
			for (var i = 0; i < str.length; i++){
				var c = str.charCodeAt(i);
				if ( (c >= 0x0 && c < 0x81) || (c == 0xf8f0) || (c >= 0xff61 && c < 0xffa0) || (c >= 0xf8f1 && c < 0xf8f4)){
					num += 1;
				} else {
					num += 2;
				}

				if (num > byte){
					return false;
				}
			}

			return true;
		},

		/**
		 * 半角数字、桁数チェック
		 * @param str
		 * @param len
		 * @param fixed
		 * @returns {Boolean}
		 */
		checkNumericLen : function(str, len, fixed){
			if (str.length > len){
				return false;
			} else if (fixed == true && str.length != len){
				return false;
			}
			if (str.match(/[^0-9]/)){
				return false;
			}
			return true;
		},

		/**
		 * 日付チェック
		 * @param str
		 * @returns {Boolean}
		 */
		checkDate : function(str){
			// 正規表現による書式チェック
			if(!str.match(/^\d{8}$/)){
				return false;
			}
			var vYear = str.substr(0, 4) - 0;
			var vMonth = str.substr(4, 2) - 1; // Javascriptは、0-11で表現
			var vDay = str.substr(6, 2) - 0;
			// 月、日の妥当性チェック
			if(vMonth < 0 || vMonth > 11 || vDay < 1 || vDay > 31){
				return false;
			}
			var vDt = new Date(vYear, vMonth, vDay);
			if(isNaN(vDt)){
				return false;
			}
			if(vDt.getFullYear() != vYear || vDt.getMonth() != vMonth || vDt.getDate() != vDay){
				return false;
			}
			return true;
		},
		/**
		 * 選択変換
		 */
		convertComboGrid: function(json, id){
			var g = $('#'+id).combogrid('grid');
			var value = $.getJSONObject(json, id).value;
			var text  = $.getJSONObject(json, id).value;

			// 分類（全選択の場合や未選択を「すべて」）
			if (g.datagrid('getSelections').length === g.datagrid('getRows').length || value.length===0){
				value = ['-1'];
				text  = 'すべて';
				// 情報更新
				$.setJSONObject(json,id,value,text)
			}
		},
		/**
		 * 大分類（大分類が全選択の場合、すべて）変換
		 */
		convertBumonDaibun: function(value){
			// 大分類（大分類が全選択の場合、すべて）
			var g = $('#'+$.id.SelDaiBun).combogrid('grid');
			if (g.datagrid('getSelections').length === g.datagrid('getRows').length){
				value = ['-1'];
			}
			return value;
		},
		/**
		 * 警告（上限等）
		 */
		showWarningMessage: function(data){
			if (data && data.total > 65000){
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'検索結果件数が65,000件を超えました。\n検索条件の絞込みを行ってください。','warning');
			} else if (data && typeof data.message !== 'undefined' && data.message !== ''){
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,data.message,'warning');
			}
		},
		/**
		 * 天気マーク変換
		 */
		getWeathernews: function(value){
			// 未定義の場合
			if(typeof value === "undefined") {
				value = "";
			}
			// 分割（天気名/気温）
			var weather = value.split("/");
			// 天気名なしの場合
			if (weather[0]===""){
				return value;
			}
			// 気温定義
			var kion = "";
			if (weather.length===2){
				kion = "/"+weather[1];
			}
			// 天気名から天気コード変換
			var imageCode="";
			switch (weather[0]) {

			case '晴' :					imageCode = '100';break;
			case '晴時々曇' :			imageCode = '101';break;
			case '晴一時雨' :			imageCode = '102';break;
			case '晴時々雨' :			imageCode = '103';break;
			case '晴一時雪' :			imageCode = '104';break;
			case '晴時々雪' :			imageCode = '105';break;
			case '晴一時雨か雪' :		imageCode = '106';break;
			case '晴時々雨か雪' :		imageCode = '107';break;
			case '晴一時雨か雷雨' :		imageCode = '108';break;
			case '晴後時々曇' :			imageCode = '110';break;
			case '晴後曇' :				imageCode = '111';break;
			case '晴後一時雨' :			imageCode = '112';break;
			case '晴後時々雨' :			imageCode = '113';break;
			case '晴後雨' :				imageCode = '114';break;
			case '晴後一時雪' :			imageCode = '115';break;
			case '晴後時々雪' :			imageCode = '116';break;
			case '晴後雪' :				imageCode = '117';break;
			case '晴後雨か雪' :			imageCode = '118';break;
			case '晴後雨か雷雨' :		imageCode = '119';break;
			case '晴朝夕一時雨' :		imageCode = '120';break;
			case '晴朝のうち一時雨' :	imageCode = '121';break;
			case '晴夕方一時雨' :		imageCode = '122';break;
			case '晴山沿い雷雨' :		imageCode = '123';break;
			case '晴山沿い雪' :			imageCode = '124';break;
			case '晴午後は雷雨' :		imageCode = '125';break;
			case '晴昼頃から雨' :		imageCode = '126';break;
			case '晴夕方から雨' :		imageCode = '127';break;
			case '晴夜は雨' :			imageCode = '128';break;
			case '晴夜半から雨' :		imageCode = '129';break;
			case '朝のうち霧後晴' :		imageCode = '130';break;
			case '晴明け方霧' :			imageCode = '131';break;
			case '晴朝夕曇' :			imageCode = '132';break;
			case '晴時々雨で雷を伴う' :	imageCode = '140';break;
			case '晴一時雪か雨' :		imageCode = '160';break;
			case '晴時々雪か雨' :		imageCode = '170';break;
			case '晴後雪か雨' :			imageCode = '181';break;
			case '曇' :					imageCode = '200';break;
			case '曇時々晴' :			imageCode = '201';break;
			case '曇一時雨' :			imageCode = '202';break;
			case '曇時々雨' :			imageCode = '203';break;
			case '曇一時雪' :			imageCode = '204';break;
			case '曇時々雪' :			imageCode = '205';break;
			case '曇一時雨か雪' :		imageCode = '206';break;
			case '曇時々雨か雪' :		imageCode = '207';break;
			case '曇一時雨か雷雨' :		imageCode = '208';break;
			case '霧' :					imageCode = '209';break;
			case '曇後時々晴' :			imageCode = '210';break;
			case '曇後晴' :				imageCode = '211';break;
			case '曇後一時雨' :			imageCode = '212';break;
			case '曇後時々雨' :			imageCode = '213';break;
			case '曇後雨' :				imageCode = '214';break;
			case '曇後一時雪' :			imageCode = '215';break;
			case '曇後時々雪' :			imageCode = '216';break;
			case '曇後雪' :				imageCode = '217';break;
			case '曇後雨か雪' :			imageCode = '218';break;
			case '曇後雨か雷雨' :		imageCode = '219';break;
			case '曇朝夕一時雨' :		imageCode = '220';break;
			case '曇朝のうち一時雨' :	imageCode = '221';break;
			case '曇夕方一時雨' :		imageCode = '222';break;
			case '曇日中時々晴' :		imageCode = '223';break;
			case '曇昼頃から雨' :		imageCode = '224';break;
			case '曇夕方から雨' :		imageCode = '225';break;
			case '曇夜は雨' :			imageCode = '226';break;
			case '曇夜半から雨' :		imageCode = '227';break;
			case '曇昼頃から雪' :		imageCode = '228';break;
			case '曇夕方から雪' :		imageCode = '229';break;
			case '曇夜は雪' :			imageCode = '230';break;
			case '曇海上海岸は霧か霧雨':imageCode = '231';break;
			case '曇時々雨で雷を伴う' :	imageCode = '240';break;
			case '曇時々雪で雷を伴う' :	imageCode = '250';break;
			case '曇一時雪か雨' :		imageCode = '260';break;
			case '曇時々雪か雨' :		imageCode = '270';break;
			case '曇後雪か雨' :			imageCode = '281';break;
			case '雨' :					imageCode = '300';break;
			case '雨時々晴' :			imageCode = '301';break;
			case '雨時々止む' :			imageCode = '302';break;
			case '雨時々雪' :			imageCode = '303';break;
			case '雨か雪' :				imageCode = '304';break;
			case '大雨' :				imageCode = '306';break;
			case '雨で暴風を伴う' :		imageCode = '308';break;
			case '雨一時雪' :			imageCode = '309';break;
			case '雨後晴' :				imageCode = '311';break;
			case '雨後曇' :				imageCode = '313';break;
			case '雨後時々雪' :			imageCode = '314';break;
			case '雨後雪' :				imageCode = '315';break;
			case '雨か雪後晴' :			imageCode = '316';break;
			case '雨か雪後曇' :			imageCode = '317';break;
			case '朝のうち雨後晴' :		imageCode = '320';break;
			case '朝のうち雨後曇' :		imageCode = '321';break;
			case '雨朝晩一時雪' :		imageCode = '322';break;
			case '雨昼頃から晴' :		imageCode = '323';break;
			case '雨夕方から晴' :		imageCode = '324';break;
			case '雨夜は晴' :			imageCode = '325';break;
			case '雨夕方から雪' :		imageCode = '326';break;
			case '雨夜は雪' :			imageCode = '327';break;
			case '雨一時強く降る' :		imageCode = '328';break;
			case '雨一時みぞれ' :		imageCode = '329';break;
			case '雪か雨' :				imageCode = '340';break;
			case '雨で雷を伴う' :		imageCode = '350';break;
			case '雪か雨後晴' :			imageCode = '361';break;
			case '雪か雨後曇' :			imageCode = '371';break;
			// TODO:不明天気コード
			case '391' :				imageCode = '391';break;
			case '392' :				imageCode = '392';break;
			case '393' :				imageCode = '393';break;
			case '394' :				imageCode = '394';break;
			case '395' :				imageCode = '395';break;
			case '396' :				imageCode = '396';break;
			case '雪' :					imageCode = '400';break;
			case '雪時々晴' :			imageCode = '401';break;
			case '雪時々止む' :			imageCode = '402';break;
			case '雪時々雨' :			imageCode = '403';break;
			case '大雪' :				imageCode = '405';break;
			case '風雪強い' :			imageCode = '406';break;
			case '暴風雪' :				imageCode = '407';break;
			case '雪一時雨' :			imageCode = '409';break;
			case '雪後晴' :				imageCode = '411';break;
			case '雪後曇' :				imageCode = '413';break;
			case '雪後雨' :				imageCode = '414';break;
			case '朝のうち雪後晴' :		imageCode = '420';break;
			case '朝のうち雪後曇' :		imageCode = '421';break;
			case '雪昼頃から雨' :		imageCode = '422';break;
			case '雪夕方から雨' :		imageCode = '423';break;
			case '雪夜半から雨' :		imageCode = '424';break;
			case '雪一時強く降る' :		imageCode = '425';break;
			case '雪後みぞれ' :			imageCode = '426';break;
			case '雪一時みぞれ' :		imageCode = '427';break;
			case '雪で雷を伴う' :		imageCode = '450';break;

			default:
				break;
			}
			if (imageCode===""){
				// 天気コードなしの場合
				return value;
			}else{
				// 天気コードありの場合
				return "<img src='../img/"+imageCode+".png' title='"+weather[0]+"' alt='"+weather[0]+"' style='width:25px; float: center;'>"+"<span>"+kion+"</span>";
			}
		},
		setToolbarHeight: function(){
			// toolbar の高さ調整
			$($.id.toolbar).height($($.id.toolbar).get(0).scrollHeight>$($.id.toolbar).get(0).offsetHeight?$($.id.toolbar).get(0).scrollHeight:$($.id.toolbar).get(0).offsetHeight);
		},

		id : {
			 setHeight				:	8				// オフセット
			,action_default			:	"run"			// action パラメータの初期値
			,action_get				:	"get"			// action パラメータ データ取得用
			,action_init			:	"init"			// action パラメータ 初期化
			,action_items			:	"items"			// action パラメータ 商品取得
			,action_tenpo			:	"tenpo"			// action パラメータ 店舗グループ
			,action_shire			:	"shire"			// action パラメータ 仕入先グループ
			,action_maker			:	"maker"			// action パラメータ メーカーグループ
			,action_shiori			:	"shiori"		// action パラメータ 定義保存
			,action_store			:	"store"			// action パラメータ 保存
			,action_update			:	"update"		// action パラメータ 更新
			,action_delete			:	"delete"		// action パラメータ 削除
			,send_mode				:	"sendMode"		// 転送モード

			,valueKikan_Month		:	"1"				// 期間：月
			,valueKikan_Week		:	"2"				// 期間：週
			,valueKikan_Day			:	"3"				// 期間：日
			,valueKikan_FYear		:	"4"				// 期間：年度

			,valueSubQuery_None			:	"1"			// 抽出：なし
			,valueSubQuery_Moyoshi		:	"2"			// 抽出：催しコード
			,valueSubQuery_MoyoshiKB	:	"3"			// 抽出：催し区分
			,valueSubQuery_ShohinKB		:	"4"			// 抽出：商品区分

			,valueSakutai_weekday	:	"1"				// 昨対：曜日合わせ
			,valueSakutai_day		:	"2"				// 昨対：日合わせ

			,valueWhere_Bunrui		:	"1"				// 条件：分類
			,valueWhere_Category	:	"9"				// 条件：商品カテゴリ

			,valueSyukei			:	"0"				// 集計単位/対象：XX集計
			,valueSyukei_bumon		:	"1"				// 集計単位：部門
			,valueSyukei_line		:	"2"				// 集計単位：ライン
			,valueSyukei_class		:	"3"				// 集計単位：クラス
			,valueSyukei_syohin		:	"4"				// 集計単位：商品

			,valueSyukei_TenpoALL	:	"1"				// 集計単位：店舗合計
			,valueSyukei_TenpoE		:	"2"				// 集計単位：各店

			,valueKbn_Nyuka			:	"1"				// 区分：入荷
			,valueKbn_Syohin		:	"2"				// 区分：商品

			,valueTenpoG_Han		:	"-89"			// 店舗グループ：販売統括部
			,valueTenpoG_Sei		:	"-88"			// 店舗グループ：青果市場


			,valueHyo_Jikei			:	"1"				// 表側、表列項目:時系列
			,valueHyo_BumonG		:	"2"				// 表側、表列項目:部門グループ
			,valueHyo_Bumon			:	"3"				// 表側、表列項目:部門
			,valueHyo_DaiCls		:	"4"				// 表側、表列項目:大分類
			,valueHyo_ChuCls		:	"5"				// 表側、表列項目:中分類
			,valueHyo_SyoCls		:	"6"				// 表側、表列項目:小分類
			,valueHyo_Syohin		:	"7"				// 表側、表列項目:商品
			,valueHyo_HanTou		:	"8"				// 表側、表列項目:販売統括部
			,valueHyo_Hanbai		:	"9"				// 表側、表列項目:販売部
			,valueHyo_Tenpo			:	"10"			// 表側、表列項目:店舗
			,valueHyo_Ichiba		:	"11"			// 表側、表列項目:市場
			,valueHyo_Shiire		:	"12"			// 表側、表列項目:仕入先
			,valueHyo_Maker			:	"13"			// 表側、表列項目:メーカー
			,valueHyo_Views			:	"14"			// 表側、表列項目:表示項目
			,valueHyo_Kamoku		:	"15"			// 表側、表列項目:勘定科目
			,valueHyo_Ranku			:	"16"			// 表側、表列項目:ランキング
			,valueHyo_Page			:	"17"			// 表側、表列項目:構成頁
			,valueHyo_PosNo			:	"18"			// 表側、表列項目:レジ番号
			,valueHyo_Moyoshi		:	"19"			// 表側、表列項目:催し区分
			,valueHyo_Times			:	"20"			// 表側、表列項目:時間帯
			,valueHyo_Tourui		:	"21"			// 表側、表列項目:当月/累計
			,valueHyo_Shozoku		:	"22"			// 表側、表列項目:所属
			,valueHyo_Syoku			:	"23"			// 表側、表列項目:職種
			,valueHyo_Denpyo		:	"29"			// 表側、表列項目:経費明細
			,valueHyo_Honbu			:	"30"			// 表側、表列項目:本部
			,valueHyo_TouBum		:	"31"			// 表側、表列項目:統括部門
			,valueHyo_TanRui		:	"32"			// 表側、表列項目:単日累計
			,valueHyo_HViews		:	"33"			// 表側、表列項目:表示項目（横）
			,valueHyo_Chuukei		:	"34"			// 表側、表列項目:中計
			,valueHyo_TenPage		:	"35"			// 表側、表列項目:店舗・構成頁
			,valueHyo_SyohinKB		:	"36"			// 表側、表列項目:商品区分
			,valueHyo_WeekNo		:	"37"			// 表側、表列項目:曜日

			,valueKbn_Seisen		:	"1010"			// 区分マスタ：生鮮
			,valueKbn_Dry			:	"1011"			// 区分マスタ：ドライ・NF・許認可

			,valueYear_eir_change	:	"2011"			// 営業レポート切換基準年度 2011年以降：売価還元対応

			,valueKeitai_C			:	"1"				// 表示形態：クロス
			,valueKeitai_M			:	"2"				// 表示形態：明細

			,valKizonKbn_New		:	"1"				// 既存店区分：新店
			,valKizonKbn_Kzn		:	"2"				// 既存店区分：既存店

			,array_Output_030		:	["03","05","08","09","12","21","23","24","25"]	// 表示項目：030取引先・メーカー・構成分析 構成比を指定しない項目

			,valueHikakuBase:"" 	// 基準
			,valueHikakuComp:"ON" 	// 比較

			,valueSel_Head			:	"-1" 			// 選択リストヘッド(値)
			,valueSel_HeadTxt		:	"すべて" 		// 選択リストヘッド(文言)
			,valueSel_MultTxt		:	"複数" 			// 選択リスト複数(文言)


			,hidden_reportno		:	"#reportno"		// レポート番号
			,hidden_userid			:	"#userid"		// ユーザーID
			,hidden_userTenpo		:	"#userTenpo"	// ユーザー店舗
			,hidden_userBumon		:	"#userBumon"	// ユーザー部門情報
			,hiddenUser				:	"#hiddenUser"	// ユーザーCD
			,hiddenReport			:	"#hiddenReport"	// レポートCD
			,hiddenSendParam		:	"#hiddenParam"	// パラメータ
			,hiddenInit				:	"#hiddenInit"	// 初期検索条件
			,hiddenChangedIdx		:	"#hiddenChangedIdx"	// 変更があった箇所のIndexを保持
			,hiddenSelectIdx		:	"#hiddenSelectIdx"	// 選択した箇所のIndexを保持

			,hiddenTenpo			:	"#hiddenTenpo"	// 店舗コード

			,btn_search				:	"btn_Search"	// 検索ボタン
			,btn_excel				:	"btn_Excel"		// EXCELボタン

			,btn_reset				:	"btn_reset"		// 条件リセット：戻るボタン
			,btn_view_shiori		:	"btn_view_shiori"	// 定義保存：適用ボタン
			,btn_entry_shiori		:	"btn_entry_shiori"	// 定義保存：保存ボタン
			,btn_delete_shiori		:	"btn_delete_shiori"	// 定義保存：削除ボタン

			,btn_Input				:	"btn_Input"		// 商品入力
			,btn_entry				:	"btn_entry"		// 登録ボタン
			,btn_call				:	"btn_call"		// 呼出ボタン
			,btn_delete				:	"btn_delete"	// 削除ボタン
			,btn_add				:	"btn_add"		// 追加ボタン
			,btn_undo				:	"btn_undo"		// 戻すボタン
			,btn_reload				:	"btn_reload"	// 更新ボタン
			,btn_Input_TenpoG		:	"btn_Input_TenpoG"		// 店舗グループ
			,btn_entry_subGyotai	:	"btn_entry_subGyotai"	// 保存ボタン（サブ業態）
			,btn_entry_tg			:	"btn_entry-tg"		// 登録ボタン（店舗グループ）
			,btn_call_tg			:	"btn_call-tg"		// 呼出ボタン（店舗グループ）
			,btn_delete_tg			:	"btn_delete-tg"		// 削除ボタン（店舗グループ）

			,btn_set				:	"btn_set"			// 設定ボタン
			,btn_anbun				:	"btn_anbun"			// 按分ボタン

			,btn_login				:	"btn_login"		// ログインボタン
			,btn_cancel				:	"btn_cancel"	// キャンセルボタン

			,SelKikan				:	"SelKikan"		// 選択リスト（期間）
			,SelKikanF				:	"SelKikanF"		// 選択リスト（期間FROM）
			,SelKikanT				:	"SelKikanT"		// 選択リスト（期間TO）
			,SelYmdF				:	"SelYmdF"		// 選択リスト（年月日FROM）
			,SelYmdT				:	"SelYmdT"		// 選択リスト（年月日TO）
			,SelYmF					:	"SelYmF"		// 選択リスト（年月FROM）
			,SelYmT					:	"SelYmT"		// 選択リスト（年月TO）
			,SelYearF				:	"SelYearF"		// 選択リスト（年FROM）
			,SelYearT				:	"SelYearT"		// 選択リスト（年TO）
			,SelWeekF				:	"SelWeekF"		// 選択リスト（週FROM）
			,SelWeekT				:	"SelWeekT"		// 選択リスト（週TO）
			,SelFYearF				:	"SelFYearF"		// 選択リスト（年度FROM）
			,SelFYearT				:	"SelFYearT"		// 選択リスト（年度TO）
			,SelKikanF2				:	"SelKikanF2"	// 選択リスト（比較期間FROM）
			,SelKikanT2				:	"SelKikanT2"	// 選択リスト（比較期間TO）
			,SelYmdF2				:	"SelYmdF2"		// 選択リスト（比較年月日FROM）
			,SelYmdT2				:	"SelYmdT2"		// 選択リスト（比較年月日TO）
			,SelYmF2				:	"SelYmF2"		// 選択リスト（比較年月FROM）
			,SelYmT2				:	"SelYmT2"		// 選択リスト（比較年月TO）
			,SelYearF2				:	"SelYearF2"		// 選択リスト（比較年FROM）
			,SelYearT2				:	"SelYearT2"		// 選択リスト（比較年TO）
			,SelWeekF2				:	"SelWeekF2"		// 選択リスト（比較週FROM）
			,SelWeekT2				:	"SelWeekT2"		// 選択リスト（比較週TO）
			,SelFYearF2				:	"SelFYearF2"	// 選択リスト（比較年度FROM）
			,SelFYearT2				:	"SelFYearT2"	// 選択リスト（比較年度TO）
			,SelGyotai				:	"SelGyotai"		// 選択リスト（業態）
			,SelGyotaiSub			:	"SelGyotaiSub"	// 選択リスト（サブ業態）
			,SelTenpoG				:	"SelTenpoG"		// 選択リスト（店舗グループ）
			,SelTenpo				:	"SelTenpo"		// 選択リスト（店舗）
			,SelMTenpo				:	"SelMTenpo"		// 選択リスト（モデル店舗）
			,SelJigyo				:	"SelJigyo"		// 選択リスト（事業部）
			,SelKigyo				:	"SelKigyo"		// 選択リスト（企業）
			,SelHanToubu			:	"SelHanToubu"	// 選択リスト（販売統括部門）
			,SelHanbaibu			:	"SelHanbaibu"	// 選択リスト（販売部門）
			,SelBumonG				:	"SelBumonG"		// 選択リスト（部門グループ）
			,SelBumon				:	"SelBumon"		// 選択リスト（部門）
			,SelDaiBun				:	"SelDaiBun"		// 選択リスト（大分類）
			,SelTyuBun				:	"SelTyuBun"		// 選択リスト（中分類）
			,SelSyoBun				:	"SelSyoBun"		// 選択リスト（小分類）
			,SelSyohin				:	"SelSyohin"		// 選択リスト（商品）
			,SelHonbu				:	"SelHonbu"		// 選択リスト（本部）
			,SelSyozoku				:	"SelSyozoku"	// 選択リスト（所属）
			,SelMoyoshi				:	"SelMoyoshi"	// 選択リスト（催しコード）
			,SelSubQuery			:	"SelSubQuery"	// 選択リスト（抽出）
			,SelMoyoshiKB			:	"SelMoyoshiKB"	// 選択リスト（催し区分）
			,SelSyohinKB			:	"SelSyohinKB"	// 選択リスト（商品区分）
			,SelShiori				:	"SelShiori"		// 選択リスト（定義保存）

			,SelShire				:	"SelShire"		// 選択リスト（仕入先）
			,SelMaker				:	"SelMaker"		// 選択リスト（メーカ）
			,SelTanto				:	"SelTanto"		// 選択リスト（担当者）
			,SelKpage				:	"SelKpage"		// 選択リスト（構成頁）

			,SelLine				:	"SelLine"		// 選択リスト（ライン）
			,SelClass				:	"SelClass"		// 選択リスト（クラス）
			,SelWhere				:	"SelWhere"		// 選択リスト（条件）
			,SelCategory			:	"SelCategory"	// 選択リスト（商品カテゴリ）
			,SelMaker				:	"SelMaker"		// 選択リスト（メーカー）




			,SelHyosoku1			:	"SelHyosoku1"	// 選択リスト（表側1）
			,SelHyosoku2			:	"SelHyosoku2"	// 選択リスト（表側2）
			,SelHyoretsu			:	"SelHyoretsu"	// 選択リスト（表列）
			,SelSyukeiKi			:	"SelSyukeiKi"	// 選択リスト（集計方法-期間）
			,SelSyukeiTen			:	"SelSyukeiTen"	// 選択リスト（集計方法-店舗）
			,SelSyukeiBun			:	"SelSyukeiBun"	// 選択リスト（集計方法-分類）
			,SelKamokuGTen			:	"SelKamokuGTen"	// 選択リスト（科目グループ-店舗）
			,SelKamokuGHon			:	"SelKamokuGHon"	// 選択リスト（科目グループ-本部）
			,SelKamokuDTen			:	"SelKamokuDTen"	// 選択リスト（伝票明細-店舗）
			,SelKamokuDHon			:	"SelKamokuDHon"	// 選択リスト（伝票明細-本部）
			,SelRank				:	"SelRank"		// 選択リスト（ランキング項目）
			,SelHanbaiKbn			:	"SelHanbaiKbn"	// 選択リスト（販売区分）
			,SelHikaku				:	"SelHikaku"		// 選択リスト（比較値）


			,SelOutput				:	"SelOutput"		// 選択リスト（出力項目）
			,SelOrder				:	"SelOrder"		// 選択リスト（表示順）
			,SelKijunRank			:	"SelKijunRank"	// 選択リスト（ランク基準）
			,SelSyukei				:	"SelSyukei"		// 選択リスト（集計単位）
			,SelJiku				:	"SelJiku"		// 選択リスト（分析軸）
			,SelRankR				:	"SelRankR"		// 選択リスト（Rランク）
			,SelRankF				:	"SelRankF"		// 選択リスト（Fランク）
			,SelRankM				:	"SelRankM"		// 選択リスト（Mランク）
			,SelAge					:	"SelAge"		// 選択リスト（年齢層）
			,SelToriCode			:	"SelToriCode"	// 選択リスト（取引コード）
			,SelKijun				:	"SelKijun"		// 選択リスト（基準）

			,SelKbnHyoji			:	"SelKbnHyoji"	// 選択リスト（区分表示）

			,SelSyokusyu			:	"SelSyokusyu"	// 選択リスト（職種）

			,RadSakutai				:	"RadSakutai"	// ラジオボタン（昨対）
			,RadTen					:	"RadTen"		// ラジオボタン（店･集計別）
			,RadGyotai				:	"RadGyotai"		// ラジオボタン（業態･店別）
			,RadMaker				:	"RadMaker"		// ラジオボタン（メーカー/取引先）
			,RadBest				:	"RadBest"		// ラジオボタン（上位、下位）
			,RadKbn					:	"RadKbn"		// ラジオボタン（区分）
			,RadKeitai				:	"RadKeitai"		// ラジオボタン（表示形態）

			,ChkKei					:	"ChkKei"		// チェックボックス（小計・合計表示）
			,ChkKiz					:	"ChkKiz"		// チェックボックス（既存店小計表示）
			,ChkTan					:	"ChkTan"		// チェックボックス（単日データ表示）
			,ChkRui					:	"ChkRui"		// チェックボックス（累計データ表示）
			,ChkShiori				:	"ChkShiori"		// チェックボックス（定義保存の共有）
			,ChkSys					:	"ChkSys"		// チェックボックス（詳細表示）

			,TxtNumber				:	"TxtNumber"		// テキスト（件数）
			,TxtRankAF				:	"TxtRankAF"		// テキスト（AランクFROM）
			,TxtRankAT				:	"TxtRankAT"		// テキスト（AランクTO）
			,TxtRankBF				:	"TxtRankBF"		// テキスト（BランクFROM）
			,TxtRankBT				:	"TxtRankBT"		// テキスト（BランクTO）
			,TxtRankCF				:	"TxtRankCF"		// テキスト（CランクFROM）
			,TxtRankCT				:	"TxtRankCT"		// テキスト（CランクTO）
			,TxtRankAT2				:	"TxtRankAT2"	// テキスト（AランクTO2）
			,TxtRankBF2				:	"TxtRankBF2"	// テキスト（BランクFROM2）
			,TxtRankBT2				:	"TxtRankBT2"	// テキスト（BランクTO2）
			,TxtRankCF2				:	"TxtRankCF2"	// テキスト（CランクFROM2）
			,TxtRankCT2				:	"TxtRankCT2"	// テキスト（CランクTO2）
			,TxtRank				:	"TxtRank"		// テキスト（選択ランク）
			,TxtRank2				:	"TxtRank2"		// テキスト（選択ランク2）
			,TxtRankR1				:	"TxtRankR1"		// テキスト（Rランク1）
			,TxtRankR2				:	"TxtRankR2"		// テキスト（Rランク2）
			,TxtRankR3				:	"TxtRankR3"		// テキスト（Rランク3）
			,TxtRankR4				:	"TxtRankR4"		// テキスト（Rランク4）
			,TxtRankR5				:	"TxtRankR5"		// テキスト（Rランク5）
			,TxtRankF1				:	"TxtRankF1"		// テキスト（Fランク1）
			,TxtRankF2				:	"TxtRankF2"		// テキスト（Fランク2）
			,TxtRankF3				:	"TxtRankF3"		// テキスト（Fランク3）
			,TxtRankF4				:	"TxtRankF4"		// テキスト（Fランク4）
			,TxtRankF5				:	"TxtRankF5"		// テキスト（Fランク5）
			,TxtRankM1				:	"TxtRankM1"		// テキスト（Mランク1）
			,TxtRankM2				:	"TxtRankM2"		// テキスト（Mランク2）
			,TxtRankM3				:	"TxtRankM3"		// テキスト（Mランク3）
			,TxtRankM4				:	"TxtRankM4"		// テキスト（Mランク4）
			,TxtRankM5				:	"TxtRankM5"		// テキスト（Mランク5）

			,TxtRankA1				:	"TxtRankA1"		// テキスト（矢印↑）
			,TxtRankA2				:	"TxtRankA2"		// テキスト（矢印／）
			,TxtRankA3				:	"TxtRankA3"		// テキスト（矢印→）
			,TxtRankA4				:	"TxtRankA4"		// テキスト（矢印＼）
			,TxtRankA5				:	"TxtRankA5"		// テキスト（矢印↓）
			,TxtRankA1C				:	"TxtRankA1C"	// テキスト（矢印↑）
			,TxtRankA2C				:	"TxtRankA2C"	// テキスト（矢印／）
			,TxtRankA3C				:	"TxtRankA3C"	// テキスト（矢印→）
			,TxtRankA4C				:	"TxtRankA4C"	// テキスト（矢印＼）
			,TxtRankA5C				:	"TxtRankA5C"	// テキスト（矢印↓）


			,TxtPass				:	"TxtPass"		// テキスト（パスワード）


			,TxtBmnAraRit			:	"TxtBmnAraRit"	// テキスト（部門荒利率）
			,TxtKeisuYos			:	"TxtKeisuYos"	// テキスト（係数予算）
			,TxtKeisuKyk			:	"TxtKeisuKyk"	// テキスト（係数客数）

			,listtable				:	"#list"			// jqGrid 用 table id

			,toolbarform			:	"#ff"			// toolbarform
			,gridform				:	"#gf"			// gridform
			,toolbar				:	"#tb"			// toolbar
			,placeholder			:	"#placeholder"	// placeholder
			,gridholder				:	"#gridholder"	// gridholder
			,compholder				:	"#compholder"	// gridholder
			,buttons				:	"#buttons"		// buttons

			,reference				:	'#reference'	// reference：参照情報エリア

			, header_tenpo			:	1
			, column_class			:	2
			, pageSize				:	"pageSize"
			, panelState			:	"panelState"

			, textCollapse			:	"条件表示"
			, textExpand			:	"条件非表示"
			, checkBoxOnData		:	"&#10004;"

			, TxtHanSei				:	"#TxtHanSei"
			, labelHonbu			:	"販売本部"
			, labelIchiba			:	"青果市場"
		},

		message : {
			 ID_MESSAGE_WARNING_EXCEL_OUTPUT		:	"照会後のみExcelの表示は可能です。"
			,ID_MESSAGE_WARNING_REPORT_OUTPUT		:	"照会後に実行してください。"
			,ID_MESSAGE_WARNING_SELECT_CATEGORY		:	"商品カテゴリを指定してください。"
			,ID_MESSAGE_VALIDATION_SELECT_ITEM		:	"商品を選択してください。"
			,ID_MESSAGE_VALIDATION_SELECT_ITEM_EX	:	"横軸に「商品」を表示する場合、「小分類」まで指定してください。"
			,ID_MESSAGE_VALIDATION_SELECT_COLUMN	:	"表示項目を指定してください。"
			,ID_MESSAGE_VALIDATION_SELECT_JIKEIRETSU:	"縦軸１と横軸の両方に「時系列」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_BUMON		:	"縦軸１と横軸の両方に「部門グループ」「部門」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_CLASS		:	"縦軸１と横軸の両方に「部門グループ」「部門」「大分類」「中分類」「小分類」「商品」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_CLASS_EX	:	"縦軸１と横軸の両方に「統括部門」「部門グループ」「部門」「大分類」「中分類」「小分類」「商品」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_TENPO		:	"縦軸１と横軸の両方に「販売統括部」「販売部」「店舗」「市場」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_VIEW		:	"縦軸１、縦軸２、横軸のいずれか１つに「表示項目」を指定してください。"
			,ID_MESSAGE_VALIDATION_SELECT_KIKAKU	:	"縦軸１と横軸の両方に「催し区分」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_PAGE		:	"縦軸１と横軸の両方に「構成頁」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_SHIIRE	:	"縦軸１と横軸の両方に「仕入先」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_MAKER		:	"縦軸１と横軸の両方に「メーカー」を指定できません。"
			,ID_MESSAGE_VALIDATION_SELECT_TANRUI	:	"縦軸１に「単月累計」を指定した場合、縦軸２「表示項目」、横軸「時系列」または「表示項目」以外を指定してください。"
			,ID_MESSAGE_VALIDATION_SELECT_TANRUI_EX	:	"縦軸１に「単月累計」を指定した場合、期間に「日」を指定してください。"
			,ID_MESSAGE_VALIDATION_SELECT_	:	"縦軸１と横軸の両方に「」を指定できません。"
			,ID_MESSAGE_TITLE_INFO:"メッセージ"
			,ID_MESSAGE_TITLE_WARN:"メッセージ"
			,ID_MESSAGE_TITLE_ERR:"メッセージ"
			,ID_MESSAGE_TITLE_CONF:"メッセージ"

		},

		report: []

	};

	var plugin = this;

	plugin.settings = {};

	var init = function() {
		// push Array report Option
		defaults.report.push(options);
		// extend
		$.extend(defaults);
	};

	plugin.PublicMethod = function() {
		// code goes here
	};

	plugin.PrivateMethod = function() {
		// code goes here
	};

	init();

};

})(jQuery);