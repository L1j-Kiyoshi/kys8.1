package l1j.server.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.Controller.BraveavatarController;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_Party;
import l1j.server.server.serverpackets.S_ServerMessage;

public class L1Party {

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(L1Party.class.getName());

	private final List<L1PcInstance> _membersList = new ArrayList<L1PcInstance>();

	private L1PcInstance _leader = null;

	public void addMember(L1PcInstance pc) {
		if (pc == null) {
			throw new NullPointerException();
		}
		if (_membersList.size() == Config.MAX_PT && !_leader.isGm() || _membersList.contains(pc)) {
			pc.sendPackets(new S_ServerMessage(417));
			return;
		}

		if (_membersList.isEmpty()) {
			setLeader(pc);
		} else {
			createMiniHp(pc);
		}

		if (pc.isCrown()) {
			if (SkillsTable.getInstance().spellCheck(pc.getId(), 121)) {
				BraveavatarController.getInstance().addMember(pc);
			}
		}

		_membersList.add(pc);
		pc.setParty(this);
		showAddPartyInfo(pc);

	}

	private void removeMember(L1PcInstance pc) {
		if (!_membersList.contains(pc)) {
			return;
		}

		_membersList.remove(pc);
		// リーダーが脱退する場合、リストから削除
		if (pc.isCrown()) {
			if (isLeader(pc)) {
				BraveavatarController.getInstance().removeMember(pc);
			}
		}
		pc.setParty(null);
		if (!_membersList.isEmpty()) {
			deleteMiniHp(pc);
		}

		// パーティーから除外された時、ブレイブアバター効果を除去する。
		if (pc.getPbavatar()) {
			BraveavatarController.getInstance().brave_end(pc);
		}

		//
		pc.sendPackets(new S_Party(0x68, pc));
	}

	public boolean isVacancy() {
		return _membersList.size() < Config.MAX_PT;
	}

	public int getVacancy() {
		return Config.MAX_PT - _membersList.size();
	}

	public boolean isMember(L1PcInstance pc) {
		return _membersList.contains(pc);
	}

	private void setLeader(L1PcInstance pc) {
		_leader = pc;
	}

	public L1PcInstance getLeader() {
		return _leader;
	}

	public boolean isLeader(L1PcInstance pc) {
		return pc.getId() == _leader.getId();
	}

	public boolean isAutoDivision(L1PcInstance pc) {
		return pc.getPartyType() == 1 || pc.getPartyType() == 4;
	}

	public String getMembersNameList() {
		String _result = new String("");
		for (L1PcInstance pc : _membersList) {
			_result = _result + pc.getName() + " ";
		}
		return _result;
	}

	public void refresh(L1PcInstance pc) {// パーティー追加
		for (L1PcInstance member : getMembers()) {
			if (pc.getId() == member.getId()) {
				continue;
			}
			if (member.getParty().getLeader() == pc) {
				// NameColor
				member.sendPackets(new S_Party(0x6c2, pc));
			} else {
				// NameColor
				member.sendPackets(new S_Party(0x6c1, pc));
			}
		}
	}

	public void memberDie(L1PcInstance pc) {// パーティーメンバージュクオトとき
		for (L1PcInstance member : getMembers()) {
			if (pc.getId() == member.getId()) {
				continue;
			}
			// NameColor
			member.sendPackets(new S_Party(0x6c0, pc));
		}
	}

	private void showAddPartyInfo(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			if (pc.getId() == getLeader().getId() && getNumOfMembers() == 1) {
				continue;
			}
			//
			if (pc.getId() == member.getId()) {
				// 自分にとっては68（newMember）
				pc.sendPackets(new S_Party(0x68, pc));
			} else {
				// 周辺のパーティーメンバーたちには69（oldMember）
				member.sendPackets(new S_Party(0x69, pc));
			}
			// refreshParty
			member.sendPackets(new S_Party(0x6e, member));
			//
			createMiniHp(member);
		}
	}

	private void createMiniHp(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			if (member == null)
				continue;
			member.sendPackets(new S_HPMeter(pc));
			pc.sendPackets(new S_HPMeter(member));
		}
	}

	private void deleteMiniHp(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			if (member == null)
				continue;
			pc.sendPackets(new S_HPMeter(member.getId(), 0xff, 0xff));
			member.sendPackets(new S_HPMeter(pc.getId(), 0xff, 0xff));
		}
	}

	public void updateMiniHP(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			if (member == null)
				continue;
			member.sendPackets(new S_HPMeter(pc));
		}
	}

	private void breakup() {

		for (L1PcInstance member : getMembers()) {
			removeMember(member);
			member.sendPackets(new S_ServerMessage(418));
		}

		//
		// _leader.sendPackets(new S_Test2());
		// _leader.sendPackets(new S_Test3());
	}

	public void passLeader(L1PcInstance pc) { // リーダー委任
		for (L1PcInstance member : getMembers()) {
			member.getParty().setLeader(pc);
			member.sendPackets(new S_Party(0x6A, pc));
		}
	}

	public void leaveMember(L1PcInstance pc) {
		if (isLeader(pc) || getNumOfMembers() == 2) {
			breakup();
		} else {
			removeMember(pc);
			for (L1PcInstance member : getMembers()) {
				sendLeftMessage(member, pc);
			}
			sendLeftMessage(pc, pc);
		}
	}

	public void kickMember(L1PcInstance pc) { // リーダー追放
		if (getNumOfMembers() == 2) {
			breakup();
		} else {
			removeMember(pc);
			for (L1PcInstance member : getMembers()) {
				sendLeftMessage(member, pc);
			}
			sendKickMessage(pc);
		}
		pc.sendPackets(new S_ServerMessage(419)); // パーティーから追放されました。
	}

	public L1PcInstance[] getMembers() {
		return _membersList.toArray(new L1PcInstance[_membersList.size()]);
	}

	public int getNumOfMembers() {
		return _membersList.size();
	}

	private void sendKickMessage(L1PcInstance kickpc) {
		kickpc.sendPackets(new S_ServerMessage(419));
	}

	private void sendLeftMessage(L1PcInstance sendTo, L1PcInstance left) {
		sendTo.sendPackets(new S_ServerMessage(420, left.getName()));
	}

	public List<L1PcInstance> getList() {// ハーディンパーティーメンバーリスト
		return _membersList;
	}

}
