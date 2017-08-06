package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_SkillBuy extends ServerBasePacket {
    public S_SkillBuy(int o, L1PcInstance player) {
        int count = Scount(player);
        writeC(Opcodes.S_BUYABLE_SPELL_LIST);
        writeD(100);
        writeH(count);
        for (int k = 0; k < count; k++) {
            writeD(k);
        }
    }

    public int Scount(L1PcInstance player) {
        int RC = 0;
        // int TC = 0;
        switch (player.getType()) {
            case 0: // 君主
            /*
             * if (player.get_level() >= 10 && player.get_level() <= 19) { RC =
			 * 8; } else if (player.get_level() >= 20) { RC = 16; }
			 */

                RC = 16;
                break;

            case 1: // ナイト
            /*
			 * if (player.get_level() >= 50) { RC = 8; }
			 */

                RC = 8;
                break;

            case 2: // エルフ
			/*
			 * if (player.get_level() >= 8 && player.get_level() <= 15) { RC =
			 * 8; } else if (player.get_level() >= 16 && player.get_level() <=
			 * 23) { RC = 16; } else if (player.get_level() >= 24) { RC = 23; }
			 */

                RC = 23;
                break;

            case 3: // WIZ
			/*
			 * if (player.get_level() >= 4 && player.get_level() <= 7) { RC = 8; }
			 * else if (player.get_level() >= 8 && player.get_level() <= 11) {
			 * RC = 16; } else if (player.get_level() >= 12) { RC = 23; }
			 */

                RC = 23;
                break;

            case 4: // DE
			/*
			 * if (player.get_level() >= 12 && player.get_level() <= 23) { RC =
			 * 8; } else if (player.get_level() >= 24) { RC = 16; }
			 */

                RC = 16;
                break;
            case 7: // ウォリアー
			/*
			 * if (player.get_level() >= 50) { RC = 8; }
			 */

                RC = 8;
                break;
            default:
                break;
        }
        return RC;
		/*
		 * for(int i = 0 ; i < RC ; ++i) { if(chk(player, i) == false) { TC++; } }
		 * return TC;
		 */
    }

    /*
     * public boolean chk(L1PcInstance player, int i) { boolean have = false;
     * try { Connection connection = null; connection =
     * L1DatabaseFactory.getInstance().getConnection(); PreparedStatement
     * preparedstatement = connection.prepareStatement("SELECT * FROM
     * character_skills WHERE char_obj_id=?"); preparedstatement.setInt(1,
     * player.get_objectId()); ResultSet resultset =
     * preparedstatement.executeQuery(); while (resultset.next()) { int b =
     * resultset.getInt(3); if (i == b) { have = true; } } resultset.close();
     * preparedstatement.close(); connection.close(); } catch (Exception
     * exception) { } return have; }
     */
    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return _S__1B_WAR;
    }

    private static final String _S__1B_WAR = "[S] S_SkillBuy";
}
