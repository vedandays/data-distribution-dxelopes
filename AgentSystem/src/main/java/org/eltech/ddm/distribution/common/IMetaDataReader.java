package org.eltech.ddm.distribution.common;

public interface IMetaDataReader {

    /**
     * Считать названия столбцов источника.
     *
     * @return список столбцов источника.
     */
    MetaDataMessage readHeaders();
}
