package l1j.server.server.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Warehouse.ClanWarehouse;
import l1j.server.server.model.Warehouse.PrivateWarehouse;
import l1j.server.server.model.Warehouse.WarehouseManager;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.SQLUtil;

public class ArnoldBackEvent extends Thread {
 
private static ArnoldBackEvent _instance;

private boolean _CameBackArnoldEvent;
  
public boolean getCameBackArnoldEvent() {
	return _CameBackArnoldEvent;
	}

private static long sTime = 0;

private String NowTime = "";

private static final SimpleDateFormat s = new SimpleDateFormat("HH", Locale.KOREA);

private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
  
public void setCameBackArnoldEvent(boolean cameBackArnoldEvent) {
	_CameBackArnoldEvent = cameBackArnoldEvent;
	}
  
  public boolean isGmOpenArnold = false;
  
  public static ArnoldBackEvent getInstance() {
	  if(_instance == null) {
		  _instance = new ArnoldBackEvent();
	  }
	  return _instance;
  }
  
  @Override
  public void run() {
	  try {
		  while (true) {
			  Thread.sleep(1000); 
			  /** オープン **/
			  if(!isOpen6() && !isGmOpenArnold)
				  continue;
			  if(L1World.getInstance().getAllPlayers().size() <= 0)
				  continue;
			  
			  isGmOpenArnold = false;
			  
			  Config.ARNOLD_EVENTS = true;
			  L1SpawnUtil.spawn2(33433, 32798, (short) 4, 6, 0, (Config.ARNOLD_EVENT_TIME * 3600000), 0);//テレポーター 
			  L1SpawnUtil.spawn2(33431, 32798, (short) 4, 7, 0, (Config.ARNOLD_EVENT_TIME * 3600000), 0);//テレポーター 
			  L1SpawnUtil.spawn2(33429, 32798, (short) 4, 8, 0, (Config.ARNOLD_EVENT_TIME * 3600000), 0);//テレポーター 
			  L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"運営者様がアーノルドイベントをオープンします。"));
			  L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"ギラン旅館前エンピシをご利用ください。"));
			  L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"アーノルドイベントは" + Config.ARNOLD_EVENT_TIME + "時間維持されます。"));
			  
			  setCameBackArnoldEvent(true);
			  
			  Thread.sleep(Config.ARNOLD_EVENT_TIME*3600000); //60分程度
			  
			  Config.ARNOLD_EVENTS = false;
			  /** 終了 **/
			  End();
		  }
		  
	  } catch(Exception e){
		  e.printStackTrace();
	  }
  }
  
  private static void delenpc(int npcid) {
	  L1NpcInstance npc = null;
	  for (L1Object object : L1World.getInstance().getObject()) {
		  if (object instanceof L1NpcInstance) {
			  npc = (L1NpcInstance) object;
			  if (npc.getNpcTemplate().get_npcId() == npcid) {
				  npc.deleteMe();
				  npc = null;
			  }
		  }
	  }
  }
  
  /**
   *オープン時刻を持って来る
   *
   *@return (Strind) オープン時刻（MM-dd HH：mm）
   */
  public String arnoldOpen() {
	  Calendar c = Calendar.getInstance();
	  c.setTimeInMillis(sTime);
	  return ss.format(c.getTime());
  }
  
  /**
   *マップが開いていることを確認
   *
   *@return (boolean) 開いている場合true閉じている場合false
   */
   private boolean isOpen6() {
    NowTime = getTime();
    if((Integer.parseInt(NowTime)) == 0) return true;
    return false;
   }
   
  //悪魔王の領土ソース参照して追加する。
   public boolean isOpen7() {
		NowTime = getTime();
		if ((Integer.parseInt(NowTime)) >= 2 
				&& (Integer.parseInt(NowTime)) <= 8)
			return true;
		return false;
	}
   
   /**
   *実際、現在時刻を持って来る
   *
   *@return (String) 現在時刻（HH：mm）
   */
   private String getTime() {
    return s.format(Calendar.getInstance().getTime());
   }


  
  private static int delItemlist[] = { 307, 308, 309, 310, 311, 312, 313, 314, 21095, 
		  30146, 30147, 30150};

  public synchronized static void deleteArnoldEvent() {
	  try {
		  if (delItemlist.length <= 0)
			  return;
				
		  for (L1PcInstance tempPc : L1World.getInstance().getAllPlayers()) {
			  if (tempPc == null)
				  continue;
			  for (int i = 0; i < delItemlist.length; i++) {
				  L1ItemInstance[] item = tempPc.getInventory().findItemsId(delItemlist[i]);
				  if (item != null && item.length > 0) {
					  for (int o = 0; o < item.length; o++) {
						  tempPc.getInventory().removeItem(item[o]);
					  }
				  }
				  try {
					  PrivateWarehouse pw = WarehouseManager.getInstance().getPrivateWarehouse(tempPc.getAccountName());
					  L1ItemInstance[] item2 = pw.findItemsId(delItemlist[i]);
					  if (item2 != null && item2.length > 0) {
						  for (int o = 0; o < item2.length; o++) {
							  pw.removeItem(item2[o]);
						  }
					  }
				  } catch (Exception e) {}
				  try {
					  if (tempPc.getClanid() > 0) {
						  ClanWarehouse cw = WarehouseManager.getInstance().getClanWarehouse(tempPc.getClanname());
						  L1ItemInstance[] item3 = cw.findItemsId(delItemlist[i]);
						  if (item3 != null && item3.length > 0) {
							  for (int o = 0; o < item3.length; o++) {
								  cw.removeItem(item3[o]);
							  }
						  }
					  }
				  } catch (Exception e) {}
				  try {
					  if (tempPc.getPetList().size() > 0) {
						  for (L1NpcInstance npc : tempPc.getPetList().values()) {
							  L1ItemInstance[] pitem = npc.getInventory().findItemsId(delItemlist[i]);
							  if (pitem != null && pitem.length > 0) {
								  for (int o = 0; o < pitem.length; o++) {
									  npc.getInventory().removeItem(pitem[o]);
								  }
							  }
						  }
					  }
				  } catch (Exception e) {}
			  }
		  }
		  try {
			  for (L1Object obj : L1World.getInstance().getAllItem()) {
				  if (!(obj instanceof L1ItemInstance))
					  continue;
				  L1ItemInstance temp_item = (L1ItemInstance) obj;
				  if (temp_item.getItemOwner() == null) {
					  if (temp_item.getX() == 0 && temp_item.getY() == 0)
						  continue;
				  }
				  for (int ii = 0; ii < delItemlist.length; ii++) {
					  if (delItemlist[ii] == temp_item.getItemId()) {
						  L1Inventory groundInventory = L1World.getInstance().getInventory(temp_item.getX(),temp_item.getY(),temp_item.getMapId());
						  groundInventory.removeItem(temp_item);
						  break;
					  }
				  }
			  }
		  } catch (Exception e) {}
		  StringBuilder sb = new StringBuilder();
		  for (int i = 0; i < delItemlist.length; i++) {
			  sb.append(+delItemlist[i]);
			  if (i < delItemlist.length - 1) {
				  sb.append(",");
			  }
		  }
		  Delete(sb.toString());
	  } catch (Exception e) {
	  }
  }
	
  private static void Delete(String id_name) {
	  Connection con = null;
	  PreparedStatement pstm = null;
	  try {
		  con = L1DatabaseFactory.getInstance().getConnection();
		  pstm = con.prepareStatement("delete FROM _cha_inv_items WHERE item_id IN (" + id_name + ")");
		  pstm.executeUpdate();
	  } catch (Exception e) {
		  e.printStackTrace();
	  } finally {
		  SQLUtil.close(pstm);
	  }
	  try {
		  pstm = con.prepareStatement("delete FROM character_warehouse WHERE item_id in (" + id_name + ")");
		  pstm.executeUpdate();
	  } catch (Exception e) {
		  e.printStackTrace();
	  } finally {
		  SQLUtil.close(pstm);
	  }
	  try {
		  pstm = con.prepareStatement("delete FROM clan_warehouse WHERE item_id in (" + id_name + ")");
		  pstm.executeUpdate();
	  } catch (Exception e) {
		  e.printStackTrace();
	  } finally {
		  SQLUtil.close(pstm);
		  SQLUtil.close(con);
	  }
  }
	
	/** 終了 **/
  public void End() {
	  Config.ARNOLD_EVENTS = false;
	  delenpc(6);
	  delenpc(7);
	  delenpc(8);
	  deleteArnoldEvent();
	  L1World.getInstance().broadcastServerMessage("\\fSアーノルドイベントが終了しました。");
	  setCameBackArnoldEvent(false);
  }
  
}
	