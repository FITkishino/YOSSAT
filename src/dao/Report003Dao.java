/**
 *
 */
package dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import authentication.bean.User;
import common.CmnDate;
import common.CmnDate.DATE_FORMAT;
import common.DefineReport;
import common.DefineReport.ValAnbunKekka;
import common.DefineReport.ValAnbunSiji;
import common.JsonArrayData;
import common.MessageUtility;
import common.MessageUtility.Msg;
import common.MessageUtility.MsgKey;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 */
public class Report003Dao extends ItemDao {

	/**
	 * インスタンスを生成します。
	 * @param source
	 */
	public Report003Dao(String JNDIname) {
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
		JSONArray msg = this.check(map);

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
		JSONArray tenpoArray	= JSONArray.fromObject(getMap().get("TENPO"));		// 店舗
		JSONArray bumonArray	= JSONArray.fromObject(getMap().get("BUMON"));		// 部門
		String szBumonTxt 		= getMap().get("BUMON_TXT");						// 部門テキスト
		String szSys 			= getMap().get("SYS");								// 詳細表示

		// パラメータ確認
		// 必須チェック
		if ( (szKikanF == null) || tenpoArray.size() == 0 || bumonArray.size() == 0 ) {
			System.out.println(super.getConditionLog());
			return "";
		}

		// タイトル情報(任意)設定
		List<String> titleList = new ArrayList<String>();

		// 期間条件
		String szWhereDate = " T1.NENTUKI = '"+szKikanF+"' ";

		// 部門条件
		String szWhereBumon = " and T1.BUNBMC IN ("
				+StringUtils.removeEnd(StringUtils.replace(bumonArray.join(","),"\"","'"),",")
				+")";

		// 店舗条件
		String szWhereTenpo = " and T1.MISECD IN ("
				+StringUtils.removeEnd(StringUtils.replace(tenpoArray.join(","),"\"","'"),",")
				+")";

		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append(" with MYS as ( ");
		sbSQL.append(" select *");
		sbSQL.append(" from INAMS.TTBMYS T1 ");
		sbSQL.append(" where " + szWhereDate + szWhereBumon + szWhereTenpo);
		sbSQL.append(" )");
		if(!StringUtils.isEmpty(szSys)){
			sbSQL.append(" select ");
			sbSQL.append("  T1.MISECD || ' ' || RTRIM(M1.TENMEI) as F1 ");
			sbSQL.append(" ,T1.BUNBMC || ' ' || RTRIM(M2.TOUKATU_NM_S) as F2 ");
			sbSQL.append(" ,case when T2.ANBUNZ = '"+ValAnbunSiji.REDO.getVal()+"' then '"+ValAnbunSiji.REDO.getTxt()+"' end as F3 ");
			sbSQL.append(" ,case T2.ANBUNR when '"+ValAnbunKekka.SUCCESS.getVal()+"' then '"+ValAnbunKekka.SUCCESS.getTxt()+"' when '"+ValAnbunKekka.FAILURE.getVal()+"' then '"+ValAnbunKekka.FAILURE.getTxt()+"' end as F4 ");
			sbSQL.append(" ,to_char(T2.DT_UPDATE, 'YYYY/MM/DD hh24:mi:ss') as F5");
			sbSQL.append(" ,T1.MISECD as F6");
			sbSQL.append(" ,T1.BUNBMC as F7");
			sbSQL.append(" from MYS T1 ");
			sbSQL.append(" left join INAYS.TABKNR T2 on T1.MISECD = T2.MISECD and T1.BUNBMC = T2.BUNBMC and T1.NENTUKI = T2.NENTUKI ");
			sbSQL.append(" left join INAYS.MTNPTT M1 on T1.MISECD = M1.MISECD  ");
			sbSQL.append(" left join INAYS.MCLSTT M2 on T1.BUNBMC = M2.BUNBMC ");
			sbSQL.append(" order by T1.MISECD,T1.BUNBMC ");
		}else{
			sbSQL.append(" select ");
			sbSQL.append("  T1.MISECD || ' ' || RTRIM(max(M1.TENMEI)) as F1 ");
			if(StringUtils.equals(szBumonTxt, DefineReport.Values.ALL.getTxt())||bumonArray.size()==1){
				sbSQL.append(" ,'"+szBumonTxt+"' as F2 ");
			}else{
				sbSQL.append(" ,'複数 ('||count(T1.BUNBMC)||')' as F2 ");
			}
			sbSQL.append(" ,case sum(case when T2.ANBUNZ = '"+ValAnbunSiji.REDO.getVal()+"' then 1 end) when 0 then '' when count(T1.BUNBMC) then '"+ValAnbunSiji.REDO.getTxt()+"' else '"+ValAnbunSiji.REDO.getTxt()+"('||sum(case when T2.ANBUNZ = '"+ValAnbunSiji.REDO.getVal()+"' then 1 end)||')' end as F3 ");
			sbSQL.append(" ,case sum(case when T2.ANBUNR = '"+ValAnbunKekka.SUCCESS.getVal()+"' then 1 else 0 end) when 0 then '' when count(T1.BUNBMC) then '"+ValAnbunKekka.SUCCESS.getTxt()+"' else '"+ValAnbunKekka.SUCCESS.getTxt()+"('||sum(case when T2.ANBUNR = '"+ValAnbunKekka.SUCCESS.getVal()+"' then 1 end)||')  ' end");
			sbSQL.append("||case sum(case when T2.ANBUNR = '"+ValAnbunKekka.FAILURE.getVal()+"' then 1 else 0 end) when 0 then '' when count(T1.BUNBMC) then '"+ValAnbunKekka.FAILURE.getTxt()+"' else '"+ValAnbunKekka.FAILURE.getTxt()+"('||sum(case when T2.ANBUNR = '"+ValAnbunKekka.FAILURE.getVal()+"' then 1 end)||')' end as F4 ");
			sbSQL.append(" ,to_char(max(T2.DT_UPDATE), 'YYYY/MM/DD hh24:mi:ss') as F5");
			sbSQL.append(" ,T1.MISECD as F6");
			sbSQL.append(" ,'' as F7");
			sbSQL.append(" from MYS T1 ");
			sbSQL.append(" left join INAYS.TABKNR T2 on T1.MISECD = T2.MISECD and T1.BUNBMC = T2.BUNBMC and T1.NENTUKI = T2.NENTUKI ");
			sbSQL.append(" left join INAYS.MTNPTT M1 on T1.MISECD = M1.MISECD ");
			sbSQL.append(" group by T1.MISECD ");
			sbSQL.append(" order by T1.MISECD ");
		}

		// オプション情報（タイトル）設定
		JSONObject option = new JSONObject();
		option.put(DefineReport.ID_PARAM_OPT_TITLE, titleList.toArray(new String[titleList.size()]));
		setOption(option);

		//System.out.println(getClass().getSimpleName()+"[sql]"+sbSQL.toString());
		return sbSQL.toString();
	}

