package InformationRetrieval.Query;

import java.util.Comparator;

public class QueryResultItemComparator implements Comparator<QueryResultItem> {

    /**
     * Compares two query result items according to their scores.
     * @param resultA the first query result item to be compared.
     * @param resultB the second query result item to be compared.
     * @return -1 if the score of the first item is smaller than the score of the second item; 1 if the score of the
     * first item is larger than the score of the second item; 0 otherwise.
     */
    public int compare(QueryResultItem resultA, QueryResultItem resultB){
        return Double.compare(resultA.getScore(), resultB.getScore());
    }
}
