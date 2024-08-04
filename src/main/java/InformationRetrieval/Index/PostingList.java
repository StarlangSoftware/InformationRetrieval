package InformationRetrieval.Index;

import InformationRetrieval.Query.QueryResult;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * For each term, we have a list that records which documents the term occurs in. Each item in the list – which records
 * that a term appeared in a document is conventionally called a posting.
 */
public class PostingList {
    protected ArrayList<Posting> postings;

    /**
     * Constructor of the PostingList class. Initializes the list.
     */
    public PostingList(){
        postings = new ArrayList<>();
    }

    /**
     * Constructs a posting list from a line, which contains postings separated with space.
     * @param line A string containing postings separated with space character.
     */
    public PostingList(String line){
        postings = new ArrayList<>();
        String[] ids = line.split(" ");
        for (String id : ids){
            add(Integer.parseInt(id));
        }
    }

    /**
     * Adds a new posting (document id) to the posting list.
     * @param docId New document id to be added to the posting list.
     */
    public void add(int docId){
        postings.add(new Posting(docId));
    }

    /**
     * Returns the number of postings in the posting list.
     * @return Number of postings in the posting list.
     */
    public int size(){
        return postings.size();
    }

    /**
     * Algorithm for the intersection of two postings lists p1 and p2. We maintain pointers into both lists and walk
     * through the two postings lists simultaneously, in time linear in the total number of postings entries. At each
     * step, we compare the docID pointed to by both pointers. If they are the same, we put that docID in the results
     * list, and advance both pointers. Otherwise, we advance the pointer pointing to the smaller docID.
     * @param secondList p2, second posting list.
     * @return Intersection of two postings lists p1 and p2.
     */
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

    /**
     * Returns simple union of two postings list p1 and p2. The algorithm assumes the intersection of two postings list
     * is empty, therefore the union is just concatenation of two postings lists.
     * @param secondList p2
     * @return Union of two postings lists.
     */
    public PostingList merge(PostingList secondList){
        PostingList result = new PostingList();
        result.postings = new ArrayList<>();
        result.postings.addAll(postings);
        result.postings.addAll(secondList.postings);
        return result;
    }

    /**
     * Converts the postings list to a query result object. Simply adds all postings one by one to the result.
     * @return QueryResult object containing the postings in this object.
     */
    public QueryResult toQueryResult(){
        QueryResult result = new QueryResult();
        for (Posting posting:postings){
            result.add(posting.getId());
        }
        return result;
    }

    /**
     * Prints this object into a file with the given index.
     * @param printWriter Output stream to write the file.
     * @param index Position of this posting list in the inverted index.
     */
    public void writeToFile(PrintWriter printWriter, int index){
        if (size() > 0){
            printWriter.write(index + " " + size() + "\n");
            printWriter.write(toString());
        }
    }

    /**
     * Converts the posting list to a string. String is of the form all postings separated via space.
     * @return String form of the posting list.
     */
    public String toString(){
        StringBuilder result = new StringBuilder();
        for (Posting posting : postings){
            result.append(posting.getId()).append(" ");
        }
        return result.toString().trim() + "\n";
    }
}
