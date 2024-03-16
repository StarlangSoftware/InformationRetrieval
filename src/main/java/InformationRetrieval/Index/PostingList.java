package InformationRetrieval.Index;

import InformationRetrieval.Query.QueryResult;

import java.io.PrintWriter;
import java.util.ArrayList;

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
        int i = 0, j = 0;
        PostingList result = new PostingList();
        while (i < size() && j < secondList.size()){
            Posting p1 = postings.get(i);
            Posting p2 = secondList.postings.get(j);
            if (p1.getId() == p2.getId()){
                result.add(p1.getId());
                i++;
                j++;
            } else {
                if (p1.getId() < p2.getId()){
                    i++;
                } else {
                    j++;
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
        StringBuilder result = new StringBuilder();
        for (Posting posting : postings){
            result.append(posting.getId()).append(" ");
        }
        return result.toString().trim() + "\n";
    }
}
