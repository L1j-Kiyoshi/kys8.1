/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.server.server.clientpackets;

import java.io.File;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ClanAttention;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

//Referenced classes of package l1j.server.server.clientpackets:
//ClientBasePacket

/**
 * 본 가문에서 오는 처리 클라이언트 패킷
 */
public class C_ClanAttention extends ClientBasePacket{
	private static final String C_PledgeRecommendation = "[C] C_PledgeRecommendation";

	public C_ClanAttention(byte[] decrypt, GameClient client){
		super(decrypt);
		
		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		
		int data = readC();
		//0 혈맹추가, 1 목록삭제, 2 혈맹목록
		
		//System.out.println("C_ClanAttention - readC타입 : [ " + data + " ]");
		L1Clan targetClan = null;
		L1Clan clan = null;
		switch(data) {
		case 0: // 문장주시 혈맹 추가
			/**
			 * 3348	혈맹 주시: %0 혈맹의 문장 주시를 승낙 하시겠습니까?
			 * 3323	혈맹 주시: %s 혈맹의 문장 주시를 해제 하시겠습니까? (Y/N)
             * 3324	혈맹 주시: 문장 주시 불가, 대상 혈맹과 전쟁 상태
			 */
			
			String pcClanName = pc.getClanname();
			String targetClanName = readS();
			clan = L1World.getInstance().getClan(pcClanName);
			if (clan == null) { // 자크란이 발견되지 않는다
				pc.sendPackets(new S_SystemMessage("\\aG먼저 혈맹을 창설하시길 바랍니다."));
				return;
			}
			
			if (pcClanName.toLowerCase().equals(targetClanName.toLowerCase())) { // 자크란을 지정
				pc.sendPackets(new S_SystemMessage("\\aG자신의 클랜에 주시를 할 수 없습니다."));
				return;
			}
			
			for(int i = 0; i < clan.getGazeList().size(); i++){
				if(clan.getGazeList().get(i).toLowerCase().equals(targetClanName.toLowerCase())){
					pc.sendPackets(new S_SystemMessage("\\aG이미 상대혈맹과 주시를 하고 있습니다."));
					return;
				}
			}
			
			if(clan.getGazeList().size() >= 5){
				pc.sendPackets(new S_SystemMessage("\\aG문장주시는 최대 5개 혈맹에만 가능합니다."));
				return;
			}
			
			
			for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // 크란명을 체크
				if (checkClan.getClanName().toLowerCase().equals(targetClanName.toLowerCase())) {
					targetClan = checkClan;
					break;
				}
			}
			
			if (targetClan == null) { // 상대 크란이 발견되지 않았다
				pc.sendPackets(new S_SystemMessage("\\aG상대혈맹이 존재하지 않습니다."));
				return;
			}
			
			File file = new File(System.getProperty("user.dir") + "/emblem/" + clan.getEmblemId());

			if (!file.exists()) {
				pc.sendPackets(new S_SystemMessage("혈마크 없이는 문장주시를 요청 할 수 없습니다."));
				return;
			}
			
			file = new File(System.getProperty("user.dir") + "/emblem/" + targetClan.getEmblemId());
			if (!file.exists()) {
				pc.sendPackets(new S_SystemMessage("상대혈맹에 혈마크가 없습니다."));
				return;
			}
			
			L1PcInstance target = L1World.getInstance().getPlayer(targetClan.getLeaderName());
			if (target != null) {
				pc.sendPackets(new S_SystemMessage("혈맹 주시: 요청중입니다. 기다려주세요."));
				target.setTempID(pc.getId());
				target.sendPackets(new S_Message_YN(3348, pc.getClanname()));// %0 혈맹의 문장 주시를 승낙 하시겠습니까?
			} else{
				pc.sendPackets(new S_ServerMessage(3349));// 문장 주시 불가, 없는 혈맹 혹은 연합혈맹 이거나 군주가 오프라인 상태
			}
			
			break;
		case 1: //문장주시 삭제
			// 3323	혈맹 주시: %s 혈맹의 문장 주시를 해제 하시겠습니까? (Y/N)
			String targetClanName2 = readS();
			if(!pc.isCrown()){
				pc.sendPackets(new S_SystemMessage("\\aG군주만이 문장주시를 해제할 수 있습니다."));
				return;
			}
			clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan == null) { // 자크란이 발견되지 않는다
				return;
			}
			
			for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // 크란명을 체크
				if (checkClan.getClanName().toLowerCase().equals(targetClanName2.toLowerCase())) {
					targetClan = checkClan;
					break;
				}
			}
			
			if (targetClan == null) { // 상대 크란이 발견되지 않았다
				pc.sendPackets(new S_SystemMessage("\\aG상대혈맹이 존재하지 않습니다."));
				return;
			}
			
			//주시 리스트에서 삭제
			clan.removeGazelist(targetClan.getClanName());
			targetClan.removeGazelist(clan.getClanName());
			
			//문장주시 리스트 업데이트
			for (L1PcInstance member : clan.getOnlineClanMember()) {
				member.sendPackets(new S_ClanAttention(clan.getGazeSize(), clan.getGazeList()));
			}
			
			for (L1PcInstance member : targetClan.getOnlineClanMember()) {
				member.sendPackets(new S_ClanAttention(targetClan.getGazeSize(), targetClan.getGazeList()));
			}
			
			
			break;
		case 2: // 문장주시 혈맹 목록
			
			break;
		}
		
		
	}
	
	@Override
	public String getType() {
		return C_PledgeRecommendation;
	}
}
