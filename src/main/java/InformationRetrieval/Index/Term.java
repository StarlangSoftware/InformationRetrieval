package InformationRetrieval.Index;

import Dictionary.Word;

/**
 * A term is a (perhaps normalized) type that is included in the IR system’s dictionary.
 */
public class Term extends Word {
    private final int termId;

    /**
     * Constructor for the Term class. Sets the fields.
     * @param name Text of the term
     * @param termId Id of the term
     */
    public Term(String name, int termId){
        super(name);
        this.termId = termId;
    }

    /**
     * Accessor for the term id attribute.
     * @return Term id attribute
     */
    public int getTermId() {
        return termId;
    }

}
