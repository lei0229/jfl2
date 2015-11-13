package org.jfl2.core.conf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.jfl2.core.Jfl2Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;


public abstract class SerializeBase {
    @JsonIgnore
    @XmlTransient
    private static Logger log = LoggerFactory.getLogger(SerializeBase.class);

    /**
     * 文字列から読み込みます
     *
     * @param json        source
     * @param resultClass Class of result
     * @param <T>         Class of result
     * @return 生成したクラス
     */
    static final public <T extends SerializeBase> T fromJson(String json, Class<T> resultClass) throws IOException {
        return fromJson(new StringReader(json), resultClass);
    }

    /**
     * readerから読み込みます
     *
     * @param reader      source
     * @param resultClass Class of result
     * @param <T>         Class of result
     * @return 生成したクラス
     */
    static final public <T extends SerializeBase> T fromJson(Reader reader, Class<T> resultClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(Jfl2Const.DATETIME_FORMAT_RFC_3339));
        return mapper.readValue(reader, resultClass);
    }

    /**
     * readerから読み込みます
     *
     * @param xml         XML文字列
     * @param resultClass 返却クラス
     * @param <T>         返却クラス
     * @return 生成したオブジェクト
     * @throws IOException
     */
    static final public <T extends SerializeBase> T fromXml(String xml, Class<T> resultClass) throws IOException {
        return fromXml(new StringReader(xml), resultClass);
    }

    /**
     * readerから読み込みます
     *
     * @param reader      読み込み元
     * @param resultClass 返却クラス
     * @param <T>         返却クラス
     * @return 生成したオブジェクト
     * @throws IOException
     */
    static final public <T extends SerializeBase> T fromXml(Reader reader, Class<T> resultClass) throws IOException {
        return JAXB.unmarshal(reader, resultClass);
    }

    /**
     * @param reader
     * @param resultClass
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    static final public <T extends SerializeBase> T from(Reader reader, Class<T> resultClass, OutputType type) throws IOException {
        if (OutputType.JSON.equals(type)) {
            return fromJson(reader, resultClass);
        }
        if (OutputType.XML.equals(type)) {
            return fromXml(reader, resultClass);
        }
        throw new NullPointerException("Needs OutputType");
    }

    /**
     * @param body
     * @param resultClass
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    static final public <T extends SerializeBase> T from(String body, Class<T> resultClass, OutputType type) throws IOException {
        if (OutputType.JSON.equals(type)) {
            return fromJson(body, resultClass);
        }
        if (OutputType.XML.equals(type)) {
            return fromXml(body, resultClass);
        }
        throw new NullPointerException("Needs OutputType");
    }

    /**
     * 出力タイプを指定します。
     *
     * @return XML / JSON
     */
    @JsonIgnore
    @XmlTransient
    public abstract OutputType getOutputType();

    /**
     * @param outputNullField
     * @return
     * @throws JsonProcessingException
     */
    final public String toJson(boolean outputNullField) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(Jfl2Const.DATETIME_FORMAT_RFC_3339));
        if (!outputNullField) {
            mapper.setSerializationInclusion(Include.NON_NULL);    // nullを出さない
        }
        return mapper.writeValueAsString(this);
    }

    /**
     * @return
     * @throws JsonProcessingException
     */
    final public String toJson() throws JsonProcessingException {
        return toJson(true);
    }

    /**
     * @return
     */
    final public String toXml() {
        StringWriter out = new StringWriter();
        JAXB.marshal(this, out);
        return out.toString();
    }

    /**
     * to String
     */
    @Override
    public String toString() {
        if (getOutputType() == OutputType.JSON) {
            try {
                return toJson();
            } catch (JsonProcessingException e) {
                log.error("json perse error", e);
            }
        }
        if (getOutputType() == OutputType.XML) {
            return toXml();
        }
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
//		if( log.isDebugEnabled() ){ log.debug("---- this: {} ", this); log.debug("----  obj: {} ", obj); }
        if (obj == null) {
            return false;
        }
        return StringUtils.equals(this.toString(), obj.toString());
    }

    /**
     * 出力タイプ
     */
    public enum OutputType {
        JSON,
        XML,;
    }
}
