package InformationRetrieval.Index;

import InformationRetrieval.Query.CategoryDeterminationType;
import InformationRetrieval.Query.Query;

import java.io.PrintWriter;
import java.util.*;

public class CategoryTree {

    private final CategoryNode root;

    /**
     * Simple constructor of the tree. Sets the root node of the tree.
     * @param rootName Category name of the root node.
     */
    public CategoryTree(String rootName){
        root = new CategoryNode(rootName, null);
    }

    /**
     * Adds a path (and if required nodes in the path) to the category tree according to the hierarchy string. Hierarchy
     * string is obtained by concatenating the names of all nodes in the path from root node to a leaf node separated
     * with '%'.
     * @param hierarchy Hierarchy string
     * @return The leaf node added when the hierarchy string is processed.
     */
    public CategoryNode addCategoryHierarchy(String hierarchy){
        String[] categories = hierarchy.split("%");
        CategoryNode current = root;
        for (String category : categories){
            CategoryNode node = current.getChild(category);
            if (node == null){
                node = new CategoryNode(category, current);
            }
            current = node;
        }
        return current;
    }

    /**
     * The method checks the query words in the category words of all nodes in the tree and returns the nodes that
     * satisfies the condition. If any word in the query appears in any category word, the node will be returned.
     * @param query Query string
     * @param dictionary Term dictionary
     * @param categoryDeterminationType Category determination type
     * @return The category nodes whose names contain at least one word from the query string
     */
    public ArrayList<CategoryNode> getCategories(Query query, TermDictionary dictionary, CategoryDeterminationType categoryDeterminationType){
        ArrayList<CategoryNode> result = new ArrayList<>();
        switch (categoryDeterminationType){
            default:
            case KEYWORD:
                root.getCategoriesWithKeyword(query, result);
                break;
            case COSINE:
                root.getCategoriesWithCosine(query, dictionary, result);
                break;
        }
        return result;
    }

    /**
     * The method sets the representative count. The representative count filters the most N frequent words.
     * @param representativeCount Number of representatives.
     */
    public void setRepresentativeCount(int representativeCount) {
        root.setRepresentativeCount(representativeCount);
    }

    /**
     * The method prints the representative words to the output file. Representative words are the
     * most frequent words that appears in the name of the product.
     * @param output Output file stream.
     * @param dictionary Term dictionary.
     */
    public void printRepresentatives(PrintWriter output, TermDictionary dictionary){
        root.printRepresentatives(output, dictionary);
    }
}
