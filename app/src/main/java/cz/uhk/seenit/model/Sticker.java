package cz.uhk.seenit.model;

public class Sticker {
    private int id;
    private String name;
    private String loc;
    private boolean collected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    // Zemepisna sirka je prvni index v loc (pred carkou)
    public float getLatitude() {
        return Float.parseFloat(loc.split(",")[0]);
    }

    // Zemepisna delka je druhy index v loc (za carkou)
    public float getLongitude() {
        return Float.parseFloat(loc.split(",")[1]);
    }
}