package org.eltech.ddm.runner;

import jade.Boot;

public class RunOnNonMainNode {
    public static void main(String[] args) {
        // run with arguments
        String[] arg = {"-gui","-port", "1098"}; //for tests
        new Boot().main(arg);

        /* default run */
        //new Boot().main(args);
    }
}
