/**
 * jquery report option
 */
;(function($) {

	$.extend({
		reportOption: {
		name:		'Out_Report003',			// （必須）レポートオプションの確認
		jsonTemp:	[],							// （必須）検索条件情報_入力チェック前
		jsonString:	[],							// （必須）検索条件情報
		jsonHidden: [],							// （必須）親画面からの引き継ぎ情報
		jsonInit: [],							// （必須）検索条件初期情報
		caption: function(){					// （必須）タイトル
			return $('#reportname').val();
		},
		sortName: '',	// ソート項目名
		sortOrder: '',	// ソート順
		timeData : (new Date()).getTime(),
		dedefaultObjNum:	4,	// 初期化オブジェクト数
		initObjNum:	-1,
		initedObject: [],
		maxMergeCell: 0,
		onChangeFlag : false,
		onChangeFlag2 : false,
		onSelectFlag : false,
		columnName:'',	// OnClickRowの列名
		queried : false,
		onChangeReport: false,
		initializes : true,
		initialize: function (reportno){	// （必須）初期化
			var that = this;
			// ツールバー初期化
			$($.id.toolbar).panel({
				title: this.caption(),
				iconCls:'icon-cube1',
				border: false,
				onCollapse:function(){
					that.setResize();
				},
				onExpand:function(){
					that.setResize();
				},
				collapsible:false
			});
			$($.id.buttons).show();

			// 引き継ぎ情報
			this.jsonHidden = $.getTargetValue();
			// 初期検索条件
			this.jsonInit = $.getInitValue();
			// データ表示エリア初期化
			that.setGrid($.id.gridholder, reportno);

			// 初期化するオブジェクト数設定
			this.initObjNum = this.dedefaultObjNum;

			// 詳細
			$.setCheckboxInit(this.jsonHidden, $.id.ChkSys, that);

			// 部門
			this.setBumon(reportno, $.id.SelBumon);

			// 店舗
			this.setTenpo(reportno, $.id.SelTenpo);

			// 期間
			this.setKikanYM(reportno, $.id.SelYmF);

			// タブ移動サンプル
			$.changeReportByTabs(that);

			// 按分 クリックイベント
			$('#'+$.id.btn_anbun).on("click", that.pushAnbun);

			that.setResize();

			// 初期化終了
			this.initializes =! this.initializes;

			// ログ出力
			$.log(that.timeData, 'initialize:');
		},
		initCondition: function (){	// 条件初期値セット
			var that = this;
			// 初期化項目
		},
		clear:function(){
			// 隠し情報初期化
			$($.id.hiddenChangedIdx).val("");						// 変更行Index
			// グリッド初期化
			this.success(this.name, false);
		},
		validation: function (){	// （必須）批准
			var that = this;
			// EasyUI のフォームメソッド 'validate' 実施
			var rt = $($.id.toolbarform).form('validate');
			if(rt){
				rt = $.confirmUnregist('未登録ですが実行してもよろしいでしょうか？');
			}
			var szChkSys		= $.getJSONObject(that.jsonTemp, $.id.ChkSys).value;		// 詳細表示
			if(rt && szChkSys==='1'){
				var szTxtTenpo		= $.getJSONObject(that.jsonTemp, $.id.SelTenpo).text;		// 店舗
				var szTxtBumon		= $.getJSONObject(that.jsonTemp, $.id.SelBumon).text;		// 部門
				if(isNaN(szTxtTenpo.substr(0, 3))&&isNaN(szTxtBumon.substr(0, 2))){
					rt = false;
					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"詳細表示時は、店舗、部門、いずれかを1件に絞り込んでください。",'warning');
					return rt;
				}
			}
			// 入力エラーなしの場合に検索条件を格納
			if (rt == true) that.jsonString = that.jsonTemp.slice(0);
			// 入力チェック用の配列をクリア
			that.jsonTemp = [];
			return rt;
		},
		success: function(reportno, sortable){	// （必須）正処理
			if (sortable) sortable=1; else sortable=0;
			var that = this;
			// 検索実行
			var szSelKikanF		= $.getJSONObject(this.jsonString, $.id.SelKikanF).value;	// 期間FROM
			var szSelTenpo		= $.getJSONObject(this.jsonString, $.id.SelTenpo).value;	// 店舗
			var szSelBumon		= $.getJSONObject(this.jsonString, $.id.SelBumon).value;	// 部門
			var szTxtBumon		= $.getJSONObject(this.jsonString, $.id.SelBumon).text;		// 部門
			var szChkSys		= $.getJSONObject(this.jsonString, $.id.ChkSys).value;		// 詳細表示

			// 処理時間計測用
			that.timeData = (new Date()).getTime();
			$($.id.gridholder).datagrid('loading');

			// grid.options 取得
			var options = $($.id.gridholder).datagrid('options');
			that.sortName	= options.sortName;
			that.sortOrder	= options.sortOrder;

			$.post(
				$.reg.jqgrid ,
				{
					report:			that.name,		// レポート名
					KIKAN_F:		szSelKikanF,	// 期間FROM
					TENPO:			JSON.stringify(szSelTenpo),		// 店舗
					BUMON:			JSON.stringify(szSelBumon),		// 部門
					BUMON_TXT:		szTxtBumon,						// 部門
					SYS:			szChkSys,						// 詳細
					t:				(new Date()).getTime(),
					sortable:		sortable,
					sortName:		that.sortName,
					sortOrder:		that.sortOrder,
					rows:			10000	// 表示可能レコード数(入力系なので、ここで全件取得しておく)
				},
				function(json){
					// 検索処理エラー判定
					if($.searchError(json)) return false;

					// ログ出力
					$.log(that.timeData, 'query:');

					// Load処理回避
					$.tryChangeURL(null);

					if (sortable===0){
						var options = $($.id.gridholder).datagrid('options');
						// 初期検索時に並び替え情報のリセット
						options.sortName = null;
						options.sortOrder = null;

						var titles = JSON.parse(json).titles;
						/** Colomns設定(不要の場合は除去) ※DataGrid用 */
						// 列表示切替
						if(titles != undefined && titles.length > 0){

						}
					}

					that.queried = true;
					// Load処理回避
					$.tryChangeURL($.reg.jqeasy);

					// グリッド再描画（easyui 1.4.2 対応）
					$($.id.gridholder).datagrid('load', {} );

					// 登録ボタン状態変化
					$.setButtonState('#'+$.id.btn_entry, true, $.id.btn_entry);
					$.setButtonState('#'+$.id.btn_anbun, true, $.id.btn_anbun);
					// 検索ボタン無効化
					$.setButtonState('#'+$.id.btn_search, false, 'success');

					// ログ出力
					$.log(that.timeData, 'loaded:');
				}
			);
		},
		/**
		 * 按分(DB更新)ボタンイベント
		 * @param {Object} e
		 */
		pushAnbun:function(e){
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
				var checkedRows = $($.id.gridholder).datagrid('getChecked');
				rtn = (changedIndex.length > 0 && changedIndex[0] !== undefined && changedIndex[0] !== "") && checkedRows.length > 0;
				if(!rtn){
					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"按分対象データはありません。",'warning');
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
				$.messager.confirm($.message.ID_MESSAGE_TITLE_CONF,'再按分を実行します。よろしいでしょうか？', func);
			}
		},
		updValidation: function(id){	// （必須）批准
			var that = this;
			// EasyUI のフォームメソッド 'validate' 実施
			var rt = $($.id.gridform).form('validate');
			if(!rt){
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'入力内容を確認してください。','warning');
			}
			return rt;
		},
		updSuccess: function(id){	// validation OK時 の update処理
			var that = this;

			// Grid内全情報取得
			var rows = $($.id.gridholder).datagrid('getRows');
			var panel =$($.id.gridholder).datagrid("getPanel");
			// 変更行情報取得
			var changedIndex = $($.id.hiddenChangedIdx).val().split(",");
			// 対象情報抜粋
			var targetRows = [];
			for (var i=0; i<rows.length; i++){
				// 変更、もしくはチェックされている
				if($.inArray(i+'', changedIndex) !== -1 || panel.find('tr[datagrid-row-index="'+i+'"]').hasClass("datagrid-row-checked")){
					var rowData = {
							IDX: i+1,		// エラーメッセージ用に行番号を追加
							F1 : rows[i]["F6"],
							F2 : rows[i]["F7"],
							F3 : $("#F3_"+i).text().length > 0 ? '1':''
						};
					targetRows.push(rowData);
				}
			}

			var szSelKikanF		= $.getJSONObject(this.jsonString, $.id.SelKikanF).value;	// 期間FROM
			var szSelTenpo		= $.getJSONObject(this.jsonString, $.id.SelTenpo).value;	// 店舗
			var szSelBumon		= $.getJSONObject(this.jsonString, $.id.SelBumon).value;	// 部門
			var szTxtBumon		= $.getJSONObject(this.jsonString, $.id.SelBumon).text;		// 部門
			var szChkSys		= $.getJSONObject(this.jsonString, $.id.ChkSys).value;		// 詳細表示

			// 処理時間計測用
			that.timeData = (new Date()).getTime();
			$($.id.gridholder).datagrid('loading');

			$.post(
				$.reg.jqgrid ,
				{
					report:			that.name,		// レポート名
					action:			$.id.action_update,	// 実行処理情報
					obj:			id,								// 実行オブジェクト
					KIKAN_F:		szSelKikanF,					// 期間FROM
					TENPO:			JSON.stringify(szSelTenpo),		// 店舗
					BUMON:			JSON.stringify(szSelBumon),		// 部門
					BUMON_TXT:		szTxtBumon,						// 部門
					SYS:			szChkSys,						// 詳細
					IDX:			$($.id.hiddenChangedIdx).val(),	// 更新対象Index
					DATA:			JSON.stringify(targetRows),		// 更新対象情報
					t:				(new Date()).getTime()
				},
				function(data){
					// 検索処理エラー判定
					if($.updError(id, data)) return false;

					$.messager.alert($.message.ID_MESSAGE_TITLE_INFO,JSON.parse(data).opts.S_MSG,'info');
					// 初期化
					that.clear();

					// ログ出力
					$.log(that.timeData, 'loaded:');
				}
			);
		},
		getEasyUI: function(){	// （必須）情報の取得
			// 初期化
			this.jsonTemp = [];

			// レポート名
			this.jsonTemp.push({
				id:		"reportname",
				value:	this.caption(),
				text:	this.caption()
			});
			var kikanF_val =	'';
			var kikanF_txt =	'';
			kikanF_val =	$('#'+$.id.SelYmF).combogrid('getValue');
			kikanF_txt =	$('#'+$.id.SelYmF).combogrid('getText');

			// 期間FROM
			this.jsonTemp.push({
				id:		$.id.SelKikanF,
				value:	kikanF_val,
				text:	kikanF_txt
			});
			// 年月FROM
			this.jsonTemp.push({
				id:		$.id.SelYmF,
				value:	$('#'+$.id.SelYmF).combogrid('getValue'),
				text:	$('#'+$.id.SelYmF).combogrid('getText')
			});
			// 店舗
			this.jsonTemp.push({
				id:		$.id.SelTenpo,
				value:	$('#'+$.id.SelTenpo).combogrid('getValues'),
				text:	$('#'+$.id.SelTenpo).combogrid('getText')
			});
			// 部門
			this.jsonTemp.push({
				id:		$.id.SelBumon,
				value:	$('#'+$.id.SelBumon).combogrid('getValues'),
				text:	$('#'+$.id.SelBumon).combogrid('getText')
			});
			// 詳細表示
			this.jsonTemp.push({
				id:		$.id.ChkSys,
				value:	$('#'+$.id.ChkSys).is(':checked') ? '1' : '',
				text:	$('#'+$.id.ChkSys).is(':checked') ? '表示' : ''
			});
		},
		setKikanYM: function(reportno, id){		// 期間（年月）
			var that = this;
			var idx = -1;
			$('#'+id).combogrid({
				panelWidth:260,
				url:$.reg.easy,
				required: true,
				editable: false,
				autoRowHeight:false,
				idField:'VALUE',
				textField:'TEXT',
				columns:[[
					{field:'TEXT',	title:'',	width:260}
				]],
				fitColumns: true,
				showHeader: false,
				onShowPanel:function(){
					$.setScrollGrid(this);
				},
				onBeforeLoad:function(param){
					// 情報設定
					var json = [{
						DUMMY: 'DUMMY'
					}];

					param.page		=	reportno;
					param.obj		=	id;
					param.sel		=	(new Date()).getTime();
					param.target	=	id;
					param.action	=	$.id.action_init;
					param.json		=	JSON.stringify(json);
				},
				onLoadSuccess:function(data){
					// 初期化
					var num = 0;
					if ($.inArray(id, that.initedObject) < 0){
						that.initedObject.push(id);
						var val = $.getJSONValue(that.jsonHidden, id);
						for (var i=0; i<data.rows.length; i++){
							if (data.rows[i].VALUE == val){
								num = i;
								break;
							}
						}
					}
					if (data.rows.length > 0){
						$('#'+id).combogrid('grid').datagrid('selectRow', num);
					}
					idx = 1;
					// ログ出力
					$.log(that.timeData, id+' init:');
					that.onChangeFlag = false;
					// 店舗
					that.tryLoadMethods('#'+$.id.SelTenpo);
				},
				onChange:function(newValue, oldValue){
					if(idx > 0){
						that.onChangeFlag = false;
						// 店舗
						that.tryLoadMethods('#'+$.id.SelTenpo);
					}
				}
			});
		},
		setTenpo: function(reportno, id){		// 店舗
			var that = this;
			var idx = -1;
			$('#'+id).combogrid({
				panelWidth:250,
				url:$.reg.easy,
				required: true,
				editable: false,
				autoRowHeight:false,
				idField:'VALUE',
				textField:'TEXT',
				multiple :true,
				columns:[[
					{field:'ck',	checkbox:true},
					{field:'TEXT',	title:'',	width:250}
				]],
				fitColumns: true,
				onShowPanel:function(){
					$.setScrollGrid(this);
				},
				onBeforeLoad:function(param){
					idx = -1;
					// 初期化しない
					if (that.initializes) return false;
					// 情報設定
					var json = [{
						REQUIRED: 'REQUIRED',
						NENTUKI: $('#'+$.id.SelYmF).combogrid('getValue')
					}];

					param.page		=	reportno;
					param.obj		=	id;
					param.sel		=	(new Date()).getTime();
					param.target	=	id;
					param.action	=	$.id.action_init;
					param.json		=	JSON.stringify(json);
				},
				onLoadSuccess:function(data){
					var val = null;
					if ($.inArray(id, that.initedObject) < 0){
						that.initedObject.push(id);
					}
					var json = $.getJSONObject(that.jsonHidden, id);
					if(json && json.value!=""){
						val = new Array();
						for (var i=0; i<data.rows.length; i++){
							if ($.inArray(data.rows[i].VALUE, json.value)!=-1){
								val.push(data.rows[i].VALUE);
							}
						}
						if (val.length===data.rows.length){
							val = null;
						}
						if ($.isArray(val) && val.length===0){	// 旧コード対応
							val = null;
						}
					}
					if (val){
						$('#'+id).combogrid('setValues',val);
					}else{
						$('#'+id).combogrid('grid').datagrid('checkAll');
					}
					idx = 1;
					// ログ出力
					$.log(that.timeData, id+' init:');
					that.onChangeFlag = false;
					$.ajaxSettings.async = false;
					// 部門
					that.tryLoadMethods('#'+$.id.SelBumon);
				},
				onChange:function(newValue, oldValue){
					if(newValue && newValue.length > 0){
						$.setJSONObject(that.jsonHidden, id, newValue, newValue);
					}
					if(idx > 0){
						// 上位変更時、下位更新は常に同期
						$.ajaxSettings.async = false;
						that.onChangeFlag = false;
						// 部門
						that.tryLoadMethods('#'+$.id.SelBumon);
					}
				}
			});
		},
		setBumon: function(reportno, id){		// 部門
			var that = this;
			var idx = -1;
			$('#'+id).combogrid({
				panelWidth:250,
				url:$.reg.easy,
				required: true,
				editable: false,
				autoRowHeight:false,
				idField:'VALUE',
				textField:'TEXT',
				multiple :true,
				columns:[[
					{field:'ck',	checkbox:true},
					{field:'TEXT',	title:'',	width:250},
					{field:'VALUE2',	title:'',	hidden:true}
				]],
				fitColumns: true,
				onShowPanel:function(){
					$.setScrollGrid(this);
				},
				onBeforeLoad:function(param){
					idx = -1;
					// 初期化しない
					if (that.initializes) return false;

					// 必要な情報がそろっていない場合検索しない
					if ($.inArray($.id.SelYmF, that.initedObject) == -1
							||$.inArray($.id.SelTenpo, that.initedObject) == -1){
						return false;
					}

					// 情報設定
					var json = [{
						REQUIRED: 'REQUIRED',
						TENPO: $('#'+$.id.SelTenpo).combogrid('getValues'),
						NENTUKI: $('#'+$.id.SelYmF).combogrid('getValue')
					}];

					param.page		=	reportno;
					param.obj		=	id;
					param.sel		=	(new Date()).getTime();
					param.target	=	id;
					param.action	=	$.id.action_init;
					param.json		=	JSON.stringify(json);
				},
				onLoadSuccess:function(data){
					// 選択値設定
					var val = null;
					if ($.inArray(id, that.initedObject) < 0){
						that.initedObject.push(id);
					}
					var json = $.getJSONObject(that.jsonHidden, id);
					if(json && json.value!=""){
						val = new Array();
						for (var i=0; i<data.rows.length; i++){
							if ($.inArray(data.rows[i].VALUE, json.value)!=-1){
								val.push(data.rows[i].VALUE);
							}
						}
						if (val.length===data.rows.length){
							val = null;
						}
						if ($.isArray(val) && val.length===0){	// 旧コード対応
							val = null;
						}
					}
					if (val){
						$('#'+id).combogrid('setValues',val);
					}else{
						$('#'+id).combogrid('grid').datagrid('checkAll');
					}
					idx = 1;
					// ログ出力
					$.log(that.timeData, id+' init:');
					that.onChangeFlag = true;
					$.ajaxSettings.async = true;
					// 検索ボタン有効化
					$.setButtonState('#'+$.id.btn_search, true, id);
					// 初期表示検索処理
					$.initialSearch(that);
				},
				onChange:function(newValue, oldValue){
					if(newValue && newValue.length > 0){
						$.setJSONObject(that.jsonHidden, id, newValue, newValue);
					}
					if(idx > 0 && that.onChangeFlag){
						// 検索ボタン有効化
						$.setButtonState('#'+$.id.btn_search, true, id);
					}
				}
			});
		},
		setObjectState: function(){	// 軸の選択内容による制御
			var that = this;
		},
		setGrid: function (id, reportNumber){	// グリッドの構築
			var that = this;
			var init = true;
			$(id).datagrid({
				nowrap: true,
				border: true,
				striped: false,
				collapsible:false,
				remoteSort: false,
				frozenColumns:[[
				]],
				columns:[[
					{field:'F1',	title:'店舗',	width:200,	align:'left',	halign:'center'},
					{field:'F2',	title:'部門',	width:150,	align:'left',	halign:'center'},
					{field:'ck',	title:'選択',	checkbox:true},
					{field:'F3',	title:'再按分指示',	width:90,	align:'left',	halign:'center',
						formatter:function(value,row,index){
							return "<span id='F3_"+index+ "'>"+value+"</span>";
						}
					},
					{field:'F4',	title:'再按分結果',	width:120,	align:'left',	halign:'center'},
					{field:'F5',	title:'更新日時',	width:150,	align:'left',	halign:'center'}
				]],
				fitColumns:false,	// 指定カラム幅を適用する場合、false 指定。
				rowStyler:function(index, row){
					return 'background-color:#FFFFFF;';
				},
				onSortColumn:function(sort, order){
					if (that.jsonString.length===0) return false;
				},
				onBeforeLoad:function(param){
					param.report = that.name;
				},
				onLoadSuccess:function(data){
					if(init){
						init = false;
						that.setResize();
						return;	// 中断
					}

					// セレクト処理
					var column = "F3";
					for(i = 0, len = data.rows.length; i < len; i++){
						if($.trim(data.rows[i][column])!==""){
							$(id).datagrid("selectRow", i);
						}
					}

					// 検索後、初回のみ処理
					if (that.queried){
						that.queried = false;	// 検索後、初回のみ処理
						// 状態保存
						$.saveState(reportNumber, that.getJSONString(), id);
						// 警告
						$.showWarningMessage(data);
					}
					// セルの縦マージ
					$.mergeVerticallCells($.id.gridholder, data, 'F1');
				},
				onClickCell:function(rowIndex, field, value){
					// 列名保持
					that.columnName = field;
					if(field==="F1" && that.onSelectFlag===false){
						that.onSelectFlag = true;
						var mStartRow = $(id).datagrid("getPanel").find('tr[datagrid-row-index="'+rowIndex+'"]');
						var rowspan = mStartRow.find("td[field='F1']:first").attr("rowspan");
						var checked = mStartRow.hasClass("datagrid-row-checked");
						// マージセルの場合先頭行でClick処理で選択が実行されるので、次行以下選択
						for(i = rowIndex + 1, len = rowIndex + rowspan*1; i < len; i++){
							// var tt = $(panel).find('tr[datagrid-row-index="'+i+'"]').find("td[field='ck']").find("input");
							if(checked){
								$(id).datagrid("unselectRow", i);
							}else{
								$(id).datagrid("selectRow", i);
							}
						}
						that.onSelectFlag = false;
					}
				},
				onCheckAll:function(rows){
					$("span[id^='F3_']").each(function(){
						var index = $(this).attr("id").split("_")[1];
						$(this).text("再按分");
						$.setChangeIdx(index);
					});
				},
				onUncheckAll:function(rows){
					$("span[id^='F3_']").each(function(){
						var index = $(this).attr("id").split("_")[1];
						$(this).text("");
						$.setChangeIdx(index);
					});
				},
				onCheck:function(rowIndex, rowData){
					if(!that.queried){
						$("#F3_"+rowIndex).text("再按分");
						$.setChangeIdx(rowIndex);
					}
				},
				onUncheck:function(rowIndex, rowData){
					if(!that.queried){
						$("#F3_"+rowIndex).text("");
						$.setChangeIdx(rowIndex);
					}
				},
				autoRowHeight:false,
				singleSelect:false
			});
			if (	(!jQuery.support.opacity)
				&&	(!jQuery.support.style)
				&&	(typeof document.documentElement.style.maxHeight == "undefined")
				) {
				// ページリストに select を利用している。IE6  のバグで z-index が適用されない。
				// modalダイアログを利用する場合は、表示なしにする必要あり。
				$.fn.pagination.defaults.showPageList = false;
			}
		},
		getRecord: function(){		// （必須）レコード件数を戻す
			var data = $($.id.gridholder).datagrid('getData');
			if (data == null) {
				return 0;
			} else {
				return data.total;
			}
		},
		setResize: function(){		// （必須）リサイズ
			var changeHeight = $(window).height();
			if (0 < changeHeight) {

				// window 幅取得
				var changeWidth  = $(window).width();

				// toolbar の調整
				$($.id.toolbar).panel('resize',{width:changeWidth});

				// toolbar の高さ調整
				$.setToolbarHeight();

				// DataGridの高さ
				var gridholderHeight = 0;
				var placeholderHeight = 0;

				if ($($.id.gridholder).datagrid().options != 'undefined') {
					// tb
					placeholderHeight = $($.id.toolbar).panel('panel').height() + $($.id.buttons).innerHeight();

					// datagrid の格納された panel の高さ
					gridholderHeight = $(window).height() - placeholderHeight;
				}

				$($.id.gridholder).datagrid('resize', {
					width:	changeWidth,
					height:	gridholderHeight
				});
			}
		},
		getJSONString : function(){		// （必須）JSON形式の文字列
			return this.jsonString;
		},
		tryLoadMethods: function(id){	// （オプション）combo.onChange Event
			var that = this;
			// セッションタイムアウト確認
			if ($.checkIsTimeout(that)) return false;
			try {
				$(id).combogrid('clear');
				var grid = $(id).combogrid('grid');
				grid.datagrid('load');
			} catch (e) {
				// combgrid 未更新時のERROR回避
			}
		},
		changeReport:function(mode, opts, rowData){				// 画面遷移
			var that = this;

			// 初期化終了
			that.initializes = false;
			that.onChangeReport = true;

		},
		excel: function(reportno){	// (必須)Excel出力
			// グリッドの情報取得
			var options = $($.id.gridholder).datagrid('options');

			// タイトル部
			var title = [];
			title = $.outputExcelTitle(title, options.frozenColumns);
			title = $.outputExcelTitle(title, options.columns);

			// タイトル数確認
			if ($.checkExcelTitle(title))	return;

			var data = {
				'header': JSON.stringify(title)
			};

			var kbn = options.frozenColumns[0].length;

			// 転送
			$.ajax({
				url: $.reg.excel,
				type: 'POST',
				data: data,
				async: false,
				success: function(){
					// Excel出力
					$.outputExcel(reportno, kbn);
				},
				error: function(){
					// Excel出力エラー
					$.outputExcelError();
				}
			});
		}
	} });
})(jQuery);