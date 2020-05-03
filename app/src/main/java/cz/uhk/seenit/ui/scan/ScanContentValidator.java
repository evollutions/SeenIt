package cz.uhk.seenit.ui.scan;

// Trida na validaci oskenovaneho obsahu
public class ScanContentValidator {

    // Obsah samolepek musi mit tento prefix
    private static final String STICKER_PREFIX = "SeenIt:";

    // Delka prefixu je fixni - 7 znaku
    private static final int STICKER_PREFIX_LENGTH = 7;

    // Delka obsahu je fixni - 39 znaku
    private static final int STICKER_CONTENT_LENGTH = 39;

    // Konstanta pro lokalni validaci bez API
    public static final String DEMO_STICKER_CONTENT = "SeenIt:22c795596c1f48c788e7eb5ce3b41300";

    /**
     * @return ID samolepky pokud je obsah validni, jinak null
     */
    public static String getStickerId(String stickerContent) {
        if (!stickerContent.startsWith(STICKER_PREFIX)) {
            // Obsah neobsahuje spravny prefix
            return null;
        }

        if (stickerContent.length() != STICKER_CONTENT_LENGTH) {
            // Obsah nema spravnou fixni delku
            return null;
        }

        // Obsah vypada validni, ziskame z nej ID samolepky
        return stickerContent.substring(STICKER_PREFIX_LENGTH, STICKER_CONTENT_LENGTH);
    }
}