package InformationRetrieval.Document;

import Dictionary.TurkishWordComparator;
import Dictionary.WordComparator;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalDisambiguation.MorphologicalDisambiguator;

public class Parameter {

    private IndexType indexType = IndexType.INVERTED_INDEX;
    private WordComparator wordComparator = new TurkishWordComparator();
    private boolean loadIndexesFromFile = false;
    private MorphologicalDisambiguator disambiguator;
    private FsmMorphologicalAnalyzer fsm;
    private boolean normalizeDocument = false;
    private boolean phraseIndex = true;
    private boolean positionalIndex = true;
    private boolean constructNGramIndex = true;
    private boolean limitNumberOfDocumentsLoaded = false;
    private int documentLimit = 1000;
    private int wordLimit = 10000;
    private DocumentType documentType = DocumentType.NORMAL;
    private int representativeCount = 10;

    /**
     * Empty constructor for the general query search.
     */
    public Parameter(){
    }

    /**
     * Accessor for the index type search parameter. Index can be inverted index or incidence matrix.
     * @return Index type search parameter
     */
    public IndexType getIndexType() {
        return indexType;
    }

    /**
     * Accessor for the word comparator. Word comparator is a function to compare terms.
     * @return Word comparator
     */
    public WordComparator getWordComparator() {
        return wordComparator;
    }

    /**
     * Accessor for the loadIndexesFromFile search parameter. If loadIndexesFromFile is true, all the indexes will be
     * read from the file, otherwise they will be reconstructed.
     * @return loadIndexesFromFile search parameter
     */
    public boolean loadIndexesFromFile() {
        return loadIndexesFromFile;
    }

    /**
     * Accessor for the disambiguator search parameter. The disambiguator is used for morphological disambiguation for
     * the terms in Turkish.
     * @return disambiguator search parameter
     */
    public MorphologicalDisambiguator getDisambiguator() {
        return disambiguator;
    }

    /**
     * Accessor for the fsm search parameter. The fsm is used for morphological analysis for  the terms in Turkish.
     * @return fsm search parameter
     */
    public FsmMorphologicalAnalyzer getFsm() {
        return fsm;
    }

    /**
     * Accessor for the constructPhraseIndex search parameter. If constructPhraseIndex is true, phrase indexes will be
     * reconstructed or used in query processing.
     * @return constructPhraseIndex search parameter
     */
    public boolean constructPhraseIndex() {
        return phraseIndex;
    }

    /**
     * Accessor for the normalizeDocument search parameter. If normalizeDocument is true, the terms in the document will
     * be preprocessed by morphological anaylysis and some preprocessing techniques.
     * @return normalizeDocument search parameter
     */
    public boolean normalizeDocument() {
        return normalizeDocument;
    }

    /**
     * Accessor for the positionalIndex search parameter. If positionalIndex is true, positional indexes will be
     * reconstructed or used in query processing.
     * @return positionalIndex search parameter
     */
    public boolean constructPositionalIndex() {
        return positionalIndex;
    }

    /**
     * Accessor for the constructNGramIndex search parameter. If constructNGramIndex is true, N-Gram indexes will be
     * reconstructed or used in query processing.
     * @return constructNGramIndex search parameter
     */
    public boolean constructNGramIndex() {
        return constructNGramIndex;
    }

    /**
     * Accessor for the limitNumberOfDocumentsLoaded search parameter. If limitNumberOfDocumentsLoaded is true,
     * the query result will be filtered according to the documentLimit search parameter.
     * @return limitNumberOfDocumentsLoaded search parameter
     */
    public boolean limitNumberOfDocumentsLoaded() {
        return limitNumberOfDocumentsLoaded;
    }

    /**
     * Accessor for the documentLimit search parameter. If limitNumberOfDocumentsLoaded is true,  the query result will
     * be filtered according to the documentLimit search parameter.
     * @return limitNumberOfDocumentsLoaded search parameter
     */
    public int getDocumentLimit() {
        return documentLimit;
    }

    /**
     * Accessor for the wordLimit search parameter. wordLimit is the limit on the partial term dictionary size. For
     * large collections, we term dictionaries are divided into multiple files, this parameter sets the number of terms
     * in those separate dictionaries.
     * @return wordLimit search parameter
     */
    public int getWordLimit() {
        return wordLimit;
    }

    /**
     * Accessor for the representativeCount search parameter. representativeCount is the maximum number of representative
     * words in the category based query search.
     * @return representativeCount search parameter
     */
    public int getRepresentativeCount() {
        return representativeCount;
    }

