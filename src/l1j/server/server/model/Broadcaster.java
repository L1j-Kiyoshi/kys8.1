package l1j.server.server.model;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.ServerBasePacket;

public class Broadcaster {
	/**
	 * キャラクターの可視範囲にあるプレーヤーでは、パケットを送信する。
	 * 
	 * @param packet
	 *            送信するパケットを示すServerBasePacketオブジェクト。
	 */
	public static void broadcastPacket(L1Character cha, ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(cha)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * キャラクターの可視範囲にあるプレーヤーでは、パケットを送信する。ただし、ターゲットの画面には送信しない。
	 * 
	 * @param packet
	 *            送信するパケットを示すServerBasePacketオブジェクト。
	 */
	public static void broadcastPacketExceptTargetSight(L1Character cha, ServerBasePacket packet, L1Character target) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayerExceptTargetSight(cha, target)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * キャラクターの50マス以内にいるプレイヤーに、パケットを送信する。
	 * 
	 * @param packet
	 *           送信するパケットを示すServerBasePacketオブジェクト。
	 */
	public static void wideBroadcastPacket(L1Character cha, ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(cha,	50)) {
			pc.sendPackets(packet);
		}
	}
	public static void wideBroadcastPacket(L1Character cha, ServerBasePacket packet, int range) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(cha,	range)) {
			pc.sendPackets(packet);
		}
	}
	
	public static void broadcastPacket(L1Character cha,
			ServerBasePacket packet, boolean clear) {
		try {
			if (cha == null)
				return;
			for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(cha)) {
				pc.sendPackets(packet);
			}
			if (clear) {
				packet.clear();
				packet = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
