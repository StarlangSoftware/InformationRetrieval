package InformationRetrieval.Query;

public class QueryResultItem {

    private final int docId;
    private final double score;

    /**
     * Constructor for the QueryResultItem class. Sets the document id and score of a single query result.
     * @param docId Id of the document that satisfies the query.
     * @param score Score of the document for the query.
     */
    public QueryResultItem(int docId, double score){
        this.docId = docId;
        this.score = score;
    }

    /**
     * Accessor for the docID attribute.
     * @return docID attribute
     */
    public int getDocId(){
        return docId;
    }

    /**
     * Accessor for the score attribute.
     * @return score attribute.
     */
    public double getScore(){
        return score;
    }
}
