package l1j.server.server.model.Instance;

import java.text.SimpleDateFormat;
import java.util.Locale;

import l1j.server.GameSystem.valakas.ValaRaid;
import l1j.server.GameSystem.valakas.ValaRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaid;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Anta.AntarasRaidTimer;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaid;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidSystem;
import l1j.server.IndunSystem.DragonRaid.Fafu.FafurionRaidTimer;
import l1j.server.IndunSystem.DragonRaid.Rind.RindRaid;
import l1j.server.IndunSystem.DragonRaid.Rind.RindSystem;
import l1j.server.server.Controller.CrockController;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1FieldObjectInstance extends L1NpcInstance {
	private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
	private static final long serialVersionUID = 1L;
	private int moveMapId;

	public L1FieldObjectInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {  }

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int npcid = getNpcTemplate().get_npcId();

		switch(npcid){
		/** 時間の割れ目 **/
		case 200:
			if (CrockController.getInstance().isTimeCrock()) {
				if (CrockController.getInstance().crocktype() == 0) {
					new L1Teleport().teleport(pc, 32639, 32876, (short) 780, 4, false);// テーベ
				} else {
					new L1Teleport().teleport(pc, 32794, 32751, (short) 783, 4, false);// ティカル
				}
			} else {
				pc.sendPackets(new S_ChatPacket(pc, "時間の割れ目は現在閉鎖されています。"));
				pc.sendPackets(new S_ChatPacket(pc, "オープン時間は毎日午後7時です。"));
			}
			break;
		/** 時間の割れ目 **/
			
		case 900007: // ドラゴンポータル[アンタラス] =>レイドマップ！ 
			if (pc.hasSkillEffect(L1SkillId.ANTA_BUFF)){
				pc.sendPackets(new S_ServerMessage(1626)); 
				return; 
			} 
			new L1Teleport().teleport(pc, 32600, 32741, (short) moveMapId, pc.getHeading(), true);
//			DragonRaidMap(pc, moveMapId);
			break;
		case 810851: //[アンタラス入口] =>アンタラス待機部屋
			new L1Teleport().teleport(pc, 32671, 32672, pc.getMapId(), pc.getHeading(), true);
			break;
		case 900008: //[アンタラス待機部屋] =>アンタラスレアマップ
			telAntarasLair(pc, moveMapId);
			break;
		case 900036: // ドラゴンポータル[パプリオン] =>レイドマップ！
			if (pc.hasSkillEffect(L1SkillId.FAFU_BUFF)){
				pc.sendPackets(new S_ServerMessage(1626)); 
				return; 
			} 
			new L1Teleport().teleport(pc, 32916, 32672, (short) moveMapId, pc.getHeading(), true);
//			DragonRaidMap(pc, moveMapId);
			break;
		case 900037: //[パプリオン待機部屋] =>パプリオンレアマップ
			telFafurionLair(pc, moveMapId);		
			break;
		case 900219: // リンドレイドポータル
			if (pc.hasSkillEffect(L1SkillId.RIND_BUFF)){
				pc.sendPackets(new S_ServerMessage(1626)); 
				return; 
			} 
			new L1Teleport().teleport(pc, 32734, 32855, (short) moveMapId, pc.getHeading(), true);
			break;
		case 5101: // リンドビオル直系型入口
			new L1Teleport().teleport(pc, 32736, 32847, (short) moveMapId, 5, true);
			break;
		case 5102: // リンドビオルレア入口
			RindRaid Rind = RindSystem.getInstance().getRind(moveMapId);
			if (!Rind.isReady()){
				Rind.setReady(true);
				Rind.Start();
			}
			new L1Teleport().teleport(pc, 32855, 32881, (short) moveMapId, 5, true);
			break;
		case 7210011: //ヴァラカスの避難所
			telValakasRoom(pc); 
			break;
		case 3310017: // ヴァラカスレア入口
			ValaRaid vala = ValaRaidSystem.getInstance().getVala(pc.valakasMapId);
			if (!vala.isReady()){
				vala.setReady(true);
				vala.Start();
			}
			new L1Teleport().teleport(pc, 32761, 32885, (short) pc.valakasMapId, 5, true);
			break;
		case 910008:

			if (pc.hasSkillEffect(L1SkillId.VALA_BUFF)) {
				pc.sendPackets(new S_SystemMessage("ドラゴンレイド魔法によりドラゴンポータルに入場することができません。"));
				pc.sendPackets(
						new S_SystemMessage(
								ss.format(pc.getNetConnection().getAccount().getDragonRaid()) + "以降に入場可能です。"),
						true);
				return;
			} else {
				new L1Teleport().teleport(pc, 32733, 32927, (short) pc.valakasMapId, pc.getHeading(), true);
			}
			break;
		default: 
			break; 
		}
	}

	/** 指定されたマップの32人を超えるかチェックしテルせる@param pc @param mapid 
	 * 	1626: ドラゴンの血痕が全身で漂っています。血痕の臭いが消えるまでドラゴンポータルに入場することができません.*/
	private void DragonRaidMap(L1PcInstance pc, int mapid){
		int count = 0;
		for(L1PcInstance player : L1World.getInstance().getAllPlayers()){
			if(player == null)
				continue;
			if(player.getMapId() == mapid){
				count += 1;
				if(count > 31)
					return;
			}
		}

		switch(getNpcTemplate().get_npcId()) {
		case 900007:
			if (pc.hasSkillEffect(L1SkillId.ANTA_BUFF) || pc.hasSkillEffect(L1SkillId.FAFU_BUFF) || pc.hasSkillEffect(L1SkillId.RIND_BUFF)){
				pc.sendPackets(new S_ServerMessage(1626)); 
				return; 
			} 
//			if(AntarasRaidSystem.getInstance().getAR(mapid).isAntaras()){
//				pc.sendPackets(new S_ServerMessage(1537));//ドラゴンが覚めて進入できない
//				return;
//			} else {
				pc.sendPackets(new S_Message_YN(2923, ""));
				pc.DragonPortalLoc[0] = 32600;
				pc.DragonPortalLoc[1] = 32741;
				pc.DragonPortalLoc[2] = mapid;
//			}
			break;
		case 900036:
			if (pc.hasSkillEffect(L1SkillId.ANTA_BUFF) || pc.hasSkillEffect(L1SkillId.FAFU_BUFF) || pc.hasSkillEffect(L1SkillId.RIND_BUFF)){
				pc.sendPackets(new S_ServerMessage(1626)); 
				return; 
			} 
//			if(FafurionRaidSystem.getInstance().getAR(mapid).isFafurion()){
//				pc.sendPackets(new S_ServerMessage(1537));// ドラゴンが覚めて進入できない
//				return;
//			} else {
				pc.sendPackets(new S_Message_YN(2923, ""));
				pc.DragonPortalLoc[0] = 32976;
				pc.DragonPortalLoc[1] = 32743;
				pc.DragonPortalLoc[2] = mapid;
//			}
			break;
		}
	}

	/** 1536: 人員がいっぱいでこれ以上入場することができません。
	 *  1537: ドラゴンが目を覚まし、今では入場できません。*/
	private void telAntarasLair(L1PcInstance pc, int moveMapId) {
		int count = 0;
		AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(moveMapId);
		count = ar.countLairUser();
		if (count >= 32) {
			pc.sendPackets(new S_ServerMessage(1536));
			return;
		}
//		if (ar.isAntaras() && ar.isLairUser(pc)) {
//			pc.sendPackets(new S_ServerMessage(1537));
//			return;
//		}
		if (count <= 32 && count >= 0)
			ar.addLairUser(pc);

		new L1Teleport().teleport(pc, 32796, 32664, (short) moveMapId, 5, true);

		if(count == 0){ 
			AntarasRaidTimer antastart = new AntarasRaidTimer(ar, 1, 1, 30 * 1000);// 2分チェック（アンタラススポン）
			antastart.begin();
		}
	}

	
	
	private void telFafurionLair(L1PcInstance pc, int moveMapId) {
		int count = 0;
		FafurionRaid ar = FafurionRaidSystem.getInstance().getAR(moveMapId);
		count = ar.countLairUser();
		if (count >= 32) {
			pc.sendPackets(new S_ServerMessage(1536));
			return;
		}
//		if (ar.isFafurion() && ar.isLairUser(pc)) {
//			pc.sendPackets(new S_ServerMessage(1537));
//			return;
//		}
		if (count <= 32 && count >= 0)
			ar.addLairUser(pc);

		new L1Teleport().teleport(pc, 32988, 32842, (short) moveMapId, 5, true);

		if(count == 0){ 
			FafurionRaidTimer fafustart = new FafurionRaidTimer(ar, 1, 3, 30 * 1000);// 2分チェック（パプリオン出現）
			fafustart.begin();
		}
	}
	
	private void telValakasRoom(L1PcInstance pc) {
		new L1Teleport().teleport(pc, 32833, 32757, (short)getMapId(), 5, false); 
		pc.isInValakasBoss = true;
	}
	/** 移動マップを設定する。@param id */
	public void setMoveMapId(int id){ moveMapId = id; }

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		removeAllKnownObjects();
	}
}
