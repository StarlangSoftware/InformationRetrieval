package InformationRetrieval.Index;

import DataStructure.CounterHashMap;
import InformationRetrieval.Query.Query;

import java.io.PrintWriter;
import java.util.*;

public class CategoryNode {

    private final ArrayList<CategoryNode> children = new ArrayList<>();
    private final CategoryNode parent;
    private CounterHashMap<Integer> counts = new CounterHashMap<>();
    private final ArrayList<String> categoryWords;

    /**
     * Constructor for the category node. Each category is represented as a tree node in the category tree. Category
     * words are constructed by splitting the name of the category w.r.t. space. Sets the parent node and adds this
     * node as a child to parent node.
     * @param name Name of the category.
     * @param parent Parent node of this node.
     */
    public CategoryNode(String name, CategoryNode parent){
        String[] words = name.split(" ");
        categoryWords = new ArrayList<>();
        categoryWords.addAll(Arrays.asList(words));
        this.parent = parent;
        if (parent != null){
            parent.addChild(this);
        }
    }

    /**
     * Adds the given child node to this node.
     * @param child New child node
     */
    private void addChild(CategoryNode child){
        children.add(child);
    }

    /**
     * Constructs the category name from the category words. Basically combines all category words separated with space.
     * @return Category name.
     */
    public String getName(){
        StringBuilder result = new StringBuilder(categoryWords.get(0));
        for (int i = 1; i < categoryWords.size(); i++){
            result.append(" ").append(categoryWords.get(i));
        }
        return result.toString();
    }

    /**
     * Searches the children of this node for a specific category name.
     * @param childName Category name of the child.
     * @return The child with the given category name.
     */
    public CategoryNode getChild(String childName){
        for (CategoryNode child : children){
            if (child.getName().equals(childName)){
                return child;
            }
        }
        return null;
    }

    /**
     * Adds frequency count of the term to the counts hash map of all ascendants of this node.
     * @param termId ID of the occurring term.
     * @param count Frequency of the term.
     */
    public void addCounts(int termId, int count){
        CategoryNode current = this;
        while (current.parent != null){
            current.counts.putNTimes(termId, count);
            current = current.parent;
        }
    }

    /**
     * Checks if the given node is an ancestor of the current node.
     * @param ancestor Node for which ancestor check will be done
     * @return True, if the given node is an ancestor of the current node.
     */
    public boolean isDescendant(CategoryNode ancestor){
        if (this.equals(ancestor)){
            return true;
        }
        if (parent == null){
            return false;
        }
        return parent.isDescendant(ancestor);
    }

    /**
     * Accessor of the children attribute
     * @return Children of the node
     */
    public ArrayList<CategoryNode> getChildren(){
        return children;
    }

    /**
     * Recursive method that returns the hierarchy string of the node. Hierarchy string is obtained by concatenating the
     * names of all ancestor nodes separated with '%'.
     * @return Hierarchy string of this node
     */
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

    /**
     * Recursive method that sets the representative count. The representative count filters the most N frequent words.
     * @param representativeCount Number of representatives.
     */
    public void setRepresentativeCount(int representativeCount){
        List<Map.Entry<Integer, Integer>> topList;
        if (representativeCount <= counts.size()){
            topList = counts.topN(representativeCount);
            counts = new CounterHashMap<>();
            for (Map.Entry<Integer, Integer> entry : topList){
                counts.putNTimes(entry.getKey(), entry.getValue());
            }
        }
        for (CategoryNode child : children){
            child.setRepresentativeCount(representativeCount);
        }
    }

    /**
     * Recursive method that prints the representative words to the output file. Representative words are the
     * most frequent words that appears in the name of the product.
     * @param output Output file stream.
     * @param dictionary Term dictionary.
     */
    public void printRepresentatives(PrintWriter output, TermDictionary dictionary){
        StringBuilder result = new StringBuilder();
        ArrayList<String> representatives = new ArrayList<>();
        for (int key : counts.keySet()){
            representatives.add(dictionary.getTerm(key).getName());
        }
        representatives.sort(String.CASE_INSENSITIVE_ORDER);
        for (String representative : representatives){
            result.append("\t").append(representative);
        }
        output.println(toString() + result);
        for (CategoryNode child : children){
            child.printRepresentatives(output, dictionary);
        }
    }

    /**
     * Recursive method that checks the query words in the category words of all descendants of this node and
     * accumulates the nodes that satisfies the condition. If any word  in the query appears in any category word, the
     * node will be accumulated.
     * @param query Query string
     * @param result Accumulator array
     */
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

    /**
     * Recursive method that checks the query words in the category words of all descendants of this node and
     * accumulates the nodes that satisfies the condition. If any word  in the query appears in any category word, the
     * node will be accumulated.
     * @param query Query string
     * @param dictionary Term dictionary
     * @param result Accumulator array
     */
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
