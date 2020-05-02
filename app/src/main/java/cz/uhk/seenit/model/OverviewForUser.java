package cz.uhk.seenit.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OverviewForUser {
    public int id;
    public int collectedStickerCount;
    public int totalStickerCount;
    public List<Sticker> stickers;

    public OverviewForUser() {
        stickers = new ArrayList<>();
    }

    public class Sticker {
        public int id;
        public String name;
        public URL iconUrl;
        public Date collectedDate;
    }
}