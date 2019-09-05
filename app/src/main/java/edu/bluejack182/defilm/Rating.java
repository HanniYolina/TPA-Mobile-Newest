package edu.bluejack182.defilm;

import java.util.HashMap;
import java.util.Map;

public class Rating {

    private String Source;
    private String Value;
    private Map<String, Object> AdditionalProperties = new HashMap<String, Object>();

    public Rating(String source, String value, Map<String, Object> additionalProperties) {
        Source = source;
        Value = value;
        AdditionalProperties = additionalProperties;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public Map<String, Object> getAdditionalProperties() {
        return AdditionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        AdditionalProperties = additionalProperties;
    }
}
