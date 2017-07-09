package l1j.server.server.model;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.RepeatTask;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SupportInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Strup;

public class HelpBySupport extends RepeatTask {
	private static Logger _log = Logger.getLogger(HelpBySupport.class
			.getName());
	private static Random _random = new Random(System.nanoTime());
	
	private final L1PcInstance _pc;
	
	private L1SupportInstance _support;

	public HelpBySupport(L1PcInstance pc, long interval) {
		super(interval);
		_pc = pc;
		  Object[] supportList = _pc.getSupportList().values().toArray();
		  L1SupportInstance support = null;
		  for (Object supportObject : supportList) {
			  support = (L1SupportInstance) supportObject;
				_support = support;
		  }
	}

	@Override
	public void execute() {
		try {
			if (_pc.isDead()) {
				return;
			}
			if (_support.hasSkillEffect(L1SkillId.HASTE)) {
			} else if (_support.hasSkillEffect(L1SkillId.GREATER_HASTE)) {
			} else if (_support.hasSkillEffect(L1SkillId.STATUS_HASTE)) {
			}else{
				SupportHaste();
			}
			if (_pc.hasSkillEffect(L1SkillId.HASTE)) {
			} else if (_pc.hasSkillEffect(L1SkillId.GREATER_HASTE)) {
			} else if (_pc.hasSkillEffect(L1SkillId.STATUS_HASTE)) {
			}else{
				Haste();
			}
			
			if (_support.hasSkillEffect(L1SkillId.HOLY_WALK)) {
			}else{
				Brave();
			}
			
			if(_pc.hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_STR)){
			}else{
				Strup();
			}
			
			if(_pc.hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_DEX)){
			}else{
				Dexup();
			}
			
