package InformationRetrieval.Query;

import java.util.Comparator;

public class QueryResultItemComparator implements Comparator<QueryResultItem> {

    public int compare(QueryResultItem resultA, QueryResultItem resultB){
        if (resultA.getScore() > resultB.getScore()){
            return -1;
        } else {
            if (resultA.getScore() < resultB.getScore()){
                return 1;
            } else {
                return 0;
            }
        }
    }
}
