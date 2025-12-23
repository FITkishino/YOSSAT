package dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import authentication.bean.User;
import common.CmnDate;
import common.DefineReport;
import common.Defines;
import common.ItemList;
import common.JsonArrayData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 */
public class Report006Dao extends ItemDao {

	/**
	 * インスタンスを生成します。
	 * @param source
	 */
	public Report006Dao(String JNDIname) {
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


	private String createCommand() {
		// ログインユーザー情報取得
		User userInfo = getUserInfo();
		if(userInfo==null){
			return "";
		}

		String szKikanF		= getMap().get("KIKAN_F");		// 期間FROM
		String szTENPO		= getMap().get("TENPO");		// 店舗
		JSONArray bumonArray	= JSONArray.fromObject(getMap().get("BUMON"));		// 部門


		// パラメータ確認
		// 必須チェック
		if ((szKikanF == null) || (szTENPO == null) ) {
			System.out.println(super.getConditionLog());
			return "";
		}

		// タイトル情報(任意)設定
		List<String> titleList = new ArrayList<String>();
		titleList.addAll(Arrays.asList(new String[]{"期間","今年の要因","予測客数"}));

		// 期間条件
		String szDtF = szKikanF+"01";
		String szDtT = CmnDate.dateFormat(CmnDate.getLastDateOfMonth(szKikanF+"01"));
		String szWhereK  = " between '"+szDtF+"' and '"+szDtT+"'";

		StringBuffer sbWhereT = new StringBuffer();		// 店舗条件
		if(!DefineReport.Values.NONE.getVal().equals(szTENPO)){
			sbWhereT.append(" and T1.MISECD = '" + szTENPO + "'");
		}
		StringBuffer sbWhereB = new StringBuffer();		// 部門条件
		if(!bumonArray.isEmpty() && !DefineReport.Values.NONE.getVal().equals(bumonArray.optString(0))){
			sbWhereB.append(" and BUNBMC in (" + StringUtils.removeEnd(StringUtils.replace(bumonArray.join(","),"\"","'"),",") + ")");
		}

		StringBuffer sbItm1 = new StringBuffer();
		StringBuffer sbItm2 = new StringBuffer();

		// 列情報取得
		ItemList iL = new ItemList();
		String sqlColCommand = "select BUNBMC as VAL,BUNBMC||' '||rtrim(max(TOUKATU_NM_S)) as \"TXT\" from SATTR.MCLSER M3 " + sbWhereB.toString().replaceFirst(" and", " where") + " group by BUNBMC order by VAL";
		@SuppressWarnings("static-access")
		JSONArray colArray = iL.selectJSONArray(sqlColCommand, null, Defines.STR_JNDI_DS);
		String val = "", key = "", txt = "";
		for (int i = -1; i < colArray.size(); i++) {
			if(i==-1){			// 合計列
				val = "00";
				txt = "合計";
				sbItm1.append(",sum(AYOSAN) as I"+val);
			}else{
				val = StringUtils.stripToEmpty(ObjectUtils.toString(colArray.getJSONObject(i).get("VAL")));
				txt = StringUtils.stripToEmpty(ObjectUtils.toString(colArray.getJSONObject(i).get("TXT")));
				sbItm1.append(",sum(case when BUNBMC = '"+val+"' then AYOSAN end) as I"+val);
			}
			sbItm2.append(",sum(I"+val+")");
			titleList.add(txt);
		}

		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("with MCAL as ( ");
		sbSQL.append("  select T1.COMTOB as DT, LEFT(T1.COMTOB, 6) as YM");
		sbSQL.append("  ,CHAR(TO_CHAR(TO_DATE(T1.COMTOB, 'yyyymmdd'), 'yyyy/mm/dd'), 10)||CASE DAYOFWEEK(TO_DATE(T1.COMTOB, 'yyyymmdd')) WHEN 1 THEN '(日)' WHEN 2 THEN '(月)' WHEN 3 THEN '(火)' WHEN 4 THEN '(水)' WHEN 5 THEN '(木)' WHEN 6 THEN '(金)' WHEN 7 THEN '(土)' END as TXT");
		sbSQL.append("  ,WEEK_ISO(TO_DATE(T1.COMTOB, 'YYYYMMDD')) as WEEK");
		sbSQL.append("  ,T2.EVENT,T2.TYOSAN");
		sbSQL.append("  from  ");
		sbSQL.append("  (select COMTOB from SATYS.MCALTT where COMTOB"+szWhereK+") T1 ");
		sbSQL.append("  left outer join ");
		sbSQL.append("  (select DT,EVENT,TYOSAN from SATYS.TTDEVT T1 where DT"+szWhereK+sbWhereT.toString()+") T2 ");
		sbSQL.append("  on T1.COMTOB = T2.DT ");
		sbSQL.append(") ");
		sbSQL.append(",BDYS as ( ");
		sbSQL.append("  select DT"+sbItm1.toString());
		sbSQL.append("  from SATYS.TTBDYS T1 ");
		sbSQL.append("  where T1.DT"+szWhereK+sbWhereT.toString()+sbWhereB.toString());
		sbSQL.append("  group by T1.DT ");
		sbSQL.append(") ");
		sbSQL.append(",DKYK as ( ");
		sbSQL.append("  select DT ");
		sbSQL.append("    ,SUM(T1.KYAKUSU) as KYAKUSU ");
		sbSQL.append("  from SATYS.TTDKYK T1 ");
		sbSQL.append("  where DT"+szWhereK+sbWhereT.toString());
		sbSQL.append("  group by DT ");
		sbSQL.append(") ");
		sbSQL.append(" select");
		sbSQL.append("  case when grouping(M1.DT)=1 then '合計' else max(M1.TXT) end");
		sbSQL.append(" ,case when grouping(M1.DT)=1 then '' else max(M1.EVENT) end");
		sbSQL.append(" ,sum(T2.KYAKUSU)"+sbItm2.toString());
		sbSQL.append(" from ");
		sbSQL.append("  MCAL M1  ");
		sbSQL.append("  left outer join BDYS  T1 on M1.DT = T1.DT ");
		sbSQL.append("  left outer join DKYK  T2 on M1.DT = T2.DT ");
		sbSQL.append(" group by grouping sets((),M1.DT) ");
		sbSQL.append(" order by ");
		sbSQL.append("  nvl(M1.DT, '0') ");

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

}
