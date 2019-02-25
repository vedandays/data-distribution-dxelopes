package org.eltech.ddm.runner;

import jade.Boot;

public class RunOnNonMainNode {
    public static void main(String[] args) {
        // "-gui"
        String[] arg = {"-port 1098"}; //for tests
        new Boot().main(arg);
        //new new Boot().main(args);
    }
}
