/**
 * jquery report option
 */
;(function($) {

	$.extend({
		reportOption: {
		name:		'Out_Report002',			// （必須）レポートオプションの確認
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
		isTenpoALl: function(){
			var srcTenpo = $.getJSONValue(this.jsonString, $.id.SelTenpo);	// 検索店舗
			if(srcTenpo==='-1'){return false;}	// 全店検索
			return true;
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
		gridData:[],						// 検索結果
		gridTitle:[],						// 検索結果
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
					report:			that.name,		// レポート名
					KIKAN_F:		szSelKikanF,	// 期間FROM
					TENPO:			szSelTenpo,		// 店舗
					BUMON:			szSelBumon,		// 部門
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

					var canChangeEventKikan = opts.canChangeEventKikan;

					// 登録ボタン状態変化
					$.setButtonState('#'+$.id.btn_entry, canChangeEventKikan && that.usableTenpoData(), $.id.btn_entry);
					// 検索ボタン無効化
					$.setButtonState('#'+$.id.btn_search, false, 'success');

					// 状態保存
					$.saveState2(reportno, that.getJSONString());

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
				$.messager.alert($.message.ID_MESSAGE_TITLE_WARN,"入力内容を確認してください。",'warning');
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
			for (var i=0; i<rows.length; i++){
				if($.inArray(i+'', changedIndex) !== -1){
					var rowData = {
							IDX: i+1,		// エラーメッセージ用に行番号を追加
							F1 : rows[i]["F27"],	// 隠し日付YYYYMMDD
							F2 : $("#F23_"+i).val()	// イベント
						};
					targetRows.push(rowData);
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
					report:			that.name,		// レポート名
					action:			$.id.action_update,	// 実行処理情報
					KIKAN_F:		szSelKikanF,					// 期間FROM
					TENPO:			szSelTenpo,						// 店舗
					BUMON:			szSelBumon,						// 部門
					IDX:			$($.id.hiddenChangedIdx).val(),	// 更新対象Index
					DATA:			JSON.stringify(targetRows),		// 更新対象情報
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
		},
		setData: function(rows, opts){		// データ表示
			var that = this;
			var totals = opts.totals;
			var szSelBumon		= $.getJSONObject(this.jsonString, $.id.SelBumon).value;	// 部門

			var canChangeEventKikan = opts.canChangeEventKikan;

			// 更新者
			if(opts.updUser){
				$('#nm_update').text("最終更新者："+opts.updUser);
			}

			// 入力行の一括削除
			$('tr.ctrlRow').remove();

			// 入力可フラグ
			var inputEvent = canChangeEventKikan && that.usableTenpoData()&&that.isTenpoALl();

			for(var i=0; i<rows.length; i++){
				var row = rows[i];

				var colorF8 = 'black';
				if(row['F8']*1 < 0){
					colorF8 = 'red';
				}

				var view = '';
				view +='<tr class="ctrlRow" style="height: 23px;">'+
							'<td style="text-align: center;"><span id="F1_'+i+'">'+row['F1']+'</span></td>'+
							'<td style="text-align: right;"><span id="F2_'+i+'">'+$.getWeathernews(row['F2'])+'</span></td>'+
							'<td style="text-align: right;"><span id="F3_'+i+'">'+getFormat(row['F3'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F4_'+i+'">'+getFormat(row['F4'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F5_'+i+'">'+getFormat(row['F5'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F6_'+i+'">'+getFormat(row['F6'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F7_'+i+'">'+getFormat(row['F7'], '#,##0.0')+'%'+'</span></td>'+
							'<td style="text-align: right;"><span id="F8_'+i+'">'+getFormat(row['F8'], '#,##0.0')+'%'+'</span></td>'+

							'<td style="text-align: right;"><span id="F9_'+i+'">'+getFormat(row['F9'], '#,##0.0')+'%'+'</span></td>'+
							'<td style="text-align: right;"><span id="F10_'+i+'">'+row['F10']+'</span></td>'+

							'<td style="text-align: right;"><span id="F11_'+i+'" style="color: '+colorF8+';">'+getFormat(row['F11'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F12_'+i+'">'+getFormat(row['F12'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F13_'+i+'">'+getFormat(row['F13'], '#,##0.0')+'%'+'</span></td>'+
							'<td style="text-align: right;"><span id="F14_'+i+'">'+getFormat(row['F14'], '#,##0.0')+'%'+'</span></td>'+
							'<td style="text-align: left;padding-left: 3px;"><span id="F15_'+i+'">'+getFormat(row['F15'], '#,##0.0')+'%'+'</span></td>'+
							'';
        // イベント
				if(inputEvent){
					view +=	'<td style="text-align: left;" class="yellow"><input type="text" id="F16_'+i+'" style="width:163px;" class="TextDisp" tabindex="'+(100+i)+'" value="'+row['F16']+'"></td>'+
							'<td style="text-align: right;"><span id="F17_'+i+'">'+getFormat(row['F17'], '#,##0')+'</span></td>'+
			        '<td style="text-align: center;" class="yellow"><input type="text" id="F18_'+i+'" style="width: 29px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F18'], '#0')+'"></td>'+
							'';
				}else{
					view +=	'<td style="text-align: left;"><input type="text" id="F16_'+i+'" style="width:163px;" class="TextDisp" tabindex="-1" readonly="readonly" value="'+row['F16']+'"></td>'+
							'<td style="text-align: right;"><span id="F17_'+i+'">'+getFormat(row['F17'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F18_'+i+'">'+getFormat(row['F18'], '#,##0')+'</span></td>'+
							'';
				}

				if($.getWeathernews(row['F20'])==1  ){
			    view +='<td style="text-align:center;"><span id="F19_'+i+'" style=" color: red;">☀</span></td>'
        }else if ($.getWeathernews(row['F20'])==2  ){
			   view +='<td style="text-align:center;"><span id="F19_'+i+'" style=" color: grey;">☁</span></td>'
        }else if ($.getWeathernews(row['F20'])==3  ){
			   view +='<td style="text-align:center;"><span id="F19_'+i+'" style=" color: blue;">☂</span></td>'
        }else if ($.getWeathernews(row['F20'])==4  ){
			   view +='<td style="text-align:center;"><span id="F19_'+i+'" style=" color: teal;">☃</span></td>'
        }else{
			   view +='<td style="text-align:center;"><span id="F19_'+i+'">'+$.getWeathernews(row['F20'])+'</span></td>';
	    	} ;
        if(inputEvent){
			  view +=	'<td style="text-align: center;" class="yellow"><input type="text" id="F20_'+i+'" style="width: 25px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F20'], '#0')+'"></td>'+
							'';
			  }else{
				view +=		'<td style="text-align:center;"><span id="F20_'+i+'">'+$.getWeathernews(row['F20'])+'</span></td>';
        }
				if($.getWeathernews(row['F22'])==1  ){
			    view +='<td style="text-align:center;"><span id="F21_'+i+'" style=" color: red;">☀</span></td>'
        }else if ($.getWeathernews(row['F22'])==2  ){
			   view +='<td style="text-align:center;"><span id="F21_'+i+'" style=" color: grey;">☁</span></td>'
        }else if ($.getWeathernews(row['F22'])==3  ){
			   view +='<td style="text-align:center;"><span id="F21_'+i+'" style=" color: blue;">☂</span></td>'
        }else if ($.getWeathernews(row['F22'])==4  ){
			   view +='<td style="text-align:center;"><span id="F21_'+i+'" style=" color: teal;">☃</span></td>'
        }else{
			   view +='<td style="text-align:center;"><span id="F21_'+i+'">'+$.getWeathernews(row['F22'])+'</span></td>';
	    	} ;

         if(inputEvent){
			  view +=	'<td style="text-align: center;" class="yellow"><input type="text" id="F22_'+i+'" style="width: 27px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F22'], '#0')+'"></td>'+
			  '<td style="text-align: center;" class="yellow"><input type="text" id="F23_'+i+'" style="width: 29px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F23'], '#0')+'"></td>'+
			  '<td style="text-align: center;" class="yellow"><input type="text" id="F24_'+i+'" style="width: 29px; text-align: right; ime-mode: disabled;" class="TextDisp" tabindex="'+(300+i)+'" value="'+getFormat(row['F24'], '#0')+'"></td>'+
							'';
			  }else{
				view +=	'<td style="text-align:center;"><span id="F22_'+i+'">'+$.getWeathernews(row['F22'])+'</span></td>'+
				      '<td style="text-align: right;"><span id="F23_'+i+'">'+row['F23']+'</span></td>'+
							'<td style="text-align: right;"><span id="F24_'+i+'">'+row['F24']+'</span></td>';
				}
        view +=
							'<td style="text-align: right;"><span id="F25_'+i+'">'+getFormat(row['F25'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F26_'+i+'">'+getFormat(row['F26'], '#,##0')+'</span></td>'+
							'<td style="text-align: right;"><span id="F27_'+i+'">'+getFormat(row['F27'], '#,##0.00')+'%'+'</span></td>'+
						'</tr>';

				$('#emptyRow').before(view);
			}

			// 31行目まで表示
			for(var i=rows.length+1; i<=31; i++){
				var view = '';
				view +=	'<tr class="ctrlRow" style="height: 23px;">'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'<td>&nbsp;</td>'+
							'';


				$('#emptyRow').before(view);
			}

			// 今年の要因
			if(inputEvent){
				var input = $('input[id^=F16_]');
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
							$.setChangeIdx(id.split("_")[1]);
							preVal = newVal;
						}
					});
				});
				setEnterEvent(input);
			}
			// フロア客数
			if(inputEvent){
				var input = $('input[id^=F18_]');
				input.each(function(){
					var preVal = $(this).val();
					$(this).focus(function(){
						$(this).val($(this).val());
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
							$.setChangeIdx(id.split("_")[1]);
							preVal = newVal;
							// 合計値計算
							that.setSum();
						}
					});
				});
				setEnterEvent(input);
			}
			// 天気午前
			if(inputEvent){
				var input = $('input[id^=F20_]');
				input.each(function(){
					var preVal = $(this).val().replace(/,/g, '');
					$(this).focus(function(){
						$(this).val($(this).val().replace(/,/g, ''));
						$(this).css('color', 'black');
						$(this).attr('maxlength', '1');
						$(this).select();

					}).blur(function(){
						var id = $(this).attr('id');
						var newVal = $(this).val();

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
							$.setChangeIdx(id.split("_")[1]);
							preVal = newVal;
              //天気マークの更新
							that.setWeatherMark('F19_'+id.split("_")[1]*1,newVal);
							// 合計値計算
							that.setSum();
						}
					});
				});
				setEnterEvent(input);
			}
			// 天気午後
      if(inputEvent){
				var input = $('input[id^=F22_]');
				input.each(function(){
					var preVal = $(this).val().replace(/,/g, '');
					$(this).focus(function(){
						$(this).val($(this).val().replace(/,/g, ''));
						$(this).css('color', 'black');
						$(this).attr('maxlength', '1');
						$(this).select();

					}).blur(function(){
						var id = $(this).attr('id');
						var newVal = $(this).val();

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
							$.setChangeIdx(id.split("_")[1]);
							preVal = newVal;
							// 合計値計算
							that.setSum();
							//天気マークの更新
							that.setWeatherMark('F21_'+id.split("_")[1]*1,newVal);
						}
					});
				});
				setEnterEvent(input);
			}
			// 気温午前
      if(inputEvent){
				var input = $('input[id^=F23_]');
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
							$.setChangeIdx(id.split("_")[1]);
							preVal = newVal;
							// 合計値計算
							that.setSum();
						}
					});
				});
				setEnterEvent(input);
			}
			// 気温午後
      if(inputEvent){
				var input = $('input[id^=F24_]');
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
							$.setChangeIdx(id.split("_")[1]);
							preVal = newVal;
							// 合計値計算
							that.setSum();
						}
					});
				});
				setEnterEvent(input);
			}

			for(var i=0; i<totals.length; i++){
				var row = totals[i];

				if(i < totals.length - 1){
					$('#W1_'+i).text(row['W1']);
					$('#W2_'+i).text(getFormat(row['W2'], '#,##0'));
					$('#W3_'+i).text(getFormat(row['W3'], '#,##0'));
					$('#W4_'+i).text(getFormat(row['W4'], '#,##0'));
					$('#W5_'+i).text(getFormat(row['W5'], '#,##0.0'));
					$('#W6_'+i).text(getFormat(row['W6'], '#,##0'));
					$('#W7_'+i).text(getFormat(row['W7'], '#,##0.0'));
					$('#W8_'+i).text(getFormat(row['W8'], '#,##0'));
					$('#W9_'+i).text(getFormat(row['W9'], '#,##0'));
					$('#W10_'+i).text(getFormat(row['W10'], '#,##0.0'));
					$('#W11_'+i).text(getFormat(row['W11'], '#,##0'));
					$('#W12_'+i).text(getFormat(row['W12'], '#,##0'));
					$('#W13_'+i).text(getFormat(row['W13'], '#,##0.0'));
          $('#W14_'+i).text(getFormat(row['W14'], '#,##0'));
          $('#W15_'+i).text(getFormat(row['W15'], '#,##0'));
          $('#W16_'+i).text(getFormat(row['W16'], '#,##0.0'));
					if(row['W4']*1 < 0){
						$('#W4_'+i).css('color', 'red');
					}

				} else{
					// 月間計
					$('#W1_T').text(row['W1']);
					$('#W2_T').text(getFormat(row['W2'], '#,##0'));
					$('#W3_T').text(getFormat(row['W3'], '#,##0'));
					$('#W4_T').text(getFormat(row['W4'], '#,##0'));
					$('#W5_T').text(getFormat(row['W5'], '#,##0.0'));
					$('#W6_T').text(getFormat(row['W6'], '#,##0'));
					$('#W7_T').text(getFormat(row['W7'], '#,##0.0'));
					$('#W8_T').text(getFormat(row['W8'], '#,##0'));
					$('#W9_T').text(getFormat(row['W9'], '#,##0'));
					$('#W10_T').text(getFormat(row['W10'], '#,##0.0'));
					$('#W11_T').text(getFormat(row['W11'], '#,##0'));
					$('#W12_T').text(getFormat(row['W12'], '#,##0'));
					$('#W13_T').text(getFormat(row['W13'], '#,##0.0'));
					$('#W14_T').text(getFormat(row['W14'], '#,##0'));
					$('#W15_T').text(getFormat(row['W15'], '#,##0'));
					$('#W16_T').text(getFormat(row['W16'], '#,##0.0'));

					$('#W2_T2').text(row['W2'] != '' ? getFormat(row['W2'], '#,##0') : '');
					$('#W6_T2').text(row['W6'] != '' ? getFormat(row['W6'], '#,##0') : '');
					$('#W8_T2').text(row['W8'] != '' ? getFormat(row['W8'], '#,##0') : '');
					$('#W11_T2').text(row['W11'] != '' ? getFormat(row['W11'], '#,##0') : '');
					$('#W12_T2').text(row['W12'] != '' ? getFormat(row['W12'], '#,##0') : '');
					$('#W13_T2').text(row['W13'] != '' ? getFormat(row['W13'], '#,##0.0') : '');
				  $('#W14_T2').text(row['W14'] != '' ? getFormat(row['W14'], '#,##0') : '');
				  $('#W15_T2').text(row['W14'] != '' ? getFormat(row['W15'], '#,##0') : '');
				  $('#W16_T2').text(row['W14'] != '' ? getFormat(row['W16'], '#,##0.0') : '');


					if(row['W4']*1 < 0){
						$('#W4_T').css('color', 'red');
					}
				}
			}

			// 空白をセット
			for(var i=totals.length-1; i<6; i++){
				$('#W1_'+i).text('　');
				$('#W2_'+i).text('');
				$('#W3_'+i).text('');
				$('#W4_'+i).text('');
				$('#W5_'+i).text('');
				$('#W6_'+i).text('');
				$('#W7_'+i).text('');
				$('#W8_'+i).text('');
				$('#W9_'+i).text('');
				$('#W10_'+i).text('');
				$('#W11_'+i).text('');
				$('#W12_'+i).text('');
				$('#W13_'+i).text('');
				$('#W14_'+i).text('');
				$('#W15_'+i).text('');
				$('#W16_'+i).text('');
			}
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
					// 検索ボタン有効化
					$.setButtonState('#'+$.id.btn_search, true, id);
					// 初期表示検索処理
					$.initialSearch(that);
				},
				onChange:function(newValue, oldValue){
					if(idx > 0 && that.onChangeFlag){
						if(newValue){
							$.setJSONObject(that.jsonHidden, id, newValue, newValue);
						}
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
