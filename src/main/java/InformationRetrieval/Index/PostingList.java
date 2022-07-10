package InformationRetrieval.Index;

import InformationRetrieval.Query.QueryResult;

import java.util.ArrayList;
import java.util.Iterator;

public class PostingList {
    protected ArrayList<Posting> postings;

    public PostingList(){
        postings = new ArrayList<>();
    }

    public void add(int docId){
        postings.add(new Posting(docId));
    }

    public int size(){
        return postings.size();
    }

    public PostingList intersection(PostingList secondList){
        Iterator<Posting> iterator1 = postings.iterator(), iterator2 = secondList.postings.iterator();
        Posting p1 = iterator1.next(), p2 = iterator2.next();
        PostingList result = new PostingList();
        while (iterator1.hasNext() && iterator2.hasNext()){
            if (p1.getId() == p2.getId()){
                result.add(p1.getId());
                p1 = iterator1.next();
                p2 = iterator2.next();
            } else {
                if (p1.getId() < p2.getId()){
                    p1 = iterator1.next();
                } else {
                    p2 = iterator2.next();
                }
            }
        }
        return result;
    }

    public QueryResult toQueryResult(){
        QueryResult result = new QueryResult();
        for (Posting posting:postings){
            result.add(posting.getId());
        }
        return result;
    }

    public String toString(){
        String result = "";
        for (Posting posting : postings){
            result += posting.getId() + " ";
        }
        return result.trim() + "\n";
    }
}
