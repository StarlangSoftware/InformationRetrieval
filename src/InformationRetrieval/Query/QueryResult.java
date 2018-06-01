package InformationRetrieval.Query;

import java.util.ArrayList;
import java.util.Collections;

public class QueryResult {
    private ArrayList<QueryResultItem> items;

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

    public void sort(){
        QueryResultItemComparator comparator = new QueryResultItemComparator();
        Collections.sort(items, comparator);
    }
}
