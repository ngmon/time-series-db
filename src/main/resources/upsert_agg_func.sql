CREATE OR REPLACE FUNCTION upsert(tabled varchar(20), dated TIMESTAMP,field varchar(10),begind TIMESTAMP,endd TIMESTAMP) RETURNS integer AS 
$$ 
DECLARE 
sum DOUBLE PRECISION;
count DOUBLE PRECISION;
avg DOUBLE PRECISION;
sumf varchar(10);
countf varchar(10);
avgf varchar(10);
sql varchar(200);
counter integer;
BEGIN
    sumf:='sum' || field;
    avgf:='avg' || field;
    countf:='count' || field;        
    LOOP
	SELECT SUM(value),COUNT(value), AVG(value) INTO sum,count,avg FROM EVENT WHERE date>=begind AND date<endd;	
        sql := 'UPDATE '||tabled||' SET ('||avgf||','||countf||','||sumf||')=('||avg||','||count||','||sum||') WHERE date = '||quote_nullable(dated);
	EXECUTE sql;
	GET DIAGNOSTICS counter = ROW_COUNT;
        IF counter>0 THEN
            RETURN 1;
        END IF; 
        BEGIN
            sql:='INSERT INTO '||tabled||' ('||avgf||','||countf||','||sumf||',date) VALUES ('||avg||','||count||','||sum||','||quote_nullable(dated)||')';
	    EXECUTE sql;
            RETURN 1;
        EXCEPTION WHEN unique_violation THEN
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;