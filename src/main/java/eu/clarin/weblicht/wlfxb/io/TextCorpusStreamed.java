/**
 *
 */
package eu.clarin.weblicht.wlfxb.io;

import eu.clarin.weblicht.wlfxb.md.xb.MetaData;
import eu.clarin.weblicht.wlfxb.md.xb.MetaDataItem;
import eu.clarin.weblicht.wlfxb.tc.api.TextCorpusLayer;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerStoredAbstract;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerTag;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

/**
 * @author Yana Panchenko
 *
 */
public class TextCorpusStreamed extends TextCorpusStored {

    private EnumSet<TextCorpusLayerTag> layersToRead;
    private EnumSet<TextCorpusLayerTag> layersFound = EnumSet.noneOf(TextCorpusLayerTag.class);
    private EnumSet<TextCorpusLayerTag> readSucceeded = EnumSet.noneOf(TextCorpusLayerTag.class);
    private XMLEventReader xmlEventReader;
    private XMLEventWriter xmlEventWriter;
    private XmlReaderWriter xmlReaderWriter;
    private static final int LAYER_INDENT_RELATIVE = 1;

    public TextCorpusStreamed(InputStream inputStream,
            EnumSet<TextCorpusLayerTag> layersToRead)
            throws WLFormatException {
        super("unknown");
        this.layersToRead = layersToRead;
        try {
        initializeReaderAndWriter(inputStream, null, false);
        process();
        } catch (WLFormatException e) {
            xmlReaderWriter.close();
            throw e;
        }
    }

    public TextCorpusStreamed(InputStream inputStream,
            EnumSet<TextCorpusLayerTag> layersToRead, OutputStream outputStream)
            throws WLFormatException {
        super("unknown");
        this.layersToRead = layersToRead;
        try {
        initializeReaderAndWriter(inputStream, outputStream, false);
        process();
        } catch (WLFormatException e) {
            xmlReaderWriter.close();
            throw e;
        }
    }

    public TextCorpusStreamed(InputStream inputStream,
            EnumSet<TextCorpusLayerTag> layersToRead, OutputStream outputStream,
            boolean outputAsXmlFragment)
            throws WLFormatException {
        super("unknown");
        this.layersToRead = layersToRead;
        try {
        initializeReaderAndWriter(inputStream, outputStream, outputAsXmlFragment);
        process();
        } catch (WLFormatException e) {
            xmlReaderWriter.close();
            throw e;
        }
    }

    public TextCorpusStreamed(InputStream inputStream,
            EnumSet<TextCorpusLayerTag> layersToRead, OutputStream outputStream,
            List<MetaDataItem> metaDataToAdd)
            throws WLFormatException {
        super("unknown");
        this.layersToRead = layersToRead;
        try {
        initializeReaderAndWriter(inputStream, outputStream, false);
        addMetadata(metaDataToAdd);
        process();
        } catch (WLFormatException e) {
            xmlReaderWriter.close();
            throw e;
        }
    }

