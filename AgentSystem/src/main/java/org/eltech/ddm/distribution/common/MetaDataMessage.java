package org.eltech.ddm.distribution.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetaDataMessage implements Serializable {
    private List<String> headerNames = new ArrayList<>();

    public MetaDataMessage() {
    }

    public MetaDataMessage(List<String> headerNames) {
        this.headerNames = headerNames;
    }

    public List<String> getHeaderNames() {
        return headerNames;
    }

    public void setHeaderNames(List<String> headerNames) {
        this.headerNames = headerNames;
    }
}
