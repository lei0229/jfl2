package org.jfl2.core.conf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * テスト用
 */
public class JsonTestClass extends SerializeBase {
    @JsonProperty
    public String element1;

    public JsonTestClass(){

    }

    @Override
    public OutputType getOutputType() {
        return OutputType.JSON;
    }
}
