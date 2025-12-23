/**
 *
 */
package dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import authentication.bean.User;
import common.CmnDate;
import common.CmnDate.DATE_FORMAT;
import common.DefineReport;
import common.InputChecker;
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
public class Report004Dao extends ItemDao {
	private static String DECI_DIGITS = ",21,5";

	/**
	 * インスタンスを生成します。
	 * @param source
	 */
	public Report004Dao(String JNDIname) {
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

		String szKikanF			= getMap().get("KIKAN_F");		// 特定日

		// パラメータ確認
		// 必須チェック
		if ( (szKikanF == null) ) {
			System.out.println(super.getConditionLog());
			return "";
		}

		// タイトル情報(任意)設定
		List<String> titleList = new ArrayList<String>();

		// 期間条件
		String szDtF = szKikanF+"01";
		String szDtT = CmnDate.dateFormat(CmnDate.getLastDateOfMonth(szKikanF+"01"));
		String szWhereK  = " between '"+szDtF+"' and '"+szDtT+"'";

		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("with MCAL as ( ");
		sbSQL.append("  select T1.COMTOB as DT");
		sbSQL.append("  from SATYS.MCALTT T1 ");
		sbSQL.append("  where COMTOB"+szWhereK);
		sbSQL.append(") ");
		sbSQL.append(",ABKNK as ( ");
		sbSQL.append(" select DT, max(DT_KIJUN) as DT_KIJUN ");
		sbSQL.append(" from SATYS.TABKNK T1");
		sbSQL.append(" where DT"+szWhereK);
		sbSQL.append(" group by DT");
		sbSQL.append(" )");
		sbSQL.append(" select");
		sbSQL.append("  to_char(to_date(M1.DT, 'yyyymmdd'), 'yyyy/mm/dd')");
		sbSQL.append(" ,LEFT(T1.DT_KIJUN,4) || '/' || SUBSTR(T1.DT_KIJUN,5,2) || '/' || RIGHT(T1.DT_KIJUN,2)");
		sbSQL.append(" from MCAL M1");
		sbSQL.append(" left outer join ABKNK T1 on T1.DT = M1.DT");
		sbSQL.append(" order by M1.DT ");

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
		JSONArray dataArray = JSONArray.fromObject(map.get("DATA"));	// 対象情報


		JSONObject msgObj = new JSONObject();

		// ログインユーザー情報取得
		int userId	= userInfo.getCD_user();								// ログインユーザー

		// 更新情報
		String values = "";
		for (int i = 0; i < dataArray.size(); i++) {
			JSONObject data = dataArray.getJSONObject(i);
			if(data.isEmpty()){
				continue;
			}
			String dt		= StringUtils.remove(data.optString("F1"), "/");	// 特定日
			String dt_kjn	= StringUtils.remove(data.optString("F2"), "/");	// 基準日
			values += ",('"+dt+"','"+dt_kjn+"')";
		}
		values = StringUtils.removeStart(values, ",");

		if(values.length()==0){
			msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E00001.getVal()));
			return msgObj;
		}

		// 基本INSERT/UPDATE文
		StringBuffer sbSQL;
		ArrayList<String> prmData = new ArrayList<String>();

		sbSQL = new StringBuffer();
		sbSQL.append("merge into SATYS.TABKNK as T");
		sbSQL.append(" using (select");
		sbSQL.append(" cast(T1.DT as character(8)) as DT");					// 年月日
		sbSQL.append(",cast(T1.DT_KIJUN as character(8)) as DT_KIJUN");		// 基準年月日
		sbSQL.append(",cast("+userId+" as integer) as CD_UPDATE");			// 更新者
		sbSQL.append(",current timestamp as DT_UPDATE");					// 更新日
		sbSQL.append(" from (values"+values+") as T1(DT, DT_KIJUN)");
		sbSQL.append(" ) as RE on (T.DT = RE.DT) ");
		sbSQL.append(" when matched then ");
		sbSQL.append(" update set");
		sbSQL.append("  DT_KIJUN =RE.DT_KIJUN");
		sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
		sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
		sbSQL.append(" when not matched then ");
		sbSQL.append(" insert");
		sbSQL.append(" values(RE.DT,RE.DT_KIJUN,RE.CD_UPDATE,RE.DT_UPDATE,RE.CD_UPDATE,RE.DT_UPDATE)");
		//sbSQL.append(";");

		int count = super.executeSQL(sbSQL.toString(), prmData);
		if(StringUtils.isEmpty(getMessage())){
			if (DefineReport.ID_DEBUG_MODE) System.out.println("按分基準日管理を "+MessageUtility.getMessage(Msg.S00003.getVal(), new String[]{Integer.toString(dataArray.size()) , Integer.toString(count)}));
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
	public JSONArray check(HashMap<String, String> map) {
		// パラメータ確認
		JSONArray dataArray = JSONArray.fromObject(map.get("DATA"));	// 更新情報

		JSONArray msg = new JSONArray();

		// チェック処理
		// 対象件数チェック
		if(dataArray.size() == 0 || dataArray.getJSONObject(0).isEmpty()){
			msg.add(MessageUtility.getMessageObj(Msg.E10000.getVal()));
			return msg;
		}

		// 各データ行チェック
		FieldType fieldType = FieldType.GRID;
		String rowIndex = "";
		String val = "";
		for (int i = 0; i < dataArray.size(); i++) {
			JSONObject data = dataArray.getJSONObject(i);
			rowIndex = data.optString("IDX");
			val = data.optString("F2");
			// 基準日
			if(!InputChecker.isNotNull(val)){	// 必須チェック
				msg.add(MessageUtility.getCheckNullMessage(DefineReport.InpText.KIJUN_DT.getTxt(), fieldType, new String[]{rowIndex}));
			}else if( !InputChecker.isValidDate(val)){	// 日付妥当性チェック
				String year = Integer.toString(NumberUtils.toInt(StringUtils.left(val, 4), 0));
				if(!(InputChecker.isLeapYear(year) && "0299".equals(StringUtils.right(val, 4)))){	// 閏年特殊チェック
					msg.add(MessageUtility.getMessageObj(Msg.E20005.getVal(), new String[]{DefineReport.InpText.KIJUN_DT.getTxt(), DATE_FORMAT.GRID_YMD.formatPattern(), rowIndex}));
				}
			}
		}
		return msg;
	}
}
