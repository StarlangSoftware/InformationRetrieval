package InformationRetrieval.Index;

import Dictionary.Word;
import Dictionary.WordComparator;

/**
 * Stores a single occurrence of a term in a document.
 */
public class TermOccurrence {
    private final Word term;
    private final int docID;
    private final int position;

    /**
     * Constructor for the TermOccurrence class. Sets the attributes.
     * @param term Term for this occurrence.
     * @param docID Document id of the term occurrence.
     * @param position Position of the term in the document for this occurrence.
     */
    public TermOccurrence(Word term, int docID, int position){
        this.term = term;
        this.docID = docID;
        this.position = position;
    }

    /**
     * Accessor for the term.
     * @return Term
     */
    public Word getTerm(){
        return term;
    }

    /**
     * Accessor for the document id.
     * @return Document id.
     */
    public int getDocID(){
        return docID;
    }

    /**
     * Accessor for the position of the term.
     * @return Position of the term.
     */
    public int getPosition(){
        return position;
    }

    /**
     * Checks if the current occurrence is different from the other occurrence.
     * @param currentTerm Term occurrence to be compared.
     * @param comparator Comparator function to compare two terms.
     * @return True, if two terms are different; false if they are the same.
     */
    public boolean isDifferent(TermOccurrence currentTerm, WordComparator comparator){
        return comparator.compare(term, currentTerm.getTerm()) != 0;
    }
}
