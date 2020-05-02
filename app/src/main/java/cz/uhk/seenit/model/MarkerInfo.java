package cz.uhk.seenit.model;

public class MarkerInfo {
    public int stickerId;
    public boolean collected;

    public MarkerInfo(int stickerId, boolean collected) {
        this.stickerId = stickerId;
        this.collected = collected;
    }
}