package dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

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
public class Report002Dao extends ItemDao {

	private static String DECI_DIGITS = ",20,10";

	/**
	 * インスタンスを生成します。
	 * @param source
	 */
	public Report002Dao(String JNDIname) {
		super(JNDIname);
	}

	/**
	 * 検索実行
	 *
	 * @return
	 */
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
	 * @param userInfo
	 *
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public JSONObject update(HttpServletRequest request,HttpSession session,  HashMap<String, String> map, User userInfo) {

		// 更新情報チェック(基本JS側で制御)
		JSONObject msgObj = new JSONObject();
		JSONArray msg = this.check(map, userInfo);

		if(msg.size() > 0){
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
		if(userInfo==null){
			return "";
		}

		String szKikanF			= getMap().get("KIKAN_F");		// 期間FROM
		String szTENPO			= getMap().get("TENPO");		// 店舗
		String szBUMON			= getMap().get("BUMON");		// 部門

		// パラメータ確認
		// 必須チェック
		if ((szKikanF == null) || (szTENPO == null) ) {
			System.out.println(super.getConditionLog());
			return "";
		}

		// タイトル情報(任意)設定
		List<String> titleList = new ArrayList<String>();
		titleList.addAll(Arrays.asList("日付","天気","最高気温","最低気温","日別予算","予算累計"
				,"売上","売上累計","売上点数","客数","予算差異","予算比","累計予算比","進捗率"
				,"前年日付","前年売上","前年売上累計","前年比","累計前年比","売上推移率","前年客数","前年売上点数"
				,"今年の要因","仕入原価","荒利高","荒利率"));


		// 帳票単位
		boolean isBumon = StringUtils.isNotEmpty(szBUMON) && !DefineReport.Values.NONE.getVal().equals(szBUMON);

		String szDtF = szKikanF+"01";
		String szDtT = CmnDate.dateFormat(CmnDate.getLastDateOfMonth(szKikanF+"01"));
		String szWhereK  = " between '"+szDtF+"' and '"+szDtT+"'";	// 期間条件
		String sbWhereT = " and T1.MISECD = '" + szTENPO + "'";		// 店舗条件
		String sbWhereB = "";										// 部門条件
		String sbWhereTB= "";										// 部門条件
		if(isBumon){
			sbWhereB = " and T1.BUNBMC = '" + szBUMON + "'";
			sbWhereTB = " and TOUKATU_CD_S = '" + szBUMON + "'";
		}

		// 一覧表情報
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("with MCAL as ( ");
		sbSQL.append("  select T1.COMTOB as DT");
		sbSQL.append("  ,max(CHAR(TO_CHAR(TO_DATE(T1.COMTOB, 'yyyymmdd'), 'mm/dd'), 5)||CASE DAYOFWEEK(TO_DATE(T1.COMTOB, 'yyyymmdd')) WHEN 1 THEN '(日)' WHEN 2 THEN '(月)' WHEN 3 THEN '(火)' WHEN 4 THEN '(水)' WHEN 5 THEN '(木)' WHEN 6 THEN '(金)' WHEN 7 THEN '(土)' END) as TXT");
		sbSQL.append("  ,max(WEEK_ISO(TO_DATE(T1.COMTOB, 'YYYYMMDD'))) as WEEK");
		sbSQL.append("  ,max(T2.EVENT) as EVENT,max(T2.TYOSAN) as TYOSAN");
		sbSQL.append("  ,max(T3.TENKIKBN) as TENKIKBN,max(T3.MAXKION) as MAXKION,max(T3.MINKION) as MINKION ");
		sbSQL.append("  ,max(CASE T3.TENKIKBN "+super.getWeathernewsSql()+" ELSE T3.TENKIKBN END) AS TENKI");
		sbSQL.append("  from ");
		sbSQL.append("  (select COMTOB from SATYS.MCALTT where COMTOB"+szWhereK+") T1 ");
		sbSQL.append("  left outer join (select DT,EVENT,TYOSAN from SATYS.TTDEVT T1 where DT"+szWhereK+sbWhereT+") T2 on T1.COMTOB = T2.DT ");
		sbSQL.append("  left outer join (select * from SATMS.CAUSAL_INFO T1 where COMTOB"+szWhereK+sbWhereT+") T3 on T1.COMTOB = T3.COMTOB ");
		sbSQL.append("  group by T1.COMTOB");
		sbSQL.append(") ");
		sbSQL.append(",MCALZ as ( ");
		sbSQL.append("  select T1.DT, T1.DT_KIJUN");
		sbSQL.append("  ,max(char(left(T1.DT_KIJUN,4)||'/'||substr(T1.DT_KIJUN,5,2)||'/', 8)||case  when SUBSTR(T1.DT_KIJUN,7,2) = '99' then '1,2' else substr(T1.DT_KIJUN,7,2)||");
		sbSQL.append("   CASE DAYOFWEEK(TO_DATE(T1.DT_KIJUN, 'yyyymmdd')) WHEN 1 THEN '(日)' WHEN 2 THEN '(月)' WHEN 3 THEN '(火)' WHEN 4 THEN '(水)' WHEN 5 THEN '(木)' WHEN 6 THEN '(金)' WHEN 7 THEN '(土)' END");
		sbSQL.append("   end) as TXT");
		sbSQL.append("  ,max(T2.EVENT) as EVENT,max(T2.TYOSAN) as TYOSAN");
		sbSQL.append("  ,'"+szTENPO+"' as MISECD,max(T4.MISECD_HT) as MISECD_HT ");
		sbSQL.append("  from ");
		sbSQL.append("  (select DT, DT_KIJUN from SATYS.TABKNK where DT"+szWhereK+") T1 ");
		sbSQL.append("  left outer join SATYS.TTDEVT T2 on T1.DT_KIJUN = T2.DT" + sbWhereT.replace("T1", "T2"));
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
		sbSQL.append("  ,sum(sum(T1.UYOSAN)) over(order by T1.DT) as UYOSANR");
		sbSQL.append("  ,sum(sum(T1.AYOSAN)) over(order by T1.DT) as AYOSANR");
		sbSQL.append("  ,sum(sum(T1.TYOSAN)) over(order by T1.DT) as TYOSANR");
		sbSQL.append("  ,sum(sum(T1.UYOSAN)) over() as UYOSANG ");
		sbSQL.append("  ,sum(sum(T1.AYOSAN)) over() as AYOSANG ");
		sbSQL.append("  ,sum(sum(T1.TYOSAN)) over() as TYOSANG ");
		sbSQL.append("  ,MAX(T4.GENKAKEI) AS GENKA ");	// 原価
		sbSQL.append("  from SATYS.TTBDYS T1 ");

		sbSQL.append("  LEFT OUTER JOIN ( ");
		sbSQL.append("  select T1.COMTOB as DT");
		sbSQL.append("  ,sum(T1.GENKAKEI) as GENKAKEI ");
		sbSQL.append("  from SATTR.SIBMDD T1 ");
		sbSQL.append("  where T1.COMTOB"+szWhereK+sbWhereT+sbWhereB);
		sbSQL.append("  group by T1.COMTOB ");
		sbSQL.append("  ) T4 ON T1.DT = T4.DT ");

		sbSQL.append("  where T1.DT"+szWhereK+sbWhereT+sbWhereB);
		sbSQL.append("  group by T1.DT ");
		sbSQL.append(") ");
		sbSQL.append(",HBZ1 as ( ");
		sbSQL.append("  select  ");
		sbSQL.append("   T2.DT, T2.DT_KIJUN, T3.BUNBMC ");
		sbSQL.append("  ,sum(T1.UYOSAN) as UYOSAN ");
		sbSQL.append("  from SATTR.HIBYDD T1 ");
		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD");
		sbSQL.append("  inner join SATTR.MCLSHB T3 on T1.CLSSGK = T3.CLSSGK"+sbWhereB.replace("T1", "T3"));
		sbSQL.append("  group by T2.DT, T2.DT_KIJUN, T3.BUNBMC ");
		sbSQL.append(") ");
		sbSQL.append(",HBZ2 as ( ");
		sbSQL.append("  select  ");
		sbSQL.append("   T2.DT, T2.DT_KIJUN, T3.BUNBMC ");
		sbSQL.append("  ,sum(T1.UYOSAN) as UYOSAN ");
		sbSQL.append("  from SATTR.HIBYDD T1 ");
		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD_HT");
		sbSQL.append("  inner join SATTR.MCLSHB T3 on T1.CLSSGK = T3.CLSSGK"+sbWhereB.replace("T1", "T3"));
		sbSQL.append("  group by T2.DT, T2.DT_KIJUN, T3.BUNBMC ");
		sbSQL.append(") ");
		sbSQL.append(",HBZ as ( ");
		sbSQL.append("  select M1.DT, M1.DT_KIJUN");
		sbSQL.append("  ,T1.UYOSAN");
		sbSQL.append("  ,sum(T1.UYOSAN) over(order by M1.DT) as UYOSANR");
		sbSQL.append("  ,sum(T1.UYOSAN) over() as UYOSANG ");
		sbSQL.append("  from MCALZ M1 ");
		sbSQL.append("  left join (");
		sbSQL.append("  select nvl(T1.DT,T2.DT) as DT");
		sbSQL.append("  ,sum(nvl(T1.UYOSAN, T2.UYOSAN)) as UYOSAN");
		sbSQL.append("  from HBZ1 T1 full outer join HBZ2 T2 on T1.DT = T2.DT and T1.BUNBMC = T2.BUNBMC");
		sbSQL.append("  group by nvl(T1.DT,T2.DT)");
		sbSQL.append("  ) T1 on M1.DT = T1.DT");
		sbSQL.append(") ");
		sbSQL.append(",MCLSHA AS (");
		sbSQL.append("  select distinct BUNBMC, TOUKATU_CD_S from SATTR.MCLSHA where TOUKATU_CD_S in (select distinct TOUKATU_CD_S from SATYS.MCLSTT)" + sbWhereTB);

		if (sbWhereTB.length()==0){
			// 部門「すべて」の場合、統括部門'28'を集計に含める
			sbSQL.append(" UNION ALL SELECT BUNBMC,TOUKATU_CD_S FROM SATMS.BUMON_KANRI_MST WHERE TOUKATU_CD_S='28' ");
		}

		sbSQL.append(") ");
		sbSQL.append(",HA as ( ");
		sbSQL.append("  select M1.DT as DT");
		sbSQL.append("  ,sum(T1.URIKINGAKU) as URIKINGAKU ");
		sbSQL.append("  , sum(T1.URISURYO) AS URISURYO");
		sbSQL.append("  ,sum(T1.ARKINGAKU ) as ARKINGAKU ");
		sbSQL.append("  ,sum(sum(nvl(T1.URIKINGAKU,0))) over(order by M1.DT) as URIKINGAKUR");
		sbSQL.append("  ,sum(sum(nvl(T1.ARKINGAKU ,0))) over(order by M1.DT) as ARKINGAKUR");
		sbSQL.append("  from MCAL M1 ");
		sbSQL.append("  left outer join (");
		sbSQL.append("    select T1.COMTOB,T1.URIKINGAKU,T1.URISURYO,T1.ARKINGAKU");
		sbSQL.append("    from SATTR.HABMDD T1");
		sbSQL.append("    inner join MCLSHA T3 on T1.BUNBMC = T3.BUNBMC "+sbWhereT);
		sbSQL.append("  ) T1 on M1.DT = T1.COMTOB");
		sbSQL.append("  group by M1.DT ");
		sbSQL.append(") ");
		sbSQL.append(",HAZ1 as ( ");
		sbSQL.append("  select T2.DT, T2.DT_KIJUN, T3.TOUKATU_CD_S");
		sbSQL.append("  ,sum(T1.URIKINGAKU) as URIKINGAKU ");
		sbSQL.append("  ,sum(T1.URISURYO) AS URISURYO ");
		sbSQL.append("  ,sum(T1.ARKINGAKU ) as ARKINGAKU ");
		sbSQL.append("  from SATTR.HABMDD T1 ");
		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD");
		sbSQL.append("  inner join MCLSHA T3 on T1.BUNBMC = T3.BUNBMC");
		sbSQL.append("  where T1.URIKINGAKU <> 0 ");
		sbSQL.append("  group by T2.DT, T2.DT_KIJUN, T3.TOUKATU_CD_S");
		sbSQL.append(") ");
		sbSQL.append(",HAZ2 as ( ");
		sbSQL.append("  select T2.DT, T2.DT_KIJUN, T3.TOUKATU_CD_S");
		sbSQL.append("  ,sum(T1.URIKINGAKU) as URIKINGAKU ");
		sbSQL.append("  ,sum(T1.URISURYO) AS URISURYO ");
		sbSQL.append("  ,sum(T1.ARKINGAKU ) as ARKINGAKU ");
		sbSQL.append("  from SATTR.HABMDD T1 ");
		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD_HT");
		sbSQL.append("  inner join MCLSHA T3 on T1.BUNBMC = T3.BUNBMC");
		sbSQL.append("  where T1.URIKINGAKU <> 0 ");
		sbSQL.append("  group by T2.DT, T2.DT_KIJUN, T3.TOUKATU_CD_S");
		sbSQL.append(") ");
		sbSQL.append(",HAZ as ( ");
		sbSQL.append("  select M1.DT, M1.DT_KIJUN ");
		sbSQL.append("  ,T1.URIKINGAKU");
		sbSQL.append("  ,T1.URISURYO ");
		sbSQL.append("  ,T1.ARKINGAKU");
		sbSQL.append("  ,sum(nvl(T1.URIKINGAKU,0)) over(order by M1.DT) as URIKINGAKUR");
		sbSQL.append("  ,sum(nvl(T1.ARKINGAKU ,0)) over(order by M1.DT) as ARKINGAKUR");
		sbSQL.append(", SUM(NVL(T1.URIKINGAKU, 0)) OVER () AS URIKINGAKUT");	// 売上金額合計
		sbSQL.append("  from MCALZ M1 ");
		sbSQL.append("  left join (");
		sbSQL.append("  select nvl(T1.DT,T2.DT) as DT");
		sbSQL.append("  ,sum(nvl(T1.URIKINGAKU, T2.URIKINGAKU)) as URIKINGAKU ");
		sbSQL.append("  ,sum(nvl(T1.URISURYO, T2.URISURYO)) AS URISURYO ");
		sbSQL.append("  ,sum(nvl(T1.ARKINGAKU, T2.ARKINGAKU)) as ARKINGAKU ");
		sbSQL.append("  from HAZ1 T1 full outer join HAZ2 T2  on T1.DT = T2.DT and T1.TOUKATU_CD_S = T2.TOUKATU_CD_S ");
		sbSQL.append("  group by nvl(T1.DT,T2.DT)");
		sbSQL.append("  ) T1 on M1.DT = T1.DT");
		sbSQL.append(") ");
//		sbSQL.append(",SI as ( ");
//		sbSQL.append("  select T1.COMTOB as DT");
//		sbSQL.append("  ,sum(T1.GENKAKEI) as GENKAKEI ");
//		sbSQL.append("  from SATTR.SIBMDD T1 ");
//		sbSQL.append("  where T1.COMTOB"+szWhereK+sbWhereT+sbWhereB);
//		sbSQL.append("  group by T1.COMTOB ");
//		sbSQL.append(") ");
		sbSQL.append(",KYK as ( ");
		sbSQL.append("  select T1.COMTOB as DT");
		sbSQL.append("  ,sum(T1.KYAKUSU_1F) as KYAKUSU ");
		sbSQL.append("  from SATTR.TNKYDD T1 ");
		sbSQL.append("  inner join MCAL T2 on T1.COMTOB = T2.DT "+sbWhereT);
		sbSQL.append("  group by T1.COMTOB ");
		sbSQL.append(") ");

		// 前年客数
		sbSQL.append(", KYZ1 AS ( ");
		sbSQL.append("SELECT ");
		sbSQL.append("  T2.DT AS DT ");
		sbSQL.append("  , SUM(T1.KYAKUSU_1F) AS KYAKUSU ");
		sbSQL.append("FROM ");
		sbSQL.append("  SATTR.TNKYDD T1 ");
		sbSQL.append("  INNER JOIN MCALZ T2 ");
		sbSQL.append("    ON T1.COMTOB = T2.DT_KIJUN ");
		sbSQL.append("    AND T1.MISECD = T2.MISECD ");
		sbSQL.append("GROUP BY ");
		sbSQL.append("  T2.DT ");
		sbSQL.append("  , T2.DT_KIJUN ");
		sbSQL.append(") ");
		sbSQL.append(", KYZ2 AS ( ");
		sbSQL.append("SELECT ");
		sbSQL.append("  T2.DT AS DT ");
		sbSQL.append("  , SUM(T1.KYAKUSU_1F) AS KYAKUSU ");
		sbSQL.append("FROM ");
		sbSQL.append("  SATTR.TNKYDD T1 ");
		sbSQL.append("  INNER JOIN MCALZ T2 ");
		sbSQL.append("    ON T1.COMTOB = T2.DT_KIJUN ");
		sbSQL.append("    AND T1.MISECD = T2.MISECD_HT ");
		sbSQL.append("GROUP BY ");
		sbSQL.append("  T2.DT ");
		sbSQL.append("  , T2.DT_KIJUN ");
		sbSQL.append(") ");
		sbSQL.append(", KYZ AS ( ");
		sbSQL.append("SELECT ");
		sbSQL.append("  NVL(T1.DT, T2.DT) AS DT ");
		sbSQL.append("  , SUM(NVL(T1.KYAKUSU, T2.KYAKUSU)) AS KYAKUSU ");
		sbSQL.append("FROM ");
		sbSQL.append("  KYZ1 T1 ");
		sbSQL.append("  FULL OUTER JOIN KYZ2 T2 ");
		sbSQL.append("    ON T1.DT = T2.DT ");
		sbSQL.append("GROUP BY ");
		sbSQL.append("  NVL(T1.DT, T2.DT) ");
		sbSQL.append(") ");

		sbSQL.append(" select ");
		sbSQL.append("  M1.TXT ");																// F1
		sbSQL.append(" ,M1.TENKI ");	// 天気
		sbSQL.append(" ,M1.MAXKION ");	// 最高気温
		sbSQL.append(" ,M1.MINKION ");	// 最低気温
		sbSQL.append(" ,truncate(T1.AYOSAN,0) ");
		sbSQL.append(" ,truncate(nvl(T1.AYOSANR,0),0) ");
		sbSQL.append(" ,truncate(decimal(T5.URIKINGAKU)/1000,0) ");
		sbSQL.append(" ,truncate(decimal(nvl(T5.URIKINGAKUR,0))/1000,0) ");						// F5
		sbSQL.append(" ,T5.URISURYO AS URISURYO ");	// 売上点数
		sbSQL.append(" ,T4.KYAKUSU ");				// 客数
		sbSQL.append(" ,truncate(decimal(nvl(T5.URIKINGAKU ,0))/1000,0) - truncate(nvl(T1.AYOSAN,0),0) ");
		sbSQL.append(" ,case when nvl(T1.AYOSAN *1000,0)= 0 or nvl(T5.URIKINGAKU ,0)= 0 then 0 else truncate(decimal(T5.URIKINGAKU )/(T1.AYOSAN *1000)* 100, 1) end ");
		sbSQL.append(" ,case when nvl(T1.AYOSANR*1000,0)= 0 or nvl(T5.URIKINGAKUR,0)= 0 then 0 else truncate(decimal(T5.URIKINGAKUR)/(T1.AYOSANR*1000)* 100, 1) end ");
		sbSQL.append(" ,case when nvl(T1.AYOSANG*1000,0)= 0 or nvl(T5.URIKINGAKUR,0)= 0 then 0 else truncate(decimal(T5.URIKINGAKUR)/(T1.AYOSANG*1000)* 100, 1) end ");
		sbSQL.append(" ,M2.TXT ");																// F10
		sbSQL.append(" ,truncate(decimal(T3.URIKINGAKU)/1000,0) ");
		sbSQL.append(" ,truncate(decimal(nvl(T3.URIKINGAKUR,0))/1000,0) ");
		sbSQL.append(" ,case when nvl(T3.URIKINGAKU ,0)= 0 or nvl(T5.URIKINGAKU ,0)= 0 then 0 else truncate(decimal(T5.URIKINGAKU )/T3.URIKINGAKU * 100, 1) end ");
		sbSQL.append(" ,case when nvl(T3.URIKINGAKUR,0)= 0 or nvl(T5.URIKINGAKUR,0)= 0 then 0 else truncate(decimal(T5.URIKINGAKUR)/T3.URIKINGAKUR* 100, 1) end ");
		sbSQL.append(" ,case when nvl(T2.UYOSANG    ,0)= 0 or nvl(T3.URIKINGAKUR,0)= 0 then 0 else truncate(decimal(T3.URIKINGAKUR)/T3.URIKINGAKUT* 100, 1) end ");		// 売上進捗率
		sbSQL.append(" ,T7.KYAKUSU AS KYAKUSUZ ");		// 前年客数
		sbSQL.append(" ,T3.URISURYO AS URISURYOZ ");	// 前年売上点数
		sbSQL.append(" ,M1.EVENT ");
		sbSQL.append(" ,truncate(decimal(T1.GENKA )/1000,0) ");	// 原価
		sbSQL.append(" ,truncate(decimal(T5.ARKINGAKU)/1000,0) ");
		sbSQL.append(" ,case when nvl(T5.URIKINGAKU,0) = 0 or nvl(T5.ARKINGAKU, 0) = 0 then 0 else truncate(decimal(T5.ARKINGAKU)/T5.URIKINGAKU* 100, 1) end ");
		sbSQL.append(" ,M1.DT ");																// 隠し
		sbSQL.append(" from ");
		sbSQL.append("  MCAL M1  ");
		sbSQL.append("  left outer join MCALZ M2 on M1.DT = M2.DT ");
		sbSQL.append("  left outer join BDYS  T1 on M1.DT = T1.DT ");
		sbSQL.append("  left outer join HBZ   T2 on M1.DT = T2.DT ");
		sbSQL.append("  left outer join HA    T5 on M1.DT = T5.DT ");
		sbSQL.append("  left outer join HAZ   T3 on M1.DT = T3.DT ");
//		sbSQL.append("  left outer join SI    T6 on M1.DT = T6.DT ");
		sbSQL.append("  left outer join KYK   T4 on M1.DT = T4.DT ");
		sbSQL.append("  LEFT OUTER JOIN KYZ   T7 ON M1.DT = T7.DT ");	// 前年客数
		sbSQL.append(" order by ");
		sbSQL.append("  M1.DT ");


		// その他表示情報
		ItemList iL = new ItemList();
		StringBuffer sbSQL2 = new StringBuffer();
		// 日別予算最終更新者情報
		String updUser = "";
		sbSQL2.append(" select T1.CD_UPDATE as ID");
//		sbSQL2.append("  ,nvl(T2.NM_FAMILY||T2.NM_NAME, case when T1.CD_UPDATE = 0 then '自動按分' else T1.CD_UPDATE||'' end) as NAME");
		sbSQL2.append("  ,nvl(T2.NM_FAMILY||'　'||T2.NM_NAME, case when T1.CD_UPDATE = 0 then '自動按分' else T1.CD_UPDATE||'' end) || '　更新日時：' || TO_CHAR(T1.DT_UPDATE,'YYYY/MM/DD HH24:MI:SS') as NAME");
		sbSQL2.append(" from (");
		if(isBumon){
			sbSQL2.append("   select * from (select CD_UPDATE, DT_UPDATE from SATYS.TTBDYS T1 where T1.DT"+szWhereK+sbWhereT+sbWhereB+" order by DT_UPDATE desc fetch first 1 rows only) T");
		}else {
			sbSQL2.append("   select * from (select CD_UPDATE, DT_UPDATE from SATYS.TTDEVT T1 where T1.DT"+szWhereK+sbWhereT+" order by DT_UPDATE desc fetch first 1 rows only) T");
			sbSQL2.append("   union");
			sbSQL2.append("   select * from (select CD_UPDATE, DT_UPDATE from SATYS.TTDKYK T1 where T1.DT"+szWhereK+sbWhereT+" order by DT_UPDATE desc fetch first 1 rows only) T");
			sbSQL2.append("   union");
			sbSQL2.append("   select * from (select CD_UPDATE, DT_UPDATE from SATYS.TTMEVT T1 where T1.NENTUKI = '"+szKikanF+"'"+sbWhereT+" order by DT_UPDATE desc fetch first 1 rows only) T");
		}
		sbSQL2.append(" ) T1");
		sbSQL2.append(" left outer join KEYSYS.SYS_USERS T2 on T1.CD_UPDATE = T2.CD_USER");
		sbSQL2.append(" order by T1.DT_UPDATE desc fetch first 1 rows only");
		@SuppressWarnings("static-access")
		JSONArray array1 = iL.selectJSONArray(sbSQL2.toString(), null, Defines.STR_JNDI_DS);
		if(array1.size() > 0){
			updUser = ObjectUtils.toString(array1.getJSONObject(0).get("NAME"));
		}
		// 週計/月計情報取得
		String sqlCommandKei = this.createCommandKei(szWhereK,sbWhereT,sbWhereB,szTENPO,sbWhereTB);
		@SuppressWarnings("static-access")
		JSONArray keiArray = iL.selectJSONArray(sqlCommandKei, null, Defines.STR_JNDI_DS);

		// 入力期間情報取得
		boolean canChangeEventKikan = super.canChangeEventKikan(getMap(), userInfo);

		// オプション情報（タイトル）設定
		JSONObject option = new JSONObject();
		option.put(DefineReport.ID_PARAM_OPT_TITLE, titleList.toArray(new String[titleList.size()]));
		option.put("updUser", updUser);
		option.put("canChangeEventKikan", canChangeEventKikan);
		if(keiArray.size() > 0){
			option.put("totals", keiArray);
		}
		setOption(option);

		//System.out.println(getClass().getSimpleName()+"[sql]"+sbSQL.toString());
		return sbSQL.toString();
	}


	private String createCommandKei(String szWhereK, String sbWhereT, String sbWhereB,String szTENPO, String sbWhereTB) {
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("with MCAL as ( ");
		sbSQL.append("  select T1.COMTOB as DT");
		sbSQL.append("  ,WEEK_ISO(TO_DATE(T1.COMTOB, 'YYYYMMDD')) as WEEK");
		sbSQL.append("  ,TO_CHAR(TO_DATE(MIN(T1.COMTOB) over (partition by WEEK_ISO(TO_DATE(T1.COMTOB, 'YYYYMMDD'))), 'yyyymmdd'), 'mm/dd') || '-' || TO_CHAR(TO_DATE(MAX(T1.COMTOB) over (partition by WEEK_ISO(TO_DATE(T1.COMTOB, 'YYYYMMDD'))), 'yyyymmdd'), 'mm/dd') as TXT");
		sbSQL.append("  from SATYS.MCALTT T1");
		sbSQL.append("  where COMTOB"+szWhereK);
		sbSQL.append(") ");
		sbSQL.append(",MCAL2 as ( ");
		sbSQL.append("  select nvl(T1.WEEK, 999) as WEEK, max(T1.DT) as MAXDT");
		sbSQL.append("  ,case when T1.WEEK is null then '月間計' else max(T1.TXT) end as TXT");
		sbSQL.append("  from MCAL T1 ");
		sbSQL.append("  group by grouping sets(T1.WEEK, ())");
		sbSQL.append(") ");
		sbSQL.append(",MCALZ as ( ");
		sbSQL.append("  select T1.DT, T1.DT_KIJUN");
		sbSQL.append("  ,WEEK_ISO(TO_DATE(T1.DT, 'YYYYMMDD')) as WEEK");
		sbSQL.append("  ,'"+szTENPO+"' as MISECD,T4.MISECD_HT");
		sbSQL.append("  from ");
		sbSQL.append("  (select DT, DT_KIJUN from SATYS.TABKNK where DT"+szWhereK+") T1 ");
		sbSQL.append("  left outer join SATYS.TSTKNR T4 on T1.DT_KIJUN > T4.NENTUKI_OP||'00'" + sbWhereT.replace("T1", "T4"));
		sbSQL.append("  order by DT ");
		sbSQL.append(") ");
		sbSQL.append(",MCALZ2 as ( ");
		sbSQL.append("  select nvl(T1.WEEK, 999) as WEEK, max(T1.DT) as MAXDT");
		sbSQL.append("  from MCALZ T1 ");
		sbSQL.append("  group by grouping sets(T1.WEEK, ())");
		sbSQL.append(") ");
		sbSQL.append(",BDYS as ( ");
		sbSQL.append("  select nvl(T2.WEEK, 999) as WEEK ");
		sbSQL.append("  ,sum(T1.UYOSAN) as UYOSAN ");
		sbSQL.append("  ,sum(T1.AYOSAN) as AYOSAN ");
		sbSQL.append("  ,sum(T1.TYOSAN) as TYOSAN ");
		sbSQL.append("  from SATYS.TTBDYS T1 ");
		sbSQL.append("  inner join MCAL T2 on T1.DT = T2.DT "+sbWhereT+sbWhereB);
		sbSQL.append("  group by grouping sets(T2.WEEK, ()) ");
		sbSQL.append(") ");
//		sbSQL.append(",HBZ1 as ( ");
//		sbSQL.append("  select  T2.WEEK,T2.DT, T3.BUNBMC ");
//		sbSQL.append("  ,decimal(sum(T1.UYOSAN))/1000 as UYOSAN ");
//		sbSQL.append("  from SATTR.HIBYDD T1 ");
//		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD");
//		sbSQL.append("  inner join SATTR.MCLSHB T3 on T1.CLSSGK = T3.CLSSGK"+sbWhereB.replace("T1", "T3"));
//		sbSQL.append("  group by T2.WEEK,T2.DT, T3.BUNBMC");
//		sbSQL.append(") ");
//		sbSQL.append(",HBZ2 as ( ");
//		sbSQL.append("  select  T2.WEEK,T2.DT, T3.BUNBMC ");
//		sbSQL.append("  ,decimal(sum(T1.UYOSAN))/1000 as UYOSAN ");
//		sbSQL.append("  from SATTR.HIBYDD T1 ");
//		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD_HT");
//		sbSQL.append("  inner join SATTR.MCLSHB T3 on T1.CLSSGK = T3.CLSSGK"+sbWhereB.replace("T1", "T3"));
//		sbSQL.append("  group by T2.WEEK,T2.DT, T3.BUNBMC");
//		sbSQL.append(") ");
//		sbSQL.append(",HBZ as ( ");
//		sbSQL.append("  select nvl(nvl(T1.WEEK, T2.WEEK), 999) as WEEK");
//		sbSQL.append("  ,sum(nvl(T1.UYOSAN, T2.UYOSAN)) as UYOSAN");
//		sbSQL.append("  from HBZ1 T1 full join HBZ2 T2 on T1.DT = T2.DT and T1.BUNBMC = T2.BUNBMC");
//		sbSQL.append("  group by grouping sets(nvl(T1.WEEK, T2.WEEK), ())");
//		sbSQL.append(") ");
		sbSQL.append(",MCLSHA AS (");
		sbSQL.append("  select distinct BUNBMC, TOUKATU_CD_S from SATTR.MCLSHA where TOUKATU_CD_S in (select distinct TOUKATU_CD_S from SATYS.MCLSTT)" + sbWhereTB);
		if (sbWhereTB.length()==0){
			// 部門「すべて」の場合、統括部門'28'を集計に含める
			sbSQL.append(" UNION ALL SELECT BUNBMC,TOUKATU_CD_S FROM SATMS.BUMON_KANRI_MST WHERE TOUKATU_CD_S='28' ");
		}
		sbSQL.append(") ");
		sbSQL.append(",HA as ( ");
		sbSQL.append("  select  ");
		sbSQL.append("   nvl(T2.WEEK, 999) as WEEK");
		sbSQL.append("  ,sum(T1.URIKINGAKU) as URIKINGAKU ");
		sbSQL.append("  ,sum(T1.ARKINGAKU ) as ARKINGAKU ");
		sbSQL.append("  from SATTR.HABMDD T1 ");
		sbSQL.append("  inner join MCAL T2 on T1.COMTOB = T2.DT "+sbWhereT);
		sbSQL.append("  inner join MCLSHA T3 on T1.BUNBMC = T3.BUNBMC");
		sbSQL.append("  group by grouping sets(T2.WEEK, ()) ");
		sbSQL.append(") ");
		sbSQL.append(",HAZ1 as ( ");
		sbSQL.append("  select T2.WEEK,T2.DT, T3.TOUKATU_CD_S");
		sbSQL.append("  ,sum(T1.URIKINGAKU) as URIKINGAKU ");
		sbSQL.append("  ,sum(T1.ARKINGAKU ) as ARKINGAKU ");
		sbSQL.append("  from SATTR.HABMDD T1 ");
		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD");
		sbSQL.append("  inner join MCLSHA T3 on T1.BUNBMC = T3.BUNBMC");
		sbSQL.append("  where T1.URIKINGAKU <> 0");
		sbSQL.append("  group by T2.WEEK,T2.DT, T3.TOUKATU_CD_S");
		sbSQL.append(") ");
		sbSQL.append(",HAZ2 as ( ");
		sbSQL.append("  select T2.WEEK,T2.DT, T3.TOUKATU_CD_S");
		sbSQL.append("  ,sum(T1.URIKINGAKU) as URIKINGAKU ");
		sbSQL.append("  ,sum(T1.ARKINGAKU ) as ARKINGAKU ");
		sbSQL.append("  from SATTR.HABMDD T1 ");
		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD_HT");
		sbSQL.append("  inner join MCLSHA T3 on T1.BUNBMC = T3.BUNBMC");
		sbSQL.append("  where T1.URIKINGAKU <> 0");
		sbSQL.append("  group by T2.WEEK,T2.DT, T3.TOUKATU_CD_S");
		sbSQL.append(") ");
		sbSQL.append(",HAZ as ( ");
		sbSQL.append("  select nvl(nvl(T1.WEEK, T2.WEEK), 999) as WEEK");
		sbSQL.append("  ,sum(nvl(T1.URIKINGAKU, T2.URIKINGAKU)) as URIKINGAKU");
		sbSQL.append("  ,sum(nvl(T1.ARKINGAKU , T2.ARKINGAKU )) as ARKINGAKU");
		sbSQL.append("  from HAZ1 T1 full outer join HAZ2 T2 on T1.DT = T2.DT and T1.TOUKATU_CD_S = T2.TOUKATU_CD_S");
		sbSQL.append("  group by grouping sets(nvl(T1.WEEK, T2.WEEK), ())");
		sbSQL.append(") ");
		sbSQL.append(",SI as ( ");
		sbSQL.append("  select nvl(T2.WEEK, 999) as WEEK");
		sbSQL.append("  ,sum(T1.GENKAKEI) as GENKAKEI ");
		sbSQL.append("  from SATTR.SIBMDD T1 ");
		sbSQL.append("  inner join MCAL T2 on T1.COMTOB = T2.DT "+sbWhereT+sbWhereB);
		sbSQL.append("  group by grouping sets(T2.WEEK, ()) ");
		sbSQL.append(") ");
		sbSQL.append(",KYK as ( ");
		sbSQL.append("  select  ");
		sbSQL.append("   nvl(T2.WEEK, 999) as WEEK");
		sbSQL.append("  ,sum(T1.KYAKUSU_1F) as KYAKUSU ");
		sbSQL.append("  from SATTR.TNKYDD T1 ");
		sbSQL.append("  inner join MCAL T2 on T1.COMTOB = T2.DT "+sbWhereT);
		sbSQL.append("  group by grouping sets(T2.WEEK, ()) ");
		sbSQL.append(") ");
		sbSQL.append(",KYKZ1 as ( ");
		sbSQL.append("  select nvl(T2.WEEK, 999) as WEEK");
		sbSQL.append("  ,sum(T1.KYAKUSU_1F) as KYAKUSU ");
		sbSQL.append("  from SATTR.TNKYDD T1 ");
		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD");
		sbSQL.append("  group by grouping sets(T2.WEEK, ()) ");
		sbSQL.append(") ");
		sbSQL.append(",KYKZ2 as ( ");
		sbSQL.append("  select nvl(T2.WEEK, 999) as WEEK");
		sbSQL.append("  ,sum(T1.KYAKUSU_1F) as KYAKUSU ");
		sbSQL.append("  from SATTR.TNKYDD T1 ");
		sbSQL.append("  inner join MCALZ T2 on T1.COMTOB = T2.DT_KIJUN and T1.MISECD = T2.MISECD_HT");
		sbSQL.append("  group by grouping sets(T2.WEEK, ()) ");
		sbSQL.append(") ");
		sbSQL.append(",KYKZ as ( ");
		sbSQL.append("  select nvl(T1.WEEK, T2.WEEK) as WEEK");
		sbSQL.append("  ,nvl(T1.KYAKUSU, T2.KYAKUSU) as KYAKUSU ");
		sbSQL.append("  from KYKZ1 T1 full outer  join KYKZ2 T2 on T1.WEEK = T2.WEEK");
		sbSQL.append(") ");
		sbSQL.append(" select ");
		sbSQL.append("  M1.TXT as W1");																								// F1：期間
		sbSQL.append(" ,truncate(T1.AYOSAN,0) as W2");
		sbSQL.append(" ,truncate(decimal(T3.URIKINGAKU)/1000,0) as W3");
		sbSQL.append(" ,truncate(decimal(nvl(T3.URIKINGAKU,0))/1000,0)-truncate(nvl(T1.AYOSAN,0),0) as W4");
		sbSQL.append(" ,case when nvl(T1.AYOSAN*1000,0)= 0 or nvl(T3.URIKINGAKU,0)= 0 then 0 else truncate(decimal(T3.URIKINGAKU)/(T1.AYOSAN*1000)* 100, 1) end as W5");			// F5:予算比
		sbSQL.append(" ,truncate(decimal(T4.URIKINGAKU)/1000,0) as W6");
		sbSQL.append(" ,case when nvl(T4.URIKINGAKU,0) = 0 or nvl(T3.URIKINGAKU,0)= 0 then 0 else truncate(decimal(T3.URIKINGAKU)/T4.URIKINGAKU* 100, 1) end as W7");
		sbSQL.append(" ,T6.KYAKUSU as W8");
		sbSQL.append(" ,T7.KYAKUSU as W9");
		sbSQL.append(" ,case when nvl(T7.KYAKUSU,0)= 0 or nvl(T6.KYAKUSU,0)= 0 then 0 else truncate(decimal(T6.KYAKUSU)/T7.KYAKUSU* 100, 1) end as W10");			// F10:前年比(客数)
		sbSQL.append(" ,truncate(decimal(T5.GENKAKEI )/1000,0) as W11");
		sbSQL.append(" ,truncate(decimal(T3.ARKINGAKU)/1000,0) as W12");
		sbSQL.append(" ,case when nvl(T3.URIKINGAKU,0) = 0 or nvl(T3.ARKINGAKU,0) = 0 then 0 else truncate(decimal(T3.ARKINGAKU)/T3.URIKINGAKU* 100, 1) end as W13");
		sbSQL.append(" from ");
		sbSQL.append("  MCAL2 M1  ");
		sbSQL.append("  left outer join BDYS  T1 on M1.WEEK = T1.WEEK ");
		//sbSQL.append("  left outer join HBZ   T2 on M1.WEEK = T2.WEEK ");
		sbSQL.append("  left outer join HA    T3 on M1.WEEK = T3.WEEK ");
		sbSQL.append("  left outer join HAZ   T4 on M1.WEEK = T4.WEEK ");
		sbSQL.append("  left outer join SI    T5 on M1.WEEK = T5.WEEK ");
		sbSQL.append("  left outer join KYK   T6 on M1.WEEK = T6.WEEK ");
		sbSQL.append("  left outer join KYKZ  T7 on M1.WEEK = T7.WEEK ");
		sbSQL.append(" order by ");
		sbSQL.append("  M1.MAXDT, M1.WEEK ");

		return sbSQL.toString();
	}


	private void outputQueryList() {

		// 検索条件の加工クラス作成
		JsonArrayData jad = new JsonArrayData();
		jad.setJsonString(getJson());

		// 保存用 List (検索情報)作成
		setWhere(new ArrayList<List<String>>());
		List<String> cells = new ArrayList<String>();
		cells.add( DefineReport.Select.KIKAN.getTxt() );
		cells.add( jad.getJSONText(DefineReport.Select.KIKAN_F.getObj()));
		cells.add( DefineReport.Select.TENPO.getTxt());
		cells.add( jad.getJSONText( DefineReport.Select.TENPO.getObj()) );
		cells.add( DefineReport.Select.BUMON.getTxt());
		cells.add( jad.getJSONText( DefineReport.Select.BUMON.getObj()) );
		getWhere().add(cells);

		// 共通箇所設定
		createCmnOutput(jad);

	}

	/**
	 * 更新処理実行
	 * @return
	 *
	 * @throws Exception
	 */
	private JSONObject updateData(HashMap<String, String> map, User userInfo) throws Exception {
		// パラメータ確認
		String szKikanF			= map.get("KIKAN_F");							// 期間FROM
		String szTenpo 			= map.get("TENPO");								// 店舗
		String szBumon 			= map.get("BUMON");								// 部門
		JSONArray dataArray = JSONArray.fromObject(map.get("DATA"));			// 対象情報


		JSONObject msgObj = new JSONObject();

		// ログインユーザー情報取得
		int userId	= userInfo.getCD_user();								// ログインユーザー

		// 更新情報
		String values = "";
		ArrayList<String> params = new ArrayList<String>();
		for (int i = 0; i < dataArray.size(); i++) {
			JSONObject data = dataArray.getJSONObject(i);
			if(data.isEmpty()){
				continue;
			}
			values += ",('"+szTenpo+"','"+data.optString("F1")+"',?)";
			params.add(data.optString("F2"));
		}
		values = StringUtils.removeStart(values, ",");

		if(values.length()==0){
			msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E00001.getVal()));
			return msgObj;
		}

