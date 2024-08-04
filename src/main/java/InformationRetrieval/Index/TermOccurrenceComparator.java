package InformationRetrieval.Index;

import Dictionary.WordComparator;

import java.util.Comparator;

public class TermOccurrenceComparator implements Comparator<TermOccurrence> {

    WordComparator comparator;

    /**
     * Constructor for the TermOccurrenceComparator class. Sets the word comparator.
     * @param comparator Word comparator used in term occurrence comparator.
     */
    public TermOccurrenceComparator(WordComparator comparator){
        this.comparator = comparator;
    }

    /**
     * Compares two term occurrences.
     * @param termA the first term occurrence to be compared.
     * @param termB the second term occurrence to be compared.
     * @return If the term of the first term occurrence is different from the term of the second term occurrence then
     * the method returns the comparison result between those two terms lexicographically. If the term of the first term
     * occurrence is same as the term of the second term occurrence then the term occurrences are compared with respect
     * to their document ids. If the first has smaller document id, the method returns -1; if the second has smaller
     * document id, the method returns +1.  As the third comparison criteria, if also the document ids are the same,
     * the method compares term occurrences with respect to the position. If the first has smaller position, the method
     * returns -1; if the second has smaller position id, the method returns +1, and if all three features are the same,
     * the method returns 0.
     */
    public int compare(TermOccurrence termA, TermOccurrence termB) {
        int wordComparisonResult = comparator.compare(termA.getTerm(), termB.getTerm());
        if (wordComparisonResult != 0){
            return wordComparisonResult;
        } else {
            if (termA.getDocID() == termB.getDocID()){
                return Integer.compare(termA.getPosition(), termB.getPosition());
            } else {
                if (termA.getDocID() < termB.getDocID()){
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

}
