package InformationRetrieval.Index;

import Dictionary.WordComparator;

import java.util.ArrayList;

public class NGramIndex extends InvertedIndex{
    public NGramIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, int size, WordComparator comparator) {
        super(dictionary, terms, size, comparator);
    }

    public NGramIndex(String fileName, int dictionarySize) {
        super(fileName);
    }
}
