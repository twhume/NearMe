DROP DATABASE IF EXISTS `nearme`;
CREATE DATABASE `nearme` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `nearme`;

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