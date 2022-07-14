package InformationRetrieval.Index;

import InformationRetrieval.Query.QueryResult;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class PostingList {
    protected ArrayList<Posting> postings;

    public PostingList(){
        postings = new ArrayList<>();
    }

    public PostingList(String line){
        postings = new ArrayList<>();
        String[] ids = line.split(" ");
        for (String id : ids){
            add(Integer.parseInt(id));
        }
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

    public PostingList union(PostingList secondList){
        PostingList result = new PostingList();
        result.postings = new ArrayList<>();
        result.postings.addAll(postings);
        result.postings.addAll(secondList.postings);
        return result;
    }

    public QueryResult toQueryResult(){
        QueryResult result = new QueryResult();
        for (Posting posting:postings){
            result.add(posting.getId());
        }
        return result;
    }

    public void writeToFile(PrintWriter printWriter, int index){
        if (size() > 0){
            printWriter.write(index + " " + size() + "\n");
            printWriter.write(toString());
        }
    }

    public String toString(){
        String result = "";
        for (Posting posting : postings){
            result += posting.getId() + " ";
        }
        return result.trim() + "\n";
    }
}
