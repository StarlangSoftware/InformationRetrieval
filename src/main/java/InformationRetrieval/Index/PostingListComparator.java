package InformationRetrieval.Index;

import java.util.Comparator;

public class PostingListComparator implements Comparator<PostingList> {

    /**
     * Comparator method to compare two posting lists.
     * @param listA the first posting list to be compared.
     * @param listB the second posting list to be compared.
     * @return 1 if the size of the first posting list is larger than the second one, -1 if the size
     * of the first posting list is smaller than the second one, 0 if they are the same.
     */
    public int compare(PostingList listA, PostingList listB){
        return Integer.compare(listA.size(), listB.size());
    }

}
