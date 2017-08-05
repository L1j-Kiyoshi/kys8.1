package l1j.server.server.serverpackets;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import l1j.server.server.Opcodes;

public class S_Emblem extends ServerBasePacket {

    private static final String S_EMBLEM = "[S] S_Emblem";

    public S_Emblem(int emblemId) {
        BufferedInputStream bis = null;
        try {
            String emblem_file = String.valueOf(emblemId);
            File file = new File("emblem/" + emblem_file);
            if (file.exists()) {
                int data = 0;
                bis = new BufferedInputStream(new FileInputStream(file));
                writeC(Opcodes.S_EMBLEM);
                writeD(emblemId);
                while ((data = bis.read()) != -1) {
                    writeP(data);
                }
            }
        } catch (Exception e) {
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ignore) {
                    // ignore
                }
            }
        }
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return S_EMBLEM;
    }
}

