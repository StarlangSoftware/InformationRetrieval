package InformationRetrieval.Index;

import Dictionary.*;

import java.util.Collections;

public class TermDictionary extends Dictionary{

    public TermDictionary(WordComparator comparator){
        super(comparator);
    }

    public void addTerm(Word term){
        int middle = Collections.binarySearch(words, new Word(term.getName()), comparator);
        if (middle < 0){
            words.add(-middle - 1, term);
        }
    }
}
