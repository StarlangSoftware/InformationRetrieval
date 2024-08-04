package InformationRetrieval.Index;

import InformationRetrieval.Query.QueryResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * For each term, we have a list that records which documents and also positions the term occurs in. Each item in the
 * list
 */
public class PositionalPostingList {

    private ArrayList<PositionalPosting> postings;

    /**
     * Constructor of the PositionalPostingList class. Initializes the list.
     */
    public PositionalPostingList(){
        postings = new ArrayList<>();
    }

    /**
     * Reads a positional posting list from a file. Reads N lines, where each line stores a positional posting. The
     * first item in the line shows document id. The second item in the line shows the number of positional postings.
     * Other items show the positional postings.
     * @param br Input stream to read from.
     * @param count Number of positional postings for this positional posting list.
     */
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

    /**
     * Returns the number of positional postings in the posting list.
     * @return Number of positional postings in the posting list.
     */
    public int size(){
        return postings.size();
    }

    /**
     * Does a binary search on the positional postings list for a specific document id.
     * @param docId Document id to be searched.
     * @return The position of the document id in the positional posting list. If it does not exist, the method returns
     * -1.
     */
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

    /**
     * Converts the positional postings list to a query result object. Simply adds all positional postings one by one
     * to the result.
     * @return QueryResult object containing the positional postings in this object.
     */
    public QueryResult toQueryResult(){
        QueryResult result = new QueryResult();
        for (PositionalPosting posting: postings){
            result.add(posting.getDocId());
        }
        return result;
    }

    /**
     * Adds a new positional posting (document id and position) to the posting list.
     * @param docId New document id to be added to the positional posting list.
     * @param position New position to be added to the positional posting list.
     */
    public void add(int docId, int position){
        int index = getIndex(docId);
        if (index == -1){
            postings.add(new PositionalPosting(docId));
            postings.get(postings.size() - 1).add(position);
        } else {
            postings.get(index).add(position);
        }
    }

    /**
     * Gets the positional posting at position index.
     * @param index Position of the positional posting.
     * @return The positional posting at position index.
     */
    public PositionalPosting get(int index){
        return postings.get(index);
    }

    /**
     * Returns simple union of two positional postings list p1 and p2. The algorithm assumes the intersection of two
     * positional postings list is empty, therefore the union is just concatenation of two positional postings lists.
     * @param secondList p2
     * @return Union of two positional postings lists.
     */
    public PositionalPostingList merge(PositionalPostingList secondList){
        PositionalPostingList result = new PositionalPostingList();
        result.postings = new ArrayList<>();
        result.postings.addAll(postings);
        result.postings.addAll(secondList.postings);
        return result;
    }

    /**
     * Algorithm for the intersection of two positional postings lists p1 and p2. We maintain pointers into both lists
     * and walk through the two positional postings lists simultaneously, in time linear in the total number of postings
     * entries. At each step, we compare the docID pointed to by both pointers. If they are not the same, we advance the
     * pointer pointing to the smaller docID. Otherwise, we advance both pointers and do the same intersection search on
     * the positional lists of two documents. Similarly, we compare the positions pointed to by both position pointers.
     * If they are successive, we add the position to the result and advance both position pointers. Otherwise, we
     * advance the pointer pointing to the smaller position.
     * @param secondList p2, second posting list.
     * @return Intersection of two postings lists p1 and p2.
     */
    public PositionalPostingList intersection(PositionalPostingList secondList){
        int i = 0, j = 0;
        PositionalPostingList result = new PositionalPostingList();
        while (i < postings.size() && j < secondList.postings.size()){
            PositionalPosting p1 = postings.get(i);
            PositionalPosting p2 = secondList.postings.get(j);
            if (p1.getDocId() == p2.getDocId()){
                int position1 = 0;
                int position2 = 0;
                ArrayList<Posting> postings1 = p1.getPositions();
                ArrayList<Posting> postings2 = p2.getPositions();
                while (position1 < postings1.size() && position2 < postings2.size()){
                    if (postings1.get(position1).getId() + 1 == postings2.get(position2).getId()){
                        result.add(p1.getDocId(), postings2.get(position2).getId());
                        position1++;
                        position2++;
                    } else {
                        if (postings1.get(position1).getId() + 1 < postings2.get(position2).getId()){
                            position1++;
                        } else {
                            position2++;
                        }
                    }
                }
                i++;
                j++;
            } else {
                if (p1.getDocId() < p2.getDocId()){
                    i++;
                } else {
                    j++;
                }
            }
        }
        return result;
    }

    /**
     * Prints this object into a file with the given index.
     * @param printWriter Output stream to write the file.
     * @param index Position of this positional posting list in the inverted index.
     */
    public void writeToFile(PrintWriter printWriter, int index){
        if (size() > 0){
            printWriter.write(index + " " + size() + "\n");
            printWriter.write(toString());
        }
    }

    /**
     * Converts the positional posting list to a string. String is of the form all postings separated via space.
     * @return String form of the positional posting list.
     */
    public String toString(){
        StringBuilder result = new StringBuilder();
        for (PositionalPosting positionalPosting : postings){
            result.append("\t").append(positionalPosting.toString()).append("\n");
        }
        return result.toString();
    }

}
