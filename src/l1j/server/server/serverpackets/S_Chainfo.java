package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.utils.SQLUtil;

public class S_Chainfo extends ServerBasePacket {

	private static final String S_Chainfo = "[C] S_Chainfo";

	private static Logger _log = Logger.getLogger(S_Chainfo.class.getName());

	private byte[] _byte = null;

	public S_Chainfo(int number, String cha) {
		buildPacket(number, cha);
	}

	private void buildPacket(int number, String cha) {
		Connection con = null;
		Connection con1 = null;
		Connection con2 = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String info1 = null;//ここから変数保持ランナー
		String info13 = null;
		String clas = null;
		String ggg = null;
		int oo = 0;
		String ggg1 = null;
		int oo1 = 0;
		String ggg2 = null;
		int oo2 = 0;
		String ggg3 = null;
		int oo3 = 0;
		String ggg4 = null;
		int oo4 = 0;
		String ggg5 = null;
		int oo5 = 0;
		String ggg6 = null;
		int oo6 = 0;
		String ggg7 = null;
		int oo7 = 0;
		String ggg8 = null;
		int oo8 = 0;
		String ggg9 = null;
		int oo9 = 0;
		String ggg10 = null;
		int oo10 = 0;
		String ggg11 = null;
		int oo11 = 0;
		String ggg12 = null;
		int oo12 = 0;
		String ggg13 = null;
		int oo13 = 0;
		int rol= 0;
		int	info2 = 0;
		int	info3 = 0;
		int	info4 = 0;
		int	info5 = 0;
		int	info6 = 0;
		int	info7 = 0;
		int	info8 = 0;
		int	info9 = 0;
		int	info10 = 0;
		int	info11 = 0;
		int	info12 = 0;
		int x = 0;
		int ddd = 357859160;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM characters WHERE char_name=?");
			pstm.setString(1, cha);
			rs = pstm.executeQuery();
			
			while (rs.next()) {
			    info1 = rs.getString(2);//キャラクターオブジェクトIDで検査< - これは、以下のアイテムチェックするときに必要
				info2 = rs.getInt(4);//レプ
				info3 =	rs.getInt(7);//ピトン
				info4 =	rs.getInt(9);//エムトン
				info5 =	rs.getInt(11);//AC
				info6 =	rs.getInt(12);//力
				info7 =	rs.getInt(14);//コーン
				info8 =	rs.getInt(16);//デックス
				info9 =	rs.getInt(18);//カリー
				info10 = rs.getInt(20);//ポイント
				info11 = rs.getInt(22);//ウィズ
				info12 = rs.getInt(27);//クラス
				info13 = rs.getString(1);
			}



			con2 = L1DatabaseFactory.getInstance().getConnection();
			pstm2 = con2.prepareStatement("SELECT * FROM character_items WHERE char_id=? AND item_name=?");
			pstm2.setString(1, info1);
			pstm2.setString(2, "アデナ");
			rs2 = pstm2.executeQuery();
			while (rs2.next()) {
			rol = rs2.getInt(5);
			}
			con1 = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con1.prepareStatement("SELECT `enchantlvl`,`item_name` FROM `character_items` WHERE char_id=? ORDER BY `enchantlvl` DESC LIMIT 14");
			pstm1.setString(1, info1);//上記の検査したキャラのオブジェクトに再びクエリ検査
			rs1 = pstm1.executeQuery();
			while (rs1.next()) {
				++x;
				if(x == 1){
					ggg = rs1.getString("item_name");//アイテム名
					oo = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 2){
					ggg1 = rs1.getString("item_name");//アイテム名
					oo1 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 3){
					ggg2 = rs1.getString("item_name");//アイテム名
					oo2 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 4){
					ggg3 = rs1.getString("item_name");//アイテム名
					oo3 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 5){
					ggg4 = rs1.getString("item_name");//アイテム名
					oo4 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 6){
					ggg5 = rs1.getString("item_name");//アイテム名
					oo5 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 7){
					ggg6 = rs1.getString("item_name");//アイテム名
					oo6 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 8){
					ggg7 = rs1.getString("item_name");//アイテム名
					oo7 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 9){
					ggg8 = rs1.getString("item_name");//アイテム名
					oo8 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 10){
					ggg9 = rs1.getString("item_name");//アイテム名
					oo9 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 11){
					ggg10 = rs1.getString("item_name");//アイテム名
					oo10 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 12){
					ggg11 = rs1.getString("item_name");//アイテム名
					oo11 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 13){
					ggg12 = rs1.getString("item_name");//アイテム名
					oo12 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
				else if(x == 14){
					ggg13 = rs1.getString("item_name");//アイテム名
					oo13 = rs1.getInt("enchantlvl");//これエンチャントレベル
				}
			}
			    if(info12 == 0){
                  clas = "君主";
				}
				else if(info12 == 1){
                  clas = "ナイト";
				}
				else if(info12 == 2){
                  clas = "エルフ";
				}
				else if(info12 == 3){
                  clas = "ウィザード";
				}
				else if(info12 == 4){
                  clas = "ダークエルフ";
				}
				else if(info12 == 5){
	              clas = "ドラゴンナイト";
				}
				else if(info12 == 6){
	              clas = "イリュージョニスト";
				}
				else if(info12 == 7){
		              clas = "ウォリアー";
				}
                writeC(Opcodes.S_BOARD_READ);
				writeD(number);//ナンバー
				writeS(cha);//脚本 - キャラクター名
				writeS(info13);//タイトル - アカウント 
				writeS(clas);//投稿数0001  - クラス
				//writeS("[レベル] "+info2+"  [アデン] "+rol+"\n HP:"+info3+"  MP:"+info4+"  AC:"+info5+"\n STR:"+info6+"  CON:"+info7+"  DEX:" +info8+"\n CHA:"+info9+"  INT:"+info10+"  WIS:"+info11+"\n+"+oo+" "+ggg+"\n+"+oo1+" "+ggg1+"\n+"+oo2+" "+ggg2+"\n+"+oo3+" "+ggg3+"\n+"+oo4+" "+ggg4+"\n+"+oo5+" "+ggg5+"\n+"+oo6+" "+ggg6+"\n+"+oo7+" "+ggg7+"\n+"+oo8+" "+ggg8+"\n+"+oo9+" "+ggg9+"\n+"+oo10+" "+ggg10+"\n+"+oo11+" "+ggg11+"\n+"+oo12+" "+ggg12+"\n+"+oo13+" "+ggg13); 
				writeS("レベル"+info2+"  [アデン] "+rol+"\n HP "+info3+" MP "+info4+"  AC "+info5+"\n 力:"+info6+"  デックス:" +info8+"  ポイント:"+info10+"\n コーン:"+info7+"  ウィズ:"+info11+"  カリー:"+info9+"\n+"+oo+" "+ggg+"\n+"+oo1+" "+ggg1+"\n+"+oo2+" "+ggg2+"\n+"+oo3+" "+ggg3+"\n+"+oo4+" "+ggg4+"\n+"+oo5+" "+ggg5+"\n+"+oo6+" "+ggg6+"\n+"+oo7+" "+ggg7+"\n+"+oo8+" "+ggg8+"\n+"+oo9+" "+ggg9+"\n+"+oo10+" "+ggg10+"\n+"+oo11+" "+ggg11+"\n+"+oo12+" "+ggg12+"\n+"+oo13+" "+ggg13);

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			SQLUtil.close(rs1);
			SQLUtil.close(pstm1);
			SQLUtil.close(con1);
			SQLUtil.close(rs2);
			SQLUtil.close(pstm2);
			SQLUtil.close(con2);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_Chainfo;
	}
}
