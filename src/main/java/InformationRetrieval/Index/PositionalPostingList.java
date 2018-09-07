package InformationRetrieval.Index;

import InformationRetrieval.Query.QueryResult;

import java.util.ArrayList;
import java.util.Iterator;

public class PositionalPostingList {

    private ArrayList<PositionalPosting> postings;

    public PositionalPostingList(){
        postings = new ArrayList<PositionalPosting>();
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

    QueryResult toQueryResult(){
        QueryResult result = new QueryResult();
        for (PositionalPosting posting: postings){
            result.add(posting.getDocId());
        }
        return result;
    }

    void add(int docId, int position){
        int index = getIndex(docId);
        if (index == -1){
            postings.add(new PositionalPosting(docId));
            postings.get(postings.size() - 1).add(position);
        } else {
            postings.get(index).add(position);
        }
    }

    PositionalPosting get(int index){
        return postings.get(index);
    }

    PositionalPostingList intersection(PositionalPostingList secondList){
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

}
