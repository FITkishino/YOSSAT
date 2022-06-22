$(function(){

	// backspaceキーによる「戻る」機能の無効化
	var ctrlBackSpace = function(e) {
		var code = e.which ? e.which : e.keyCode;
		if (code === 8) {
			var target = $(e.target);
			if ((!target.is('input:text') && !target.is('textarea')) || target.attr('readonly') || target.is(':disabled')) {
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
	// keydown Event (ESC無効)
	function escpress(e) {
		if(e == null) return true;
		if(document.all && event.keyCode == 27) {
			return false;
		}
		return true;
	}
	document.onkeydown = escpress;

});


function setEnterEvent(obj) {
	obj.on('keydown', function(e){
		if($(".window-mask,.datagrid-mask").length > 0){
			e.preventDefault();	// イベント中断
		}else{
			//console.log(this.id+':keydown ' + e.key);
			var code = e.which ? e.which : e.keyCode;
			//console.log($(this).attr('id') + ".keydown"+ " val=" + $(this).val() + " code=" + code + "  ref" + $(':focus').attr('id'));
			if(code === 13){	// Enter
				if(this.type !== 'textarea' && this.type !== 'submit'){
					var targets =  $("[tabindex]").filter(":visible").filter(":enabled").filter("[tabindex!=-1]").filter("[readonly!=readonly]").sort(function(a, b) {
						return $(a).attr('tabIndex')*1 - $(b).attr('tabIndex')*1;
					});
					if(targets.length < 2){
						$(this).blur();
					}else{
						var index =targets.index(this);
						var criteria = e.shiftKey ? ":lt(" + index + "):last" : ":gt(" + index + "):first";
						if(index === targets.length - 1) criteria = e.shiftKey ? criteria : ":eq(0)";
						targets.filter(criteria).focus();
					}
					e.preventDefault();
				}
			}else if(code === 90){
				e.preventDefault();	// イベント中断
			}
		}
	});
}


function getFormat(value, format, suffix){		// （必須）JSON形式の文字列
	// 数値->文字フォーマット変換
	if (undefined===value) return '';
	if (undefined==='') return '';
	if (value==='') return '';
	// 形式未指定時の初期値
	if (undefined==format) format='#,###';
	if (undefined==suffix) suffix = '';

	if(format.indexOf('+')!==-1){
		format = format.slice(1);
		if(value < 0){
			return $.formatNumber(value, {format:format, locale:"jp"}) + suffix;
		}else{
			return '+'+$.formatNumber(value, {format:format, locale:"jp"}) + suffix;
		}
	}
	return $.formatNumber(value, {format:format, locale:"jp"}) + suffix;
}




