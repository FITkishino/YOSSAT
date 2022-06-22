/**
 * jquery report option
 */
;(function($) {

	$.extend({
		reportOption: {
		name:		'Out_Report004',			// （必須）レポートオプションの確認
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
		dedefaultObjNum:	1,	// 初期化オブジェクト数
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

			// 期間
			this.setKikanYM(reportno, $.id.SelYmF);

			// タブ移動サンプル
			$.changeReportByTabs(that);

			// Load処理回避
			//$.tryChangeURL(null);

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
					}

					that.queried = true;
					// Load処理回避
					$.tryChangeURL($.reg.jqeasy);

					// グリッド再描画（easyui 1.4.2 対応）
					$($.id.gridholder).datagrid('load', {} );

					// 登録ボタン状態変化
					$.setButtonState('#'+$.id.btn_entry, true, $.id.btn_entry);
					// 検索ボタン無効化
					$.setButtonState('#'+$.id.btn_search, false, 'success');

					// ログ出力
					$.log(that.timeData, 'loaded:');
				}
			);
		},
		updValidation: function (){	// （必須）批准
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
			// 変更行情報取得
			var changedIndex = $($.id.hiddenChangedIdx).val().split(",");
			// 対象情報抜粋
			var targetRows = [];
			for (var i=0; i<rows.length; i++){
				if($.inArray(i+'', changedIndex) !== -1){
					var rowData = {
							IDX: i+1,		// エラーメッセージ用に行番号を追加
							F1 : rows[i]["F1"],
							F2 : $("#F2_"+i).val().replace(/\//g, '')
						};
					targetRows.push(rowData);
				}
			}

			// 処理時間計測用
			that.timeData = (new Date()).getTime();
			$($.id.gridholder).datagrid('loading');

			$.post(
				$.reg.jqgrid ,
				{
					report:			that.name,		// レポート名
					action:			$.id.action_update,	// 実行処理情報
					obj:			id,								// 実行オブジェクト
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
					// 初期表示検索処理
					$.initialSearch(that);
				},
				onChange:function(newValue, oldValue){
					// 検索ボタン有効化
					$.setButtonState('#'+$.id.btn_search, true, id);
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
					{field:'F1',	title:'特定日',			width:114,	align:'center',	halign:'center'},
					{field:'F2',	title:'基準日',			width:134,	align:'center',	halign:'center',
						formatter:function(value, row, index) {
							return '<input type="text" id="F2_'+index+'" style="width: 75px; text-align: center; ime-mode: disabled;" class="TextDisp" tabindex="'+(1000+index)+'" value="'+value+'">';
						},
						styler:function(value, row, index) {
							return {class:'yellow'}
						}
					}
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

					var input = $('input[id^=F2_]');
					input.each(function(){
						var preVal = $(this).val().replace(/\//g, '');
						$(this).focus(function(){
							$(this).val($(this).val().replace(/\//g, ''));
							$(this).attr('maxlength', '8');
							$(this).select();

						}).blur(function(){
							var id = $(this).attr('id');

							var newVal = $(this).val().replace(/\//g, '');
							if(newVal!==preVal){
								// 日付妥当性チェック
								var year = parseInt(newVal.substr(0,4),10);
								var month = parseInt(newVal.substr(4,2),10) - 1;
								var day = parseInt(newVal.substr(6,2),10);

								var rt = false;
								if(year >= 0 && month >= 0 && month <= 11 && day >= 1 && day <= 31){
									var date = new Date(year, month, day);
									rt = !isNaN(date) && date.getFullYear() == year && date.getMonth() == month && date.getDate() == day;
								}
								// 閏年の場合、02/99 可
								if($.isLeapYear(year) && month == 1 && day == 99){
									rt = true;
								}

								if(!rt){
									var func = function(){$('#'+id).val(preVal).focus();};
									$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"有効な日付を入力してください。",'warning', func);
									return false;
								}
							}

							$(this).removeAttr('maxlength');
							if(newVal.length > 0){
								$(this).val(newVal.substr(0,4) + "/" + newVal.substr(4,2) + "/" + newVal.substr(6,2));
							}

							if(newVal!==preVal){
								$.setChangeIdx(id.split("_")[1]);
								preVal = newVal;
							}
						});
					});
					setEnterEvent(input);
				},
				onClickCell:function(rowIndex, field, value){

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