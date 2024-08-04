package InformationRetrieval.Index;

import java.util.ArrayList;

/**
 * In a positional inverted index, a posting is a document ID and a list of positions in the document for the term.
 */
public class PositionalPosting {
    private final ArrayList<Posting> positions;
    private final int docId;

    /**
     * Constructor for the PositionalPosting class. Sets the document id and initializes the position array.
     * @param docId document id of the posting.
     */
    public PositionalPosting(int docId){
        this.docId = docId;
        positions = new ArrayList<>();
    }

    /**
     * Adds a position to the position list.
     * @param position Position added to the position list.
     */
    public void add(int position){
        positions.add(new Posting(position));
    }

    /**
     * Accessor for the document id attribute.
     * @return Document id.
     */
    public int getDocId(){
        return docId;
    }

    /**
     * Accessor for the positions attribute.
     * @return Position list.
     */
    public ArrayList<Posting> getPositions(){
        return positions;
    }

    /**
     * Returns size of the position list.
     * @return Size of the position list.
     */
    public int size(){
        return positions.size();
    }

    /**
     * Converts the positional posting to a string. String is of the form, document id, number of positions, and all
     * positions separated via space.
     * @return String form of the positional posting.
     */
    public String toString(){
        StringBuilder result = new StringBuilder(docId + " " + positions.size());
        for (Posting posting : positions){
            result.append(" ").append(posting.getId());
        }
        return result.toString();
    }
}
