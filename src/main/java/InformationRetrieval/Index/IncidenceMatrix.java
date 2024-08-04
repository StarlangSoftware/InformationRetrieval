package InformationRetrieval.Index;

import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;

import java.util.ArrayList;

public class IncidenceMatrix {
    private final boolean[][] incidenceMatrix;
    private final int dictionarySize;
    private final int documentSize;

    /**
     * Empty constructor for the incidence matrix representation. Initializes the incidence matrix according to the
     * given dictionary and document size.
     * @param dictionarySize Number of words in the dictionary (number of distinct words in the collection)
     * @param documentSize Number of documents in the collection
     */
    public IncidenceMatrix(int dictionarySize, int documentSize){
        this.dictionarySize = dictionarySize;
        this.documentSize = documentSize;
        incidenceMatrix = new boolean[dictionarySize][documentSize];
    }

    /**
     * Constructs an incidence matrix from a list of sorted tokens in the given terms array.
     * @param dictionary Term dictionary
     * @param terms List of tokens in the memory collection.
     * @param documentSize Number of documents in the collection
     */
    public IncidenceMatrix(ArrayList<TermOccurrence> terms, TermDictionary dictionary, int documentSize){
        this(dictionary.size(), documentSize);
        int i;
        TermOccurrence term;
        if (!terms.isEmpty()){
            term = terms.get(0);
            i = 1;
            set(dictionary.getWordIndex(term.getTerm().getName()), term.getDocID());
            while (i < terms.size()){
                term = terms.get(i);
                set(dictionary.getWordIndex(term.getTerm().getName()), term.getDocID());
                i++;
            }
        }
    }

    /**
     * Sets the given cell in the incidence matrix to true.
     * @param row Row no of the cell
     * @param col Column no of the cell
     */
    public void set(int row, int col){
        if (row < 0 || row >= dictionarySize){
            System.out.println("The term with index " + row + " is out of incidence matrix\n");
            return;
        }
        if (col < 0 || col >= documentSize){
            System.out.println("The document with index " + row + " is out of incidence matrix\n");
            return;
        }
        incidenceMatrix[row][col] = true;
    }

    public QueryResult search(Query query, TermDictionary dictionary){
        int i, j, termIndex;
        boolean[] resultRow;
        QueryResult result = new QueryResult();
        resultRow = new boolean[documentSize];
        for (i = 0; i < documentSize; i++){
            resultRow[i] = true;
        }
        for (i = 0; i < query.size(); i++){
            termIndex = dictionary.getWordIndex(query.getTerm(i).getName());
            if (termIndex != -1){
                for (j = 0; j < documentSize; j++){
                    resultRow[j] = resultRow[j] && incidenceMatrix[termIndex][j];
                }
            } else {
                return result;
            }
        }
        for (i = 0; i < documentSize; i++){
            if (resultRow[i]){
                result.add(i);
            }
        }
        return result;
    }

}
