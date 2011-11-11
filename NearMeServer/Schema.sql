DROP DATABASE IF EXISTS `nearme`;
CREATE DATABASE `nearme` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `nearme`;

CREATE TABLE `user` (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	hashId	INTEGER NOT NULL,
	deviceId	VARCHAR(64) NOT NULL,
	latitude	DOUBLE,
	longitude	DOUBLE,
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

INSERT INTO addressBook VALUES (1, 1, "Tom", 0);
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

INSERT INTO `poi` VALUES (1,'Warung Tujuh','49.56','-12.257','0001');
INSERT INTO `poi` VALUES (2,'L\'Eglise','56.145','-15.251','0001');
INSERT INTO `poi` VALUES (3,'Breeze','45.265','-16.120','0001');
INSERT INTO `poi` VALUES (4,'Casa Don Carlos','50.025','-13.121','0001');
INSERT INTO `poi` VALUES (5,'The Heart and Hand, Brighton','48.254','-12.354','0002');
INSERT INTO `poi` VALUES (6,'The Evening Star, Brighton','50.157','-12.200','0002');
INSERT INTO `poi` VALUES (7,'The King and Queen ','50.698','-14.587','0002');
INSERT INTO `poi` VALUES (8,'Grace Brighton','53.251','-13.248','0003');
INSERT INTO `poi` VALUES (9,'Oceana Brighton','50.314','-13.369','0003');
INSERT INTO `poi` VALUES (10,'Madame Geisha','47.58','-15.211','0003');
INSERT INTO `poi` VALUES (11,'Revolution','52.879','-16.587','0003');
INSERT INTO `poi` VALUES (12,'Coalition','50.258','-14.753','0003');
INSERT INTO `poi` VALUES (13,'Brighton Museum & Art Gallery','58.487','-17.187','0004');
INSERT INTO `poi` VALUES (14,'Brighton Toy and Model Museum','45.477','-16.248','0004');
INSERT INTO `poi` VALUES (15,'Brighton Fishing Museum & Quarter ','56.898','-11.157','0004');

CREATE TABLE `type` (
  `Id` varchar(11) NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `type` VALUES ('0001','Restaurants');
INSERT INTO `type` VALUES ('0002','Pubs');
INSERT INTO `type` VALUES ('0003','Clubs');
INSERT INTO `type` VALUES ('0004','Museum');