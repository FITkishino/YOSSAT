package common;

import java.text.MessageFormat;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import common.DefineReport.DataType;
import common.DefineReport.Option;
import net.sf.json.JSONObject;

/**
 * メッセージ関連ユーティリティクラス
 *
 * @author EATONE
 */
public class MessageUtility {
	/**
	 * コンストラクタ
	 */
	protected MessageUtility() {
		super();
	}

	/** キー:メッセージタイプ */
	public static final String TYPE = "TYPE";
	/** キー:メッセージID */
	public static final String ID = "ID";
	/** キー:メッセージ内容 */
	public static final String MSG = "MSG";

	/** 固定値:メッセージタイプ */
	public enum MsgType implements Option {
		/** エラー */
		E("E","エラー"),
		/** 正常 */
		S("S","正常"),
		/** 警告 */
		W("W","警告");

		private final String txt;
		private final String val;

		/** 初期化 */
		private MsgType(String val, String txt) {
			this.val = val;
			this.txt = txt;
		}
		/** @return val 値 */
		public String getVal() { return val; }
		/** @return txt 表示名称 */
		public String getTxt() { return txt; }
	}

	/** 固定値:メッセージ格納キー */
	public enum MsgKey {
		/** エラー */
		E("E_MSG"),
		/** 正常 */
		S("S_MSG"),
		/** 警告 */
		W("W_MSG");

		private final String key;
		/** 初期化 */
		private MsgKey(String key) {
			this.key = key;
		}
		/** @return val 値 */
		public String getKey() { return key; }
	}

	/** フィールドタイプ */
	public enum FieldType {
		/** デフォルト */
		DEFAULT("1"),
		/** グリッド内データ */
		GRID("2");

		/** ID */
		private final String id;

		/** 初期化 */
		FieldType(String id) {
			this.id = id;
		}
		/** @return id */
		public String getId() {
			return id;
		}
	}

	/** 固定値:メッセージ一覧 */
	public enum Msg implements Option {
		/** 更新完了 */
		S00001("S00001","更新が完了しました。"),
		/** 削除完了 */
		S00002("S00002","削除が完了しました。"),
		/** 更新完了(件数あり) */
		S00003("S00003","{0}件中 {1}件を更新しました。"),
		/** 削除完了(件数あり) */
		S00004("S00004","{0}件削除しました。"),
		/** 更新完了(件数あり) */
		S00005("S00005","{0}件により {1}件更新しました。"),
		/** 更新完了(件数あり) */
		S00006("S00006","{0}を {1}件更新しました。"),
		/** 更新完了(件数なし) */
		S00007("S00007","{0}を更新しました。"),
		/** 警告：検索結果件数オーバー */
		W00001("W00001","検索結果件数が{0}件を超えました。検索条件の絞込みを行ってください。"),
		/** 更新処理失敗 */
		E00001("E00001","更新処理に失敗しました。"),
		/** 削除処理失敗 */
		E00002("E00002","削除処理に失敗しました。"),
		/** 更新対象無し */
		E10000("E10000","更新対象データはありません。"),
		/** 必須指定 */
		E10001("E10001","{0}は、必須指定です。"),
		/** 文字数 */
		E10002("E10002","{0}は、{1}文字以下で入力してください。"),
		/** 半角数字文字数 */
		E10003("E10003","{0}は、{1}文字以下の半角数字で入力してください。"),
		/** 小数文字数 */
		E10004("E10004","{0}は、整数部{1}文字、小数部{2}文字以下の半角数字で入力してください。"),
		/** 日付形式 */
		E10005("E10005","{0}は、正しい日付を、西暦（{1}）で入力してください。"),
		/** 汎用 */
		E10006("E10006","{0}は、{1}で入力してください。"),
		/** E20001 */
		E20001("E20001","{1}件目にエラーがあります。{0}は、必須指定です。"),
		/** E20002 */
		E20002("E20002","{2}件目にエラーがあります。{0}は、{1}文字以下を指定してください。"),
		/** E20003 */
		E20003("E20003","{2}件目にエラーがあります。{0}は、{1}文字以下の半角数字を指定してください。"),
		/** E20004 */
		E20004("E20004","{3}件目にエラーがあります。{0}は、整数部{1}文字、小数部{2}文字以下の半角数字を指定してください。"),
		/** E20005 */
		E20005("E20005","{2}件目にエラーがあります。{0}は、正しい日付を、西暦（{1}）で入力してください。"),
		/** 汎用 */
		E20006("E20006","{2}件目にエラーがあります。{0}は、{1}で入力してください。"),
		;

