package org.eltech.ddm.runner;

import jade.Boot;

public class RunOnNonMainNode {
    public static void main(String[] args) {
        String[] arg = {"-port 1098"};
        new Boot().main(arg);
    }
}
