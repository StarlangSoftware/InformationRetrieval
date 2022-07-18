package InformationRetrieval.Index;

import Dictionary.WordComparator;

import java.util.ArrayList;

public class NGramIndex extends InvertedIndex{
    public NGramIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, WordComparator comparator) {
        super(dictionary, terms, comparator);
    }

    public NGramIndex(String fileName) {
        super(fileName);
    }
}
