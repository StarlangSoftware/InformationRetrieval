package InformationRetrieval.Query;

import Dictionary.Word;
import java.util.ArrayList;

public class Query {
    private final ArrayList<Word> terms;

    public Query(String query){
        terms = new ArrayList<>();
        String[] terms = query.split(" ");
        for (String term: terms){
            this.terms.add(new Word(term));
        }
    }

    public Word getTerm(int index){
        return terms.get(index);
    }

    public int size(){
        return terms.size();
    }
}
