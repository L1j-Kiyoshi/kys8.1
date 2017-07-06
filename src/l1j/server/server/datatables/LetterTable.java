package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class LetterTable {
	private static Logger _log = Logger.getLogger(LetterTable.class.getName());
	private volatile static LetterTable uniqueInstance = null;

	public LetterTable() {
	}

	public static LetterTable getInstance() {
		if (uniqueInstance == null) {
			synchronized (LetterTable.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new LetterTable();
				}
			}
		}
		return uniqueInstance;
	}

	// 템플릿 ID일람
	// 16:캐릭터가 존재하지 않는다
	// 32:짐이 너무 많다
	// 48:혈맹이 존재하지 않는다
	// 64:※내용이 표시되지 않는다(흰색자)
	// 80:※내용이 표시되지 않는다(흑자)
	// 96:※내용이 표시되지 않는다(흑자)
	// 112:축하합니다. %n당신이 참가된 경매는 최종 가격%0아데나의 가격으로 낙찰되었습니다.
	// 128:당신이 제시된 금액보다 좀 더 비싼 금액을 제시한 (분)편이 나타났기 때문에, 유감스럽지만 입찰에 실패했습니다.
	// 144:당신이 참가한 경매는 성공했습니다만, 현재집을 소유할 수 있는 상태에 없습니다.
	// 160:당신이 소유하고 있던 집이 최종 가격%1아데나로 낙찰되었습니다.
	// 176:당신이 신청 하신 경매는, 경매 기간내에 제시한 금액 이상에서의 지불을 표명하는 것이 나타나지 않았기 (위해)때문에, 결국 삭제되었습니다.
	// 192:당신이 신청 하신 경매는, 경매 기간내에 제시한 금액 이상에서의 지불을 표명하는 것이 나타나지 않았기 (위해)때문에, 결국 삭제되었습니다.
	// 208:당신의 혈맹이 소유하고 있는 집은, 본령주의 영지에 귀속하고 있기 (위해)때문에, 향후 이용하고 싶다면 이 쪽에 세금을 내지 않으면 안됩니다.
	// 224:당신은, 당신의 집에 부과된 세금%0아데나를 아직 납입하고 있지 않습니다.
	// 240:당신은, 결국 당신의 집에 부과된 세금%0를 납입하지 않았기 때문에, 경고대로 당신의 집에 대한 소유권을 박탈합니다.

	public int getLetterCount(String name, int type) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int cnt = 0;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT count(*) as cnt FROM letter WHERE receiver=? AND template_id = ? order by date");
			pstm.setString(1, name);
			pstm.setInt(2, type);
			rs = pstm.executeQuery();
			if (rs.next()) {
				cnt = rs.getInt(1);
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return cnt;
	}

	public int writeLetter(int code, Timestamp dTime, String sender, String receiver, int templateId, String subject, String content) {
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		int itemObjectId = 0;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm1 = con.prepareStatement(" SELECT Max(item_object_id)+1 as cnt FROM letter ORDER BY item_object_id ");
			rs = pstm1.executeQuery();
			if (rs.next()) {
				itemObjectId = rs.getInt("cnt");
			}

			pstm2 = con.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?, isCheck=? ");
			pstm2.setInt(1, itemObjectId);
			pstm2.setInt(2, code);
			pstm2.setString(3, sender);
			pstm2.setString(4, receiver);
			pstm2.setTimestamp(5, dTime);
			pstm2.setInt(6, templateId);
			pstm2.setString(7, subject);
			pstm2.setString(8, content);
			pstm2.setInt(9, 0);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
		return itemObjectId;
	}

	public void writeLetter(int itemObjectId, int code, String sender, String receiver, String date, int templateId, byte[] subject, byte[] content) {
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM letter ORDER BY item_object_id");
			rs = pstm1.executeQuery();
			pstm2 = con.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?");
			pstm2.setInt(1, itemObjectId);
			pstm2.setInt(2, code);
			pstm2.setString(3, sender);
			pstm2.setString(4, receiver);
			pstm2.setString(5, date);
			pstm2.setInt(6, templateId);
			pstm2.setBytes(7, subject);
			pstm2.setBytes(8, content);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public void writeLetter(int itemObjectId, int code, String sender, String receiver, String date, int templateId, String subject, String content) {
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT * FROM letter ORDER BY item_object_id");
			rs = pstm1.executeQuery();
			pstm2 = con.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?");
			pstm2.setInt(1, itemObjectId);
			pstm2.setInt(2, code);
			pstm2.setString(3, sender);
			pstm2.setString(4, receiver);
			pstm2.setString(5, date);
			pstm2.setInt(6, templateId);
			pstm2.setString(7, subject);
			pstm2.setString(8, content);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public void deleteLetter(int itemObjectId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM letter WHERE item_object_id=?");
			pstm.setInt(1, itemObjectId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void SaveLetter(int id, int letterType) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE letter SET template_id = ? WHERE item_object_id=?");
			pstm.setInt(1, letterType);
			pstm.setInt(2, id);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void writeLetter(int code, String dTime, String sender, String receiver, int templateId, String subject, String content) {
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		int itemObjectId = 0;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm1 = con.prepareStatement(" SELECT Max(item_object_id)+1 as cnt FROM letter ORDER BY item_object_id ");
			rs = pstm1.executeQuery();
			if (rs.next()) {
				itemObjectId = rs.getInt("cnt");
			}

			pstm2 = con.prepareStatement("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?, isCheck=? ");
			pstm2.setInt(1, itemObjectId);
			pstm2.setInt(2, code);
			pstm2.setString(3, sender);
			pstm2.setString(4, receiver);
			pstm2.setString(5, dTime);
			pstm2.setInt(6, templateId);
			pstm2.setString(7, subject);
			pstm2.setString(8, content);
			pstm2.setInt(9, 0);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public void CheckLetter(int id) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE letter SET isCheck = 1 WHERE item_object_id=?");
			pstm.setInt(1, id);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