		// 基本INSERT/UPDATE文
		StringBuffer sbSQL;
		sbSQL = new StringBuffer();
		sbSQL.append("merge into SATYS.TTDEVT as T");
		sbSQL.append(" using (select");
		sbSQL.append(" cast(T1.MISECD as character(3)) as MISECD");			// 店コード
		sbSQL.append(",cast(T1.DT as character(8)) as DT");					// 予算年月日
		sbSQL.append(",cast(T1.EVENT as varchar(1000)) as EVENT");			// イベント
		sbSQL.append(",cast("+userId+" as integer) as CD_UPDATE");			// 更新者
		sbSQL.append(",current timestamp as DT_UPDATE");					// 更新日
		sbSQL.append(" from (values"+values+") as T1(MISECD, DT, EVENT)");
		sbSQL.append(" ) as RE on (T.MISECD = RE.MISECD and T.DT = RE.DT) ");
		sbSQL.append(" when matched then ");
		sbSQL.append(" update set");
		sbSQL.append("  EVENT    =RE.EVENT");
		sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
		sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
		sbSQL.append(" when not matched then ");
		sbSQL.append(" insert");
		sbSQL.append(" values(RE.MISECD,RE.DT,RE.EVENT,0.0,RE.CD_UPDATE,RE.DT_UPDATE,RE.CD_UPDATE,RE.DT_UPDATE)");
		//sbSQL.append(";");

