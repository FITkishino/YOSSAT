package common;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class DefineReport {

  /**
   * 開発関連
   */
  /* デバッグ(true:開発時、false:リリース時 */
  public final static boolean ID_DEBUG_MODE = true;

  /**
   * PARAMETER 関連
   */
  /* パラメータ：ページ番号 */
  public final static String ID_PARAM_PAGE = "page";
  /* パラメータ：オブジェクト番号 */
  public final static String ID_PARAM_OBJ = "obj";
  /* パラメータ：選択値 */
  public final static String ID_PARAM_VAL = "sel";
  /* パラメータ：JSON値 */
  public final static String ID_PARAM_JSON = "json";
  /* パラメータ：アクション */
  public final static String ID_PARAM_ACTION = "action";
  /* パラメータ：ターゲット */
  public final static String ID_PARAM_TARGET = "target";

  /* パラメータ：最大取得件数 */
  public final static String ID_SEARCHJSON_PARAM_MAXROW = "maxRows";
  /* パラメータ：検索キー */
  public final static String ID_SEARCHJSON_PARAM_NAMEWITH = "q";

  /* パラメータ：アクション 初期値 */
  public final static String ID_PARAM_ACTION_DEFAULT = "run"; // 検索実行
  public final static String ID_PARAM_ACTION_GET = "get"; // 通常通信
  public final static String ID_PARAM_ACTION_INIT = "init"; // 初期化
  public final static String ID_PARAM_ACTION_ITEMS = "items"; // 商品入力
  public final static String ID_PARAM_ACTION_TENPO = "tenpo"; // 店舗グループ
  public final static String ID_PARAM_ACTION_SHIORI = "shiori"; // 定義保存
  public final static String ID_PARAM_ACTION_STORE = "store"; // メンテナンス保存
  public final static String ID_PARAM_ACTION_AUTOQUERY = "autoQuery"; // 自動検索
  public final static String ID_PARAM_ACTION_UPDATE = "update"; // 更新処理
  public final static String ID_PARAM_ACTION_DELETE = "delete"; // 削除処理
  public final static String ID_PARAM_ACTION_CHECK = "check"; // チェック処理

  // JSON：メンバー：実値
  public final static String ID_JSON_VALUE = "value";
  // JSON：メンバー：表示値
  public final static String ID_JSON_TEXT = "text";

  /* パラメータ：オプション情報(title) */
  public final static String ID_PARAM_OPT_TITLE = "title";
  /* パラメータ：オプション情報(header) */
  public final static String ID_PARAM_OPT_HEAD = "header";
  /* パラメータ：オプション情報(footer) */
  public final static String ID_PARAM_OPT_FOOT = "footer";

  /**
   * ページ番号
   */
  /** ページ番号 商品マスタ検索 */
  public final static String ID_PAGE_ITEM = "ItemSets";
  /** 画面ID：日別予算作成画面 */
  public final static String ID_PAGE_001 = "Out_Report001";
  /** 画面ID：売上予算管理表 */
  public final static String ID_PAGE_002 = "Out_Report002";
  /** 画面ID：再按分管理画面 */
  public final static String ID_PAGE_003 = "Out_Report003";
  /** 画面ID：基準日設定画面 */
  public final static String ID_PAGE_004 = "Out_Report004";
  /** 画面ID：新店管理画面 */
  public final static String ID_PAGE_005 = "Out_Report005";
  /** 画面ID：店長確認画面 */
  public final static String ID_PAGE_006 = "Out_Report006";



  /** 画面ID：平均日商 */
  public final static String ID_PAGE_007 = "Out_Report007";
  /** 画面ID：売上仕入 */
  public final static String ID_PAGE_011 = "Out_Report011";
  /** 画面ID：催し実績 */
  public final static String ID_PAGE_012 = "Out_Report012";
  /** 画面ID：利益管理表（原価） */
  public final static String ID_PAGE_014 = "Out_Report014";
  /** 画面ID：利益管理表（売価） */
  public final static String ID_PAGE_015 = "Out_Report015";
  /** 画面ID：人時売上 */
  public final static String ID_PAGE_016 = "Out_Report016";
  /** 画面ID：人時生産性レポート */
  public final static String ID_PAGE_017 = "Out_Report017";
  /** 画面ID：損益計算書（店舗） */
  public final static String ID_PAGE_019 = "Out_Report019";
  /** 画面ID：損益計算書（本部） */
  public final static String ID_PAGE_020 = "Out_Report020";
  /** 画面ID：営業レポート */
  public final static String ID_PAGE_021 = "Out_Report021";
  /** 画面ID：営業成績レポート */
  public final static String ID_PAGE_022 = "Out_Report022";
  /** 画面ID：入荷実績 */
  public final static String ID_PAGE_023 = "Out_Report023";
  /** 画面ID：構成頁 */
  public final static String ID_PAGE_025 = "Out_Report025";
  /** 画面ID：期首在庫修正 */
  public final static String ID_PAGE_026 = "Out_Report026";
  /** 画面ID：品切回数 */
  public final static String ID_PAGE_027 = "Out_Report027";
  /** 画面ID：予算入力 */
  public final static String ID_PAGE_028 = "Out_Report028";
  /** 画面ID：汎用分析 */
  public final static String ID_PAGE_029 = "Out_Report029";
  /** 画面ID：取引先・メーカー・構成分析 */
  public final static String ID_PAGE_030 = "Out_Report030";
  /** 画面ID：経営会議報告用営業分析 */
  public final static String ID_PAGE_032 = "Out_Report032";
  /** 画面ID：月次売上実績 */
  public final static String ID_PAGE_040 = "Out_Report040";
  /** 画面ID：ランキング */
  public final static String ID_PAGE_041 = "Out_Report041";
  /** 画面ID：社員名簿 */
  public final static String ID_PAGE_042 = "Out_Report042";

  public final static String ID_PAGE_101 = "Out_Report101";
  public final static String ID_PAGE_102 = "Out_Report102";
  public final static String ID_PAGE_103 = "Out_Report103";
  public final static String ID_PAGE_104 = "Out_Report104";
  public final static String ID_PAGE_105 = "Out_Report105";
  public final static String ID_PAGE_106 = "Out_Report106";
  public final static String ID_PAGE_107 = "Out_Report107";
  public final static String ID_PAGE_108 = "Out_Report108";
  public final static String ID_PAGE_MAINTAIN_001 = "Out_Maintain_001";

  /** RFM系ページ情報 */
  public final static String[] ID_PAGE_RFM = new String[] {ID_PAGE_101, ID_PAGE_102, ID_PAGE_103, ID_PAGE_104, ID_PAGE_105, ID_PAGE_106};

  public static boolean isRfmPage(String page) {
    return ArrayUtils.contains(ID_PAGE_RFM, page);
  }

  /** PL系ページ情報 */
  public final static String[] ID_PAGE_PL = new String[] {ID_PAGE_017, ID_PAGE_019, ID_PAGE_020, ID_PAGE_021, ID_PAGE_022, ID_PAGE_041};

  public static boolean isPLPage(String page) {
    return ArrayUtils.contains(ID_PAGE_PL, page);
  }

  /** 閉店店舗表示画面 */
  public final static String[] ID_PAGE_DISP_CLOSED = new String[] {ID_PAGE_002, ID_PAGE_006};

  public static boolean isPastPage(String page) {
    return ArrayUtils.contains(ID_PAGE_DISP_CLOSED, page);
  }


  /** 子画面 */
  public final static String[] ID_PAGE_CHILD = new String[] {ID_PAGE_ITEM};

  /** 子画面か否か */
  public static boolean isChildPage(String page) {
    return ArrayUtils.contains(ID_PAGE_CHILD, page);
  }


  /** 特殊ページ情報:パスワード */
  public final static String ID_ADMIN_PASS_HEAD_042 = "ina";


  /**
   * HTML 関連
   */
  public final static String LBL_SUFFIX = " ：";

  /** HTML関連 ボタン */
  public enum Button {
    /** 検索 */
    SEARCH("btn_Search", "F6=検索"),
    /** Excel */
    EXCEL("btn_Excel", "F7=Excel"),
    /** 商品入力 */
    INPUT("btn_Input", ""),
    /** 店舗グループ入力 */
    INPUT_TENPOG("btn_Input_TenpoG", ""),
    /** 保存 */
    ENTRY("btn_entry", "保存"),
    /** 保存（サブ業態メンテナンス） */
    ENTRY_SUBGYOTAI("btn_entry_subGyotai", "保存"),
    /** 保存（定義保存） */
    ENTRY_SHIORI("btn_entry_shiori", "保存"),
    /** 保存（店舗グループ） */
    ENTRY_TENPOG("btn_entry-tg", "保存"),
    /** 呼出 */
    CALL("btn_call", "呼出"),
    /** 呼出（店舗グループ） */
    CALL_TENPOG("btn_call-tg", "呼出"),
    /** 削除 */
    DELETE("btn_delete", "削除"),
    /** 削除（店舗グループ） */
    DELETE_TENPOG("btn_delete-tg", "削除"),
    /** 削除（定義保存） */
    DELETE_SHIORI("btn_delete_shiori", "削除"),
    /** 適用（定義保存） */
    VIEW_SHIORI("btn_view_shiori", "適用"),
    /** 条件リセット */
    RESET("btn_reset", "戻る"),
    /** (特殊ページ)ログイン */
    LOGIN("btn_login", "ログイン"),
    /** キャンセル */
    CANCEL("btn_cancel", "キャンセル"),
    /** アップロード */
    UPLOAD("btn_upload", "アップロード"),
    /** ダウンロード */
    DOWNLOAD("btn_download", "ダウンロード"),
    /** 設定 */
    SET("btn_set", "一括設定"),
    /** 設定 */
    ANBUN("btn_anbun", "按分実行");

    private final String obj;
    private final String txt;

    /** 初期化 */
    private Button(String obj, String txt) {
      this.obj = obj;
      this.txt = txt;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リスト */
  public enum Select {
    /** 期間 */
    KIKAN("SelKikan", "期間"),
    /** 期間FROM */
    KIKAN_F("SelKikanF", "開始"),
    /** 期間TO */
    KIKAN_T("SelKikanT", "終了"),
    /** 年月日FROM */
    YMD_F("SelYmdF", "年月日"),
    /** 年月日TO */
    YMD_T("SelYmdT", "年月日"),
    /** 年月FROM */
    YM_F("SelYmF", "年月"),
    /** 年月TO */
    YM_T("SelYmT", "年月"),
    /** 年FROM */
    YEAR_F("SelYearF", "年"),
    /** 年TO */
    YEAR_T("SelYearT", "年"),
    /** 週FROM */
    WEEK_F("SelWeekF", "週"),
    /** 週TO */
    WEEK_T("SelWeekT", "週"),
    /** 年度FROM */
    FYEAR_F("SelFYearF", "年度"),
    /** 年度TO */
    FYEAR_T("SelFYearT", "年度"),
    /** 比較期間FROM */
    KIKAN_F2("SelKikanF2", "開始"),
    /** 期間TO */
    KIKAN_T2("SelKikanT2", "終了"),
    /** 比較比較年月日FROM */
    YMD_F2("SelYmdF2", "比較年月日"),
    /** 比較年月日TO */
    YMD_T2("SelYmdT2", "比較年月日"),
    /** 比較年月FROM */
    YM_F2("SelYmF2", "比較年月"),
    /** 比較年月TO */
    YM_T2("SelYmT2", "比較年月"),
    /** 比較年FROM */
    YEAR_F2("SelYearF2", "比較年"),
    /** 比較年TO */
    YEAR_T2("SelYearT2", "比較年"),
    /** 比較週FROM */
    WEEK_F2("SelWeekF2", "比較週"),
    /** 比較週TO */
    WEEK_T2("SelWeekT2", "比較週"),
    /** 比較年度FROM */
    FYEAR_F2("SelFYearF2", "年度"),
    /** 比較年度TO */
    FYEAR_T2("SelFYearT2", "年度"),
    /** 業態 */
    GYOTAI("SelGyotai", "業態"),
    /** サブ業態 */
    GYOTAI_SUB("SelGyotaiSub", "サブ業態"),

    /** 店舗グループ */
    TENPO_G("SelTenpoG", "種別"),
    /** 店舗 */
    TENPO("SelTenpo", "店舗"),
    /** 事業部 */
    JIGYO("SelJigyo", "事業部"),

    /** 比較対象店舗（モデル店舗） */
    M_TENPO("SelMTenpo", "比較対象店舗"),


    /** 企業 */
    KIGYO("SelKigyo", "企業"),
    /** 販売統括部門 */
    HANTOUBU("SelHanToubu", ""),
    /** 販売部 */
    HANBAIBU("SelHanbaibu", "販売部"),
    /** 部門グループ */
    BUMON_G("SelBumonG", "部門グ"),
    /** 部門 */
    BUMON("SelBumon", "部門"),
    /** 大分類 */
    DAI_BUN("SelDaiBun", "大分類"),
    /** 中分類 */
    TYU_BUN("SelTyuBun", "中分類"),
    /** 小分類 */
    SYO_BUN("SelSyoBun", "小分類"),

    /** 本部 */
    HONBU("SelHonbu", "本部"),
    /** 所属 */
    SYOZOKU("SelSyozoku", "所属"),


    /** ライン */
    LINE("SelLine", "ライン"),
    /** クラス */
    CLASS("SelClass", "クラス"),
    /** 商品 */
    SYOHIN("SelSyohin", "商品"),
    /** 条件 */
    WHERE("SelWhere", ""),
    /** 商品カテゴリ */
    CATEGORY("SelCategory", "商品カテゴリ"),

    /** 仕入先 */
    SHIRE("SelShire", "仕入先"),
    /** メーカ */
    MAKER("SelMaker", "メーカ"),
    /** 担当者 */
    TANTO("SelTanto", "担当者"),
    /** 構成頁 */
    KPAGE("SelKpage", "構成頁"),

    /** 表側1 */
    KBN1("SelHyosoku1", "縦軸1"),

    /** 表側1 */
    HYOSOKU1("SelHyosoku1", "縦軸1"),
    /** 表側2 */
    HYOSOKU2("SelHyosoku2", "縦軸2"),
    /** 表列 */
    HYORETSU("SelHyoretsu", "横軸"),
    /** 集計方法-期間 */
    SYUKEI_KI("SelSyukeiKi", "期間"),
    /** 集計方法-店舗 */
    SYUKEI_TEN("SelSyukeiTen", "店舗"),
    /** 集計方法-分類 */
    SYUKEI_BUN("SelSyukeiBun", "分類"),
    /** 出力項目 */
    OUTPUT("SelOutput", "表示項目"),
    /** 表示順項目 */
    ORDER("SelOrder", "表示順項目"),
    /** 集計単位 */
    SYUKEI("SelSyukei", "集計単位"),
    /** 催し */
    MOYOSHI("SelMoyoshi", "催し"),
    /** 抽出 */
    SUB_QUERY("SelSubQuery", "抽出"),
    /** 催しコード */
    SUB_MOYOSHI_KB("SelMoyoshiKB", "催し区分"),
    /** 商品コード */
    SUB_SYOHIN_KB("SelSyohinKB", "商品区分"),
    /** 定義保存 */
    SHIORI("SelShiori", "定義保存"),
    /** ランク基準 */
    KIJUN_RANK("SelKijunRank", "ランク基準"),
    /** 分析軸 */
    JIKU("SelJiku", "分析軸"),
    /** Rランク */
    RANK_R("SelRankR", "Rランク"),
    /** Fランク */
    RANK_F("SelRankF", "Fランク"),
    /** Mランク */
    RANK_M("SelRankM", "Mランク"),
    /** 年齢層 */
    AGE("SelAge", "年齢層"),
    /** 取引コード */
    TORICODE("SelToriCode", "取引コード"),
    /** 基準 */
    KIJUN("SelKijun", "基準"),
    /** 対比率 */
    TAIHIRITSU("SelTaihi", "対比率"),

    /** 科目グループ(店舗) */
    KAMOKU_G_TEN("SelKamokuGTen", "科目"),
    /** 科目グループ(本部) */
    KAMOKU_G_HON("SelKamokuGHon", "科目"),

    /** 伝票明細(店舗) */
    KAMOKU_D_TEN("SelKamokuDTen", "経費明細"),
    /** 伝票明細(本部) */
    KAMOKU_D_HON("SelKamokuDHon", "経費明細"),

    /** ランキング項目 */
    RANK("SelRank", "ランキング項目"),
    /** 販売区分 */
    HANBAI_KBN("SelHanbaiKbn", "販売区分"),
    /** 比較値 */
    HIKAKU("SelHikaku", "比較値"),

    /** 区分表示 */
    KBN_HYOJI("SelKbnHyoji", "区分表示"),

    /** 職種 */
    SYOKUSYU("SelSyokusyu", "職種");

    private final String obj;
    private final String txt;

    /** 初期化 */
    private Select(String obj, String txt) {
      this.obj = obj;
      this.txt = txt;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 テキスト */
  public enum Text {
    /** 件数 */
    NUMBER("TxtNumber", "件数"),
    /** AランクFROM */
    RANK_A_F("TxtRankAF", "Aランク"),
    /** AランクTO */
    RANK_A_T("TxtRankAT", "Aランク"),
    /** BランクFROM */
    RANK_B_F("TxtRankBF", "Bランク"),
    /** BランクTO */
    RANK_B_T("TxtRankBT", "Bランク"),
    /** CランクFROM */
    RANK_C_F("TxtRankCF", "Cランク"),
    /** CランクTO */
    RANK_C_T("TxtRankCT", "Cランク"),
    /** AランクTO2 */
    RANK_A_T2("TxtRankAT2", "Aランク"),
    /** BランクFROM2 */
    RANK_B_F2("TxtRankBF2", "Bランク"),
    /** BランクTO2 */
    RANK_B_T2("TxtRankBT2", "Bランク"),
    /** CランクFROM2 */
    RANK_C_F2("TxtRankCF2", "Cランク"),
    /** CランクTO2 */
    RANK_C_T2("TxtRankCT2", "Cランク"),
    /** 選択ランクTO2 */
    RANK1("TxtRank", "表示ランク"),
    /** 選択ランク2 */
    RANK2("TxtRank2", "表示ランク"),
    /** Rランク1 */
    RANK_R1("TxtRankR1", "ランク1"),
    /** Rランク2 */
    RANK_R2("TxtRankR2", "ランク2"),
    /** Rランク3 */
    RANK_R3("TxtRankR3", "ランク3"),
    /** Rランク4 */
    RANK_R4("TxtRankR4", "ランク4"),
    /** Rランク5 */
    RANK_R5("TxtRankR5", "ランク5"),
    /** Fランク1 */
    RANK_F1("TxtRankF1", "ランク1"),
    /** Fランク2 */
    RANK_F2("TxtRankF2", "ランク2"),
    /** Fランク3 */
    RANK_F3("TxtRankF3", "ランク3"),
    /** Fランク4 */
    RANK_F4("TxtRankF4", "ランク4"),
    /** Fランク5 */
    RANK_F5("TxtRankF5", "ランク5"),
    /** Mランク1 */
    RANK_M1("TxtRankM1", "ランク1"),
    /** Mランク2 */
    RANK_M2("TxtRankM2", "ランク2"),
    /** Mランク3 */
    RANK_M3("TxtRankM3", "ランク3"),
    /** Mランク4 */
    RANK_M4("TxtRankM4", "ランク4"),
    /** Mランク5 */
    RANK_M5("TxtRankM5", "ランク5"),

    /** 矢印（↑） */
    RANK_A1("TxtRankA1", "↑"),
    /** 矢印（／） */
    RANK_A2("TxtRankA2", "／"),
    /** 矢印（→） */
    RANK_A3("TxtRankA3", "→"),
    /** 矢印（＼） */
    RANK_A4("TxtRankA4", "＼"),
    /** 矢印（↓） */
    RANK_A5("TxtRankA5", "↓"),
    /** 矢印（↑） */
    RANK_A1C("TxtRankA1C", "↑"),
    /** 矢印（／） */
    RANK_A2C("TxtRankA2C", "／"),
    /** 矢印（→） */
    RANK_A3C("TxtRankA3C", "→"),
    /** 矢印（＼） */
    RANK_A4C("TxtRankA4C", "＼"),
    /** 矢印（↓） */
    RANK_A5C("TxtRankA5C", "↓"),

    /** 販売本部 */
    HANBAI_LABEL("TxtHanSei", "販売本部"),

    /** ダイアログ内パスワード入力欄 */
    PASS("TxtPass", ""),

    /** 部門荒利率 */
    BMN_ARA_RIT("TxtBmnAraRit", "部門荒利率"),
    /** 係数予算 */
    KEISU_YOS("TxtKeisuYos", "係数予算"),
    /** 係数客数 */
    KEISU_KYK("TxtKeisuKyk", "係数客数");

    private final String obj;
    private final String txt;

    /** 初期化 */
    private Text(String obj, String txt) {
      this.obj = obj;
      this.txt = txt;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }
  }

  /** データ型 */
  public enum DataType {
    /** 単一行テキスト */
    SINGLE_LINE_TEXT,
    /** 複数行テキスト */
    MULTI_LINE_TEXT,
    /** 英数字 */
    ALPHA,
    /** 整数 */
    INTEGER,
    /** 小数 */
    DECIMAL,
    /** 日付 */
    DATE;

    /**
     * 指定されたデータ型がテキストかどうかを戻します。
     *
     * @param dataType データ型
     * @return true：テキスト false：その他
     */
    public static boolean isText(DataType dataType) {
      return SINGLE_LINE_TEXT.equals(dataType) || MULTI_LINE_TEXT.equals(dataType) || ALPHA.equals(dataType);
    }
  }

  /** HTML関連 テキスト(入力用) */
  public enum InpText {
    /** 基準日 */
    KIJUN_DT("TxtKijunDt", "基準日", DataType.DATE, 10, 0),
    /** 今年の要因(日単位) */
    EVENT_DD("TxtEventDd", "今年の要因", DataType.SINGLE_LINE_TEXT, 1000, 0),
    /** 今年の要因(月単位) */
    EVENT_MM("TxtEventMm", "予算組みポイント", DataType.MULTI_LINE_TEXT, 1000, 0),
    /** 按分予算 */
    AYOSAN("TxtAYosan", "修正予算", DataType.INTEGER, 9, 0),
    /** 店長予算 */
    TYOSAN("TxtTYosan", "店長予算案", DataType.INTEGER, 9, 0),
    /** 予測客数 */
    KYAKUSU("TxtKyakusu", "予測客数", DataType.INTEGER, 7, 0);

    private final String obj;
    private final String txt;
    private final DataType type;
    private final int digit1;
    private final int digit2;

    /** 初期化 */
    private InpText(String obj, String txt, DataType type, int digit1, int digit2) {
      this.obj = obj;
      this.txt = txt;
      this.type = type;
      this.digit1 = digit1;
      this.digit2 = digit2;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }

    /** @return txt データ型 */
    public DataType getType() {
      return type;
    }

    /** @return obj 桁数1 */
    public int getDigit1() {
      return digit1;
    }

    /** @return txt 桁数2 */
    public int getDigit2() {
      return digit2;
    }

    /** @return len 桁数 */
    public int getLen() {
      return digit1 + digit2;
    }

    /** @return lbl ラベル */
    public String getLbl() {
      return txt + LBL_SUFFIX;
    }
  }

  /** HTML関連 ラベル */
  public enum Label {
    /** 分析期間 */
    KIKAN1("分析期間"),
    /** 比較期間 */
    KIKAN2("比較期間"),
    /** 特定日 */
    TKT_DT("特定日"),
    /** 基準日 */
    KJN_DT("基準日"),
    /** 開始年月 */
    OPN_YM("開店年月"),

    /** 伸び率評価基準 */
    KIJUN1("伸び率評価基準"),
    /** 注意書き */
    CAPTION1("※無制限の場合、0指定"),
    /** 小計表示 */
    KEI("小計"),
    /** 累計 */
    RUIKEI("累計"),
    /** 職種 */
    SYOKUSYU("職種"),
    /** 中計 */
    CHUUKEI("中計"),
    /** 小計 */
    SYOUKEI("小計"),
    /** パスワード認証 */
    PASS("パスワード認証"),
    /** 登録 */
    REGIST("登録"),
    /** 詳細表示 */
    SYS("詳細"),

    /** 新店舗 */
    SIN_TEN("新店舗");

    private final String txt;

    /** 初期化 */
    private Label(String txt) {
      this.txt = txt;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 チェックボックス */
  public enum Checkbox {
    /** 定義なし */
    NONE("", ""),
    /** 計表示 */
    KEI("ChkKei", "表示"),
    /** 既存店 */
    KIZ("ChkKiz", "既存店"),
    /** 単日 */
    TAN("ChkTan", "単日"),
    /** 累計 */
    RUI("ChkRui", "累計"),
    /** 定義保存の共有 */
    SHIORI("ChkShiori", "共有"),
    /** 詳細表示 */
    SYS("ChkSys", "詳細表示");

    private final String obj;
    private final String txt;

    /** 初期化 */
    private Checkbox(String obj, String txt) {
      this.obj = obj;
      this.txt = txt;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション */
  public interface Option {
    public String getVal();

    public String getTxt();
  }

  public static String getOptions(Option[] enumCls) {
    return getOptions(enumCls, new String[0]);
  }

  public static String getOptions(Option[] enumCls, String... excludeValues) {
    String rtn = "";
    for (Option opt : enumCls) {
      // 除外するリストに含まれていた場合、リストに追加しない
      if (ArrayUtils.contains(excludeValues, opt.getVal())) {
        continue;
      }
      rtn += "<option value=\"" + opt.getVal() + "\">" + opt.getTxt() + "</option>";
    }
    return rtn;
  }

  public static String getOptionDatas(Option[] enumCls) {
    String rtn = "";
    for (Option opt : enumCls) {
      rtn += "{VALUE:'" + opt.getVal() + "',TEXT:'" + opt.getTxt() + "'},";
    }
    return "data:[" + StringUtils.removeEnd(rtn, ",") + "]";
  }

  public static Option getOptionFromText(Option[] enumCls, String text) {
    for (Option opt : enumCls) {
      if (StringUtils.equals(text, opt.getTxt())) {
        return opt;
      }
    }
    return null;
  }

  public static Option getOptionFromValue(Option[] enumCls, String value) {
    for (Option opt : enumCls) {
      if (StringUtils.equals(value, opt.getVal())) {
        return opt;
      }
    }
    return null;
  }

  /** HTML関連 選択リストオプション(期間-月週日) */
  public enum OptionKikan implements Option {
    /** 日 */
    DAY("3", "日"),
    /** 週 */
    WEEK("2", "週"),
    /** 月 */
    MONTH("1", "月");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionKikan(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション(期間-月日) */
  public enum OptionKikanMD implements Option {
    /** 日 */
    DAY("3", "日"),
    /** 月 */
    MONTH("1", "月");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionKikanMD(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション(期間-年度月週日) */
  public enum OptionKikanYMWD implements Option {
    /** 日 */
    DAY("3", "日"),
    /** 週 */
    WEEK("2", "週"),
    /** 月 */
    MONTH("1", "月"),
    /** 年度 */
    FYEAR("4", "年度");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionKikanYMWD(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション(期間-年度月) */
  public enum OptionKikanYM implements Option {
    /** 月 */
    MONTH("1", "月"),
    /** 年度 */
    FYEAR("4", "年度");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionKikanYM(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション(期間-月) */
  public enum OptionKikanM implements Option {
    /** 月 */
    MONTH("1", "月");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionKikanM(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション(条件) */
  public enum OptionWhere implements Option {
    /** 分類 */
    BUNRUI("1", "分類"),
    /** 商品カテゴリ */
    CATEGORY("9", "商品カテゴリ");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionWhere(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（集計方法） */
  public enum OptionSyukei implements Option {
    /** 集計方法-期間 */
    NONE("01", "なし"), KIKAN("02", "時系列"), TENPO("03", "店舗別"), KIKAN_TENPO("04", "時系列＋店舗別");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionSyukei(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（抽出） */
  public enum OptionSubQuery implements Option {
    /** 抽出 */
    NONE("1", "なし"), MOYOSHI("2", "催しコード"), MOYOSHIKB("3", "催し区分"), SYOHINKB("4", "商品区分");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionSubQuery(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（集計方法(店舗)） */
  public enum OptionSyukeiTenpo implements Option {
    /** 集計方法-店舗 */
    ID("02", "明細");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionSyukeiTenpo(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（集計単位(部門)） */
  public enum OptionSyukeiBumon implements Option {
    /** 部門 */
    BUMON("1", "部門"),
    /** ライン */
    LINE("2", "ライン"),
    /** クラス */
    CLASS("3", "クラス"),
    /** 商品 */
    SYOHIN("4", "商品"),
    /** 商品カテゴリ */
    CATEGORY("9", "商品カテゴリ");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionSyukeiBumon(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（集計単位(部門)） */
  public enum OptionSyukeiBumon2 implements Option {
    /** 部門 */
    BUMON("1", "部門"),
    /** ライン */
    LINE("2", "ライン"),
    /** クラス */
    CLASS("3", "クラス");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionSyukeiBumon2(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（表示順項目） */
  public enum OptionOrder implements Option {
    /** 金額 */
    ITEM1("1", "金額"),
    /** 点数 */
    ITEM2("2", "点数");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionOrder(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（ABCランク） */
  public enum OptionABCRank implements Option {
    /** すべて */
    ITEM1("-1", "すべて"),
    /** A */
    ITEM2("A", "A"),
    /** B */
    ITEM3("B", "B"),
    /** C */
    ITEM4("C", "C"),
    /** 評価対象外 */
    ITEM5("Z", "評価対象外");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionABCRank(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（ABCランク） */
  public enum OptionABCRank2 implements Option {
    /** 全ランク */
    ITEM1("-1", "すべて"),
    /** A */
    ITEM2("A", "A"),
    /** B */
    ITEM3("B", "B"),
    /** C */
    ITEM4("C", "C"),
    /** 評価対象外 */
    ITEM5("Z", "評価対象外");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionABCRank2(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（ランク基準） */
  public enum OptionKijunRank implements Option {
    /** 金額 */
    ITEM1("1", "金額"),
    /** 点数 */
    ITEM2("2", "点数");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionKijunRank(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション(RFM分析-分析軸) */
  public enum OptionRFM implements Option {
    /** F×R */
    ITEM1("1", "F×R"),
    /** F×M */
    ITEM2("2", "F×M"),
    /** 年齢層×R */
    ITEM3("3", "年齢層×R"),
    /** 年齢層×F */
    ITEM4("4", "年齢層×F"),
    /** 年齢層×M */
    ITEM5("5", "年齢層×M"),
    /** 部門売上順位×R */
    ITEM6("6", "部門売上順位×R"),
    /** 部門売上順位×F */
    ITEM7("7", "部門売上順位×F"),
    /** 部門売上順位×M */
    ITEM8("8", "部門売上順位×M");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionRFM(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション(RFM分析-分析軸) */
  public enum OptionRFM2 implements Option {
    /** R */
    ITEM1("1", "R"),
    /** F */
    ITEM2("2", "F"),
    /** M */
    ITEM3("3", "M");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionRFM2(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（RFMランク） */
  public enum OptionRankRFM implements Option {
    /** ランク5 */
    ITEM5("5", "ランク5"),
    /** ランク4 */
    ITEM4("4", "ランク4"),
    /** ランク3 */
    ITEM3("3", "ランク3"),
    /** ランク2 */
    ITEM2("2", "ランク2"),
    /** ランク1 */
    ITEM1("1", "ランク1");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionRankRFM(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（RFM分析-年齢層） */
  public enum OptionRFMAge implements Option {
    /** 20歳未満 */
    ITEM1("19", "20歳未満"),
    /** 20 - 29歳 */
    ITEM2("29", "20 - 29歳"),
    /** 30 - 39歳 */
    ITEM3("39", "30 - 39歳"),
    /** 40 - 49歳 */
    ITEM4("49", "40 - 49歳"),
    /** 50 - 59歳 */
    ITEM5("59", "50 - 59歳"),
    /** 60 - 69歳 */
    ITEM6("69", "60 - 69歳"),
    /** 70歳以上 */
    ITEM7("70", "70歳以上"),
    /** その他 */
    ITEM8("99", "その他");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionRFMAge(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション(期間-月週日) */
  public enum OptionTanRui implements Option {
    /** 単日 */
    TAN("1", "単日"),
    /** 累計 */
    RUI("2", "累計");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OptionTanRui(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  // TODO
  /** HTML関連 選択リストオプション（販売区分） */
  public enum OptionHanbaiKbn implements Option {
    /** すべて */
    ITEM0("", "すべて"),
    /** 合計 */
    ITEM1("1", "合計"),
    /** 定番 */
    ITEM2("2", "定番"),
    /** 特売 */
    ITEM3("3", "特売"),
    /** 全店特売 */
    ITEM4("4", "全店特売"),
    /** 自店山積 */
    ITEM5("5", "自店山積"),
    /** 個店特売 */
    ITEM6("6", "個店特売"),
    /** 生活応援 */
    ITEM7("7", "生活応援"),
    /** 本部山積 */
    ITEM8("8", "本部山積");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionHanbaiKbn(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（比較値） */
  public enum OptionHikaku implements Option {
    /** 売上金額 */
    ITEM1("01", "売上金額"),
    /** 売上点数 */
    ITEM5("05", "売上点数");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionHikaku(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 ラジオ（昨対） */
  public enum RadSakutai {
    /** 区分 */
    NAME("RadSakutai", "昨対", ""),
    /** 曜日合わせ */
    ID1("RadSakutai1", "曜日合わせ", "1"),
    /** 日合わせ */
    ID2("RadSakutai2", "日合わせ", "2");

    private final String obj;
    private final String txt;
    private final String val;

    /** 初期化 */
    private RadSakutai(String obj, String txt, String val) {
      this.obj = obj;
      this.txt = txt;
      this.val = val;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }

  /** HTML関連 ラジオ（店･集計別） */
  public enum RadTen {
    /** 区分 */
    NAME("RadTen", "店･集計別", ""),
    /** 店集計 */
    ID1("RadTen1", "店集計", "1"),
    /** 店別 */
    ID2("RadTen2", "店別", "2");

    private final String obj;
    private final String txt;
    private final String val;

    /** 初期化 */
    private RadTen(String obj, String txt, String val) {
      this.obj = obj;
      this.txt = txt;
      this.val = val;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }

  /** HTML関連 ラジオ（業態･店別） */
  public enum RadGyotai {
    /** 区分 */
    NAME("RadGyotai", "業態･店別", ""),
    /** 業態別 */
    ID1("RadGyotai1", "業態別", "1"),
    /** 店別 */
    ID2("RadGyotai2", "店別", "2"),
    /** 部門別 */
    ID3("RadGyotai3", "部門別", "3"),
    /** サブ業態別 */
    ID4("RadGyotai4", "サブ業態別", "4");

    private final String obj;
    private final String txt;
    private final String val;

    /** 初期化 */
    private RadGyotai(String obj, String txt, String val) {
      this.obj = obj;
      this.txt = txt;
      this.val = val;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }

  /** HTML関連 ラジオ（メーカー/取引先） */
  public enum RadMaker {
    /** 区分 */
    NAME("RadMaker", "メーカー/取引先", ""),
    /** メーカー */
    ID1("RadMaker1", "メーカー", "1"),
    /** 取引先 */
    ID2("RadMaker2", "取引先", "2");

    private final String obj;
    private final String txt;
    private final String val;

    /** 初期化 */
    private RadMaker(String obj, String txt, String val) {
      this.obj = obj;
      this.txt = txt;
      this.val = val;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }

  /** HTML関連 ラジオ（表示形態：ベストワースト） */
  public enum RadBest {
    /** 区分 */
    NAME("RadBest", "表示形態", ""),
    /** 上位 */
    ID1("RadBest1", "ベスト", "1"),
    /** 下位 */
    ID2("RadBest2", "ワースト", "2");

    private final String obj;
    private final String txt;
    private final String val;

    /** 初期化 */
    private RadBest(String obj, String txt, String val) {
      this.obj = obj;
      this.txt = txt;
      this.val = val;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }


  /** HTML関連 ラジオ（区分） */
  public enum RadKbn {
    /** 区分 */
    NAME("RadKbn", "区分", ""),
    /** 入荷 */
    ID1("RadKbn1", "入荷", "1"),
    /** 商品 */
    ID2("RadKbn2", "商品", "2");

    private final String obj;
    private final String txt;
    private final String val;

    /** 初期化 */
    private RadKbn(String obj, String txt, String val) {
      this.obj = obj;
      this.txt = txt;
      this.val = val;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }


  /** HTML関連 ラジオ（表示形態） */
  public enum RadKeitai {
    /** 区分 */
    NAME("RadKeitai", "表示形態", ""),
    /** 入荷 */
    ID1("RadKeitai1", "クロス", "1"),
    /** 商品 */
    ID2("RadKeitai2", "明細", "2");

    private final String obj;
    private final String txt;
    private final String val;

    /** 初期化 */
    private RadKeitai(String obj, String txt, String val) {
      this.obj = obj;
      this.txt = txt;
      this.val = val;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return txt 表示名称 */
    public String getTxt() {
      return txt;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }


  /** HTML関連 選択リストオプション（出力項目(売上)） */
  public enum OptionOutputUriage implements Option {
    /** 金額 */
    ITEM1("1", "金額"),
    /** 前年金額 */
    ITEM2("2", "前年金額"),
    /** 点数 */
    ITEM3("3", "点数"),
    /** 前年点数 */
    ITEM4("4", "前年点数"),
    /** 廃棄金額 */
    ITEM5("5", "廃棄金額"),
    /** 廃棄点数 */
    ITEM6("6", "廃棄点数"),
    /** 値引金額 */
    ITEM7("7", "値引金額"),
    /** 値引点数 */
    ITEM8("8", "値引点数");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionOutputUriage(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（出力項目） */
  public enum OptionOutput006 implements Option {
    /** 金額 */
    ITEM1("1", "金額"),
    /** 点数 */
    ITEM2("2", "点数"),
    /** 廃棄金額 */
    ITEM3("3", "廃棄金額"),
    /** 廃棄点数 */
    ITEM4("4", "廃棄点数"),
    /** 値引金額 */
    ITEM5("5", "値引金額"),
    /** 値引点数 */
    ITEM6("6", "値引点数");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionOutput006(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** HTML関連 選択リストオプション（出力項目(RFM)） */
  public enum OptionOutputRFM implements Option {
    /** 売上高 */
    ITEM1("1", "売上高"),
    /** 売上構成比 */
    ITEM2("2", "売上構成比"),
    /** 買上人数 */
    ITEM3("3", "買上人数"),
    /** 人数構成比 */
    ITEM4("4", "人数構成比"),
    /** 買上単価 */
    ITEM5("5", "買上単価");

    private final String txt;
    private final String val;

    /** 初期化 */
    private OptionOutputRFM(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** 固定値定義 */
  public enum Values implements Option {
    /** 選択値空白 */
    NONE("-1", ""),

    /** すべて */
    ALL("-1", "すべて"),

    /** 合計 */
    SUM("-1", "合計"),
    /** 平均 */
    AVG("-2", "平均"),

    /** 全店計 */
    TENPO_ALL("-99", "全店計"),
    /** 新店計 */
    TENPO_NEW("-98", "新店計"),
    /** 既存店計 */
    TENPO_EX("-97", "既存店小計"),
    /** 店舗別 */
    TENPO_EACH("-96", "店舗別"),

    /** 店舗グループ:販売統括部 */
    TENPO_G_HAN("-89", "販売統括部"),
    /** 店舗グループ:青果市場 */
    TENPO_G_SEI("-88", "青果市場"),
    /** 店舗グループ:ボンマタン */
    TENPO_G_BON("-87", "ボンマタン");

    private final String val;
    private final String txt;

    /** 初期化 */
    private Values(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /**
   * 固定値定義（表側、表列項目一覧）<br>
   * VAL0:なし<br>
   * VAL1:時系列<br>
   * VAL2:部門グループ<br>
   * VAL3:部門<br>
   * VAL4:大分類<br>
   * VAL5:中分類<br>
   * VAL6:小分類<br>
   * VAL7:商品<br>
   * VAL8:販売統括部<br>
   * VAL9:販売部<br>
   * VAL10:店舗<br>
   * VAL11:市場<br>
   * VAL12:仕入先<br>
   * VAL13:メーカー<br>
   * VAL14:表示項目<br>
   * VAL15:勘定科目<br>
   * VAL16:ランキング<br>
   * VAL17:構成頁<br>
   * VAL18:レジ番号<br>
   * VAL19:催し区分<br>
   * VAL20:時間帯<br>
   * VAL21:当月/累計<br>
   * VAL22:所属<br>
   * VAL23:職種<br>
   */
  public enum ValHyo implements Option {
    /** なし */
    VAL0("0", "なし"),
    /** 時系列 */
    VAL1("1", "時系列"),
    /** 部門グループ */
    VAL2("2", "部門グループ"),
    /** 部門 */
    VAL3("3", "部門"),
    /** 大分類 */
    VAL4("4", "大分類"),
    /** 中分類 */
    VAL5("5", "中分類"),
    /** 小分類 */
    VAL6("6", "小分類"),
    /** 商品 */
    VAL7("7", "商品"),
    /** 販売統括部 */
    VAL8("8", "販売統括部"),
    /** 販売部 */
    VAL9("9", "販売部"),
    /** 店舗 */
    VAL10("10", "店舗"),
    /** 市場 */
    VAL11("11", "市場"),
    /** 仕入先 */
    VAL12("12", "仕入先"),
    /** メーカー */
    VAL13("13", "メーカー"),
    /** 表示項目 */
    VAL14("14", "表示項目"),
    /** 勘定科目 */
    VAL15("15", "勘定科目"),
    /** ランキング */
    VAL16("16", "ランキング"),
    /** 構成頁 */
    VAL17("17", "構成頁"),
    /** レジ番号 */
    VAL18("18", "レジ番号"),
    /** 催し区分 */
    VAL19("19", "催し区分"),
    /** 時間帯 */
    VAL20("20", "時間帯"),
    /** 当月/累計 */
    VAL21("21", "当月/累計"),
    /** 所属 */
    VAL22("22", "所属"),
    /** 職種 */
    VAL23("23", "職種"),
    /** 週別 */
    VAL24("24", "週別"),
    /** 曜日別 */
    VAL25("25", "曜日別"),
    /** ランク基準 */
    VAL26("26", "ランク基準"),
    /** 伸び率評価基準 */
    VAL27("27", "伸び率評価基準"),
    /** 販売区分 */
    VAL28("28", "販売区分"),
    /** 伝票明細 */
    VAL29("29", "伝票明細"),
    /** 本部 */
    VAL30("30", "本部"),
    /** 統括部門 */
    VAL31("31", "統括部門"),
    /** 単日累計 */
    VAL32("32", "単日累計"),
    /** 表示項目（横） */
    VAL33("33", "表示項目（横）"),
    /** 中計 */
    VAL34("34", "中計"),
    /** 店舗・構成頁 */
    VAL35("35", "店舗・構成頁");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValHyo(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /**
   * 固定値定義（既存店区分）<br>
   */
  public enum ValKizonKbn implements Option {
    /** 新店 */
    NEW("1", "新店"),
    /** 既存店 */
    KIZON("2", "既存店");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValKizonKbn(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }


  /**
   * 固定値定義（按分指示）<br>
   */
  public enum ValAnbunSiji implements Option {
    /** 再按分 */
    REDO("0", "再按分"),
    /** 按分済 */
    DONE("1", "按分済");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValAnbunSiji(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /**
   * 固定値定義（按分結果）<br>
   */
  public enum ValAnbunKekka implements Option {
    /** 按分失敗 */
    FAILURE("0", "失敗"),
    /** 按分成功 */
    SUCCESS("1", "成功"),
    /** 按分前 */
    YET("", "按分前");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValAnbunKekka(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /**
   * 固定値定義（入荷区分）<br>
   * 買掛、センター出荷、移動
   */
  public enum ValNyukaKbn implements Option {
    /** 買掛 */
    VAL1("1", "買掛"),
    /** センター出荷 */
    VAL2("2", "センター出荷"),
    /** 移動 */
    VAL3("3", "移動");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValNyukaKbn(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }
  /**
   * 固定値定義（商品区分）<br>
   * レギュラー、特売、スポット
   */
  public enum ValSyohinKbn implements Option {
    /** レギュラー */
    VAL1("0", "レギュラー"),
    /** 特売 */
    VAL2("1", "特売"),
    /** スポット */
    VAL3("2", "スポット");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValSyohinKbn(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }
  /** 固定値定義（部門区分） */
  public enum ValBumonKbn implements Option {
    /** ドライ */
    DRY("1", "ドライ・NF・許認可"),
    /** 生鮮 */
    SEISEN("2", "生鮮");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValBumonKbn(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /** 固定値定義（売価還元対象区分） */
  public enum ValKangenKbn implements Option {
    /** 非対象 */
    VAL0("0", "非対象"),
    /** 対象 */
    VAL1("1", "対象");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValKangenKbn(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }


  /**
   * 固定値定義（登録分類モード）<br>
   */
  public enum ValBunruiKbn implements Option {
    /** 大分類 */
    DAI("0", "大分類"),
    /** 中分類 */
    TYU("1", "中分類"),
    /** 部門 */
    BMN("2", "部門");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValBunruiKbn(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }


  /**
   * 固定値定義（区分表示）<br>
   */
  public enum ValKbnHyoji implements Option {
    /** 全店 */
    TENPO_ALL("0", "全店"),
    /** 新店 */
    TENPO_NEW("1", "新店"),
    /** 既存店 */
    TENPO_EX("2", "既存店");

    private final String val;
    private final String txt;

    /** 初期化 */
    private ValKbnHyoji(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }


  /**
   * 固定値定義（科目グループコード(店舗)）<br>
   */
  public enum KamG implements Option {
    /** 総明細 */
    VAL1("99000", "総明細"),
    /** 人件費合計 */
    VAL2("24000", "人件費合計");

    private final String val;
    private final String txt;

    /** 初期化 */
    private KamG(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /**
   * 固定値定義（科目グループコード(本部)）<br>
   */
  public enum KamGHon implements Option {
    /** 総明細 */
    VAL1("99000", "総明細"),
    /** 人件費合計 */
    VAL2("23000", "人件費合計");

    private final String val;
    private final String txt;

    /** 初期化 */
    private KamGHon(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }
  /**
   * 固定値定義（科目コード(本部)）<br>
   */
  public enum KamokuHon implements Option {
    /** 総売上高 */
    VAL1("100400000", "総売上高"),
    /** 売上値引・戻し */
    VAL2("100500000", "売上値引・戻し"),
    /** 売上高 */
    VAL3("101000000", "売上高"),
    /** 売上原価 */
    VAL4("102000000", "売上原価"),
    /** 荒利益 */
    VAL5("103000000", "荒利益"),
    /** リベート */
    VAL6("104000000", "リベート"),
    /** 賃貸収入 */
    VAL7("105000000", "賃貸収入"),
    /** 物流収入 */
    VAL8("105100000", "物流収入"),
    /** その他営業収入 */
    VAL9("105200000", "その他営業収入"),
    /** 営業総利益 */
    VAL10("190000000", "営業総利益"),
    /** 包装費 */
    VAL11("201000000", "包装費"),
    /** 運送費 */
    VAL12("202000000", "運送費"),
    /** ギフト配達費 */
    VAL13("202100000", "ギフト配達費"),
    /** 荷役料 */
    VAL14("202200000", "荷役料"),
    /** 販売促進費 */
    VAL15("204000000", "販売促進費"),
    /** 販売促進費カード */
    VAL16("204100000", "販売促進費カード"),
    /** 販売促進費スタンプ */
    VAL17("204500000", "販売促進費スタンプ"),
    /** 販売促進費チラシ */
    VAL18("205000000", "販売促進費チラシ"),
    /** 販売用品費 */
    VAL19("207000000", "販売用品費"),
    /** 現金過不足 */
    VAL20("208000000", "現金過不足"),
    /** 販売費合計 */
    VAL21("290000000", "販売費合計"),
    /** 役員報酬 */
    VAL22("300500000", "役員報酬"),
    /** 給料 */
    VAL23("301000000", "給料"),
    /** 残業 */
    VAL24("302000000", "残業"),
    /** 給料パートナー */
    VAL25("303000000", "給料パートナー"),
    /** 賞与 */
    VAL26("305000000", "賞与"),
    /** 賞与パートナー */
    VAL27("305500000", "賞与パートナー"),
    /** 法定福利費 */
    VAL28("306000000", "法定福利費"),
    /** 福利厚生費 */
    VAL29("306100000", "福利厚生費"),
    /** 年金保険料 */
    VAL30("307000000", "年金保険料"),
    /** 採用費 */
    VAL31("308000000", "採用費"),
    /** 教育訓練費 */
    VAL32("308500000", "教育訓練費"),
    /** 人件費合計 */
    VAL33("390000000", "人件費合計"),
    /** 開発研究費 */
    VAL34("403010000", "開発研究費"),
    /** 図書新聞費 */
    VAL35("403020000", "図書新聞費"),
    /** 衛生費 */
    VAL36("403100000", "衛生費"),
    /** 衛生費ゴミ処理 */
    VAL37("403110000", "衛生費ゴミ処理"),
    /** 会議費 */
    VAL38("403120000", "会議費"),
    /** 広告費 */
    VAL39("403130000", "広告費"),
    /** 交際費 */
    VAL40("403140000", "交際費"),
    /** 水道料 */
    VAL41("404000000", "水道料"),
    /** 電気料 */
    VAL42("405000000", "電気料"),
    /** ガス料 */
    VAL43("406000000", "ガス料"),
    /** 通信費 */
    VAL44("407000000", "通信費"),
    /** 車輌費 */
    VAL45("407500000", "車輌費"),
    /** 旅費交通費 */
    VAL46("408000000", "旅費交通費"),
    /** 修繕費 */
    VAL47("409000000", "修繕費"),
    /** 保安警備費 */
    VAL48("410000000", "保安警備費"),
    /** 消耗品費 */
    VAL49("411000000", "消耗品費"),
    /** 器具備品費 */
    VAL50("412000000", "器具備品費"),
    /** 雑費 */
    VAL51("413000000", "雑費"),
    /** 借地借家料 */
    VAL52("414000000", "借地借家料"),
    /** 保険料 */
    VAL53("415000000", "保険料"),
    /** 租税公課 */
    VAL54("416000000", "租税公課"),
    /** 支払手数料 */
    VAL55("417000000", "支払手数料"),
    /** 設備リース料 */
    VAL56("418000000", "設備リース料"),
    /** ＥＤＰリース料 */
    VAL57("418500000", "ＥＤＰリース料"),
    /** 減価償却費 */
    VAL58("419000000", "減価償却費"),
    /** 保守料 */
    VAL59("420000000", "保守料"),
    /** 会費組合費 */
    VAL60("421000000", "会費組合費"),
    /** 寄附金 */
    VAL61("423000000", "寄附金"),
    /** 貸倒引当金繰入 */
    VAL62("424000000", "貸倒引当金繰入"),
    /** 管理費合計 */
    VAL63("490000000", "管理費合計"),
    /** 管理可能費合計 */
    VAL64("491000000", "管理可能費合計"),
    /** 管理不可能費合計 */
    VAL65("492000000", "管理不可能費合計"),
    /** 販管費合計 */
    VAL66("495000000", "販管費合計"),
    /** リベート戻し */
    VAL67("496000000", "リベート戻し"),
    /** リベート戻し(ダミー) */
    VAL68("496000001", "リベート戻し(ダミー)"),
    /** 営業利益 */
    VAL69("501000000", "営業利益"),
    /** 営業外収益合計 */
    VAL70("502000000", "営業外収益合計"),
    /** 受取利息 */
    VAL71("502010000", "受取利息"),
    /** 貸付金利息 */
    VAL72("502020000", "貸付金利息"),
    /** 保証金利息 */
    VAL73("502030000", "保証金利息"),
    /** 有価証券利息 */
    VAL74("502040000", "有価証券利息"),
    /** 現先利息 */
    VAL75("502050000", "現先利息"),
    /** 受取配当金 */
    VAL76("502060000", "受取配当金"),
    /** 有価証券売却益 */
    VAL77("502070000", "有価証券売却益"),
    /** 雑収入 */
    VAL78("502080000", "雑収入"),
    /** 為替差益 */
    VAL79("502090000", "為替差益"),
    /** 営業外費用合計 */
    VAL80("503000000", "営業外費用合計"),
    /** 短期支払利息 */
    VAL81("503010000", "短期支払利息"),
    /** 長期支払利息 */
    VAL82("503020000", "長期支払利息"),
    /** 社債利息 */
    VAL83("503030000", "社債利息"),
    /** 新株発行費償却 */
    VAL84("503040000", "新株発行費償却"),
    /** 社債発行差金償却 */
    VAL85("503050000", "社債発行差金償却"),
    /** 社債発行費償却 */
    VAL86("503060000", "社債発行費償却"),
    /** 有価証券売却損 */
    VAL87("503070000", "有価証券売却損"),
    /** 雑損失 */
    VAL88("503080000", "雑損失"),
    /** 為替差損 */
    VAL89("503090000", "為替差損"),
    /** 経常利益 */
    VAL90("504000000", "経常利益"),
    /** 特別利益 */
    VAL91("504100000", "特別利益"),
    /** 特別利益(ダミー) */
    VAL92("504100001", "特別利益(ダミー)"),
    /** 特別損失 */
    VAL93("504200000", "特別損失"),
    /** 特別損失(ダミー) */
    VAL94("504200001", "特別損失(ダミー)"),
    /** 税引前利益 */
    VAL95("506000000", "税引前利益"),
    /** 法人税及び住民税 */
    VAL96("506100000", "法人税及び住民税"),
    /** 法人税及び住民税(ダミー) */
    VAL97("506100001", "法人税及び住民税(ダミー)"),
    /** 当期利益 */
    VAL98("506200000", "当期利益");

    private final String val;
    private final String txt;

    /** 初期化 */
    private KamokuHon(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }


  /**
   * 固定値定義（科目コード(店舗)）<br>
   */
  public enum KamokuTen implements Option {
    /** 総売上高 */
    VAL1("100400000", "総売上高"),
    /** ポイント引当 */
    VAL2("100500000", "ポイント引当"),
    /** 売上高 */
    VAL3("101000000", "売上高"),
    /** 売上原価 */
    VAL4("102000000", "売上原価"),
    /** 荒利益 */
    VAL5("103000000", "荒利益"),
    /** 賃貸収入 他 */
    VAL6("105000000", "賃貸収入　他"),
    /** 営業総利益 */
    VAL7("190000000", "営業総利益"),
    /** 包 装 費 */
    VAL8("201000000", "包　装　費"),
    /** 運 送 費 */
    VAL9("202000000", "運　送　費"),
    /** ギフト配達費 */
    VAL10("202500000", "ギフト配達費"),
    /** 販売促進費 */
    VAL11("203000000", "販売促進費"),
    /** 販売促進費チラシ */
    VAL12("205000000", "販売促進費チラシ"),
    /** 販売用品費 他 */
    VAL13("207000000", "販売用品費　他"),
    /** 給 料 */
    VAL14("301000000", "給　　料"),
    /** 残 業 */
    VAL15("302000000", "残　　業"),
    /** 給料パートナー */
    VAL16("303000000", "給料パートナー"),
    /** 賞 与 */
    VAL17("305000000", "賞　　与"),
    /** 賞与パートナー */
    VAL18("305500000", "賞与パートナー"),
    /** 衛 生 費 */
    VAL19("403100000", "衛　生　費"),
    /** 衛生費ゴミ処理 */
    VAL20("403200000", "衛生費ゴミ処理"),
    /** 水 道 料 */
    VAL21("404000000", "水　道　料"),
    /** 電 気 料 */
    VAL22("405000000", "電　気　料"),
    /** ガ ス 料 */
    VAL23("406000000", "ガ　ス　料"),
    /** 修 繕 費 */
    VAL24("409000000", "修　繕　費"),
    /** 保安警備費 */
    VAL25("410000000", "保安警備費"),
    /** 消耗・器具備品費 */
    VAL26("411000000", "消耗・器具備品費"),
    /** 雑 費 他 */
    VAL27("413000000", "雑　費　他"),
    /** 借地借家料 */
    VAL28("414000000", "借地借家料"),
    /** 租 税 公 課 */
    VAL29("416000000", "租 税 公 課"),
    /** 支払手数料 */
    VAL30("417000000", "支払手数料"),
    /** 設備リース料 */
    VAL31("418000000", "設備リース料"),
    /** 減価償却費 */
    VAL32("419000000", "減価償却費"),
    /** 保 守 料 */
    VAL33("420000000", "保　守　料"),
    /** 保険料 他 */
    VAL34("421000000", "保険料　他"),
    /** 管理費合計 */
    VAL35("490000000", "管理費合計"),
    /** 管理可能費合計 */
    VAL36("490100000", "管理可能費合計"),
    /** 管理不可能費合計 */
    VAL37("490200000", "管理不可能費合計"),
    /** リベート */
    VAL38("496000000", "リベート"),
    /** 配賦前営業利益 */
    VAL39("501000000", "配賦前営業利益"),
    /** 配賦後営業利益 */
    VAL40("501500000", "配賦後営業利益"),
    /** 営業外費用合計 */
    VAL41("502000000", "営業外費用合計"),
    /** 経常利益 */
    VAL42("504000000", "経常利益"),
    /** 本部費物流収入 */
    VAL43("504900000", "本部費物流収入"),
    /** 本部費配賦 */
    VAL44("505000000", "本部費配賦"),
    /** 年金保険料 */
    VAL45("702000000", "年金保険料"),
    /** 法定福利費 */
    VAL46("703100000", "法定福利費"),
    /** 採用費 他 */
    VAL47("703300000", "採用費　他"),
    /** ＰＣ加工人件費 */
    VAL48("704000000", "ＰＣ加工人件費"),
    /** 販売費合計 */
    VAL49("803000000", "販売費合計"),
    /** 人件費合計 */
    VAL50("804000000", "人件費合計"),
    /** 販管費合計 */
    VAL51("805000000", "販管費合計"),
    /** 販売利益 */
    VAL52("806000000", "販売利益"),
    /** 人時売上(円) */
    VAL53("J39100000", "人時売上(円)"),
    /** 人時荒利（円） */
    VAL54("J39200000", "人時荒利（円）"),
    /** 正社員人時数 */
    VAL55("J39300000", "正社員人時数"),
    /** パートナー人時数 */
    VAL56("J39400000", "パートナー人時数"),
    /** パートナー比率（人件費） */
    VAL57("J39500000", "パートナー比率（人件費）"),
    /** パートナー比率（人時） */
    VAL58("J39600000", "パートナー比率（人時）");

    private final String val;
    private final String txt;

    /** 初期化 */
    private KamokuTen(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }
  /**
   * 固定値定義（部門G）<br>
   */
  public enum BumomnG implements Option {
    /** 青果Ｇ */
    VAL01("01", "青果Ｇ"),
    /** 鮮魚Ｇ */
    VAL02("02", "鮮魚Ｇ"),
    /** 精肉Ｇ */
    VAL03("03", "精肉Ｇ"),
    /** デイリー食品Ｇ */
    VAL04("04", "デイリー食品Ｇ"),
    /** 一般食品Ｇ */
    VAL05("05", "一般食品Ｇ"),
    /** 生活用品Ｇ */
    VAL06("06", "生活用品Ｇ"),
    /** 惣菜Ｇ */
    VAL07("07", "惣菜Ｇ"),
    /** ベーカリーＧ */
    VAL08("08", "ベーカリーＧ"),
    /** 催事Ｇ */
    VAL09("09", "催事Ｇ");

    private final String val;
    private final String txt;

    /** 初期化 */
    private BumomnG(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }



  /**
   * 固定値定義（科目コード(店舗)）<br>
   */
  public enum Bumomn implements Option {
    /** 菓子 */
    VAL01("01", "菓子"),
    /** 野菜 */
    VAL02("02", "野菜"),
    /** 一般食品 */
    VAL03("03", "一般食品"),
    /** 鮮魚 */
    VAL04("04", "鮮魚"),
    /** 精肉 */
    VAL05("05", "精肉"),
    /** 塩干 */
    VAL06("06", "塩干"),
    /** 雑貨 */
    VAL07("07", "雑貨"),
    /** 農水産乾物 */
    VAL08("08", "農水産乾物"),
    /** フルーツ */
    VAL09("09", "フルーツ"),
    /** デイリー */
    VAL10("10", "デイリー"),
    /** 乳製品 */
    VAL11("11", "乳製品"),
    /** バラエティー */
    VAL12("12", "バラエティー"),
    /** 実用衣料 */
    VAL13("13", "実用衣料"),
    /** たばこ */
    VAL14("14", "たばこ"),
    /** フローラル */
    VAL15("15", "フローラル"),
    /** クックサン（惣菜） */
    VAL20("20", "クックサン（惣菜）"),
    /** クックサン（寿司） */
    VAL23("23", "クックサン（寿司）"),
    /** 催事 */
    VAL32("32", "催事"),
    /** ベーカリー */
    VAL34("34", "ベーカリー"),
    /** ボンマタン */
    VAL43("43", "ボンマタン"),
    /** 酒 */
    VAL44("44", "酒"),
    /** 本 */
    VAL47("47", "本"),
    /** 米 */
    VAL54("54", "米");

    private final String val;
    private final String txt;

    /** 初期化 */
    private Bumomn(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /**
   * 表示項目<br>
   * 初期値
   */
  public enum OutputInit implements Option {
    /** 01.時間帯売上実績 */
    VAL001("[\"01\",\"02\",\"03\",\"04\",\"06\",\"08\"]", "01.時間帯売上実績"),
    /** 05.ベスト＆ワースト分析 */
    VAL005("[\"01\",\"02\",\"14\",\"05\",\"04\",\"12\",\"11\",\"15\",\"16\"]", "05.ベスト＆ワースト分析"),
    /** 06.比較分析 */
    VAL006("[\"01\",\"11\",\"09\",\"10\",\"12\",\"13\"]", "06.比較分析"),
    /** 07.平均日商 */
    VAL007("[\"0101\",\"0201\",\"0901\",\"1001\",\"1101\",\"0301\",\"0501\",\"0601\",\"0701\"]", "07.平均日商"),
    /** 12.催し実績 */
    VAL012("[\"0101\",\"0201\",\"0301\",\"0401\",\"0501\",\"0601\",\"0701\",\"0801\",\"0901\",\"1001\",\"1101\"]", "12.催し実績"),
    /** 25.構成頁 */
    VAL025("[\"2\",\"3\",\"4\"]", "25.構成頁");

    private final String val;
    private final String txt;

    /** 初期化 */
    private OutputInit(String val, String txt) {
      this.val = val;
      this.txt = txt;
    }

    /** @return val 値 */
    @Override
    public String getVal() {
      return val;
    }

    /** @return txt 表示名称 */
    @Override
    public String getTxt() {
      return txt;
    }
  }

  /**
   * SQL 関連
   */
  /** 定数value */
  public static final String VAL = "VALUE";
  /** 定数text */
  public static final String TXT = "TEXT";
  /** 定数カレンダー最小日付 */
  public static final String MINDT = "MINDYMD";
  /** 定数カレンダー最大日付 */
  public static final String MAXDT = "MAXDYMD";
  /** 定数最大行数+1 */
  public static final String MAX_ROWNUM = "20001";
  /** 定数四捨五入桁数(率:速報系) */
  public static final int CMN_DIGITS = 1;
  /** 定数四捨五入桁数(率:月次系) */
  public static final int CMN_DIGITS_GTJ = 2;
  /** 定数四捨五入桁数(金額) */
  public static final int CMN_DIGITS_GK = 0;

  /** 営業レポート切換基準年度 2011年以降：売価還元対応 */
  public static final int EIR_CHANGE_FYEAR = 2011;

  /** SQL 関連 スキーマ一覧） */
  public enum Schema {
    /** 売上情報 */
    SATTR("SATTR"),
    /** RFM情報 */
    SATTD("SATTD");

    private final String val;

    /** 初期化 */
    private Schema(String val) {
      this.val = val;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }


  /** SQL 関連 固定値テーブルキー） */
  public enum KbnKey {
    /** 表側/表列共通 */
    CMNHYO("1001"),
    /** 表側1 */
    HYOSOKU1("100101"),
    /** 表側2 */
    HYOSOKU2("100102"),
    /** 表列 */
    HYORETSU("100103"),
    /** 開発コード：表側1 */
    HYOSOKU1DEV("2100101"),
    /** 開発コード：表側2 */
    HYOSOKU2DEV("2100102"),
    /** 開発コード：表列 */
    HYORETSUDEV("2100103"),
    /** 表示項目 */
    OUTPUT("1002"),
    /** 集計単位 */
    SYUKEI("1003"),
    /** カテゴリー 売価部門 */
    CTG_BUMON_BAIKA("1006"),
    /** 催し区分 */
    MOYOSHI("1007"),
    /** 商品区分 */
    SYOHIN("1008"),
    /** 抽出条件 */
    COND("1009"),
    /** 生鮮部門(最終仕入原価法の対象部門) */
    BUMON_SEISEN_SSGH("1010"),
    /** ドライ・NF・許認可部門(最終仕入原価法の対象部門) */
    BUMON_DRY_SSGH("1011"),
    /** 生鮮部門 */
    BUMON_SEISEN_ER("2"),
    /** ドライ */
    BUMON_DRY_ER("1"),
    /** 区分表示 */
    HYOJI("1015"),
    /** 職種 */
    SYOKUSYU("1016"),
    /** 役職 */
    YAKUSYOKU("1017");

    private final String val;

    /** 初期化 */
    private KbnKey(String val) {
      this.val = val;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }


  /** SQL 関連 固定値テーブルキーで用いている識別コード */
  public enum KbnKeyParts {
    /** 共通 */
    CMN("0"),
    /** 単日単位 */
    TANJITU("1"),
    /** 期間単位 */
    KIKAN("2"),
    /** 部門単位 */
    BUMON("1"),
    /** 分類単位 */
    BUNRUI("2"),
    /** 売価還元対応 */
    AFTER("1"),
    /** 売価還元対応前 */
    BEFORE("2"),
    /** 特殊 */
    SPECIAL("9");

    private final String val;

    /** 初期化 */
    private KbnKeyParts(String val) {
      this.val = val;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }

  /** HTML関連 隠し情報 */
  public enum Hidden {
    /** 変更行 */
    CHANGED_IDX("hiddenChangedIdx", ""),
    /** 選択行 */
    SELECT_IDX("hiddenSelectIdx", "");

    private final String obj;
    private final String val;

    /** 初期化 */
    private Hidden(String obj, String val) {
      this.obj = obj;
      this.val = val;
    }

    /** @return obj Object名 */
    public String getObj() {
      return obj;
    }

    /** @return val 値 */
    public String getVal() {
      return val;
    }
  }

  // SQL：空白
  public final static String ID_SQL_BLANK = "select 0 from SYSIBM.SYSDUMMY1 where 0 = 1";
  // SQL：すべて
  public final static String ID_SQL_ALL = "select VALUE, TEXT from (values ('" + Values.NONE.getVal() + "', 'すべて')) as X(value, TEXT)";
  public final static String ID_SQL_ALL2 = "select VALUE, TEXT, SEQ from (values ('" + Values.NONE.getVal() + "', 'すべて', -1)) as X(value, TEXT, SEQ)";

  // SQL：カレンダーマスター
  public final static String ID_SQL_CAL = "select min(COMTOB) as " + MINDT + ", max(COMTOB) as " + MAXDT + " from SATYS.MCALTT";

  // SQL：区分テーブル
  public final static String ID_SQL_KBN = "select rtrim(IKBUQID) as VALUE, IBKUQVL as TEXT from SATMS.PIMSKB where IKBGPID = ? ";
  public final static String ID_SQL_KBNS = "select rtrim(IKBUQID) as VALUE, IBKUQVL as TEXT from SATMS.PIMSKB where IKBGPID in (@) ";
  public final static String ID_SQL_KBN_TAIL = " order by IBKVSEQ";

  // SQL：期間(初期表示用)
  public final static String ID_SQL_KIKAN_DAY_INIT = "select max(COMTOB) as DT1, max(COMTOB) as DT2 from SATYS.MCALTT";
  public final static String ID_SQL_KIKAN_WEEK_INIT =
      "select NVL(MAX(NENDO), - 1) as NENDO, NVL(MIN(COMTOB), - 1) as MINDT, NVL(MAX(COMTOB), - 1) as MAXDT from SATYS.MCALTT where NENSYUU = (select integer(NENSYUU) - 100 from SATYS.MCALTT where COMTOB = ?)";
  // 登録系画面
  public final static String ID_SQL_KIKAN_MONTH_INIT = "select DT1, DT2 from ( select max(NENTUKI) as DT1, max(NENTUKI) as DT2 from SATYS.MCALTT where NENTUKI = ?"
      + " union all select max(NENTUKI) as DT1, max(NENTUKI) as DT2 from SATYS.MCALTT where NENTUKI = ?"
      + " union all select max(NENTUKI) as DT1, max(NENTUKI) as DT2 from SATYS.MCALTT ) T where DT1 is not null fetch first 1 row only";
  // 確認系画面
  public final static String ID_SQL_KIKAN_MONTH_INIT2 = "select DT1, DT2 from ( select max(NENTUKI) as DT1, max(NENTUKI) as DT2 from SATYS.MCALTT where NENTUKI = ? and '01' < ?"
      + " union all select max(NENTUKI) as DT1, max(NENTUKI) as DT2 from SATYS.MCALTT where NENTUKI = ?"
      + " union all select max(NENTUKI) as DT1, max(NENTUKI) as DT2 from SATYS.MCALTT ) T where DT1 is not null fetch first 1 row only";
  /** 共通（SATYS.MCALTT） */
  // SQL：期間(月)共通部
  private final static String ID_SQL_KIKAN_YM_2 =
      ", TEXT from (select MIN(NENTUKI) as MINDT, MAX(NENTUKI) as MAXDT, left(NENTUKI,4) || '年' || substr(NENTUKI,5) || '月' as TEXT from SATYS.MCALTT group by NENTUKI)where  MINDT <= TO_CHAR(current_date,'YYYYMM') order by VALUE desc";
  // SQL：期間(月)FROM
  public final static String ID_SQL_KIKAN_YM_FROM = "select MINDT as VALUE" + ID_SQL_KIKAN_YM_2;
  // SQL：期間(月)TO
  public final static String ID_SQL_KIKAN_YM_TO = "select MAXDT as VALUE" + ID_SQL_KIKAN_YM_2;

  // 期間共通（閏年除外）
  public final static String ID_SQL_KIKAN_CMN_WHERE = " NENDO > 0 ";

  // SQL：販売部・店舗共通
  public final static String ID_SQL_TEN_HANTOUBU_WHERE = " and HTOUKATU_CD_S = ? ";
  public final static String ID_SQL_TEN_HANTOUBU_WHEREP = " and HTOUKATU_CD_P = ? ";
  public final static String ID_SQL_TEN_HANTOUBUS_WHERE = " and HTOUKATU_CD_S in (@) ";
  public final static String ID_SQL_TEN_HANTOUBUS_WHEREP = " and HTOUKATU_CD_P in (@) ";
  public final static String ID_SQL_TEN_ICHIBA_WHERE = " and ICHIBA = ? ";
  public final static String ID_SQL_TEN_ICHIBAS_WHERE = " and ICHIBA in (@) ";
  public final static String ID_SQL_TEN_HANBAIBU_WHERE = " and HANBAIB_S = ? ";
  public final static String ID_SQL_TEN_HANBAIBU_WHEREP = " and HANBAIB_P = ? ";
  public final static String ID_SQL_TEN_HANBAIBUS_WHERE = " and HTOUKATU_CD_S||HANBAIB_S in (@) ";
  public final static String ID_SQL_TEN_HANBAIBUS_WHEREP = " and HTOUKATU_CD_P||HANBAIB_P in (@) ";
  /** 閉鎖店除外条件 */
  public final static String ID_SQL_TEN_EXIST = " and (TENHEH >= TO_CHAR(current date, 'yyyyMMdd') or TENHEH = 0) ";
  /** 既存店条件 */
  public final static String ID_SQL_TEN_KZN =
      " and exists (select 'X' from SATMS.MTNPJIK M20 where M20.NENTUKI = ? and M20.MISECD = T1.MISECD and M20.KIZONTEN_KBN = '" + DefineReport.ValKizonKbn.KIZON.getVal() + "') ";
  /** 新店条件 */
  public final static String ID_SQL_TEN_NEW =
      " and exists (select 'X' from SATMS.MTNPJIK M20 where M20.NENTUKI = ? and M20.MISECD = T1.MISECD and M20.KIZONTEN_KBN = '" + DefineReport.ValKizonKbn.NEW.getVal() + "') ";

  // SQL：店舗
  public final static String ID_SQL_TENPO_FOOTER = " order by VALUE";
  /** 共通（SATYS.MTNPTT） */
  public final static String ID_SQL_TENPO_ALL = "select '-1' as VALUE,'全店舗' as TEXT  from SYSIBM.SYSDUMMY1 union all ";
  public final static String ID_SQL_TENPO = "select T1.MISECD as VALUE, T1.MISECD|| ' ' || rtrim(T1.TENMEI) as TEXT from SATYS.MTNPTT T1";
  public final static String ID_SQL_TENPO_HEAD = "select VALUE, TEXT from (values ('" + Values.NONE.getVal() + "', 'すべて')) as X(value, TEXT) union all ";
  public final static String ID_SQL_TENPO_HEAD2 = "select VALUE, TEXT from (values " + " (" + Values.TENPO_ALL.getVal() + ", '" + Values.TENPO_ALL.getTxt() + "'),(" + Values.TENPO_NEW.getVal() + ", '"
      + Values.TENPO_NEW.getTxt() + "'),(" + Values.TENPO_EX.getVal() + ", '" + Values.TENPO_EX.getTxt() + "')" + ") as X(value, TEXT) union all ";
  public final static String ID_SQL_TENPO_HEAD3 = "select VALUE, TEXT from (values ('', '')) as X(value, TEXT) union all ";

  // SQL：本部ユーザ初回利用時の店舗（関連情報の最小公約店コード）
  public final static String ID_SQL_TENPO_MIN_CD = "SELECT MIN(T1.MISECD) FROM SATMS.TENPO_MST T0 " + "INNER JOIN SATYS.MTNPTT T1 ON T0.MISECD = T1.MISECD";

  // SQL：部門・分類系共通
  public final static String ID_SQL_BMN_BUMON_WHERE = " and BUNBMC = ? ";
  public final static String ID_SQL_BMN_BUMONS_WHERE = " and BUNBMC in (@) ";

  // SQL：部門
  public final static String ID_SQL_BUMON_HEAD = "select VALUE, TEXT ,'-1' as VALUE2,'D000'as BUNBMG_S   from (values ('" + Values.NONE.getVal() + "', '店合計')) as X(value, TEXT)  union all ";
  public final static String ID_SQL_BUMON_HEAD2 =
      "select VALUE, TEXT, VALUE2 from (values ('" + Values.NONE.getVal() + "', '店合計','" + Values.NONE.getVal() + "')) as X(value, TEXT, value2) union all ";
  public final static String ID_SQL_BUMON_HEAD3 =
      " select T1.BUNBMG_S as VALUE , '--' ||BUNBMG_S|| ' ' ||BMGM_S||'--'as TEXT ,- 1 - ROW_NUMBER() OVER (ORDER BY BUNBMG_S) as VALUE2, T1.BUNBMG_S  from  SATYS.MCLSTT  T1 ";
  public final static String ID_SQL_BUMON_FOOTER = " group by T1.BUNBMG_S   , T1.BUNBMC order by BUNBMG_S , VALUE2";

  /** 共通（SATYS.MCLSTT） */
  public final static String ID_SQL_BUMON = "select T1.BUNBMC as VALUE, T1.BUNBMC || ' ' || rtrim(max(T1.TOUKATU_NM_S)) as TEXT ,T1.BUNBMC as VALUE2, T1.BUNBMG_S as BUNBMG_S   from SATYS.MCLSTT T1";

  // SQL：部門荒利率
  public final static String ID_SQL_BMN_ARA_RIT = "select case when max(BUNARP) = min(BUNARP) then max(BUNARP) end as VALUE from SATMS.TTBMYS where MISECD = ? and NENTUKI = ? ";

  /**
   * 商品カテゴリ関連のSQL
   */
  /** 商品条件（T_CTG_HEAD）検索 */
  public static final String ID_SQL_KEY_HEAD =
      "select VALUE, TEXT from (values ('', '')) as X(value, TEXT) union all " + "select TO_CHAR(CD_CTG) as VALUE, NM_CTG as TEXT from KEYSYS.T_CTG_HEAD where CD_USER = ? order by TEXT";

  /** 商品条件（T_CTG_HEAD）検索 */
  public static final String ID_SQL_KEY_HEAD_NO =
      "SELECT RTRIM(T1.CD_ITEM) AS F1, T2.SYOM AS F2, ROW_NUMBER() OVER () AS F3 FROM KEYSYS.T_CTG_ITEM AS T1 INNER JOIN SATTR.SYOUHIN_MST AS T2 ON T1.CD_ITEM = T2.SYOCD WHERE T1.CD_CTG = ? ORDER BY T1.NO_LINE ASC";

  /** 商品条件（T_CTG_HEAD）登録 */
  public static final String ID_SQL_KEY_SET_HEAD =
      "merge into KEYSYS.T_CTG_HEAD T1 using (select cast (? as integer) as CD_USER, cast (? as varchar (100)) as NM_CTG from SYSIBM.SYSDUMMY1) T2 on (T1.CD_USER = T2.CD_USER and T1.NM_CTG = T2.NM_CTG) when matched then update set T1.DT_UPDATE = current_timestamp when not matched then insert (T1.CD_USER, T1.NM_CTG, T1.DT_UPDATE) values (T2.CD_USER, T2.NM_CTG, current_timestamp) else ignore";

  /** 商品条件（T_CTG_ITEM）削除 */
  public static final String ID_SQL_KEY_DELETE_ITEM = "delete from KEYSYS.T_CTG_ITEM where CD_CTG in (select CD_CTG from KEYSYS.T_CTG_HEAD where CD_USER = ? and NM_CTG = ?)";

  /** 商品条件（T_CTG_ITEM）登録 */
  public static final String ID_SQL_KEY_SET_ITEM =
      "merge into KEYSYS.T_CTG_ITEM T1 using (select CD_CTG, cast (? as varchar (20)) as CD_ITEM, cast (? as integer) as NO_LINE from KEYSYS.T_CTG_HEAD where CD_USER = ? and NM_CTG = ? ) T2 on (T1.CD_CTG = T2.CD_CTG and T1.CD_ITEM = T2.CD_ITEM and T1.NO_LINE = T2.NO_LINE) when not matched then insert (T1.CD_CTG, T1.CD_ITEM, T1.NO_LINE) values (T2.CD_CTG, T2.CD_ITEM, T2.NO_LINE) else ignore";

  /** 商品条件（T_CTG_HEAD）削除専用 */
  public static final String ID_SQL_KEY_DELETE_HEAD_SP = "delete from KEYSYS.T_CTG_HEAD where CD_CTG = ?";

  /** 商品条件（T_CTG_ITEM）削除専用 */
  public static final String ID_SQL_KEY_DELETE_ITEM_SP = "delete from KEYSYS.T_CTG_ITEM where CD_CTG = ?";

  /** SQL：商品検索（JANコード） */
  public final static String ID_SQL_SYOHIN_JAN =
      "SELECT VALUE, TEXT FROM (SELECT SYOCD AS VALUE, SYOM AS TEXT FROM SATTR.SYOUHIN_MST WHERE (SYOCD LIKE ? OR SYOM LIKE ? ) FETCH FIRST 30 ROWS ONLY) ORDER BY TEXT";

  /**
   * 店舗グループ関連のSQL
   */
  /** 店舗グループ（T_TNP_HEAD）検索 */
  public static final String ID_SQL_KEY_HEAD_TG =
      "select VALUE, TEXT from (values ('', '')) as X(value, TEXT) union all " + "select TO_CHAR(CD_CTG) as VALUE, NM_CTG as TEXT from KEYSYS.T_TNP_HEAD where CD_USER = ? order by TEXT";

  /** 店舗グループ（T_TNP_HEAD）検索 */
  public static final String ID_SQL_KEY_HEAD_NO_TG =
      "SELECT RTRIM(T1.CD_ITEM) AS F1, T2.TENMEI AS F2, ROW_NUMBER() OVER () AS F3 FROM KEYSYS.T_TNP_ITEM AS T1 INNER JOIN SATMS.TENPO_MST AS T2 ON T1.CD_ITEM = T2.MISECD WHERE T1.CD_CTG = ? ORDER BY T1.NO_LINE ASC";

  /** 店舗グループ（T_TNP_HEAD）登録 */
  public static final String ID_SQL_KEY_SET_HEAD_TG =
      "merge into KEYSYS.T_TNP_HEAD T1 using (select cast (? as integer) as CD_USER, cast (? as varchar (100)) as NM_CTG from SYSIBM.SYSDUMMY1) T2 on (T1.CD_USER = T2.CD_USER and T1.NM_CTG = T2.NM_CTG) when matched then update set T1.DT_UPDATE = current_timestamp when not matched then insert (T1.CD_USER, T1.NM_CTG, T1.DT_UPDATE) values (T2.CD_USER, T2.NM_CTG, current_timestamp) else ignore";

  /** 店舗グループ（T_TNP_ITEM）削除 */
  public static final String ID_SQL_KEY_DELETE_ITEM_TG = "delete from KEYSYS.T_TNP_ITEM where CD_CTG in (select CD_CTG from KEYSYS.T_TNP_HEAD where CD_USER = ? and NM_CTG = ?)";

  /** 店舗グループ（T_TNP_ITEM）登録 */
  public static final String ID_SQL_KEY_SET_ITEM_TG =
      "merge into KEYSYS.T_TNP_ITEM T1 using (select CD_CTG, cast (? as varchar (20)) as CD_ITEM, cast (? as integer) as NO_LINE from KEYSYS.T_TNP_HEAD where CD_USER = ? and NM_CTG = ? ) T2 on (T1.CD_CTG = T2.CD_CTG and T1.CD_ITEM = T2.CD_ITEM and T1.NO_LINE = T2.NO_LINE) when not matched then insert (T1.CD_CTG, T1.CD_ITEM, T1.NO_LINE) values (T2.CD_CTG, T2.CD_ITEM, T2.NO_LINE) else ignore";

  /** 店舗グループ（T_TNP_HEAD）削除専用 */
  public static final String ID_SQL_KEY_DELETE_HEAD_SP_TG = "delete from KEYSYS.T_TNP_HEAD where CD_CTG = ?";

  /** 店舗グループ（T_TNP_ITEM）削除専用 */
  public static final String ID_SQL_KEY_DELETE_ITEM_SP_TG = "delete from KEYSYS.T_TNP_ITEM where CD_CTG = ?";

  /** SQL：店舗検索 */
  public final static String ID_SQL_SYOHIN_TENPO =
      "SELECT VALUE, TEXT FROM (SELECT MISECD AS VALUE, TENMEI AS TEXT FROM SATMS.TENPO_MST WHERE (MISECD LIKE ? OR TENMEI LIKE ? ) FETCH FIRST 30 ROWS ONLY) ORDER BY TEXT";

  /**
   * 定義保存関連のSQL
   */
  /** 検索条件の削除 */
  public static final String ID_SQL_DELETE_SHIORI = "DELETE FROM KEYSYS.T_SHIORI WHERE CD_SHIORI=?";
  /** 検索条件の登録 */
  public static final String ID_SQL_INSERT_SHIORI = "INSERT INTO KEYSYS.T_SHIORI(CD_USER,CD_REPORT,NM_SHIORI,SNAPSHOT,FG_PUBLIC) VALUES(?,?,?,?,?)";
  /** SQL：定義検索 */
  public final static String ID_SQL_SELECT_SHIORI =
      "SELECT VALUE, TEXT, PUBLIC, CD_USER, SNAPSHOT FROM ((SELECT CD_SHIORI AS VALUE, NM_SHIORI AS TEXT, CASE WHEN FG_PUBLIC = 1 THEN '*' ELSE '' END AS PUBLIC, CD_USER, SNAPSHOT FROM KEYSYS.T_SHIORI WHERE CD_USER = ? AND CD_REPORT = ? FETCH FIRST 30 ROWS ONLY) UNION ALL (SELECT CD_SHIORI AS VALUE, NM_SHIORI AS TEXT, CASE WHEN FG_PUBLIC = 1 THEN '*' ELSE '' END AS PUBLIC, CD_USER, SNAPSHOT FROM KEYSYS.T_SHIORI WHERE FG_PUBLIC = 1 AND CD_USER <> ? AND CD_REPORT = ? FETCH FIRST 30 ROWS ONLY)) ORDER BY PUBLIC DESC, TEXT";

  /**
   * ログ情報の登録
   */
  public static final String ID_SQL_INSERT_SYSLOGS = "insert into KEYSYS.SYS_LOGS(CD_USER,DT_ACTION,CD_ACTION,REMARK,ID_USER,ID_REPORT) values(?,current_timestamp,?,?,?,?)";


  /**
   * ログ情報のアクション
   */
  public static final String ID_ACTION_LOGIN = "login";
  public static final String ID_ACTION_QUERY = "query";
  public static final String ID_ACTION_EXCEL = "excel";


  /**
   * 検索条件の削除
   */
  public static final String ID_SQL_DELETE_SNAPSHOT = "DELETE FROM KEYSYS.SYS_SNAPSHOT WHERE CD_USER=? AND CD_REPORT=?";

  /**
   * 検索条件の登録
   */
  public static final String ID_SQL_INSERT_SNAPSHOT = "INSERT INTO KEYSYS.SYS_SNAPSHOT(CD_USER,CD_REPORT,SNAPSHOT,NM_CREATE,NM_UPDATE) VALUES(?,?,?,?,?)";

  /**
   * 検索条件の検索
   */
  public static final String ID_SQL_SELECT_SNAPSHOT = "SELECT SNAPSHOT FROM KEYSYS.SYS_SNAPSHOT WHERE CD_USER=? and CD_REPORT=?";

  /**
   * SESSION 関連
   */
  /* 「照会」押下時の分析条件(JSON)を格納するセッション名 */
  public final static String ID_SESSION_STORAGE = "STORAGE";
  public final static String ID_SESSION_TABLE = "table";
  public final static String ID_SESSION_WHERE = "where";
  public final static String ID_SESSION_META = "meta";
  public final static String ID_SESSION_HEADER = "header";
  public final static String ID_SESSION_OPTION = "option";
  public final static String ID_SESSION_OPT_TABLE = "opt_table";
  public final static String ID_SESSION_MSG = "message";
  public final static String ID_SESSION_FILE = "file";

  public final static String ID_MSG_SQL_EXCEPTION = "SQLエラーが発生しました。";
  public final static String ID_MSG_APP_EXCEPTION = "アプリケーションエラーが発生しました。";

  public final static String ID_MSG_COLUMN_GREATER = "横軸に展開する情報が多すぎます。表示条件を絞り込んでください。";
  public final static String ID_SQLSTATE_APPLICATION_HEPE = "54001";
  public final static String ID_SQLSTATE_COLUMN_OVER = "54004";
  public final static String ID_SQLSTATE_COLUMN_GREATER = "54011";
  public final static String ID_SQLSTATE_BUFFER_GREATER = "54048";

  public final static String ID_MSG_CONNECTION_REST = "再度、検索を実行してください。";
  public final static String ID_SQLSTATE_CONNECTION_RESET = "08001";

  /**
   * 時間帯売上実績の範囲
   */
  public final static String ID_MODE_TIME_NEW = "1"; // 最新情報のみ参照
  public final static String ID_MODE_TIME_OLD = "2"; // 過去情報のみ参照
  public final static String ID_MODE_TIME_RNG = "3"; // 最新＋過去情報を参照
  /**
   * 隠蔽情報
   */
  // レポート番号
  public final static String ID_HIDDEN_REPORT_NO = "reportno";
  // レポート名
  public final static String ID_HIDDEN_REPORT_NAME = "reportname";
  // ユーザID
  public final static String ID_HIDDEN_USER_ID = "userid";
}
