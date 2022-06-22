package dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import authentication.bean.User;
import common.DefineReport;
import common.JsonArrayData;
import common.MessageUtility;
import common.MessageUtility.Msg;
import common.MessageUtility.MsgKey;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 */
public class Report005Dao extends ItemDao {

	/**
	 * インスタンスを生成します。
	 * @param source
	 */
	public Report005Dao(String JNDIname) {
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

	/**
	 * 削除処理
	 * @param userInfo
	 *
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public JSONObject delete(HttpServletRequest request,HttpSession session,  HashMap<String, String> map, User userInfo) {

		// 更新情報チェック(基本JS側で制御)
		JSONObject msgObj = new JSONObject();
		JSONArray msg = this.check(map);

		if(msg.size() > 0){
			msgObj.put(MsgKey.E.getKey(), msg);
			return msgObj;
		}

		// 削除処理
		try {
			msgObj = this.deleteData(map, userInfo);
		} catch (Exception e) {
			e.printStackTrace();
			msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E00002.getVal()));
		}
		return msgObj;
	}

	private String createCommand() {
		// ログインユーザー情報取得
		User userInfo = getUserInfo();
		if(userInfo==null){
			return "";
		}


		JSONArray tenpoArray	= JSONArray.fromObject(getMap().get("TENPO"));		// 店舗

		// パラメータ確認
		// 必須チェック
		if ( tenpoArray.size() == 0 ) {
			System.out.println(super.getConditionLog());
			return "";
		}

		// タイトル情報(任意)設定
		List<String> titleList = new ArrayList<String>();

		// 店舗条件
		String szWhereTenpo = " T1.MISECD IN ("
				+StringUtils.removeEnd(StringUtils.replace(tenpoArray.join(","),"\"","'"),",")
				+")";

		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append(" with MST as ( ");
		sbSQL.append(" select *");
//		sbSQL.append(" from INAYS.MTNPTT T1 ");
		sbSQL.append(" from INAMS.TENPO_MST T1 ");	// 20180425 change
		sbSQL.append(" where " + szWhereTenpo);
		sbSQL.append(" )");
		sbSQL.append(" select ");
		sbSQL.append("  M1.MISECD || ' ' || RTRIM(max(M1.TENMEI)) as F1 ");
		sbSQL.append(" ,max(T1.MISECD_HT)|| ' ' || RTRIM(max(M2.TENMEI)) as F2 ");
		sbSQL.append(" ,max(left(M1.TENKAH,6)) as F3 ");
		sbSQL.append(" ,M1.MISECD as F4 ");
		sbSQL.append(" from MST M1 ");
		sbSQL.append(" left join INAYS.TSTKNR T1 on M1.MISECD = T1.MISECD ");
		sbSQL.append(" left join INAYS.MTNPTT M2 on T1.MISECD_HT= M2.MISECD ");
		sbSQL.append(" group by M1.MISECD ");
		sbSQL.append(" order by M1.MISECD ");

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
			// 店コード,比較対象店舗
			values += ",('"+data.optString("F1")+"','"+data.optString("F2")+"')";
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
		sbSQL.append("merge into INAYS.TSTKNR as T");
		sbSQL.append(" using (select");
		sbSQL.append(" cast(T1.MISECD as character(3)) as MISECD");			// 店コード
		sbSQL.append(",cast(T1.MISECD_HT as character(3)) as MISECD_HT");	// 比較対象店舗
		sbSQL.append(",cast(T2.TENKAH as character(6)) as NENTUKI_OP");		// 開店年月
		sbSQL.append(",cast("+userId+" as integer) as CD_UPDATE");			// 更新者
		sbSQL.append(",current timestamp as DT_UPDATE");					// 更新日
		sbSQL.append(" from (values"+values+") as T1(MISECD, MISECD_HT)");
//		sbSQL.append(" left outer join INAYS.MTNPTT T2 on T1.MISECD = T2.MISECD");
		sbSQL.append(" left outer join INAMS.TENPO_MST T2 on T1.MISECD = T2.MISECD");	// 20180425 change
		sbSQL.append(" ) as RE on (T.MISECD = RE.MISECD) ");
		sbSQL.append(" when matched then ");
		sbSQL.append(" update set");
		sbSQL.append("  MISECD_HT=RE.MISECD_HT");
		sbSQL.append(" ,CD_UPDATE=RE.CD_UPDATE");
		sbSQL.append(" ,DT_UPDATE=RE.DT_UPDATE");
		sbSQL.append(" when not matched then ");
		sbSQL.append(" insert");
		sbSQL.append(" values(RE.MISECD,RE.MISECD_HT,RE.NENTUKI_OP,RE.CD_UPDATE,RE.DT_UPDATE,RE.CD_UPDATE,RE.DT_UPDATE)");
		//sbSQL.append(";");

		int count = super.executeSQL(sbSQL.toString(), prmData);
		if(StringUtils.isEmpty(getMessage())){
			if (DefineReport.ID_DEBUG_MODE) System.out.println("新店管理を "+MessageUtility.getMessage(Msg.S00003.getVal(), new String[]{Integer.toString(dataArray.size()) , Integer.toString(count)}));
			msgObj.put(MsgKey.S.getKey(), MessageUtility.getMessage(Msg.S00001.getVal()));
		}else{
			msgObj.put(MsgKey.E.getKey(), getMessage());
		}
		return msgObj;
	}

	/**
	 * 削除処理実行
	 * @return
	 *
	 * @throws Exception
	 */
	private JSONObject deleteData(HashMap<String, String> map, User userInfo) throws Exception {
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
			// 店コード,比較対象店舗
			values += ",'"+data.optString("F1")+"'";
		}
		values = StringUtils.removeStart(values, ",");

		if(values.length()==0){
			msgObj.put(MsgKey.E.getKey(), MessageUtility.getMessage(Msg.E00002.getVal()));
			return msgObj;
		}

		// 基本INSERT/UPDATE文
		StringBuffer sbSQL;
		ArrayList<String> prmData = new ArrayList<String>();

		sbSQL = new StringBuffer();
		sbSQL.append("delete from INAYS.TSTKNR where MISECD in ("+values+")");

		int count = super.executeSQL(sbSQL.toString(), prmData);
		if(StringUtils.isEmpty(getMessage())){
			if (DefineReport.ID_DEBUG_MODE) System.out.println("新店管理を "+MessageUtility.getMessage(Msg.S00004.getVal(), new String[]{Integer.toString(count)}));
			msgObj.put(MsgKey.S.getKey(), MessageUtility.getMessage(Msg.S00002.getVal()));
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
		// 対象件数チェック
		if(dataArray.size() == 0 || dataArray.getJSONObject(0).isEmpty()){
			msg.add(MessageUtility.getMessageObj(Msg.E10000.getVal()));
			return msg;
		}

		// 各データ行チェック
//		if(!outobj.equals(DefineReport.Button.DELETE.getObj())) {
//			FieldType fieldType = FieldType.GRID;
//			for (int i = 0; i < dataArray.size(); i++) {
//				JSONObject data = dataArray.getJSONObject(i);
//				String[] addParam = {Integer.toString(i+1)};
//				// 比較対象店舗必須チェック
//				if(!InputChecker.isNotNull(data.optString("F2"))){
//					msg.add(MessageUtility.getCheckNullMessage(DefineReport.Select.M_TENPO.getTxt(), fieldType, addParam));
//				}
//			}
//
//		}
		return msg;
	}
}
