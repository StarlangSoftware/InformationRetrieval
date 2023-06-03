package InformationRetrieval.Query;

import InformationRetrieval.Document.DocumentWeighting;
import InformationRetrieval.Index.TermWeighting;

public class SearchParameter {

    private CategoryDeterminationType categoryDeterminationType = CategoryDeterminationType.KEYWORD;
    private FocusType focusType = FocusType.OVERALL;
    private RetrievalType retrievalType = RetrievalType.RANKED;
    private DocumentWeighting documentWeighting = DocumentWeighting.NO_IDF;
    private TermWeighting termWeighting = TermWeighting.NATURAL;
    private int documentsRetrieved = 1;

    private boolean searchAttributes = false;

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

    public void setFocusType(FocusType focusType){
        this.focusType = focusType;
    }

    public void setCategoryDeterminationType(CategoryDeterminationType categoryDeterminationType) {
        this.categoryDeterminationType = categoryDeterminationType;
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

    public FocusType getFocusType() {
        return focusType;
    }

    public CategoryDeterminationType getCategoryDeterminationType() {
        return categoryDeterminationType;
    }

    public boolean getSearchAttributes() {
        return searchAttributes;
    }

    public void setSearchAttributes(boolean searchAttributes) {
        this.searchAttributes = searchAttributes;
    }

}
