package InformationRetrieval.Index;

import java.util.ArrayList;

public class PositionalPosting {
    private ArrayList<Posting> positions;
    private int docId;

    public PositionalPosting(int docId){
        this.docId = docId;
        positions = new ArrayList<Posting>();
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
}
