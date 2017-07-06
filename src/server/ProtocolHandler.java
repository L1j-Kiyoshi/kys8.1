package server;

import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

//import l1j.server.server.clientpackets.P_connect;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.LittleEndianHeapChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import l1j.server.Config;
import l1j.server.server.GameClient;
import server.netty.coder.manager.DecoderManager;

public final class ProtocolHandler extends SimpleChannelUpstreamHandler {

    private static final TimerPool _timerPool = new TimerPool(300);

    class sessionCheck extends TimerTask {
	Channel session;

	public sessionCheck(Channel _session) {
	    session = _session;
	}

	@Override
	public void run() {
	    GameClient lc = (GameClient) session.getAttachment();
	    if (lc != null) {
		if (lc.packetvirsion == false) {
		    lc.close();
		    lc = null;
		}
	    } else {
		session.close();
	    }
	}

    }

    private final CopyOnWriteArrayList<GameClient> _Clientlist = new CopyOnWriteArrayList<GameClient>();
    // private HashMap<String, Ip> _list;

    public ProtocolHandler() {
	// _list = new HashMap<String, Ip>();
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	String[] address = e.getChannel().getRemoteAddress().toString().substring(1).split(":");
	String ip = address[0];
	int port = Integer.valueOf(address[1]);
	long time = System.currentTimeMillis();

	if (port <= 0) {
	    e.getChannel().close();
	    return;
	}

	_timerPool.getTimer().schedule(new sessionCheck(e.getChannel()), 3500);
	/*
	 * Ip IP = _list.get(ip); if(IP == null){ IP = new Ip(); IP.ip = ip;
	 * IP.time = time; _list.put(IP.ip, IP); return; }
	 *
	 * if(IP.block){ e.getChannel().close(); return; }
	 *
	 * if(time < IP.time+1000){ if(IP.count>3){ IP.block = true;
	 * e.getChannel().close(); return; }else{ IP.count++; } }else{ IP.count
	 * = 0; } IP.time = time;
	 */
    }

    public int getRowIndex() {
	int size = DecoderManager.getInstance().getindex_size();
	int[] index = new int[size];
	for (GameClient Client : _Clientlist) {
	    index[Client.getthreadIndex()]++;
	}
	int temp = 1000;
	int o = 0;
	for (int i = 0; i < index.length; i++) {
	    int temp1 = index[i];
	    if (temp1 < temp) {
		temp = temp1;
		o = i;
	    }
	}
	return o;
    }

    /**
     *
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	try {
	    if (!Config.shutdownCheck && e.getChannel().isConnected()) {
		// long seed = 0x7479623E; //0913
		// long seed = 0x7795647E; // 170314
		long seed = 0x1f84a45dL; // 170620

		KeyPacket key = new KeyPacket();
		GameClient lc = new GameClient(e.getChannel(), seed);
		DecoderManager.getInstance();
		lc.setthreadIndex(getRowIndex());
		_Clientlist.add(lc);
		ChannelBuffer buffer = buffer(key.getBytes(), key.getLength());
		e.getChannel().write(buffer);
		e.getChannel().setAttachment(lc);
	    } else {
		e.getChannel().close();
	    }
	} catch (Exception E) {
	    E.printStackTrace();
	    // Logger.getInstance().error(getClass().toString()+"
	    // sessionOpened(IoSession session)\r\n"+e.toString(),
	    // Config.LOG.error);
	}
    }

    private ChannelBuffer buffer(byte[] data, int length) {
	byte[] size = new byte[2];
	size[0] |= length & 0xff;
	size[1] |= length >> 8 & 0xff;
	ChannelBuffer _buffer = new LittleEndianHeapChannelBuffer(length);// +4
	/*
	 * _buffer.writeByte((byte) (0x01)); _buffer.writeByte((byte) (0x00));
	 * _buffer.writeByte((byte) (0x00)); _buffer.writeByte((byte) (0x00));
	 */
	_buffer.writeBytes(size);
	_buffer.writeBytes(data);

	return _buffer;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
	GameClient client = null;
	try {

	    client = (GameClient) e.getChannel().getAttachment();
	    ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
	    if (client != null) {
		int size = buffer.readableBytes();
		if (size > 0 && size < 1024) {
		    byte[] data = buffer.array();

		    if (client.packetvirsion == false) {
			if (client.packetcount++ > 2) {
			    client.ckclose = true;
			    DecoderManager.getInstance().putClient(client);
			    return;
			}
		    }

		    if (data[0] == 4 && data[1] == 4 && data[2] == 4 && data[3] == 1) {
			// new P_connect(data,client);
			return;
		    }
		    client.getCircleArray().insert(data, size);
		    if (client.getCircleArray().isPacketPull() > 0) {
			DecoderManager.getInstance().putClient(client);

		    }

		}
	    } else {
		e.getChannel().close();
	    }
	    client = null;
	} catch (Exception E) {
	    if (client != null) {
		client.ckclose = true;
		DecoderManager.getInstance().putClient(client);
	    }
	}

    }

    /**
     *
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	GameClient lc = (GameClient) e.getChannel().getAttachment();
	if (lc != null) {
	    _Clientlist.remove(lc);
	    lc.close();
	    lc = null;
	}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
	// TODO Auto-generated method stub
    }

}
