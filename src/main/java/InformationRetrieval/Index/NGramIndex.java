package InformationRetrieval.Index;

import Dictionary.WordComparator;

import java.util.ArrayList;

public class NGramIndex extends InvertedIndex{

    /**
     * Empty constructor for the NGram index.
     */
    public NGramIndex(){
        super();
    }

    /**
     * Constructs an NGram index from a list of sorted tokens. The terms array should be sorted before calling this
     * method. Calls the constructor for the InvertedIndex.
     * @param dictionary Term dictionary
     * @param terms Sorted list of tokens in the memory collection.
     * @param comparator Comparator method to compare two terms.
     */
    public NGramIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, WordComparator comparator) {
        super(dictionary, terms, comparator);
    }

    /**
     * Reads the NGram index from an input file.
     * @param fileName Input file name for the NGram index.
     */
    public NGramIndex(String fileName) {
        super(fileName);
    }

}
