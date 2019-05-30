package org.eltech.ddm.common;

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
