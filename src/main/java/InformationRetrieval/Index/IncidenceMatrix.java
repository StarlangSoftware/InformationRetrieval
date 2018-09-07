package InformationRetrieval.Index;

import InformationRetrieval.Query.Query;
import InformationRetrieval.Query.QueryResult;

public class IncidenceMatrix {
    private boolean[][] incidenceMatrix;
    private int dictionarySize;
    private int documentSize;

    public IncidenceMatrix(int dictionarySize, int documentSize){
        this.dictionarySize = dictionarySize;
        this.documentSize = documentSize;
        incidenceMatrix = new boolean[dictionarySize][documentSize];
    }

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
