package cz.uhk.seenit.model;

public class MarkerInfo {
    private int stickerId;
    private boolean collected;

    public MarkerInfo(int stickerId, boolean collected) {
        this.stickerId = stickerId;
        this.collected = collected;
    }

    public int getStickerId() {
        return stickerId;
    }

    public void setStickerId(int stickerId) {
        this.stickerId = stickerId;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}
