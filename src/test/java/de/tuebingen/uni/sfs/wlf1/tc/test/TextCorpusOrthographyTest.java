/**
 *
 */
package de.tuebingen.uni.sfs.wlf1.tc.test;

import de.tuebingen.uni.sfs.wlf1.io.TextCorpusStreamed;
import de.tuebingen.uni.sfs.wlf1.tc.api.CorrectionOperation;
import de.tuebingen.uni.sfs.wlf1.tc.api.OrthographyLayer;
import de.tuebingen.uni.sfs.wlf1.tc.api.TextCorpus;
import de.tuebingen.uni.sfs.wlf1.tc.api.Token;
import de.tuebingen.uni.sfs.wlf1.tc.xb.TextCorpusLayerTag;
import java.io.*;
import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Yana Panchenko
 *
 */
public class TextCorpusOrthographyTest extends AbstractTextCorpusTest {

    private static final String INPUT_FILE_WITHOUT_LAYER = "/data/tc-orth/tcf-before.xml";
    private static final String INPUT_FILE_WITH_LAYER = "/data/tc-orth/tcf-after.xml";
    private static final String EXPECTED_OUTPUT_FILE = "/data/tc-orth/output-expected.xml";
    private static final String OUTPUT_FILE = "/tmp/output.xml";
    private static final EnumSet<TextCorpusLayerTag> layersToReadBeforeCorrections =
            EnumSet.of(TextCorpusLayerTag.TOKENS);
    private static final EnumSet<TextCorpusLayerTag> layersToReadAfterCorrections =
            EnumSet.of(TextCorpusLayerTag.TOKENS, TextCorpusLayerTag.ORTHOGRAPHY);
    private String corr1String = "will";
    private CorrectionOperation corr1Op = CorrectionOperation.replace;
    private int corr1TokIndex = 1;
    public static final String corr2String = "essen";
    private CorrectionOperation corr2Op = CorrectionOperation.insert_after;
    private int corr2TokIndex = 3;

    @Test
    public void testRead() throws Exception {
        TextCorpus tc = read(INPUT_FILE_WITH_LAYER, layersToReadAfterCorrections);
        OrthographyLayer layer = tc.getOrthographyLayer();
        Assert.assertEquals(2, layer.size());
        Assert.assertEquals("will", layer.getCorrection(0).getString());
        Assert.assertEquals(tc.getTokensLayer().getToken(1), layer.getTokens(layer.getCorrection(0))[0]);
    }

    @Test
    public void testReadWrite() throws Exception {
        File file = new File(INPUT_FILE_WITHOUT_LAYER);
        InputStream is = new FileInputStream(file);
        File ofile = new File(OUTPUT_FILE);
        OutputStream os = new FileOutputStream(ofile);
        TextCorpusStreamed tc = new TextCorpusStreamed(is, layersToReadBeforeCorrections, os, false);
        System.out.println(tc);
        OrthographyLayer layer = tc.createOrthographyLayer();

        Token token;
        token = tc.getTokensLayer().getToken(corr1TokIndex);
        layer.addCorrection(corr1String, token, corr1Op);
        token = tc.getTokensLayer().getToken(corr2TokIndex);
        layer.addCorrection(corr2String, token, corr2Op);

        // IMPORTANT close the streams!!!
        tc.close();
        System.out.println(tc);
        // compare output xml with expected xml
        assertEqualXml(EXPECTED_OUTPUT_FILE, OUTPUT_FILE);
    }
}