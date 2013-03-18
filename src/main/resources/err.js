map = function(x) {        
        var time = x.time;
        time.setTime(time.getTime()-time.getTime()%1000);
        emit(time, 1);  
    };
    
reduce = function(id, values) { 
        return values.length; 
    };
    
mapstore = {
    _id : "map", 
    value : map    
};

db.system.js.remove();
db.system.js.save(mapstore);

db.tester.drop();
db.tester.save({time: new Date(), value : 1});

db.runCommand({
                 mapReduce: 'tester',
                 map: "map(this)", // map or "map()"
                 reduce: reduce,
                 out: { inline: 1 }             
               });