package dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import authentication.bean.User;
import common.CmnDate;
import common.DefineReport;
import common.Defines;
import common.InputChecker;
import common.ItemList;
import common.JsonArrayData;
import common.MessageUtility;
import common.MessageUtility.FieldType;
import common.MessageUtility.Msg;
import common.MessageUtility.MsgKey;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 */
public class Report001Dao extends ItemDao {

  /**
   * インスタンスを生成します。
   *
   * @param source
   */
  public Report001Dao(String JNDIname) {
    super(JNDIname);
  }

  /**
   * 検索実行
   *
   * @return
   */
  @Override
  public boolean selectBy() {

    // 検索コマンド生成
    String command = createCommand();

    // 出力用検索条件生成
    outputQueryList();

    // 検索実行
    return super.selectBySQL(command);
  }

  /**
   * 更新処理
   *
   * @param userInfo
   *
   * @return
   * @throws IOException
   * @throws FileNotFoundException
   * @throws Exception
   */
  public JSONObject update(HttpServletRequest request, HttpSession session, HashMap<String, String> map, User userInfo) {

    // 更新情報チェック(基本JS側で制御)
    JSONObject msgObj = new JSONObject();
    JSONArray msg = this.check(map, userInfo);

    if (msg.size() > 0) {
      msgObj.put(MsgKey.E.getKey(), msg);
      return msgObj;
    }

    // 更新処理
    try {
      msgObj = this.updateData(map, userInfo);
    } catch (Exception e) {
      e.printStackTrace();
      msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E00001.getVal()));
    }
    return msgObj;
  }

  private String createCommand() {
    // ログインユーザー情報取得
    User userInfo = getUserInfo();
    if (userInfo == null) {
      return "";
    }

    String szKikanF = getMap().get("KIKAN_F"); // 期間FROM
    String szTENPO = getMap().get("TENPO"); // 店舗
    String szBUMON = getMap().get("BUMON"); // 部門
    getMap().get("BMN_ARA_RIT");


    // パラメータ確認
    // 必須チェック
    if ((szKikanF == null) || (szTENPO == null)) {
      System.out.println(super.getConditionLog());
      return "";
    }

    // タイトル情報(任意)設定
    List<String> titleList = new ArrayList<>();
    titleList.addAll(Arrays.asList("日付", "週", "今年の要因", "日付", "予算案", "店長予算案", "修正予算", "荒利予算額", "前年比", "前年売上", "前年日付", "前年の要因", "前年客数", "予測客数", "前年天気", "前年最高気温", "前年最低気温"));


    // 帳票単位
    boolean isBumon = StringUtils.isNotEmpty(szBUMON) && !DefineReport.Values.NONE.getVal().equals(szBUMON);

    String szDtF = CmnDate.dateFormat(CmnDate.getFirstDateOfMonth21(szKikanF + "01"));
    String szDtT = CmnDate.dateFormat(CmnDate.getLastDateOfMonth(szKikanF + "01"));
    String szWhereK = " between '" + szDtF + "' and '" + szDtT + "'"; // 期間条件
    String sbWhereT = " and T1.MISECD = '" + szTENPO + "'"; // 店舗条件
    String sbWhereB = ""; // 部門条件
    String sbWhereGTB = ""; // 部門条件
    String sbWhereTB = ""; // 部門条件
    String sbWhereGB = ""; // 部門条件
    if (isBumon) {
      sbWhereB = " and T1.BUNBMC = '" + szBUMON + "'";
      sbWhereTB = " and TOUKATU_CD_S = '" + szBUMON + "'";
      sbWhereGTB = " and TOUKATU_CD_S in (select   F1.BUNBMC FROM  SATYS.MCLSTT F1 where F1.BUNBMG_S =  '" + szBUMON + "')";
      sbWhereGB = " and T1.BUNBMC in (select   F1.BUNBMC FROM  SATYS.MCLSTT F1 where F1.BUNBMG_S =  '" + szBUMON + "')";
    }

    // 一覧表情報
    // TODO:天気・気温情報
    StringBuffer sbSQL = new StringBuffer();
    System.out.print("bumon_map;" + getMap().get("BUMON") + "\n");
    System.out.print("bumon_;" + szBUMON + "\n");
    sbSQL.append("with MCAL as ( ");
    sbSQL.append("  select T1.COMTOB as DT");
    sbSQL.append(
        "  ,max(CHAR(TO_CHAR(TO_DATE(T1.COMTOB, 'yyyymmdd'), 'mm/dd'), 5)||CASE DAYOFWEEK(TO_DATE(T1.COMTOB, 'yyyymmdd')) WHEN 1 THEN '(日)' WHEN 2 THEN '(月)' WHEN 3 THEN '(火)' WHEN 4 THEN '(水)' WHEN 5 THEN '(木)' WHEN 6 THEN '(金)' WHEN 7 THEN '(土)' END) as TXT");
    sbSQL.append("  ,max(WEEK_ISO(TO_DATE(T1.COMTOB, 'YYYYMMDD'))) as WEEK");
    sbSQL.append("  ,max(T2.EVENT) as EVENT,max(T2.TYOSAN) as TYOSAN");
    sbSQL.append("  ,max(T3.EVENT) as global_EVENT");
    sbSQL.append("  from ");
    sbSQL.append("  (select COMTOB from SATYS.MCALTT where COMTOB" + szWhereK + ") T1 ");
    sbSQL.append("  left outer join (select DT,EVENT,TYOSAN from SATYS.TTDEVT T1 where DT" + szWhereK + " ");
    if (szTENPO.equals("-1")) {
      sbSQL.append(" ) T2 on T1.COMTOB = T2.DT ");
    } else {
      sbSQL.append(" " + sbWhereT + ") T2 on T1.COMTOB = T2.DT ");
    }
    sbSQL.append("  left outer join SATTR.EVENT_MANAGE T3 on T3.DATE = T1.COMTOB and T3.IS_GLOBAL = 1 ");
    sbSQL.append("  group by T1.COMTOB");
    sbSQL.append(") ");
    sbSQL.append(",MCALZ as ( ");
    sbSQL.append("  select T1.DT, T1.DT_KIJUN");
    sbSQL.append("  ,max(char(left(T1.DT_KIJUN,4)||'/'||substr(T1.DT_KIJUN,5,2)||'/', 8)||case  when SUBSTR(T1.DT_KIJUN,7,2) = '99' then '1,2' else substr(T1.DT_KIJUN,7,2)||");
    sbSQL.append(
        "   CASE DAYOFWEEK(TO_DATE(T1.DT_KIJUN, 'yyyymmdd')) WHEN 1 THEN '(日)' WHEN 2 THEN '(月)' WHEN 3 THEN '(火)' WHEN 4 THEN '(水)' WHEN 5 THEN '(木)' WHEN 6 THEN '(金)' WHEN 7 THEN '(土)' END");
    sbSQL.append("   end) as TXT");
    sbSQL.append("  ,max(T5.EVENT) as global_EVENT_Z ");
    sbSQL.append("  ,max(T2.EVENT) as EVENT,max(T2.TYOSAN) as TYOSAN");
    sbSQL.append("  ,max(T2.MAXKION) as MAXKION,max(T2.MINKION) as MINKION ");
    sbSQL.append("  ,max(T2.TENKIKBN_AM) as TENKI_AM");
    sbSQL.append("  ,max(T2.TENKIKBN_PM) as TENKI_PM");
    sbSQL.append("  ,'" + szTENPO + "' as MISECD,max(T4.MISECD_HT) as MISECD_HT ");
    sbSQL.append("  , sum(T2.CUSTOMER_NUM) as CUSTOMER_NUM ");
    sbSQL.append("  , sum(T2.FLOOR_CUSTOMER_NUM) as FLOOR_CUSTOMER_NUM ");
    sbSQL.append("  from ");
    sbSQL.append("  (select CALYMD as  DT,CALZDY as  DT_KIJUN from SATYS.TABKNK where CALYMD" + szWhereK + ") T1 ");
    sbSQL.append("  left outer join SATYS.TTDEVT T2 on T1.DT_KIJUN = T2.DT");
    if (szTENPO.equals("-1")) {
      sbSQL.append("  ");
    } else {
      sbSQL.append(" " + sbWhereT.replace("T1", "T2") + " ");
    }
    sbSQL.append(" left outer join SATTR.EVENT_MANAGE T5 on  T1.DT_KIJUN = T5.DATE  and T5.IS_GLOBAL = 1 ");
    sbSQL.append("  left outer join SATYS.TSTKNR T4 on " + sbWhereT.replace("T1", "T4").replace("and", ""));
    sbSQL.append("  group by T1.DT, T1.DT_KIJUN ");
    sbSQL.append("  order by DT ");
    sbSQL.append(") ");
    sbSQL.append(",BDYS as ( ");
    sbSQL.append("  select ");
    sbSQL.append("   T1.DT ");
    sbSQL.append("  ,sum(T1.UYOSAN) as UYOSAN ");
    sbSQL.append("  ,sum(T1.AYOSAN) as AYOSAN ");
    sbSQL.append("  ,sum(T1.TYOSAN) as TYOSAN ");
    sbSQL.append("  ,sum(T1.AYOSAN*T2.BUNARP/100) as ARAYOS ");
    sbSQL.append("  from SATYS.TTBDYS T1 ");
    sbSQL.append("  inner join SATYS.MCALTT M1 on T1.DT = M1.COMTOB and T1.DT" + szWhereK + " ");
    if (szTENPO.equals("-1")) {
      sbSQL.append("  ");
    } else {
      sbSQL.append(" " + sbWhereT + " ");
    }

    if (NumberUtils.isNumber(szBUMON.replaceAll(" ", ""))) {
      sbSQL.append(" " + sbWhereB + " ");
    } else {
      sbSQL.append(" " + sbWhereGB + " ");
    }
    sbSQL.append("  inner join SATMS.TTBMYS T2 on T2.MISECD = T1.MISECD and T2.NENTUKI = M1.NENTUKI and T2.BUNBMC = T1.BUNBMC");
    sbSQL.append("  group by T1.DT ");
    sbSQL.append(") ");
    sbSQL.append(",MCLSHA AS (");
    sbSQL.append("  select distinct BUNBMC, TOUKATU_CD_S from SATTR.MCLSHA where TOUKATU_CD_S in (select distinct TOUKATU_CD_S from SATYS.MCLSTT)");
    if (NumberUtils.isNumber(szBUMON.replaceAll(" ", ""))) {
      sbSQL.append(" " + sbWhereTB + " ");
    } else {
      sbSQL.append(" " + sbWhereGTB + " ");
    }
    sbSQL.append(") ");
    sbSQL.append(",HAZ1 as ( ");
    sbSQL.append("  select T2.DT, T2.DT_KIJUN, T3.TOUKATU_CD_S");
    sbSQL.append("  ,sum(T1.URIKINGAKU) as URIKINGAKU ");
    sbSQL.append("  from SATTR.HABMDD T1 ");
    sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN ");
    if (szTENPO.equals("-1")) {
      sbSQL.append("  ");
    } else {
      sbSQL.append(" and T1.MISECD = T2.MISECD ");
    }
    sbSQL.append("  inner join MCLSHA T3 on T1.BUNBMC = T3.BUNBMC");
    sbSQL.append("  where T1.URIKINGAKU <> 0 ");
    sbSQL.append("  group by T2.DT, T2.DT_KIJUN, T3.TOUKATU_CD_S");
    sbSQL.append(") ");
    sbSQL.append(",HAZ2 as ( ");
    sbSQL.append("  select T2.DT, T2.DT_KIJUN, T3.TOUKATU_CD_S");
    sbSQL.append("  ,sum(T1.URIKINGAKU) as URIKINGAKU ");
    sbSQL.append("  from SATTR.HABMDD T1 ");
    sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD_HT");
    sbSQL.append("  inner join MCLSHA T3 on T1.BUNBMC = T3.BUNBMC");
    sbSQL.append("  where T1.URIKINGAKU <> 0 ");
    sbSQL.append("  group by T2.DT, T2.DT_KIJUN, T3.TOUKATU_CD_S");
    sbSQL.append(") ");
    sbSQL.append(",HAZ as ( ");
    sbSQL.append("  select nvl(T1.DT,T2.DT) as DT");
    sbSQL.append("  ,sum(nvl(T1.URIKINGAKU, T2.URIKINGAKU)) as URIKINGAKU ");
    sbSQL.append("  from HAZ1 T1 full outer join HAZ2 T2  on T1.DT = T2.DT and T1.TOUKATU_CD_S = T2.TOUKATU_CD_S ");
    sbSQL.append("  group by nvl(T1.DT,T2.DT)");
    sbSQL.append(") ");
    sbSQL.append(",DKYK as ( ");
    sbSQL.append("  select T2.CALYMD AS DT");
    sbSQL.append("   ,sum(T1.KYAKUSU) as KYAKUSU");
    sbSQL.append("  from SATYS.TABKNK T2");
    sbSQL.append("  left outer join SATYS.TTDKYK T1 on ");
    sbSQL.append("  T2.CALZDY = T1.DT");
    sbSQL.append("  where T2.CALYMD" + szWhereK + " ");
    if (szTENPO.equals("-1")) {
      sbSQL.append("  ");
    } else {
      sbSQL.append(" " + sbWhereT + " ");
    }
    sbSQL.append("  group by T2.CALYMD ");
    sbSQL.append(") ");
    sbSQL.append(",KYKZ1 as ( ");
    sbSQL.append("  select T2.DT, T2.DT_KIJUN");
    sbSQL.append("  ,sum(T1.KYAKUSU_SUM) as KYAKUSU");
    sbSQL.append("  from SATTR.TNKYDD T1");
    sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN ");
    if (szTENPO.equals("-1")) {
      sbSQL.append("  ");
    } else {
      sbSQL.append(" and T1.MISECD = T2.MISECD ");
    }
    sbSQL.append("  group by T2.DT, T2.DT_KIJUN ");
    sbSQL.append(") ");
    sbSQL.append(",KYKZ2 as ( ");
    sbSQL.append("  select T2.DT, T2.DT_KIJUN");
    sbSQL.append("  ,sum(T1.KYAKUSU_SUM) as KYAKUSU");
    sbSQL.append("  from SATTR.TNKYDD T1");
    sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD_HT");
    sbSQL.append("  group by T2.DT, T2.DT_KIJUN ");
    sbSQL.append(") ");
    sbSQL.append(",KYKZ as ( ");
    sbSQL.append("  select nvl(T1.DT,T2.DT) as DT");
    sbSQL.append("  ,nvl(T1.KYAKUSU, T2.KYAKUSU) as KYAKUSU");
    sbSQL.append("  from KYKZ1 T1 full outer join KYKZ2 T2 on T1.DT = T2.DT");
    sbSQL.append(") ");
    sbSQL.append(" select ");
    sbSQL.append("    M1.DT "); // F1
    sbSQL.append("  , M1.WEEK ");
    if (szTENPO.equals("-1")) {
      sbSQL.append("  , M1.global_EVENT ");
    } else {
      sbSQL.append("  , M1.EVENT ");
    }
    sbSQL.append("  , M1.TXT ");
    sbSQL.append("  , truncate(T1.UYOSAN,0) "); // F5
    sbSQL.append("  , truncate(T1.AYOSAN,0) ");
    sbSQL.append(
        "  , case when truncate(decimal(nvl(T3.URIKINGAKU,0)) / 1000, 0) = 0 then 0 else truncate(decimal(truncate (T1.AYOSAN, 0)) / decimal(truncate(decimal(T3.URIKINGAKU) / 1000, 0)) * 100, 1) end  "); // F7.修正予算÷F10.前年売上×100
    sbSQL.append("  , round(decimal(T3.URIKINGAKU)/1000,0) "); // F10
    sbSQL.append("  , M2.TXT ");
    if (szTENPO.equals("-1")) {
      sbSQL.append("  , M2.global_EVENT_Z ");
    } else {
      sbSQL.append("  , M2.EVENT ");
    }
    sbSQL.append("  , M2.CUSTOMER_NUM ");
    sbSQL.append("  , M2.FLOOR_CUSTOMER_NUM ");

    if (szTENPO.equals("-1")) {
      sbSQL.append("  , null "); // F15
      sbSQL.append("  , null "); // F15
      sbSQL.append("  , null ");
      sbSQL.append("  , null  ");
      sbSQL.append("  , null  ");
      sbSQL.append("  , null ");
    } else {
      sbSQL.append("  , null "); // F15
      sbSQL.append("  , M2.TENKI_AM "); // F15
      sbSQL.append("  , null ");
      sbSQL.append("  , M2.TENKI_PM ");
      sbSQL.append("  , M2.MAXKION ");
      sbSQL.append("  , M2.MINKION ");
    }
    sbSQL.append(" from ");
    sbSQL.append("  MCAL M1  ");
    sbSQL.append("  left outer join MCALZ M2 on M1.DT = M2.DT ");
    sbSQL.append("  left outer join BDYS  T1 on M1.DT = T1.DT ");
    sbSQL.append("  left outer join HAZ   T3 on M1.DT = T3.DT ");
    sbSQL.append("  left outer join DKYK  T4 on M1.DT = T4.DT ");
    sbSQL.append("  left outer join KYKZ  T5 on M1.DT = T5.DT ");
    sbSQL.append(" order by ");
    sbSQL.append("  M1.DT ");


    new ItemList();
    StringBuffer sbSQL2 = new StringBuffer();
    // 日別予算最終更新者情報
    String updUser = "";
    sbSQL2.append(" select T1.CD_UPDATE as ID");
    sbSQL2.append("  ,nvl(T2.NM_FAMILY||'　'||T2.NM_NAME, case when T1.CD_UPDATE = 0 then '自動按分' else T1.CD_UPDATE||'' end) || '　更新日時：' || TO_CHAR(T1.DT_UPDATE,'YYYY/MM/DD HH24:MI:SS') as NAME");
    sbSQL2.append(" from (");
    if (isBumon) {
      sbSQL2.append("   select * from (select CD_UPDATE, DT_UPDATE from SATYS.TTBDYS T1 where T1.DT" + szWhereK + sbWhereT + sbWhereB + " order by DT_UPDATE desc fetch first 1 rows only) T");
    } else {
      sbSQL2.append("   select * from (select CD_UPDATE, DT_UPDATE from SATYS.TTDEVT T1 where T1.DT" + szWhereK + sbWhereT + " order by DT_UPDATE desc fetch first 1 rows only) T");
      sbSQL2.append("   union");
      sbSQL2.append("   select * from (select CD_UPDATE, DT_UPDATE from SATYS.TTDKYK T1 where T1.DT" + szWhereK + sbWhereT + " order by DT_UPDATE desc fetch first 1 rows only) T");
      sbSQL2.append("   union");
      sbSQL2.append("   select * from (select CD_UPDATE, DT_UPDATE from SATYS.TTMEVT T1 where T1.NENTUKI = '" + szKikanF + "'" + sbWhereT + " order by DT_UPDATE desc fetch first 1 rows only) T");
    }
    sbSQL2.append(" ) T1");
    sbSQL2.append(" left outer join KEYSYS.SYS_USERS T2 on T1.CD_UPDATE = T2.CD_USER");
    sbSQL2.append(" order by T1.DT_UPDATE desc fetch first 1 rows only");
    @SuppressWarnings("static-access")
    JSONArray array1 = ItemList.selectJSONArray(sbSQL2.toString(), null, Defines.STR_JNDI_DS);
    if (array1.size() > 0) {
      updUser = ObjectUtils.toString(array1.getJSONObject(0).get("NAME"));
    }
    // 店別月別情報
    String comment = "";
    sbSQL2 = new StringBuffer();
    sbSQL2.append("  select T1.EVENT from SATYS.TTMEVT T1 where T1.NENTUKI = '" + szKikanF + "'" + sbWhereT);
    @SuppressWarnings("static-access")
    JSONArray array2 = ItemList.selectJSONArray(sbSQL2.toString(), null, Defines.STR_JNDI_DS);
    if (array2.size() > 0) {
      comment = ObjectUtils.toString(array2.getJSONObject(0).get("EVENT"));
    }

    // 入力期間情報取得
    boolean canChangeYosanKikan = super.canChangeYosanKikan(getMap(), userInfo);
    boolean canChangeTYosanKikan = super.canChangeTYosanKikan(getMap(), userInfo);
    boolean canChangeKyakuKikan = super.canChangeKyakuKikan(getMap(), userInfo);
    boolean canChangeEventKikan = super.canChangeEventKikan(getMap(), userInfo);
    boolean canChangeWeatherKikan = super.canChangeWeatherKikan(getMap(), userInfo);

    // オプション情報（タイトル）設定
    JSONObject option = new JSONObject();
    option.put(DefineReport.ID_PARAM_OPT_TITLE, titleList.toArray(new String[titleList.size()]));
    option.put("updUser", updUser);
    option.put("comment", comment);
    option.put("canChangeYosanKikan", canChangeYosanKikan);
    option.put("canChangeTYosanKikan", canChangeTYosanKikan);
    option.put("canChangeKyakuKikan", canChangeKyakuKikan);
    option.put("canChangeEventKikan", canChangeEventKikan);
    option.put("canChangeWeatherKikan", canChangeWeatherKikan);
    setOption(option);

    System.out.println(getClass().getSimpleName() + "[sql]" + sbSQL.toString());
    return sbSQL.toString();
  }

  private void outputQueryList() {

    // 検索条件の加工クラス作成
    JsonArrayData jad = new JsonArrayData();
    jad.setJsonString(getJson());

    // 保存用 List (検索情報)作成
    setWhere(new ArrayList<List<String>>());
    List<String> cells = new ArrayList<>();

    // タイトル名称
    cells.add(jad.getJSONText(DefineReport.ID_HIDDEN_REPORT_NAME));
    cells.add("");
    cells.add(jad.getJSONText(DefineReport.ID_HIDDEN_REPORT_NAME));
    getWhere().add(cells);

    // 空白行
    cells = new ArrayList<>();
    cells.add("");
    getWhere().add(cells);

    cells = new ArrayList<>();
    cells.add("");
    cells.add("");
    cells.add(DefineReport.Select.KIKAN.getTxt());
    cells.add(jad.getJSONText(DefineReport.Select.KIKAN_F.getObj()));
    cells.add(DefineReport.Select.TENPO.getTxt());
    cells.add(jad.getJSONText(DefineReport.Select.TENPO.getObj()));
    cells.add(DefineReport.Select.BUMON.getTxt());
    cells.add(jad.getJSONText(DefineReport.Select.BUMON.getObj()));
    getWhere().add(cells);

    // 空白行
    cells = new ArrayList<>();
    cells.add("");
    getWhere().add(cells);
  }

  final static String CommentChangeIdx = "999"; // コメントは行単位データではないため、変更時、変更配列に行番号の代わりに特殊な数値を設定し、変更したか否かを判断する

  /**
   * 更新処理実行
   *
   * @return
   *
   * @throws Exception
   */
  private JSONObject updateData(HashMap<String, String> map, User userInfo) throws Exception {
    // パラメータ確認
    String szKikanF = map.get("KIKAN_F"); // 期間FROM
    String szTenpo = map.get("TENPO"); // 店舗
    String szBumon = map.get("BUMON"); // 部門
    String[] dataIdxs = StringUtils.split(map.get("IDX"), ","); // 対象情報Index
    JSONArray dataArray = JSONArray.fromObject(map.get("DATA")); // 対象情報（予算系）
    JSONArray dataArray2 = JSONArray.fromObject(map.get("DATA2")); // 対象情報（予測客数）
    String comment = map.get("COMMENT"); // コメント

    String outobj = map.get(DefineReport.ID_PARAM_OBJ); // 実行ボタン

    JSONObject msgObj = new JSONObject();

    // ログインユーザー情報取得
    int userId = userInfo.getCD_user(); // ログインユーザー

    // 店舗単位情報更新
    if (StringUtils.equals(DefineReport.Values.ALL.getVal(), szBumon)) {
      // 更新情報
      String values1 = "", values2 = "", values3 = "", values4 = "";
      ArrayList<String> params1 = new ArrayList<>(), params2 = new ArrayList<>(), params3 = new ArrayList<>();
      for (int i = 0; i < dataArray.size(); i++) {
        JSONObject data = dataArray.getJSONObject(i);
        if (data.isEmpty()) {
          continue;
        }
        Integer yosan = NumberUtils.toInt(data.optString("F3"));
        values1 += ",('" + szTenpo + "','" + data.optString("F1") + "', ? ," + yosan + ")"; // 店日別情報(TTDEVT)
        params1.add(data.optString("F2"));
      }
      values1 = StringUtils.removeStart(values1, ",");
      for (int i = 0; i < dataArray2.size(); i++) {
        JSONObject data = dataArray2.getJSONObject(i);
        if (data.isEmpty()) {
          continue;
        }
        Integer kyaku = NumberUtils.toInt(data.optString("F2"));
        values2 += ",('" + szTenpo + "','" + data.optString("F1") + "'," + kyaku + ")"; // 店日別予想客数(TTDKYK)
      }
      values2 = StringUtils.removeStart(values2, ",");
      if (ArrayUtils.contains(dataIdxs, CommentChangeIdx)) {
        values3 = "('" + szTenpo + "','" + szKikanF + "',?)"; // 店月別情報(TTMEVT)
        params3.add(comment);
      }
      values4 = StringUtils.removeStart(values4, ",");

      if (!outobj.equals(DefineReport.Button.ANBUN.getObj()) && values1.length() == 0 && values2.length() == 0 && values3.length() == 0) {
        msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E00001.getVal()));
        return msgObj;
      }

      // 基本INSERT/UPDATE文
      ArrayList<String> sqlList = new ArrayList<>();
      ArrayList<ArrayList<String>> prmList = new ArrayList<>();
      ArrayList<String> lblList = new ArrayList<>();
      ArrayList<Integer> countList = new ArrayList<>();
      StringBuffer sbSQL;
      if (values1.length() > 0) {
        // 店日別情報(TTDEVT)
        sbSQL = new StringBuffer();
        sbSQL.append("merge into SATYS.TTDEVT as T");
        sbSQL.append(" using (select");
        sbSQL.append(" cast(T1.MISECD as character(3)) as MISECD"); // 店コード
        sbSQL.append(",cast(T1.DT as character(8)) as DT"); // 予算年月日
        sbSQL.append(",cast(T1.EVENT as varchar(1000)) as EVENT"); // イベント
        sbSQL.append(",cast(T1.TYOSAN as decimal(9, 0)) as TYOSAN"); // 店長予算案
        sbSQL.append(",cast(" + userId + " as integer) as CD_UPDATE"); // 更新者
        sbSQL.append(",current timestamp as DT_UPDATE"); // 更新日
        sbSQL.append(" from (values" + values1 + ") as T1(MISECD, DT, EVENT, TYOSAN)");
        sbSQL.append(" ) as RE on (T.MISECD = RE.MISECD and T.DT = RE.DT) ");
        sbSQL.append(" when matched then ");
        sbSQL.append(" update set");
        sbSQL.append("  EVENT    =RE.EVENT");
        sbSQL.append(" ,TYOSAN   =RE.TYOSAN");
        sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
        sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
        sbSQL.append(" when not matched then ");
        sbSQL.append(" insert");
        sbSQL.append(" values(RE.MISECD,RE.DT,RE.EVENT,RE.TYOSAN,RE.CD_UPDATE,RE.DT_UPDATE,RE.CD_UPDATE,RE.DT_UPDATE)");
        sqlList.add(sbSQL.toString());
        prmList.add(params1);
        lblList.add("店日別情報");
      }
      if (values2.length() > 0) {
        // 店日別予想客数(TTDKYK)
        sbSQL = new StringBuffer();
        sbSQL.append("merge into SATYS.TTDKYK as T");
        sbSQL.append(" using (select");
        sbSQL.append(" cast(T1.MISECD as character(3)) as MISECD"); // 店コード
        sbSQL.append(",cast(T1.DT as character(8)) as DT"); // 予算年月日
        sbSQL.append(",cast(T1.KYAKUSU as decimal(7, 0)) as KYAKUSU"); // 予想客数
        sbSQL.append(",cast(" + userId + " as integer) as CD_UPDATE"); // 更新者
        sbSQL.append(",current timestamp as DT_UPDATE"); // 更新日
        sbSQL.append(" from (values" + values2 + ") as T1(MISECD, DT, KYAKUSU)");
        sbSQL.append(" ) as RE on (T.MISECD = RE.MISECD and T.DT = RE.DT) ");
        sbSQL.append(" when matched then ");
        sbSQL.append(" update set");
        sbSQL.append("  KYAKUSU   =RE.KYAKUSU");
        sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
        sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
        sbSQL.append(" when not matched then ");
        sbSQL.append(" insert");
        sbSQL.append(" values(RE.MISECD,RE.DT,RE.KYAKUSU,RE.CD_UPDATE,RE.DT_UPDATE,RE.CD_UPDATE,RE.DT_UPDATE)");
        sqlList.add(sbSQL.toString());
        prmList.add(params2);
        lblList.add("店日別予想客数");
      }
      if (values3.length() > 0) {
        // 店月別情報(TTMEVT)
        sbSQL = new StringBuffer();
        sbSQL.append("merge into SATYS.TTMEVT as T");
        sbSQL.append(" using (select");
        sbSQL.append(" cast(T1.MISECD as character(3)) as MISECD"); // 店コード
        sbSQL.append(",cast(T1.NENTUKI as decimal(6, 0)) as NENTUKI"); // 年月
        sbSQL.append(",cast(T1.EVENT as varchar(1000)) as EVENT"); // イベント
        sbSQL.append(",cast(" + userId + " as integer) as CD_UPDATE"); // 更新者
        sbSQL.append(",current timestamp as DT_UPDATE"); // 更新日
        sbSQL.append(" from (values" + values3 + ") as T1(MISECD, NENTUKI, EVENT)");
        sbSQL.append(" ) as RE on (T.MISECD = RE.MISECD and T.NENTUKI = RE.NENTUKI) ");
        sbSQL.append(" when matched then ");
        sbSQL.append(" update set");
        sbSQL.append("  EVENT    =RE.EVENT");
        sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
        sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
        sbSQL.append(" when not matched then ");
        sbSQL.append(" insert");
        sbSQL.append(" values(RE.MISECD,RE.NENTUKI,RE.EVENT,RE.CD_UPDATE,RE.DT_UPDATE,RE.CD_UPDATE,RE.DT_UPDATE)");
        sqlList.add(sbSQL.toString());
        prmList.add(params3);
        lblList.add("店月別情報");
      }
      if (values4.length() > 0) {
        // 昨年天気午前_午後
        sbSQL = new StringBuffer();
        sbSQL.append("merge into SATYS.TTMEVT as T");
        sbSQL.append(" using (select");
        sbSQL.append(" cast(T1.MISECD as character(3)) as MISECD"); // 店コード
        sbSQL.append(",cast(T1.NENTUKI as decimal(6, 0)) as NENTUKI"); // 年月
        sbSQL.append(",cast(T1.EVENT as varchar(1000)) as EVENT"); // イベント
        sbSQL.append(",cast(" + userId + " as integer) as CD_UPDATE"); // 更新者
        sbSQL.append(",current timestamp as DT_UPDATE"); // 更新日
        sbSQL.append(" from (values" + values3 + ") as T1(MISECD, NENTUKI, EVENT)");
        sbSQL.append(" ) as RE on (T.MISECD = RE.MISECD and T.NENTUKI = RE.NENTUKI) ");
        sbSQL.append(" when matched then ");
        sbSQL.append(" update set");
        sbSQL.append("  EVENT    =RE.EVENT");
        sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
        sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
        sbSQL.append(" when not matched then ");
        sbSQL.append(" insert");
        sbSQL.append(" values(RE.MISECD,RE.NENTUKI,RE.EVENT,RE.CD_UPDATE,RE.DT_UPDATE,RE.CD_UPDATE,RE.DT_UPDATE)");
        sqlList.add(sbSQL.toString());
        prmList.add(params3);
        lblList.add("店月別情報");
      }
      // 按分処理実行
      if (outobj.equals(DefineReport.Button.ANBUN.getObj())) {
        lblList.add("店部門別日別予算");
        countList = this.executeAnbun(sqlList, prmList, map, userInfo);
      } else {
        countList = super.executeSQLs(sqlList, prmList);
      }
      if (StringUtils.isEmpty(getMessage())) {
        int count = 0;
        for (Integer element : countList) {
          count += element;
          // System.out.println(MessageUtility.getMessage(Msg.S00006.getVal(), new String[]{lblList.get(i), Integer.toString(countList.get(i))}));
        }
        if (count == 0) {
          msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E10000.getVal()));
        } else {
          msgObj.put(MsgKey.S.getKey(), MessageUtility.getMessage(Msg.S00001.getVal()));
        }
      } else {
        msgObj.put(MsgKey.E.getKey(), getMessage());
      }

      // 部門単位
    } else {
      // 更新情報
      String values = "";
      for (int i = 0; i < dataArray.size(); i++) {
        JSONObject data = dataArray.getJSONObject(i);
        if (data.isEmpty()) {
          continue;
        }
        Integer yosan = NumberUtils.toInt(data.optString("F2"));
        values += ",('" + szTenpo + "','" + data.optString("F1") + "','" + szBumon + "'," + yosan + ")";
      }
      values = StringUtils.removeStart(values, ",");

      if (values.length() == 0) {
        msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E10000.getVal()));
        return msgObj;
      }

      // 基本INSERT/UPDATE文
      StringBuffer sbSQL;
      ArrayList<String> prmData = new ArrayList<>();
      // 店部門別日別予算(TTBDYS)
      sbSQL = new StringBuffer();
      sbSQL.append("merge into SATYS.TTBDYS as T");
      sbSQL.append(" using (select");
      sbSQL.append(" cast(T1.MISECD as character(3)) as MISECD"); // 店コード
      sbSQL.append(",cast(T1.DT as character(8)) as DT"); // 予算年月日
      sbSQL.append(",cast(to_char(current date, 'YYYYMMDD') as character(8)) as DT_ENTRY"); // エントリー年月日
      sbSQL.append(",cast(T1.BUNBMC as CHARACTER(4)) as BUNBMC"); // 部門コード
      sbSQL.append(",cast(T1.AYOSAN as decimal(9, 0)) as AYOSAN"); // 按分予算
      sbSQL.append(",cast(" + userId + " as integer) as CD_UPDATE"); // 更新者
      sbSQL.append(",current timestamp as DT_UPDATE"); // 更新日
      sbSQL.append(" from (values" + values + ") as T1(MISECD, DT, BUNBMC, AYOSAN)");
      sbSQL.append(" ) as RE on (T.MISECD = RE.MISECD and T.DT = RE.DT and T.BUNBMC = RE.BUNBMC) ");
      sbSQL.append(" when matched then ");
      sbSQL.append(" update set");
      sbSQL.append("  AYOSAN   =RE.AYOSAN");
      sbSQL.append(" ,DT_ENTRY =RE.DT_ENTRY");
      sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
      sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
      super.executeSQL(sbSQL.toString(), prmData);
      if (StringUtils.isEmpty(getMessage())) {
        // System.out.println(MessageUtility.getMessage(Msg.S00006.getVal(), new String[]{"店部門別日別予算", Integer.toString(count)}));
        msgObj.put(MsgKey.S.getKey(), MessageUtility.getMessage(Msg.S00001.getVal()));
      } else {
        msgObj.put(MsgKey.E.getKey(), getMessage());
      }
    }
    return msgObj;
  }

  /**
   * チェック処理
   *
   * @param userInfo
   *
   * @throws Exception
   */
  public JSONArray check(HashMap<String, String> map, User userInfo) {
    map.get("KIKAN_F");
    map.get("TENPO");
    String szBumon = map.get("BUMON"); // 部門
    String dataIdx = map.get("IDX"); // 対象情報Index
    JSONArray dataArray = JSONArray.fromObject(map.get("DATA")); // 対象情報（予算系）
    JSONArray dataArray2 = JSONArray.fromObject(map.get("DATA2")); // 対象情報（予測客数）
    String comment = map.get("COMMENT"); // コメント

    String outobj = map.get(DefineReport.ID_PARAM_OBJ); // 実行ボタン



    JSONArray msg = new JSONArray();

    // チェック処理
    if (!super.isUserTenpo(map, userInfo)) {
      msg.add(MessageUtility.getMessageObj(Msg.E00001.getVal()));
      return msg;
    }
    // 対象件数チェック
    if (dataIdx.isEmpty()) {
      // 按分
      if (outobj.equals(DefineReport.Button.ANBUN.getObj())) {
        return msg;
      } else {
        msg.add(MessageUtility.getMessageObj(Msg.E10000.getVal()));
        return msg;
      }
    }

    // 各データ行チェック
    FieldType fieldType = FieldType.GRID;
    // 店舗単位情報更新
    if (StringUtils.equals(DefineReport.Values.ALL.getVal(), szBumon)) {
      for (int i = 0; i < dataArray.size(); i++) {
        JSONObject data = dataArray.getJSONObject(i);
        String rowIndex = data.optString("IDX");
        // 今年の要因
        if (!InputChecker.isTextLessThanMaxByteLength(data.optString("F2"), DefineReport.InpText.EVENT_DD.getLen())) {
          msg.add(MessageUtility.getCheckMaxLengthMessage(DefineReport.InpText.EVENT_DD, fieldType, new String[] {rowIndex}));
        }
        // 店長予算案
        if (!InputChecker.isTextLessThanMaxByteLength(data.optString("F3"), DefineReport.InpText.TYOSAN.getLen())) {
          msg.add(MessageUtility.getCheckMaxLengthMessage(DefineReport.InpText.TYOSAN, fieldType, new String[] {rowIndex}));
        } else if (!InputChecker.isZeroOrPlusInteger(data.optString("F3"))) {
          msg.add(MessageUtility.getCheckZeroOrPlusIntegerMessage(DefineReport.InpText.TYOSAN, fieldType, new String[] {rowIndex}));
        }
      }
      for (int i = 0; i < dataArray2.size(); i++) {
        JSONObject data = dataArray2.getJSONObject(i);
        String rowIndex = data.optString("IDX");
        // 予測客数
        if (!InputChecker.isTextLessThanMaxByteLength(data.optString("F2"), DefineReport.InpText.KYAKUSU.getLen())) {
          msg.add(MessageUtility.getCheckMaxLengthMessage(DefineReport.InpText.KYAKUSU, fieldType, new String[] {rowIndex}));
        } else if (!InputChecker.isZeroOrPlusInteger(data.optString("F2"))) {
          msg.add(MessageUtility.getCheckZeroOrPlusIntegerMessage(DefineReport.InpText.KYAKUSU, fieldType, new String[] {rowIndex}));
        }
      }
      String[] dataIdxs = StringUtils.split(dataIdx, ","); // 対象情報Index
      if (ArrayUtils.contains(dataIdxs, CommentChangeIdx)) {
        // 今年の要因
        if (!InputChecker.isTextLessThanMaxByteLength(comment, DefineReport.InpText.EVENT_MM.getLen())) {
          msg.add(MessageUtility.getCheckMaxLengthMessage(DefineReport.InpText.EVENT_MM, FieldType.DEFAULT));
        }
      }

    } else {
      for (int i = 0; i < dataArray.size(); i++) {
        JSONObject data = dataArray.getJSONObject(i);
        String rowIndex = data.optString("IDX");
        // 修正予算
        if (!InputChecker.isTextLessThanMaxByteLength(data.optString("F2"), DefineReport.InpText.AYOSAN.getLen())) {
          msg.add(MessageUtility.getCheckMaxLengthMessage(DefineReport.InpText.AYOSAN, fieldType, new String[] {rowIndex}));
        } else if (!InputChecker.isZeroOrPlusInteger(data.optString("F2"))) {
          msg.add(MessageUtility.getCheckZeroOrPlusIntegerMessage(DefineReport.InpText.AYOSAN, fieldType, new String[] {rowIndex}));
        }
      }
    }
    return msg;
  }

  /**
   * 更新＋按分処理
   *
   * @param sqlCommands
   * @param paramDatas
   * @param jdbcName
   * @return 実行件数
   * @throws Exception
   */
  public ArrayList<Integer> executeAnbun(ArrayList<String> commands, ArrayList<ArrayList<String>> paramDatas, HashMap<String, String> map, User userInfo) throws Exception {
    ArrayList<Integer> countList = new ArrayList<>();

    // コネクションの取得
    Connection con = null;
    try {
      con = DBConnection.getConnection(this.JNDIname);
    } catch (Exception e1) {
      e1.printStackTrace();
      return countList;
    }

    PreparedStatement statement = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd = null;
    long startTime, stop, diff;

    try {
      con.setAutoCommit(false);

      for (int index = 0; index < commands.size(); index++) {
        String command = commands.get(index);
        ArrayList<String> paramData = paramDatas.get(index);

        // 実行SQL設定
        statement = con.prepareStatement(command);

        // パラメータ設定
        for (int i = 0; i < paramData.size(); i++) {
          statement.setString((i + 1), paramData.get(i));
        }
        startTime = System.currentTimeMillis();

        // SQL実行
        if (DefineReport.ID_DEBUG_MODE)
          System.out.println("[sql]" + command + "[prm]" + (paramData == null ? "" : StringUtils.join(paramData.toArray(), ",")));
        // SQL実行
        int count = statement.executeUpdate();
        countList.add(count);

        stop = System.currentTimeMillis();
        diff = stop - startTime;
        if (DefineReport.ID_DEBUG_MODE)
          System.out.println("TIME:" + diff + " ms" + " COUNT:" + count);
      }

      // 更新が終わったところで按分処理
      String szKikanF = map.get("KIKAN_F"); // 期間FROM
      String szTenpo = map.get("TENPO"); // 店舗

      String szDtF = szKikanF + "01";
      String szDtT = CmnDate.dateFormat(CmnDate.getLastDateOfMonth(szKikanF + "01"));
      String szWhereK = " between '" + szDtF + "' and '" + szDtT + "'"; // 期間条件
      String sbWhereT = " and T1.MISECD = '" + szTenpo + "'"; // 店舗条件


      StringBuffer sbSQL = new StringBuffer();
      sbSQL.append(" select");
      sbSQL.append("    T1.MISECD");
      sbSQL.append("  , T1.DT");
      sbSQL.append("  , T1.BUNBMC");
      sbSQL.append("  , T2.TYOSAN_MM");
      sbSQL.append("  , T2.TYOSAN");
      sbSQL.append("  , truncate(sum(truncate(decimal(T2.TYOSAN)*T1.BYOSAN_KOSEHI)) over(),0) as TYOSAN_MM_N");
      sbSQL.append("  , truncate(sum(truncate(decimal(T2.TYOSAN)*T1.BYOSAN_KOSEHI)) over(partition by T1.DT),0) as TYOSAN_N");
      sbSQL.append("  , truncate(decimal(T2.TYOSAN)*T1.BYOSAN_KOSEHI) as YOSAN");
      sbSQL.append("  , T1.BYOSAN_RANK");
      sbSQL.append("  , T2.TYOSAN_RANK");
      sbSQL.append(" from (");
      sbSQL.append("  select ");
      sbSQL.append("      T1.MISECD");
      sbSQL.append("    , T1.DT");
      sbSQL.append("    , T1.BUNBMC");
      sbSQL.append("    , T1.UYOSAN_MM");
      sbSQL.append("    , T1.BYOSAN");
      sbSQL.append("    , T1.UYOSAN");
      sbSQL.append("    , T1.BYOSAN_KOSEHI");
      sbSQL.append("    , dense_rank() over(order by T1.BYOSAN_KOSEHI desc, T1.BUNBMC) as BYOSAN_RANK ");
      sbSQL.append("  from (");
      sbSQL.append("    select");
      sbSQL.append("      T1.MISECD");
      sbSQL.append("    , T1.DT");
      sbSQL.append("    , T1.BUNBMC");
      sbSQL.append("    , sum(T1.UYOSAN) over () as UYOSAN_MM");
      sbSQL.append("    , sum(T1.UYOSAN) over (partition by T1.BUNBMC) as BYOSAN");
      sbSQL.append("    , T1.UYOSAN");
      sbSQL.append("    , case when sum(T1.UYOSAN) over () = 0 then 0 ");
      sbSQL.append("      else decimal (sum(T1.UYOSAN) over (partition by T1.BUNBMC)) / sum(T1.UYOSAN) over ()");
      sbSQL.append("      end as BYOSAN_KOSEHI");
      sbSQL.append("    from");
      sbSQL.append("      SATYS.TTBDYS T1 ");
      sbSQL.append("    where T1.DT " + szWhereK + sbWhereT);
      sbSQL.append("  ) T1");
      sbSQL.append("  order by BUNBMC");
      sbSQL.append(" ) T1");
      sbSQL.append(" inner join (");
      sbSQL.append("  select ");
      sbSQL.append("    T1.MISECD");
      sbSQL.append("  , T1.DT");
      sbSQL.append("  , T1.TYOSAN_MM");
      sbSQL.append("  , T1.TYOSAN");
      sbSQL.append("  , T1.TYOSAN_KOSEHI");
      sbSQL.append("  , ROW_NUMBER() OVER(ORDER BY T1.TYOSAN_KOSEHI desc, T1.DT) as TYOSAN_RANK");
      sbSQL.append("  from (");
      sbSQL.append("    select");
      sbSQL.append("      T1.MISECD");
      sbSQL.append("    , T1.DT");
      sbSQL.append("    , sum(T1.TYOSAN) over () as TYOSAN_MM");
      sbSQL.append("    , T1.TYOSAN");
      sbSQL.append("    , case when sum(T1.TYOSAN) over () = 0 then 0 ");
      sbSQL.append("      else decimal (T1.TYOSAN) / sum(T1.TYOSAN) over ()");
      sbSQL.append("      end as TYOSAN_KOSEHI");
      sbSQL.append("    from");
      sbSQL.append("      SATYS.TTDEVT T1 ");
      sbSQL.append("    where T1.DT " + szWhereK + sbWhereT);
      sbSQL.append("  ) T1");
      sbSQL.append(" ) T2 on T1.MISECD = T2.MISECD and T1.DT = T2.DT");
      sbSQL.append(" order by BYOSAN_RANK, TYOSAN_RANK");

      String command = sbSQL.toString();
      // 実行SQL設定
      statement = con.prepareStatement(command);
      startTime = System.currentTimeMillis();
      // SQL実行
      if (DefineReport.ID_DEBUG_MODE)
        System.out.println("[sql]" + command);
      rs = statement.executeQuery();
      stop = System.currentTimeMillis();
      diff = stop - startTime;
      if (DefineReport.ID_DEBUG_MODE)
        System.out.println("TIME:" + diff + " ms");

      // 結果の取得
      long inpSumTyosan = 0l, calSumTyosan = 0l;
      Map<String, JSONObject> uYosan = new LinkedHashMap<>();
      Map<String, JSONObject> bYosan = new LinkedHashMap<>();
      Map<String, JSONObject> tYosan = new LinkedHashMap<>();
      rsmd = rs.getMetaData();
      int sizeColumn = rsmd.getColumnCount(); // カラム数
      int index = 0;
      while (rs.next()) {
        JSONObject obj = new JSONObject();
        for (int i = 1; i <= sizeColumn; i++) {
          obj.put(rsmd.getColumnName(i), rs.getString(i));
        }
        if (index == 0) {
          inpSumTyosan = obj.optLong("TYOSAN_MM", 0);
          calSumTyosan = obj.optLong("TYOSAN_MM_N", 0);
        }
        String bmn = StringUtils.trim(obj.optString("BUNBMC"));
        if (!bYosan.containsKey(bmn)) {
          JSONObject bObj = new JSONObject();
          bObj.put("BYOSAN_RANK", obj.optString("BYOSAN_RANK"));
          bYosan.put(bmn, bObj);
        }
        String dt = obj.optString("DT");
        if (!tYosan.containsKey(dt)) {
          JSONObject tObj = new JSONObject();
          tObj.put("TYOSAN_RANK", obj.optString("TYOSAN_RANK"));
          tObj.put("TYOSAN", obj.optString("TYOSAN"));
          tObj.put("TYOSAN_N", obj.optString("TYOSAN_N"));
          tYosan.put(dt, tObj);
        }
        // 行データ格納
        uYosan.put(dt + bmn, obj);
        index++;
      }


      // 差分調整処理：入力店長予算案合計＝按分店長予算合計にする
      // ※以前は店長予算案＝部門予算にする必要があったため、部門予算構成比の大きい順に、部門予算いっぱいまで足りない予算額を店長予算構成比の大きい順に足していき次の部門に移ったが、
      // 今回の修正で店長予算案≠部門予算になったため、部門構成比の大きい順で足りない予算額を店長予算構成比の大きい順に足していき、一巡したら次の部門に移る。
      boolean loopErr = false;
      int loopCnt = 0;
      // 入力店長予算案合計が、按分計算店長予算案の合計より大きい場合ループ
      while (inpSumTyosan > calSumTyosan) {
        for (String bkey : bYosan.keySet()) {
          for (String tkey : tYosan.keySet()) {
            // System.out.println(bkey + " / " + tkey + " 画面入力合計:"+inpSumTyosan + " 按分計算合計:"+calSumTyosan
            // +" TYOSAN:"+tYosan.get(tkey).getLong("TYOSAN")+" TYOSAN_N:"+tYosan.get(tkey).getLong("TYOSAN_N")
            // +" YOSAN:"+uYosan.get(tkey+bkey).getLong("YOSAN"));
            // 無限エラー対策
            loopCnt++;
            if (loopCnt > 1000000) {
              loopErr = true;
              break;
            }
            // 入力店長予算案が、按分計算店長予算案と一致する場合、端数調整は行わない
            if (tYosan.get(tkey).getLong("TYOSAN") == tYosan.get(tkey).getLong("TYOSAN_N")) {
              continue;
            }
            calSumTyosan += 1;
            tYosan.get(tkey).put("TYOSAN_N", tYosan.get(tkey).getLong("TYOSAN_N") + 1);
            uYosan.get(tkey + bkey).put("YOSAN", uYosan.get(tkey + bkey).getLong("YOSAN") + 1);
            // 合計値が一致した場合終了
            if (inpSumTyosan == calSumTyosan) {
              break;
            }
          }
          // 合計値が一致した場合終了
          if ((inpSumTyosan == calSumTyosan) || loopErr) {
            break;
          }
        }
        if (loopErr) {
          break;
        }
      }
      if (loopErr) {
        setMessage(DefineReport.ID_MSG_APP_EXCEPTION);
        throw new Exception();
      }

      // ログインユーザー情報取得
      int userId = userInfo.getCD_user(); // ログインユーザー

      // 更新情報
      String values = "";
      for (Map.Entry<String, JSONObject> e : uYosan.entrySet()) {
        JSONObject data = e.getValue();
        Long yosan = data.optLong("YOSAN", 0);
        values += ",('" + data.optString("MISECD") + "','" + data.optString("DT") + "','" + data.optString("BUNBMC") + "'," + yosan + ")";
      }
      values = StringUtils.removeStart(values, ",");

      sbSQL = new StringBuffer();
      sbSQL.append("merge into SATYS.TTBDYS as T");
      sbSQL.append(" using (select");
      sbSQL.append("  cast(T1.MISECD as character(3)) as MISECD"); // 店コード
      sbSQL.append(" ,cast(T1.DT as character(8)) as DT"); // 予算年月日
      sbSQL.append(" ,cast(to_char(current date, 'YYYYMMDD') as character(8)) as DT_ENTRY"); // エントリー年月日
      sbSQL.append(" ,cast(T1.BUNBMC as character (4)) as BUNBMC"); // 部門コード
      sbSQL.append(" ,cast(T1.TYOSAN as decimal(9, 0)) as TYOSAN"); // 店長予算案
      sbSQL.append(" ,cast(" + userId + " as integer) as CD_UPDATE"); // 更新者
      sbSQL.append(" ,current timestamp as DT_UPDATE"); // 更新日
      sbSQL.append(" from (values" + values + ") as T1(MISECD, DT, BUNBMC, TYOSAN)");
      sbSQL.append(") as RE on (T.MISECD = RE.MISECD and T.DT = RE.DT and T.BUNBMC = RE.BUNBMC) ");
      sbSQL.append(" when matched then ");
      sbSQL.append(" update set");
      sbSQL.append("  TYOSAN   =RE.TYOSAN");
      sbSQL.append(" ,DT_ENTRY =RE.DT_ENTRY");
      sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
      sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");

      command = sbSQL.toString();

      // 実行SQL設定
      statement.close();
      statement = con.prepareStatement(command);

      // パラメータ設定
      startTime = System.currentTimeMillis();

      // SQL実行
      if (DefineReport.ID_DEBUG_MODE)
        System.out.println("[sql]" + command);
      // SQL実行
      int count = statement.executeUpdate();
      countList.add(count);

      stop = System.currentTimeMillis();
      diff = stop - startTime;
      if (DefineReport.ID_DEBUG_MODE)
        System.out.println("TIME:" + diff + " ms" + " COUNT:" + count);

      con.commit();

    } catch (SQLException e) {
      countList = new ArrayList<>();
      rollback(con);
      e.printStackTrace();
      if (DefineReport.ID_SQLSTATE_CONNECTION_RESET.equals(e.getSQLState())) {
        // 通信切断
        setMessage(DefineReport.ID_MSG_CONNECTION_REST + "(" + e.getSQLState() + ")");
      } else {
        // その他SQLエラー
        setMessage(DefineReport.ID_MSG_SQL_EXCEPTION + e.getMessage());
      }

    } catch (Exception e) {
      countList = new ArrayList<>();
      rollback(con);
      e.printStackTrace();

    } finally {
      close(rs);
      close(statement);
      close(con);
    }
    return countList;
  }
}
