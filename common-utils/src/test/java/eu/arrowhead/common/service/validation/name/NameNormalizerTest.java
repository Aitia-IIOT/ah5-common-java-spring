package eu.arrowhead.common.service.validation.name;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=NameNormalizer.class)
public class NameNormalizerTest {
	//=================================================================================================
	// members

    @Autowired
    private NameNormalizer nameNormalizer;
        
    //=================================================================================================
    // methods

	//-------------------------------------------------------------------------------------------------
    @Test
    public void normalizeTest() {
		assertAll("name normalization",
				// empty name
				() -> assertThrows(java.lang.IllegalArgumentException.class, () -> {nameNormalizer.normalize(null);}),
				() -> assertThrows(java.lang.IllegalArgumentException.class, () -> {nameNormalizer.normalize("");}),
				// trim and to lowercase
				() -> assertEquals("name", nameNormalizer.normalize("\n \rNaMe  \t")));
    }
}
