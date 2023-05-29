package InformationRetrieval.Query;

import java.util.Comparator;

public class QueryResultItemComparator implements Comparator<QueryResultItem> {

    public int compare(QueryResultItem resultA, QueryResultItem resultB){
        return Double.compare(resultA.getScore(), resultB.getScore());
    }
}
