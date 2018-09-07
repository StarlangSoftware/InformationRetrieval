package InformationRetrieval.Query;

public class QueryResultItem {

    private int docId;
    private double score;

    public QueryResultItem(int docId, double score){
        this.docId = docId;
        this.score = score;
    }

    public int getDocId(){
        return docId;
    }

    public double getScore(){
        return score;
    }
}
