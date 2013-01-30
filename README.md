time-series-db
==============

Mongo Query API for aggregation results. <br>
Invoking of query execution one can obtain __events__ or __metrics__ computed from events.

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
* fromDate/toDate

Query __metrics__ counted from events (*with variable time step defined):
* count total
* count*
* sum*
* max*
* min*
* avg*
* distinct

_______
Internal storage
<pre>
{
  _id:id              //generated
  t:time,             //stored as ISO Date
  s:source,           //type,source of data
  d:  {               //event data
      value1:{...},
      value2:{...}
      }
}
</pre>
