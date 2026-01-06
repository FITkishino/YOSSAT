/**
 * jquery report option
 */
;(function($) {

	$.extend({
		reportOption: {
		name:		'Out_Report001',			// （必須）レポートオプションの確認
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
		columnName:'',	// OnClickRowの列名
		queried : false,
		onChangeReport: false,
		isUserTenpo: function(){			// 検索店舗が所属店舗か判断
			var srcTenpo = $.getJSONValue(this.jsonString, $.id.SelTenpo);	// 検索店舗
			if(srcTenpo.length < 1){return false;}	// 検索前
			var usrTenpo = $($.id.hidden_userTenpo).val();
			if(usrTenpo.length < 1){return true;}	// 特殊権限ユーザー
			if(srcTenpo===usrTenpo){return true;}	// 所属店舗ユーザー
			return false;
		},
		isTenpoLevelData:function(){		// 検索結果で店舗単位情報かどうか判断
			var srcBumon = $.getJSONValue(this.jsonString, $.id.SelBumon);	// 検索部門
			return srcBumon===$.id.valueSel_Head;
		},
		isBumonOnlyUser: function(){			// 部門担当者か否か
			var usrBumon = $($.id.hidden_userBumon).val();
			if(usrBumon.length > 0){return true;}	// 部門担当者
			return false;
		},
		usableTenpoData: function(){		// 検索結果で店舗単位情報が更新可能か判断
			return this.isUserTenpo && this.isTenpoLevelData() && !this.isBumonOnlyUser();
		},
		usableBumonData: function(){		// 検索結果で部門単位情報が更新可能か判断
			return this.isUserTenpo && !this.isTenpoLevelData();
		},
		gridData:[],						// 検索結果
		gridTitle:[],						// 検索結果
		commentChangeIdx:999,				// コメントは行単位データではないため、変更時、変更配列に行番号の代わりに特殊な数値を設定し、変更したか否かを判断する
		yosChangePrefix:1000,				// 予算系と予測客数は異なるテーブルへの更新のため、変更情報は別途保持。どちらを変更したかを判断するための数値
		kykChangePrefix:2000,				// 予算系と予測客数は異なるテーブルへの更新のため、変更情報は別途保持。どちらを変更したかを判断するための数値
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
			//that.setGrid($.id.gridholder, reportno);

			// 初期化するオブジェクト数設定
			this.initObjNum = this.dedefaultObjNum;

			// 部門
			this.setBumon(reportno, $.id.SelBumon);

			// 店舗
			this.setTenpo(reportno, $.id.SelTenpo);

			// 期間
			this.setKikanYM(reportno, $.id.SelYmF);

			// ポイント入力欄
			var input = $('#TxtComment');
			input.attr('readonly', 'readonly');
			input.attr('tabindex', '-1');
			input.each(function(){
				var preVal = $(this).val();
				$(this).focus(function(){
					$(this).val($(this).val());
					$(this).select();

				}).blur(function(){
					var id = $(this).attr('id');
					var newVal = $(this).val();
					if(newVal!==preVal){
						// 文字数チェック
						var byte = 1000;
						if(newVal!=="" && !$.checkByte(newVal, byte)){
							var func = function(){$('#'+id).val(preVal).focus();};
							$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"半角の場合"+byte+"文字以下で入力してください。",'warning', func);
							return false;
						}
					}
					$(this).val(newVal);
					if(newVal!==preVal){
						$.setChangeIdx(that.commentChangeIdx);
						preVal = newVal;
					}
				});
			});
			setEnterEvent(input);


			// 部門荒利率
			// ※ 各条件から呼出し

			// タブ移動サンプル
			$.changeReportByTabs(that);

//			var width =  $('#ctrlTbl').outerWidth()*1 + 18 ;
//			$('#view1').css('max-width', width);
//			$('#view2').css('max-width', width);
			$('#view1').scroll(function(){
				$('#view2').scrollLeft($(this).scrollLeft());
			});
			$('#view2').scroll(function(){
				$('#view1').scrollLeft($(this).scrollLeft());
			});

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
			var szSelKikanF		= $.getJSONObject(this.jsonString, $.id.SelKikanF).value;		// 期間FROM
			var szSelTenpo		= $.getJSONObject(this.jsonString, $.id.SelTenpo).value;		// 店舗
			var szSelBumon		= $.getJSONObject(this.jsonString, $.id.SelBumon).value;		// 部門
			var szTxtBmnAraRit	= $.getJSONObject(this.jsonString, $.id.TxtBmnAraRit).value;	// 部門荒利率

			// 処理時間計測用
			that.timeData = (new Date()).getTime();
			// Loading表示
			$.appendMaskMsg();

			$.post(
				$.reg.jqgrid ,
				{
					report:			that.name,		// レポート名
					KIKAN_F:		szSelKikanF,	// 期間FROM
					TENPO:			szSelTenpo,		// 店舗
					BUMON:			szSelBumon,		// 部門
					BMN_ARA_RIT:	szTxtBmnAraRit,	// 部門荒利率
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

					// 検索データ（想定）
					that.gridData = JSON.parse(json).rows;
					that.gridTitle = JSON.parse(json).titles;

					var opts = JSON.parse(json).opts

					// データ表示
					that.setData(that.gridData, opts);

					// 各種入力欄期間チェック
					var canChangeYosanKikan = opts.canChangeYosanKikan;
					var canChangeTYosanKikan= opts.canChangeTYosanKikan;
					var canChangeKyakuKikan = opts.canChangeKyakuKikan;
					var canChangeEventKikan = opts.canChangeEventKikan;
					// データ存在チェック
					var existsBumon = $('#'+$.id.SelBumon).combogrid('grid').datagrid('getRows').length > 1;

					// 登録ボタン状態変化
					$.setButtonState('#'+$.id.btn_entry, existsBumon && (canChangeYosanKikan||canChangeTYosanKikan||canChangeKyakuKikan||canChangeEventKikan) && (that.usableTenpoData()||that.usableBumonData()), $.id.btn_entry);
					// 按分ボタン状態変化
					$.setButtonState('#'+$.id.btn_anbun, existsBumon && canChangeTYosanKikan && that.usableTenpoData(), $.id.btn_anbun);
					// 検索ボタン無効化
					$.setButtonState('#'+$.id.btn_search, false, 'success');

					// 状態保存
					$.saveState2(reportno, that.getJSONString());

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

			var rtn = false;
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
				$.messager.confirm($.message.ID_MESSAGE_TITLE_CONF,'店長予算案の按分を実行します。よろしいでしょうか？', func);
			}
		},
		updValidation: function(id){	// （必須）批准
			var that = this;
			// EasyUI のフォームメソッド 'validate' 実施
			var rt = $($.id.gridform).form('validate');
			if(!rt){
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,'入力内容を確認してください。','warning');
			}
			if(rt){
// 20170905
//				if(that.usableTenpoData() && $('#diff1').text()*1 !== 0){
//					rt = false;
//					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"店長予算案の計が、予算案の計と一致しません。",'warning');
//				}
				if(that.usableBumonData() && $('#diff2').text()*1 !== 0){
					rt = false;
					$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"修正予算の計が、予算案の計と一致しません。",'warning');
				}
			}
			return rt;
		},
		updSuccess: function(id){	// validation OK時 の update処理
			var that = this;

			// Grid内全情報取得
			var rows = that.gridData;
			// 変更行情報取得
			var changedIndex = $($.id.hiddenChangedIdx).val().split(",");

			// 対象情報抜粋
			var targetRows = [];
			var targetRows2= [];
			var txtComment = null;
			if(that.usableTenpoData()){
				for (var i=0; i<rows.length; i++){
					var yosIdx = that.yosChangePrefix + i;
					if($.inArray(yosIdx+'', changedIndex) !== -1){
						var rowData = {
								IDX: i+1,		// エラーメッセージ用に行番号を追加
								F1 : rows[i]["F1"],
								F2 : $("#F3_"+i).val(),
								F3 : $("#F6_"+i).val().replace(/,/g, '')
							};
						targetRows.push(rowData);
					}
					var kykIdx = that.kykChangePrefix + i;
					if($.inArray(kykIdx+'', changedIndex) !== -1){
						var rowData = {
								IDX: i+1,		// エラーメッセージ用に行番号を追加
								F1 : rows[i]["F1"],
								F2 : $("#F14_"+i).val().replace(/,/g, '')
							};
						targetRows2.push(rowData);
					}
				}
				if($.inArray(that.commentChangeIdx, changedIndex)){
					txtComment = $('#TxtComment').val();
				}
			}else if(that.usableBumonData()){
				for (var i=0; i<rows.length; i++){
					var yosIdx = that.yosChangePrefix + i;
					if($.inArray(yosIdx+'', changedIndex) !== -1){
						var rowData = {
								IDX: i+1,		// エラーメッセージ用に行番号を追加
								F1 : rows[i]["F1"],
								F2 : $("#F6_"+i).val().replace(/,/g, '')
							};
						targetRows.push(rowData);
					}
				}
			}

			var szSelKikanF		= $.getJSONObject(this.jsonString, $.id.SelKikanF).value;	// 期間FROM
			var szSelTenpo		= $.getJSONObject(this.jsonString, $.id.SelTenpo).value;	// 店舗
			var szSelBumon		= $.getJSONObject(this.jsonString, $.id.SelBumon).value;	// 部門

			// 処理時間計測用
			that.timeData = (new Date()).getTime();
			// Loading表示
			$.appendMaskMsg();

			$.post(
				$.reg.jqgrid ,
				{
					report:			that.name,						// レポート名
					action:			$.id.action_update,				// 実行処理情報
					obj:			id,								// 実行オブジェクト
					KIKAN_F:		szSelKikanF,					// 期間FROM
					TENPO:			szSelTenpo,						// 店舗
					BUMON:			szSelBumon,						// 部門
					IDX:			$($.id.hiddenChangedIdx).val(),	// 更新対象Index
					DATA:			JSON.stringify(targetRows),		// 更新対象情報（予算系）
					DATA2:			JSON.stringify(targetRows2),	// 更新対象情報（予測客数）
					COMMENT:		txtComment,						//
					t:				(new Date()).getTime()
				},
				function(data){
					// Loading非表示
					$.removeMaskMsg();
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
				value:	$('#'+$.id.SelTenpo).combogrid('getValue'),
				text:	$('#'+$.id.SelTenpo).combogrid('getText')
			});
			// 部門
			this.jsonTemp.push({
				id:		$.id.SelBumon,
				value:	$('#'+$.id.SelBumon).combogrid('getValue'),
				text:	$('#'+$.id.SelBumon).combogrid('getText')
			});
			// 部門荒利率
			this.jsonTemp.push({
				id:		$.id.TxtBmnAraRit,
				value:	$('#'+$.id.TxtBmnAraRit).val().replace(/,/g, ''),
				text:	$('#'+$.id.TxtBmnAraRit).val()
			});
		},
		setData: function(rows, opts){		// データ表示
			var that = this;

			var canChangeYosanKikan = opts.canChangeYosanKikan;
			//var canChangeTYosanKikan= opts.canChangeTYosanKikan;
			var canChangeKyakuKikan = opts.canChangeKyakuKikan;
			var canChangeEventKikan = opts.canChangeEventKikan;

			var szSelBumon		= $.getJSONObject(this.jsonString, $.id.SelBumon).value;	// 部門

			// コメント
			$('#TxtComment').val(opts.comment);
			if(canChangeEventKikan && that.usableTenpoData()){
				var input = $('#TxtComment');
				input.removeAttr('readonly');
				input.attr('tabindex', '900');
				input.parent().addClass('yellow');
			}else{
				var input = $('#TxtComment');
				input.attr('readonly', 'readonly');
				input.attr('tabindex', '-1');
				input.parent().removeClass('yellow');
			}
			// 更新者
			if(opts.updUser){
				$('#nm_update').text("最終更新者："+opts.updUser);
			}

			// 入力行の一括削除
			$('tr.ctrlRow').remove();

			// 入力可フラグ
			var inputF3 = canChangeEventKikan && that.usableTenpoData();
			//var inputF6 = canChangeTYosanKikan&& that.usableTenpoData();
			var inputF6 = canChangeYosanKikan && that.usableBumonData();
			var inputF14= canChangeKyakuKikan && that.usableTenpoData();

			for(var i=0; i<rows.length; i++){
				var row = rows[i];

				var view = '';
				view +='<tr class="ctrlRow" style="height: 23px;">'+
							'';

				if(inputF3){
					view +=	'<td style="text-align: left;" class="yellow"><input type="text" id="F3_'+i+'" style="width:163px;" class="TextDisp" tabindex="'+(100+i)+'" value="'+row['F3']+'"></td>'+
							'';
				}else{
					view +=	'<td style="text-align: left;"><input type="text" id="F3_'+i+'" style="width:163px;" class="TextDisp" tabindex="-1" readonly="readonly" value="'+row['F3']+'"></td>'+
							'';
				}

				view +=		'<td style="text-align: center;">'+
								'<input type="hidden" id="F1_'+i+'" value="'+row['F1']+'">'+
								'<input type="hidden" id="F2_'+i+'" value="'+row['F2']+'">'+
								'<span id="F4_'+i+'">'+row['F4']+'</span>'+
							'</td>'+
							'<td style="text-align: right;"><span id="F5_'+i+'">'+getFormat(row['F5'], '#,##0')+'</span></td>'+
							'';
//				if(inputF6){
//					view +=	'<td style="text-align: right;" class="yellow"><input type="text" id="F6_'+i+'" style="width: 48px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(200+i)+'" value="'+getFormat(row['F6'], '#,##0')+'"></td>'+
//							'';
//				}else{
//					view +=	'<td style="text-align: right;"><input type="text" id="F6_'+i+'" style="width: 48px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="-1" readonly="readonly" value="'+getFormat(row['F6'], '#,##0')+'"></td>'+
//					'';
//				}

				if(inputF6){
					view +=	'<td style="text-align: right;" class="yellow"><input type="text" id="F6_'+i+'" style="width: 48px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F6'], '#,##0')+'"></td>'+
							'';
				}else{
					view +=	'<td style="text-align: right;"><input type="text" id="F6_'+i+'" style="width: 48px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="-1" readonly="readonly" value="'+getFormat(row['F6'], '#,##0')+'"></td>'+
					'';
				}

				view +=	'<td style="text-align: right;"><span id="F7_'+i+'" style="width: 55px; overflow: hidden; display: inline-block;">'+row['F7']+'</span></td>'+
							'<td style="text-align: right;"><span id="F8_'+i+'">'+getFormat(row['F8'], '#,##0')+'</span></td>'+
							'<td style="text-align: left;padding-left: 3px;"><span id="F9_'+i+'">'+row['F9']+'</span></td>'+
							'<td style="text-align: left;"><input type="text" id="F10_'+i+'" style="width:163px;" class="TextDisp" tabindex="-1" readonly="readonly" value="'+row['F10']+'"></td>'+
							'<td style="text-align: right;"><span id="F11_'+i+'">'+getFormat(row['F11'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F12_'+i+'"style="width: 58px;">'+getFormat(row['F12'], '#,##0')+'</span></td>'+
              //'<td style="text-align: right;"><input type="text" id="F14_'+i+'" style="width: 48px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="-1" readonly="readonly" value="'+getFormat(row['F14'], '#,##0')+'"></td>'+
              '';
//				if(inputF14){
//				//	view +=	'<td style="text-align: right;" class="yellow"><input type="text" id="F14_'+i+'" style="width: 48px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(400+i)+'" value="'+getFormat(row['F14'], '#,##0')+'"></td>'+
//				//			'';
//				//}else{
//					view +=	'<td style="text-align: right;"><input type="text" id="F14_'+i+'" style="width: 48px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="-1" readonly="readonly" value="'+getFormat(row['F14'], '#,##0')+'"></td>'+
//					'';
//				}
        if($.getWeathernews(row['F14'])==1  ){
			    view +='<td style="text-align:center;"><span id="F13_'+i+'" style=" color: red;">☀</span></td>'
        }else if ($.getWeathernews(row['F14'])==2  ){
			   view +='<td style="text-align:center;"><span id="F13_'+i+'" style=" color: grey;">☁</span></td>'
        }else if ($.getWeathernews(row['F14'])==3  ){
			   view +='<td style="text-align:center;"><span id="F13_'+i+'" style=" color: blue;">☂</span></td>'
        }else if ($.getWeathernews(row['F14'])==4  ){
			   view +='<td style="text-align:center;"><span id="F13_'+i+'" style=" color: teal;">☃</span></td>'
        }else{
			   view +='<td style="text-align:center;"><span id="F13_'+i+'">'+$.getWeathernews(row['F14'])+'</span></td>';
	    	} ;
        if(inputF3){
			  view +=	'<td style="text-align: center;" class="yellow"><input type="text" id="F14_'+i+'" style="width: 25px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F14'], '#0')+'"></td>'+
							'';
			  }else{
				view +=		'<td style="text-align:center;"><span id="F14_'+i+'">'+$.getWeathernews(row['F14'])+'</span></td>';
        }
				if($.getWeathernews(row['F16'])==1  ){
			    view +='<td style="text-align:center;"><span id="F15_'+i+'" style=" color: red;">☀</span></td>'
        }else if ($.getWeathernews(row['F16'])==2  ){
			   view +='<td style="text-align:center;"><span id="F15_'+i+'" style=" color: grey;">☁</span></td>'
        }else if ($.getWeathernews(row['F16'])==3  ){
			   view +='<td style="text-align:center;"><span id="F15_'+i+'" style=" color: blue;">☂</span></td>'
        }else if ($.getWeathernews(row['F16'])==4  ){
			   view +='<td style="text-align:center;"><span id="F15_'+i+'" style=" color: teal;">☃</span></td>'
        }else{
			   view +='<td style="text-align:center;"><span id="F15_'+i+'">'+$.getWeathernews(row['F16'])+'</span></td>';
	    	} ;

         if(inputF3){
			  view +=	'<td style="text-align: center;" class="yellow"><input type="text" id="F16_'+i+'" style="width: 30px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F16'], '#0')+'"></td>'+
			  '<td style="text-align: center;" class="yellow"><input type="text" id="F17_'+i+'" style="width: 30px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F17'], '#0')+'"></td>'+
			  '<td style="text-align: center;" class="yellow"><input type="text" id="F18_'+i+'" style="width: 30px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F18'], '#0')+'"></td>'+
							'';
			  }else{
				view +=	'<td style="text-align:center;"><span id="F16_'+i+'">'+$.getWeathernews(row['F16'])+'</span></td>'+
				      '<td style="text-align: right;"><span id="F17_'+i+'">'+row['F17']+'</span></td>'+
							'<td style="text-align: right;"><span id="F18_'+i+'">'+row['F18']+'</span></td>';
				}
				view +=	'</tr>';

				$('#emptyRow').before(view);
			}

			// 31行目まで表示
			for(var i=rows.length+1; i<=31; i++){
				var view = '';
				view +=	'<tr class="ctrlRow" style="height: 23px;">'+
							'';

				if(inputF3){
					view +=	'<td class="yellow">&nbsp;</td>'+
							'';
				}else{
					view +=	'<td>&nbsp;</td>'+
							'';
				}

				view +=		'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'';
//				if(inputF6){
//					view +=	'<td class="yellow">&nbsp;</td>'+
//							'';
//				}else{
//					view +=	'<td>&nbsp;</td>'+
//							'';
//				}
				if(inputF6){
					view +=	'<td class="yellow">&nbsp;</td>'+
							'';
				}else{
					view +=	'<td>&nbsp;</td>'+
							'';
				}
				view +=		'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>';

		//		if(inputF14){
	//				view +=	'<td class="yellow">&nbsp;</td>'+
		//					'';
		//		}else{
		//			view +=	'<td>&nbsp;</td>'+
		//					'';
		//		}
				if(inputF3){
				view +=		'<td>&nbsp;</td>'+
							'<td class="yellow">&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td class="yellow">&nbsp;</td>'+
							'<td class="yellow">&nbsp;</td>'+
							'<td class="yellow">&nbsp;</td>';
				}else{
					view +=		'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>';
				}
				view +=	'</tr>';

				$('#emptyRow').before(view);
			}

			// 今年の要因
			if(inputF3){
				var input = $('input[id^=F3_]');
				input.each(function(){
					var preVal = $(this).val();
					$(this).focus(function(){
						$(this).val($(this).val());
						$(this).select();

					}).blur(function(){
						var id = $(this).attr('id');
						var newVal = $(this).val();
						if(newVal!==preVal){
							// 文字数チェック
							var byte = 1000;
							if(newVal!=="" && !$.checkByte(newVal, byte)){
								var func = function(){$('#'+id).val(preVal).focus();};
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"半角の場合"+byte+"文字以下で入力してください。",'warning', func);
								return false;
							}
						}
						$(this).val(newVal);
						if(newVal!==preVal){
							var changeIdx = that.yosChangePrefix + id.split("_")[1]*1
							$.setChangeIdx(changeIdx);
							preVal = newVal;
						}
					});
				});
				setEnterEvent(input);
			}

			// 修正予算
			if(inputF6){
				var input = $('input[id^=F6_]');
				input.each(function(){
					var preVal = $(this).val().replace(/,/g, '');
					$(this).focus(function(){
						$(this).val($(this).val().replace(/,/g, ''));
						$(this).css('color', 'black');
						$(this).attr('maxlength', '5');
						$(this).select();

					}).blur(function(){
						var id = $(this).attr('id');

						var newVal = $(this).val().replace(/,/g, '');
						if(newVal!==preVal){
							// 入力チェック
							var func = function(){$('#'+id).val(preVal).focus();};
							if((newVal+'').length < 1 || ! that.chkInt(newVal, 5)){
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"5文字以下の半角数字で入力してください。",'warning', func);
								return false;
							}
							if(newVal*1 < 0){
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"0以上の値を入力してください。",'warning', func);
								return false;
							}
						}

						$(this).removeAttr('maxlength');
						$(this).val(getFormat(newVal, '#,##0'));
						if($(this).val()!==$("#F5_"+id.split("_")[1]).text()){
							$(this).css('color', 'blue');
						}

						if(newVal!==preVal){
							var changeIdx = that.yosChangePrefix + id.split("_")[1]*1
							$.setChangeIdx(changeIdx);
							preVal = newVal;
							// 合計値計算
							that.setSum();
						}
					});
				});
				setEnterEvent(input);
			}
			// 昨年天気午前
			if(inputF14){
				var input = $('input[id^=F14_]');
				input.each(function(){
					var preVal = $(this).val().replace(/,/g, '');
					$(this).focus(function(){
						$(this).val($(this).val().replace(/,/g, ''));
						$(this).css('color', 'black');
						$(this).attr('maxlength', '1');
						$(this).select();

					}).blur(function(){
						var id = $(this).attr('id');
						var newVal = $(this).val().replace(/,/g, '');

						if(newVal!==preVal){
							// 入力チェック
							var func = function(){$('#'+id).val(preVal).focus();};
							if((newVal+'').length < 1 || ! that.chkInt(newVal, 1)){
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"1～4の値を半角で入力してください。",'warning',func);
								return false;
							}
							if(newVal>= 5 ||newVal ==0 ){
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"1～4の値を入力してください。",'warning',func);
								return false;
							}
						}

						$(this).removeAttr('maxlength');
						$(this).val(getFormat(newVal, '#,##0'));

						if(newVal!==preVal){
							var changeIdx = that.kykChangePrefix + id.split("_")[1]*1
							$.setChangeIdx(changeIdx);
							preVal = newVal;
              //天気マークの更新
							that.setWeatherMark('F13_'+id.split("_")[1]*1,newVal);
							// 合計値計算
							that.setSum();
						}
					});
				});
				setEnterEvent(input);
			}
			// 昨年天気午後
      if(inputF14){
				var input = $('input[id^=F16_]');
				input.each(function(){
					var preVal = $(this).val().replace(/,/g, '');
					$(this).focus(function(){
						$(this).val($(this).val().replace(/,/g, ''));
						$(this).css('color', 'black');
						$(this).attr('maxlength', '1');
						$(this).select();

					}).blur(function(){
						var id = $(this).attr('id');
						var newVal = $(this).val().replace(/,/g, '');

						if(newVal!==preVal){
							// 入力チェック
							var func = function(){$('#'+id).val(preVal).focus();};
							if((newVal+'').length < 1 || ! that.chkInt(newVal, 1)){
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"1～4の値を半角で入力してください。",'warning',func);
								return false;
							}
							if(newVal>= 5 ||newVal ==0 ){
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"1～4の値を入力してください。",'warning',func);
								return false;
							}

						}

						$(this).removeAttr('maxlength');
						$(this).val(getFormat(newVal, '#,##0'));

						if(newVal!==preVal){
							var changeIdx = that.kykChangePrefix + id.split("_")[1]*1
							$.setChangeIdx(changeIdx);
							preVal = newVal;
							// 合計値計算
							that.setSum();
							//天気マークの更新
							that.setWeatherMark('F15_'+id.split("_")[1]*1,newVal);
						}
					});
				});
				setEnterEvent(input);
			}
			// 昨年気温午前
      if(inputF14){
				var input = $('input[id^=F17_]');
				input.each(function(){
					var preVal = $(this).val().replace(/,/g, '');
					$(this).focus(function(){
						$(this).val($(this).val().replace(/,/g, ''));
						$(this).css('color', 'black');
						$(this).attr('maxlength', '3');
						$(this).select();

					}).blur(function(){
						var id = $(this).attr('id');
						var newVal = $(this).val().replace(/,/g, '');

						if(newVal!==preVal){
							// 入力チェック
							var func = function(){$('#'+id).val(preVal).focus();};
							if(Number.isInteger(newVal)){
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"半角整数の値を入力してください。",'warning',func);
								return false;
							}


						}

						$(this).removeAttr('maxlength');
						$(this).val(getFormat(newVal, '#,##0'));

						if(newVal!==preVal){
							var changeIdx = that.kykChangePrefix + id.split("_")[1]*1
							$.setChangeIdx(changeIdx);
							preVal = newVal;
							// 合計値計算
							that.setSum();
						}
					});
				});
				setEnterEvent(input);
			}
			// 昨年気温午後
      if(inputF14){
				var input = $('input[id^=F18_]');
				input.each(function(){
					var preVal = $(this).val().replace(/,/g, '');
					$(this).focus(function(){
						$(this).val($(this).val().replace(/,/g, ''));
						$(this).css('color', 'black');
						$(this).attr('maxlength', '3');
						$(this).select();

					}).blur(function(){
						var id = $(this).attr('id');
						var newVal = $(this).val().replace(/,/g, '');

						if(newVal!==preVal){
							// 入力チェック
							var func = function(){$('#'+id).val(preVal).focus();};
							if(Number.isInteger(newVal)){
								$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"半角整数の値を半角で入力してください。",'warning',func);
								return false;
							}

						}

						$(this).removeAttr('maxlength');
						$(this).val(getFormat(newVal, '#,##0'));

						if(newVal!==preVal){
							var changeIdx = that.kykChangePrefix + id.split("_")[1]*1
							$.setChangeIdx(changeIdx);
							preVal = newVal;
							// 合計値計算
							that.setSum();
						}
					});
				});
				setEnterEvent(input);
			}
			// 合計値計算
			that.setSum();
		},
		setWeatherMark: function(id,newVal){ //天気マークの更新
			        var mark = document.getElementById(id);
		          if(newVal==1){
			        mark.textContent='☀' ;
			        mark.style.color = 'red';
              }else if(newVal==2){
				      mark.textContent='☁' ;
				      mark.style.color= 'grey';
			        }else if(newVal==3){
				      mark.textContent='☂' ;
				      mark.style.color= 'blue';
			        }else if(newVal==4){
						  mark.textContent='☃' ;
						  mark.style.color= 'teal';
				      }else{
						  mark.textContent='' ;
					    };
		},

		setSum: function(){		// 合計値計算
			var sumF5_T = 0;
			var sumF6_T = 0;
			var sumF7_T = 0;
			var sumF8_T = 0;
			var sumF11_T = 0;
			var sumF12_T = 0;
			var sumF5_W = 0;
			var sumF6_W = 0;
			var sumF7_W = 0;
			var sumF8_W = 0;
			var weekF = '999999999';
			var weekT = '0';
			var num = 0;
			var weekNum = null;

			// 部門荒利率が特定できる場合
			var existsBmnAraRit = $('#'+$.id.TxtBmnAraRit).val() !== "";

			$('input[id^=F6_]').each(function(){
				var index = $(this).attr('id').split('_')[1];

				var valF5 = $('#F5_'+index).text().replace(/,/g, '') * 1;
				var valF6 = $('#F6_'+index).val().replace(/,/g, '') * 1;
				var valF7= $('#F7_'+index).text().replace(/,/g, '') * 1;
				var valF8= $('#F8_'+index).text().replace(/,/g, '') * 1;
				var valF11= $('#F11_'+index).text().replace(/,/g, '') * 1;
				var valF12= $('#F12_'+index).text().replace(/,/g, '') * 1;


				// 昨年比
				var valF7 = 0;
				if(valF8*1 > 0){
					valF7 = Math.floor(valF6 / valF8 * 100 * 10) / 10;
				}

				// 週間計をセット
				if(weekNum != null && weekNum != $('#F2_'+index).val()*1){
					// 昨年比
					var sumF7W = 0;
					if(sumF8_W*1 > 0){
						sumF7_W = Math.floor(sumF6_W / sumF8_W * 100 * 10) / 1000;
					}

					$('#week_'+num).text(weekF.substr(5,2)+'/'+weekF.substr(7,2)+' - '+weekT.substr(5,2)+'/'+weekT.substr(7,2));
					$('#sumF5_'+num).text(getFormat(sumF5_W, '#,##0'));;
					$('#sumF6_'+num).text(getFormat(sumF6_W, '#,##0'));
					$('#sumF7_'+num).text(getFormat(sumF7_W, '#,##0.0%'));
					$('#sumF8_'+num).text(getFormat(sumF8_W, '#,##0'));
					num++;
					weekF = '999999999';
					weekT = '0';
					sumF5_W = 0;
					sumF6_W = 0;
					sumF7_W = 0;
					sumF8_W = 0;
				}

				weekNum = $('#F2_'+index).val()*1;
				weekF = Math.min(weekF*1, $('#F1_'+index).val()*1 + 100000000)+'';
				weekT = Math.max(weekT*1, $('#F1_'+index).val()*1 + 100000000)+'';

				sumF5_T += valF5;
				sumF6_T += valF6;
				sumF7_T += valF7;
				sumF8_T += valF8;
				sumF11_T += valF11;
				sumF12_T += valF12;
				sumF5_W += valF5;
				sumF6_W += valF6;
				sumF8_W += valF8;


				$('#F7_'+index).text(getFormat(valF7/ 100, '#,##0.0%'));

				// 予算案と修正予算が異なる場合、青字にする
				if(valF5 != valF6){
					$('#F6_'+index).css('color', 'blue');
				}

			});

			// 週間計をセット
			if(weekNum != null){
				// 昨年比
				var sumF7_W = 0;
				if(sumF8_W*1 > 0){
					sumF7_W = Math.floor(sumF6_W / sumF8_W * 100 * 10) / 1000;
				}

				$('#week_'+num).text(weekF.substr(5,2)+'/'+weekF.substr(7,2)+' - '+weekT.substr(5,2)+'/'+weekT.substr(7,2));
				$('#sumF5_'+num).text(getFormat(sumF5_W, '#,##0'));
				$('#sumF6_'+num).text(getFormat(sumF6_W, '#,##0'));
				$('#sumF7_'+num).text(getFormat(sumF7_W, '#,##0.0%'));
				$('#sumF8_'+num).text(getFormat(sumF8_W, '#,##0'));
				num++;
			}

			// 昨年比
			var sumF7_T = 0;
			if(sumF8_T*1 > 0){
				sumF7_T = Math.floor(sumF6_T / sumF8_T * 100 * 10) / 1000;
			}

			// 合計値をセット
			$('#sumF5_T2').text(getFormat(sumF5_T, '#,##0'));
	  	$('#sumF6_T2').text(getFormat(sumF6_T, '#,##0'));
			$('#sumF7_T2').text(getFormat(sumF7_T, '#,##0.0%'));
			$('#sumF8_T2').text(getFormat(sumF8_T, '#,##0'));
			$('#sumF5_T1').text(getFormat(sumF5_T, '#,##0'));
			$('#sumF6_T1').text(getFormat(sumF6_T, '#,##0'));
			$('#sumF7_T1').text(getFormat(sumF7_T, '#,##0.0%'));
			$('#sumF8_T1').text(getFormat(sumF8_T, '#,##0'));
			$('#sumF11_T1').text(getFormat(sumF11_T, '#,##0'));
			$('#sumF12_T1').text(getFormat(sumF12_T, '#,##0'));
			$('#diff2').text(getFormat((sumF6_T*1 - sumF5_T*1), '#,##0'));

			if(sumF6_T*1 - sumF5_T*1 == 0){
				$('#diff2').css('color', 'black');
			}else{
				$('#diff2').css('color', 'red');
			}

			// 空白をセット
			while(num < 6){
				$('#week_'+num).text('　');
				$('#sumF5_'+num).text('');
				$('#sumF6_'+num).text('');
				$('#sumF7_'+num).text('');
				$('#sumF8_'+num).text('');
				num++;
			}
		},
		chkInt: function(value, param){
			var re = new RegExp("^[0-9]{1,"+param+"}$");
			return value.match(re);
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
				required: false,
				editable: false,
				autoRowHeight:false,
				idField:'VALUE',
				textField:'TEXT',
				columns:[[
					{field:'TEXT',	title:'',	width:250}
				]],
				fitColumns: true,
				showHeader: false,
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
				required: false,
				editable: false,
				autoRowHeight:false,
				idField:'VALUE',
				textField:'TEXT',
				columns:[[
					{field:'TEXT',	title:'',	width:250}
				]],
				fitColumns: true,
				showHeader: false,
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
					that.onChangeFlag = true;
					$.ajaxSettings.async = true;
					// 部門荒利率取得
					that.setBmnAraRit(reportno);
				},
				onChange:function(newValue, oldValue){
					if(idx > 0 && that.onChangeFlag){
						if(newValue){
							$.setJSONObject(that.jsonHidden, id, newValue, newValue);
						}
						// 部門荒利率取得
						that.setBmnAraRit(reportno);
					}
				}
			});
		},
		setBmnAraRit: function(reportno){		// 部門
			var that = this;
			var id = $.id.TxtBmnAraRit;

			// 必要な項目全部初期化してたら取得
			if ($.inArray($.id.SelYmF, that.initedObject) >= 0
					&& $.inArray($.id.SelTenpo, that.initedObject) >= 0
					&& $.inArray($.id.SelBumon, that.initedObject) >= 0){
				// 情報設定
				var json = [{
					KIKAN_F:		$('#'+$.id.SelYmF).combogrid('getValue'),	// 期間FROM
					TENPO:			$('#'+$.id.SelTenpo).combogrid('getValue'),		// 店舗
					BUMON:			$('#'+$.id.SelBumon).combogrid('getValue'),		// 部門
				}];

				$.post(
					$.reg.easy ,
					{
						page	:	reportno,
						obj		:	id,
						sel		:	(new Date()).getTime(),
						target	:	id,
						action	:	$.id.action_init,
						json	:	JSON.stringify(json),
					},
					function(json){
						var rows = JSON.parse(json).rows
						var val = "";
						if(rows.length > 0 && rows[0].VALUE !== undefined){
							val = getFormat(rows[0].VALUE, '#,##0.00');
						}
						$("#"+id).val(val);


						// 検索ボタン有効化
						$.setButtonState('#'+$.id.btn_search, true, id);
						if ($.inArray(id, that.initedObject) < 0){
							that.initedObject.push(id);
							// ログ出力
							$.log(that.timeData, id+' init:');
							// 初期表示検索処理
							$.initialSearch(that);
						}
					}
				);
			}


		},
		setObjectState: function(){	// 軸の選択内容による制御
			var that = this;
		},
		setGrid: function (id, reportNumber){	// グリッドの構築
			var that = this;
		},
		getRecord: function(){		// （必須）レコード件数を戻す
			var data = this.gridData;
			if (data == null) {
				return 0;
			} else {
				return data.length;
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

				var view = $('#data_panel');
				if (view.panel().options != 'undefined') {
					// tb
					placeholderHeight = $($.id.toolbar).panel('panel').height() + $($.id.buttons).height();

					// datagrid の格納された panel の高さ
					gridholderHeight = $(window).height() - placeholderHeight;
				}

				view.panel('resize', {
					width:	changeWidth,
					height:	gridholderHeight
				});

				var height = gridholderHeight - $('#view1').height();

				$('#view1').css('width', changeWidth - 12);
				$('#view2').css('height', height - 6).css('width', changeWidth);

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
			var kbn = 0;
			var addTableData = [];
			addTableData.push($.outputExcelAddDataTable("tr[id^=info_sum_r]"));
			var data = {
				'header': JSON.stringify([this.gridTitle]),
				'report': reportno,
				'kbn'	: kbn,
				'opt_table':JSON.stringify(addTableData)
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
