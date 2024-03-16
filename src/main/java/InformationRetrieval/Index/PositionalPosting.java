package InformationRetrieval.Index;

import java.util.ArrayList;

public class PositionalPosting {
    private final ArrayList<Posting> positions;
    private final int docId;

    public PositionalPosting(int docId){
        this.docId = docId;
        positions = new ArrayList<>();
    }

    public void add(int position){
        positions.add(new Posting(position));
    }

    public int getDocId(){
        return docId;
    }

    public ArrayList<Posting> getPositions(){
        return positions;
    }

    public int size(){
        return positions.size();
    }

    public String toString(){
        StringBuilder result = new StringBuilder(docId + " " + positions.size());
        for (Posting posting : positions){
            result.append(" ").append(posting.getId());
        }
        return result.toString();
    }
}
