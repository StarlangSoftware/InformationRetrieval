package InformationRetrieval.Query;

import Dictionary.Word;
import java.util.ArrayList;
import java.util.HashSet;

public class Query {
    private final ArrayList<Word> terms;

    public Query(){
        terms = new ArrayList<>();
    }
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

    public void filterAttributes(HashSet<String> attributeList, Query termAttributes, Query phraseAttributes){
        int i = 0;
        while (i < this.terms.size()){
            if (i < this.terms.size() - 1){
                String pair = this.terms.get(i).getName() + " " + this.terms.get(i + 1).getName();
                if (attributeList.contains(pair)){
                    phraseAttributes.terms.add(new Word(pair));
                    i += 2;
                    continue;
                }
            }
            if (attributeList.contains(this.terms.get(i).getName())){
                termAttributes.terms.add(this.terms.get(i));
            }
            i++;
        }
    }
}
