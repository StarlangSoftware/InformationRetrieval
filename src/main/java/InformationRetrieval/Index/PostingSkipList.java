package InformationRetrieval.Index;

public class PostingSkipList extends PostingList {
    private boolean skipped;

    /**
     * Constructor for the PostingSkipList class.
     */
    public PostingSkipList(){
        skipped = false;
    }

    /**
     * Adds a new posting (document id) to the posting skip list.
     * @param docId New document id to be added to the posting skip list.
     */
    public void add(int docId){
        PostingSkip p = new PostingSkip(docId);
        ((PostingSkip) postings.get(postings.size() - 1)).setNext(p);
        postings.add(p);
    }

    /**
     * Augments postings lists with skip pointers. Skip pointers are effectively shortcuts that allow us to avoid
     * processing parts of the postings list that will not figure in the search results. We follow a simple heuristic
     * for placing skips, which has been found to work well in practice, is that for a postings list of length P, use
     * square root of P evenly-spaced skip pointers.
     */
    public void addSkipPointers(){
        int i, j, N = (int) Math.sqrt(size());
        int posting;
        int skip;
        if (!skipped){
            skipped = true;
            for (i = 0, posting = 0; posting != postings.size(); posting++, i++){
                if (i % N == 0 && i + N < size()){
                    for (j = 0, skip = posting; j < N; skip++){
                        j++;
                    }
                    ((PostingSkip)postings.get(posting)).addSkip((PostingSkip) postings.get(skip));
                }
            }
        }
    }

    /**
     * Algorithm for the intersection of two postings skip lists p1 and p2. We maintain pointers into both lists and
     * walk through the two postings lists simultaneously, in time linear in the total number of postings entries. At
     * each step, we compare the docID pointed to by both pointers. If they are the same, we put that docID in the
     * results list, and advance both pointers. Otherwise, we advance the pointer pointing to the smaller docID or use
     * skip pointers to skip as many postings as possible.
     * @param secondList p2, second posting list.
     * @return Intersection of two postings lists p1 and p2.
     */
    public PostingSkipList intersection(PostingSkipList secondList){
        PostingSkip p1 = (PostingSkip) postings.get(0);
        PostingSkip p2 = (PostingSkip) secondList.postings.get(0);
        PostingSkipList result = new PostingSkipList();
        while (p1 != null && p2 != null){
            if (p1.getId() == p2.getId()){
                result.add(p1.getId());
                p1 = p1.next();
                p2 = p2.next();
            } else {
                if (p1.getId() < p2.getId()){
                    if (skipped && p1.hasSkip() && p1.getSkip().getId() < p2.getId()){
                        while (p1.hasSkip() && p1.getSkip().getId() < p2.getId()){
                            p1 = p1.getSkip();
                        }
                    } else {
                        p1 = p1.next();
                    }
                } else {
                    if (skipped && p2.hasSkip() && p2.getSkip().getId() < p1.getId()){
                        while (p2.hasSkip() && p2.getSkip().getId() < p1.getId()){
                            p2 = p2.getSkip();
                        }
                    } else {
                        p2 = p2.next();
                    }
                }
            }
        }
        return result;
    }

}
