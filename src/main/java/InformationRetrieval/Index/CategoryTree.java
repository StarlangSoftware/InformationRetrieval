package InformationRetrieval.Index;

import InformationRetrieval.Query.CategoryDeterminationType;
import InformationRetrieval.Query.Query;

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

    public void setRepresentativeCount(int representativeCount) {
        root.setRepresentativeCount(representativeCount);
    }
}
