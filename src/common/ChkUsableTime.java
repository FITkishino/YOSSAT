/*
 * 作成日: 2013/12/18
 *
 */
package common;

import java.util.Date;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import authentication.connection.DBConnection;
import authentication.defines.SQL;

/**
 * システム利用可能時間帯チェッククラス
 */
public class ChkUsableTime {
	private CmnDate dayfrom_;
	private CmnDate dayto_;

	// 許可ユーザー
	private static final String ID_USER_NAME = "system";

	/**
	 * コンストラクタ <br>
	 * web.xmlのシステム利用時間をパラメータとする
	 *
	 * @param fromData
	 * @param toData
	 */
	public ChkUsableTime(String fromData, String toData) {
		String from_ = fromData + "00";
		String to_ = toData + "59";

		dayfrom_ = new CmnDate();
		dayfrom_.setTime(from_);
		dayto_ = new CmnDate();
		dayto_.setTime(to_);
	}

	/**
	 * 利用可能時間外かを返す
	 *
	 * @param userid
	 * @return
	 */
	public boolean isCloseTime(String userid) {
		// if(true) { return false; }	// 無効化

		// 許可ユーザーの場合
		if (ID_USER_NAME.equals(userid)) { return false; }

		// from <= systime <= to はOK
		Date day = new Date();
		if ((day.compareTo(dayfrom_) == -1) || (day.compareTo(dayto_) == 1)) { return true; }
		return false;
	}

	/**
	 * メンテナンス中かを返す
	 *
	 * @param userid
	 * @return
	 */
	public boolean isWaitTime(String userid) {
		// if(true) { return false; }	// 無効化

		// 許可ユーザーの場合
		if (ID_USER_NAME.equals(userid)) { return false; }

		int cnt = 1;
		Map<String, Object> mu = null;
		try {
			String sql = "select count(*) as CNT from " + SQL.system_schema + ".SYS_WAIT";
			QueryRunner qr = new QueryRunner(DBConnection.getDataSource());
			MapHandler rsh = new MapHandler();
			mu = qr.query(sql, rsh);
			cnt = Integer.parseInt(mu.get("CNT").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (cnt > 0);
	}
}
