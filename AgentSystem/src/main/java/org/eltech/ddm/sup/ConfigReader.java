package org.eltech.ddm.sup;

import org.eltech.ddm.agents.AgentInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/*reading file with agents information */

public class ConfigReader {
   /* public static  ArrayList<AgentInfo> readFile(String filePath){
        String line;
        String[] lines;
        String splitBySymbol = ",";

        ArrayList<AgentInfo> agentInfoArrayList = new ArrayList<>();

        try( BufferedReader bk = new BufferedReader(new FileReader(filePath)) )  {

            while ((line = bk.readLine()) != null) {
                agentInfoArrayList.add(Parser.setInAgentInfo(splitBySymbol, line));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return agentInfoArrayList;
    }*/
}
