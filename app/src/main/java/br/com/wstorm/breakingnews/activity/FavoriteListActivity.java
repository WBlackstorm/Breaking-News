package br.com.wstorm.breakingnews.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.wstorm.breakingnews.R;
import br.com.wstorm.breakingnews.model.News;

/**
 * An activity representing a list of News. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NewsDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class FavoriteListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_news_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.favorite_list));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        View recyclerView = findViewById(R.id.news_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.news_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_home, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

       if (id == android.R.id.home) {

            finish();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        try{

            GsonBuilder b = new GsonBuilder();
            b.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
            Gson gson = b.create();

            String json = null;

            InputStream is = getAssets().open("news.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            Type listType = new TypeToken<ArrayList<News>>() {}.getType();
            List<News> newsList = gson.fromJson(json, listType);

            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(newsList));


        } catch (JsonSyntaxException e) {

            Log.d("NewsListActivity", "Problema no parsing do json", e.getCause());

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<News> newsList;

        public SimpleItemRecyclerViewAdapter(List<News> items) {
            newsList = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            final News news = newsList.get(position);

            if (news.getImage() != null && news.getImage() != "") {

                Picasso.with(FavoriteListActivity.this)
                        .load(news.getImage())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder).into(holder.image);

            } else {

                holder.image.setImageDrawable(getDrawable(R.drawable.placeholder));

            }

            holder.title.setText(news.getTitle());

            holder.description.setText(news.getDescription());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(NewsDetailFragment.ARG_ITEM_ID, news);
                        NewsDetailFragment fragment = new NewsDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.news_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, NewsDetailActivity.class);
                        intent.putExtra(NewsDetailFragment.ARG_ITEM_ID, news);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final ImageView image;
            public final TextView title;
            public final TextView description;

            public ViewHolder(View view) {
                super(view);
                image = (ImageView)view.findViewById(R.id.image);
                title = (TextView) view.findViewById(R.id.title);
                description = (TextView) view.findViewById(R.id.description);
            }

        }

    }

}
