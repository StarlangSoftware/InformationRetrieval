package InformationRetrieval.Index;

import Dictionary.Word;

public class Term extends Word {
    private final int termId;

    public Term(String name, int termId){
        super(name);
        this.termId = termId;
    }

    public int getTermId() {
        return termId;
    }

}
