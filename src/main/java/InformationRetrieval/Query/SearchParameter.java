package InformationRetrieval.Query;

import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Index.TermWeighting;

public class SearchParameter {

    private RetrievalType retrievalType = RetrievalType.RANKED;
    private DocumentWeighting documentWeighting = DocumentWeighting.NO_IDF;
    private TermWeighting termWeighting = TermWeighting.NATURAL;
    private int documentsRetrieved = 1;

    public SearchParameter(){
    }

    public void setRetrievalType(RetrievalType retrievalType) {
        this.retrievalType = retrievalType;
    }

    public void setDocumentWeighting(DocumentWeighting documentWeighting) {
        this.documentWeighting = documentWeighting;
    }

    public void setTermWeighting(TermWeighting termWeighting) {
        this.termWeighting = termWeighting;
    }

    public void setDocumentsRetrieved(int documentsRetrieved) {
        this.documentsRetrieved = documentsRetrieved;
    }

    public RetrievalType getRetrievalType() {
        return retrievalType;
    }

    public DocumentWeighting getDocumentWeighting() {
        return documentWeighting;
    }

    public TermWeighting getTermWeighting() {
        return termWeighting;
    }

    public int getDocumentsRetrieved() {
        return documentsRetrieved;
    }

}
