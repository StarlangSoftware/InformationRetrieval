package InformationRetrieval.Index;

import DataStructure.CounterHashMap;
import InformationRetrieval.Query.Query;

import java.util.*;

public class CategoryNode {

    private final ArrayList<CategoryNode> children = new ArrayList<>();
    private final CategoryNode parent;
    private CounterHashMap<Integer> counts = new CounterHashMap<>();
    private final ArrayList<String> categoryWords;

    public CategoryNode(String name, CategoryNode parent){
        String[] words = name.split(" ");
        categoryWords = new ArrayList<>();
        for (String word : words){
            categoryWords.add(word);
        }
        this.parent = parent;
        if (parent != null){
            parent.addChild(this);
        }
    }

    private void addChild(CategoryNode child){
        children.add(child);
    }

    public String getName(){
        String result = categoryWords.get(0);
        for (int i = 1; i < categoryWords.size(); i++){
            result += " " + categoryWords.get(i);
        }
        return result;
    }

    public CategoryNode getChild(String childName){
        for (CategoryNode child : children){
            if (child.getName().equals(childName)){
                return child;
            }
        }
        return null;
    }

    public void addCounts(int termId, int count){
        CategoryNode current = this;
        while (current.parent != null){
            current.counts.putNTimes(termId, count);
            current = current.parent;
        }
    }

    public boolean isDescendant(CategoryNode ancestor){
        if (this.equals(ancestor)){
            return true;
        }
        if (parent == null){
            return false;
        }
        return parent.isDescendant(ancestor);
    }

    public ArrayList<CategoryNode> getChildren(){
        return children;
    }

    public String toString(){
        if (parent != null){
            if (parent.parent != null){
                return parent + "%" + getName();
            } else {
                return getName();
            }
        }
        return "";
    }

    public void setRepresentativeCount(int representativeCount){
        List<Map.Entry<Integer, Integer>> topList;
        if (representativeCount <= counts.size()){
            topList = counts.topN(representativeCount);
            counts = new CounterHashMap<>();
            for (Map.Entry<Integer, Integer> entry : topList){
                counts.putNTimes(entry.getKey(), entry.getValue());
            }
        }
    }

    public void getCategoriesWithKeyword(Query query, ArrayList<CategoryNode> result){
        double categoryScore = 0;
        for (int i = 0; i < query.size(); i++){
            if (categoryWords.contains(query.getTerm(i).getName())){
                categoryScore++;
            }
        }
        if (categoryScore > 0){
            result.add(this);
        }
        for (CategoryNode child : children){
            child.getCategoriesWithKeyword(query, result);
        }
    }

    public void getCategoriesWithCosine(Query query, TermDictionary dictionary, ArrayList<CategoryNode> result){
        double categoryScore = 0;
        for (int i = 0; i < query.size(); i++){
            Term term = (Term) dictionary.getWord(query.getTerm(i).getName());
            if (term != null){
                categoryScore += counts.count(term.getTermId());
            }
        }
        if (categoryScore > 0){
            result.add(this);
        }
        for (CategoryNode child : children){
            child.getCategoriesWithCosine(query, dictionary, result);
        }
    }

}