    /**
     * Mutator for the index type search parameter. Index can be inverted index or incidence matrix.
     * @param indexType Index type search parameter
     */
    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    /**
     * Mutator for the word comparator. Word comparator is a function to compare terms.
     * @param wordComparator Word comparator
     */
    public void setWordComparator(WordComparator wordComparator) {
        this.wordComparator = wordComparator;
    }

    /**
     * Mutator for the loadIndexesFromFile search parameter. If loadIndexesFromFile is true, all the indexes will be
     * read from the file, otherwise they will be reconstructed.
     * @param loadIndexesFromFile loadIndexesFromFile search parameter
     */
    public void setLoadIndexesFromFile(boolean loadIndexesFromFile) {
        this.loadIndexesFromFile = loadIndexesFromFile;
    }

    /**
     * Mutator for the disambiguator search parameter. The disambiguator is used for morphological disambiguation for
     * the terms in Turkish.
     * @param disambiguator disambiguator search parameter
     */
    public void setDisambiguator(MorphologicalDisambiguator disambiguator) {
        this.disambiguator = disambiguator;
    }

    /**
     * Mutator for the fsm search parameter. The fsm is used for morphological analysis for the terms in Turkish.
     * @param fsm fsm search parameter
     */
    public void setFsm(FsmMorphologicalAnalyzer fsm) {
        this.fsm = fsm;
    }

    /**
     * Mutator for the normalizeDocument search parameter. If normalizeDocument is true, the terms in the document will
     * be preprocessed by morphological anaylysis and some preprocessing techniques.
     * @param normalizeDocument normalizeDocument search parameter
     */
    public void setNormalizeDocument(boolean normalizeDocument) {
        this.normalizeDocument = normalizeDocument;
    }

    /**
     * Mutator for the constructPhraseIndex search parameter. If constructPhraseIndex is true, phrase indexes will be
     * reconstructed or used in query processing.
     * @param phraseIndex constructPhraseIndex search parameter
     */
    public void setPhraseIndex(boolean phraseIndex) {
        this.phraseIndex = phraseIndex;
    }

    /**
     * Mutator for the positionalIndex search parameter. If positionalIndex is true, positional indexes will be
     * reconstructed or used in query processing.
     * @param positionalIndex positionalIndex search parameter
     */
    public void setPositionalIndex(boolean positionalIndex) {
        this.positionalIndex = positionalIndex;
    }

    /**
     * Mutator for the constructNGramIndex search parameter. If constructNGramIndex is true, N-Gram indexes will be
     * reconstructed or used in query processing.
     * @param nGramIndex constructNGramIndex search parameter
     */
    public void setNGramIndex(boolean nGramIndex) {
        this.constructNGramIndex = nGramIndex;
    }

    /**
     * Mutator for the limitNumberOfDocumentsLoaded search parameter. If limitNumberOfDocumentsLoaded is true,
     * the query result will be filtered according to the documentLimit search parameter.
     * @param limitNumberOfDocumentsLoaded limitNumberOfDocumentsLoaded search parameter
     */
    public void setLimitNumberOfDocumentsLoaded(boolean limitNumberOfDocumentsLoaded) {
        this.limitNumberOfDocumentsLoaded = limitNumberOfDocumentsLoaded;
    }

    /**
     * Mutator for the documentLimit search parameter. If limitNumberOfDocumentsLoaded is true,  the query result will
     * be filtered according to the documentLimit search parameter.
     * @param documentLimit documentLimit search parameter
     */
    public void setDocumentLimit(int documentLimit) {
        this.documentLimit = documentLimit;
    }

    /**
     * Mutator for the documentLimit search parameter. If limitNumberOfDocumentsLoaded is true,  the query result will
     * be filtered according to the documentLimit search parameter.
     * @param wordLimit wordLimit search parameter
     */
    public void setWordLimit(int wordLimit) {
        this.wordLimit = wordLimit;
    }

    /**
     * Accessor for the document type search parameter. Document can be normal or a categorical document.
     * @return Document type search parameter
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Mutator for the document type search parameter. Document can be normal or a categorical document.
     * @param documentType Document type search parameter
     */
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    /**
     * Mutator for the representativeCount search parameter. representativeCount is the maximum number of representative
     * words in the category based query search.
     * @param representativeCount representativeCount search parameter
     */
    public void setRepresentativeCount(int representativeCount) {
        this.representativeCount = representativeCount;
    }

}
