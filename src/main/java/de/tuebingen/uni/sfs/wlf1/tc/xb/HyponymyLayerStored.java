/**
 * 
 */
package de.tuebingen.uni.sfs.wlf1.tc.xb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import de.tuebingen.uni.sfs.wlf1.tc.api.Lemma;
import de.tuebingen.uni.sfs.wlf1.tc.api.Orthform;


/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name=HyponymyLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class HyponymyLayerStored extends LexicalSemanticsLayerStored {
	
	public static final String XML_NAME = "hyponymy";
	
	protected HyponymyLayerStored() {
		super();
	}
	
	protected HyponymyLayerStored(TextCorpusLayersConnector connector) {
		super(connector);
	}

	protected HyponymyLayerStored(String source) {
		super(source);
	}
	
	protected void setLayersConnector(TextCorpusLayersConnector connector) {
		super.connector = connector;
		for (OrthformStored orthform : super.orthforms) {
			for (String lemRef : orthform.lemmaRefs) {
				connector.lemma2ItsHyponyms.put(connector.lemmaId2ItsLemma.get(lemRef), orthform);
			}
		}
	}

	@Override
	public Orthform getOrthform(Lemma lemma) {
		return super.connector.lemma2ItsHyponyms.get(lemma);
	}
	
	@Override
	public String toString() {
		return XML_NAME + " " + super.toString();
	}
}