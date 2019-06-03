package org.eltech.ddm.common;
/**
 * Custom class for synchronization communication of agents
 *
 * @author Derkach Petr
 */
public class StateExist {
    private boolean isCreated = false;

    public StateExist(boolean isCreated) {
        this.isCreated = isCreated;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }
}
