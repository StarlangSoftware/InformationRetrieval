package InformationRetrieval.Query;

import Dictionary.Word;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Query {
    private final ArrayList<Word> terms;

    private static final ArrayList<String> shortcuts = new ArrayList<>(Arrays.asList("cc", "cm2", "cm", "gb", "ghz", "gr", "gram", "hz", "inc", "inch", "inç",
            "kg", "kw", "kva", "litre", "lt", "m2", "m3", "mah", "mb", "metre", "mg", "mhz", "ml", "mm", "mp", "ms", "kb", "mb", "gb", "tb", "pb", "kbps",
            "mt", "mv", "tb", "tl", "va", "volt", "watt", "ah", "hp", "oz", "rpm", "dpi", "ppm", "ohm", "kwh", "kcal", "kbit", "mbit", "gbit", "bit", "byte",
            "mbps", "gbps", "cm3", "mm2", "mm3", "khz", "ft", "db", "sn", "g", "v", "m", "l", "w", "s"));

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

    public Query filterAttributes(HashSet<String> attributeList, Query termAttributes, Query phraseAttributes){
        int i = 0;
        Query filteredQuery = new Query();
        while (i < this.terms.size()){
            if (i < this.terms.size() - 1){
                String pair = this.terms.get(i).getName() + " " + this.terms.get(i + 1).getName();
                if (attributeList.contains(pair)){
                    phraseAttributes.terms.add(new Word(pair));
                    i += 2;
                    continue;
                }
                if (shortcuts.contains(this.terms.get(i + 1).getName())){
                    Pattern p = Pattern.compile("([-+]?\\d+)|([-+]?\\d+\\.\\d+)|(\\d*\\.\\d+)");
                    if (p.matcher(this.terms.get(i).getName()).matches()){
                        phraseAttributes.terms.add(new Word(pair));
                        i += 2;
                        continue;
                    }
                }
            }
            if (attributeList.contains(this.terms.get(i).getName())){
                termAttributes.terms.add(this.terms.get(i));
            } else {
                filteredQuery.terms.add(this.terms.get(i));
            }
            i++;
        }
        return filteredQuery;
    }
}
