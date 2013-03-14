/**
 * wlfxb - a library for creating and processing of TCF data streams.
 *
 * Copyright (C) Yana Panchenko.
 *
 * This file is part of wlfxb.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 *
 */
package eu.clarin.weblicht.wlfxb.lx.xb;

import eu.clarin.weblicht.wlfxb.lx.api.Lemma;
import eu.clarin.weblicht.wlfxb.lx.api.PosTag;
import eu.clarin.weblicht.wlfxb.lx.api.PosTagsLayer;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = PosTagsLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class PosTagsLayerStored extends LexiconLayerStoredAbstract implements PosTagsLayer {

    public static final String XML_NAME = "POStags";
    @XmlAttribute(name = CommonAttributes.TAGSET, required = true)
    private String tagset;
    @XmlElement(name = PosTagStored.XML_NAME)
    private List<PosTagStored> tags = new ArrayList<PosTagStored>();
    private LexiconLayersConnector connector;

    protected PosTagsLayerStored() {
    }

    protected PosTagsLayerStored(String tagset) {
        this.tagset = tagset;
    }

    protected PosTagsLayerStored(LexiconLayersConnector connector) {
        this.connector = connector;
    }

    protected void setLayersConnector(LexiconLayersConnector connector) {
        this.connector = connector;
        for (PosTagStored tag : tags) {
            connect(tag, connector.lemmaId2ItsLemma.get(tag.lemRef));
        }
    }

    private void connect(PosTagStored tag, Lemma lemma) {
        if (!connector.lemma2ItsTags.containsKey(lemma)) {
            connector.lemma2ItsTags.put(lemma, new ArrayList<PosTag>());
        }
        connector.lemma2ItsTags.get(lemma).add(tag);
    }

    @Override
    public boolean isEmpty() {
        return tags.isEmpty();
    }

    @Override
    public int size() {
        return tags.size();
    }

    @Override
    public String getTagset() {
        return tagset;
    }

    @Override
    public PosTag getTag(int index) {
        PosTag tag = tags.get(index);
        return tag;
    }

    @Override
    public PosTag[] getTags(Lemma lemma) {
        if (connector.lemma2ItsTags.containsKey(lemma)) {
            List<PosTag> tagsList = connector.lemma2ItsTags.get(lemma);
            PosTag[] posTags = tagsList.toArray(new PosTag[tagsList.size()]);
            return posTags;
        }
        return null;
    }

    @Override
    public Lemma getLemma(PosTag tag) {
        if (tag instanceof PosTagStored) {
            PosTagStored tagStored = (PosTagStored) tag;
            return connector.lemmaId2ItsLemma.get(tagStored.lemRef);
        } else {
            return null;
        }
    }

    @Override
    public PosTag addTag(String tagString, Lemma tagLemma) {
        PosTagStored tag = new PosTagStored();
        tag.tagString = tagString;
        tag.lemRef = tagLemma.getID();
        connect(tag, tagLemma);
        tags.add(tag);
        return tag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(tags.toString());
        return sb.toString();
    }
}
