/**
 * jquery report option
 */
;(function($) {

	$.extend({
		reportOption: {
		name:		'Out_Report006',			// （必須）レポートオプションの確認
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
		dedefaultObjNum:	3,	// 初期化オブジェクト数
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

			// 部門
			this.setBumon(reportno, $.id.SelBumon);

			// 店舗
			this.setTenpo(reportno, $.id.SelTenpo);

			// 期間
			this.setKikanYM(reportno, $.id.SelYmF);

			// タブ移動サンプル
			$.changeReportByTabs(that);


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
		validation: function (){	// （必須）批准
			var that = this;
			// EasyUI のフォームメソッド 'validate' 実施
			var rt = $($.id.toolbarform).form('validate');
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
					TENPO:			szSelTenpo,						// 店舗
					BUMON:			JSON.stringify(szSelBumon),		// 部門
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
							// 可変列作成
							var columnBottom=[];	// 項目名称
							var startField=1;		// 開始位置
							var filed=2;			// 可変列開始位置

							// datagrid のタイトル再設定
							var columns = [];
							// 列情報を取得
							for (var i=startField; i<titles.length; i++){
								var tit = titles[i];
								if(tit==="今年の要因"){
									columnBottom.push({field:'F'+filed, title:tit, width:170, align:'left', halign:'left'});
								}else if(tit==="予測客数"){
									columnBottom.push({field:'F'+filed, title:tit, width:60, align:'right', halign:'left',
										formatter:function(value,rowData,rowIndex){return $.getFormat(value,'#,##0');},
										styler: function(value,row,index){if (value < 0){ return 'color:red;';}}
									});
								}else{
									columnBottom.push({field:'F'+filed, title:tit, width:90, align:'right', halign:'left',
										formatter:function(value,rowData,rowIndex){return $.getFormat(value,'#,##0');},
										styler: function(value,row,index){if (value < 0){ return 'color:red;';}}
									});
								}
								filed++;
							}
							columns.push(columnBottom);
							// datagrid のタイトル再設定
							$($.id.gridholder).datagrid({ columns:columns });
						}
					}

					that.queried = true;
					// Load処理回避
					$.tryChangeURL($.reg.jqeasy);

					// グリッド再描画（easyui 1.4.2 対応）
					$($.id.gridholder).datagrid('load', {} );

					// 検索ボタン無効化
					$.setButtonState('#'+$.id.btn_search, false, 'success');

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
				value:	$('#'+$.id.SelTenpo).combogrid('getValue'),
				text:	$('#'+$.id.SelTenpo).combogrid('getText')
			});
			// 部門
			this.jsonTemp.push({
				id:		$.id.SelBumon,
				value:	$('#'+$.id.SelBumon).combogrid('getValues'),
				text:	$('#'+$.id.SelBumon).combogrid('getText')
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
				multiple :false,
				columns:[[
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
					// 初期化
					var num = 0;
					if ($.inArray(id, that.initedObject) < 0){
						that.initedObject.push(id);
					}
					var val = $.getJSONValue(that.jsonHidden, id);
					for (var i=0; i<data.rows.length; i++){
						if (data.rows[i].VALUE == val){
							num = i;
							break;
						}
					}
					if (data.rows.length > 0){
						$('#'+id).combogrid('grid').datagrid('selectRow', num);
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
					if(idx > 0){
						if(newValue){
							$.setJSONObject(that.jsonHidden, id, newValue, newValue);
						}

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

					// 必要な情報がそろっていない場合検索しない
					if ($.inArray($.id.SelYmF, that.initedObject) == -1
							||$.inArray($.id.SelTenpo, that.initedObject) == -1){
						return false;
					}

					// 情報設定
					var json = [{
						REQUIRED: 'REQUIRED',
						TENPO: $('#'+$.id.SelTenpo).combogrid('getValue'),
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
					 {field:'F1',	title:'期間',		width:105}
				]],
				columns:[[
					 {field:'F2',	title:'今年の要因',	width:170}
					,{field:'F3',	title:'予測客数',	width:60,	align:'right',	halign:'left',
						formatter:function(value, rowData, rowIndex) {return $.getFormat(value, '#,##0');}
					}
					,{field:'F4',	title:'',	width:90,	align:'right',	halign:'left',
						formatter:function(value, rowData, rowIndex) {return $.getFormat(value, '#,##0');}
					}
				]],
				fitColumns:false,	// 指定カラム幅を適用する場合、false 指定。
				rowStyler:function(index, row){
//					if (row.F1 != null && (row.F1.indexOf('計') !== -1 )) {
					return 'background-color:#FFFFFF;';
//						return 'background-color:#CCFFCC;';
//					}
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
					// 検索後、初回のみ処理
					if (that.queried){
						that.queried = false;	// 検索後、初回のみ処理
						// 状態保存
						$.saveState(reportNumber, that.getJSONString(), id);
						// 警告
						$.showWarningMessage(data);
					}
				},
				onClickCell:function(rowIndex, field, value){
					// 列名保持
					that.columnName = field;
				},
				autoRowHeight:false,
				singleSelect:true
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

			var kbn = 0;
			var data = {
				'header': JSON.stringify(title),
				'report': reportno,
				'kbn'	: kbn
			};

			// 転送
			$.ajax({
				url: $.reg.excel,
				type: 'POST',
				data: data,
				async: true
			})
			.done(function(){
				// Excel出力
				$.outputExcel(reportno, 0);
			})
			.fail(function(){
				// Excel出力エラー
				$.outputExcelError();
			})
			.always(function(){
				// 通信完了
			});
		}
	} });
})(jQuery);
