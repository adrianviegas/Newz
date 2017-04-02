package adrian.news;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.List;

import adrian.news.model.Article;
import adrian.news.util.DownloadImageTask;

/**
 * Created by Adrian on 07/08/2016.
 */

public class ListAdapter extends ArrayAdapter<Article> {
    private Context context;
    int[] imageId;
    private static LayoutInflater inflater = null;
    private List<Article> articles;
    private final String TAG = ListAdapter.class.getName();

    public ListAdapter(Context context, List<Article> articles) {
        super(context, R.layout.row_list);

        this.articles = articles;
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(List<Article> articles) {
        this.articles = articles;
    }


    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Article getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View rowView;
        rowView = inflater.inflate(R.layout.row_list, null);
        ((TextView) rowView.findViewById(R.id.title)).setText(articles.get(position).getTitle());
        ((TextView) rowView.findViewById(R.id.content)).setText(articles.get(position).getContent().replace("  ", " ").replace("\n", " ").replace("\r", " "));
//        ((TextView)rowView.findViewById(R.id.)).setText(articles.get(position).getCategory());
        LinearLayout tags = (LinearLayout) rowView.findViewById(R.id.llTags);
        String[] arr = articles.get(position).getTags().replace('[', ' ').replace(']', ' ').trim().split(",");


        for (String a : arr) {
            TextView textView = new TextView(context);
            textView.setText(a);
//            textView.setA
            textView.setBackgroundResource(R.drawable.rounded_bg);
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_bg));
            } else {
                textView.setBackground(context.getResources().getDrawable(R.drawable.rounded_bg));
            }
            int padding = App.getDP(10);
            textView.setPadding(App.getDP(8), App.getDP(6), App.getDP(8), App.getDP(6));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int margin = App.getDP(10);
            params.setMargins(0, 0, margin, 0);
            textView.setLayoutParams(params);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tags.addView(textView);

        }
        ((TextView) rowView.findViewById(R.id.date)).setText(articles.get(position).getDate());
        final String link = articles.get(position).getImage();
        final VideoView videoView = (VideoView) rowView.findViewById(R.id.VideoView);
        videoView.setVisibility(View.GONE);

        if (link.contains("mp4")) {
            try {
                if (true) {
                    final ImageView ivImage = (ImageView) rowView.findViewById(R.id.ivImage);
                    ivImage.setImageResource(R.drawable.ic_play);
                    ivImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
                    ivImage.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ivImage.setVisibility(View.GONE);
                            videoView.setVisibility(View.VISIBLE);
                            videoView.setVideoURI(Uri.parse(link));
                            videoView.start();
                        }
                    });
                } else {
                    ImageView ivImage = (ImageView) rowView.findViewById(R.id.ivImage);
                    ivImage.setVisibility(View.GONE);
                    final VideoView videoView1 = (VideoView) rowView.findViewById(R.id.VideoView);
                    videoView1.setVideoURI(Uri.parse(link));
                    videoView1.start();

                    videoView1.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videoView1.start();
                        }
                    });

                }
            } catch (Exception e) {
                Log.e(TAG, "Error connecting", e);

                Toast.makeText(context, "Error connecting", Toast.LENGTH_SHORT).show();
            }
        } else {
            videoView.setVisibility(View.GONE);
            try {
                new DownloadImageTask((ImageView) rowView.findViewById(R.id.ivImage))
                        .execute(articles.get(position).getImage());
            } catch (Exception e) {
                Log.e(TAG, "Error downloading image", e);
            }
        }

        final int padd = App.getDP(20);

        rowView.findViewById(R.id.bComment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Comment");
                builder.setPositiveButton("OK", null);
                EditText editText = new EditText(context);
                editText.setHint("Comment");
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setPadding(padd, 0, padd, 0);
                editText.setLayoutParams(layoutParams);
                linearLayout.addView(editText);
                builder.setView(linearLayout);
                builder.setNegativeButton("CANCEL", null);
                builder.create().show();
            }
        });
        rowView.findViewById(R.id.bRate).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Rate");
                builder.setPositiveButton("OK", null);
                RatingBar editText = new RatingBar(context);
                editText.setMax(5);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setPadding(padd, 0, padd, 0);
                editText.setLayoutParams(layoutParams2);
                linearLayout.addView(editText);
                builder.setView(linearLayout);
                builder.setNegativeButton("CANCEL", null);
                builder.create().show();
            }
        });
        rowView.findViewById(R.id.bShare).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, articles.get(position).getTitle());
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                context.startActivity(Intent.createChooser(intent, "Share"));
            }
        });
        return rowView;
    }
}
