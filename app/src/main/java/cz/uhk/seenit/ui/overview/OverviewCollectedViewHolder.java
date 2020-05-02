package cz.uhk.seenit.ui.overview;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import cz.uhk.seenit.R;

// Trida drzici reference na UI prvky polozky adapteru
public class OverviewCollectedViewHolder extends RecyclerView.ViewHolder {
    public int id;
    public AppCompatTextView name;
    public AppCompatTextView collectedDate;
    public NetworkImageView icon;

    public OverviewCollectedViewHolder(View view) {
        super(view);

        name = view.findViewById(R.id.overview_sticker_name);
        collectedDate = view.findViewById(R.id.overview_sticker_collected);
        icon = view.findViewById(R.id.overview_sticker_icon);
    }
}