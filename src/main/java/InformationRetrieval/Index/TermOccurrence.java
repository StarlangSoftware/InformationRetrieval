package InformationRetrieval.Index;

import Dictionary.Word;
import Dictionary.WordComparator;

public class TermOccurrence {
    private final Word term;
    private final int docID;
    private final int position;

    public TermOccurrence(Word term, int docID, int position){
        this.term = term;
        this.docID = docID;
        this.position = position;
    }

    public Word getTerm(){
        return term;
    }

    public int getDocID(){
        return docID;
    }

    public int getPosition(){
        return position;
    }

    public boolean isDifferent(TermOccurrence currentTerm, WordComparator comparator){
        return comparator.compare(term, currentTerm.getTerm()) != 0;
    }
}
