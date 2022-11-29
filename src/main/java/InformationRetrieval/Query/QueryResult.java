package InformationRetrieval.Query;

import DataStructure.Heap.MinHeap;

import java.util.ArrayList;

public class QueryResult {
    private final ArrayList<QueryResultItem> items;

    public QueryResult(){
        items = new ArrayList<QueryResultItem>();
    }

    public void add(int docId, double score){
        items.add(new QueryResultItem(docId, score));
    }

    public void add(int docId){
        items.add(new QueryResultItem(docId, 0.0));
    }

    public int size(){
        return items.size();
    }

    public ArrayList<QueryResultItem> getItems(){
        return items;
    }

    public QueryResult intersection(QueryResult queryResult){
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

    public void getBest(int K){
        QueryResultItemComparator comparator = new QueryResultItemComparator();
        MinHeap<QueryResultItem> minHeap = new MinHeap<>(2 * K, comparator);
        for (QueryResultItem queryResultItem : items){
            minHeap.insert(queryResultItem);
        }
        items.clear();
        for (int i = 0; i < K && !minHeap.isEmpty(); i++){
            items.add(minHeap.delete());
        }
    }
}
