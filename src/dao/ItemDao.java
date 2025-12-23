/**
 *
 */
package dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.iq80.snappy.CorruptionException;
import org.iq80.snappy.Snappy;
import authentication.bean.User;
import common.CmnDate;
import common.CmnDate.DATE_FORMAT;
import common.DefineReport;
import common.Defines;
import common.ItemList;
import common.JsonArrayData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 */
public class ItemDao implements ItemInterface {

  /** JNDI */
  protected String JNDIname;

  /** パラメータ関係 */
  private HashMap<String, String> map;

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#getMap()
   */
  public HashMap<String, String> getMap() {
    return map;
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#setMap(java.util.HashMap)
   */
  public void setMap(HashMap<String, String> map) {
    this.map = map;
  }

  /** json 情報 */
  private String json;

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#getJson()
   */
  public String getJson() {
    return json;
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#setJson(java.lang.String)
   */
  public void setJson(String json) {
    this.json = json;
  }

  /** 検索条件（Excel出力用） */
  private ArrayList<List<String>> where;

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#getWhere()
   */
  public ArrayList<List<String>> getWhere() {
    return where;
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#setWhere(java.util.ArrayList)
   */
  public void setWhere(ArrayList<List<String>> where) {
    this.where = where;
  }

  /** メタ情報（Excel出力用） */
  private ArrayList<Integer> meta;

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#getMeta()
   */
  public ArrayList<Integer> getMeta() {
    return meta;
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#setMeta(java.util.ArrayList)
   */
  public void setMeta(ArrayList<Integer> meta) {
    this.meta = meta;
  }

  /** 開始レコード */
  private int start;

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#getStart()
   */
  public int getStart() {
    return start;
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#setStart(int)
   */
  public void setStart(int start) {
    this.start = start;
  }

  /** 取得レコード数 */
  private int limit;

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#getLimit()
   */
  public int getLimit() {
    return limit;
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#setLimit(int)
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }

  /** DB検索用パラメータ配列 */
  private ArrayList<String> paramData;

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#getParamData()
   */
  public ArrayList<String> getParamData() {
    return paramData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#setParamData(java.util.ArrayList)
   */
  public void setParamData(ArrayList<String> paramData) {
    this.paramData = paramData;
  }

  /** DB検索結果（0レコード＝タイトル） */
  private ArrayList<byte[]> table;

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#getTable()
   */
  public ArrayList<byte[]> getTable() {
    return table;
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#setTable(java.util.ArrayList)
   */
  public void setTable(ArrayList<byte[]> table) {
    this.table = table;
  }

  /**
   * 新しいjdbcItemDaoのインスタンスを生成します。
   * 
   * @param source
   */
  public ItemDao(String JNDIname) {

    this.JNDIname = JNDIname;

    // 保存用 List (レコード情報)作成
    table = new ArrayList<byte[]>();

    // 配列準備
    paramData = new ArrayList<String>();

    // メタ情報
    meta = new ArrayList<Integer>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#selectBy()
   */
  public boolean selectBy() {
    return selectBySQL("");
  }

  /*
   * (non-Javadoc)
   * 
   * @see dao.ItemInterface#selectBySQL(java.lang.String)
   */
  public boolean selectBySQL(String command) {

    // コネクションの取得
    Connection con = null;
    try {
      con = DBConnection.getConnection(this.JNDIname);
    } catch (Exception e1) {
      e1.printStackTrace();
      return false;
    }

    // ログインユーザー情報取得
    User userInfo = getUserInfo();
    if (userInfo == null) {
      return false;
    }

    ResultSet rs = null;
    PreparedStatement statement = null;
    long startTime, stop, diff;

    if ("".equals(command)) {
      return false;
    }
    try {

      // 実行SQL設定
      statement = con.prepareStatement(command);

      // パラメータ判断
      setParamData(new ArrayList<String>());
      for (int i = 0; i < getParamData().size(); i++) {
        statement.setString((i + 1), (String) getParamData().get(i));
      }

      startTime = System.currentTimeMillis();

      // SQL実行
      rs = statement.executeQuery();

      stop = System.currentTimeMillis();
      diff = stop - startTime;
      // 現在日時情報で初期化されたインスタンスの取得
      LocalDateTime nowDateTime = LocalDateTime.now();
      DateTimeFormatter java8Format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
      // 日時情報を指定フォーマットの文字列で取得
      String java8Disp = nowDateTime.format(java8Format);
      // 日時(yyyy/MM/dd HH:mm:ss.SSS) [RBP] レポート ユーザーID 実効時間(ミリ秒) コマンド
      System.out.println(java8Disp + "\t[YOS]\t" + getMap().get("report") + "\t" + userInfo.getId() + "\t" + diff + "\t" + command);

      this.setTable(new ArrayList<byte[]>());

      // カラム数
      ResultSetMetaData rsmd = rs.getMetaData();
      int sizeColumn = rsmd.getColumnCount();

      // タイトル名称取得
      List<String> titles = new ArrayList<String>();
      for (int i = 1; i <= sizeColumn; i++) {
        titles.add(rsmd.getColumnName(i));
      }
      getTable().add(Snappy.compress(StringUtils.join(titles.toArray(new String[titles.size()]), "\t").getBytes("UTF-8")));

      // メタ情報（名称）
      for (int i = 1; i <= sizeColumn; i++) {
        meta.add(rsmd.getColumnType(i));// 列の SQL 型
      }

      // 結果の取得
      DecimalFormat df = new DecimalFormat();
      df.setMaximumFractionDigits(1);
      df.setMinimumFractionDigits(1);

      while (rs.next()) {

        // 情報保存
        List<String> cols = new ArrayList<String>();

        for (int i = 1; i <= sizeColumn; i++) {

          // タイプ別取得
          switch (rsmd.getColumnType(i)) {
            case Types.DECIMAL:
              if (null == rs.getString(i)) {
                cols.add("");
              } else if (rsmd.getScale(i) == 0 && rs.getDouble(i) <= 2147483647 && rs.getDouble(i) >= -2147483647) {
                cols.add(Integer.toString((int) rs.getDouble(i)));
              } else {
                cols.add(String.valueOf(rs.getDouble(i)));
              }
              break;
            case Types.INTEGER:
              if (null == rs.getString(i)) {
                cols.add("");
              } else {
                cols.add(String.valueOf(rs.getInt(i)));
              }
              break;
            default:
              if (null == rs.getString(i)) {
                cols.add("");
              } else {
                cols.add(rs.getString(i));
              }
          }

        }

        // 情報保存（レコード）
        getTable().add(Snappy.compress(StringUtils.join(cols.toArray(new String[cols.size()]), "\t").getBytes("UTF-8")));
      }

    } catch (SQLException e) {
      rollback(con);
      e.printStackTrace();
      if (DefineReport.ID_SQLSTATE_COLUMN_GREATER.equals(e.getSQLState()) || DefineReport.ID_SQLSTATE_APPLICATION_HEPE.equals(e.getSQLState()) || DefineReport.ID_SQLSTATE_BUFFER_GREATER.equals(e.getSQLState()) || DefineReport.ID_SQLSTATE_COLUMN_OVER.equals(e.getSQLState())) {
        // 横軸（列）が多すぎる場合
        setMessage(DefineReport.ID_MSG_COLUMN_GREATER + "(" + e.getSQLState() + ")");
      } else if (DefineReport.ID_SQLSTATE_CONNECTION_RESET.equals(e.getSQLState())) {
        // 通信切断
        setMessage(DefineReport.ID_MSG_CONNECTION_REST + "(" + e.getSQLState() + ")");
      } else {
        // その他SQLエラー
        setMessage(DefineReport.ID_MSG_SQL_EXCEPTION + e.getMessage());
      }

    } catch (Exception e) {
      e.printStackTrace();

    } finally {
      close(rs);
      close(statement);
      close(con);
    }
    return false;
  }

  /**
   * 更新処理
   * 
   * @param sqlCommand
   * @param paramData
   * @param jdbcName
   * @return 実行件数
   * @throws Exception
   */
  public Integer executeSQL(String command, ArrayList<String> paramData) throws Exception {
    int count = 0;


    // コネクションの取得
    Connection con = null;
    try {
      con = DBConnection.getConnection(this.JNDIname);
    } catch (Exception e1) {
      e1.printStackTrace();
      return count;
    }

    PreparedStatement statement = null;
    long startTime, stop, diff;

    if ("".equals(command)) {
      return count;
    }

    try {
      con.setAutoCommit(false);

      // 実行SQL設定
      statement = con.prepareStatement(command);

      // パラメータ設定
      for (int i = 0; i < paramData.size(); i++) {
        statement.setString((i + 1), (String) paramData.get(i));
      }
      startTime = System.currentTimeMillis();

      // SQL実行
      if (DefineReport.ID_DEBUG_MODE)
        System.out.println("[sql]" + command + "[prm]" + (paramData == null ? "" : StringUtils.join(paramData.toArray(), ",")));
      // SQL実行
      count = statement.executeUpdate();

      stop = System.currentTimeMillis();
      diff = stop - startTime;
      System.out.println("TIME:" + diff + " ms" + " COUNT:" + count);

      con.commit();

    } catch (SQLException e) {
      count = 0;
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
      count = 0;
      e.printStackTrace();

    } finally {
      close(statement);
      close(con);
    }
    return count;
  }

  /**
   * 更新処理
   * 
   * @param sqlCommands
   * @param paramDatas
   * @param jdbcName
   * @return 実行件数
   * @throws Exception
   */
  public ArrayList<Integer> executeSQLs(ArrayList<String> commands, ArrayList<ArrayList<String>> paramDatas) throws Exception {
    ArrayList<Integer> countList = new ArrayList<Integer>();

    // コネクションの取得
    Connection con = null;
    try {
      con = DBConnection.getConnection(this.JNDIname);
    } catch (Exception e1) {
      e1.printStackTrace();
      return countList;
    }

    PreparedStatement statement = null;
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
          statement.setString((i + 1), (String) paramData.get(i));
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
        System.out.println("TIME:" + diff + " ms" + " COUNT:" + count);
      }

      con.commit();
    } catch (SQLException e) {
      countList = new ArrayList<Integer>();
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
      countList = new ArrayList<Integer>();
      e.printStackTrace();

    } finally {
      close(statement);
      close(con);
    }
    return countList;
  }

  /**
   * コミットします。
   * 
   * @param conn
   */
  protected void commit(Connection conn) {
    if (conn != null) {
      try {
        conn.commit();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * ロールバックします。
   * 
   * @param conn
   */
  protected void rollback(Connection conn) {
    if (conn != null) {
      try {
        conn.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * コネクションをクローズします。
   * 
   * @param conn
   */
  protected void close(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * ステートメントをクローズします。
   * 
   * @param statement
   */
  protected void close(PreparedStatement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 結果セットをクローズします。
   * 
   * @param rs
   */
  protected void close(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public String getSelectCommand() {
    return null;
  }

  /**
   * 検索結果情報から指定列の情報を文字列（カンマ区切り）として取得
   * 
   * @Override
   */
  public String getReader(int columnNumber) {

    StringBuffer sb = new StringBuffer();

    // 検索結果情報
    ArrayList<byte[]> al = this.getTable();

    Iterator<byte[]> itr = al.iterator();
    itr.next();// タイトル部スキップ

    // カラムタイプ取得
    ArrayList<Integer> mt = this.getMeta();
    Iterator<Integer> itrMt = mt.iterator();
    int indexCol = 0;
    Integer typeCol = 0;
    while (itrMt.hasNext()) {
      indexCol++;
      if (indexCol == columnNumber) {
        typeCol = itrMt.next();
        break;
      } else {
        itrMt.next();
      }
    }

    // 検索結果読み取り
    while (itr.hasNext()) {
      if (sb.length() > 0) {
        sb.append(",");
      }

      indexCol = 0;
      // セル（列）情報リスト
      String[] columnsList;
      byte[] bytes = itr.next();
      try {
        columnsList = StringUtils.splitPreserveAllTokens(new String(Snappy.uncompress(bytes, 0, bytes.length), "UTF-8"), "\t");
        for (String col : columnsList) {
          indexCol++;
          // 指定列のみ取得
          if (indexCol == columnNumber) {
            // カラムタイプ判定
            switch (typeCol) {
              case Types.CHAR:
              case Types.VARCHAR:
                sb.append("'" + col + "'");
                break;
              default:
                sb.append(col);
                break;
            }
            break; // while
          } else {
            // カラムのスキップ
            // itrCols.next();
          }
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (CorruptionException e) {
        e.printStackTrace();
      }
    }
    return sb.toString();
  }

  /** ログインユーザー情報 */
  private User userInfo;

  /**
   * ログインユーザー情報を取得します。
   * 
   * @return ログインユーザー情報
   */
  public User getUserInfo() {
    return userInfo;
  }

  /**
   * ログインユーザー情報を設定します。
   * 
   * @param userInfo ログインユーザー情報
   */
  public void setUserInfo(User userInfo) {
    this.userInfo = userInfo;
  }

  /** オプション情報 */
  private JSONObject option;

  /**
   * オプション情報を取得します。
   */
  public JSONObject getOption() {
    return option;
  }

  /**
   * オプション情報を設定します。
   * 
   * @param option
   */
  public void setOption(JSONObject option) {
    this.option = option;
  }

  /**
   * ユーザー情報のログ文を取得
   *
   * @return 検索条件の内容
   */
  protected String getUserInfoLog(User userInfo) {
    return "実行ユーザー情報：" + userInfo.getName();
  }

  /**
   * 検索条件の出力ログ文を取得(map)
   *
   * @return 検索条件の内容
   */
  protected String getConditionLog() {
    Object[] items = null;
    Map<String, String> m = new TreeMap<String, String>(getMap());
    for (String key : m.keySet()) {
      items = ArrayUtils.add(items, key + ":" + m.get(key));
    }
    return StringUtils.join(items, ",");
  }

  /**
   * 画面取得カンマ区切り検索条件をDB検索条件の形式に変換する。
   *
   * @param text 画面取得カンマ区切り検索条件
   * @return 検索条件の内容
   */
  protected static String convCommaString(String text) {
    if (StringUtils.isEmpty(text)) {
      return "";
    }
    JSONArray dataArray = JSONArray.fromObject(StringUtils.split(text, ','));
    String convData = "";
    for (int i = 0; i < dataArray.size(); i++) {
      convData += "'" + dataArray.get(i).toString() + "',";
    }
    return StringUtils.removeEnd(convData, ",");
  }

  /**
   * 数値文字列を引数形式でフォーマットする。
   *
   * @param val 数値文字列
   * @param format フォーマット
   * @return 変換文字列
   */
  protected String convFormat(String val, String format) {
    double dValue = NumberUtils.toDouble(val);
    DecimalFormat dFormat = new DecimalFormat(format);
    return dFormat.format(dValue);
  }

  /**
   * 検索条件表示用情報の共通箇所設定
   *
   * @param jad 検索条件
   */
  protected void createCmnOutput(JsonArrayData jad) {
    // タイトル名称
    List<String> cells = new ArrayList<String>();
    cells.add(jad.getJSONText(DefineReport.ID_HIDDEN_REPORT_NAME));
    getWhere().add(0, cells);

    // 空白行
    cells = new ArrayList<String>();
    cells.add("");
    getWhere().add(1, cells);

    // 空白行
    cells = new ArrayList<String>();
    cells.add("");
    getWhere().add(cells);
  }

  /**
   * 必須分類条件時の条件有効無効判断<br>
   * 集計単位と分類の選択値によって、条件が有効か無効かを返す。<br>
   * 
   * @param type 判断する条件値(=集計単位の各要素の値)
   * @param szSyukei 集計単位選択値
   * @param szBunrui 分類条件選択値
   * @return true:有効/false:無効
   */
  protected boolean isUsefulBunrui(DefineReport.Option type, String szSyukei, String szBunrui) {
    // 選択値が空の場合は条件無効
    if (StringUtils.isEmpty(szBunrui)) {
      return false;
    }
    if (DefineReport.Values.NONE.getVal().equals(szBunrui)) {
      return false;
    }
    return this.isUsefulBunrui(type, szSyukei);
  }

  /**
   * 必須分類条件時の条件有効無効判断<br>
   * 集計単位と分類の選択値によって、条件が有効か無効かを返す。<br>
   * 
   * @param type 判断する条件値(=集計単位の各要素の値)
   * @param szSyukei 集計単位選択値
   * @return true:有効/false:無効
   */
  protected boolean isUsefulBunrui(DefineReport.Option type, String szSyukei) {
    return NumberUtils.toInt(type.getVal()) <= NumberUtils.toInt(szSyukei);
  }

  /**
   * 同一項目の＝条件句を返却する<br>
   * <br>
   * 
   * @param collection 列名リスト
   * @param prefix1 テーブル別名1
   * @param prefix2 テーブル別名2
   * @return =条件句
   */
  protected String convEqualText(Object[] collection, String prefix1, String prefix2) {
    String text = "";
    for (Object val : collection) {
      text += prefix1 + val.toString() + " = " + prefix2 + val.toString() + " and ";
    }
    return StringUtils.removeEnd(text, " and ");
  }

  /**
   * 同一項目の＝条件句を返却する<br>
   * <br>
   * 
   * @param collection 列名リスト
   * @param prefix1 テーブル別名1
   * @param prefix2 テーブル別名2
   * @return =条件句
   */
  protected String convEqualText2(Object[] collection, String prefix1, String prefix2) {
    String text = "";
    for (Object val : collection) {
      text += prefix1 + val.toString() + " = nvl(" + prefix2 + val.toString() + "," + prefix1 + val.toString() + ") and ";
    }
    return StringUtils.removeEnd(text, " and ");
  }

  /**
   * 同一項目のnvl句を返却する<br>
   * <br>
   * 
   * @param collection 列名リスト
   * @param prefix1 テーブル別名1
   * @param prefix2 テーブル別名2
   * @return =条件句
   */
  protected String convNVLText(Object[] collection, String prefix1, String prefix2) {
    String text = "";
    for (Object val : collection) {
      text += "nvl(" + prefix1 + val.toString() + " , " + prefix2 + val.toString() + ") ,";
    }
    return StringUtils.removeEnd(text, " ,");
  }

  /**
   * 同一項目のnvl句を返却する<br>
   * <br>
   * 
   * @param collection 列名リスト
   * @param prefix1 テーブル別名1
   * @param prefix2 テーブル別名2
   * @return =条件句
   */
  protected String convNVLText(Object[] collection, Object[] prefix) {
    String text = "";
    for (Object val : collection) {
      text += "nvl(" + StringUtils.join(prefix, val + ",") + val + ") ,";
    }
    return StringUtils.removeEnd(text, " ,");
  }

  /**
   * feche条件句を返却する<br>
   * <br>
   * 
   * @param rowNum 最大行数
   * @return =条件句
   */
  protected String getFechSql() {
    return getFechSql(DefineReport.MAX_ROWNUM);
  }

  /**
   * feche条件句を返却する<br>
   * <br>
   * 
   * @param rowNum 最大行数
   * @return =条件句
   */
  protected String getFechSql(String rowNum) {
    return " fetch first " + rowNum + " rows only ";
  }

  /** message 情報 */
  private String message = "";

  /**
   * メッセージの取得
   */
  @Override
  public String getMessage() {
    return this.message;
  }

  /**
   * メッセージの設定
   * 
   * @param message メッセージ文字列
   */
  @Override
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * 登録可能店舗判断<br>
   * 選択店舗が自店舗か否かを返す。<br>
   * 
   * @param type 判断する条件値(=集計単位の各要素の値)
   * @param szSyukei 集計単位選択値
   * @param szBunrui 分類条件選択値
   * @return true:有効/false:無効
   */
  protected boolean isUserTenpo(HashMap<String, String> map2, User userInfo2) {
    String szTenpo = map2.get("TENPO"); // 店舗

    // 権限ユーザーの場合は、全店利用可
    if (userInfo2.isAdminUser()) {
      return true;
    }
    return userInfo2.isTenpoUser() && StringUtils.equals(szTenpo, userInfo2.getTenpo());
  }

  /**
   * 予算登録可能期間判断<br>
   * 予算を変更可能な期間か否か判断する<br>
   * 
   * @param map2 検索条件
   * @param userInfo2 ユーザー情報
   * @return true:有効/false:無効
   */
  public boolean canChangeYosanKikan(HashMap<String, String> map2, User userInfo2) {
    String szKikanF = map2.get("KIKAN_F"); // 期間FROM
    String szTenpo = map2.get("TENPO"); // 店舗
    // String szBumon = map2.get("BUMON"); // 部門

    // 権限ユーザーの場合は、全店舗、常に変更可能
    if (userInfo2.isAdminUser()) {
      return true;

      // 店舗ユーザーの場合は、自店舗に限り、権限によって変更可能な期間が異なる
    } else if (userInfo2.isTenpoUser() && StringUtils.equals(szTenpo, userInfo2.getTenpo())) {
      Date inputdt = new Date();
      Date yosandt = CmnDate.getLastDateOfMonth(szKikanF + "01");

      String today = CmnDate.dateFormat(inputdt, DATE_FORMAT.DEFAULT_DATETIME);
      String year = CmnDate.dateFormat(inputdt, DATE_FORMAT.DEFAULT_YEAR);

      // 過去の予算は一律変更不可
      if (yosandt.getTime() < inputdt.getTime()) {
        return false;
      }
      String ableDtF = "";
      String ableDtT = "";
      if (userInfo2.isSpecialTenpoUser()) {
        // 予算の延長入力権限を付与された店舗ユーザーの場合
        // 例：201703の予算は、20170201 00:00 - 201703末日 23:59まで変更可能
        ableDtF = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "010000";
        ableDtT = CmnDate.dateFormat(yosandt) + "2359";
        if (Double.valueOf(today) >= Double.valueOf(ableDtF) && Double.valueOf(today) <= Double.valueOf(ableDtT)) {
          return true;
        }
      } else if (userInfo2.isTentyo()) {
        // 通常例：201703の予算は、20170201 19:00 - 20170215 23:00まで変更可能
        // 特殊例：3月と9月は半期分予算の作成に時間がかかる。このため、たとえば3月に入力可能な4月予算は20170301 19:00 - 20170325 23:00まで変更可能
        // 2022/09/08 依頼 20220922に変更

        if (StringUtils.startsWithAny(today, new String[] {year + "03", year + "09"})) {
          ableDtF = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "011900";
          ableDtT = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + getSimeDay() + "2300";
        } else {
          ableDtF = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "011900";
          ableDtT = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "152300";
        }
        if (Double.valueOf(today) >= Double.valueOf(ableDtF) && Double.valueOf(today) <= Double.valueOf(ableDtT)) {
          return true;
        }
      } else if (userInfo2.isBumonTanto()) {
        // 通常例：201703の予算は、20170201 19:00 - 20170215 23:00まで変更可能
        // 特殊例：3月と9月は半期分予算の作成に時間がかかる。このため、たとえば3月に入力可能な4月予算は20170301 19:00 - 20170325 23:00まで変更可能
        // 2022/09/08 依頼 20220922に変更
        if (StringUtils.startsWithAny(today, new String[] {year + "03", year + "09"})) {
          ableDtF = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "011900";
          ableDtT = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + getSimeDay() + "2300";
        } else {
          ableDtF = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "011900";
          ableDtT = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "152300";
        }
        if (Double.valueOf(today) >= Double.valueOf(ableDtF) && Double.valueOf(today) <= Double.valueOf(ableDtT)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 締め日を取得<br>
   * SATMS.PIMSKB IKBGPID IKBUQID IBKUQVL IBKVSEQ IBKRMRK<br>
   * 9 0 22 1 予算入力最終日（３月、９月）
   * 
   * @return
   */
  private String getSimeDay() {
    ItemList iL = new ItemList();
    String simeDay = "23";

    ArrayList<List<String>> results = iL.selectArray("SELECT IBKUQVL FROM SATMS.PIMSKB WHERE IKBGPID=9 AND IKBUQID=0", new ArrayList<String>(), Defines.STR_JNDI_DS);
    if (results.size() > 1) {
      simeDay = results.get(1).get(0);// 締め日
    }
    System.out.println("入力可能最終日：" + simeDay);
    return simeDay;
  }

  /**
   * 店長予算案登録可能期間判断<br>
   * 店長予算案を変更可能な期間か否か判断する<br>
   * 
   * @param map2 検索条件
   * @param userInfo2 ユーザー情報
   * @return true:有効/false:無効
   */
  public boolean canChangeTYosanKikan(HashMap<String, String> map2, User userInfo2) {
    String szKikanF = map2.get("KIKAN_F"); // 期間FROM
    String szTenpo = map2.get("TENPO"); // 店舗
    // String szBumon = map2.get("BUMON"); // 部門

    // 権限ユーザーの場合は、全店舗、常に変更可能
    if (userInfo2.isAdminUser()) {
      return true;

      // 店舗ユーザーの場合は、自店舗に限り、権限によって変更可能な期間が異なる
    } else if (userInfo2.isTenpoUser() && StringUtils.equals(szTenpo, userInfo2.getTenpo())) {
      Date inputdt = new Date();
      Date yosandt = CmnDate.getLastDateOfMonth(szKikanF + "01");

      String today = CmnDate.dateFormat(inputdt, DATE_FORMAT.DEFAULT_DATETIME);
      String year = CmnDate.dateFormat(inputdt, DATE_FORMAT.DEFAULT_YEAR);

      // 過去の店長予算案は一律変更不可
      if (yosandt.getTime() < inputdt.getTime()) {
        return false;
      }
      String ableDtT = "";
      // 未来の店長予算案は一律変更可のため、指定月の予算案はいつまで入力可能かのチェックを下記で行う
      if (userInfo2.isSpecialTenpoUser()) {
        // 例：201703の予算は、201703末日 23:59まで変更可能
        ableDtT = CmnDate.dateFormat(yosandt) + "2359";
        if (Double.valueOf(today) <= Double.valueOf(ableDtT)) {
          return true;
        }
      } else if (userInfo2.isTentyo()) {
        // 通常例：201703の予算は、20170215 23:00まで変更可能
        // 特殊例：3月と9月は半期分予算の作成に時間がかかる。このため、たとえば3月に入力可能な4月予算は20170325 23:00まで変更可能
        if (StringUtils.startsWithAny(today, new String[] {year + "03", year + "09"})) {
          ableDtT = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "272300";
        } else {
          ableDtT = CmnDate.dateFormatYM(CmnDate.getMonthAddedDate(yosandt, -1)) + "152300";
        }
        if (Double.valueOf(today) <= Double.valueOf(ableDtT)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 客数登録可能期間判断<br>
   * 客数を変更可能な期間か否か判断する<br>
   * 
   * @param map2 検索条件
   * @param userInfo2 ユーザー情報
   * @return true:有効/false:無効
   */
  public boolean canChangeKyakuKikan(HashMap<String, String> map2, User userInfo2) {
    String szKikanF = map2.get("KIKAN_F"); // 期間FROM
    String szTenpo = map2.get("TENPO"); // 店舗
    // String szBumon = map2.get("BUMON"); // 部門

    // 権限ユーザーの場合は、全店舗、常に変更可能
    if (userInfo2.isAdminUser()) {
      return true;

      // 店舗ユーザーの場合は、自店舗に限り、権限によって変更可能な期間が異なる
    } else if (userInfo2.isTenpoUser() && StringUtils.equals(szTenpo, userInfo2.getTenpo())) {
      Date inputdt = new Date();
      Date yosandt = CmnDate.getLastDateOfMonth(szKikanF + "01");

      String today = CmnDate.dateFormat(inputdt, DATE_FORMAT.DEFAULT_DATETIME);
      // String year = CmnDate.dateFormat(inputdt, DATE_FORMAT.DEFAULT_YEAR);

      // 過去の客数は一律変更不可
      if (yosandt.getTime() < inputdt.getTime()) {
        return false;
      }

      // 未来の客数は一律変更可、指定月の客数はいつまで入力可能かのチェックを下記で行う
      String ableDtT = "";
      if (userInfo2.isTentyo()) {
        // 例：201703の客数は、201703末日 23:59まで変更可能
        ableDtT = CmnDate.dateFormat(yosandt) + "2359";
        if (Double.valueOf(today) <= Double.valueOf(ableDtT)) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * イベント情報登録可能期間判断<br>
   * イベント情報を変更可能な期間か否か判断する<br>
   * 
   * @param map2 検索条件
   * @param userInfo2 ユーザー情報
   * @return true:有効/false:無効
   */
  public boolean canChangeEventKikan(HashMap<String, String> map2, User userInfo2) {
    String szTenpo = map2.get("TENPO"); // 店舗

    // 権限ユーザーの場合は、全店舗、常に変更可能
    if (userInfo2.isAdminUser()) {
      return true;

      // 店舗ユーザーの場合は、自店舗に限り、権限によって変更可能
    } else if (userInfo2.isTenpoUser() && StringUtils.equals(szTenpo, userInfo2.getTenpo())) {
      // 店長・主任権限の場合、常に変更可能
      if (userInfo2.isTentyo()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 天気情報のWHEN文を作成<br>
   * 
   * @return WHEN文
   */
  public String getWeathernewsSql() {
    return " when '100' then '晴'" + " when '101' then '晴時々曇'" + " when '102' then '晴一時雨'" + " when '103' then '晴時々雨'" + " when '104' then '晴一時雪'" + " when '105' then '晴時々雪'" + " when '106' then '晴一時雨か雪'" + " when '107' then '晴時々雨か雪'" + " when '108' then '晴一時雨か雷雨'" + " when '110' then '晴後時々曇'" + " when '111' then '晴後曇'" + " when '112' then '晴後一時雨'" + " when '113' then '晴後時々雨'" + " when '114' then '晴後雨'" + " when '115' then '晴後一時雪'" + " when '116' then '晴後時々雪'" + " when '117' then '晴後雪'" + " when '118' then '晴後雨か雪'" + " when '119' then '晴後雨か雷雨'"
        + " when '120' then '晴朝夕一時雨'" + " when '121' then '晴朝のうち一時雨'" + " when '122' then '晴夕方一時雨'" + " when '123' then '晴山沿い雷雨'" + " when '124' then '晴山沿い雪'" + " when '125' then '晴午後は雷雨'" + " when '126' then '晴昼頃から雨'" + " when '127' then '晴夕方から雨'" + " when '128' then '晴夜は雨'" + " when '129' then '晴夜半から雨'" + " when '130' then '朝のうち霧後晴'" + " when '131' then '晴明け方霧'" + " when '132' then '晴朝夕曇'" + " when '140' then '晴時々雨で雷を伴う'" + " when '160' then '晴一時雪か雨'" + " when '170' then '晴時々雪か雨'" + " when '181' then '晴後雪か雨'" + " when '200' then '曇'"
        + " when '201' then '曇時々晴'" + " when '202' then '曇一時雨'" + " when '203' then '曇時々雨'" + " when '204' then '曇一時雪'" + " when '205' then '曇時々雪'" + " when '206' then '曇一時雨か雪'" + " when '207' then '曇時々雨か雪'" + " when '208' then '曇一時雨か雷雨'" + " when '209' then '霧'" + " when '210' then '曇後時々晴'" + " when '211' then '曇後晴'" + " when '212' then '曇後一時雨'" + " when '213' then '曇後時々雨'" + " when '214' then '曇後雨'" + " when '215' then '曇後一時雪'" + " when '216' then '曇後時々雪'" + " when '217' then '曇後雪'" + " when '218' then '曇後雨か雪'" + " when '219' then '曇後雨か雷雨'"
        + " when '220' then '曇朝夕一時雨'" + " when '221' then '曇朝のうち一時雨'" + " when '222' then '曇夕方一時雨'" + " when '223' then '曇日中時々晴'" + " when '224' then '曇昼頃から雨'" + " when '225' then '曇夕方から雨'" + " when '226' then '曇夜は雨'" + " when '227' then '曇夜半から雨'" + " when '228' then '曇昼頃から雪'" + " when '229' then '曇夕方から雪'" + " when '230' then '曇夜は雪'" + " when '231' then '曇海上海岸は霧か霧雨'" + " when '240' then '曇時々雨で雷を伴う'" + " when '250' then '曇時々雪で雷を伴う'" + " when '260' then '曇一時雪か雨'" + " when '270' then '曇時々雪か雨'" + " when '281' then '曇後雪か雨'" + " when '300' then '雨'"
        + " when '301' then '雨時々晴'" + " when '302' then '雨時々止む'" + " when '303' then '雨時々雪'" + " when '304' then '雨か雪'" + " when '306' then '大雨'" + " when '308' then '雨で暴風を伴う'" + " when '309' then '雨一時雪'" + " when '311' then '雨後晴'" + " when '313' then '雨後曇'" + " when '314' then '雨後時々雪'" + " when '315' then '雨後雪'" + " when '316' then '雨か雪後晴'" + " when '317' then '雨か雪後曇'" + " when '320' then '朝のうち雨後晴'" + " when '321' then '朝のうち雨後曇'" + " when '322' then '雨朝晩一時雪'" + " when '323' then '雨昼頃から晴'" + " when '324' then '雨夕方から晴'"
        + " when '325' then '雨夜は晴'" + " when '326' then '雨夕方から雪'" + " when '327' then '雨夜は雪'" + " when '328' then '雨一時強く降る'" + " when '329' then '雨一時みぞれ'" + " when '340' then '雪か雨'" + " when '350' then '雨で雷を伴う'" + " when '361' then '雪か雨後晴'" + " when '371' then '雪か雨後曇'" + " when '391' then '391'" + " when '392' then '392'" + " when '393' then '393'" + " when '394' then '394'" + " when '395' then '395'" + " when '396' then '396'" + " when '400' then '雪'" + " when '401' then '雪時々晴'" + " when '402' then '雪時々止む'" + " when '403' then '雪時々雨'"
        + " when '405' then '大雪'" + " when '406' then '風雪強い'" + " when '407' then '暴風雪'" + " when '409' then '雪一時雨'" + " when '411' then '雪後晴'" + " when '413' then '雪後曇'" + " when '414' then '雪後雨'" + " when '420' then '朝のうち雪後晴'" + " when '421' then '朝のうち雪後曇'" + " when '422' then '雪昼頃から雨'" + " when '423' then '雪夕方から雨'" + " when '424' then '雪夜半から雨'" + " when '425' then '雪一時強く降る'" + " when '426' then '雪後みぞれ'" + " when '427' then '雪一時みぞれ'" + " when '450' then '雪で雷を伴う'" + " when '999' then '欠測'";
  }
}
