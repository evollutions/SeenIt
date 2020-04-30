package cz.uhk.seenit.model;

import java.util.ArrayList;
import java.util.List;

public class StickersForLocAndUser {

    private int id;
    private List<Sticker> stickers;

    public StickersForLocAndUser() {
        stickers = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
    }
}