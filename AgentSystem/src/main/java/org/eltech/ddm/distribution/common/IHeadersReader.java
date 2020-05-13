package org.eltech.ddm.distribution.common;

public interface IHeadersReader {

    /**
     * Считать названия столбцов источника.
     *
     * @return список столбцов источника.
     */
    HeadersMessage readHeaders();
}
