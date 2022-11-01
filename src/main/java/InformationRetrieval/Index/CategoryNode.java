package InformationRetrieval.Index;

import DataStructure.CounterHashMap;
import Dictionary.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryNode {

    private String name;
    private ArrayList<CategoryNode> children = new ArrayList<>();
    private CategoryNode parent;

    private CounterHashMap<Integer> counts = new CounterHashMap<>();

    public CategoryNode(String name, CategoryNode parent){
        this.name = name;
        this.parent = parent;
        if (parent != null){
            parent.addChild(this);
        }
    }

    private void addChild(CategoryNode child){
        children.add(child);
    }

    public String getName(){
        return name;
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

    public ArrayList<CategoryNode> getChildren(){
        return children;
    }

    public List<Map.Entry<Integer, Integer>> topN(int N){
        if (N <= counts.size()){
            return counts.topN(N);
        } else {
            return counts.topN(counts.size());
        }
    }

    public String topNString(TermDictionary dictionary, int N){
        List<Map.Entry<Integer, Integer>> topN = topN(N);
        String result = toString();
        for (Map.Entry<Integer, Integer> item : topN){
            if (!Word.isPunctuation(dictionary.getWord(item.getKey()).getName())){
                result += "\t" + dictionary.getWord(item.getKey()).getName() + " (" + item.getValue() + ")";
            }
        }
        return result;
    }

    public String toString(){
        if (parent != null){
            if (parent.parent != null){
                return parent + "%" + name;
            } else {
                return name;
            }
        }
        return "";
    }
}
