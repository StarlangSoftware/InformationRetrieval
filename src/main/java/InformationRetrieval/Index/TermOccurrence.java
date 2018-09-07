package InformationRetrieval.Index;

import Dictionary.Word;

public class TermOccurrence {
    private Word term;
    private int docID;
    private int position;

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
}
