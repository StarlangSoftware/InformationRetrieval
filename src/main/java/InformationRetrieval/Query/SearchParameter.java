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

    /**
     * Empty constructor for SearchParameter object.
     */
    public SearchParameter(){
    }

    /**
     * Setter for the retrievalType.
     * @param retrievalType New retrieval type
     */
    public void setRetrievalType(RetrievalType retrievalType) {
        this.retrievalType = retrievalType;
    }

    /**
     * Mutator for the documentWeighting scheme used in tf-idf search.
     * @param documentWeighting New document weighting scheme for tf-idf search.
     */
    public void setDocumentWeighting(DocumentWeighting documentWeighting) {
        this.documentWeighting = documentWeighting;
    }

    /**
     * Mutator for the termWeighting scheme used in tf-idf search.
     * @param termWeighting New term weighting scheme for tf-idf search.
     */
    public void setTermWeighting(TermWeighting termWeighting) {
        this.termWeighting = termWeighting;
    }

    /**
     * Mutator for the maximum number of documents retrieved.
     * @param documentsRetrieved New value for the maximum number of documents retrieved.
     */
    public void setDocumentsRetrieved(int documentsRetrieved) {
        this.documentsRetrieved = documentsRetrieved;
    }

    /**
     * Mutator for the focus type.
     * @param focusType New focus type.
     */
    public void setFocusType(FocusType focusType){
        this.focusType = focusType;
    }

    /**
     * Mutator for the category determination type.
     * @param categoryDeterminationType New category determination type.
     */
    public void setCategoryDeterminationType(CategoryDeterminationType categoryDeterminationType) {
        this.categoryDeterminationType = categoryDeterminationType;
    }

    /**
     * Accessor for the retrieval type
     * @return Retrieval type.
     */
    public RetrievalType getRetrievalType() {
        return retrievalType;
    }

    /**
     * Accessor for the document weighting scheme in tf-idf search
     * @return Document weighting scheme in tf-idf search
     */
    public DocumentWeighting getDocumentWeighting() {
        return documentWeighting;
    }

    /**
     * Accessor for the term weighting scheme in tf-idf search
     * @return Term weighting scheme in tf-idf search
     */
    public TermWeighting getTermWeighting() {
        return termWeighting;
    }

    /**
     * Accessor for the maximum number of documents retrieved.
     * @return The maximum number of documents retrieved.
     */
    public int getDocumentsRetrieved() {
        return documentsRetrieved;
    }

    /**
     * Accessor for the focus type.
     * @return Focus type.
     */
    public FocusType getFocusType() {
        return focusType;
    }

    /**
     * Accessor for the category determination type.
     * @return Category determination type.
     */
    public CategoryDeterminationType getCategoryDeterminationType() {
        return categoryDeterminationType;
    }

    /**
     * Accessor for the search attributes field. The parameter will determine if an attribute search is performed.
     * @return Search attribute.
     */
    public boolean getSearchAttributes() {
        return searchAttributes;
    }

    /**
     * Mutator for the search attributes field. The parameter will determine if an attribute search is performed.
     * @param searchAttributes New value for search attribute.
     */
    public void setSearchAttributes(boolean searchAttributes) {
        this.searchAttributes = searchAttributes;
    }

}
