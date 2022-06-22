package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.iq80.snappy.Snappy;

import authentication.bean.User;
import authentication.defines.Consts;
import common.DefineReport;
import common.Defines;
import common.MessageUtility;
import common.MessageUtility.Msg;
import common.MessageUtility.MsgKey;
import dao.ItemInterface;
import dao.Report001Dao;
import dao.Report002Dao;
import dao.Report003Dao;
import dao.Report004Dao;
import dao.Report005Dao;
import dao.Report006Dao;
import dto.JQEasyModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * Servlet implementation class JQGridJSON
 */
public class JQGridJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JQGridJSON() {
		super();
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 文字変換コード設定【重要】
		request.setCharacterEncoding("UTF-8");

		// パラメータ一覧【確認】
		HashMap<String,String> map = new HashMap<String,String>();
		Enumeration<String> enums = request.getParameterNames();
		while( enums.hasMoreElements() ) {
			String name = enums.nextElement();
			if (DefineReport.ID_DEBUG_MODE) System.out.println(name + "=" + request.getParameter( name ));
			map.put(name, request.getParameter( name ));
		}

		// レポート情報
		String report = request.getParameter("report");
		if (report == null) return;

		// セッション
		HttpSession session = request.getSession(false);

		int start = 0;
		int limit = 0;
		if (request.getParameter("rows") != null) {
			limit = Integer.parseInt(request.getParameter("rows"));	// ページ辺りの表示レコード数;
		}

		// コネクションの取得
		String JNDIname = Defines.STR_JNDI_DS;
		boolean runSelect = true;

		// 更新処理
		if (DefineReport.ID_PARAM_ACTION_UPDATE.equals(map.get(DefineReport.ID_PARAM_ACTION))) {
			// メッセージ表示用
			JSONObject option = null;

			User userInfo = (User)request.getSession().getAttribute(Consts.STR_SES_LOGINUSER);

			if (DefineReport.ID_PAGE_001.equals(report)) {
				option = new Report001Dao(JNDIname).update(request, session, map, userInfo);
			}else if (DefineReport.ID_PAGE_002.equals(report)) {
				option = new Report002Dao(JNDIname).update(request, session, map, userInfo);
			}else if (DefineReport.ID_PAGE_003.equals(report)) {
				option = new Report003Dao(JNDIname).update(request, session, map, userInfo);
			}else if (DefineReport.ID_PAGE_004.equals(report)) {
				option = new Report004Dao(JNDIname).update(request, session, map, userInfo);
			}else if (DefineReport.ID_PAGE_005.equals(report)) {
				option = new Report005Dao(JNDIname).update(request, session, map, userInfo);
			}

			boolean is_empty = true;
			for(MsgKey key : MsgKey.values()){
				if (option == null){
					break;
				} else if(option.containsKey(key.getKey())){
					is_empty = false;
					break;
				}
			}
			if(is_empty){
				// 表示するメッセージがない場合、エラーメッセージをセット
				option = new JSONObject();
				JSONArray msg = new JSONArray();
				msg.add(MessageUtility.getMessageObj(Msg.E00001.getVal()));
				option.put(MsgKey.E.getKey(), msg);
			}
			session.setAttribute(DefineReport.ID_SESSION_OPTION , option);
			runSelect = false;
		}

		// 削除処理が存在する場合
		if (DefineReport.ID_PARAM_ACTION_DELETE.equals(map.get(DefineReport.ID_PARAM_ACTION))) {
			// メッセージ表示用
			JSONObject option = null;

			User userInfo = (User)request.getSession().getAttribute(Consts.STR_SES_LOGINUSER);


			if (DefineReport.ID_PAGE_005.equals(report)) {
				option = new Report005Dao(JNDIname).delete(request, session, map, userInfo);
			}

			boolean is_empty = true;
			for(MsgKey key : MsgKey.values()){
				if (option == null){
					break;
				} else if(option.containsKey(key.getKey())){
					is_empty = false;
					break;
				}
			}
			if(is_empty){
				// 表示するメッセージがない場合、エラーメッセージをセット
				option = new JSONObject();
				JSONArray msg = new JSONArray();
				msg.add(MessageUtility.getMessageObj(Msg.E00002.getVal()));
				option.put(MsgKey.E.getKey(), msg);
			}
			session.setAttribute(DefineReport.ID_SESSION_OPTION , option);
			runSelect = false;

		}

