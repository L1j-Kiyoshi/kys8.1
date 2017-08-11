package l1j.server.server.command.executor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.ServerBasePacket;

/**
 * 侵入不可能な状態になっているタイルを可視化するクラス
 * @author ROOT
 *
 */
public class L1PassableView implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1PassableView.class.getName());
	private static Map<Integer, Boolean> _usingMap = new HashMap<Integer, Boolean>();
	private L1PassableView() {}

	public static L1CommandExecutor getInstance() {
		return new L1PassableView();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		if (arg.equalsIgnoreCase("on")) {
			if(_usingMap.containsKey(pc.getId())){
				return;
			}
			_usingMap.put(pc.getId(), true);
			GeneralThreadPool.getInstance().schedule(new L1PvTimer(pc), 0);
		} else if (arg.equalsIgnoreCase("off")) {
			_usingMap.remove(pc.getId());
		}
	}

	private class L1PvTimer implements Runnable {
		private L1PcInstance _pc;
		private int _time = 300; // 更新間隔 ミリ秒

		public L1PvTimer(L1PcInstance pc) {
			_pc = pc;
		}

		@Override
		public void run() {
			boolean exitFlag = false;
			if(!_usingMap.containsKey(_pc.getId())){
				exitFlag = true;
			}
			if(_pc.getNetConnection() != null && !exitFlag){
				GeneralThreadPool.getInstance().schedule(this, _time);
			}
			// 相対座標からそれぞれの頂点座標を算出
			L1Location startLoc = new L1Location(_pc.getX() -20, _pc.getY() -20, _pc.getMapId());

			// 左から右へ走査を開始、前回の情報と異なる場合は記録を更新しクライアントにパケットを送信
			L1Location tempLoc;
			boolean passable;
			int key;
			for(int i=1; i<41; i++){
				tempLoc = new L1Location(startLoc.getX(), startLoc.getY(), startLoc.getMapId());
				for(int j=1; j<41; j++){
					key = i*100+j;
					passable = _pc.getMap().isPassable(tempLoc.getX(), tempLoc.getY());

					if(passable || exitFlag){// 終了フラグが立っている場合は強制的に前回の記録を削除
						_pc.sendPackets(new S_Remove(key));
					}else{
						int gfxid= 2474; // カボチャマスク
						String name = ""; // String.format("%s(%s)", arg1, arg2);
						_pc.sendPackets(new S_OriginalPassable(tempLoc.getX(), tempLoc.getY(), key, gfxid, name));
					}
					tempLoc.setX(tempLoc.getX()+1);
				}
				startLoc.setY(tempLoc.getY()+1);
			}
		}
	}


	private class S_Remove extends ServerBasePacket {
		S_Remove(int objId) {
			writeC(Opcodes.S_REMOVE_OBJECT);
			writeD(objId);
		}

		@Override
		public byte[] getContent() {
			return super.getBytes();
		}
	}
	/**
	 * デバッグ用 通行不可能タイルを目視するためのクラス
	 * @author ROOT
	 *
	 */
	private class S_OriginalPassable extends ServerBasePacket {
		S_OriginalPassable(int locX, int locY, int fakeId, int gfxId, String name) {
			writeC(Opcodes.S_PUT_OBJECT);
			writeH(locX);
			writeH(locY);
			writeD(fakeId);
			writeH(gfxId);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			writeD(0);
			writeC(0);
			writeC(0);
			writeS(name);
			writeC(0);
			writeD(0);
			writeD(0);
			writeC(0xFF);
			writeC(0);
			writeC(0);
			writeH(0xFFFF);
			writeC(0);
			writeC(0x08);
			writeC(0x12);
			writeC(0x00);
		}

		@Override
		public byte[] getContent() throws IOException {
			return super.getBytes();
		}
	}
}
