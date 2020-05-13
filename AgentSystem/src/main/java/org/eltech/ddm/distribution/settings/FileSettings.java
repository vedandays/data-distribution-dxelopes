package org.eltech.ddm.distribution.settings;

import org.eltech.ddm.distribution.common.FileFormat;

import java.io.Serializable;

public class FileSettings extends ASettings implements Serializable{
    private String filePath;
    private FileFormat fileFormat;

    public FileSettings(String settingsString) {
        this.settingsString = settingsString;
        String splitBySymbol = ",";
        String[] lines;
        lines = settingsString.split(splitBySymbol);

        filePath = lines[6];
        int startIndex = filePath.lastIndexOf(".") + 1;
        int endIndex = filePath.length();
        this.fileFormat = FileFormat.valueOf(filePath.substring(startIndex, endIndex).toUpperCase());
    }

    public FileSettings() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public String getSettingsString() {
        return settingsString;
    }
}
