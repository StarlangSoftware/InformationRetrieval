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
