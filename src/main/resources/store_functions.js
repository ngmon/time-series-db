var f = new Array();


f.push({
    _id:"map", 
    value : 
    function() {        
        time = this.time;
        time.setTime(time.getTime()-time.getTime()%step);
        emit(time, this);        
    }
});

f.push({
    _id:"map_cached", 
    value : 
    function() { 
        var id = {
            time: this.t, 
            match: hash,
            step: step
        };
        id.time.setTime(id.time.getTime()-id.time.getTime()%step);
        emit(id, this.d[field]);
    }
});

f.push({
    _id:"count_map", 
    value : 
    function() {        
        time = this.time;
        time.setTime(time.getTime()-time.getTime()%step);
        emit(time, this.data);  
    }
});

f.push({
    _id:"count_map_cached", 
    value : 
    function() { 
        var id = {
            time: this.t, 
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
    function count_reduce(id, values) { 
        return values.length; 
    }
});

f.push( {
    _id:"sum_reduce", 
    value : 
    function(id, values) { 
        return Array.sum(values); 
    }
});

f.push({
    _id:"avg_reduce", 
    value : 
    function(id, values) { 
        return Array.avg(values); 
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
        return (values.length%2!=0)?values[(1+values.length)/2-1]:(values[values.length/2-1]+values[values.length/2])/2;
    }
});
db.system.js.remove();
db.system.js.save(f);
