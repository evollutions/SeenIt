package cz.uhk.seenit.model;

import java.util.ArrayList;
import java.util.List;

public class StickersForLoc {
    public int id;
    public List<Sticker> stickers;

    public StickersForLoc() {
        stickers = new ArrayList<>();
    }

    public class Sticker {
        public int id;
        public String name;
        public boolean collected;
        public float latitude;
        public float longitude;
    }
}