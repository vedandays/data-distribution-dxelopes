package org.eltech.ddm.distribution.central;

import org.eltech.ddm.distribution.common.HeadersMessage;
import org.eltech.ddm.environment.DataDistribution;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Analyzer {

    public static DataDistribution analyze(HeadersMessage... messages) {
        List<List<String>> sortedHeaders = Arrays.stream(messages)
                .map(m -> m.getHeaderNames()
                        .stream()
                        .sorted()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return compareHeaders(sortedHeaders) ? DataDistribution.HORIZONTAL_DISTRIBUTION : DataDistribution.VERTICAL_DISTRIBUTION;
    }

    private static boolean compareHeaders(List<List<String>> sortedHeaders) {
        int size = sortedHeaders.get(0).size();
        boolean isSizeEquals = compareBySize(size, sortedHeaders);
        if (!isSizeEquals) {
            return false;
        }

        return compareByValues(sortedHeaders);
    }

    private static boolean compareByValues(List<List<String>> sortedHeaders) {
        List<String> headers = sortedHeaders.get(0);
        for (int i = 1; i < sortedHeaders.size(); i++) {
            List<String> currentHeaders = sortedHeaders.get(i);

            for (int j = 0; j < headers.size(); j++) {
                if (!headers.get(i).equalsIgnoreCase(currentHeaders.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean compareBySize(int size, List<List<String>> sortedHeaders) {
        for (int i = 1; i < sortedHeaders.size(); i++) {
            if (size != sortedHeaders.get(i).size()) {
                return false;
            }
        }
        return true;
    }


}
