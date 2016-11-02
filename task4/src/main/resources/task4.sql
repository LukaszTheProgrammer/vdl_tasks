DROP TABLE IF EXISTS Characters;

CREATE TABLE Characters (ID int, NAME VARCHAR(128));

INSERT INTO Characters VALUES ( 1, 'John|Snow'), (2, 'Bruce|Banner'), (3,'Bruce|Wayne'), (4,'Julio|Jones|Falcons');

DELIMITER ;;
DROP PROCEDURE IF EXISTS NUMBERS_TABLE;
CREATE PROCEDURE NUMBERS_TABLE()
  BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE max_number  INT DEFAULT (SELECT MAX( CHAR_LENGTH(Characters.name) - CHAR_LENGTH(REPLACE(Characters.name, '|', ''))) FROM Characters);
    DROP TABLE IF EXISTS TEMP_NUMBERS;
    CREATE TABLE TEMP_NUMBERS (n int);

    WHILE i <= max_number+1 DO
      INSERT INTO TEMP_NUMBERS VALUES (i);
      SET i = i +1;
    END WHILE;
  END;;

CALL NUMBERS_TABLE();

select
  CONCAT(Characters.id, ', ',
  SUBSTRING_INDEX(SUBSTRING_INDEX(Characters.name, '|', numbers.n), '|', -1)) name
from
  (SELECT n FROM TEMP_NUMBERS) numbers INNER JOIN Characters
    on CHAR_LENGTH(Characters.name)
       -CHAR_LENGTH(REPLACE(Characters.name, '|', ''))>=numbers.n-1
order by
  Characters.id, numbers.n;
