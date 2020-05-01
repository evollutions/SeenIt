package cz.uhk.seenit.model;

import java.net.URL;
import java.util.Date;

public class StickerDetail {
    private int id;
    private String name;
    private String desc;
    private URL iconUrl;
    private Date collectedDate;
    private Date collectedLastDate;
    private int collectedByPercent;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public URL getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(URL iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Date getCollectedDate() {
        return collectedDate;
    }

    public void setCollectedDate(Date collectedDate) {
        this.collectedDate = collectedDate;
    }

    public Date getCollectedLastDate() {
        return collectedLastDate;
    }

    public void setCollectedLastDate(Date collectedLastDate) {
        this.collectedLastDate = collectedLastDate;
    }

    public int getCollectedByPercent() {
        return collectedByPercent;
    }

    public void setCollectedByPercent(int collectedByPercent) {
        this.collectedByPercent = collectedByPercent;
    }
}