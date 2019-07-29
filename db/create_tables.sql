
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for accessory_enchant_list
-- ----------------------------
DROP TABLE IF EXISTS `accessory_enchant_list`;
CREATE TABLE `accessory_enchant_list` (
  `item_id` int(10) NOT NULL,
  `name` varchar(45) NOT NULL,
  `chance` int(10) NOT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for accounts
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `login` varchar(50) NOT NULL DEFAULT '',
  `password` varchar(120) DEFAULT NULL,
  `lastactive` datetime DEFAULT NULL,
  `access_level` int(11) DEFAULT NULL,
  `ip` varchar(20) NOT NULL DEFAULT '',
  `host` varchar(255) NOT NULL DEFAULT '',
  `banned` int(11) unsigned NOT NULL DEFAULT '0',
  `charslot` int(11) NOT NULL,
  `gamepassword` int(11) NOT NULL,
  `notice` varchar(20) DEFAULT '0',
  `quiz` varchar(50) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `Tam_Point` int(20) DEFAULT '0',
  `tam` int(1) NOT NULL DEFAULT '0',
  `tamStep` tinyint(1) NOT NULL DEFAULT '0',
  `Buff_HPMP_Time` datetime DEFAULT NULL,
  `Buff_DMG_Time` datetime DEFAULT NULL,
  `Buff_Reduc_Time` datetime DEFAULT NULL,
  `Buff_Magic_Time` datetime DEFAULT NULL,
  `Buff_Stun_Time` datetime DEFAULT NULL,
  `Buff_Str_Time` datetime DEFAULT NULL,
  `Buff_Dex_Time` datetime DEFAULT NULL,
  `Buff_Int_Time` datetime DEFAULT NULL,
  `Buff_Hold_Time` datetime DEFAULT NULL,
  `BUFF_PCROOM_Time` datetime DEFAULT NULL,
  `CharPassword` varchar(11) DEFAULT NULL,
  `Ncoin_Point` int(20) DEFAULT '0',
  `Shop_open_count` int(20) DEFAULT '0',
  `DragonRaid_Buff` datetime DEFAULT NULL,
  PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for adshop
-- ----------------------------
DROP TABLE IF EXISTS `adshop`;
CREATE TABLE `adshop` (
  `account` varchar(13) NOT NULL,
  `name` varchar(13) NOT NULL,
  `sex` int(15) NOT NULL,
  `type` int(15) NOT NULL,
  `x` int(15) NOT NULL,
  `y` int(15) NOT NULL,
  `heading` int(15) NOT NULL,
  `map_id` int(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for area
-- ----------------------------
DROP TABLE IF EXISTS `area`;
CREATE TABLE `area` (
  `areaid` int(5) NOT NULL DEFAULT '0',
  `mapid` int(5) NOT NULL DEFAULT '0',
  `areaname` varchar(50) DEFAULT NULL,
  `x1` int(6) NOT NULL DEFAULT '0',
  `y1` int(6) NOT NULL DEFAULT '0',
  `x2` int(6) NOT NULL DEFAULT '0',
  `y2` int(6) NOT NULL DEFAULT '0',
  `flag` int(1) NOT NULL DEFAULT '0',
  `restart` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`areaid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for armor
-- ----------------------------
DROP TABLE IF EXISTS `armor`;
CREATE TABLE `armor` (
  `item_id` int(5) NOT NULL DEFAULT '0',
  `name` varchar(70) DEFAULT 'NULL',
  `name_id` varchar(45) NOT NULL DEFAULT '',
  `type` varchar(15) NOT NULL DEFAULT '',
  `acctype` int(2) NOT NULL,
  `grade` int(2) NOT NULL DEFAULT '0',
  `material` varchar(45) NOT NULL DEFAULT '',
  `weight` int(7) unsigned NOT NULL DEFAULT '0',
  `invgfx` int(5) unsigned NOT NULL DEFAULT '0',
  `grdgfx` int(5) unsigned NOT NULL DEFAULT '0',
  `itemdesc_id` int(10) NOT NULL DEFAULT '0',
  `ac` int(3) NOT NULL DEFAULT '0',
  `safenchant` int(2) NOT NULL DEFAULT '0',
  `use_royal` int(2) unsigned NOT NULL DEFAULT '0',
  `use_knight` int(2) unsigned NOT NULL DEFAULT '0',
  `use_mage` int(2) unsigned NOT NULL DEFAULT '0',
  `use_elf` int(2) unsigned NOT NULL DEFAULT '0',
  `use_darkelf` int(2) unsigned NOT NULL DEFAULT '0',
  `use_dragonknight` int(2) unsigned NOT NULL DEFAULT '0',
  `use_blackwizard` int(2) unsigned NOT NULL DEFAULT '0',
  `use_warrior` int(2) NOT NULL DEFAULT '0',
  `add_str` int(2) NOT NULL DEFAULT '0',
  `add_con` int(2) NOT NULL DEFAULT '0',
  `add_dex` int(2) NOT NULL DEFAULT '0',
  `add_int` int(2) NOT NULL DEFAULT '0',
  `add_wis` int(2) NOT NULL DEFAULT '0',
  `add_cha` int(2) NOT NULL DEFAULT '0',
  `add_hp` int(10) NOT NULL DEFAULT '0',
  `add_mp` int(10) NOT NULL DEFAULT '0',
  `add_hpr` int(10) NOT NULL DEFAULT '0',
  `add_mpr` int(10) NOT NULL DEFAULT '0',
  `add_sp` int(10) NOT NULL DEFAULT '0',
  `min_lvl` int(4) unsigned NOT NULL DEFAULT '0',
  `max_lvl` int(4) unsigned NOT NULL DEFAULT '0',
  `m_def` int(2) NOT NULL DEFAULT '0',
  `haste_item` int(2) unsigned NOT NULL DEFAULT '0',
  `damage_reduction` int(10) NOT NULL DEFAULT '0',
  `weight_reduction` int(10) unsigned NOT NULL DEFAULT '0',
  `hit_rate` int(10) NOT NULL DEFAULT '0',
  `dmg_rate` int(10) NOT NULL DEFAULT '0',
  `bow_hit_rate` int(10) NOT NULL DEFAULT '0',
  `bow_dmg_rate` int(10) NOT NULL DEFAULT '0',
  `bless` int(2) unsigned NOT NULL DEFAULT '1',
  `trade` int(2) unsigned NOT NULL DEFAULT '0',
  `cant_delete` int(2) unsigned NOT NULL DEFAULT '0',
  `max_use_time` int(10) NOT NULL DEFAULT '0',
  `defense_water` int(2) NOT NULL DEFAULT '0',
  `defense_wind` int(2) NOT NULL DEFAULT '0',
  `defense_fire` int(2) NOT NULL DEFAULT '0',
  `defense_earth` int(2) NOT NULL DEFAULT '0',
  `regist_stun` int(2) NOT NULL DEFAULT '0',
  `regist_stone` int(2) NOT NULL DEFAULT '0',
  `regist_sleep` int(2) NOT NULL DEFAULT '0',
  `regist_freeze` int(2) NOT NULL DEFAULT '0',
  `regist_sustain` int(2) NOT NULL DEFAULT '0',
  `regist_blind` int(2) NOT NULL DEFAULT '0',
  `regist_DESPERADO` int(2) NOT NULL DEFAULT '0',
  `PVPcalcPcDefense` int(2) NOT NULL DEFAULT '0',
  `PVPweaponTotalDamage` int(2) NOT NULL DEFAULT '0',
  `MainId` int(10) NOT NULL DEFAULT '0',
  `MainId2` int(10) NOT NULL DEFAULT '0',
  `MainId3` int(10) NOT NULL DEFAULT '0',
  `Set_Id` int(10) NOT NULL DEFAULT '0',
  `Magic_name` varchar(20) DEFAULT NULL,
  `ignore_reduction_by_armor` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for armor_enchant_list
-- ----------------------------
DROP TABLE IF EXISTS `armor_enchant_list`;
CREATE TABLE `armor_enchant_list` (
  `item_id` int(10) NOT NULL,
  `name` varchar(45) NOT NULL,
  `chance` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for armor_set
-- ----------------------------
DROP TABLE IF EXISTS `armor_set`;
CREATE TABLE `armor_set` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `note` varchar(45) DEFAULT NULL,
  `sets` varchar(1000) NOT NULL,
  `polyid` int(10) NOT NULL DEFAULT '0',
  `ac` int(2) NOT NULL DEFAULT '0',
  `hp` int(5) NOT NULL DEFAULT '0',
  `mp` int(5) NOT NULL DEFAULT '0',
  `hpr` int(5) NOT NULL DEFAULT '0',
  `mpr` int(5) NOT NULL DEFAULT '0',
  `mr` int(5) NOT NULL DEFAULT '0',
  `str` int(2) NOT NULL DEFAULT '0',
  `dex` int(2) NOT NULL DEFAULT '0',
  `con` int(2) NOT NULL DEFAULT '0',
  `wis` int(2) NOT NULL DEFAULT '0',
  `cha` int(2) NOT NULL DEFAULT '0',
  `intl` int(2) NOT NULL DEFAULT '0',
  `sp` int(2) NOT NULL DEFAULT '0',
  `shorthitup` int(2) NOT NULL DEFAULT '0',
  `shortdmgup` int(2) NOT NULL DEFAULT '0',
  `longhitup` int(2) NOT NULL DEFAULT '0',
  `longdmgup` int(2) NOT NULL DEFAULT '0',
  `earth` int(10) NOT NULL DEFAULT '0',
  `fire` int(10) NOT NULL DEFAULT '0',
  `wind` int(10) NOT NULL DEFAULT '0',
  `water` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=153 DEFAULT CHARSET=utf8 COMMENT='MyISAM free: 10240 kB';

-- ----------------------------
-- Table structure for attendance
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `day` int(10) unsigned NOT NULL DEFAULT '0',
  `itemid` int(10) unsigned NOT NULL DEFAULT '0',
  `count` int(10) NOT NULL DEFAULT '0',
  `itemid_pcbang` int(10) unsigned NOT NULL DEFAULT '0',
  `count_pcbang` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`day`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for attendanceaccount
-- ----------------------------
DROP TABLE IF EXISTS `attendanceaccount`;
CREATE TABLE `attendanceaccount` (
  `account_name` varchar(13) NOT NULL DEFAULT '0',
  `day` int(10) NOT NULL DEFAULT '0',
  `time` int(10) NOT NULL DEFAULT '0',
  `clear` varchar(255) NOT NULL DEFAULT '',
  `day_pc` int(10) NOT NULL DEFAULT '0',
  `time_pc` int(10) NOT NULL DEFAULT '0',
  `clear_pc` varchar(255) NOT NULL DEFAULT '',
  `laste_check_day` int(10) NOT NULL DEFAULT '0',
  `laste_check_year` int(10) NOT NULL,
  PRIMARY KEY (`account_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for autoloot
-- ----------------------------
DROP TABLE IF EXISTS `autoloot`;
CREATE TABLE `autoloot` (
  `item_id` int(11) NOT NULL DEFAULT '0',
  `note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for autoshopbuyitemlist
-- ----------------------------
DROP TABLE IF EXISTS `autoshopbuyitemlist`;
CREATE TABLE `autoshopbuyitemlist` (
  `item_id` int(10) NOT NULL,
  `name` varchar(40) NOT NULL,
  `bless` tinyint(1) NOT NULL DEFAULT '0',
  `enchantlvl` tinyint(3) NOT NULL DEFAULT '0',
  `attr_enchantlvl` tinyint(3) NOT NULL DEFAULT '0',
  `price` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`,`name`,`bless`,`enchantlvl`,`attr_enchantlvl`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ban_ip
-- ----------------------------
DROP TABLE IF EXISTS `ban_ip`;
CREATE TABLE `ban_ip` (
  `ip` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for beginner
-- ----------------------------
DROP TABLE IF EXISTS `beginner`;
CREATE TABLE `beginner` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `item_id` int(6) NOT NULL DEFAULT '0',
  `count` int(10) NOT NULL DEFAULT '0',
  `charge_count` int(10) NOT NULL DEFAULT '0',
  `enchantlvl` int(6) NOT NULL DEFAULT '0',
  `item_name` varchar(50) NOT NULL DEFAULT '',
  `activate` char(1) NOT NULL DEFAULT 'A',
  `special_enchant` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for beginner_addteleport
-- ----------------------------
DROP TABLE IF EXISTS `beginner_addteleport`;
CREATE TABLE `beginner_addteleport` (
  `id` int(10) unsigned NOT NULL,
  `num_id` int(10) unsigned NOT NULL DEFAULT '0',
  `speed_id` int(10) NOT NULL DEFAULT '-1',
  `char_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL DEFAULT '',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `randomX` int(10) unsigned NOT NULL DEFAULT '0',
  `randomY` int(10) unsigned NOT NULL DEFAULT '0',
  `item_obj_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`char_id`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for beginner_teleport
-- ----------------------------
DROP TABLE IF EXISTS `beginner_teleport`;
CREATE TABLE `beginner_teleport` (
  `name` varchar(45) NOT NULL DEFAULT '',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for beginner_testitem
-- ----------------------------
DROP TABLE IF EXISTS `beginner_testitem`;
CREATE TABLE `beginner_testitem` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `item_id` int(6) NOT NULL DEFAULT '0',
  `count` int(10) NOT NULL DEFAULT '0',
  `enchantlvl` int(6) NOT NULL DEFAULT '0',
  `item_name` varchar(50) NOT NULL DEFAULT '',
  `activate` char(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=369 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board
-- ----------------------------
DROP TABLE IF EXISTS `board`;
CREATE TABLE `board` (
  `id` int(10) NOT NULL DEFAULT '0',
  `name` varchar(16) DEFAULT NULL,
  `date` varchar(16) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `board_id` int(10) NOT NULL DEFAULT '0',
  `remaining_time` datetime DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board_auction
-- ----------------------------
DROP TABLE IF EXISTS `board_auction`;
CREATE TABLE `board_auction` (
  `house_id` int(10) unsigned NOT NULL DEFAULT '0',
  `house_name` varchar(45) NOT NULL DEFAULT '',
  `house_area` int(10) unsigned NOT NULL DEFAULT '0',
  `deadline` datetime DEFAULT NULL,
  `price` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(45) NOT NULL DEFAULT '',
  `old_owner` varchar(45) NOT NULL DEFAULT '',
  `old_owner_id` int(10) unsigned NOT NULL DEFAULT '0',
  `bidder` varchar(45) NOT NULL DEFAULT '',
  `bidder_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`house_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board_free
-- ----------------------------
DROP TABLE IF EXISTS `board_free`;
CREATE TABLE `board_free` (
  `id` int(10) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `date` varchar(16) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board_notice
-- ----------------------------
DROP TABLE IF EXISTS `board_notice`;
CREATE TABLE `board_notice` (
  `id` int(10) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `date` varchar(16) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board_notice1
-- ----------------------------
DROP TABLE IF EXISTS `board_notice1`;
CREATE TABLE `board_notice1` (
  `id` int(10) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `date` varchar(16) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board_notice2
-- ----------------------------
DROP TABLE IF EXISTS `board_notice2`;
CREATE TABLE `board_notice2` (
  `id` int(10) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `date` varchar(16) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board_notice3
-- ----------------------------
DROP TABLE IF EXISTS `board_notice3`;
CREATE TABLE `board_notice3` (
  `id` int(10) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `date` varchar(16) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board_posts_fix
-- ----------------------------
DROP TABLE IF EXISTS `board_posts_fix`;
CREATE TABLE `board_posts_fix` (
  `id` int(10) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `date` varchar(16) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for board_posts_key
-- ----------------------------
DROP TABLE IF EXISTS `board_posts_key`;
CREATE TABLE `board_posts_key` (
  `id` int(10) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `date` varchar(16) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for castle
-- ----------------------------
DROP TABLE IF EXISTS `castle`;
CREATE TABLE `castle` (
  `castle_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL DEFAULT '',
  `war_time` datetime DEFAULT NULL,
  `tax_rate` int(11) NOT NULL DEFAULT '0',
  `public_money` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`castle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for characters
-- ----------------------------
DROP TABLE IF EXISTS `characters`;
CREATE TABLE `characters` (
  `account_name` varchar(13) NOT NULL DEFAULT '0',
  `objid` int(11) unsigned NOT NULL DEFAULT '0',
  `char_name` varchar(45) NOT NULL DEFAULT '',
  `level` int(11) unsigned NOT NULL DEFAULT '0',
  `HighLevel` int(11) unsigned NOT NULL DEFAULT '0',
  `Exp` int(20) unsigned NOT NULL DEFAULT '0',
  `MaxHp` int(10) unsigned NOT NULL DEFAULT '0',
  `CurHp` int(10) unsigned NOT NULL DEFAULT '0',
  `MaxMp` int(10) NOT NULL DEFAULT '0',
  `CurMp` int(10) NOT NULL DEFAULT '0',
  `Ac` int(10) NOT NULL DEFAULT '0',
  `Str` int(3) NOT NULL DEFAULT '0',
  `BaseStr` int(3) NOT NULL DEFAULT '0',
  `Con` int(3) NOT NULL DEFAULT '0',
  `BaseCon` int(3) NOT NULL DEFAULT '0',
  `Dex` int(3) NOT NULL DEFAULT '0',
  `BaseDex` int(3) NOT NULL DEFAULT '0',
  `Cha` int(3) NOT NULL DEFAULT '0',
  `BaseCha` int(3) NOT NULL DEFAULT '0',
  `Intel` int(3) NOT NULL DEFAULT '0',
  `BaseIntel` int(3) NOT NULL DEFAULT '0',
  `Wis` int(3) NOT NULL DEFAULT '0',
  `BaseWis` int(3) NOT NULL DEFAULT '0',
  `Status` int(10) unsigned NOT NULL DEFAULT '0',
  `Class` int(10) unsigned NOT NULL DEFAULT '0',
  `Sex` int(10) unsigned NOT NULL DEFAULT '0',
  `Type` int(10) unsigned NOT NULL DEFAULT '0',
  `Heading` int(10) unsigned NOT NULL DEFAULT '0',
  `LocX` int(11) unsigned NOT NULL DEFAULT '0',
  `LocY` int(11) unsigned NOT NULL DEFAULT '0',
  `MapID` int(10) unsigned NOT NULL DEFAULT '0',
  `Food` int(10) unsigned NOT NULL DEFAULT '0',
  `Lawful` int(10) NOT NULL DEFAULT '0',
  `Title` varchar(35) NOT NULL DEFAULT '',
  `ClanID` int(10) unsigned NOT NULL DEFAULT '0',
  `Clanname` varchar(45) NOT NULL,
  `ClanRank` int(3) NOT NULL DEFAULT '0',
  `notes` varchar(60) NOT NULL,
  `BonusStatus` int(10) NOT NULL DEFAULT '0',
  `ElixirStatus` int(10) NOT NULL DEFAULT '0',
  `ElfAttr` int(10) NOT NULL DEFAULT '0',
  `PKcount` int(10) NOT NULL DEFAULT '0',
  `ExpRes` int(10) NOT NULL DEFAULT '0',
  `PartnerID` int(10) NOT NULL DEFAULT '0',
  `AccessLevel` int(10) unsigned NOT NULL DEFAULT '0',
  `OnlineStatus` int(10) unsigned NOT NULL DEFAULT '0',
  `HomeTownID` int(10) NOT NULL DEFAULT '0',
  `Contribution` int(10) NOT NULL DEFAULT '0',
  `Pay` int(10) NOT NULL DEFAULT '0',
  `HellTime` int(10) unsigned NOT NULL DEFAULT '0',
  `Banned` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `Karma` int(10) NOT NULL DEFAULT '0',
  `LastPk` datetime DEFAULT NULL,
  `DeleteTime` datetime DEFAULT NULL,
  `ReturnStat` int(10) NOT NULL,
  `sealingPW` varchar(10) DEFAULT NULL,
  `sealScrollTime` int(11) NOT NULL DEFAULT '0',
  `sealScrollCount` int(11) NOT NULL DEFAULT '0',
  `lastLoginTime` datetime DEFAULT NULL,
  `lastLogoutTime` datetime DEFAULT NULL,
  `einhasad` int(11) NOT NULL DEFAULT '0',
  `AinState` int(1) NOT NULL DEFAULT '0',
  `SurvivalGauge` int(1) NOT NULL DEFAULT '30',
  `BirthDay` int(11) DEFAULT NULL,
  `PC_Kill` int(10) DEFAULT NULL,
  `PC_Death` int(10) DEFAULT NULL,
  `GiranTime` int(10) NOT NULL DEFAULT '0',
  `OrenTime` int(10) NOT NULL DEFAULT '0',
  `DrageonTime` int(10) NOT NULL DEFAULT '0',
  `RadungeonTime` int(10) NOT NULL DEFAULT '0',
  `SomeTime` int(10) NOT NULL DEFAULT '0',
  `SoulTime` int(10) NOT NULL DEFAULT '0',
  `newdoTime` int(10) NOT NULL DEFAULT '0',
  `iceTime` int(10) NOT NULL DEFAULT '0',
  `islanddungeonTime` int(10) NOT NULL DEFAULT '0',
  `Mark_Count` int(10) NOT NULL DEFAULT '60',
  `Age` int(2) DEFAULT '0',
  `AddDamage` int(3) DEFAULT '0',
  `AddDamageRate` int(3) DEFAULT '0',
  `AddReduction` int(3) DEFAULT '0',
  `AddReductionRate` int(3) DEFAULT '0',
  `IsPeerage` int(10) NOT NULL DEFAULT '0',
  `Abysspoint` int(10) unsigned DEFAULT '0',
  `TamEndTime` datetime DEFAULT NULL,
  `SpecialSize` int(10) NOT NULL DEFAULT '0',
  `FishingShopBuyTime_1` timestamp NULL DEFAULT NULL COMMENT '영양만점고기 미끼에 대한 시간값 확인용.\r\n상점에서 구입하면 해당값이 구입한 시간으로 기록됨.',
  `HuntPrice` int(10) DEFAULT NULL,
  `HuntText` varchar(30) DEFAULT NULL,
  `HuntCount` int(10) DEFAULT NULL,
  `Clan_Join_Date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`objid`),
  KEY `key_id` (`account_name`,`char_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for characters_inventory_set
-- ----------------------------
DROP TABLE IF EXISTS `characters_inventory_set`;
CREATE TABLE `characters_inventory_set` (
  `objectId` int(10) NOT NULL DEFAULT '0',
  `setCode` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `set1` varchar(255) NOT NULL DEFAULT '',
  `set2` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`objectId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_balance
-- ----------------------------
DROP TABLE IF EXISTS `character_balance`;
CREATE TABLE `character_balance` (
  `id` int(10) NOT NULL DEFAULT '0',
  `class_title` varchar(20) DEFAULT '',
  `addDmg` int(10) DEFAULT NULL,
  `addHitRate` int(10) DEFAULT NULL,
  `addReduction` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_buddys
-- ----------------------------
DROP TABLE IF EXISTS `character_buddys`;
CREATE TABLE `character_buddys` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_id` int(10) NOT NULL DEFAULT '0',
  `buddy_id` int(10) unsigned NOT NULL DEFAULT '0',
  `buddy_name` varchar(45) NOT NULL,
  PRIMARY KEY (`char_id`,`buddy_id`),
  KEY `key_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_buff
-- ----------------------------
DROP TABLE IF EXISTS `character_buff`;
CREATE TABLE `character_buff` (
  `char_obj_id` int(10) NOT NULL DEFAULT '0',
  `skill_id` int(10) unsigned NOT NULL DEFAULT '0',
  `remaining_time` int(10) NOT NULL DEFAULT '0',
  `poly_id` int(10) DEFAULT '0',
  PRIMARY KEY (`char_obj_id`,`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_config
-- ----------------------------
DROP TABLE IF EXISTS `character_config`;
CREATE TABLE `character_config` (
  `object_id` int(10) NOT NULL DEFAULT '0',
  `length` int(10) unsigned NOT NULL DEFAULT '0',
  `data` blob,
  PRIMARY KEY (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_elf_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `character_elf_warehouse`;
CREATE TABLE `character_elf_warehouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(13) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `is_equipped` int(11) DEFAULT NULL,
  `enchantlvl` int(11) DEFAULT NULL,
  `is_id` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `charge_count` int(11) DEFAULT NULL,
  `remaining_time` int(11) DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `attr_enchantlvl` int(11) DEFAULT NULL,
  `special_enchant` int(11) DEFAULT NULL,
  `bless` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`account_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_exclude
-- ----------------------------
DROP TABLE IF EXISTS `character_exclude`;
CREATE TABLE `character_exclude` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_id` int(10) NOT NULL DEFAULT '0',
  `type` int(2) NOT NULL DEFAULT '0',
  `exclude_id` int(10) unsigned NOT NULL DEFAULT '0',
  `exclude_name` varchar(45) NOT NULL,
  PRIMARY KEY (`char_id`,`type`,`exclude_id`),
  KEY `key_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='MyISAM free: 10240 kB; MyISAM free: 10240 kB';

-- ----------------------------
-- Table structure for character_fairly_config
-- ----------------------------
DROP TABLE IF EXISTS `character_fairly_config`;
CREATE TABLE `character_fairly_config` (
  `object_id` int(10) NOT NULL,
  `data` blob,
  PRIMARY KEY (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_items
-- ----------------------------
DROP TABLE IF EXISTS `character_items`;
CREATE TABLE `character_items` (
  `id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) DEFAULT NULL,
  `char_id` int(11) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `is_equipped` int(11) DEFAULT NULL,
  `enchantlvl` int(11) DEFAULT NULL,
  `is_id` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `charge_count` int(11) DEFAULT NULL,
  `remaining_time` int(11) DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `bless` int(11) DEFAULT NULL,
  `attr_enchantlvl` int(11) DEFAULT NULL,
  `special_enchant` int(11) DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `package` tinyint(3) DEFAULT '0',
  `buy_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `key_id` (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_monsterbooklist
-- ----------------------------
DROP TABLE IF EXISTS `character_monsterbooklist`;
CREATE TABLE `character_monsterbooklist` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `monsterlist` text NOT NULL,
  `monquest` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=909504554 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_package_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `character_package_warehouse`;
CREATE TABLE `character_package_warehouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(13) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `is_equipped` int(11) DEFAULT NULL,
  `enchantlvl` int(11) DEFAULT NULL,
  `is_id` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `charge_count` int(11) DEFAULT NULL,
  `remaining_time` int(11) DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `attr_enchantlvl` int(11) DEFAULT NULL,
  `special_enchant` int(11) DEFAULT NULL,
  `bless` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`account_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_present_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `character_present_warehouse`;
CREATE TABLE `character_present_warehouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(13) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `is_equipped` int(11) DEFAULT NULL,
  `enchantlvl` int(11) DEFAULT NULL,
  `is_id` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `charge_count` int(11) DEFAULT NULL,
  `remaining_time` int(11) DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `attr_enchantlvl` int(11) DEFAULT NULL,
  `special_enchant` int(11) DEFAULT NULL,
  `bless` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`account_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_quests
-- ----------------------------
DROP TABLE IF EXISTS `character_quests`;
CREATE TABLE `character_quests` (
  `char_id` int(10) unsigned NOT NULL,
  `quest_id` int(10) unsigned NOT NULL DEFAULT '0',
  `quest_step` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`char_id`,`quest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_rank
-- ----------------------------
DROP TABLE IF EXISTS `character_rank`;
CREATE TABLE `character_rank` (
  `char_id` int(10) unsigned NOT NULL,
  `account_name` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `exp` int(10) unsigned NOT NULL DEFAULT '0',
  `total_rank` int(10) unsigned NOT NULL DEFAULT '1601',
  `old_total_rank` int(10) unsigned NOT NULL DEFAULT '1601',
  `class_rank` int(10) unsigned NOT NULL DEFAULT '201',
  `old_class_rank` int(10) unsigned NOT NULL DEFAULT '201',
  `total_step_up` tinyint(1) NOT NULL DEFAULT '0',
  `total_step_down` tinyint(1) NOT NULL DEFAULT '0',
  `class_step_up` tinyint(1) NOT NULL DEFAULT '0',
  `class_step_down` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_restoreitem
-- ----------------------------
DROP TABLE IF EXISTS `character_restoreitem`;
CREATE TABLE `character_restoreitem` (
  `objid` int(255) NOT NULL DEFAULT '0',
  `itemid` int(255) DEFAULT NULL,
  `enchantLevel` int(255) DEFAULT NULL,
  `attrenchantLevel` int(255) DEFAULT NULL,
  `bless` int(255) DEFAULT NULL,
  PRIMARY KEY (`objid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_shop
-- ----------------------------
DROP TABLE IF EXISTS `character_shop`;
CREATE TABLE `character_shop` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `obj_id` int(10) NOT NULL DEFAULT '0',
  `char_name` varchar(50) NOT NULL DEFAULT '0',
  `item_objid` int(10) NOT NULL DEFAULT '0',
  `item_id` int(10) NOT NULL DEFAULT '0',
  `Item_name` varchar(50) NOT NULL DEFAULT '0',
  `count` int(10) NOT NULL DEFAULT '0',
  `enchant` int(10) NOT NULL DEFAULT '0',
  `price` int(10) NOT NULL DEFAULT '0',
  `type` int(10) NOT NULL DEFAULT '0',
  `locx` int(10) NOT NULL DEFAULT '0',
  `locy` int(10) NOT NULL DEFAULT '0',
  `locm` int(10) NOT NULL DEFAULT '0',
  `iden` int(10) NOT NULL DEFAULT '1',
  `attr` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_skills
-- ----------------------------
DROP TABLE IF EXISTS `character_skills`;
CREATE TABLE `character_skills` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_obj_id` int(10) NOT NULL DEFAULT '0',
  `skill_id` int(10) unsigned NOT NULL DEFAULT '0',
  `skill_name` varchar(45) NOT NULL DEFAULT '',
  `is_active` int(10) DEFAULT NULL,
  `activetimeleft` int(10) DEFAULT NULL,
  PRIMARY KEY (`char_obj_id`,`skill_id`),
  KEY `key_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=895 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_special_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `character_special_warehouse`;
CREATE TABLE `character_special_warehouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(13) DEFAULT '',
  `item_id` int(11) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `is_equipped` int(11) DEFAULT NULL,
  `enchantlvl` int(11) DEFAULT NULL,
  `is_id` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `charge_count` int(11) DEFAULT NULL,
  `remaining_time` int(11) DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `attr_enchantlvl` int(11) DEFAULT NULL,
  `bless` int(11) DEFAULT '0',
  `second_id` int(11) DEFAULT NULL,
  `round_id` int(11) DEFAULT NULL,
  `ticket_id` int(11) DEFAULT NULL,
  `maan_time` datetime DEFAULT NULL,
  `regist_level` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `key_id` (`account_name`)
) ENGINE=MyISAM AUTO_INCREMENT=379026641 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_teleport
-- ----------------------------
DROP TABLE IF EXISTS `character_teleport`;
CREATE TABLE `character_teleport` (
  `id` int(10) unsigned NOT NULL,
  `num_id` int(10) unsigned NOT NULL DEFAULT '0',
  `speed_id` int(10) NOT NULL DEFAULT '-1',
  `char_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL DEFAULT '',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `randomX` int(10) unsigned NOT NULL DEFAULT '0',
  `randomY` int(10) unsigned NOT NULL DEFAULT '0',
  `item_obj_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`char_id`)
) ENGINE=MyISAM AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `character_warehouse`;
CREATE TABLE `character_warehouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(13) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `is_equipped` int(11) DEFAULT NULL,
  `enchantlvl` int(11) DEFAULT NULL,
  `is_id` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `charge_count` int(11) DEFAULT NULL,
  `remaining_time` int(11) DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `attr_enchantlvl` int(11) DEFAULT NULL,
  `bless` int(11) DEFAULT '0',
  `special_enchant` int(11) DEFAULT NULL,
  `package` tinyint(3) DEFAULT '0',
  `buy_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `key_id` (`account_name`)
) ENGINE=InnoDB AUTO_INCREMENT=909636087 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for character_weekquest
-- ----------------------------
DROP TABLE IF EXISTS `character_weekquest`;
CREATE TABLE `character_weekquest` (
  `char_name` varchar(100) NOT NULL,
  `quest_number_1` int(10) DEFAULT NULL,
  `quest_number_2` int(10) DEFAULT NULL,
  `quest_number_3` int(10) DEFAULT NULL,
  `quest_number_4` int(10) DEFAULT NULL,
  `quest_number_5` int(10) DEFAULT NULL,
  `quest_number_6` int(10) DEFAULT NULL,
  `quest_number_7` int(10) DEFAULT NULL,
  `quest_number_8` int(10) DEFAULT '0',
  `quest_number_9` int(10) DEFAULT '0',
  `quest_week` int(10) DEFAULT '0',
  `lineclear1` int(10) DEFAULT NULL,
  `lineclear2` int(10) DEFAULT NULL,
  `lineclear3` int(10) DEFAULT NULL,
  PRIMARY KEY (`char_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clan_data
-- ----------------------------
DROP TABLE IF EXISTS `clan_data`;
CREATE TABLE `clan_data` (
  `clan_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `clan_name` varchar(45) NOT NULL DEFAULT '',
  `leader_id` int(10) unsigned NOT NULL DEFAULT '0',
  `leader_name` varchar(45) NOT NULL DEFAULT '',
  `hascastle` int(10) unsigned NOT NULL DEFAULT '0',
  `hashouse` int(10) unsigned NOT NULL DEFAULT '0',
  `alliance` int(10) NOT NULL DEFAULT '0',
  `clan_birthday` datetime NOT NULL,
  `bot` enum('true','false') DEFAULT 'false',
  `bot_style` tinyint(3) DEFAULT '0',
  `bot_level` tinyint(3) DEFAULT '0',
  `max_online_user` int(10) DEFAULT NULL,
  `announcement` varchar(160) NOT NULL,
  `emblem_id` int(10) NOT NULL DEFAULT '0',
  `emblem_status` tinyint(1) NOT NULL DEFAULT '0',
  `clan_exp` int(10) DEFAULT '0',
  `bless` int(45) NOT NULL DEFAULT '0',
  `bless_count` int(45) NOT NULL DEFAULT '0',
  `attack` int(45) NOT NULL DEFAULT '0',
  `defence` int(45) NOT NULL DEFAULT '0',
  `pvpattack` int(45) NOT NULL DEFAULT '0',
  `pvpdefence` int(45) NOT NULL DEFAULT '0',
  `under_dungeon` tinyint(3) NOT NULL DEFAULT '0',
  `ranktime` int(10) NOT NULL DEFAULT '0',
  `rankdate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`clan_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2000000009 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clan_matching_apclist
-- ----------------------------
DROP TABLE IF EXISTS `clan_matching_apclist`;
CREATE TABLE `clan_matching_apclist` (
  `pc_name` varchar(45) NOT NULL DEFAULT '',
  `pc_objid` int(10) DEFAULT NULL,
  `clan_name` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clan_matching_list
-- ----------------------------
DROP TABLE IF EXISTS `clan_matching_list`;
CREATE TABLE `clan_matching_list` (
  `clanname` varchar(45) NOT NULL DEFAULT '',
  `text` varchar(500) DEFAULT NULL,
  `type` int(10) DEFAULT NULL,
  PRIMARY KEY (`clanname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clan_members
-- ----------------------------
DROP TABLE IF EXISTS `clan_members`;
CREATE TABLE `clan_members` (
  `clan_id` int(10) NOT NULL,
  `index_id` int(10) NOT NULL,
  `leader_id` int(10) NOT NULL,
  `leader_name` varchar(45) NOT NULL DEFAULT '',
  `note` varchar(45) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clan_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `clan_warehouse`;
CREATE TABLE `clan_warehouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `clan_name` varchar(45) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `is_equipped` int(11) DEFAULT NULL,
  `enchantlvl` int(11) DEFAULT NULL,
  `is_id` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `charge_count` int(11) DEFAULT NULL,
  `remaining_time` int(11) DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `attr_enchantlvl` int(11) DEFAULT NULL,
  `special_enchant` int(11) DEFAULT NULL,
  `package` tinyint(3) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`clan_name`)
) ENGINE=InnoDB AUTO_INCREMENT=906868709 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clan_warehousehistory
-- ----------------------------
DROP TABLE IF EXISTS `clan_warehousehistory`;
CREATE TABLE `clan_warehousehistory` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `clan_id` int(10) NOT NULL DEFAULT '0',
  `char_name` varchar(45) NOT NULL,
  `item_name` varchar(45) NOT NULL,
  `item_count` int(10) DEFAULT NULL,
  `elapsed_time` int(10) DEFAULT NULL,
  `item_getorput` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=907170274 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clan_warehouse_list
-- ----------------------------
DROP TABLE IF EXISTS `clan_warehouse_list`;
CREATE TABLE `clan_warehouse_list` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `clanid` int(11) DEFAULT '0',
  `list` varchar(200) DEFAULT '',
  `date` varchar(20) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for clan_warehouse_log
-- ----------------------------
DROP TABLE IF EXISTS `clan_warehouse_log`;
CREATE TABLE `clan_warehouse_log` (
  `id` int(1) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL DEFAULT '',
  `clan_name` varchar(30) NOT NULL DEFAULT '',
  `item_name` varchar(30) NOT NULL DEFAULT '',
  `item_count` int(1) unsigned NOT NULL,
  `type` bit(1) NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for commands
-- ----------------------------
DROP TABLE IF EXISTS `commands`;
CREATE TABLE `commands` (
  `name` varchar(255) NOT NULL,
  `access_level` int(10) NOT NULL DEFAULT '9999',
  `class_name` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for droplist
-- ----------------------------
DROP TABLE IF EXISTS `droplist`;
CREATE TABLE `droplist` (
  `mobId` int(6) NOT NULL DEFAULT '0',
  `mobname` varchar(100) NOT NULL,
  `moblevel` int(10) NOT NULL DEFAULT '0',
  `itemId` int(6) NOT NULL DEFAULT '0',
  `itemname` varchar(50) NOT NULL,
  `min` int(4) NOT NULL DEFAULT '0',
  `max` int(4) NOT NULL DEFAULT '0',
  `chance` int(8) NOT NULL DEFAULT '0',
  `Enchant` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`mobId`,`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for drop_item
-- ----------------------------
DROP TABLE IF EXISTS `drop_item`;
CREATE TABLE `drop_item` (
  `item_id` int(10) NOT NULL DEFAULT '0',
  `drop_rate` float unsigned NOT NULL DEFAULT '0',
  `drop_amount` float unsigned NOT NULL DEFAULT '0',
  `note` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dungeon
-- ----------------------------
DROP TABLE IF EXISTS `dungeon`;
CREATE TABLE `dungeon` (
  `src_x` int(10) NOT NULL DEFAULT '0',
  `src_y` int(10) NOT NULL DEFAULT '0',
  `src_mapid` int(10) NOT NULL DEFAULT '0',
  `new_x` int(10) NOT NULL DEFAULT '0',
  `new_y` int(10) NOT NULL DEFAULT '0',
  `new_mapid` int(10) NOT NULL DEFAULT '0',
  `new_heading` int(10) NOT NULL DEFAULT '1',
  `note` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`src_x`,`src_y`,`src_mapid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dungeon_random
-- ----------------------------
DROP TABLE IF EXISTS `dungeon_random`;
CREATE TABLE `dungeon_random` (
  `src_x` int(10) NOT NULL DEFAULT '0',
  `src_y` int(10) NOT NULL DEFAULT '0',
  `src_mapid` int(10) NOT NULL DEFAULT '0',
  `new_x1` int(10) NOT NULL DEFAULT '0',
  `new_y1` int(10) NOT NULL DEFAULT '0',
  `new_mapid1` int(10) NOT NULL DEFAULT '0',
  `new_x2` int(10) NOT NULL DEFAULT '0',
  `new_y2` int(10) NOT NULL DEFAULT '0',
  `new_mapid2` int(10) NOT NULL DEFAULT '0',
  `new_x3` int(10) NOT NULL DEFAULT '0',
  `new_y3` int(10) NOT NULL DEFAULT '0',
  `new_mapid3` int(10) NOT NULL DEFAULT '0',
  `new_x4` int(10) NOT NULL DEFAULT '0',
  `new_y4` int(10) NOT NULL DEFAULT '0',
  `new_mapid4` int(10) NOT NULL DEFAULT '0',
  `new_x5` int(10) NOT NULL DEFAULT '0',
  `new_y5` int(10) NOT NULL DEFAULT '0',
  `new_mapid5` int(10) NOT NULL DEFAULT '0',
  `new_heading` int(10) NOT NULL DEFAULT '1',
  `note` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`src_x`,`src_y`,`src_mapid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for etcitem
-- ----------------------------
DROP TABLE IF EXISTS `etcitem`;
CREATE TABLE `etcitem` (
  `item_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL DEFAULT '',
  `name_id` varchar(45) NOT NULL DEFAULT '',
  `item_type` varchar(40) NOT NULL DEFAULT '',
  `use_type` varchar(20) NOT NULL DEFAULT '',
  `material` varchar(45) NOT NULL DEFAULT '',
  `weight` int(10) unsigned NOT NULL DEFAULT '0',
  `invgfx` int(10) unsigned NOT NULL DEFAULT '0',
  `grdgfx` int(10) unsigned NOT NULL DEFAULT '0',
  `itemdesc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `stackable` int(10) unsigned NOT NULL DEFAULT '0',
  `max_charge_count` int(10) unsigned NOT NULL DEFAULT '0',
  `dmg_small` int(10) unsigned NOT NULL DEFAULT '0',
  `dmg_large` int(10) unsigned NOT NULL DEFAULT '0',
  `min_lvl` int(10) unsigned NOT NULL DEFAULT '0',
  `max_lvl` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `bless` int(2) unsigned NOT NULL DEFAULT '1',
  `trade` int(2) unsigned NOT NULL DEFAULT '0',
  `cant_delete` int(2) unsigned NOT NULL DEFAULT '0',
  `delay_id` int(10) unsigned NOT NULL DEFAULT '0',
  `delay_time` int(10) unsigned NOT NULL DEFAULT '0',
  `delay_effect` int(10) unsigned NOT NULL DEFAULT '0',
  `food_volume` int(10) unsigned NOT NULL DEFAULT '0',
  `save_at_once` tinyint(1) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3000165 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for getback
-- ----------------------------
DROP TABLE IF EXISTS `getback`;
CREATE TABLE `getback` (
  `area_x1` int(10) NOT NULL DEFAULT '0',
  `area_y1` int(10) NOT NULL DEFAULT '0',
  `area_x2` int(10) NOT NULL DEFAULT '0',
  `area_y2` int(10) NOT NULL DEFAULT '0',
  `area_mapid` int(10) NOT NULL DEFAULT '0',
  `getback_x1` int(10) NOT NULL DEFAULT '0',
  `getback_y1` int(10) NOT NULL DEFAULT '0',
  `getback_x2` int(10) NOT NULL DEFAULT '0',
  `getback_y2` int(10) NOT NULL DEFAULT '0',
  `getback_x3` int(10) NOT NULL DEFAULT '0',
  `getback_y3` int(10) NOT NULL DEFAULT '0',
  `getback_mapid` int(10) NOT NULL DEFAULT '0',
  `getback_townid` int(10) unsigned NOT NULL DEFAULT '0',
  `getback_townid_elf` int(10) unsigned NOT NULL DEFAULT '0',
  `getback_townid_darkelf` int(10) unsigned NOT NULL DEFAULT '0',
  `scrollescape` int(10) NOT NULL DEFAULT '1',
  `note` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`area_x1`,`area_y1`,`area_x2`,`area_y2`,`area_mapid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for getback_restart
-- ----------------------------
DROP TABLE IF EXISTS `getback_restart`;
CREATE TABLE `getback_restart` (
  `area` int(10) NOT NULL DEFAULT '0',
  `note` varchar(50) DEFAULT NULL,
  `locx` int(10) NOT NULL DEFAULT '0',
  `locy` int(10) NOT NULL DEFAULT '0',
  `mapid` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`area`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for house
-- ----------------------------
DROP TABLE IF EXISTS `house`;
CREATE TABLE `house` (
  `house_id` int(10) unsigned NOT NULL DEFAULT '0',
  `house_name` varchar(45) NOT NULL DEFAULT '',
  `house_area` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(45) NOT NULL DEFAULT '',
  `keeper_id` int(10) unsigned NOT NULL DEFAULT '0',
  `is_on_sale` int(10) unsigned NOT NULL DEFAULT '0',
  `is_purchase_basement` int(10) unsigned NOT NULL DEFAULT '0',
  `tax_deadline` datetime DEFAULT NULL,
  PRIMARY KEY (`house_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for inn
-- ----------------------------
DROP TABLE IF EXISTS `inn`;
CREATE TABLE `inn` (
  `name` varchar(45) DEFAULT '',
  `npcid` int(10) NOT NULL COMMENT '旅館NPC',
  `room_number` int(5) NOT NULL COMMENT '編號',
  `key_id` int(11) DEFAULT NULL,
  `lodger_id` int(11) DEFAULT NULL COMMENT '租用人',
  `hall` tinyint(2) DEFAULT NULL COMMENT '會議室',
  `due_time` datetime DEFAULT NULL COMMENT '租用結束時間',
  PRIMARY KEY (`npcid`,`room_number`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for inn_key
-- ----------------------------
DROP TABLE IF EXISTS `inn_key`;
CREATE TABLE `inn_key` (
  `item_obj_id` int(11) NOT NULL,
  `key_id` int(11) NOT NULL,
  `npc_id` int(10) DEFAULT NULL,
  `hall` tinyint(2) DEFAULT NULL,
  `due_time` datetime DEFAULT NULL,
  PRIMARY KEY (`item_obj_id`,`key_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for item_enchant_list
-- ----------------------------
DROP TABLE IF EXISTS `item_enchant_list`;
CREATE TABLE `item_enchant_list` (
  `item_id` int(10) NOT NULL,
  `name` varchar(45) NOT NULL,
  `chance` int(10) NOT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for item_key_boss
-- ----------------------------
DROP TABLE IF EXISTS `item_key_boss`;
CREATE TABLE `item_key_boss` (
  `item_obj_id` int(11) NOT NULL,
  `key_id` int(11) NOT NULL,
  PRIMARY KEY (`item_obj_id`,`key_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for letter
-- ----------------------------
DROP TABLE IF EXISTS `letter`;
CREATE TABLE `letter` (
  `item_object_id` int(10) unsigned NOT NULL DEFAULT '0',
  `code` int(10) unsigned NOT NULL DEFAULT '0',
  `sender` varchar(16) DEFAULT NULL,
  `receiver` varchar(16) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `template_id` int(5) unsigned NOT NULL DEFAULT '0',
  `subject` varchar(200) DEFAULT NULL,
  `content` varchar(2000) DEFAULT NULL,
  `isCheck` bit(1) DEFAULT NULL,
  PRIMARY KEY (`item_object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for letter_command
-- ----------------------------
DROP TABLE IF EXISTS `letter_command`;
CREATE TABLE `letter_command` (
  `id` int(10) NOT NULL,
  `subject` varchar(100) DEFAULT NULL,
  `content` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for letter_spam
-- ----------------------------
DROP TABLE IF EXISTS `letter_spam`;
CREATE TABLE `letter_spam` (
  `no` int(10) NOT NULL DEFAULT '0',
  `name` varchar(16) DEFAULT NULL,
  `spamname` varchar(16) DEFAULT '',
  PRIMARY KEY (`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for levelup_quests_item
-- ----------------------------
DROP TABLE IF EXISTS `levelup_quests_item`;
CREATE TABLE `levelup_quests_item` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `level` int(10) NOT NULL DEFAULT '0',
  `type` int(5) NOT NULL DEFAULT '0',
  `note` varchar(100) DEFAULT NULL,
  `item_name` varchar(50) NOT NULL DEFAULT '',
  `item_id` int(10) NOT NULL DEFAULT '0',
  `count` int(10) NOT NULL DEFAULT '0',
  `enchant` int(6) NOT NULL DEFAULT '0',
  `attrlevel` int(5) NOT NULL DEFAULT '0',
  `bless` int(5) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `type` (`type`),
  KEY `bid` (`level`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log_chat
-- ----------------------------
DROP TABLE IF EXISTS `log_chat`;
CREATE TABLE `log_chat` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `account_name` varchar(50) NOT NULL,
  `char_id` int(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `clan_id` int(10) NOT NULL,
  `clan_name` varchar(50) DEFAULT NULL,
  `locx` int(10) NOT NULL,
  `locy` int(10) NOT NULL,
  `mapid` int(10) NOT NULL,
  `type` int(10) NOT NULL,
  `target_account_name` varchar(50) DEFAULT NULL,
  `target_id` int(10) DEFAULT '0',
  `target_name` varchar(50) DEFAULT NULL,
  `target_clan_id` int(10) DEFAULT NULL,
  `target_clan_name` varchar(50) DEFAULT NULL,
  `target_locx` int(10) DEFAULT NULL,
  `target_locy` int(10) DEFAULT NULL,
  `target_mapid` int(10) DEFAULT NULL,
  `content` varchar(256) NOT NULL,
  `datetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1865 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log_enchant
-- ----------------------------
DROP TABLE IF EXISTS `log_enchant`;
CREATE TABLE `log_enchant` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_id` int(10) NOT NULL DEFAULT '0',
  `item_id` int(10) unsigned NOT NULL DEFAULT '0',
  `old_enchantlvl` int(3) NOT NULL DEFAULT '0',
  `new_enchantlvl` int(3) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mapids
-- ----------------------------
DROP TABLE IF EXISTS `mapids`;
CREATE TABLE `mapids` (
  `mapid` int(10) NOT NULL DEFAULT '0',
  `locationname` varchar(45) DEFAULT NULL,
  `startX` int(10) unsigned NOT NULL DEFAULT '0',
  `endX` int(10) unsigned NOT NULL DEFAULT '0',
  `startY` int(10) unsigned NOT NULL DEFAULT '0',
  `endY` int(10) unsigned NOT NULL DEFAULT '0',
  `monster_amount` float unsigned NOT NULL DEFAULT '0',
  `drop_rate` float unsigned NOT NULL DEFAULT '0',
  `underwater` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `markable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `teleportable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `escapable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `resurrection` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `painwand` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `penalty` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `take_pets` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `recall_pets` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `usable_item` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `usable_skill` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`mapid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mobgroup
-- ----------------------------
DROP TABLE IF EXISTS `mobgroup`;
CREATE TABLE `mobgroup` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `note` varchar(255) NOT NULL DEFAULT '',
  `remove_group_if_leader_die` int(10) unsigned NOT NULL DEFAULT '0',
  `leader_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion1_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion1_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion2_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion2_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion3_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion3_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion4_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion4_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion5_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion5_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion6_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion6_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion7_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion7_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion8_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion8_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mobskill
-- ----------------------------
DROP TABLE IF EXISTS `mobskill`;
CREATE TABLE `mobskill` (
  `mobid` int(10) unsigned NOT NULL DEFAULT '0',
  `actNo` int(10) unsigned NOT NULL DEFAULT '0',
  `mobname` varchar(45) NOT NULL DEFAULT '',
  `Type` int(10) unsigned NOT NULL DEFAULT '0',
  `TriRnd` int(10) unsigned NOT NULL DEFAULT '0',
  `TriHp` int(10) unsigned NOT NULL DEFAULT '0',
  `TriCompanionHp` int(10) unsigned NOT NULL DEFAULT '0',
  `TriRange` int(10) NOT NULL DEFAULT '0',
  `TriCount` int(10) NOT NULL DEFAULT '0',
  `ChangeTarget` int(10) unsigned NOT NULL DEFAULT '0',
  `Range` int(10) unsigned NOT NULL DEFAULT '0',
  `AreaWidth` int(10) unsigned NOT NULL DEFAULT '0',
  `AreaHeight` int(10) unsigned NOT NULL DEFAULT '0',
  `Leverage` int(10) unsigned NOT NULL DEFAULT '0',
  `SkillId` int(10) unsigned NOT NULL DEFAULT '0',
  `Gfxid` int(10) unsigned NOT NULL DEFAULT '0',
  `ActId` int(10) unsigned NOT NULL DEFAULT '0',
  `SummonId` int(10) unsigned NOT NULL DEFAULT '0',
  `SummonMin` int(10) NOT NULL DEFAULT '0',
  `SummonMax` int(10) NOT NULL DEFAULT '0',
  `PolyId` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`mobid`,`actNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for monster_book
-- ----------------------------
DROP TABLE IF EXISTS `monster_book`;
CREATE TABLE `monster_book` (
  `monsternumber` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `monstername` varchar(255) NOT NULL,
  `monster_id` int(10) DEFAULT NULL,
  `locx` int(10) unsigned DEFAULT '0',
  `locy` int(10) unsigned DEFAULT '0',
  `mapid` int(10) unsigned DEFAULT '0',
  `type` int(10) DEFAULT NULL,
  `marterial` int(10) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`monsternumber`)
) ENGINE=InnoDB AUTO_INCREMENT=554 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for monster_weekquest
-- ----------------------------
DROP TABLE IF EXISTS `monster_weekquest`;
CREATE TABLE `monster_weekquest` (
  `Type` int(10) NOT NULL DEFAULT '0',
  `MOB_ID1` int(10) NOT NULL,
  `MOB_ID2` int(10) NOT NULL,
  `MOB_ID3` int(10) NOT NULL,
  `MOB_ID4` int(10) DEFAULT NULL,
  `MOB_ID5` int(10) DEFAULT NULL,
  `MOB_ID6` int(10) DEFAULT NULL,
  `MOB_ID7` int(10) DEFAULT NULL,
  `MOB_ID8` int(10) DEFAULT NULL,
  `MOB_ID9` int(10) DEFAULT NULL,
  PRIMARY KEY (`Type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for nodropitem
-- ----------------------------
DROP TABLE IF EXISTS `nodropitem`;
CREATE TABLE `nodropitem` (
  `item_id` int(11) NOT NULL DEFAULT '0',
  `note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for noshopandware
-- ----------------------------
DROP TABLE IF EXISTS `noshopandware`;
CREATE TABLE `noshopandware` (
  `item_id` int(11) NOT NULL DEFAULT '0',
  `note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` int(30) NOT NULL,
  `message` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for notradable
-- ----------------------------
DROP TABLE IF EXISTS `notradable`;
CREATE TABLE `notradable` (
  `item_id` int(11) NOT NULL DEFAULT '0',
  `note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for npc
-- ----------------------------
DROP TABLE IF EXISTS `npc`;
CREATE TABLE `npc` (
  `npcid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL DEFAULT '',
  `nameid` varchar(45) NOT NULL DEFAULT '',
  `note` varchar(45) NOT NULL DEFAULT '',
  `impl` varchar(45) NOT NULL DEFAULT '',
  `gfxid` int(10) unsigned NOT NULL DEFAULT '0',
  `lvl` int(10) unsigned NOT NULL DEFAULT '0',
  `hp` int(10) unsigned NOT NULL DEFAULT '0',
  `mp` int(10) unsigned NOT NULL DEFAULT '0',
  `ac` int(10) NOT NULL DEFAULT '0',
  `str` int(10) NOT NULL DEFAULT '0',
  `con` int(10) NOT NULL DEFAULT '0',
  `dex` int(10) NOT NULL DEFAULT '0',
  `wis` int(10) NOT NULL DEFAULT '0',
  `intel` int(10) NOT NULL DEFAULT '0',
  `mr` int(10) NOT NULL DEFAULT '0',
  `exp` int(10) unsigned NOT NULL DEFAULT '0',
  `lawful` int(10) NOT NULL DEFAULT '0',
  `size` varchar(10) NOT NULL DEFAULT '',
  `weakAttr` int(10) NOT NULL DEFAULT '0',
  `ranged` int(10) unsigned NOT NULL DEFAULT '0',
  `tamable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `passispeed` int(10) unsigned NOT NULL DEFAULT '0',
  `atkspeed` int(10) unsigned NOT NULL DEFAULT '0',
  `atk_magic_speed` int(10) unsigned NOT NULL DEFAULT '0',
  `sub_magic_speed` int(10) unsigned NOT NULL DEFAULT '0',
  `undead` int(10) unsigned NOT NULL DEFAULT '0',
  `poison_atk` int(10) unsigned NOT NULL DEFAULT '0',
  `paralysis_atk` int(10) unsigned NOT NULL DEFAULT '0',
  `agro` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `agrososc` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `agrocoi` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `family` varchar(20) NOT NULL DEFAULT '',
  `agrofamily` int(1) unsigned NOT NULL DEFAULT '0',
  `agrogfxid1` int(10) NOT NULL DEFAULT '-1',
  `agrogfxid2` int(10) NOT NULL DEFAULT '-1',
  `picupitem` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `digestitem` int(1) unsigned NOT NULL DEFAULT '0',
  `bravespeed` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `hprinterval` int(6) unsigned NOT NULL DEFAULT '0',
  `hpr` int(5) unsigned NOT NULL DEFAULT '0',
  `mprinterval` int(6) unsigned NOT NULL DEFAULT '0',
  `mpr` int(5) unsigned NOT NULL DEFAULT '0',
  `teleport` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `randomlevel` int(3) unsigned NOT NULL DEFAULT '0',
  `randomhp` int(5) unsigned NOT NULL DEFAULT '0',
  `randommp` int(5) unsigned NOT NULL DEFAULT '0',
  `randomac` int(3) NOT NULL DEFAULT '0',
  `randomexp` int(5) unsigned NOT NULL DEFAULT '0',
  `randomlawful` int(5) NOT NULL DEFAULT '0',
  `damage_reduction` int(5) unsigned NOT NULL DEFAULT '0',
  `hard` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `doppel` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `IsTU` tinyint(1) NOT NULL DEFAULT '0' COMMENT '턴 안 뎁트가 효과 있을까',
  `IsErase` tinyint(1) NOT NULL DEFAULT '0' COMMENT '레이스이마직크가 효과 있을까',
  `ignore_aoe` int(10) unsigned NOT NULL,
  `bowActId` int(5) unsigned NOT NULL DEFAULT '0',
  `karma` int(10) NOT NULL DEFAULT '0',
  `transform_id` int(10) NOT NULL DEFAULT '-1',
  `transform_gfxid` int(10) NOT NULL DEFAULT '0',
  `light_size` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `amount_fixed` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `change_head` int(1) NOT NULL DEFAULT '0',
  `spawnlist_door` int(10) NOT NULL DEFAULT '0',
  `count_map` int(10) NOT NULL DEFAULT '0',
  `cant_resurrect` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`npcid`)
) ENGINE=InnoDB AUTO_INCREMENT=460000129 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for npcaction
-- ----------------------------
DROP TABLE IF EXISTS `npcaction`;
CREATE TABLE `npcaction` (
  `npcid` int(10) unsigned NOT NULL DEFAULT '0',
  `normal_action` varchar(45) NOT NULL DEFAULT '',
  `caotic_action` varchar(45) NOT NULL DEFAULT '',
  `teleport_url` varchar(45) NOT NULL DEFAULT '',
  `teleport_urla` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`npcid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for npcbuylist
-- ----------------------------
DROP TABLE IF EXISTS `npcbuylist`;
CREATE TABLE `npcbuylist` (
  `itemid` int(255) NOT NULL,
  `EnchantLevel` int(255) NOT NULL,
  `AttrEnchant` int(255) NOT NULL DEFAULT '0',
  `bless` int(255) NOT NULL,
  `price` int(255) NOT NULL,
  PRIMARY KEY (`itemid`,`EnchantLevel`,`AttrEnchant`,`bless`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for npcchat
-- ----------------------------
DROP TABLE IF EXISTS `npcchat`;
CREATE TABLE `npcchat` (
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `chat_timing` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `note` varchar(45) NOT NULL DEFAULT '',
  `start_delay_time` int(10) NOT NULL DEFAULT '0',
  `chat_id1` varchar(45) NOT NULL DEFAULT '',
  `chat_id2` varchar(45) NOT NULL DEFAULT '',
  `chat_id3` varchar(45) NOT NULL DEFAULT '',
  `chat_id4` varchar(45) NOT NULL DEFAULT '',
  `chat_id5` varchar(45) NOT NULL DEFAULT '',
  `chat_interval` int(10) unsigned NOT NULL DEFAULT '0',
  `is_shout` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `is_world_chat` tinyint(1) NOT NULL DEFAULT '0',
  `is_repeat` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `repeat_interval` int(10) unsigned NOT NULL DEFAULT '0',
  `game_time` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`npc_id`,`chat_timing`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for petitem
-- ----------------------------
DROP TABLE IF EXISTS `petitem`;
CREATE TABLE `petitem` (
  `item_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `note` varchar(45) NOT NULL DEFAULT '',
  `hitmodifier` int(3) NOT NULL DEFAULT '0',
  `dmgmodifier` int(3) NOT NULL DEFAULT '0',
  `ac` int(3) NOT NULL DEFAULT '0',
  `add_str` int(2) NOT NULL DEFAULT '0',
  `add_con` int(2) NOT NULL DEFAULT '0',
  `add_dex` int(2) NOT NULL DEFAULT '0',
  `add_int` int(2) NOT NULL DEFAULT '0',
  `add_wis` int(2) NOT NULL DEFAULT '0',
  `add_hp` int(10) NOT NULL DEFAULT '0',
  `add_mp` int(10) NOT NULL DEFAULT '0',
  `add_sp` int(10) NOT NULL DEFAULT '0',
  `m_def` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40767 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pets
-- ----------------------------
DROP TABLE IF EXISTS `pets`;
CREATE TABLE `pets` (
  `item_obj_id` int(10) unsigned NOT NULL DEFAULT '0',
  `objid` int(10) unsigned NOT NULL DEFAULT '0',
  `npcid` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL DEFAULT '',
  `lvl` int(10) unsigned NOT NULL DEFAULT '0',
  `hp` int(10) unsigned NOT NULL DEFAULT '0',
  `mp` int(10) unsigned NOT NULL DEFAULT '0',
  `exp` int(10) unsigned NOT NULL DEFAULT '0',
  `lawful` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pettypes
-- ----------------------------
DROP TABLE IF EXISTS `pettypes`;
CREATE TABLE `pettypes` (
  `BaseNpcId` int(10) NOT NULL,
  `Name` varchar(45) NOT NULL,
  `ItemIdForTaming` int(10) NOT NULL,
  `HpUpMin` int(10) NOT NULL,
  `HpUpMax` int(10) NOT NULL,
  `MpUpMin` int(10) NOT NULL,
  `MpUpMax` int(10) NOT NULL,
  `NpcIdForEvolving` int(10) NOT NULL,
  `MessageId1` int(10) NOT NULL,
  `MessageId2` int(10) NOT NULL,
  `MessageId3` int(10) NOT NULL,
  `MessageId4` int(10) NOT NULL,
  `MessageId5` int(10) NOT NULL,
  `DefyMessageId` int(10) NOT NULL,
  PRIMARY KEY (`BaseNpcId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for polymorphs
-- ----------------------------
DROP TABLE IF EXISTS `polymorphs`;
CREATE TABLE `polymorphs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `polyid` int(11) DEFAULT NULL,
  `minlevel` int(11) DEFAULT NULL,
  `weaponequip` int(11) DEFAULT NULL,
  `armorequip` int(11) DEFAULT NULL,
  `isSkillUse` int(11) DEFAULT NULL,
  `cause` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=133937 DEFAULT CHARSET=utf8 COMMENT='MyISAM free: 10240 kB';

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `objid` varchar(45) NOT NULL DEFAULT '0',
  `reporter` varchar(45) NOT NULL,
  PRIMARY KEY (`objid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for resolvent
-- ----------------------------
DROP TABLE IF EXISTS `resolvent`;
CREATE TABLE `resolvent` (
  `item_id` int(10) NOT NULL DEFAULT '0',
  `note` varchar(45) NOT NULL,
  `crystal_count` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for resolvent1
-- ----------------------------
DROP TABLE IF EXISTS `resolvent1`;
CREATE TABLE `resolvent1` (
  `item_id` int(10) NOT NULL DEFAULT '0',
  `note` varchar(45) NOT NULL,
  `crystal_count` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for robots
-- ----------------------------
DROP TABLE IF EXISTS `robots`;
CREATE TABLE `robots` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `title` varchar(255) NOT NULL DEFAULT '',
  `class` int(10) NOT NULL DEFAULT '61',
  `sex` int(2) NOT NULL DEFAULT '0',
  `clanid` int(10) NOT NULL DEFAULT '0',
  `clanname` varchar(255) NOT NULL DEFAULT '',
  `ban` int(2) NOT NULL DEFAULT '0',
  `connect` int(2) NOT NULL DEFAULT '0',
  `step` int(2) NOT NULL DEFAULT '0',
  `lawful` int(2) NOT NULL DEFAULT '0',
  `map` int(10) NOT NULL,
  PRIMARY KEY (`id`,`name`)
) ENGINE=MyISAM AUTO_INCREMENT=1127 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for robots_crown
-- ----------------------------
DROP TABLE IF EXISTS `robots_crown`;
CREATE TABLE `robots_crown` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `title` varchar(255) NOT NULL DEFAULT '',
  `sex` int(2) NOT NULL DEFAULT '0',
  `clanid` int(10) NOT NULL DEFAULT '0',
  `clanname` varchar(255) NOT NULL DEFAULT '',
  `lawful` int(10) NOT NULL DEFAULT '0',
  `heading` int(10) NOT NULL,
  `x` int(10) NOT NULL,
  `y` int(10) NOT NULL,
  `map` int(10) NOT NULL,
  `clan_memo` varchar(45) DEFAULT NULL,
  `user_title` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=898426925 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for robot_location
-- ----------------------------
DROP TABLE IF EXISTS `robot_location`;
CREATE TABLE `robot_location` (
  `uid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `x` int(10) NOT NULL,
  `y` int(10) NOT NULL,
  `map` int(10) NOT NULL,
  `etc` text NOT NULL,
  `count` int(10) NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`)
) ENGINE=MyISAM AUTO_INCREMENT=166 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for robot_ment
-- ----------------------------
DROP TABLE IF EXISTS `robot_ment`;
CREATE TABLE `robot_ment` (
  `ment` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`ment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for robot_ment2
-- ----------------------------
DROP TABLE IF EXISTS `robot_ment2`;
CREATE TABLE `robot_ment2` (
  `message` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`message`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for robot_message
-- ----------------------------
DROP TABLE IF EXISTS `robot_message`;
CREATE TABLE `robot_message` (
  `uid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` enum('pvp','die') NOT NULL,
  `ment` text NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=MyISAM AUTO_INCREMENT=156 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for robot_name
-- ----------------------------
DROP TABLE IF EXISTS `robot_name`;
CREATE TABLE `robot_name` (
  `uid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`uid`)
) ENGINE=MyISAM AUTO_INCREMENT=1127 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for serverinfo
-- ----------------------------
DROP TABLE IF EXISTS `serverinfo`;
CREATE TABLE `serverinfo` (
  `id` varchar(255) NOT NULL DEFAULT '',
  `adenmake` bigint(30) DEFAULT '0',
  `adenconsume` bigint(30) DEFAULT '0',
  `adentax` int(10) DEFAULT '0',
  `bugdividend` float(10,0) DEFAULT '0',
  `accountcount` int(10) DEFAULT '0',
  `charcount` int(10) DEFAULT '0',
  `pvpcount` int(10) DEFAULT '0',
  `penaltycount` int(10) DEFAULT '0',
  `clanmaker` int(10) DEFAULT '0',
  `maxuser` int(10) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `item_id` int(10) unsigned NOT NULL DEFAULT '0',
  `order_id` int(10) unsigned NOT NULL DEFAULT '0',
  `selling_price` int(10) NOT NULL DEFAULT '-1',
  `pack_count` int(10) unsigned NOT NULL DEFAULT '0',
  `purchasing_price` int(10) NOT NULL DEFAULT '-1',
  `enchant` int(10) NOT NULL DEFAULT '0',
  `time_limit` enum('false','true') NOT NULL DEFAULT 'false',
  `note` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`npc_id`,`item_id`,`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for shop_aden
-- ----------------------------
DROP TABLE IF EXISTS `shop_aden`;
CREATE TABLE `shop_aden` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `itemid` int(10) DEFAULT NULL,
  `itemname` varchar(22) DEFAULT NULL,
  `price` int(10) DEFAULT NULL,
  `type` int(10) DEFAULT '0',
  `status` int(10) DEFAULT '0',
  `html` varchar(22) DEFAULT '',
  `pack` int(10) DEFAULT '0',
  `enchant` int(10) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for shop_npc
-- ----------------------------
DROP TABLE IF EXISTS `shop_npc`;
CREATE TABLE `shop_npc` (
  `npc_id` int(10) NOT NULL,
  `id` int(10) NOT NULL DEFAULT '1',
  `item_id` int(10) NOT NULL DEFAULT '0',
  `memo` text,
  `count` int(10) NOT NULL DEFAULT '1',
  `enchant` int(10) NOT NULL DEFAULT '0',
  `selling_price` int(10) NOT NULL DEFAULT '-1',
  `purchasing_price` int(10) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`npc_id`,`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for skills
-- ----------------------------
DROP TABLE IF EXISTS `skills`;
CREATE TABLE `skills` (
  `skill_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL DEFAULT '',
  `skill_level` int(10) NOT NULL DEFAULT '0',
  `skill_number` int(10) NOT NULL DEFAULT '0',
  `mpConsume` int(10) unsigned NOT NULL DEFAULT '0',
  `hpConsume` int(10) unsigned NOT NULL DEFAULT '0',
  `itemConsumeId` int(10) unsigned NOT NULL DEFAULT '0',
  `itemConsumeCount` int(10) unsigned NOT NULL DEFAULT '0',
  `reuseDelay` int(10) unsigned NOT NULL DEFAULT '0',
  `buffDuration` int(10) unsigned NOT NULL DEFAULT '0',
  `target` varchar(45) NOT NULL DEFAULT '',
  `target_to` int(10) NOT NULL DEFAULT '0',
  `damage_value` int(10) unsigned NOT NULL DEFAULT '0',
  `damage_dice` int(10) unsigned NOT NULL DEFAULT '0',
  `damage_dice_count` int(10) unsigned NOT NULL DEFAULT '0',
  `probability_value` int(10) unsigned NOT NULL DEFAULT '0',
  `probability_dice` int(10) unsigned NOT NULL DEFAULT '0',
  `limit` int(10) unsigned DEFAULT '0',
  `attr` int(10) unsigned NOT NULL DEFAULT '0',
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `lawful` int(10) NOT NULL DEFAULT '0',
  `ranged` int(10) NOT NULL DEFAULT '0',
  `area` int(10) NOT NULL DEFAULT '0',
  `through` int(10) NOT NULL DEFAULT '0',
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `nameid` varchar(45) NOT NULL DEFAULT '',
  `action_id` int(10) unsigned NOT NULL DEFAULT '0',
  `action_id2` int(10) unsigned NOT NULL DEFAULT '0',
  `action_id3` int(10) unsigned NOT NULL DEFAULT '0',
  `castgfx` int(10) unsigned NOT NULL DEFAULT '0',
  `castgfx2` int(10) unsigned NOT NULL DEFAULT '0',
  `castgfx3` int(10) unsigned NOT NULL DEFAULT '0',
  `sysmsgID_happen` int(10) unsigned NOT NULL DEFAULT '0',
  `sysmsgID_stop` int(10) unsigned NOT NULL DEFAULT '0',
  `sysmsgID_fail` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`skill_id`)
) ENGINE=InnoDB AUTO_INCREMENT=707030 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist`;
CREATE TABLE `spawnlist` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `location` varchar(45) NOT NULL DEFAULT '',
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `npc_templateid` int(10) unsigned NOT NULL DEFAULT '0',
  `group_id` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `randomx` int(10) unsigned NOT NULL DEFAULT '0',
  `randomy` int(10) unsigned NOT NULL DEFAULT '0',
  `locx1` int(10) unsigned NOT NULL DEFAULT '0',
  `locy1` int(10) unsigned NOT NULL DEFAULT '0',
  `locx2` int(10) unsigned NOT NULL DEFAULT '0',
  `locy2` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(10) unsigned NOT NULL DEFAULT '0',
  `min_respawn_delay` int(10) unsigned NOT NULL DEFAULT '0',
  `max_respawn_delay` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `respawn_screen` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `movement_distance` int(10) unsigned NOT NULL DEFAULT '0',
  `rest` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `near_spawn` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2120011528 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_boss
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_boss`;
CREATE TABLE `spawnlist_boss` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `location` varchar(45) NOT NULL DEFAULT '',
  `cycle_type` varchar(20) NOT NULL DEFAULT '',
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `group_id` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `randomx` int(10) unsigned NOT NULL DEFAULT '0',
  `randomy` int(10) unsigned NOT NULL DEFAULT '0',
  `locx1` int(10) unsigned NOT NULL DEFAULT '0',
  `locy1` int(10) unsigned NOT NULL DEFAULT '0',
  `locx2` int(10) unsigned NOT NULL DEFAULT '0',
  `locy2` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `respawn_screen` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `movement_distance` int(10) unsigned NOT NULL DEFAULT '0',
  `rest` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `spawn_type` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `percentage` tinyint(3) unsigned NOT NULL DEFAULT '100',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_boss_new
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_boss_new`;
CREATE TABLE `spawnlist_boss_new` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `note` varchar(45) DEFAULT '',
  `npcid` int(10) unsigned DEFAULT '0',
  `info` text,
  `rndXY` int(10) unsigned DEFAULT '0',
  `groupid` int(10) unsigned DEFAULT '0',
  `is_yn` int(10) unsigned DEFAULT '0',
  `is_ment` int(10) unsigned DEFAULT '0',
  `ment` varchar(100) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_door
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_door`;
CREATE TABLE `spawnlist_door` (
  `id` int(11) NOT NULL DEFAULT '0',
  `location` varchar(25) NOT NULL DEFAULT '',
  `gfxid` int(11) NOT NULL DEFAULT '0',
  `locx` int(11) NOT NULL DEFAULT '0',
  `locy` int(11) NOT NULL DEFAULT '0',
  `mapid` int(11) NOT NULL DEFAULT '0',
  `direction` int(11) NOT NULL DEFAULT '0',
  `left_edge_location` int(11) NOT NULL DEFAULT '0',
  `right_edge_location` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `keeper` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_fantasyisland
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_fantasyisland`;
CREATE TABLE `spawnlist_fantasyisland` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(19) NOT NULL DEFAULT '',
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=262 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_furniture
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_furniture`;
CREATE TABLE `spawnlist_furniture` (
  `item_obj_id` int(10) unsigned NOT NULL DEFAULT '0',
  `npcid` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) NOT NULL DEFAULT '0',
  `locy` int(10) NOT NULL DEFAULT '0',
  `mapid` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_hadin
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_hadin`;
CREATE TABLE `spawnlist_hadin` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(19) NOT NULL DEFAULT '',
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=689 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_light
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_light`;
CREATE TABLE `spawnlist_light` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `npcid` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=174 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_npc
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_npc`;
CREATE TABLE `spawnlist_npc` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `location` varchar(200) NOT NULL DEFAULT '',
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `npc_templateid` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `randomx` int(10) unsigned NOT NULL DEFAULT '0',
  `randomy` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(10) unsigned NOT NULL DEFAULT '0',
  `respawn_delay` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `movement_distance` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30117 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_npc_cash_shop
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_npc_cash_shop`;
CREATE TABLE `spawnlist_npc_cash_shop` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `memo` text,
  `name` varchar(40) NOT NULL,
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(10) NOT NULL DEFAULT '0',
  `title` varchar(35) NOT NULL DEFAULT '',
  `shop_name` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_npc_shop
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_npc_shop`;
CREATE TABLE `spawnlist_npc_shop` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `memo` text,
  `name` varchar(40) NOT NULL,
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(10) NOT NULL DEFAULT '0',
  `title` varchar(35) NOT NULL DEFAULT '',
  `shop_name` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=439 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_raid
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_raid`;
CREATE TABLE `spawnlist_raid` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(19) NOT NULL DEFAULT '',
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4000001 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_trap
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_trap`;
CREATE TABLE `spawnlist_trap` (
  `id` int(8) NOT NULL,
  `note` varchar(64) DEFAULT NULL,
  `trapId` int(8) NOT NULL,
  `mapId` int(4) NOT NULL,
  `locX` int(4) NOT NULL,
  `locY` int(4) NOT NULL,
  `locRndX` int(4) NOT NULL DEFAULT '0',
  `locRndY` int(4) NOT NULL DEFAULT '0',
  `count` int(4) NOT NULL DEFAULT '1',
  `span` int(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_ub
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_ub`;
CREATE TABLE `spawnlist_ub` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ub_id` int(10) unsigned NOT NULL DEFAULT '0',
  `pattern` int(10) unsigned NOT NULL DEFAULT '0',
  `group_id` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(19) NOT NULL DEFAULT '',
  `npc_templateid` int(10) unsigned NOT NULL DEFAULT '0',
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `spawn_delay` int(10) unsigned NOT NULL DEFAULT '0',
  `seal_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1120 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_vala
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_vala`;
CREATE TABLE `spawnlist_vala` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(19) NOT NULL DEFAULT '',
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spawnlist_valakas_room
-- ----------------------------
DROP TABLE IF EXISTS `spawnlist_valakas_room`;
CREATE TABLE `spawnlist_valakas_room` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(19) NOT NULL DEFAULT '',
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `locx` int(10) unsigned NOT NULL DEFAULT '0',
  `locy` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2342342446 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for spr_action
-- ----------------------------
DROP TABLE IF EXISTS `spr_action`;
CREATE TABLE `spr_action` (
  `spr_id` int(4) unsigned NOT NULL,
  `act_id` int(4) unsigned NOT NULL,
  `framecount` int(4) unsigned NOT NULL DEFAULT '0',
  `framerate` int(4) unsigned NOT NULL DEFAULT '24',
  PRIMARY KEY (`spr_id`,`act_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tam
-- ----------------------------
DROP TABLE IF EXISTS `tam`;
CREATE TABLE `tam` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `objid` int(10) NOT NULL DEFAULT '0',
  `Name` varchar(45) NOT NULL,
  `Day` int(10) NOT NULL DEFAULT '0',
  `encobjid` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for town
-- ----------------------------
DROP TABLE IF EXISTS `town`;
CREATE TABLE `town` (
  `town_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL DEFAULT '',
  `leader_id` int(10) unsigned NOT NULL DEFAULT '0',
  `leader_name` varchar(45) DEFAULT NULL,
  `tax_rate` int(10) unsigned NOT NULL DEFAULT '0',
  `tax_rate_reserved` int(10) unsigned NOT NULL DEFAULT '0',
  `sales_money` int(10) unsigned NOT NULL DEFAULT '0',
  `sales_money_yesterday` int(10) unsigned NOT NULL DEFAULT '0',
  `town_tax` int(10) unsigned NOT NULL DEFAULT '0',
  `town_fix_tax` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`town_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for trap
-- ----------------------------
DROP TABLE IF EXISTS `trap`;
CREATE TABLE `trap` (
  `id` int(8) NOT NULL,
  `note` varchar(64) DEFAULT NULL,
  `type` varchar(64) NOT NULL,
  `gfxId` int(4) NOT NULL,
  `isDetectionable` tinyint(1) NOT NULL,
  `base` int(4) NOT NULL,
  `dice` int(4) NOT NULL,
  `diceCount` int(4) NOT NULL,
  `poisonType` char(1) NOT NULL DEFAULT 'n',
  `poisonDelay` int(4) NOT NULL DEFAULT '0',
  `poisonTime` int(4) NOT NULL DEFAULT '0',
  `poisonDamage` int(4) NOT NULL DEFAULT '0',
  `monsterNpcId` int(4) NOT NULL DEFAULT '0',
  `monsterCount` int(4) NOT NULL DEFAULT '0',
  `teleportX` int(4) NOT NULL DEFAULT '0',
  `teleportY` int(4) NOT NULL DEFAULT '0',
  `teleportMapId` int(4) NOT NULL DEFAULT '0',
  `skillId` int(4) NOT NULL DEFAULT '0',
  `skillTimeSeconds` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ub_managers
-- ----------------------------
DROP TABLE IF EXISTS `ub_managers`;
CREATE TABLE `ub_managers` (
  `ub_id` int(10) unsigned NOT NULL DEFAULT '0',
  `ub_manager_npc_id` int(10) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ub_rank
-- ----------------------------
DROP TABLE IF EXISTS `ub_rank`;
CREATE TABLE `ub_rank` (
  `ub_id` int(10) NOT NULL DEFAULT '0',
  `char_name` varchar(45) NOT NULL,
  `score` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ub_settings
-- ----------------------------
DROP TABLE IF EXISTS `ub_settings`;
CREATE TABLE `ub_settings` (
  `ub_id` int(10) unsigned NOT NULL DEFAULT '0',
  `ub_name` varchar(45) NOT NULL DEFAULT '',
  `ub_mapid` int(10) unsigned NOT NULL DEFAULT '0',
  `ub_area_x1` int(10) unsigned NOT NULL DEFAULT '0',
  `ub_area_y1` int(10) unsigned NOT NULL DEFAULT '0',
  `ub_area_x2` int(10) unsigned NOT NULL DEFAULT '0',
  `ub_area_y2` int(10) unsigned NOT NULL DEFAULT '0',
  `min_lvl` int(10) unsigned NOT NULL DEFAULT '0',
  `max_lvl` int(10) unsigned NOT NULL DEFAULT '0',
  `max_player` int(10) unsigned NOT NULL DEFAULT '0',
  `enter_royal` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `enter_knight` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `enter_mage` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `enter_elf` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `enter_darkelf` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `enter_dragonknight` tinyint(3) NOT NULL DEFAULT '0',
  `enter_blackwizard` tinyint(3) NOT NULL DEFAULT '0',
  `enter_male` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `enter_female` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `use_pot` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `hpr_bonus` int(10) NOT NULL DEFAULT '0',
  `mpr_bonus` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ub_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ub_times
-- ----------------------------
DROP TABLE IF EXISTS `ub_times`;
CREATE TABLE `ub_times` (
  `ub_id` int(10) unsigned NOT NULL DEFAULT '0',
  `ub_time` int(10) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for util_racer
-- ----------------------------
DROP TABLE IF EXISTS `util_racer`;
CREATE TABLE `util_racer` (
  `racerNumber` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `victoryNumber` int(10) NOT NULL DEFAULT '0',
  `loseNumber` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`racerNumber`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for weapon
-- ----------------------------
DROP TABLE IF EXISTS `weapon`;
CREATE TABLE `weapon` (
  `item_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL DEFAULT '',
  `name_id` varchar(45) NOT NULL DEFAULT '',
  `type` varchar(45) NOT NULL DEFAULT '',
  `material` varchar(45) NOT NULL DEFAULT '',
  `weight` int(10) unsigned NOT NULL DEFAULT '0',
  `invgfx` int(10) unsigned NOT NULL DEFAULT '0',
  `grdgfx` int(10) unsigned NOT NULL DEFAULT '0',
  `itemdesc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `dmg_small` int(10) unsigned NOT NULL DEFAULT '0',
  `dmg_large` int(10) unsigned NOT NULL DEFAULT '0',
  `safenchant` int(10) NOT NULL DEFAULT '0',
  `use_royal` int(10) unsigned NOT NULL DEFAULT '0',
  `use_knight` int(10) unsigned NOT NULL DEFAULT '0',
  `use_mage` int(10) unsigned NOT NULL DEFAULT '0',
  `use_elf` int(10) unsigned NOT NULL DEFAULT '0',
  `use_darkelf` int(10) unsigned NOT NULL DEFAULT '0',
  `use_dragonknight` int(10) unsigned NOT NULL DEFAULT '0',
  `use_blackwizard` int(10) unsigned NOT NULL DEFAULT '0',
  `use_warrior` int(10) NOT NULL DEFAULT '0',
  `hitmodifier` int(10) NOT NULL DEFAULT '0',
  `dmgmodifier` int(10) NOT NULL DEFAULT '0',
  `add_str` int(10) NOT NULL DEFAULT '0',
  `add_con` int(10) NOT NULL DEFAULT '0',
  `add_dex` int(10) NOT NULL DEFAULT '0',
  `add_int` int(10) NOT NULL DEFAULT '0',
  `add_wis` int(10) NOT NULL DEFAULT '0',
  `add_cha` int(10) NOT NULL DEFAULT '0',
  `add_hp` int(10) NOT NULL DEFAULT '0',
  `add_mp` int(10) NOT NULL DEFAULT '0',
  `add_hpr` int(10) NOT NULL DEFAULT '0',
  `add_mpr` int(10) NOT NULL DEFAULT '0',
  `add_sp` int(10) NOT NULL DEFAULT '0',
  `m_def` int(10) NOT NULL DEFAULT '0',
  `haste_item` int(2) unsigned NOT NULL DEFAULT '0',
  `double_dmg_chance` int(10) unsigned NOT NULL DEFAULT '0',
  `magicdmgmodifier` int(10) NOT NULL DEFAULT '0',
  `canbedmg` int(10) unsigned NOT NULL DEFAULT '0',
  `min_lvl` int(10) unsigned NOT NULL DEFAULT '0',
  `max_lvl` int(10) unsigned NOT NULL DEFAULT '0',
  `bless` int(2) unsigned NOT NULL DEFAULT '1',
  `trade` int(2) unsigned NOT NULL DEFAULT '0',
  `cant_delete` int(2) unsigned NOT NULL DEFAULT '0',
  `max_use_time` int(10) unsigned NOT NULL DEFAULT '0',
  `Magic_name` varchar(20) DEFAULT NULL,
  `penetration` int(2) unsigned NOT NULL DEFAULT '0',
  `ignore_reduction_by_weapon` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7000137 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for weapon_damege
-- ----------------------------
DROP TABLE IF EXISTS `weapon_damege`;
CREATE TABLE `weapon_damege` (
  `item_id` int(10) NOT NULL,
  `name` varchar(40) NOT NULL,
  `addDamege` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for weapon_enchant_list
-- ----------------------------
DROP TABLE IF EXISTS `weapon_enchant_list`;
CREATE TABLE `weapon_enchant_list` (
  `item_id` int(10) NOT NULL,
  `name` varchar(45) NOT NULL,
  `chance` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for weapon_skill
-- ----------------------------
DROP TABLE IF EXISTS `weapon_skill`;
CREATE TABLE `weapon_skill` (
  `weapon_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `note` varchar(255) DEFAULT NULL,
  `probability` int(11) unsigned NOT NULL DEFAULT '0',
  `fix_damage` int(11) unsigned NOT NULL DEFAULT '0',
  `random_damage` int(11) unsigned NOT NULL DEFAULT '0',
  `area` int(11) NOT NULL DEFAULT '0',
  `skill_id` int(11) unsigned NOT NULL DEFAULT '0',
  `skill_time` int(11) unsigned NOT NULL DEFAULT '0',
  `effect_id` int(11) unsigned NOT NULL DEFAULT '0',
  `effect_target` int(11) unsigned NOT NULL DEFAULT '0',
  `arrow_type` int(11) unsigned NOT NULL DEFAULT '0',
  `attr` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`weapon_id`)
) ENGINE=InnoDB AUTO_INCREMENT=203004 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for _cha_inv_items
-- ----------------------------
DROP TABLE IF EXISTS `_cha_inv_items`;
CREATE TABLE `_cha_inv_items` (
  `id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) DEFAULT NULL,
  `char_id` int(11) DEFAULT NULL,
  `char_name` varchar(22) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `is_equipped` int(11) DEFAULT NULL,
  `enchantlvl` int(11) DEFAULT NULL,
  `is_id` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `charge_count` int(11) DEFAULT NULL,
  `remaining_time` int(11) DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `bless` int(11) DEFAULT NULL,
  `attr_enchantlvl` int(11) DEFAULT NULL,
  `step_enchantlvl` int(11) DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `second_id` int(11) DEFAULT NULL,
  `round_id` int(11) DEFAULT NULL,
  `ticket_id` int(11) DEFAULT NULL,
  `regist_level` int(11) DEFAULT '0',
  `KeyVal` int(11) DEFAULT '0',
  `CreaterName` varchar(20) DEFAULT NULL,
  `demon_bongin` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for _report
-- ----------------------------
DROP TABLE IF EXISTS `_report`;
CREATE TABLE `_report` (
  `name` varchar(255) NOT NULL,
  `count` int(11) NOT NULL DEFAULT '1',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- View structure for master
-- ----------------------------
DROP VIEW IF EXISTS `master`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER  VIEW `master` AS select `armor`.`item_id` AS `item_id`,`armor`.`name` AS `name` from `armor` union select `etcitem`.`item_id` AS `item_id`,`etcitem`.`name` AS `name` from `etcitem` union select `weapon`.`item_id` AS `item_id`,`weapon`.`name` AS `name` from `weapon` ;
