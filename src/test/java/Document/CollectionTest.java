package Document;

import Dictionary.TurkishWordComparator;
import InformationRetrieval.Document.Collection;
import InformationRetrieval.Document.IndexType;
import org.junit.Test;

public class CollectionTest {

    @Test
    public void testIncidenceMatrix() {
        Collection collection = new Collection("testCollection", IndexType.INCIDENCE_MATRIX, new TurkishWordComparator());
    }

    @Test
    public void testInvertedIndex() {
        Collection collection = new Collection("testCollection", IndexType.INVERTED_INDEX, new TurkishWordComparator());
    }

}
