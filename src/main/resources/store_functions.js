var f = new Array();

f.push({
    _id:"map", 
    value : 
    function(x) {        
        time = x.time;
        time.setTime(time.getTime()-time.getTime()%step);
        emit(time, x.data[field]);        
    }
});

f.push({
    _id:"map_cached", 
    value : 
    function(x) { 
        var id = {
            time: x.time, 
            match: hash,
            step: step
        };
        id.time.setTime(id.time.getTime()-id.time.getTime()%step);
        emit(id, x.data[field]);
    }
});

f.push({
    _id:"count_map", 
    value : 
    function(x) {        
        var time = x.time;
        time.setTime(time.getTime()-time.getTime()%step);
        emit(time, 1);  
    }
});

f.push({
    _id:"count_map_cached", 
    value : 
    function(x) { 
        var id = {
            time: x.t, 
            match: hash,
            step: step
        };
        id.time.setTime(id.time.getTime()-id.time.getTime()%step);
        emit(id, 1);
    }
});

f.push({
    _id:"count_reduce", 
    value : 
    function(id, values) { 
        return values.length; 
    }
});

f.push( {
    _id:"sum_reduce", 
    value : 
    function( id, values) { 
        var ret = 0;
        for(i = 0; i<values.length; i++){
            ret += values[i];
        }
        return ret; 
    }
});

f.push({
    _id:"avg_reduce", 
    value : 
    function(id, values) { 
        var ret = 0;
        for(i = 0; i<values.length; i++){
            ret += values[i];
        }
        return ret/values.length; 
    }
});

f.push({
    _id:"min_reduce", 
    value : 
    function(id, values) { 
        return Math.min.apply(Math, values);
    }
});

f.push({
    _id:"max_reduce", 
    value : 
    function(id, values) { 
        return Math.max.apply(Math, values);
    }
});

f.push({
    _id:"median_reduce", 
    value : 
    function(id, values) { 
        x= (values.length%2!=0)?values[(1+values.length)/2-1]:(values[values.length/2-1]+values[values.length/2])/2;
        print(x);
        return x;
    }
});
db.system.js.remove();
db.system.js.save(f);
