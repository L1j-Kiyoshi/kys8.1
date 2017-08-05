package l1j.server.server.Controller;

import java.util.Calendar;

import l1j.server.IndunSystem.ClanDungeon.AzmodanSystem;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.gametime.BaseTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.gametime.TimeListener;
import l1j.server.server.serverpackets.S_SystemMessage;

public class OneTimeController implements TimeListener {
    private static OneTimeController _instance;

    public static void start() {
        if (_instance == null) {
            _instance = new OneTimeController();
        }
        _instance.some();
        RealTimeClock.getInstance().addListener(_instance);
    }

    private void some() {
    }

    @Override
    public void onDayChanged(BaseTime time) {
    }

    @Override
    public void onHourChanged(BaseTime time) {
        int h = time.get(Calendar.HOUR_OF_DAY);
        if (h == 0) {
            UnderReset();
            L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("通知：すべてのクランの地下通路がリセットされました。"));
        }
    }

    @Override
    public void onMinuteChanged(BaseTime time) {
    }

    @Override
    public void onMonthChanged(BaseTime time) {
    }

    private void UnderReset() {
        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            clan.setUnderDungeon(0);
            ClanTable.getInstance().updateUnderDungeon(clan.getClanId(), 0);
            clan.setUnderMapid(0);
            /** アズモダンクランIDの削除 **/
            AzmodanSystem.getInstance().removeClan(clan.getClanId());
        }
    }
}
