time-series-db
==============

Mongo Query API for aggregation results. <br>
Invoking of query execution one can obtain __events__ or __metrics__ counted from events.

Query __events__ :
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

Query __metrics__ counted from events (with variable time step defined):
* count
* sum
* max
* min
* avg

_______
Internal storage
<pre>
{
  t:time,             //stored as long!
  s:source,
  d:  {               //event data
      value1:{...},
      value2:{...}
      }
}
</pre>
