package InformationRetrieval.Index;

import java.util.ArrayList;

public class NGramIndex extends InvertedIndex{
    public NGramIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, int size) {
        super(dictionary, terms, size);
    }

    public NGramIndex(String fileName, int dictionarySize) {
        super(fileName, dictionarySize);
    }
}
