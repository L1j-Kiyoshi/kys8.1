package server.netty.coder.manager;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import l1j.server.server.GameClient;
import server.CircleArray;



public class LineageDecoderThread implements Runnable{
	
	public LineageDecoderThread(){
		_client = new LinkedBlockingQueue<GameClient>();
	}
	
	private LinkedBlockingQueue<GameClient> _client;
	int count = 0;
	int pa_size;
	ByteBuffer buf;
	public void run(){
		while(true){
			try {
				GameClient client =  _client.take();
					if(client!=null){
						if(!client.isConnected()||client.ckclose==true){
							client.close();
							removeClient(client);							
							continue;
						}
						
						CircleArray Circle = client.getCircleArray();
						while((pa_size = Circle.isPacketPull2()) > 0){
							client.encryptD(Circle.Pull(pa_size));
						}
						
						
					}else{
						removeClient(client);
					}
			} catch (Exception e) {
				//Logger.getInstance().error(getClass().toString()+" run()\r\n"+e.toString(), Config.LOG.error);
			}
		}
	}
	
	private int PacketSize(byte[] data){
		int length = data[0] &0xff;
		length |= data[1] << 8 &0xff00;
		return length;
	}
	
	private int PacketBufSize(ByteBuffer buf){
		if(buf.remaining() < 2)return 0;
		int length = buf.get() &0xff;
		length |= buf.get() << 8 &0xff00;
		return length;
	}
	

	public void putClient(GameClient c){
		try {
			//if(!_client.contains(c)) {
				_client.put(c);
			//}
		} catch (Exception e) {
			//Logger.getInstance().error(getClass().toString()+" putClient(LineageClient c)\r\n"+e.toString(), Config.LOG.error);
		}
	}
	

	public GameClient getClient(String id){
		if(id!=null){
			try {
				for(GameClient c : _client){
					if(c!=null){
						if(c.getID()!=null && c.getID().equalsIgnoreCase(id)) {
							return c;
						}
					}else{
						removeClient(c);
					}
				}
			} catch (Exception e) {
				//Logger.getInstance().error(getClass().toString()+" getClient(String id)\r\n"+e.toString(), Config.LOG.error);
			}
		}
		return null;
	}
	

	public void removeClient(GameClient c){
		/** LINALL CONNECT SOURCE START **/
		/*if(Config.AUTH_CONNECT) {
			if (c.getAccount() != null) {
				if(LinAllDataSync.getInstance().is_Account(c.getAccount())) {
					LinAllDataSync.getInstance().account_Delete(c.getAccount());
				}
			}
		}*/
		/** LINALL CONNECT SOURCE END **/
		while(_client.contains(c))
				_client.remove(c);
	}
	

	public int ClientCount(){
		return _client.size();
	}

	public boolean ContainsClient(GameClient c){
		return _client.contains(c);
	}
	public LinkedBlockingQueue<GameClient> getAllClient(){
		return this._client;
	}

}
