package org.jfl2.core.conf;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

/**
 * テスト用
 */
public class XmlTestClass extends SerializeBase {
    @XmlElement
    public String element1;

    public XmlTestClass(){

    }

    @Override
    public OutputType getOutputType() {
        return OutputType.XML;
    }
}
