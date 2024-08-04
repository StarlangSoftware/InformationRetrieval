package InformationRetrieval.Index;

/**
 * In a (non-positional) inverted index, a posting is just a document ID.
 */
public class Posting {

    protected int id;

    /**
     * Constructor for the Posting class. Sets the document id attribute.
     * @param Id Document id.
     */
    public Posting(int Id){
        this.id = Id;
    }

    /**
     * Accessor for the document id attribute.
     * @return Document id.
     */
    public int getId(){
        return id;
    }
}