		private final String txt;
		private final String val;

		/** 初期化 */
		private Msg(String val, String txt) {
			this.val = val;
			this.txt = txt;
		}
		/** @return val 値 */
		public String getVal() { return val; }
		/** @return txt 表示名称 */
		public String getTxt() { return txt; }
	}

	/** メッセージ取得 */
	public static String getMessage(String key) {
		return Msg.valueOf(key).getTxt();
	}
	public static String getMessage(String key, String... args) {
		return MessageFormat.format(Msg.valueOf(key).getTxt(), (Object[]) args);
	}

	public static JSONObject getMessageObj(MsgType type, String key, String... args) {
		JSONObject obj = new JSONObject();
		obj.put(TYPE, type.getVal());
		obj.put(ID, key);
		obj.put(MSG, getMessage(key,args));
		return obj;
	}

	/**
	 * メッセージを戻します。
	 *
	 * @param messageId
	 *			メッセージID
	 * @param addParam
	 *			メッセージに付加するパラメータ
	 * @return メッセージ
	 */
	public static JSONObject getMessageObj(String messageId, String... args) {
		MsgType type = MsgType.valueOf(StringUtils.left(messageId,1));
		return getMessageObj(type, messageId, args);
	}

	/**
	 * フィールドタイプに応じた必須入力エラーメッセージを戻します。
	 *
	 * @param field
	 *			フィールド
	 * @param fieldType
	 *			フィールドタイプ
	 * @param addParam
	 *			メッセージに付加するパラメータ
	 * @return 必須入力エラーメッセージ
	 */
	public static JSONObject getCheckNullMessage(String lbl, FieldType fieldType, String... addParam) {
		String messageId = Msg.valueOf(MsgType.E.getVal()+fieldType.getId()+"0001").getVal();
		String[] param = new String[]{ lbl };
		param = (String[]) ArrayUtils.addAll(param, addParam);
		return getMessageObj(MsgType.E, messageId, param);
	}

	/**
	 * フィールドタイプに応じた文字数超過エラーメッセージを戻します。
	 *
	 * @param field
	 *			フィールド
	 * @param fieldType
	 *			フィールドタイプ
	 * @param addParam
	 *			メッセージに付加するパラメータ
	 * @return 文字数超過エラーメッセージ
	 */
	public static JSONObject getCheckMaxLengthMessage(DefineReport.InpText field,
			FieldType fieldType, String... addParam) {
		String messageId = "";
		String[] param = null;
		if(DataType.INTEGER.equals(field.getType())){
			messageId = Msg.valueOf(MsgType.E.getVal()+fieldType.getId()+"0003").getVal();
			param = new String[]{ field.getTxt(), String.valueOf(field.getDigit1()) };
		}else if(DataType.DECIMAL.equals(field.getType())){
			messageId = Msg.valueOf(MsgType.E.getVal()+fieldType.getId()+"0004").getVal();
			param = new String[]{ field.getTxt(), String.valueOf(field.getDigit1()), String.valueOf(field.getDigit2()) };
		}else{
			messageId = Msg.valueOf(MsgType.E.getVal()+fieldType.getId()+"0002").getVal();
			param = new String[]{ field.getTxt(), String.valueOf(field.getDigit1()) };
		}
		param = (String[]) ArrayUtils.addAll(param, addParam);
		return getMessageObj(MsgType.E, messageId, param);
	}


	/**
	 * フィールドタイプに応じた数値チェックエラーメッセージを戻します。
	 *
	 * @param field
	 *			フィールド
	 * @param fieldType
	 *			フィールドタイプ
	 * @param addParam
	 *			メッセージに付加するパラメータ
	 * @return 数値チェックエラーメッセージ
	 */
	public static JSONObject getCheckZeroOrPlusIntegerMessage(DefineReport.InpText field,
			FieldType fieldType, String... addParam) {
		String messageId = Msg.valueOf(MsgType.E.getVal()+fieldType.getId()+"0006").getVal();
		String[] param =  new String[]{ field.getTxt(), "ゼロ以上の整数" };
		param = (String[]) ArrayUtils.addAll(param, addParam);
		return getMessageObj(MsgType.E, messageId, param);
	}
}
