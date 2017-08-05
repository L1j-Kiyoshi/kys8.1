/*
 * This file is pART of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package l1j.server.server.utils;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

/**
 * @author ATracer
 */
public class DeadLockDetector implements Runnable {

    private static String INDENT = "    ";
    private StringBuilder sb = null;
    private L1PcInstance pc = null;

    public DeadLockDetector(L1PcInstance _pc) {
        pc = _pc;
    }

    @Override
    public void run() {
        // boolean noDeadLocks = true;
        // while(noDeadLocks){
        try {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long[] threadIds = bean.findDeadlockedThreads();
            if (threadIds != null) {
                if (pc != null && pc.getNetConnection() != null)
                    pc.sendPackets(new S_SystemMessage("デッドロックを検出！ - サーバーCmd参考"));
                else
                    System.out.println("デッドロックを検出！");
                sb = new StringBuilder();
                // noDeadLocks = false;

                ThreadInfo[] infos = bean.getThreadInfo(threadIds);
                sb.append("\nスレッドロック情報: \n");
                for (ThreadInfo threadInfo : infos) {
                    printThreadInfo(threadInfo);
                    LockInfo[] lockInfos = threadInfo.getLockedSynchronizers();
                    MonitorInfo[] monitorInfos = threadInfo.getLockedMonitors();

                    printLockInfo(lockInfos);
                    printMonitorInfo(threadInfo, monitorInfos);
                }

                sb.append("\nスレッドダンプ: \n");
                for (ThreadInfo ti : bean.dumpAllThreads(true, true)) {
                    printThreadInfo(ti);
                }
                System.out.println(sb.toString());
            } else {
                if (pc != null && pc.getNetConnection() != null)
                    pc.sendPackets(new S_SystemMessage("デッドロックなし"));
                else
                    System.out.println("デッドロック無し。");
            }
            // Thread.sleep(checkInterval);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // }
    }

    private void printThreadInfo(ThreadInfo threadInfo) {
        printThread(threadInfo);
        sb.append(INDENT + threadInfo.toString() + "\n");
        StackTraceElement[] stacktrace = threadInfo.getStackTrace();
        MonitorInfo[] monitors = threadInfo.getLockedMonitors();

        for (int i = 0; i < stacktrace.length; i++) {
            StackTraceElement ste = stacktrace[i];
            sb.append(INDENT + "at " + ste.toString() + "\n");
            for (MonitorInfo mi : monitors) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append(INDENT + "  - locked " + mi + "\n");
                }
            }
        }
    }

    private void printThread(ThreadInfo ti) {
        sb.append("\nスレッド出力\n");
        sb.append("\"" + ti.getThreadName() + "\"" + " Id=" + ti.getThreadId() + " in " + ti.getThreadState() + "\n");
        if (ti.getLockName() != null) {
            sb.append(" on lock=" + ti.getLockName() + "\n");
        }
        if (ti.isSuspended()) {
            sb.append(" (一時停止)" + "\n");
        }
        if (ti.isInNative()) {
            sb.append(" (有効)" + "\n");
        }
        if (ti.getLockOwnerName() != null) {
            sb.append(INDENT + " owned by " + ti.getLockOwnerName() + " Id=" + ti.getLockOwnerId() + "\n");
        }
    }

    private void printMonitorInfo(ThreadInfo threadInfo, MonitorInfo[] monitorInfos) {
        sb.append(INDENT + "ロックされたモニター: " + monitorInfos.length + "本\n");
        for (MonitorInfo monitorInfo : monitorInfos) {
            sb.append(INDENT + "  - " + monitorInfo + " locked at " + "\n");
            sb.append(INDENT + "      " + monitorInfo.getLockedStackDepth() + " " + monitorInfo.getLockedStackFrame()
                    + "\n");
        }
    }

    private void printLockInfo(LockInfo[] lockInfos) {
        sb.append(INDENT + "ロックされたシンク：" + lockInfos.length + "本\n");
        for (LockInfo lockInfo : lockInfos) {
            sb.append(INDENT + "  - " + lockInfo + "\n");
        }
    }
}
