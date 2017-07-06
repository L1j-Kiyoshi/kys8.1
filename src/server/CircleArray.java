package server;

import l1j.server.server.utils.IntRange;

public class CircleArray {
	private byte[] data;
	private int size = 0;
	private int read_loc = 0;
	private int write_loc = 0;
	private int total_write_length = 0;
	
	public CircleArray(int _size){
		size =  _size;
		data = new byte[_size];
	}
	/**
	 * 
	 * @param packet
	 */
	public void insert(byte[] packet,int _size){
		 int temp_length = IntRange.ensure(_size, 0,size - write_loc);
		 System.arraycopy(packet, 0, data, write_loc, temp_length);
		 write_loc = (write_loc + temp_length) % size;		 
		 if((_size - temp_length) > 0){
		 System.arraycopy(packet, temp_length, data, write_loc, _size - temp_length);
		 write_loc += _size - temp_length;
		 }
		 total_write_length += _size;
	}
	
	/**
	 * length 
	 * @param length
	 * @return
	 */
	public byte[] Pull(int length){
		 byte[] packet = new byte[length];
		 int temp_length = IntRange.ensure(length, 0,size - read_loc);
		 System.arraycopy(data, read_loc, packet, 0, temp_length);
		 read_loc = (read_loc + temp_length) % size;			 
		 if((length - temp_length) > 0){
			 System.arraycopy(data, read_loc, packet, temp_length, length - temp_length);
			 read_loc += length - temp_length;
		 }
		 total_write_length -= length;
		 return packet;
	}
	
	/**
	 * 
	 * @return
	 */
	public int isPacketPull(){
		int readsize = PacketSize();
		if(readsize != -1 && readsize <= total_write_length){
			return readsize;
		}
		return -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public int PacketSize() {
		 if(4 > total_write_length)return -1;		 
		 int length = (data[read_loc]) & 0xff;
		 	length |= (data[(read_loc+1) % size]) << 8 & 0xff00;
		 	return length;
	 }
	
	
	/**
	 * 
	 * @return
	 */
	public int isPacketPull2(){
		int readsize = PacketSize2();
		if(readsize != -1 && readsize <= total_write_length){
			return readsize;
		}
		return -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public int PacketSize2() {
		 if(4 > total_write_length)return -1;		 
		 int length = (data[read_loc]) & 0xff;
		 	length |= (data[(read_loc+1) % size]) << 8 & 0xff00;
		 	return length;
	 }
}
