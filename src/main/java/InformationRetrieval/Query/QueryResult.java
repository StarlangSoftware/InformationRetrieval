package InformationRetrieval.Query;

import DataStructure.Heap.MaxHeap;
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

    public ArrayList<QueryResultItem> getItems(){
        return items;
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