		// 検索実行
		if (runSelect) {

			// 検索クラス作成
			if (DefineReport.ID_PAGE_001.equals(report)) {
				convertItem(new Report001Dao(JNDIname), request, map, limit, session, start);

			} else if (DefineReport.ID_PAGE_002.equals(report)) {
				convertItem(new Report002Dao(JNDIname), request, map, limit, session, start);

			} else if (DefineReport.ID_PAGE_003.equals(report)) {
				convertItem(new Report003Dao(JNDIname), request, map, limit, session, start);

			} else if (DefineReport.ID_PAGE_004.equals(report)) {
				convertItem(new Report004Dao(JNDIname), request, map, limit, session, start);

			} else if (DefineReport.ID_PAGE_005.equals(report)) {
				convertItem(new Report005Dao(JNDIname), request, map, limit, session, start);

			} else if (DefineReport.ID_PAGE_006.equals(report)) {
				convertItem(new Report006Dao(JNDIname), request, map, limit, session, start);

			}
		}

		// レコード情報の格納先(JSONObject)作成
		JSONObject jsonOB = new JSONObject();

		// jqEasy 用 JSON モデル作成
		JQEasyModel json = new JQEasyModel();

		// 項目単位の情報格納
		List<JSONObject> lineData = new ArrayList<JSONObject>();

		// レコードカウント
		int count = -1;

		// セルインデックス
		int index = 0;

		String states = "";

		if (session.getAttribute(DefineReport.ID_SESSION_TABLE) != null) {

			ArrayList<byte[]> al = (ArrayList<byte[]>) session.getAttribute(DefineReport.ID_SESSION_TABLE);
			int records = al.size();
			if (records > 0)	records--;	// タイトル行の除外
			json.setTotal(records);			// 総レコード数の設定

			// 実績なしの店舗・分類列を除外 ---------------------------------------
			if ( DefineReport.ID_PAGE_020.equals(report) ) {
				String szHYORETSU	= map.get("HYORETSU");	// 表列
				if(DefineReport.ValHyo.VAL22.getVal().equals(szHYORETSU)){
					// 固定列＋総計を除外開始位置に設定
					int startIdx = 1;
					// 後方の列数
					int rearCols = 2;
					// 除外しない列のキー（タイトル後方一致）
					String[] notDeKeys = new String[]{"計"};
					al = excludeColumns(session, json, al, startIdx, 1, rearCols, notDeKeys);
				}
			}else if ( DefineReport.ID_PAGE_021.equals(report) ) {
				String szHYORETSU	= map.get("HYORETSU");	// 表列
				if(DefineReport.ValHyo.VAL10.getVal().equals(szHYORETSU)){
					// 固定列＋総計を除外開始位置に設定
					int startIdx = 3;
					// 後方の列数
					int rearCols = 0;
					// 除外しない列のキー（タイトル後方一致）
					String[] notDeKeys = new String[]{"計"};
					al = excludeColumns(session, json, al, startIdx, 1, rearCols, notDeKeys);
				}
			}
			// --------------------------------------------------------------------------

			int page = 1;					// ページ位置（初期値）

			// 総ページ数の算出
			int total_pages=0;
			double ii = (double)records/(double)limit;
			if( records > 0 ) {
				total_pages = (int) Math.ceil(ii);
			} else {
				total_pages = 0;
			} // if for some

			if ( page  > total_pages) {
				page = total_pages; // calculate the starting position of the rows
			}

			start = limit * page - limit; // if for some reasons start position

			if( start < 0 ) {
				start = 0;
			}

			int state = 0;
			boolean stateFlag = false;

			Iterator<byte[]> itr = al.iterator();

			// 項目単位の情報格納
			lineData = new ArrayList<JSONObject>();

			while (itr.hasNext()) {

				count++;

				// 表示範囲の情報取得
				if ( ((start + 1) <= count) && (count <= (start + limit)) ) {

					// jqGrid 用レコード情報準備
					JSONObject n1 = new JSONObject();

					// セル（列）情報リスト
					byte[] bytes = itr.next();
					String[] columnsList = StringUtils.splitPreserveAllTokens( new String(Snappy.uncompress(bytes, 0, bytes.length), "UTF-8"), "\t");

					index=0;
					for(String col : columnsList){
						index++;

						if ( state == index ) {
							// easyui.treegrid state : closed
							states = col;
							n1.put("state",states);
							if (!"".equals(states) && states != null) {
								n1.put("iconCls","icon-ok");
							}
							//itrCols.next();	// 次のセルへ移動
						} else {
							// セル（列）生成
							n1.put("F"+String.valueOf(index),col);
						}

					}

					// 行情報へセル情報を追加
					lineData.add(n1);

				} else if (count == 0) {

					// タイトル
					// セル（列）情報リスト
					byte[] bytes = itr.next();
					String[] columnsList = StringUtils.splitPreserveAllTokens(new String(Snappy.uncompress(bytes, 0, bytes.length), "UTF-8"), "\t");

					for(String col : columnsList){
						state++;
						if ("STATE".equals(col)){
							stateFlag = true;
							break;
						}
					}
					// state カラムがない場合は、初期化
					if (!stateFlag) state=0;

				} else {

					// 次レコード移動
					itr.next();
				}

			}

			if (DefineReport.ID_DEBUG_MODE) System.out.println("size : " + lineData.size());

		} else {
			// 事前検索結果がsessionに保持されていません。
			System.out.println("table属性がsessionに保持されていません。");
		}

