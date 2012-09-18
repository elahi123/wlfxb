/**
 * 
 */
package de.tuebingen.uni.sfs.wlf1.ed.xb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import de.tuebingen.uni.sfs.wlf1.ed.api.ExternalDataLayer;
import de.tuebingen.uni.sfs.wlf1.utils.CommonAttributes;

/**
 * @author Yana Panchenko
 *
 */
@XmlTransient
public abstract class ExternalDataLayerStored implements ExternalDataLayer {

	@XmlAttribute(name=CommonAttributes.TYPE, required = true)
	String type;
	@XmlValue
	String dataURI;
	
	protected ExternalDataLayerStored() {
		super();
	}
	
	protected ExternalDataLayerStored(String mimeType) {
		this.type = mimeType;
	}

	public String getDataMimeType() {
		return type;
	}
	
	public String getLink() {
		return dataURI;
	}
	
	public void addLink(String dataURI) {
		this.dataURI = dataURI;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDataMimeType());
		sb.append(" ");
		sb.append(getLink());
		return sb.toString();
	}

}