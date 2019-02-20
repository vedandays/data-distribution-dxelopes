package org.eltech.ddm.frotend.environment;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eltech.ddm.environment.DataDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Actor Execution environment settings contains handlers count
 * type of data distribution and fileList to process with actors
 *
 * @author etitkov
 */
@Getter
@RequiredArgsConstructor
public class ActorsExecutionEnvironmentSettings {
    @NonNull
    private final DataDistribution dataDistribution;

    private final List<String> fileList = new ArrayList<>();

    /**
     * Builder-like method to provide data files
     * @param data - file relative or absolute path
     * @return - current instance
     */
    public ActorsExecutionEnvironmentSettings provideDatafile(String... data) {
        fileList.addAll(Arrays.asList(data));
        return this;
    }
}

