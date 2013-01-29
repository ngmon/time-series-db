time-series-db
==============
Mongo Query API for aggregation results

Invoking of query execution one can obtain events or metrics applied on events.

Query events :
* field - setting filter operators
    * on non-array fields:
       * exists,	doesNotExist	
       * greaterThan, greaterThanOrEq, lessThan, lessThanOrEq
       * equal, notEqual	 
    
    * on arrays:
       * hasThisOne, hasAllOf, hasAnyOf, hasNoneOf, hasThisElement	 
* orderAsc/orderDesc
* limit
* skip
* fromDate/toDate

Query metrics on events:
* count
* sum
* max
* min
* avg
