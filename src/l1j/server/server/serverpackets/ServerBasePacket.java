/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.serverpackets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ServerBasePacket {

	private int OpKey; // opcode Key

	private boolean isKey = true;
	private static Logger _log = Logger.getLogger(ServerBasePacket.class.getName());

	ByteArrayOutputStream _bao = new ByteArrayOutputStream();

	protected ServerBasePacket() {
	}

	public void clear() {
		try {
			_bao.reset();
			_bao.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_bao = null;
	}
	
	// Key
	private void setKey(int i) {
		OpKey = i;
	}

	private int getKey() {
		return OpKey;
	}

	protected void writeD(int value) {
		_bao.write(value & 0xff);
		_bao.write(value >> 8 & 0xff);
		_bao.write(value >> 16 & 0xff);
		_bao.write(value >> 24 & 0xff);
	}

	protected void writeH(int value) {
		_bao.write(value & 0xff);
		_bao.write(value >> 8 & 0xff);
	}

	protected void writeC(int value) {
		_bao.write(value & 0xff);
		// オプションコードwirteC最初の呼び出しのみ設定...
		if (isKey) {
			setKey(value);
			isKey = false;
		}
	}

	public int writeLenght(long value) {
		if (value < 0L) {
			String stringValue = Integer.toBinaryString((int) value);
			value = Long.valueOf(stringValue, 2).longValue();
		}
		int size = 0;

		if (value <= 127L)
			size = 1;
		else if (value <= 16383L)
			size = 2;
		else if (value <= 2097151L)
			size = 3;
		else if (value <= 268435455L)
			size = 4;
		else if (value <= 34359738367L) {
			size = 5;
		}

		return size;
	}
	public void reset() {
		// TODO Auto-generated method stub
		_bao.reset();
	}
	protected void writeSU16(String text) {
		try {
			if (text != null) {
				_bao.write(text.getBytes("UTF-16LE"));
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		_bao.write(0);
		_bao.write(0);
	}

	
	protected void writeK(int value) {
		int valueK = (int) (value / 128);
		if(valueK == 0){
			_bao.write(value);
		}else if(valueK <= 127){
			_bao.write((value & 0x7f) + 128);
			_bao.write(valueK);
		}else if(valueK <= 16383){
			_bao.write((value & 0x7f) + 128);
			_bao.write((valueK & 0x7f) + 128);
			_bao.write(valueK / 128);
		}else if(valueK <= 2097151){
			_bao.write((value & 0x7f) + 128);
			_bao.write((valueK & 0x7f) + 128);
			_bao.write(((valueK / 128) & 0x7f) + 128);
			_bao.write(valueK / 16384);
		}else{
			_bao.write((value & 0x7f) + 128);
			_bao.write((valueK & 0x7f) + 128);
			_bao.write(((valueK / 128) & 0x7f) + 128);
			_bao.write(((valueK / 16384) & 0x7f) + 128);
			_bao.write(valueK / 2097152);
		}
	}

	public int bitlengh(int obj) {
		int length = 0;
		if (obj < 0) {
			BigInteger b = new BigInteger("18446744073709551615");
			while (BigInteger.valueOf(obj).and(b).shiftRight((length + 1) * 7).longValue() > 0) {
				length++;
			}
			length++;
		} else {
			if (obj <= 127) {
				length = 1;
			} else if (obj <= 16383) {
				length = 2;
			} else if (obj <= 2097151) {
				length = 3;
			} else if (obj <= 268435455) {
				length = 4;
			} else if ((long) obj <= 34359738367L) {
				length = 5;
			}
		}
		return length;
	}
	
	/* 韓国オプションコードの追加のパケット */
	protected void write4bit(int value)
	  {
	    if (value <= 127) {
	      this._bao.write(value & 0x7F);
	    } else if (value <= 16383) {
	      this._bao.write(value & 0x7F | 0x80);
	      this._bao.write(value >> 7 & 0x7F);
	    } else if (value <= 2097151) {
	      this._bao.write(value & 0x7F | 0x80);
	      this._bao.write(value >> 7 & 0x7F | 0x80);
	      this._bao.write(value >> 14 & 0x7F);
	    } else if (value <= 268435455) {
	      this._bao.write(value & 0x7F | 0x80);
	      this._bao.write(value >> 7 & 0x7F | 0x80);
	      this._bao.write(value >> 14 & 0x7F | 0x80);
	      this._bao.write(value >> 21 & 0x7F);
	    } else if (value <= 34359738367L) {
	      this._bao.write(value & 0x7F | 0x80);
	      this._bao.write(value >> 7 & 0x7F | 0x80);
	      this._bao.write(value >> 14 & 0x7F | 0x80);
	      this._bao.write(value >> 21 & 0x7F | 0x80);
	      this._bao.write(value >> 28 & 0x7F);
	    }
	  }
	
	protected void writeBit(long value) {
		if (value < 0L) {
			String stringValue = Integer.toBinaryString((int) value);
			value = Long.valueOf(stringValue, 2).longValue();
		}
		int i = 0;
		while (value >> 7 * (i + 1) > 0L) {
			_bao.write((int) ((value >> 7 * i++) % 128L | 0x80));
		}
		_bao.write((int) ((value >> 7 * i) % 128L));
	}

	protected void writeLS(String text) {
		try {
			if (text != null && !text.isEmpty()) {
				byte[] name = text.getBytes("MS949");
				this._bao.write(name.length & 255);
				if (name.length > 0) {
					this._bao.write(name);
				}
			} else {
				this._bao.write(0);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	
	protected int getBitSize(long value) {
		if (value < 0L) {
			String stringValue = Integer.toBinaryString((int) value);
			value = Long.valueOf(stringValue, 2).longValue();
		}
		int size = 0;
		while (value >> (size + 1) * 7 > 0L) {
			size++;
		}
		size++;

		return size;
	}

	protected void write7B(long value) {
		int i = 0;
		BigInteger b = new BigInteger("18446744073709551615");

		while (BigInteger.valueOf(value).and(b).shiftRight((i + 1) * 7).longValue() > 0) {
			_bao.write(BigInteger.valueOf(value).and(b).shiftRight(7 * i++).remainder(BigInteger.valueOf(0x80)).or(BigInteger.valueOf(0x80)).intValue());
		}
		_bao.write(BigInteger.valueOf(value).and(b).shiftRight(7 * i++).remainder(BigInteger.valueOf(0x80)).intValue());
	}

	public int size7B(int obj) {
		int length = 0;
		if (obj < 0) {
			BigInteger b = new BigInteger("18446744073709551615");
			while (BigInteger.valueOf(obj).and(b).shiftRight((length + 1) * 7).longValue() > 0) {
				length++;
			}
			length++;
		} else {
			if (obj <= 127) {
				length = 1;
			} else if (obj <= 16383) {
				length = 2;
			} else if (obj <= 2097151) {
				length = 3;
			} else if (obj <= 268435455) {
				length = 4;
			} else if ((long) obj <= 34359738367L) {
				length = 5;
			}
		}
		return length;
	}
	
	protected void writeP(int value) {
		_bao.write(value);
	}

	protected void writeF(double org) {
		long value = Double.doubleToRawLongBits(org);
		_bao.write((int) (value & 0xff));
		_bao.write((int) (value >> 8 & 0xff));
		_bao.write((int) (value >> 16 & 0xff));
		_bao.write((int) (value >> 24 & 0xff));
		_bao.write((int) (value >> 32 & 0xff));
		_bao.write((int) (value >> 40 & 0xff));
		_bao.write((int) (value >> 48 & 0xff));
		_bao.write((int) (value >> 56 & 0xff));
	}

	protected void writeS(String text) {
		try {
			if (text != null) {
				_bao.write(text.getBytes("MS949"));
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		_bao.write(0);
	}

	protected void writeSS(String text) {
		try {
			if (text != null) {
				byte[] test = text.getBytes("MS949");
				for (int i = 0; i < test.length;) {
					if ((test[i] & 0xff) >= 0x7F) {
						/** ハングルの **/
						_bao.write(test[i + 1]);
						_bao.write(test[i]);
						i += 2;
					} else {
						/** 英語＆数字 **/
						_bao.write(test[i]);
						_bao.write(0);
						i += 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		_bao.write(0);
		_bao.write(0);
	}

	protected void writeByte(byte[] text) {
		try {
			if (text != null) {
				_bao.write(text);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public int getLength() {
		return _bao.size() + 2;
	}
	
	public byte[] getBytes() {
		return _bao.toByteArray();
	}
	
	protected void writeS2(String text) {
		try {
			if (text != null && !text.isEmpty()) {
				byte[] name = text.getBytes("MS949");
				_bao.write(name.length & 0xff);
				if (name.length > 0) {
					_bao.write(name);
				}
			} else {
				_bao.write(0 & 0xff);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
	public byte[] getBytes1() {
		return _bao.toByteArray();
	}

	public abstract byte[] getContent() throws IOException;

	/**
	 * サーバーのパケットの種類を表す文字列を返す。 （「[S] S_WhoAmount "など）
	 */
	public String getType() {
		return "";
	}

	public String toString() {
		String sTemp = getType().equals("") ? "" : "[" + getKey() + "] " + getType();
		return sTemp;
	}
	
	  public void close() {
			try {
				_bao.close();
			} catch (Exception e) {
			}
		}
	  
}
