CREATE  SEQUENCE inc START 1;
CREATE TABLE test1
(
id INTEGER DEFAULT NEXTVAL('inc'),
occurrenceTime VARCHAR(100),
detectionTime VARCHAR(100),
hostname VARCHAR(100),
type VARCHAR(100),
application VARCHAR(100),
process VARCHAR(100),
processId VARCHAR(100),
severity Integer,
priority Integer,
value VARCHAR(100),
CONSTRAINT primary_key PRIMARY KEY (id)
) ;