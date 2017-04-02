package adrian.news.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import adrian.news.R;

public class WalkthroughActivity extends AppIntro {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Award Winning Content", "We only publish the award winning content" +
                " from the best of the writers who" +
                " curate the content people love to read!", R.drawable.step1, Color.parseColor("#ffffff"), Color.parseColor("#000000"), Color.parseColor("#000000")));
        addSlide(AppIntroFragment.newInstance("Flexible Subscription", "You can now avail flexi subcription to News. Just select when & how you want your news content & we shall deliver.", R.drawable.step2, Color.parseColor("#ffffff"), Color.parseColor("#000000"), Color.parseColor("#000000")));
        addSlide(AppIntroFragment.newInstance("Choose top Categories", "Get the top stories from a variety of" +
                " News Category we have designed" +
                " to fit your mood.", R.drawable.step3, Color.parseColor("#ffffff"), Color.parseColor("#000000"), Color.parseColor("#000000")));
        addSlide(AppIntroFragment.newInstance("Swipe n Read", "Just swipe the page and move on to " +
                " read the next News. You can archive news" +
                " to keep it inboxed.", R.drawable.step4, Color.parseColor("#ffffff"), Color.parseColor("#000000"), Color.parseColor("#000000")));
        Fragment fragment = AppIntroFragment.newInstance("And that’s not all", "You can use a simple account or access our " +
                " News from the your Facebook or Google account.", R.drawable.rocket, Color.parseColor("#ffffff"), Color.parseColor("#000000"), Color.parseColor("#000000"));
        addSlide(fragment);
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));
        setDoneText("GET STARTED");
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        onDonePressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(WalkthroughActivity.this, SplashActivity.class));
        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