		int count = super.executeSQL(sbSQL.toString(), params);
		if(StringUtils.isEmpty(getMessage())){
			if (DefineReport.ID_DEBUG_MODE) System.out.println(MessageUtility.getMessage(Msg.S00003.getVal(), new String[]{Integer.toString(dataArray.size()) , Integer.toString(count)}));
			msgObj.put(MsgKey.S.getKey(), MessageUtility.getMessage(Msg.S00001.getVal()));
		}else{
			msgObj.put(MsgKey.E.getKey(), getMessage());
		}
		return msgObj;
	}

	/**
	 * チェック処理
	 *
	 * @throws Exception
	 */
	public JSONArray check(HashMap<String, String> map, User userInfo) {
		// パラメータ確認
		JSONArray dataArray = JSONArray.fromObject(map.get("DATA"));	// 更新情報

		JSONArray msg = new JSONArray();

		// チェック処理
		if(!super.isUserTenpo(map, userInfo)){
			msg.add(MessageUtility.getMessageObj(Msg.E00001.getVal()));
			return msg;
		}
		// 対象件数チェック
		if(dataArray.size() == 0 || dataArray.getJSONObject(0).isEmpty()){
			msg.add(MessageUtility.getMessageObj(Msg.E10000.getVal()));
			return msg;
		}

		// 各データ行チェック
		FieldType fieldType = FieldType.GRID;
		String rowIndex = "";
		for (int i = 0; i < dataArray.size(); i++) {
			JSONObject data = dataArray.getJSONObject(i);
			rowIndex = data.optString("IDX");
			// 今年の要因
			if(!InputChecker.isTextLessThanMaxByteLength(data.optString("F2"), 1000)){	// 桁数チェック
				msg.add(MessageUtility.getCheckMaxLengthMessage(DefineReport.InpText.EVENT_DD, fieldType, new String[]{rowIndex}));
			}
		}
		return msg;
	}
}
