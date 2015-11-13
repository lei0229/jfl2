package org.jfl2.core.conf.elements;

import javax.xml.bind.annotation.XmlAttribute;

public class MainFrameHistory {
    @XmlAttribute
    public Double displayX;
    @XmlAttribute
    public Double displayY;
    @XmlAttribute
    public Double width = 800D;
    @XmlAttribute
    public Double height = 500D;
}
