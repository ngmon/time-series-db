CREATE OR REPLACE FUNCTION upsertclassic(tabled varchar(20), dated TIMESTAMP,field varchar(10),valued DOUBLE PRECISION) RETURNS integer AS 
$$ 
DECLARE 
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
        sql := 'UPDATE '||tabled||' SET ('||sumf||','||countf||','||avgf||') = ('||sumf||' + '||valued||','||countf||'+1,('||sumf||'+'||valued||')/(1+'||countf||')) WHERE date ='||quote_nullable(dated);
	EXECUTE sql;
	GET DIAGNOSTICS counter = ROW_COUNT;
        IF counter>0 THEN
            RETURN 1;
        END IF; 
        BEGIN
            sql:='INSERT INTO '||tabled||' ('||avgf||','||countf||','||sumf||',date) VALUES ('||valued||',1,'||valued||','||quote_nullable(dated)||')';
	    EXECUTE sql;
            RETURN 1;
        EXCEPTION WHEN unique_violation THEN
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;