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
    private boolean tokenizeDocument = false;
    private boolean normalizeDocument = false;
    private boolean phraseIndex = true;
    private boolean positionalIndex = true;
    private boolean kGramIndex = true;
    private boolean constructIndexInMemory = true;
    private boolean limitNumberOfDocumentsLoaded = false;
    private int blockSize = 1000;
    private int documentLimit = blockSize;

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

    public boolean constructKGramIndex() {
        return kGramIndex;
    }

    public boolean constructIndexInMemory() {
        return constructIndexInMemory;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public boolean tokenizeDocument() {
        return tokenizeDocument;
    }

    public boolean limitNumberOfDocumentsLoaded() {
        return limitNumberOfDocumentsLoaded;
    }

    public int getDocumentLimit() {
        return documentLimit;
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

    public void setKGramIndex(boolean kGramIndex) {
        this.kGramIndex = kGramIndex;
    }

    public void setConstructIndexInMemory(boolean constructIndexInMemory) {
        this.constructIndexInMemory = constructIndexInMemory;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public void setTokenizeDocument(boolean tokenizeDocument) {
        this.tokenizeDocument = tokenizeDocument;
    }

    public void setLimitNumberOfDocumentsLoaded(boolean limitNumberOfDocumentsLoaded) {
        this.limitNumberOfDocumentsLoaded = limitNumberOfDocumentsLoaded;
    }

    public void setDocumentLimit(int documentLimit) {
        this.documentLimit = documentLimit;
    }

}
