package Document;

import InformationRetrieval.Document.Collection;
import InformationRetrieval.Document.IndexType;
import InformationRetrieval.Document.Parameter;
import org.junit.Test;

public class CollectionTest {

    @Test
    public void testIncidenceMatrix() {
        Parameter parameter = new Parameter();
        parameter.setIndexType(IndexType.INCIDENCE_MATRIX);
        Collection collection = new Collection("testCollection", parameter);
    }

    @Test
    public void testInvertedIndex() {
        Parameter parameter = new Parameter();
        Collection collection = new Collection("testCollection", parameter);
        collection.save();
    }

    @Test
    public void testInvertedIndex2() {
        Parameter parameter = new Parameter();
        parameter.setFromFile(true);
        Collection collection = new Collection("testCollection", parameter);
        System.out.println();
    }

}
