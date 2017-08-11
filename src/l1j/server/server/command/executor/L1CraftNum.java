package l1j.server.server.command.executor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.utils.IntRange;

/**
 * クラフトリスト調査用のクラス
 *
 * @author ROOT
 *
 */
public class L1CraftNum implements L1CommandExecutor {

    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(L1CraftNum.class.getName());
    public static Map<Integer, L1CraftNumCommandStructure> _craftNum = new HashMap<Integer, L1CraftNumCommandStructure>();

    private L1CraftNum() {

    }

    public static L1CommandExecutor getInstance() {
        return new L1CraftNum();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            int val1 = Integer.parseInt(tok.nextToken());
            int val2 = Integer.parseInt(tok.nextToken());
            if (!IntRange.includes(val2, 1, 1000)) {
                pc.sendPackets(new S_SystemMessage("1-1000の範囲で指定してください"));
                return;
            }
            _craftNum.put(pc.getId(), new L1CraftNumCommandStructure(val1, val2));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + "開始番号 表示件数 と入力してください"));
        }
    }

    public class L1CraftNumCommandStructure {
        int _startNum;
        int _count;

        public L1CraftNumCommandStructure(int startNum, int count) {
            _startNum = startNum;
            _count = count;
        }

        public int getStartNum() {
            return _startNum;
        }

        public int getCount() {
            return _count;
        }

        public void setStartNum(int val) {
            _startNum = val;
        }

        public void setCount(int val) {
            _count = val;
        }
    }

    public static void dataOutPut(L1PcInstance pc, String data) {
        GeneralThreadPool.getInstance().schedule(new L1CraftNumCommandBBS(pc, data), 100);
    }

    private static class L1CraftNumCommandBBS implements Runnable {
        L1PcInstance _pc;
        String _data;

        public L1CraftNumCommandBBS(L1PcInstance pc, String data) {
            _pc = pc;
            _data = data;
        }

        @Override
        public void run() {
            _pc.sendPackets(new S_L1CraftNumCommandPacket(_data));
        }
    }

    private static class S_L1CraftNumCommandPacket extends ServerBasePacket {
        public S_L1CraftNumCommandPacket(String data) {
            writeC(Opcodes.S_BOARD_READ);
            writeD(0);
            writeS("GM");
            writeS("クラフト情報");
            writeS("--");
            writeS(data);
        }

        @Override
        public byte[] getContent() throws IOException {
            return super.getBytes();
        }
    }
}

//
//
//
//
//
//
//
//
//
//
//
// 利用するためにはS_ACTION_UIを変更する必要あり
//
// case 460000127:
// L1CraftNumCommandStructure st = L1CraftNum._craftNum.get(pc.getId());
// if(st != null){
// int count = st.getCount();
// int startNum = st.getStartNum();
//
// craftlist = new int[st.getCount()];
// for(int i=0; i < count; i++){
// craftlist[i] = startNum + i;
// }
// // st更新
// st.setStartNum(startNum + count);
// //
// L1CraftNum.dataOutPut(pc, String.format("StartNo=%s\r\nCount%s", startNum,
// count));
// }else{
// craftlist = new int[0];
// }
//
// break;