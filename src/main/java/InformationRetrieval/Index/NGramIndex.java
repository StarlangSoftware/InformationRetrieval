package InformationRetrieval.Index;

import Dictionary.Word;
import Dictionary.WordComparator;

import java.util.ArrayList;

public class NGramIndex extends InvertedIndex{

    public NGramIndex(){
        super();
    }
    public NGramIndex(TermDictionary dictionary, ArrayList<TermOccurrence> terms, WordComparator comparator) {
        super(dictionary, terms, comparator);
    }

    public NGramIndex(String fileName) {
        super(fileName);
    }

    public static ArrayList<TermOccurrence> constructNGrams(String word, int termId, int k){
        ArrayList<TermOccurrence> nGrams = new ArrayList<>();
        if (word.length() >= k - 1){
            for (int l = -1; l < word.length() - k + 2; l++){
                String term;
                if (l == -1){
                    term = "$" + word.substring(0, k - 1);
                } else {
                    if (l == word.length() - k + 1){
                        term = word.substring(l, l + k - 1) + "$";
                    } else {
                        term = word.substring(l, l + k);
                    }
                }
                nGrams.add(new TermOccurrence(new Word(term), termId, l));
            }
        }
        return nGrams;
    }
}
