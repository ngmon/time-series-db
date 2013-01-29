time-series-db
==============
Mongo Query API for aggregation results

Invoking of query execution one can obtain events or metrics applied on events.
Query events :
* field - setting filter operators


Filter operators
* for simple value:
   * exists	 
   * doesNotExist	
   * greaterThan
   * greaterThanOrEq
   * lessThan
   * lessThanOrEq	 
   * equal
   * notEqual	 

* for Array:
   * hasThisOne	 	
   * hasAllOf	 
   * hasAnyOf	 	
   * hasNoneOf	 
   * hasThisElement	 
