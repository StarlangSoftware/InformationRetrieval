package InformationRetrieval.Document;

import Dictionary.TurkishWordComparator;
import Dictionary.WordComparator;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalDisambiguation.MorphologicalDisambiguator;

public class Parameter {

    private IndexType indexType = IndexType.INVERTED_INDEX;
    private WordComparator wordComparator = new TurkishWordComparator();
    private boolean fromFile = false;
    private MorphologicalDisambiguator disambiguator;
    private FsmMorphologicalAnalyzer fsm;
    private boolean normalizeDocument = false;
    private boolean biWordIndex = true;

    private boolean positionalIndex = true;

    public Parameter(){
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public WordComparator getWordComparator() {
        return wordComparator;
    }

    public boolean isFromFile() {
        return fromFile;
    }

    public MorphologicalDisambiguator getDisambiguator() {
        return disambiguator;
    }

    public FsmMorphologicalAnalyzer getFsm() {
        return fsm;
    }

    public boolean isBiWordIndex() {
        return biWordIndex;
    }
    public boolean isNormalizeDocument() {
        return normalizeDocument;
    }

    public boolean isPositionalIndex() {
        return positionalIndex;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    public void setWordComparator(WordComparator wordComparator) {
        this.wordComparator = wordComparator;
    }

    public void setFromFile(boolean fromFile) {
        this.fromFile = fromFile;
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

    public void setBiWordIndex(boolean biWordIndex) {
        this.biWordIndex = biWordIndex;
    }

    public void setPositionalIndex(boolean positionalIndex) {
        this.positionalIndex = positionalIndex;
    }

}
