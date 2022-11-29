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
    private boolean constructIndexInDisk = false;
    private boolean constructDictionaryInDisk = false;
    private boolean limitNumberOfDocumentsLoaded = false;
    private int documentLimit = 1000;
    private int wordLimit = 10000;
    private DocumentType documentType = DocumentType.NORMAL;
    private int representativeCount = 10;

    public Parameter(){
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public WordComparator getWordComparator() {
        return wordComparator;
    }

    public boolean loadIndexesFromFile() {
        return loadIndexesFromFile;
    }

    public MorphologicalDisambiguator getDisambiguator() {
        return disambiguator;
    }

    public FsmMorphologicalAnalyzer getFsm() {
        return fsm;
    }

    public boolean constructPhraseIndex() {
        return phraseIndex;
    }
    public boolean normalizeDocument() {
        return normalizeDocument;
    }

    public boolean constructPositionalIndex() {
        return positionalIndex;
    }

    public boolean constructNGramIndex() {
        return constructNGramIndex;
    }

    public boolean constructIndexInDisk() {
        return constructIndexInDisk;
    }

    public boolean limitNumberOfDocumentsLoaded() {
        return limitNumberOfDocumentsLoaded;
    }

    public int getDocumentLimit() {
        return documentLimit;
    }

    public boolean constructDictionaryInDisk() {
        return constructDictionaryInDisk;
    }

    public int getWordLimit() {
        return wordLimit;
    }

    public int getRepresentativeCount() {
        return representativeCount;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    public void setWordComparator(WordComparator wordComparator) {
        this.wordComparator = wordComparator;
    }

    public void setLoadIndexesFromFile(boolean loadIndexesFromFile) {
        this.loadIndexesFromFile = loadIndexesFromFile;
    }

    public void setDisambiguator(MorphologicalDisambiguator disambiguator) {
        this.disambiguator = disambiguator;
    }

    public void setFsm(FsmMorphologicalAnalyzer fsm) {
        this.fsm = fsm;
    }

    public void setNormalizeDocument(boolean normalizeDocument) {
        this.normalizeDocument = normalizeDocument;
    }

    public void setPhraseIndex(boolean phraseIndex) {
        this.phraseIndex = phraseIndex;
    }

    public void setPositionalIndex(boolean positionalIndex) {
        this.positionalIndex = positionalIndex;
    }

    public void setNGramIndex(boolean nGramIndex) {
        this.constructNGramIndex = nGramIndex;
    }

    public void setConstructIndexInDisk(boolean constructIndexInDisk) {
        this.constructIndexInDisk = constructIndexInDisk;
    }

    public void setLimitNumberOfDocumentsLoaded(boolean limitNumberOfDocumentsLoaded) {
        this.limitNumberOfDocumentsLoaded = limitNumberOfDocumentsLoaded;
    }

    public void setDocumentLimit(int documentLimit) {
        this.documentLimit = documentLimit;
    }

    public void setConstructDictionaryInDisk(boolean constructDictionaryInDisk) {
        this.constructDictionaryInDisk = constructDictionaryInDisk;
        if (constructDictionaryInDisk){
            constructIndexInDisk = true;
        }
    }

    public void setWordLimit(int wordLimit) {
        this.wordLimit = wordLimit;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public void setRepresentativeCount(int representativeCount) {
        this.representativeCount = representativeCount;
    }

}
