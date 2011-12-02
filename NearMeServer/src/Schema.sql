DROP DATABASE IF EXISTS `nearme`;
CREATE DATABASE `nearme` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `nearme`;

CREATE TABLE `user` (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	hashId	INTEGER NOT NULL,
	deviceId VARCHAR(64) UNIQUE NOT NULL,
	latitude DOUBLE,
	longitude DOUBLE,
	lastReport DATETIME
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

ALTER TABLE user ADD INDEX deviceId_idx(deviceId);
ALTER TABLE user ADD INDEX hash_idx(hashId);

INSERT INTO user VALUES (1, 1, "android-123456", -123.45, 67.89, "2011-11-11 00:00:00");
INSERT INTO user VALUES (2, 2, "android-7890", -123.45, 67.89, "2011-11-11 00:00:00");

CREATE TABLE `idHash` (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	hash VARCHAR(64) NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

ALTER TABLE idHash ADD INDEX hash_idx(hash);

INSERT INTO idHash VALUES (1, "hash-1234567890");
INSERT INTO idHash VALUES (2, "hash-0987654321");
INSERT INTO idHash VALUES (3, "hash-aaaaaaaaaa");
INSERT INTO idHash VALUES (4, "hash-bbbbbbbbbb");
INSERT INTO idHash VALUES (5, "hash-cccccccccc");

CREATE TABLE `addressBook` (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	ownerId INTEGER NOT NULL,
	name VARCHAR(128) NOT NULL,
	permission INTEGER NOT NULL
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

ALTER TABLE addressBook ADD INDEX ownerName_idx(ownerId,name);

INSERT INTO addressBook VALUES (1, 1, "Tom", 1);
INSERT INTO addressBook VALUES (2, 1, "Dick", 0);
INSERT INTO addressBook VALUES (3, 1, "Harry", 0);

CREATE TABLE `addressBookHashMatcher` (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	hashId INTEGER NOT NULL,
	addressBookId INTEGER NOT NULL
);

ALTER TABLE addressBookHashMatcher ADD INDEX book_idx(addressBookId);

INSERT INTO addressBookHashMatcher VALUES (1, 3, 1);
INSERT INTO addressBookHashMatcher VALUES (2, 4, 2);
INSERT INTO addressBookHashMatcher VALUES (3, 5, 3);


CREATE TABLE `poi` (
  `Id` int(50) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  `latitude` varchar(255) NOT NULL default '',
  `longitude` varchar(255) NOT NULL default '',
  `type` varchar(11) NOT NULL default '0',
  PRIMARY KEY  (`Id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

INSERT INTO `poi` VALUES (1,'Warung Tujuh','50.820171','-0.138659','0001');
INSERT INTO `poi` VALUES (2,'L\'Eglise','50.829144','-0.177498','0001');
INSERT INTO `poi` VALUES (3,'Breeze','50.819439','-0.128016','0001');
INSERT INTO `poi` VALUES (4,'Casa Don Carlos','50.822204','-0.141191','0001');
INSERT INTO `poi` VALUES (5,'The Heart and Hand, Brighton','50.825864','-0.139518','0002');
INSERT INTO `poi` VALUES (6,'The Evening Star, Brighton','50.827897','-0.141878','0002');
INSERT INTO `poi` VALUES (7,'The King and Queen ','50.824536','-0.136986','0002');
INSERT INTO `poi` VALUES (8,'Grace Brighton','50.824156','-0.141792','0003');
INSERT INTO `poi` VALUES (9,'Oceana Brighton','50.820889','-0.144861','0003');
INSERT INTO `poi` VALUES (10,'Madame Geisha','50.819818','-0.138963','0003');
INSERT INTO `poi` VALUES (11,'Revolution','50.821364','-0.144367','0003');
INSERT INTO `poi` VALUES (12,'Coalition','50.82013','-0.143402','0003');
INSERT INTO `poi` VALUES (13,'Brighton Museum & Art Gallery','50.823424','-0.137758','0004');
INSERT INTO `poi` VALUES (14,'Brighton Toy and Model Museum','50.828764','-0.140719','0004');
INSERT INTO `poi` VALUES (15,'Brighton Fishing Museum & Quarter ','50.819954','-0.141921','0004');

CREATE TABLE `type` (
  `Id` varchar(11) NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `type` VALUES ('0001','Restaurants');
INSERT INTO `type` VALUES ('0002','Pubs');
INSERT INTO `type` VALUES ('0003','Clubs');
INSERT INTO `type` VALUES ('0004','Museum');
