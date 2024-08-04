package InformationRetrieval.Query;

import DataStructure.Heap.MinHeap;

import java.util.ArrayList;

public class QueryResult {
    private final ArrayList<QueryResultItem> items;

    /**
     * Empty constructor forthe QueryResult object.
     */
    public QueryResult(){
        items = new ArrayList<QueryResultItem>();
    }

    /**
     * Adds a new result item to the list of query result.
     * @param docId Document id of the result
     * @param score Score of the result
     */
    public void add(int docId, double score){
        items.add(new QueryResultItem(docId, score));
    }

    /**
     * Adds a new result item with score 0 to the list of query result.
     * @param docId Document id of the result
     */
    public void add(int docId){
        items.add(new QueryResultItem(docId, 0.0));
    }

    /**
     * Returns number of results for query
     * @return Number of results for query
     */
    public int size(){
        return items.size();
    }

    /**
     * Returns result list for query
     * @return Result list for query
     */
    public ArrayList<QueryResultItem> getItems(){
        return items;
    }

    /**
     * Given two query results, this method identifies the intersection of those two results by doing parallel iteration
     * in O(N).
     * @param queryResult Second query result to be intersected.
     * @return Intersection of this query result with the second query result
     */
    public QueryResult intersectionFastSearch(QueryResult queryResult){
        QueryResult result = new QueryResult();
        int i = 0, j = 0;
        while (i < size() && j < queryResult.size()){
            QueryResultItem item1 = items.get(i);
            QueryResultItem item2 = queryResult.items.get(j);
            if (item1.getDocId() == item2.getDocId()){
                result.add(item1.getDocId());
                i++;
                j++;
            } else {
                if (item1.getDocId() < item2.getDocId()){
                    i++;
                } else {
                    j++;
                }
            }
        }
        return result;
    }

    /**
     * Given two query results, this method identifies the intersection of those two results by doing binary search on
     * the second list in O(N log N).
     * @param queryResult Second query result to be intersected.
     * @return Intersection of this query result with the second query result
     */
    public QueryResult intersectionBinarySearch(QueryResult queryResult){
        QueryResult result = new QueryResult();
        for (QueryResultItem searchedItem : items){
            int low = 0;
            int high = queryResult.size() - 1;
            int middle = (low + high) / 2;
            boolean found = false;
            while (low <= high){
                if (searchedItem.getDocId() > queryResult.items.get(middle).getDocId()){
                    low = middle + 1;
                } else {
                    if (searchedItem.getDocId() < queryResult.items.get(middle).getDocId()){
                        high = middle - 1;
                    } else {
                        found = true;
                        break;
                    }
                }
                middle = (low + high) / 2;
            }
            if (found){
                result.add(searchedItem.getDocId(), searchedItem.getScore());
            }
        }
        return result;
    }

    /**
     * Given two query results, this method identifies the intersection of those two results by doing exhaustive search
     * on the second list in O(N^2).
     * @param queryResult Second query result to be intersected.
     * @return Intersection of this query result with the second query result
     */
    public QueryResult intersectionLinearSearch(QueryResult queryResult){
        QueryResult result = new QueryResult();
        for (QueryResultItem searchedItem : items){
            for (QueryResultItem item : queryResult.items){
                if (searchedItem.getDocId() == item.getDocId()){
                    result.add(searchedItem.getDocId(), searchedItem.getScore());
                }
            }
        }
        return result;
    }

    /**
     * The method returns K best results from the query result using min heap in O(K log N + N log K) time.
     * @param K Size of the best subset.
     */
    public void getBest(int K){
        QueryResultItemComparator comparator = new QueryResultItemComparator();
        MinHeap<QueryResultItem> minHeap = new MinHeap<>(K, comparator);
        for (int i = 0; i < K && i < items.size(); i++){
            minHeap.insert(items.get(i));
        }
        for (int i = K + 1; i < items.size(); i++){
            QueryResultItem top = minHeap.delete();
            if (comparator.compare(top, items.get(i)) > 0){
                minHeap.insert(top);
            } else {
                minHeap.insert(items.get(i));
            }
        }
        items.clear();
        for (int i = 0; i < K && !minHeap.isEmpty(); i++){
            items.add(0, minHeap.delete());
        }
    }
}
