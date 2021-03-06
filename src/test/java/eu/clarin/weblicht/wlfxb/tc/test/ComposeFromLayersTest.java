/**
 *
 */
package eu.clarin.weblicht.wlfxb.tc.test;

import eu.clarin.weblicht.wlfxb.io.WLDObjector;
import eu.clarin.weblicht.wlfxb.lx.test.AbstractLexiconTest;
import eu.clarin.weblicht.wlfxb.md.xb.MetaData;
import eu.clarin.weblicht.wlfxb.tc.xb.*;
import eu.clarin.weblicht.wlfxb.test.utils.TestUtils;
import java.io.File;
import java.io.InputStream;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * @author Yana Panchenko
 *
 */
public class ComposeFromLayersTest extends AbstractLexiconTest{

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private static final String INPUT_TEXT = "/data/tc-text/layer-input.xml";
    private static final String INPUT_TOKENS = "/data/tc-tokens/layer-input.xml";
    private static final String INPUT_PARSING = "/data/tc-parsing/layer-input.xml";
    private static final String INPUT_SENTENCES = "/data/tc-sents/layer-input.xml";
    private static final String INPUT_LEMMAS = "/data/tc-lemmas/layer-input.xml";
    private static final String INPUT_TEXTSRC = "/data/tc-textsource/layer-input.xml";
    private static final String OUTPUT_FILE = "output.xml";

    public ComposeFromLayersTest() {
    }

    @Test
    public void test() throws Exception {

        InputStream is;

        is = this.getClass().getResourceAsStream(INPUT_TEXT);
        TextLayerStored textLayer = TestUtils.read(TextLayerStored.class, is);
        is.close();

        is = this.getClass().getResourceAsStream(INPUT_TOKENS);
        TokensLayerStored tokensLayer = TestUtils.read(TokensLayerStored.class, is);
        is.close();

        is = this.getClass().getResourceAsStream(INPUT_PARSING);
        ConstituentParsingLayerStored parsingLayer = TestUtils.read(ConstituentParsingLayerStored.class, is);
        is.close();

        is = this.getClass().getResourceAsStream(INPUT_SENTENCES);
        SentencesLayerStored sentsLayer = TestUtils.read(SentencesLayerStored.class, is);
        is.close();

        is = this.getClass().getResourceAsStream(INPUT_LEMMAS);
        LemmasLayerStored lemmasLayer = TestUtils.read(LemmasLayerStored.class, is);
        is.close();

        is = this.getClass().getResourceAsStream(INPUT_TEXTSRC);
        TextSourceLayerStored textSourceLayer = TestUtils.read(TextSourceLayerStored.class, is);
        is.close();

        TextCorpusStored textCorpus = TextCorpusStored.compose("de", textLayer, tokensLayer, parsingLayer, sentsLayer, lemmasLayer, textSourceLayer);
//        List<TextCorpusLayerStoredAbstract> layers = new ArrayList<TextCorpusLayerStoredAbstract>();
//        layers.add(textLayer); layers.add(tokensLayer); layers.add(parsingLayer); layers.add(sentsLayer); layers.add(lemmasLayer);
//        TextCorpusLayerStoredAbstract[] layersAsArray = new TextCorpusLayerStoredAbstract[layers.size()];
//        TextCorpusStored textCorpus = TextCorpusStored.compose("de",  layers.toArray(layersAsArray));


        WLDObjector.write(new MetaData(), textCorpus, testFolder.newFile(OUTPUT_FILE), false);

    }
}
