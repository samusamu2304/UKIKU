package knf.kuma.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import knf.kuma.R;
import knf.kuma.animeinfo.ActivityAnime;
import knf.kuma.commons.PatternUtil;
import knf.kuma.commons.PicassoSingle;
import knf.kuma.pojos.AnimeObject;

public class SearchAdapter extends PagedListAdapter<AnimeObject,SearchAdapter.ItemHolder> {

    private static final DiffUtil.ItemCallback<AnimeObject> DIFF_CALLBACK = new DiffUtil.ItemCallback<AnimeObject>() {
        @Override
        public boolean areItemsTheSame(@NonNull AnimeObject oldItem, @NonNull AnimeObject newItem) {
            return oldItem.key == newItem.key;
        }

        @Override
        public boolean areContentsTheSame(@NonNull AnimeObject oldItem, @NonNull AnimeObject newItem) {
            return oldItem.name.equals(newItem.name) && oldItem.aid.equals(newItem.aid);
        }
    };
    private Fragment fragment;

    SearchAdapter(Fragment fragment) {
        super(DIFF_CALLBACK);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dir, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {
        final AnimeObject object=getItem(position);
        if (object!=null){
            PicassoSingle.get(fragment.getContext()).load(PatternUtil.getCover(object.aid)).into(holder.imageView);
            holder.textView.setText(object.name);
            holder.cardView.setOnClickListener(view -> ActivityAnime.open(fragment, object, holder.imageView, false, true));
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card)
        CardView cardView;
        @BindView(R.id.img)
        ImageView imageView;
        @BindView(R.id.title)
        TextView textView;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
