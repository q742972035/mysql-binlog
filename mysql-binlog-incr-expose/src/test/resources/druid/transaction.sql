START TRANSACTION;
INSERT INTO `test_binlog_inno`(`name`,`age`,`money`) VALUES('笑',11,11.11),('大',22,22.22),('超大',33,33.33);
UPDATE `test_binlog_inno` SET money = 0 WHERE id <=2;
COMMIT;