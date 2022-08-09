package InformationRetrieval.Index;

public class PostingSkipList extends PostingList {
    boolean skipped;

    public PostingSkipList(){
        skipped = false;
    }

    public void add(int docId){
        PostingSkip p = new PostingSkip(docId);
        ((PostingSkip) postings.get(postings.size() - 1)).setNext(p);
        postings.add(p);
    }

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
