package knf.kuma.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import knf.kuma.R;
import knf.kuma.commons.EAHelper;
import knf.kuma.database.CacheDB;

public class RecordActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.error)
    View error;
    private RecordsAdapter adapter;
    private boolean animate = true;
    private boolean isFirst = true;

    public static void open(Context context) {
        context.startActivity(new Intent(context, RecordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(EAHelper.getTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        toolbar.setTitle("Historial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        adapter = new RecordsAdapter(this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                animate = false;
                adapter.remove(viewHolder.getAdapterPosition());
                Snackbar.make(recyclerView, "Elemento eliminado", Snackbar.LENGTH_SHORT).show();
            }
        });
        touchHelper.attachToRecyclerView(recyclerView);
        CacheDB.INSTANCE.recordsDAO().getAll().observe(this, recordObjects -> {
            if (recordObjects != null) {
                if (animate) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.update(recordObjects);
                            if (isFirst) {
                                isFirst = false;
                                recyclerView.scheduleLayoutAnimation();
                            }
                        }
                    });
                } else {
                    animate = true;
                }
                if (recordObjects.size() == 0)
                    error.post(new Runnable() {
                        @Override
                        public void run() {
                            error.setVisibility(View.VISIBLE);
                        }
                    });
            }
        });
    }

    @LayoutRes
    private int getLayout(){
        if (PreferenceManager.getDefaultSharedPreferences(this).getString("lay_type","0").equals("0")){
            return R.layout.recycler_records;
        }else {
            return R.layout.recycler_records_grid;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_records, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                new MaterialDialog.Builder(this)
                        .content("¿Limpiar el historial?")
                        .positiveText("Continuar")
                        .negativeText("cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                CacheDB.INSTANCE.recordsDAO().clear();
                            }
                        }).build().show();
        }
        return super.onOptionsItemSelected(item);
    }
}
