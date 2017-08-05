package l1j.server.server.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.types.Point;

public class L1Spawn {
    private static Logger _log = Logger.getLogger(L1Spawn.class.getName());
    private final L1Npc _template;

    private int _id; // just to find this in the spawn table
    private String _location;
    private int _maximumCount;
    private int _npcid;
    private int _groupId;
    private int _locx;
    private int _locy;
    private int _randomx;
    private int _randomy;
    private int _locx1;
    private int _locy1;
    private int _locx2;
    private int _locy2;
    private int _heading;
    private int _minRespawnDelay;
    private int _maxRespawnDelay;
    private final Constructor<?> _constructor;
    private short _mapid;
    private boolean _respaenScreen;
    private int _movementDistance;
    private boolean _rest;
    private int _spawnType;
    private int _delayInterval;
    private HashMap<Integer, Point> _homePoint = null;

    private static Random _random = new Random(System.nanoTime());

    private String _name;

    private class SpawnTask implements Runnable {
        private int _spawnNumber;
        private int _objectId;

        private SpawnTask(int spawnNumber, int objectId) {
            _spawnNumber = spawnNumber;
            _objectId = objectId;
        }

        @Override
        public void run() {
            doSpawn(_spawnNumber, _objectId);
        }
    }

    public L1Spawn(L1Npc mobTemplate) throws SecurityException, ClassNotFoundException {
        _template = mobTemplate;
        String implementationName = _template.getImpl();
        _constructor = Class.forName("l1j.server.server.model.Instance." + implementationName + "Instance").getConstructors()[0];
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public short getMapId() {
        return _mapid;
    }

    public void setMapId(short _mapid) {
        this._mapid = _mapid;
    }

    public boolean isRespawnScreen() {
        return _respaenScreen;
    }

    public void setRespawnScreen(boolean flag) {
        _respaenScreen = flag;
    }

    public int getMovementDistance() {
        return _movementDistance;
    }

    public void setMovementDistance(int i) {
        _movementDistance = i;
    }

    public int getAmount() {
        return _maximumCount;
    }

    public int getGroupId() {
        return _groupId;
    }

    public int getId() {
        return _id;
    }

    public String getLocation() {
        return _location;
    }

    public int getLocX() {
        return _locx;
    }

    public int getLocY() {
        return _locy;
    }

    public int getNpcId() {
        return _npcid;
    }

    public int getHeading() {
        return _heading;
    }

    public int getRandomx() {
        return _randomx;
    }

    public int getRandomy() {
        return _randomy;
    }

    public int getLocX1() {
        return _locx1;
    }

    public int getLocY1() {
        return _locy1;
    }

    public int getLocX2() {
        return _locx2;
    }

    public int getLocY2() {
        return _locy2;
    }

    public int getMinRespawnDelay() {
        return _minRespawnDelay;
    }

    public int getMaxRespawnDelay() {
        return _maxRespawnDelay;
    }

    public void setAmount(int amount) {
        _maximumCount = amount;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setGroupId(int i) {
        _groupId = i;
    }

    public void setLocation(String location) {
        _location = location;
    }

    public void setLocX(int locx) {
        _locx = locx;
    }

    public void setLocY(int locy) {
        _locy = locy;
    }

    public void setNpcid(int npcid) {
        _npcid = npcid;
    }

    public void setHeading(int heading) {
        _heading = heading;
    }

    public void setRandomx(int randomx) {
        _randomx = randomx;
    }

    public void setRandomy(int randomy) {
        _randomy = randomy;
    }

    public void setLocX1(int locx1) {
        _locx1 = locx1;
    }

    public void setLocY1(int locy1) {
        _locy1 = locy1;
    }

    public void setLocX2(int locx2) {
        _locx2 = locx2;
    }

    public void setLocY2(int locy2) {
        _locy2 = locy2;
    }

    public void setMinRespawnDelay(int i) {
        _minRespawnDelay = i;
    }

    public void setMaxRespawnDelay(int i) {
        _maxRespawnDelay = i;
    }

    private int calcRespawnDelay() {
        int respawnDelay = _minRespawnDelay * 1000;
        if (_delayInterval > 0) {
            respawnDelay += _random.nextInt(_delayInterval) * 1000;
        }
        return respawnDelay;
    }

    public void executeSpawnTask(int spawnNumber, int objectId) {
        SpawnTask task = new SpawnTask(spawnNumber, objectId);
        GeneralThreadPool.getInstance().schedule(task, calcRespawnDelay());
    }

    private boolean _initSpawn = false;

    private boolean _spawnHomePoint;

    public void init() {
        _delayInterval = _maxRespawnDelay - _minRespawnDelay;
        _initSpawn = true;
        if (Config.SPAWN_HOME_POINT && Config.SPAWN_HOME_POINT_COUNT <= getAmount()
                && Config.SPAWN_HOME_POINT_DELAY >= getMinRespawnDelay() && isAreaSpawn()) {
            _spawnHomePoint = true;
            _homePoint = new HashMap<Integer, Point>();
        }

        int spawnNum = 0;
        while (spawnNum < _maximumCount) {
            // spawnNum1?maxmumCount
            doSpawn(++spawnNum);
        }
        _initSpawn = false;
    }

    protected void doSpawn(int spawnNumber) {
        doSpawn(spawnNumber, 0);
    }

    protected void doSpawn(int spawnNumber, int objectId) {
        L1NpcInstance mob = null;
        try {
            Object parameters[] = {_template};

            int newlocx = getLocX();
            int newlocy = getLocY();
            int tryCount = 0;

            mob = (L1NpcInstance) _constructor.newInstance(parameters);
            if (objectId == 0) {
                mob.setId(IdFactory.getInstance().nextId());
            } else {
                mob.setId(objectId);
            }

            if (0 <= getHeading() && getHeading() <= 7) {
                mob.setHeading(getHeading());
            } else {
                mob.setHeading(5);
            }

            int npcId = mob.getNpcTemplate().get_npcId();
            if (npcId == 45488 && getMapId() == 9) {
                mob.setMap((short) (getMapId() + _random.nextInt(2)));
            } else if (npcId == 45601 && getMapId() == 11) {
                mob.setMap((short) (getMapId() + _random.nextInt(3)));
            } else {
                mob.setMap(getMapId());
            }
            mob.setMovementDistance(getMovementDistance());
            mob.setRest(isRest());
            ArrayList<L1PcInstance> players = null;
            L1PcInstance pc = null;
            L1Location loc = null;
            Point pt = null;
            while (tryCount <= 50) {
                switch (getSpawnType()) {
                    case SPAWN_TYPE_PC_AROUND:
                        if (!_initSpawn) {
                            players = new ArrayList<L1PcInstance>();
                            for (L1PcInstance _pc : L1World.getInstance().getAllPlayers()) {
                                if (getMapId() == _pc.getMapId()) {
                                    players.add(_pc);
                                }
                            }
                            if (players.size() > 0) {
                                pc = players.get(_random.nextInt(players.size()));
                                loc = pc.getLocation().randomLocation(PC_AROUND_DISTANCE, false);
                                newlocx = loc.getX();
                                newlocy = loc.getY();
                                break;
                            }
                        }
                    default:
                        if (isAreaSpawn()) {
                            if (!_initSpawn && _spawnHomePoint) {
                                pt = _homePoint.get(spawnNumber);
                                loc = new L1Location(pt, getMapId()).randomLocation(Config.SPAWN_HOME_POINT_RANGE, false);
                                newlocx = loc.getX();
                                newlocy = loc.getY();
                            } else {
                                int rangeX = getLocX2() - getLocX1();
                                int rangeY = getLocY2() - getLocY1();
                                newlocx = _random.nextInt(rangeX) + getLocX1();
                                newlocy = _random.nextInt(rangeY) + getLocY1();
                            }
                            if (tryCount > 49) {
                                newlocx = getLocX();
                                newlocy = getLocY();
                            }
                        } else if (isRandomSpawn()) {
                            newlocx = (getLocX() + ((int) (Math.random() * getRandomx()) - (int) (Math.random() * getRandomx())));
                            newlocy = (getLocY() + ((int) (Math.random() * getRandomy()) - (int) (Math.random() * getRandomy())));
                        } else {
                            newlocx = getLocX();
                            newlocy = getLocY();
                        }
                }
                mob.setX(newlocx);
                mob.setHomeX(newlocx);
                mob.setY(newlocy);
                mob.setHomeY(newlocy);

                if (mob.getMap().isInMap(mob.getLocation())
                        && mob.getMap().isPassable(mob.getLocation())) {
                    if (mob instanceof L1MonsterInstance) {
                        if (isRespawnScreen()) {
                            break;
                        }
                        L1MonsterInstance mobtemp = (L1MonsterInstance) mob;
                        if (L1World.getInstance().getVisiblePlayer(mobtemp).size() == 0) {
                            break;
                        }
                        SpawnTask task = new SpawnTask(spawnNumber, mob.getId());
                        GeneralThreadPool.getInstance().schedule(task, 3000L);
                        return;
                    }
                }
                tryCount++;
            }

            if (mob instanceof L1MonsterInstance) {
                ((L1MonsterInstance) mob).initHide();
            }

            mob.setSpawn(this);
            mob.setRespawn(true);
            mob.setSpawnNumber(spawnNumber);
            if (_initSpawn && _spawnHomePoint) {
                pt = new Point(mob.getX(), mob.getY());
                _homePoint.put(spawnNumber, pt);
            }

            if (mob instanceof L1MonsterInstance) {
                if (mob.getMapId() == 666) {
                    ((L1MonsterInstance) mob).set_storeDroped(true);
                }
            }

            if (npcId == 45573 && mob.getMapId() == 2) {
                for (L1PcInstance _pc : L1World.getInstance().getAllPlayers()) {
                    if (_pc.getMapId() == 2) {
                        new L1Teleport().teleport(_pc, 32664, 32797, (short) 2, 0, true);
                    }
                }
            }

            if (npcId == 5095) { // 砂嵐60秒後に削除します。
                Broadcaster.broadcastPacket(mob, new S_DoActionGFX(mob.getId(), 4));
                L1NpcDeleteTimer timer = new L1NpcDeleteTimer(mob, 60 * 1000);
                timer.begin();
            }

            /** ボスモンスターコメント **/
            if (mob.getNpcId() == 14907 || mob.getNpcId() == 45752 || mob.getNpcId() == 81163
                    || mob.getNpcId() == 45684 || mob.getNpcId() == 900040 || mob.getNpcId() == 900013
                    || mob.getNpcId() == 5100 || mob.getNpcId() == 707026 || mob.getNpcId() == 45671
                    || mob.getNpcId() == 7310015 || mob.getNpcId() == 7310021 || mob.getNpcId() == 7310028
                    || mob.getNpcId() == 7310034 || mob.getNpcId() == 7310041 || mob.getNpcId() == 7310046
                    || mob.getNpcId() == 7310051 || mob.getNpcId() == 7310056 || mob.getNpcId() == 7310061
                    || mob.getNpcId() == 7310066 || mob.getNpcId() == 7310077
                    ) {
                // ボスモンスターコメント
                L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("今リネージュワールドに「" + mob.getName() + "」登場しました。"));
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "今リネージュワールドに「" + mob.getName() + "」登場しました。"));
                //ボスモンスターコメント
            }


            /** 大黒長老出現時の周辺にテレポート **/
            if (npcId == 7000094 && mob.getMapId() == 4) {
                for (L1PcInstance pc1 : L1World.getInstance().getVisiblePlayer(mob, 3)) {
                    new L1Teleport().teleport(pc1, mob.getX() + 5, mob.getY() + 5, (short) mob.getMapId(), pc1.getHeading(), true);
                }
            }
            /** 大黒長老出現時の周辺にテレポート **/


            if (Config.BOSS_SPAWN_OPERATION) {
                switch (npcId) {
                    case 7310015: // 歪みのゼニスクイーン
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔1階の歪みのゼニスクイーン出現難易度:★★☆☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setZenis(true);
                            player.sendPackets(new S_Message_YN(622, "ゼニスクイーンを懲罰しに行くか？難易度:★★☆☆☆"));
                        }
                        break;


                    case 7310021: // 不信のシアー
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔2階不信のシアー出現難易度:★★☆☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setSia(true);
                            player.sendPackets(new S_Message_YN(622, "シアバターを懲罰しに行くか？難易度：★★☆☆☆"));
                        }
                        break;


                    case 7310028: // 恐怖の吸血鬼
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔3階の恐怖の吸血鬼出現難易度：★★★☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setVampire(true);
                            player.sendPackets(new S_Message_YN(622, "吸血鬼を懲罰しに行くか？難易度：★★★☆☆"));
                        }
                        break;


