package org.jfl2.core.conf.elements;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * 見た目に関する設定
 */
public class Appearance {
    @XmlAttribute
    public Integer width;
    @XmlAttribute
    public Integer height;
}
