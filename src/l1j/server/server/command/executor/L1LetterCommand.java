/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_LetterList;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;



public class L1LetterCommand implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1LetterCommand.class.getName());

	private L1LetterCommand() {}

	public static L1CommandExecutor getInstance() {
		return new L1LetterCommand();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try{
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			int letter_id = Integer.parseInt(st.nextToken());

			if(name != null){
				WritePrivateMail(pc, name, letter_id);
			}
		}catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "[キャラクター名] [番号]を入力してください。"));
		}
	}
	/** 個人メールを自動的に送信する **/
	private void WritePrivateMail(L1PcInstance sender, String receiverName, int letter_id) {

		Connection con = null;
		PreparedStatement pstm = null;  
		ResultSet rs = null;

		try{
			//SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd", Locale.KOREA);
			Timestamp dTime = new Timestamp(System.currentTimeMillis());

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT subject, content FROM letter_command WHERE id = ?");
			pstm.setInt(1, letter_id);
			rs = pstm.executeQuery();
			if (!rs.next()) {
				sender.sendPackets(new S_SystemMessage("そんな番号が付いている内容がありません。"));
				return;
			}
			String subject = rs.getString("subject");
			String content = rs.getString("content");

			pstm.close();
			rs.close();
			
			if(subject == null || content == null){
				sender.sendPackets(new S_SystemMessage("番号のタイトルや内容が登録されていません。"));
				return;
			}
			
			L1PcInstance target = L1World.getInstance().getPlayer(receiverName);
			if (target == null) {
				target = CharacterTable.getInstance().restoreCharacter(receiverName);
			}
			LetterTable.getInstance().writeLetter(949, dTime, sender.getName(), receiverName, 0, subject, content);
			sendMessageToReceiver(target, sender, 0, 50);

			
//			if (target != null){
//				sender.sendPackets(new S_SystemMessage(receiverName + "様の手紙を送りました。 "））;
//				return;
//			} else if(target == null){
//				sender.sendPackets(new S_SystemMessage(receiverName + " 님은 존재하지 않는 캐릭입니다."));
//			}
			if (target == null){
				sender.sendPackets(new S_SystemMessage(receiverName + "様は存在しないキャラクターです。"));
				return;
			}
			sender.sendPackets(new S_SystemMessage(receiverName + "様の手紙を送った。"));
			
			
		}catch (Exception e){
			sender.sendPackets(new S_SystemMessage("。返信エラー"));
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void LetterList(L1PcInstance pc, int type, int count) {
		pc.sendPackets(new S_LetterList(pc, type, count));
	}

	private void sendMessageToReceiver(L1PcInstance receiver, L1PcInstance sender, final int type, final int MAILBOX_SIZE) {
		if (receiver != null && receiver.getOnlineStatus() != 0) {
			LetterList(receiver, type, MAILBOX_SIZE);
			receiver.sendPackets(new S_SkillSound(receiver.getId(), 1091));
			receiver.sendPackets(new S_ServerMessage(428)); // メールが届きました。
			sender.sendPackets(new S_LetterList(sender, type, MAILBOX_SIZE));
			return;
		}
	}

}