		// レコード情報の格納（JSON形式変換用）
		json.setRows(lineData);

		// JQEasyJSONでレコード情報格納の場合
		// オプション情報設定
		JSONObject option = (JSONObject) session.getAttribute(DefineReport.ID_SESSION_OPTION);
		if (option != null) {
			if(option.containsKey(DefineReport.ID_PARAM_OPT_TITLE) &&json.getTitles()==null){
				JSONArray titArray = option.getJSONArray(DefineReport.ID_PARAM_OPT_TITLE);
				json.setTitles((String[])titArray.toArray(new String[titArray.size()]));
			}
			json.setOpts(option);
		}

		// JSON 形式へ変換
		jsonOB = JSONObject.fromObject(JSONSerializer.toJSON(json));
		if (DefineReport.ID_DEBUG_MODE) System.out.println(jsonOB.toString());

		// JSON データのロード
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter pw = response.getWriter();
		pw.print(jsonOB);

	}

	/**
	 * 分析
	 * @param ItemInterface
	 * @param request
	 * @param map
	 * @param limit
	 * @param session
	 * @param start
	 * @param con
	 */
	private void convertItem(ItemInterface shopItem, HttpServletRequest request,
			HashMap<String, String> map, int limit, HttpSession session, int start) {

		try {
			// セッション情報取得
			User userInfo = (User)request.getSession().getAttribute(Consts.STR_SES_LOGINUSER);

			// ログインユーザー情報セット
			shopItem.setUserInfo(userInfo);
			// 条件セット
			shopItem.setMap(map);
			// 検索条件（Excel出力用）
			shopItem.setJson((String) session.getAttribute(DefineReport.ID_SESSION_STORAGE));
			// 検索開始位置取得
			shopItem.setStart(start);
			// 検索取得数
			shopItem.setLimit(limit);
			// SQL 実行
			shopItem.selectBy();

			// セッションの保存先
			String sessionTable = DefineReport.ID_SESSION_TABLE;
			// レポート情報
			String report = request.getParameter("report");
			if (report != null && DefineReport.ID_PAGE_006.equals(report) && map.get("KBN") != null && !map.get("KBN").isEmpty()) {
				sessionTable = DefineReport.ID_SESSION_TABLE+"2";
			}

			// セッション保持
			session.setAttribute(sessionTable, shopItem.getTable());
			session.setAttribute(DefineReport.ID_SESSION_WHERE, shopItem.getWhere());
			session.setAttribute(DefineReport.ID_SESSION_META,  shopItem.getMeta());
			session.setAttribute(DefineReport.ID_SESSION_OPTION,  shopItem.getOption());
			session.setAttribute(DefineReport.ID_SESSION_MSG,  shopItem.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 実績なしの店舗・分類列を除外<br>
	 * OptionのTitleは全ての列タイトルが設定されている前提
	 * @param session
	 * @param json
	 * @param al
	 * @param startIdx 開始列インデックス
	 * @param loopCnt  １店舗・１分類あたりの列数
	 * @param rearCols 後方の列数
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private ArrayList<byte[]> excludeColumns(HttpSession session, JQEasyModel json,
			ArrayList<byte[]> al, int startIdx, int loopCnt, int rearCols) throws IOException {
		return excludeColumns(session, json, al, startIdx, loopCnt, rearCols, null);
	}

	/**
	 * 実績なしの店舗・分類列を除外<br>
	 * OptionのTitleは全ての列タイトルが設定されている前提
	 * @param session
	 * @param json
	 * @param al
	 * @param startIdx 開始列インデックス
	 * @param loopCnt  １店舗・１分類あたりの列数
	 * @param rearCols 後方の列数
	 * @param notDeKeys 除外しない列のキー（タイトル後方一致）
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private ArrayList<byte[]> excludeColumns(HttpSession session, JQEasyModel json,
			ArrayList<byte[]> al, int startIdx, int loopCnt, int rearCols, String[] notDeKeys) throws IOException {

		String[] titles = StringUtils.splitPreserveAllTokens(new String(Snappy.uncompress(al.get(0), 0, al.get(0).length), "UTF-8"), "\t");
		// 終了列インデックス
		int endIdx = titles.length - rearCols;

		// オプション内のタイトル情報
		JSONObject option = (JSONObject) session.getAttribute(DefineReport.ID_SESSION_OPTION);

		//除外しない列のインデックスを格納
		ArrayList<Integer> notDelIndex = new ArrayList<Integer>();

		int index = -1;
		for (byte[] rows : al) {
			index++;

			// タイトル行は処理を飛ばす
			if (index == 0) continue;

			String[] columnsList = StringUtils.splitPreserveAllTokens(new String(Snappy.uncompress(rows, 0, rows.length), "UTF-8"), "\t");
			for (int i = startIdx; i < endIdx; i++) {
				// 既に除外しない列と判断されている場合は処理を飛ばす
				if (notDelIndex.contains(i)) continue;

				try {
					// タイトルから除外対象外か判断（2016.09.27 小計行はデータがなくても表示するケースに対応して追加）
					if (option != null&&option.containsKey(DefineReport.ID_PARAM_OPT_TITLE)&&ArrayUtils.isNotEmpty(notDeKeys)){
						JSONArray titArray = option.getJSONArray(DefineReport.ID_PARAM_OPT_TITLE);
						if(StringUtils.endsWithAny(titArray.optString(i), notDeKeys) ){
							// 除外対象外
							throw new Exception();
						}
					}

					if (StringUtils.isNotEmpty(columnsList[i]) && Double.parseDouble(columnsList[i]) != 0) {
						// 実績あり
						throw new Exception();
					}
				} catch (Exception e) {
					// 実績あり（値が0でない or 文字列が入っている）
					int grpNo = (i - startIdx) / loopCnt;		// 小数点以下切捨て
					int grpTop = grpNo * loopCnt + startIdx;	// グループ先頭の列インデックス
					for (int j = 0; j < loopCnt; j++) {
						notDelIndex.add(grpTop + j);
					}
				}
			}
		}

		// 除外後のデータ格納
		ArrayList<byte[]> newData = new ArrayList<byte[]>();

		StringBuffer sb = new StringBuffer();
		Iterator<byte[]> itr = al.iterator();
		while (itr.hasNext()) {
			ArrayList<String> cols = new ArrayList<String>();

			// セル（列）情報リスト
			byte[] bytes = itr.next();
			String[] columnsList = StringUtils.splitPreserveAllTokens(new String(Snappy.uncompress(bytes, 0, bytes.length), "UTF-8"), "\t");
			index = -1;

			for(String col : columnsList){
				index++;

				if ( index < startIdx || index >= endIdx || notDelIndex.contains(index)) {
					cols.add(col);

				}
			}

			newData.add(Snappy.compress(StringUtils.join(cols.toArray(new String[cols.size()]),"\t").getBytes("UTF-8")));
		}

		// 除外前のメタデータ取得
		ArrayList<Integer> oldMetaData = (ArrayList<Integer>) session.getAttribute(DefineReport.ID_SESSION_META);

		// 除外後のメタデータ格納
		ArrayList<Integer> newMetaData = new ArrayList<Integer>();

		Iterator<Integer> itrMeta = oldMetaData.iterator();
		index = -1;
		while (itrMeta.hasNext()) {
			index++;

			if ( index < startIdx || index >= endIdx || notDelIndex.contains(index)) {
				newMetaData.add(itrMeta.next());

			} else {
				itrMeta.next();
			}
		}

		if (option != null&&option.containsKey(DefineReport.ID_PARAM_OPT_TITLE)) {
			// 除外後のメタデータ格納
			JSONObject newOption= new JSONObject();

			Iterator<String> oit = option.keySet().iterator();
			while (oit.hasNext()) {
				String key = oit.next();
				if(StringUtils.equals(key, DefineReport.ID_PARAM_OPT_TITLE)){
					ArrayList<String> titleList = new ArrayList<String>();
					JSONArray titArray = option.getJSONArray(DefineReport.ID_PARAM_OPT_TITLE);
					for (index = 0; index < endIdx; index++) {
						// 既に除外しない列と判断されている場合は処理を飛ばす
						if ( index < startIdx || index >= endIdx || notDelIndex.contains(index)) {
							titleList.add(titArray.getString(index));
						}
					}
					newOption.put(key, titleList.toArray(new String[titleList.size()]));

				}else{
					newOption.put(key, option.get(key));
				}
			}
			session.setAttribute(DefineReport.ID_SESSION_OPTION,  newOption);
		}

		// セッション保持
		session.setAttribute(DefineReport.ID_SESSION_TABLE, newData);
		session.setAttribute(DefineReport.ID_SESSION_META,  newMetaData);

		return newData;
	}


	@SuppressWarnings("unchecked")
	private void setErrData(HttpServletRequest request, HttpSession session,
			HashMap<String, String> map, String sesPrefix) {
		String[] idxs		= StringUtils.split(map.get("IDX"),",");	// 更新情報Index
		JSONArray dataArray = JSONArray.fromObject(map.get("DATA"));	// 更新情報

		// ｾｯｼｮﾝ内情報
		ArrayList<byte[]> al = (ArrayList<byte[]>) session.getAttribute(DefineReport.ID_SESSION_TABLE + sesPrefix);
		for (int i = 0; i < idxs.length; i++) {
			// セル（列）情報リスト
			JSONObject data = dataArray.getJSONObject(i);
			Object[] values = data.values().toArray();
			// title行がセットされているため、ｾｯｼｮﾝ内データにセットする際は＋１
			try {
				byte[] bytes = Snappy.compress(StringUtils.join(values,"\t").getBytes("UTF-8"));
				al.set(NumberUtils.toInt(idxs[i])+1, bytes);
			} catch (Exception e) {
				System.out.println(idxs[i]+"件目の変換中にエラーが発生しました。");
				e.printStackTrace();

			}
		}
		session.setAttribute(DefineReport.ID_SESSION_TABLE + sesPrefix, al);
	}

}
