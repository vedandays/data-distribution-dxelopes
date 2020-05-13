package org.eltech.ddm.distribution.res;

import org.eltech.ddm.environment.DataDistribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ResParser {
    public static DataDistribution readResult(){
        try {
            File myObj = new File("E:\\Programming\\bayes-dxdevelops-agents-impl\\AgentSystem\\src\\main\\java\\org\\eltech\\ddm\\distribution\\res\\res.txt"); //todo перенести в resources
            Scanner myReader = new Scanner(myObj);
            String res = myReader.nextLine();
            myReader.close();
            return DataDistribution.valueOf(res);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Не удалось считать результат:" + e);
        }
    }
}