	private void outputQueryList() {

		// 検索条件の加工クラス作成
		JsonArrayData jad = new JsonArrayData();
		jad.setJsonString(getJson());

		// 保存用 List (検索情報)作成
		setWhere(new ArrayList<List<String>>());
		List<String> cells = new ArrayList<String>();

		// 期間系
		cells = new ArrayList<String>();
		cells.add( DefineReport.Select.KIKAN.getTxt() );
		cells.add( jad.getJSONText(DefineReport.Select.KIKAN_F.getObj()));
		getWhere().add(cells);

		// 店舗系
		cells = new ArrayList<String>();
		cells.add( DefineReport.Select.KIGYO.getTxt());
		cells.add( DefineReport.Select.TENPO.getTxt());
		cells.add( jad.getJSONText( DefineReport.Select.TENPO.getObj()) );
		getWhere().add(cells);

		// 分類系
		cells = new ArrayList<String>();
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
		JSONArray tenpoArray	= JSONArray.fromObject(map.get("TENPO"));		// 店舗
		JSONArray bumonArray	= JSONArray.fromObject(map.get("BUMON"));		// 部門
		String szBumonTxt 		= map.get("BUMON_TXT");							// 部門テキスト
		String szSys 			= map.get("SYS");								// 詳細表示
		JSONArray dataArray = JSONArray.fromObject(map.get("DATA"));			// 対象情報

		String outobj			= map.get(DefineReport.ID_PARAM_OBJ);			// 実行ボタン

		JSONObject msgObj = new JSONObject();
		JSONArray msg = new JSONArray();

		// ログインユーザー情報取得
		int userId	= userInfo.getCD_user();												// ログインユーザー

		// 更新情報
		String values = "";
		for (int i = 0; i < dataArray.size(); i++) {
			JSONObject data = dataArray.getJSONObject(i);
			if(data.isEmpty()){
				continue;
			}
			String szChk = StringUtils.isEmpty(data.optString("F3"))? "":ValAnbunSiji.REDO.getVal();
			values += ",('"+data.optString("F1")+"','"+data.optString("F2")+"','"+szChk+"')";
		}
		values = StringUtils.removeStart(values, ",");

		if(!outobj.equals(DefineReport.Button.ANBUN.getObj()) && values.length()==0){
			msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E00001.getVal()));
			return msgObj;
		}

		// 期間条件
		String szWhereDate = " T1.NENTUKI = '"+szKikanF+"' ";
		String szWhereDate2= " and left(T4.DT, 6) = '"+szKikanF+"' ";

		// 部門条件
		String szWhereBumon = " and T1.BUNBMC IN ("
				+StringUtils.removeEnd(StringUtils.replace(bumonArray.join(","),"\"","'"),",")
				+")";
		String szWhereTBumon = " and TOUKATU_CD_S IN ("
				+StringUtils.removeEnd(StringUtils.replace(bumonArray.join(","),"\"","'"),",")
				+")";

		// 店舗条件
		String szWhereTenpo = " and T1.MISECD IN ("
				+StringUtils.removeEnd(StringUtils.replace(tenpoArray.join(","),"\"","'"),",")
				+")";

		// 基本INSERT/UPDATE文
		StringBuffer sbSQL;

		// ******* ①按分指示登録 ******** //
		int count1 = 0;
		if(values.length() > 0){
			sbSQL = new StringBuffer();
			sbSQL.append("merge into INAYS.TABKNR as T");
			sbSQL.append(" using (select");
			sbSQL.append(" cast('"+szKikanF+"' as character(6)) as NENTUKI");						// 年月
			sbSQL.append(",cast(T1.MISECD as character(3)) as MISECD");								// 店コード
			sbSQL.append(",cast(T1.ANBUNZ as character(1)) as ANBUNZ");								// 按分指示
			sbSQL.append(",cast('"+ValAnbunKekka.YET.getVal()+"' as character(1)) as ANBUNR");		// 按分結果
			sbSQL.append(",cast("+userId+" as integer) as CD_UPDATE");								// 更新者
			sbSQL.append(",current timestamp as DT_UPDATE");										// 更新日
			if(!StringUtils.isEmpty(szSys)){
				sbSQL.append(",cast(T1.BUNBMC as character(4)) as BUNBMC");							// 部門コード
				sbSQL.append(" from (values"+values+") as T1(MISECD, BUNBMC, ANBUNZ)");
			}else{
				sbSQL.append(",cast(T2.BUNBMC as character(4)) as BUNBMC");							// 部門コード
				sbSQL.append(" from (values"+values+") as T1(MISECD, BUNBMC, ANBUNZ)");
				sbSQL.append(" inner join (select distinct MISECD,BUNBMC from INAMS.TTBMYS T1 where " + szWhereDate + szWhereBumon + szWhereTenpo + ") T2 on T1.MISECD = T2.MISECD");
			}
			sbSQL.append(" ) as RE on (T.NENTUKI = RE.NENTUKI and T.MISECD = RE.MISECD and T.BUNBMC = RE.BUNBMC) ");
			//sbSQL.append(" when matched and RE.ANBUNZ = '"+ValAnbunSiji.TARGET.getVal()+"' then ");
			sbSQL.append(" when matched then ");
			sbSQL.append(" update set");
			sbSQL.append("  ANBUNZ =RE.ANBUNZ");
			sbSQL.append(" ,ANBUNR =RE.ANBUNR");
			sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
			sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
			sbSQL.append(" when not matched then ");
			sbSQL.append(" insert");
			sbSQL.append(" values(RE.NENTUKI,RE.MISECD,RE.BUNBMC,RE.ANBUNZ,RE.ANBUNR,RE.CD_UPDATE,RE.DT_UPDATE,RE.CD_UPDATE,RE.DT_UPDATE)");
			//sbSQL.append(";");

			count1 = super.executeSQL(sbSQL.toString(), new ArrayList<String>());
			if (DefineReport.ID_DEBUG_MODE) System.out.println(MessageUtility.getMessage(Msg.S00006.getVal(), new String[]{"再按分指示", Integer.toString(count1)}));
		}

		int count2 = 0;
		int count3 = 0;
		if(outobj.equals(DefineReport.Button.ANBUN.getObj())){
			// ******* ②再按分処理 ******** //
			if(StringUtils.isEmpty(getMessage())){
				ArrayList<String> sqlList = new ArrayList<String>();
				ArrayList<ArrayList<String>> prmList = new ArrayList<ArrayList<String>>();
				ArrayList<String> lblList = new ArrayList<String>();
				ArrayList<Integer> countList  = new ArrayList<Integer>();

				String updatetime = CmnDate.dateFormat(new Date(), DATE_FORMAT.DB_DATETIME);

				// 按分SQL
				sbSQL = new StringBuffer();
				sbSQL.append(" MERGE INTO INAYS.TTBDYS G1");
				sbSQL.append(" USING (");
				sbSQL.append(" SELECT");
				sbSQL.append("  MISECD,DT,TO_CHAR(CURRENT_TIMESTAMP,'YYYYMMDD') AS DT_ENTRY,BUNBMC");
				sbSQL.append(" ,UYOSAN2 + CASE WHEN ROW_NUMBER() OVER(partition BY MISECD,BUNBMC,NENTUKI ORDER BY UYOSAN2 DESC,DT ) = 1 THEN UYOSAN2S ELSE 0 END AS UYOSAN");
				sbSQL.append(" ,UYOSAN2 + CASE WHEN ROW_NUMBER() OVER(partition BY MISECD,BUNBMC,NENTUKI ORDER BY UYOSAN2 DESC,DT ) = 1 THEN UYOSAN2S ELSE 0 END AS AYOSAN");
// 20170905		sbSQL.append(" ,UYOSAN2 + CASE WHEN ROW_NUMBER() OVER(partition BY MISECD,BUNBMC,NENTUKI ORDER BY UYOSAN2 DESC,DT ) = 1 THEN UYOSAN2S ELSE 0 END AS TYOSAN");
				sbSQL.append("  FROM");
				sbSQL.append(" (");
				sbSQL.append("  SELECT");
				sbSQL.append("  M1.MISECD,M1.BUNBMC,M1.DT,M1.NENTUKI");
				sbSQL.append(" ,CASE WHEN SUM(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0)) OVER (partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI)=0 THEN 0 ELSE");
				sbSQL.append("  TRUNCATE(CAST(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0) * NVL(M3.UYOSAN*1000,0) / SUM(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0)) OVER (partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI) AS DECIMAL(12,3)) / 1000 , 0)");
				sbSQL.append("  END");
				sbSQL.append("  + CASE WHEN DAYOFWEEK(TO_DATE(M1.DT,'YYYYMMDD')) = 1 THEN");
				sbSQL.append("  TRUNCATE(");
				sbSQL.append("  (NVL(M3.UYOSAN,0) - SUM(CASE WHEN SUM(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0)) OVER (partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI)=0 THEN 0 ELSE");
				sbSQL.append("  TRUNCATE(CAST(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0) * NVL(M3.UYOSAN*1000,0) / SUM(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0)) OVER (partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI) AS DECIMAL(12,3)) / 1000 , 0)");
				sbSQL.append("  END) OVER(partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI))");
				sbSQL.append("  / SUM(CASE WHEN DAYOFWEEK(TO_DATE(M1.DT,'YYYYMMDD')) = 1 THEN 1 ELSE 0 END) OVER(partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI)");
				sbSQL.append("  ,0)");
				sbSQL.append("  ELSE 0 END AS UYOSAN2");
				sbSQL.append(" ,NVL(M3.UYOSAN,0) - SUM(CASE WHEN SUM(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0)) OVER (partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI)=0 THEN 0 ELSE");
				sbSQL.append("  TRUNCATE(CAST(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0) * NVL(M3.UYOSAN*1000,0) / SUM(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0)) OVER (partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI) AS DECIMAL(12,3)) / 1000 , 0)");
				sbSQL.append("  END");
				sbSQL.append("  + CASE WHEN DAYOFWEEK(TO_DATE(M1.DT,'YYYYMMDD')) = 1 THEN");
				sbSQL.append("  TRUNCATE(");
				sbSQL.append("  (NVL(M3.UYOSAN,0) - SUM(CASE WHEN SUM(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0)) OVER (partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI)=0 THEN 0 ELSE");
				sbSQL.append("  TRUNCATE(CAST(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0) * NVL(M3.UYOSAN*1000,0) / SUM(NVL(M2.URIKINGAKU,M4.URIKINGAKU,0)) OVER (partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI) AS DECIMAL(12,3)) / 1000 , 0)");
				sbSQL.append("  END) OVER(partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI))");
				sbSQL.append("  / SUM(CASE WHEN DAYOFWEEK(TO_DATE(M1.DT,'YYYYMMDD')) = 1 THEN 1 ELSE 0 END) OVER(partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI)");
				sbSQL.append("  ,0)");
				sbSQL.append("  ELSE 0 END) OVER(partition by M1.MISECD,M1.BUNBMC,M1.NENTUKI)");
				sbSQL.append("  AS UYOSAN2S");
				sbSQL.append("  FROM (");
				sbSQL.append("   SELECT");
				sbSQL.append("    T1.MISECD");
				sbSQL.append("   ,MAX(NVL(T6.MISECD_HT,T1.MISECD)) AS MISECD_HT");
				sbSQL.append("   ,T1.BUNBMC");
				sbSQL.append("   ,CAL.COMTOB AS DT");
				sbSQL.append("   ,LEFT(CAL.COMTOB,6) AS NENTUKI");
				sbSQL.append("   ,MAX(T4.DT_KIJUN) AS DT_KIJUN");
				sbSQL.append("   FROM INAMS.TTBMYS T1");
				sbSQL.append("   INNER JOIN INAYS.MCALTT CAL ON T1.NENTUKI = CAL.NENTUKI and " + szWhereDate + szWhereBumon + szWhereTenpo);
				sbSQL.append("   INNER JOIN INAYS.TABKNK T4 ON CAL.COMTOB=T4.DT");
				sbSQL.append("   INNER JOIN INAYS.TABKNR T5 ON T1.MISECD=T5.MISECD AND T1.BUNBMC=T5.BUNBMC AND LEFT(CAL.COMTOB,6)=T5.NENTUKI AND T5.ANBUNZ='0'");
				sbSQL.append("   LEFT OUTER JOIN INAYS.TSTKNR T6 ON T1.MISECD=T6.MISECD");
				sbSQL.append("   GROUP BY T1.MISECD,T1.BUNBMC,CAL.COMTOB");
				sbSQL.append("  ) M1");

				sbSQL.append("  LEFT OUTER JOIN (");
				sbSQL.append("    SELECT T1.MISECD, T2.TOUKATU_CD_S as BUNBMC, T1.COMTOB, SUM(T1.URIKINGAKU) as URIKINGAKU");
				sbSQL.append("    FROM INATR.HABMDD T1");
				sbSQL.append("    INNER JOIN (select distinct BUNBMC, TOUKATU_CD_S from INATR.MCLSHA where 1=1"+szWhereTBumon+") T2 ON T1.BUNBMC=T2.BUNBMC AND T1.URIKINGAKU<>0"+szWhereTenpo);
				sbSQL.append("    INNER JOIN INAYS.TABKNK T4 ON T4.DT_KIJUN = T1.COMTOB "+szWhereDate2);
				sbSQL.append("    GROUP BY T1.MISECD, T2.TOUKATU_CD_S, T1.COMTOB");
				sbSQL.append("  ) M2 ON M1.MISECD=M2.MISECD AND M1.BUNBMC=M2.BUNBMC AND M1.DT_KIJUN=M2.COMTOB");
				sbSQL.append("  LEFT OUTER JOIN INAMS.TTBMYS M3 ON M1.MISECD=M3.MISECD AND M1.BUNBMC=M3.BUNBMC AND M1.NENTUKI=M3.NENTUKI");
				sbSQL.append("  LEFT OUTER JOIN (");
				sbSQL.append("    SELECT T1.MISECD, T2.TOUKATU_CD_S as BUNBMC, T1.COMTOB, SUM(T1.URIKINGAKU) as URIKINGAKU");
				sbSQL.append("    FROM INATR.HABMDD T1 ");
				sbSQL.append("    INNER JOIN (select distinct BUNBMC, TOUKATU_CD_S from INATR.MCLSHA where 1=1"+szWhereTBumon+") T2 ON T1.BUNBMC=T2.BUNBMC AND T1.URIKINGAKU<>0");
				sbSQL.append("    INNER JOIN INAYS.TABKNK T4 ON T4.DT_KIJUN = T1.COMTOB "+szWhereDate2);
				sbSQL.append("    GROUP BY T1.MISECD, T2.TOUKATU_CD_S, T1.COMTOB");
				sbSQL.append("  ) M4 ON M1.MISECD_HT=M4.MISECD AND M1.BUNBMC=M4.BUNBMC AND M1.DT_KIJUN=M4.COMTOB");

				sbSQL.append("  )XX");
				sbSQL.append("  ORDER BY MISECD,BUNBMC,DT");
				sbSQL.append(" ) G2 ON G1.MISECD=G2.MISECD AND G1.BUNBMC=G2.BUNBMC AND G1.DT=G2.DT");
				sbSQL.append(" WHEN MATCHED THEN UPDATE SET");
				sbSQL.append("  G1.DT_ENTRY=TO_CHAR(CURRENT_TIMESTAMP,'YYYYMMDD')");
				sbSQL.append(" ,G1.UYOSAN=G2.UYOSAN");
				sbSQL.append(" ,G1.AYOSAN=G2.AYOSAN");
// 20170905		sbSQL.append(" ,G1.TYOSAN=G2.TYOSAN");
				sbSQL.append(" ,G1.CD_UPDATE="+userId+"");
				sbSQL.append(" ,G1.DT_UPDATE=TIMESTAMP('"+updatetime+"')");
				sqlList.add(sbSQL.toString());
				prmList.add(new ArrayList<String>());
				lblList.add("店部門別日別予算");
/* 20170905
				// 店日別情報(TTDEVT)
				sbSQL = new StringBuffer();
				sbSQL.append("merge into INAYS.TTDEVT as T");
				sbSQL.append(" using (select");
				sbSQL.append(" MISECD, DT, sum(TYOSAN) as TYOSAN");
				sbSQL.append(",cast("+userId+" as integer) as CD_UPDATE");			// 更新者
				sbSQL.append(",current timestamp as DT_UPDATE");					// 更新日
				sbSQL.append(" from INAYS.TTBDYS T1");
				sbSQL.append(" where exists(");
//				sbSQL.append("   select 'X' from INAYS.TTBDYS T2 where CD_UPDATE = "+userId+" and DT_UPDATE = TIMESTAMP('"+updatetime+"') and T1.MISECD = T2.MISECD and T1.DT=T2.DT");
				sbSQL.append("   select 'X' from INAYS.TABKNR T2");
				sbSQL.append("   where " + szWhereDate.replace("T1", "T2") + szWhereBumon.replace("T1", "T2") + szWhereTenpo.replace("T1", "T2") + " and  T2.ANBUNZ='0' and T1.MISECD = T2.MISECD and LEFT(T1.DT,6)=T2.NENTUKI");
				sbSQL.append(" )");
				sbSQL.append(" group by MISECD, DT");
				sbSQL.append(" ) as RE on (T.MISECD = RE.MISECD and T.DT = RE.DT) ");
				sbSQL.append(" when matched then ");
				sbSQL.append(" update set");
				sbSQL.append("  TYOSAN   =RE.TYOSAN");
				sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
				sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
				sqlList.add(sbSQL.toString());
				prmList.add(new ArrayList<String>());
				lblList.add("店日別情報");
*/

				countList =  super.executeSQLs(sqlList, prmList);
				if(StringUtils.isEmpty(getMessage())){
					for (int i = 0; i < countList.size(); i++) {
						count2 += countList.get(i);
						if (DefineReport.ID_DEBUG_MODE) System.out.println(MessageUtility.getMessage(Msg.S00006.getVal(), new String[]{lblList.get(i), Integer.toString(countList.get(i))}));
					}
				}
			}

			// ******* ③再按分結果登録 ******** //
			ValAnbunSiji siji = ValAnbunSiji.DONE;
			ValAnbunKekka kekka = ValAnbunKekka.SUCCESS;
			if(count2 == 0 || StringUtils.isNotEmpty(getMessage())){
				siji = ValAnbunSiji.REDO;
				kekka = ValAnbunKekka.FAILURE;
			}
			sbSQL = new StringBuffer();
			sbSQL.append("merge into INAYS.TABKNR as T");
			sbSQL.append(" using (select T1.MISECD, T1.NENTUKI, T1.BUNBMC");
			sbSQL.append(",cast('"+siji.getVal()+"' as character(1)) as ANBUNZ");		// 按分指示
			sbSQL.append(",cast('"+kekka.getVal()+"' as character(1)) as ANBUNR");		// 按分結果
			sbSQL.append(" from INAYS.TABKNR T1 where ANBUNZ = '"+ValAnbunSiji.REDO.getVal()+"'");
			//sbSQL.append(",case when count(distinct T2.DT) = 0 then '"+ValAnbunSiji.REDO.getVal()+"' else '"+ValAnbunSiji.DONE.getVal()+"' end ANBUNZ");			// 按分指示
			//sbSQL.append(",case when count(distinct T2.DT) = 0 then '"+ValAnbunKekka.FAILURE.getVal()+"' else '"+ValAnbunKekka.SUCCESS.getVal()+"' end ANBUNR");	// 按分結果
			//sbSQL.append(" from (select * from INAYS.TABKNR T1 where ANBUNZ = '"+ValAnbunSiji.REDO.getVal()+"') T1");
			//sbSQL.append(" left outer join INAYS.TTBDYS T2 on T1.MISECD = T2.MISECD and T1.NENTUKI = LEFT(T2.DT,6) and T1.BUNBMC = T2.BUNBMC and T2.CD_UPDATE = "+userId+" and T2.DT_UPDATE = TIMESTAMP('"+updatetime+"')");
			//sbSQL.append(" group by T1.MISECD, T1.NENTUKI, T1.BUNBMC");
			sbSQL.append(" ) as RE on (T.NENTUKI = RE.NENTUKI and T.MISECD = RE.MISECD and T.BUNBMC = RE.BUNBMC) ");
			sbSQL.append(" when matched then ");
			sbSQL.append(" update set");
			sbSQL.append("  ANBUNZ =RE.ANBUNZ");
			sbSQL.append(" ,ANBUNR =RE.ANBUNR");
			sbSQL.append(" ,CD_UPDATE="+userId+"");
			sbSQL.append(" ,DT_UPDATE=current timestamp");

			count3 = super.executeSQL(sbSQL.toString(), new ArrayList<String>());
			if (DefineReport.ID_DEBUG_MODE) System.out.println(MessageUtility.getMessage(Msg.S00006.getVal(), new String[]{"再按分結果", Integer.toString(count3)}));
		}

		if(StringUtils.isEmpty(getMessage())){
			if( count1 + count2 + count3 == 0){
				msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E10000.getVal()));
			}else{
				msgObj.put(MsgKey.S.getKey(), MessageUtility.getMessage(Msg.S00001.getVal()));
			}
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
	public JSONArray check(HashMap<String, String> map) {
		// パラメータ確認
		JSONArray dataArray = JSONArray.fromObject(map.get("DATA"));	// 更新情報
		String outobj		= map.get(DefineReport.ID_PARAM_OBJ);		// 実行ボタン

		JSONArray msg = new JSONArray();

		// チェック処理
//		// 対象件数チェック
		if(!outobj.equals(DefineReport.Button.ANBUN.getObj())){
			if(dataArray.size() == 0 || dataArray.getJSONObject(0).isEmpty()){
				msg.add(MessageUtility.getMessageObj(Msg.E10000.getVal()));
				return msg;
			}
		}

		return msg;
	}
}