    private void initializeReaderAndWriter(InputStream inputStream, OutputStream outputStream, boolean outputAsXmlFragment) throws WLFormatException {
        if (inputStream != null) {
            try {
                XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
                xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream, "UTF-8");
            } catch (XMLStreamException e) {
                throw new WLFormatException(e.getMessage(), e);
            }
        }
        if (outputStream != null) {
            try {
                XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
                xmlEventWriter = xmlOutputFactory.createXMLEventWriter(outputStream, "UTF-8");
            } catch (XMLStreamException e) {
                throw new WLFormatException(e.getMessage(), e);
            }
        }
        xmlReaderWriter = new XmlReaderWriter(xmlEventReader, xmlEventWriter);
        xmlReaderWriter.setOutputAsXmlFragment(outputAsXmlFragment);
    }

    private void addMetadata(List<MetaDataItem> metaDataToAdd) throws WLFormatException {
        try {
            xmlReaderWriter.readWriteUpToEndElement(MetaData.XML_NAME);
            marshall(metaDataToAdd);
            // rewrite metadata end element
            XMLEvent event = xmlEventReader.nextEvent();
            xmlReaderWriter.add(event);
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
    }

    private void process() throws WLFormatException {
        try {
            xmlReaderWriter.readWriteUpToStartElement(TextCorpusStored.XML_NAME);
            // process TextCorpus start element
            XMLEvent event = xmlEventReader.nextEvent();
            super.lang = event.asStartElement().getAttributeByName(new QName("lang")).getValue();
            // add processed TextCorpus start back
            xmlReaderWriter.add(event);
            // create TextCorpus object
            // read layers requested stopping before TextCorpus end element
            processLayers();
            super.connectLayers();
            // if no writing requested finish reading the document
            if (xmlEventWriter == null) {
                xmlReaderWriter.readWriteToTheEnd();
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        if (layersToRead.size() != readSucceeded.size()) {
            layersToRead.removeAll(readSucceeded);
             throw new WLFormatException("Following layers could not be read: " + layersToRead.toString());
        }
    }

    private void processLayers() throws WLFormatException {
        boolean textCorpusEnd = false;
        XMLEvent peekedEvent;
        try {
            peekedEvent = xmlEventReader.peek();
            while (!textCorpusEnd && peekedEvent != null) {
                if (peekedEvent.getEventType() == XMLStreamConstants.END_ELEMENT
                        && peekedEvent.asEndElement().getName().getLocalPart().equals(TextCorpusStored.XML_NAME)) {
                    textCorpusEnd = true;
                } else if (peekedEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    processLayer();
                    peekedEvent = xmlEventReader.peek();
                } else {
                    XMLEvent readEvent = xmlReaderWriter.readEvent();
                    xmlReaderWriter.add(readEvent);
                    peekedEvent = xmlEventReader.peek();
                }
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }

        if (!textCorpusEnd) {
            throw new WLFormatException(TextCorpusStored.XML_NAME + " end tag not found");
        }
    }

    private void processLayer() throws WLFormatException {

        XMLEvent peekedEvent;
        try {
            peekedEvent = xmlEventReader.peek();
            // now we assume that this event is start of a TextCorpus layer
            String tagName = peekedEvent.asStartElement().getName().getLocalPart();
            TextCorpusLayerTag layerTag = TextCorpusLayerTag.getFromXmlName(tagName);

            if (layerTag == null) { // unknown layer, just add it to output
                //readWriteElement(tagName);
                xmlReaderWriter.readWriteElement(tagName);
            } else {
                if (this.layersToRead.contains(layerTag)) { // known layer, and is requested for reading
                    // add it to the output, but store its data
                    readLayerData(layerTag);
                } else { // known layer, and is not requested for reading
                    // just add it to the output
                    xmlReaderWriter.readWriteElement(tagName);
                }
                layersFound.add(layerTag);
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }


    }

    private void readLayerData(TextCorpusLayerTag layerTag) throws WLFormatException {
        JAXBContext context;
        Unmarshaller unmarshaller;
        try {
            context = JAXBContext.newInstance(layerTag.getLayerClass());
            unmarshaller = context.createUnmarshaller();
            TextCorpusLayerStoredAbstract layer = (TextCorpusLayerStoredAbstract) unmarshaller.unmarshal(xmlEventReader);
            super.layersInOrder[layerTag.ordinal()] = layer;
            marshall(super.layersInOrder[layerTag.ordinal()]);
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        readSucceeded.add(layerTag);
    }

    private void marshall(TextCorpusLayer layer) throws WLFormatException {
        if (xmlEventWriter == null) {
            return;
        }
        TextCorpusLayerTag layerTag = TextCorpusLayerTag.getFromClass(layer.getClass());
        if (layersFound.contains(layerTag)) {
            throw new WLFormatException(layerTag.getXmlName() + " cannot be marshalled: the document already contains this annotation layer.");
        }
        JAXBContext context;
        try {
            xmlReaderWriter.startExternalFragment(LAYER_INDENT_RELATIVE);
            context = JAXBContext.newInstance(layer.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(layer, xmlEventWriter);
            xmlReaderWriter.endExternalFragment(LAYER_INDENT_RELATIVE);
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
    }

    private void marshall(List<MetaDataItem> metaDataToAdd) throws WLFormatException {
        if (xmlEventWriter == null) {
            return;
        }
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(MetaDataItem.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            for (MetaDataItem mdi : metaDataToAdd) {
                xmlReaderWriter.startExternalFragment(LAYER_INDENT_RELATIVE);
                marshaller.marshal(mdi, xmlEventWriter);
                xmlReaderWriter.endExternalFragment(LAYER_INDENT_RELATIVE);
            }
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
    }

    public void close() throws WLFormatException {
        boolean[] layersRead = new boolean[super.layersInOrder.length];
        for (TextCorpusLayerTag layerRead : layersToRead) {
            layersRead[layerRead.ordinal()] = true;
        }

        for (int i = 0; i < super.layersInOrder.length; i++) {
            // if it's a newly added layer
            if (super.layersInOrder[i] != null && !layersRead[i]
                    //&& !super.layersInOrder[i].isEmpty() 
                    ) {
                marshall(super.layersInOrder[i]);
            }
        }
        xmlReaderWriter.readWriteToTheEnd();
    }
}
