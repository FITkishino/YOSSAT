/*
 * 作成日: 2006/08/04
 *
 */
package common;

/**
 * 定数クラス <br />
 * システムで使用する定数をまとめて定義。 <br />
 */
public final class Defines {
	/** JNDI名 */
	public final static String STR_JNDI_DS = "java:/comp/env/jdbc/bmana";

	/** JSPファイル名:ログイン画面 */
	public final static String STR_JSP_LOGIN = "/jsp/Error.jsp";
	/** JSPファイル名:エラー画面 */
	public final static String STR_JSP_ERROR = "/jsp/Error.jsp";
	/** JSPファイル名:エラー画面 */
	public final static String STR_EXCEL_ERROR = "/jsp/ExcelOutputError.jsp";

	/** クロスコンテキストに利用するリクエスト情報
	 *
	 */
	// ユーザーID
	public final static String ID_REQUEST_USER_ID			=	"uid";
	// ユーザーカスタムプロパティ
	public final static String ID_REQUEST_CUSTOM_USER		=	"cu";
	// グループカスタムプロパティ
	public final static String ID_REQUEST_CUSTOM_GROUP	=	"cg";
	// ロールカスタムプロパティ
	public final static String ID_REQUEST_CUSTOM_POS		=	"cp";
	// 分類カスタムプロパティ
	public final static String ID_REQUEST_CUSTOM_SIDE		=	"cs";
	// レポートカスタムプロパティ
	public final static String ID_REQUEST_CUSTOM_REPORT	=	"cr";
	// レポートJSP
	public final static String ID_REQUEST_JSP_REPORT		=	"jsp";
	// レポートタイトル
	public final static String ID_REQUEST_TITLE_REPORT	=	"title";
	// レポート番号
	public final static String ID_REQUEST_REPORT_NO		=	"DisplayReportNo";

	// ユーザー判定結果
	public final static String ID_REQUEST_USER_RESULT	=	"idResult";
	public final static String ID_REQUEST_MOBILE_UID	=	"mobileUID";
	public final static String ID_REQUEST_MOBILE_UCD	=	"mobileUCD";

	// 親要素からの引き継ぎ情報
	public final static String ID_REQUEST_SEND_PARAM	=	"sendParam";
	// 親要素からの引き継ぎ情報
	public final static String ID_REQUEST_SEND_MODE		=	"sendMode";

	// 初期検索条件
	public final static String ID_REQUEST_INIT_PARAM	=	"initParam";


	/** フォーム名:画面メッセージ */
	public final static String STR_FRM_MSG = "formMsg";
	/** フォーム名:画面メッセージタイプ */
	public final static String STR_FRM_MSGTYPE = "msg";

	/** フォームの値:画面メッセージ表示タイプ エラー */
	public final static String STR_VAL_MSGTYPE_ERR = "errmsg";

	// ファイル一時保存用フォルダ
	public final static String ID_UPLOAD_FILE_PATH	=	"/updata";

	/** 固定値 親要素からの引き継ぎモード */
	public enum SendMode {
		/** タブ遷移 */
		TAB("0"),
		/** その他遷移 */
		OTHER("1");

		private final String val;
		/** 初期化 */
		private SendMode(String val) {
			this.val = val;
		}
		/** @return val 値 */
		public String getVal() { return val; }
	}

}
