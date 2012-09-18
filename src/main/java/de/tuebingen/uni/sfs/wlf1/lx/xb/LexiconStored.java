/**
 * 
 */
package de.tuebingen.uni.sfs.wlf1.lx.xb;

import java.lang.reflect.Constructor;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.tuebingen.uni.sfs.wlf1.lx.api.FrequenciesLayer;
import de.tuebingen.uni.sfs.wlf1.lx.api.LemmasLayer;
import de.tuebingen.uni.sfs.wlf1.lx.api.Lexicon;
import de.tuebingen.uni.sfs.wlf1.lx.api.LexiconLayer;
import de.tuebingen.uni.sfs.wlf1.lx.api.PosTagsLayer;
import de.tuebingen.uni.sfs.wlf1.lx.api.RelationsLayer;

	/**
	 * @author Yana Panchenko
	 *
	 */
	
	@XmlRootElement(name=LexiconStored.XML_NAME, namespace=LexiconStored.XML_NAMESPACE)
	@XmlAccessorType(XmlAccessType.NONE)
	@XmlType(propOrder={
			"lemmasLayer", 
			"posTagsLayer",
			"frequenciesLayer",
			"relationsLayer",
			})
	public class LexiconStored implements Lexicon {
		
		public static final String XML_NAME = "Lexicon";
		public static final String XML_NAMESPACE = "http://www.dspin.de/data/lexicon";
	
		@XmlAttribute
		protected String lang;
		protected LexiconLayerStoredAbstract[] layersInOrder;
		
		private LexiconLayersConnector connector;
		
		
		LexiconStored() {
			connector = new LexiconLayersConnector();
			layersInOrder = new LexiconLayerStoredAbstract[LexiconLayerTag.orderedLayerTags().size()];
		}

		
		public LexiconStored(String language) {
			this();
			this.lang = language;
		}
		
		public String getLanguage() {
			return lang;
		}

		
		public LemmasLayer createLemmasLayer() {
			return initializeLayer(LemmasLayerStored.class);
		}
		
		public PosTagsLayer createPosTagsLayer(String tagset) {
			return initializeLayer(PosTagsLayerStored.class, tagset);
		}
		
		public FrequenciesLayer createFrequenciesLayer() {
			return initializeLayer(FrequenciesLayerStored.class);
		}
		
		public RelationsLayer createRelationsLayer() {
			return initializeLayer(RelationsLayerStored.class);
		}

		
		
		@SuppressWarnings("unchecked")
		private <T extends LexiconLayerStoredAbstract> T initializeLayer(Class<T> layerClass, Object ... params) {
			
			Class<?>[] paramsClass = null;
			if (params != null) {
				paramsClass = new Class<?>[params.length];
				for (int i = 0; i < params.length; i++) {
					paramsClass[i] = params[i].getClass();
				}
			}
			
			LexiconLayerTag layerTag = LexiconLayerTag.getFromClass(layerClass);
			try {
				Constructor<?> ct = null;
				T instance = null;
				if (params == null) {
					ct = layerClass.getDeclaredConstructor();
					instance = (T) ct.newInstance();
				} else {
					ct = layerClass.getDeclaredConstructor(paramsClass);
					instance = (T) ct.newInstance(params);
				}
				layersInOrder[layerTag.ordinal()] = instance;
				instance.setLayersConnector(connector);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (T) layersInOrder[layerTag.ordinal()];
		}
		
		
		

		@XmlElement(name=LemmasLayerStored.XML_NAME)
		protected void setLemmasLayer(LemmasLayerStored layer) {
			layersInOrder[LexiconLayerTag.LEMMAS.ordinal()] = layer;
		}
		public LemmasLayerStored getLemmasLayer() {
			return ((LemmasLayerStored) layersInOrder[LexiconLayerTag.LEMMAS.ordinal()]);
		}
		@XmlElement(name=PosTagsLayerStored.XML_NAME)
		protected void setPosTagsLayer(PosTagsLayerStored layer) {
			layersInOrder[LexiconLayerTag.POSTAGS.ordinal()] = layer;
		}
		public PosTagsLayerStored getPosTagsLayer() {
			return ((PosTagsLayerStored) layersInOrder[LexiconLayerTag.POSTAGS.ordinal()]);
		}
		@XmlElement(name=FrequenciesLayerStored.XML_NAME)
		protected void setFrequenciesLayer(FrequenciesLayerStored layer) {
			layersInOrder[LexiconLayerTag.FREQUENCIES.ordinal()] = layer;
		}
		public FrequenciesLayerStored getFrequenciesLayer() {
			return ((FrequenciesLayerStored) layersInOrder[LexiconLayerTag.FREQUENCIES.ordinal()]);
		}
		@XmlElement(name=RelationsLayerStored.XML_NAME)
		protected void setRelationsLayer(RelationsLayerStored layer) {
			layersInOrder[LexiconLayerTag.RELATIONS.ordinal()] = layer;
		}
		public RelationsLayerStored getRelationsLayer() {
			return ((RelationsLayerStored) layersInOrder[LexiconLayerTag.RELATIONS.ordinal()]);
		}
		


		
		protected void afterUnmarshal(Unmarshaller u, Object parent) {
			connectLayers();
		}
		
//		protected boolean beforeMarshal(Marshaller m) {
//			setEmptyLayersToNull();
//			return true;
//		}
//		
//
//		/**
//		 * 
//		 */
//		private void setEmptyLayersToNull() {
//			
//			for (int i = 0; i < this.layersInOrder.length; i++) {
//				if (layersInOrder[i] != null && layersInOrder[i].isEmpty()) {
//					layersInOrder[i] = null;
//				}
//			}
//		}


		protected void connectLayers() {
			for (int i = 0; i < this.layersInOrder.length; i++) {
				if (layersInOrder[i] != null) {
					layersInOrder[i].setLayersConnector(connector);
				}
			}
		}

		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(XML_NAME);
			sb.append(":\n");
			
			for (LexiconLayer layer : this.layersInOrder) {
				if (layer != null) {
					sb.append(layer);
					sb.append("\n");
				}
			}

			return sb.toString();
		}
		
	}