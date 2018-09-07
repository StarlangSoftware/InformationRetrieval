package InformationRetrieval.Index;

import java.util.Comparator;

public class PostingListComparator implements Comparator<PostingList> {

    public int compare(PostingList listA, PostingList listB){
        if (listA.size() < listB.size()){
            return -1;
        } else {
            if (listA.size() > listB.size()){
                return 1;
            } else {
                return 0;
            }
        }
    }

}
