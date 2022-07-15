package InformationRetrieval.Index;

import InformationRetrieval.Query.QueryResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class PositionalPostingList {

    private ArrayList<PositionalPosting> postings;

    public PositionalPostingList(){
        postings = new ArrayList<>();
    }

    public PositionalPostingList(BufferedReader br, int count){
        postings = new ArrayList<>();
        try {
            for (int i = 0; i < count; i++){
                String line = br.readLine().trim();
                String[] ids = line.split(" ");
                int numberOfPositionalPostings = Integer.parseInt(ids[1]);
                if (ids.length == numberOfPositionalPostings + 2){
                    int docId = Integer.parseInt(ids[0]);
                    for (int j = 0; j < numberOfPositionalPostings; j++){
                        int positionalPosting = Integer.parseInt(ids[j + 2]);
                        add(docId, positionalPosting);
                    }
                } else {
                    System.out.println("Mismatch in the number of postings for word");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int size(){
        return postings.size();
    }

    public int getIndex(int docId){
        int begin = 0, end = size() - 1, middle;
        while (begin <= end){
            middle = (begin + end) / 2;
            if (docId == postings.get(middle).getDocId()){
                return middle;
            } else {
                if (docId < postings.get(middle).getDocId()){
                    end = middle - 1;
                } else {
                    begin = middle + 1;
                }
            }
        }
        return -1;
    }

    public QueryResult toQueryResult(){
        QueryResult result = new QueryResult();
        for (PositionalPosting posting: postings){
            result.add(posting.getDocId());
        }
        return result;
    }

    public void add(int docId, int position){
        int index = getIndex(docId);
        if (index == -1){
            postings.add(new PositionalPosting(docId));
            postings.get(postings.size() - 1).add(position);
        } else {
            postings.get(index).add(position);
        }
    }

    public PositionalPosting get(int index){
        return postings.get(index);
    }

    public PositionalPostingList union(PositionalPostingList secondList){
        PositionalPostingList result = new PositionalPostingList();
        result.postings = new ArrayList<>();
        result.postings.addAll(postings);
        result.postings.addAll(secondList.postings);
        return result;
    }

    public PositionalPostingList intersection(PositionalPostingList secondList){
        Iterator<PositionalPosting> iterator1 = postings.iterator(), iterator2 = secondList.postings.iterator();
        PositionalPosting p1 = iterator1.next(), p2 = iterator2.next();
        PositionalPostingList result = new PositionalPostingList();
        Iterator<Posting> positions1, positions2;
        Posting positionPointer1, positionPointer2;
        while (iterator1.hasNext() && iterator2.hasNext()){
            if (p1.getDocId() == p2.getDocId()){
                positions1 = p1.getPositions().iterator();
                positions2 = p2.getPositions().iterator();
                positionPointer1 = positions1.next();
                positionPointer2 = positions2.next();
                while (positions1.hasNext() && positions2.hasNext()){
                    if (positionPointer1.getId() + 1 == positionPointer2.getId()){
                        result.add(p1.getDocId(), positionPointer2.getId());
                        positionPointer1 = positions1.next();
                        positionPointer2 = positions2.next();
                    } else {
                        if (positionPointer1.getId() + 1 < positionPointer2.getId()){
                            positionPointer1 = positions1.next();
                        } else {
                            positionPointer2 = positions2.next();
                        }
                    }
                }
                p1 = iterator1.next();
                p2 = iterator2.next();
            } else {
                if (p1.getDocId() < p2.getDocId()){
                    p1 = iterator1.next();
                } else {
                    p2 = iterator2.next();
                }
            }
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
        for (PositionalPosting positionalPosting : postings){
            result += "\t" + positionalPosting.toString() + "\n";
        }
        return result;
    }

}