			if(_pc.hasSkillEffect(L1SkillId.ADVANCE_SPIRIT)){
			}else{
				Advanceup();
			}
			if(_pc.hasSkillEffect(L1SkillId.STATUS_POISON)
					||_pc.hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZING) 
					||_pc.hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)){
				cure();
			}
			heal();
			heal3();
					
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void Haste() {
		if(_support.getCurrentMp() > 40){
			_pc.sendPackets(new S_SkillHaste(_pc.getId(), 1,
					1200));
			_pc.broadcastPacket(new S_SkillHaste(_pc.getId(), 1,
					1200));
		_pc.setSkillEffect(L1SkillId.HASTE, 1200 * 1000);
		_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 755)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 755));
		_pc.setMoveSpeed(1);
		_support.setCurrentMp(_support.getCurrentMp() - 40);
		}else{
			npctalk(0);
		}
	}
	
	public void SupportHaste() {
		if(_support.getCurrentMp() > 40){
			_support.setCurrentMp(_support.getCurrentMp() - 40);
			_support.broadcastPacket(new S_SkillHaste(_support.getId(), 1,
					1200));
			_support.setSkillEffect(L1SkillId.HASTE, 1200 * 1000);
			_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
			_support.broadcastPacket(new S_SkillSound(_support.getId(), 755));
			_support.setMoveSpeed(1);
			_support.setCurrentMp(_support.getCurrentMp() - 40);
		}else{
			npctalk(0);
		}
	}
	
	public void Brave() {
		if(_support.getCurrentMp() > 15){
			_support.setBraveSpeed(4);
			_support.broadcastPacket(new S_SkillBrave(_support.getId(), 4,
					32));
			_support.setCurrentMp(_support.getCurrentMp() - 15);
			_support.setSkillEffect(L1SkillId.HOLY_WALK, 32 * 1000);
			_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
			_support.broadcastPacket(new S_SkillSound(_support.getId(), 3936));
		}else{
			npctalk(0);
		}
	}
	
	public void Strup() {
		if(_support.getCurrentMp() > 50){
			if(_pc.hasSkillEffect(L1SkillId.DRESS_MIGHTY)){
				_pc.removeSkillEffect(L1SkillId.DRESS_MIGHTY);
			}
			if(_pc.hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_STR)){
				_pc.removeSkillEffect(L1SkillId.PHYSICAL_ENCHANT_STR);
			}
		_pc.getAbility().addAddedStr((byte) 5);
		_pc.sendPackets(new S_Strup(_pc, 5, 1200));
		_pc.setSkillEffect(L1SkillId.PHYSICAL_ENCHANT_STR, 1200 * 1000);
		_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 751)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 751));
		_support.setCurrentMp(_support.getCurrentMp() - 50);
		}else{
			npctalk(0);
		}
	}
	
	public void Dexup() {
		if(_support.getCurrentMp() > 50){
			if(_pc.hasSkillEffect(L1SkillId.DRESS_DEXTERITY)){
				_pc.removeSkillEffect(L1SkillId.DRESS_DEXTERITY);
			}
			if(_pc.hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_DEX)){
				_pc.removeSkillEffect(L1SkillId.PHYSICAL_ENCHANT_DEX);
			}
		_pc.getAbility().addAddedDex((byte) 5);
		_pc.sendPackets(new S_Dexup(_pc, 5, 1200));
		_pc.setSkillEffect(L1SkillId.PHYSICAL_ENCHANT_DEX, 1200 * 1000);
		_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 750)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 750));
		_support.setCurrentMp(_support.getCurrentMp() - 50);
		}else{
			npctalk(0);
		}
	}
	
	public void heal() {
		if(_support.getCurrentMp() > 20){
		if(_pc.getCurrentHp() < _pc.getMaxHp()*60 / 100){
			_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 830)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 830));
		_pc.healHp(150);
		_support.setCurrentMp(_support.getCurrentMp() - 20);
		}
	}else{
			npctalk(0);
		}
	}
	
	public void heal2() {
		if(_support.getCurrentMp() > 20){
			_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 830)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 830));
		_pc.healHp(150);
		_support.setCurrentMp(_support.getCurrentMp() - 20);
	}else{
			npctalk(0);
		}
	}
	
	public void heal3() {
		if(_support.getCurrentMp() > 20){
			if(_support.getCurrentHp() < _support.getMaxHp()*60 / 100){
			_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
			_support.broadcastPacket(new S_SkillSound(_support.getId(), 830));
		_support.healHp(150);
		_support.setCurrentMp(_support.getCurrentMp() - 20);
			}
	}else{
			npctalk(0);
		}
	}
	
	public void cure() {
		if(_support.getCurrentMp() > 15){
			_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 871)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 871));
		_pc.curePoison();
		_support.setCurrentMp(_support.getCurrentMp() - 15);
	}else{
			npctalk(0);
		}
	}
	
	public void Advanceup() {
		if(_support.getCurrentMp() > 20){
			if(_pc.hasSkillEffect(L1SkillId.ADVANCE_SPIRIT)){
				_pc.removeSkillEffect(L1SkillId.ADVANCE_SPIRIT);
			}
		_pc.setSkillEffect(L1SkillId.ADVANCE_SPIRIT, 1200 * 1000);
		_pc.setAdvenHp(_pc.getBaseMaxHp() / 5);
		_pc.setAdvenMp(_pc.getBaseMaxMp() / 5);
		_pc.addMaxHp(_pc.getAdvenHp());
		_pc.addMaxMp(_pc.getAdvenMp());
		_pc.sendPackets(new S_HPUpdate(_pc.getCurrentHp(), _pc
				.getMaxHp()));
		if (_pc.isInParty()) { 
			_pc.getParty().updateMiniHP(_pc);
		}
		_pc.sendPackets(new S_MPUpdate(_pc.getCurrentMp(), _pc
				.getMaxMp()));
		_support.setCurrentMp(_support.getCurrentMp() - 20);
		_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 3935)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3935));
		}else{
			npctalk(0);
		}
	}
	
	public void Berserkers() {
		if(_support.getCurrentMp() > 40){
			if(_pc.hasSkillEffect(L1SkillId.BERSERKERS)){
				_pc.removeSkillEffect(L1SkillId.BERSERKERS);
			}
		_pc.setSkillEffect(L1SkillId.BERSERKERS, 320 * 1000);
		_pc.getAC().addAc(10);
		_pc.addDmgup(5);
		_pc.addHitup(2);
		_pc.sendPackets(new S_OwnCharAttrDef(_pc));
		_support.setCurrentMp(_support.getCurrentMp() - 20);
		_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 3943)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3943));
		}else{
			npctalk(0);
		}
	}
	
	public void Immune() {
		if(_pc.getInventory().checkItem(40318, 2)){
		if(_support.getCurrentMp() > 30){
			_pc.getInventory().consumeItem(40318, 2);
			_pc.setSkillEffect(L1SkillId.IMMUNE_TO_HARM, 32 * 1000);
			_pc.sendPackets(new S_SkillIconGFX(40, 32));
			_support.broadcastPacket(new S_DoActionGFX(_support.getId(), 19));
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 228)); 
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 228));
		_support.setCurrentMp(_support.getCurrentMp() - 30);
		}else{
			npctalk(0);
		}
		}else{
			npctalk(2);
		}
	}
	
	public void npctalk(int i) {
		int rnd = _random.nextInt(100)+1;
		String chat = null;
		if(rnd > 99){
		if(i == 0){
    	chat = "よエムOリングである、ゆっくり狩り笑";
    	_support.broadcastPacket(new S_NpcChatPacket(_support, chat, 0));
		}else if(i == 1){
	    	chat = "エム半残っゆっくり狩りㅜ";
	    	_support.broadcastPacket(new S_NpcChatPacket(_support, chat, 0));
		}
		}
		if(i == 2){
	    	 chat = "マドルなくモトジュォ";
	    	_support.broadcastPacket(new S_NpcChatPacket(_support, chat, 0));
		}
		if(i == 3){
	    	 chat = "エム"+ _support.getCurrentMp() +"残ったゆっくりして";
	    	_support.broadcastPacket(new S_NpcChatPacket(_support, chat, 0));
		}
	}
	
	public void npctalk2(String chatText) {
		String chat = chatText;
	    	_support.broadcastPacket(new S_NpcChatPacket(_support, chat, 0));
	}
	
	public void npctalk3(String chatText) {
		if (chatText.startsWith("ヒルジュォ")) {
			heal2();
			return;
		}else if(chatText.startsWith("バーソジュォ")){
			Berserkers();
			return;
		}else if(chatText.startsWith("STRアップくれ")){
			Strup();
			return;
		}else if(chatText.startsWith("デッキオプジュォ")){
			Dexup();
			return;
		}else if(chatText.startsWith("オベンくれ")){
			Advanceup();
			return;
		}else if(chatText.startsWith("ミュンジュォ") && _pc.isGm()){
			Immune();
			return;
		}else if(chatText.startsWith("エムミョト")){
			npctalk(3);
			return;
		}else if(chatText.startsWith("欺く ") && _pc.isGm()){
			chatText = chatText.replace("欺く","");
			npctalk2(chatText);
			return;
		}else if(chatText.startsWith("攻撃して") && _pc.isGm()){
			chatText = chatText.replace("攻撃して","");
			hate(chatText);
			return;
		}else if(chatText.startsWith("停止") && _pc.isGm()){
			cleartarget();
			return;
		}
	}
	
	public void hate(String chatText) {
		String chat = chatText;
		L1Character target = (L1Character) L1World.getInstance().findObject(chat);
		if (_support != null && target != null) {
			if (target instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) target;
				if (pc.checkNonPvP(pc, _support)) {
					return;
				}
			}
			_support.setMasterTarget(target);
		}
	}
	
	public void cleartarget(){
		_support.allTargetClear();
	}
}
