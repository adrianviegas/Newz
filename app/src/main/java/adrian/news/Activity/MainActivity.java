package adrian.news.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDACallback;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wenchao.cardstack.CardStack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import adrian.news.ListAdapter;
import adrian.news.R;
import adrian.news.model.Article;

public class MainActivity extends AppCompatActivity {
    final String TAG = MainActivity.class.getName();
    TextView tvReplay;
    ListAdapter listAdapter;
    CardStack mCardStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvReplay = (TextView) findViewById(R.id.tvReplay);
        listAdapter = new ListAdapter(MainActivity.this, null);
        mCardStack = (CardStack) findViewById(R.id.container);

        tvReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvReplay.setVisibility(View.GONE);
//                mCardStack.reset(true);

            }
        });

        fetchContent();
    }

    private void fetchContent() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(R.string.loading);
        progressDialog.show();
        tvReplay.setVisibility(View.GONE);
        final CDAClient client = CDAClient.builder()
                .setSpace(R.string.cda_space)
                .setToken(R.string.cda_token)
                .build();
        client.fetch(CDAEntry.class).all(new CDACallback<CDAArray>() {
            @Override
            protected void onSuccess(CDAArray result) {
                Map mp = result.entries();

                Gson gson = new GsonBuilder().create();
                try {
                    JSONObject jsonObject = new JSONObject(gson.toJson(result));
                    jsonObject.get("");
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException", e);
                }

                Iterator it = mp.entrySet().iterator();
                ArrayList<Article> articles = new ArrayList<>();

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    CDAEntry entry = client.fetch(CDAEntry.class).one(pair.getKey().toString());
                    Map map = entry.rawFields();
                    String h = getData(map, "enterNewsTitle");
                    String title = h.replaceAll("^\\{en-US=\\[", "").replaceAll("\\]\\}$", "").replace('[', ' ').replace(']', ' ').trim();
                    h = getData(map, "newsContent");
                    String content = h.replaceAll("^\\{en-US=", "").replaceAll("\\}$", "");
                    String as = gson.toJson(map.get("enterNewsMedia"));
                    try {
                        String aq = (String) ((JSONObject) ((JSONObject) new JSONObject(as).get("en-US")).get("sys")).get("id");
                        CDAAsset cdaAsset = client.fetch(CDAAsset.class).one(aq);
                        articles.add(new Article(title, content, getData(map, "category"), getData(map, "date"), "http:" + cdaAsset.url(), getData(map, "tags"), getData(map, "allowcomments")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    it.remove();
                }
                final int articleCount = articles.size();

                mCardStack.setContentResource(R.layout.row_list);
                mCardStack.setStackMargin(0);


                mCardStack.setListener(new CardStack.CardEventListener() {
                    @Override
                    public boolean swipeEnd(int section, float distance) {
                        Log.d(TAG, "swipeEnd: ");
                        return (distance > 300);
                    }

                    @Override
                    public boolean swipeStart(int section, float distance) {
                        Log.d(TAG, "swipeStart: ");
                        return true;
                    }

                    @Override
                    public boolean swipeContinue(int section, float distanceX, float distanceY) {
                        Log.d(TAG, "swipeContinue: ");
                        return true;
                    }

                    @Override
                    public void discarded(int mIndex, int direction) {
                        Log.d(TAG, "discarded: " + mIndex + " " + direction);
                        if (mIndex == articleCount) {
                            tvReplay.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void topCardTapped() {
                        Log.d(TAG, "topCardTapped: ");
                    }
                });
                listAdapter.setList(articles);
                listAdapter.notifyDataSetChanged();
                mCardStack.setAdapter(listAdapter);
//                mCardStack.reset(true);


                progressDialog.hide();

                //((ListView) MainActivity.this.findViewById(R.id.listView)).setAdapter(new ListAdapter(MainActivity.this, articles));
            }
        });
    }

    private String getData(Map map, String s) {
        String q = "";
        try {
            q = map.get(s).toString().replaceAll("^\\{en-US=", "").replaceAll("\\}$", "");
        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException", e);

        }
        return q;
    }


}