                    case 7310034: // 死のゾンビロード
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔4階死のゾンビロード出現難易度:★★★☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setZombieLoad(true);
                            player.sendPackets(new S_Message_YN(622, "ゾンビにドール懲罰に行く行くか？難易度：★★★☆☆"));
                        }
                        break;

                    case 7310041: // 地獄のクーガー
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔5階地獄のクーガー出現難易度:★★★☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setCougar(true);
                            player.sendPackets(new S_Message_YN(622, "クーガーを懲罰しに行くか？難易度：★★★☆☆"));
                        }
                        break;


                    case 7310046: // 不死のマミーロード
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔6階不死のマミーロード出現難易度:★★★☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setMummyLord(true);
                            player.sendPackets(new S_Message_YN(622, "マミーロードを懲罰しに行くか？難易度：★★★☆☆"));
                        }
                        break;


                    case 7310051: // 残酷なアイリス
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔7階残酷アイリス出現難易度:★★★☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setIris(true);
                            player.sendPackets(new S_Message_YN(622, "アイリスを懲罰しに行くか？難易度：★★★☆☆"));
                        }
                        break;

                    case 7310056: // 闇のナイトバルド
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔8階闇のナイトバルド出現難易度:★★★☆☆ "));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setKnightBald(true);
                            player.sendPackets(new S_Message_YN(622, "ナイトバルドの懲罰に来可視か？難易度：★★★☆☆"));
                        }
                        break;
                    case 7310061: // 不滅のリッチ
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔9階不滅のリッチ出現難易度：★★★☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setLich(true);
                            player.sendPackets(new S_Message_YN(622, "リッチを懲罰しに行くか？難易度：★★★☆☆"));
                        }
                        break;
                    case 7310066: // 傲慢なオグヌス
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔10階、傲慢なオグヌス出現難易度:★★★☆☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setUgnus(true);
                            player.sendPackets(new S_Message_YN(622, "傲慢なオグヌスを懲罰しに行くか？難易度：★★★☆☆"));
                        }
                        break;

                    case 7310077: // グリムリーパー
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：傲慢の塔の上図のリッパー出現難易度:★★★★☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setGrimReaper(true);
                            player.sendPackets(new S_Message_YN(622, "図リポウル懲罰に行っ可視か？難易度：★★★★☆"));
                        }
                        break;
                    case 45752: //バルログ
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f=ボス通知：バルログが出現しました。：★★★★☆"));
                        for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
                            player.setBalrog(true);
                            player.sendPackets(new S_Message_YN(622, "バルログを懲罰しに行くか？難易度：★★★★★"));
                        }
                        break;
                }

            }


            L1World.getInstance().storeObject(mob);
            L1World.getInstance().addVisibleObject(mob);

            if (mob instanceof L1MonsterInstance) {
                L1MonsterInstance mobtemp = (L1MonsterInstance) mob;
                if (!_initSpawn && mobtemp.getHiddenStatus() == 0) {
                    mobtemp.onNpcAI();
                }
            }
            if (getGroupId() != 0) {
                L1MobGroupSpawn.getInstance().doSpawn(mob, getGroupId(), isRespawnScreen(), _initSpawn);
            }
            mob.getLight().turnOnOffLight();
            mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
        } catch (Exception e) {
            System.out.println("エンピシ名：" + mob.getNpcId());
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public void setRest(boolean flag) {
        _rest = flag;
    }

    public boolean isRest() {
        return _rest;
    }

    private static final int SPAWN_TYPE_PC_AROUND = 1;

    private static final int PC_AROUND_DISTANCE = 30;

    private int getSpawnType() {
        return _spawnType;
    }

    public void setSpawnType(int type) {
        _spawnType = type;
    }

    private boolean isAreaSpawn() {
        return getLocX1() != 0 && getLocY1() != 0 && getLocX2() != 0
                && getLocY2() != 0;
    }

    private boolean isRandomSpawn() {
        return getRandomx() != 0 || getRandomy() != 0;
    }
}
