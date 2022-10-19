package InformationRetrieval.Document;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoryHierarchy {

    private final ArrayList<String> categoryList;

    public CategoryHierarchy(String list){
        categoryList = new ArrayList<>();
        categoryList.addAll(Arrays.asList(list.split("%")));
    }

    public String toString(){
        String result = categoryList.get(0);
        for (int i = 1; i < categoryList.size(); i++){
            result += "%" + categoryList.get(i);
        }
        return result;
    }
}
