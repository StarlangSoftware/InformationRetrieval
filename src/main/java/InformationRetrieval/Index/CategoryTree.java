package InformationRetrieval.Index;

import java.util.*;

public class CategoryTree {

    private CategoryNode root;

    public CategoryTree(String rootName){
        root = new CategoryNode(rootName, null);
    }

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

    public String topNString(TermDictionary dictionary, int N){
        Queue<CategoryNode> queue = new LinkedList<>();
        queue.add(root);
        String result = "";
        while (!queue.isEmpty()){
            CategoryNode node = queue.remove();
            if (node != root){
                result += node.topNString(dictionary, N) + "\n";
            }
            queue.addAll(node.getChildren());
        }
        return result;
    }

}
