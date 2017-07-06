/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.templates.L1Npc;

// Referenced classes of package l1j.server.server.model:
// L1EffectSpawn

public class L1EffectSpawn {

	private static final Logger _log = Logger.getLogger(L1EffectSpawn.class
			.getName());

	private static L1EffectSpawn _instance;

	private Constructor<?> _constructor;

	private L1EffectSpawn() {
	}

	public static L1EffectSpawn getInstance() {
		if (_instance == null) {
			_instance = new L1EffectSpawn();
		}
		return _instance;
	}

	public L1EffectInstance spawnEffect(int npcid, int time, int locX, int locY, short mapId, Object...o) {
		L1Npc template = NpcTable.getInstance().getTemplate(npcid);
		L1EffectInstance effect = null;

		if (template == null) {
			return null;
		}

		String className = (new StringBuilder()).append(
				"l1j.server.server.model.Instance.").append(
				template.getImpl()).append("Instance").toString();

		try {
			_constructor = Class.forName(className).getConstructors()[0];
			Object obj[] = { template };
			effect = (L1EffectInstance) _constructor.newInstance(obj);
			effect.setId(IdFactory.getInstance().nextId());
			effect.setGfxId(template.get_gfxid());
			effect.setX(locX);
			effect.setY(locY);
			effect.setHomeX(locX);
			effect.setHomeY(locY);
			effect.setHeading(0);
			effect.setMap(mapId);
			L1World.getInstance().storeObject(effect);
			L1World.getInstance().addVisibleObject(effect);

			for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(effect)) {
				effect.addKnownObject(pc);
				pc.addKnownObject(effect);
				pc.sendPackets(new S_NPCPack(effect));
				pc.broadcastPacket(new S_NPCPack(effect));
			}
			L1NpcDeleteTimer timer = new L1NpcDeleteTimer(effect, time);
			timer.begin();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		return effect;
	}

	public void doSpawnFireWall(L1Character cha, int targetX, int targetY) {
		L1Npc firewall = NpcTable.getInstance().getTemplate(81157);
		int duration = SkillsTable.getInstance().getTemplate(
				L1SkillId.FIRE_WALL).getBuffDuration();

		if (firewall == null) {
			throw new NullPointerException(
					"FireWall data not found:npcid=81157");
		}

		L1Character base = cha;
		for (int i = 0; i < 8; i++) {
			int a = base.targetDirection(targetX, targetY);
			int x = base.getX();
			int y = base.getY();
			switch(a){
			case 1: x++; y--; break;
			case 2: x++; break;
			case 3: x++; y++; break;
			case 4: y++; break;
			case 5: x--; y++; break;
			case 6: x--; break;
			case 7: x--; y--; break;
			case 0: y--; break;
			default: break;
			}
			if (!base.isAttackPosition(x, y, 1)) {
				x = base.getX();
				y = base.getY();
			}
			L1Map map = L1WorldMap.getInstance().getMap(cha.getMapId());

			if (!map.isArrowPassable(x, y, cha.getHeading())) {
				break;
			}
			L1EffectInstance effect = spawnEffect(81157, duration * 1000, x, y,	cha.getMapId());
			
			if (effect == null) {
				break;
			}
			L1EffectInstance npc = null;
			for (L1Object objects : L1World.getInstance()
					.getVisibleObjects(effect, 0)) {
				if (objects instanceof L1EffectInstance) {
					npc = (L1EffectInstance) objects;
					if (npc.getNpcTemplate().get_npcId() == 81157) {
						npc.deleteMe();
					}
				}
			}
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				effect.setCubePc(pc);
			}
			if (targetX == x && targetY == y) {
				break;
			}
			base = effect;
		}

	}
}
