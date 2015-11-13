package org.jfl2.core.conf;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.DataBindingException;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 */
public class SerializeBaseTest {
    @Test
    public void testFromJson() throws Exception {
        JsonTestClass obj = JsonTestClass.fromJson(new StringReader("{\"element1\":\"val1\"}"), JsonTestClass.class);
        assertThat(obj.element1, is("val1"));
    }

    @Test
    public void testFromJson1() throws Exception {
        JsonTestClass obj = JsonTestClass.fromJson(new String("{\"element1\":\"val2\"}"), JsonTestClass.class);
        assertThat(obj.element1, is("val2"));
    }

    @Test
    public void testFromXml() throws Exception {
        XmlTestClass obj = XmlTestClass.fromXml(new StringReader("<XmlTestClass><element1>val1</element1></XmlTestClass>"), XmlTestClass.class);
        assertThat(obj.element1, is("val1"));
    }

    @Test
    public void testFromXml1() throws Exception {
        XmlTestClass obj = XmlTestClass.fromXml(new String("<XmlTestClass><element1>val2</element1></XmlTestClass>"), XmlTestClass.class);
        assertThat(obj.element1, is("val2"));
    }

    @Test
    public void testFrom() throws Exception {
        JsonTestClass obj = JsonTestClass.from(new StringReader("{\"element1\":\"val1\"}"), JsonTestClass.class, SerializeBase.OutputType.JSON);
        assertThat(obj.element1, is("val1"));

        try {
            JsonTestClass.from(new StringReader("{\"element1\":\"val1\"}"), JsonTestClass.class, SerializeBase.OutputType.XML);
            Assert.fail("Require exception");
        } catch (DataBindingException e) {
        }
    }

    @Test
    public void testFrom1() throws Exception {
        JsonTestClass obj = JsonTestClass.from(new String("{\"element1\":\"val2\"}"), JsonTestClass.class, SerializeBase.OutputType.JSON);
        assertThat(obj.element1, is("val2"));
    }

    @Test
    public void testGetOutputType() throws Exception {
        JsonTestClass jsonObj = JsonTestClass.fromJson(new String("{\"element1\":\"val2\"}"), JsonTestClass.class);
        XmlTestClass xmlObj = XmlTestClass.fromXml(new StringReader("<XmlTestClass><element1>val1</element1></XmlTestClass>"), XmlTestClass.class);

        assertThat(jsonObj.getOutputType(), is(SerializeBase.OutputType.JSON));
        assertThat(xmlObj.getOutputType(), is(SerializeBase.OutputType.XML));
    }

    @Test
    public void testToJson() throws Exception {
        JsonTestClass jsonObj = new JsonTestClass();
        jsonObj.element1 = "val2";
        assertThat(jsonObj.toJson(), is("{\"element1\":\"val2\"}"));
        jsonObj.element1 = null;
        assertThat(jsonObj.toJson(), is("{\"element1\":null}"));
    }

    @Test
    public void testToJson1() throws Exception {
        JsonTestClass jsonObj = new JsonTestClass();
        jsonObj.element1 = "val2";
        assertThat(jsonObj.toJson(true), is("{\"element1\":\"val2\"}"));
        assertThat(jsonObj.toJson(false), is("{\"element1\":\"val2\"}"));
        jsonObj.element1 = null;
        assertThat(jsonObj.toJson(true), is("{\"element1\":null}"));
        assertThat(jsonObj.toJson(false), is("{}"));
    }

    @Test
    public void testToXml() throws Exception {
        XmlTestClass xmlObj = new XmlTestClass();
        xmlObj.element1 = "val1";
        assertThat(xmlObj.toXml(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<xmlTestClass>\n    <element1>val1</element1>\n</xmlTestClass>\n"));
        xmlObj.element1 = null;
        assertThat(xmlObj.toXml(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<xmlTestClass/>\n"));
    }

    @Test
    public void testToString() throws Exception {
        XmlTestClass xmlObj = new XmlTestClass();
        xmlObj.element1 = "val1";
        assertThat(xmlObj.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<xmlTestClass>\n    <element1>val1</element1>\n</xmlTestClass>\n"));

        JsonTestClass jsonObj = new JsonTestClass();
        jsonObj.element1 = "val2";
        assertThat(jsonObj.toString(), is("{\"element1\":\"val2\"}"));
        jsonObj.element1 = null;
        assertThat(jsonObj.toString(), is("{\"element1\":null}"));
    }

    @Test
    public void testEquals_json() throws Exception {
        JsonTestClass jsonObj1 = JsonTestClass.fromJson(new StringReader("{\"element1\":\"val1\"}"), JsonTestClass.class);
        JsonTestClass jsonObj2 = JsonTestClass.fromJson(new StringReader("{\"element1\":\"val1\"}"), JsonTestClass.class);
        assertThat(jsonObj1.equals(jsonObj2), is(true));
    }

    @Test
    public void testEquals_xml() throws Exception {
        XmlTestClass xmlObj1 = XmlTestClass.fromXml(new StringReader("<XmlTestClass><element1>val1</element1></XmlTestClass>"), XmlTestClass.class);
        XmlTestClass xmlObj2 = XmlTestClass.fromXml(new StringReader("<XmlTestClass><element1>val1</element1></XmlTestClass>"), XmlTestClass.class);
        assertThat(xmlObj1.equals(xmlObj2), is(true));
    }

}
