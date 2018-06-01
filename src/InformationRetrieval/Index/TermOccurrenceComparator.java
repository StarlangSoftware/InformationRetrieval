package InformationRetrieval.Index;

import Dictionary.WordComparator;

import java.util.Comparator;

public class TermOccurrenceComparator implements Comparator<TermOccurrence> {

    WordComparator comparator;

    public TermOccurrenceComparator(WordComparator comparator){
        this.comparator = comparator;
    }

    public int compare(TermOccurrence termA, TermOccurrence termB) {
        int wordComparisonResult = comparator.compare(termA.getTerm(), termB.getTerm());
        if (wordComparisonResult != 0){
            return wordComparisonResult;
        } else {
            if (termA.getDocID() == termB.getDocID()){
                if (termA.getPosition() == termB.getPosition()){
                    return 0;
                } else {
                    if (termA.getPosition() < termB.getPosition()){
                        return -1;
                    } else {
                        return 1;
                    }
                }
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
