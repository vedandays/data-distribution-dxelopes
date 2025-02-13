package org.eltech.ddm.distribution.fileAgent;

import com.opencsv.CSVReader;
import org.eltech.ddm.distribution.common.MetaDataMessage;
import org.eltech.ddm.distribution.common.IMetaDataReader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvMetaDataReader implements IMetaDataReader {
    private String filePath;

    public CsvMetaDataReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public MetaDataMessage readHeaders() {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            CSVReader csvReader = new CSVReader(reader);
            List<String> headers = new ArrayList<>(Arrays.asList(csvReader.readNext()));
            System.out.println("Заголовки " + filePath);
            headers.forEach(System.out::println);
            return new MetaDataMessage(headers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
