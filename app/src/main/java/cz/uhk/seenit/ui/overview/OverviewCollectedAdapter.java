package cz.uhk.seenit.ui.overview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cz.uhk.seenit.R;
import cz.uhk.seenit.model.OverviewForUser;
import cz.uhk.seenit.utils.Formatter;
import cz.uhk.seenit.utils.VolleyUtils;

public class OverviewCollectedAdapter extends RecyclerView.Adapter<OverviewCollectedViewHolder> {
    // Kolekce samolepek
    private List<OverviewForUser.Sticker> stickers;
    private Context context;

    public OverviewCollectedAdapter(List<OverviewForUser.Sticker> stickers, Context context) {
        this.stickers = stickers;
        this.context = context;
    }

    @Override
    public OverviewCollectedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Vytvoreni noveho pohledu polozky pro recycler
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_collected_card, parent, false);

        // Vytvoreny pohled polozky predame view holderu
        return new OverviewCollectedViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(OverviewCollectedViewHolder holder, int position) {
        // Ziskame aktualni samolepku
        OverviewForUser.Sticker sticker = stickers.get(position);

        // Nastavime UI prvky aktualne nabindovane polozky recycleru
        holder.id = sticker.id;
        holder.name.setText(sticker.name);
        holder.collectedDate.setText(Formatter.formatDateShort(sticker.collectedDate));
        holder.icon.setImageUrl(sticker.iconUrl.toString(), VolleyUtils.getImageLoader(context));
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }
}