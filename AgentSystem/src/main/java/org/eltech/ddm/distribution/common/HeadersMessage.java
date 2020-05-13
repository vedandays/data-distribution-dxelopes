package org.eltech.ddm.distribution.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HeadersMessage implements Serializable {
    private List<String> headerNames = new ArrayList<>();

    public HeadersMessage() {
    }

    public HeadersMessage(List<String> headerNames) {
        this.headerNames = headerNames;
    }

    public List<String> getHeaderNames() {
        return headerNames;
    }

    public void setHeaderNames(List<String> headerNames) {
        this.headerNames = headerNames;
    }
}
