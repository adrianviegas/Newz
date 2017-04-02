package adrian.news.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import adrian.news.App;
import adrian.news.DatabaseAdapter;
import adrian.news.R;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    final int RC_SIGN_IN = 300;
    final Context context = SplashActivity.this;
    final String TAG = SplashActivity.class.getName();
    LoginButton loginButton;
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_splash);
        final Context context = SplashActivity.this;
        final DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }

        if (!App.prefs.getString("email", "").trim().isEmpty()) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
        final String a = null;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(SplashActivity.this /* FragmentActiviOnConnectionFailedListenerty */, this /*  */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("sandeep.news", PackageManager.GET_SIGNATURES);
//            for (android.content.pm.Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String aa = Base64.encodeToString(md.digest(), Base64.DEFAULT);
//                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

        SignInButton signInButton = (SignInButton) findViewById(R.id.bGooglePlus);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        loginButton = (LoginButton) findViewById(R.id.bFacebook);
        loginButton.setReadPermissions("email,public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
                progressDialog.setMessage("Processing data...");
                progressDialog.show();
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        progressDialog.hide();
                        if (!App.prefs.getString("email", "").trim().isEmpty()) {
                            tracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Login")
                                    .setAction("Login Success")
                                    .setLabel("Facebook")
                                    .setValue(0)
                                    .build());
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        } else {
                            tracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Login")
                                    .setAction("Login Success")
                                    .setLabel("Facebook")
                                    .setValue(1)
                                    .build());
                            newLogin(bFacebookData.getString("email"), bFacebookData.getString("name"), bFacebookData.getString("pic"), "");
                        }

                    }

                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code3
                Log.d("myTag", exception.getMessage());
                exception.printStackTrace();
            }
        });


        tracker = ((App) getApplication()).getDefaultTracker();
// Set screen name.
        tracker.setScreenName("SplashActivity");
        tracker.enableAutoActivityTracking(true);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        final ImageView bBacktoLogin = (ImageView) findViewById(R.id.bBacktoLogin);
        bBacktoLogin.setVisibility(View.GONE);
        findViewById(R.id.bCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.llLogin).setVisibility(View.GONE);
                findViewById(R.id.llSignup).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.bLogin)).setText("CREATE");
                bBacktoLogin.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.bBacktoLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.llLogin).setVisibility(View.VISIBLE);
                findViewById(R.id.llSignup).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.bLogin)).setText("LOGIN");
                bBacktoLogin.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.bLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) findViewById(R.id.bLogin)).getText().toString().equals("LOGIN")) {


                    TextView email = (TextView) findViewById(R.id.etEmail);
                    TextView password = (TextView) findViewById(R.id.etPassword);

                    if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                        Toast.makeText(SplashActivity.this, "Dont Leave Blank", Toast.LENGTH_SHORT).show();
                    } else {
                        if (databaseAdapter.checkLogin(((TextView) findViewById(R.id.etEmail)).getText().toString(),
                                ((TextView) findViewById(R.id.etPassword)).getText().toString())) {
                            Toast.makeText(SplashActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(SplashActivity.this, "Wrong Username/Password", Toast.LENGTH_SHORT).show();
                            ((TextView) findViewById(R.id.etPassword)).setText("");
                            ((TextView) findViewById(R.id.etEmail)).setText("");
                        }
                    }
                } else {
                    TextView email = ((TextView) findViewById(R.id.etSignupEmail));
                    TextView tvpassword = ((TextView) findViewById(R.id.etSignupPassword));
                    TextView tvcpassword = ((TextView) findViewById(R.id.etSignupConfirmPassword));
                    if (email.getText().toString().isEmpty() || tvcpassword.getText().toString().isEmpty() || tvpassword.getText().toString().isEmpty()) {
                        Toast.makeText(SplashActivity.this, "Dont Leave Blank", Toast.LENGTH_SHORT).show();
                    } else {

                        String password = tvpassword.getText().toString();
                        if (password.equals(tvcpassword.getText().toString())) {
                            databaseAdapter.newUser(email.getText().toString(), password);
                            Toast.makeText(SplashActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SplashActivity.this, "Wrong Confirm Password", Toast.LENGTH_SHORT).show();
                            ((TextView) findViewById(R.id.etSignupPassword)).setText("");
                            ((TextView) findViewById(R.id.etSignupConfirmPassword)).setText("");
                        }
                    }
                }
            }
        });
    }

    private Bundle getFacebookData(JSONObject object) {

        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=150&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                Log.e(TAG, "URL not valid", e);
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
            bundle.putString("name", object.getString("first_name") + " " + object.getString("last_name"));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
        return bundle;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            updateUI(true, acct);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false, null);
        }
    }

    private void updateUI(boolean b, GoogleSignInAccount acct) {
        if (b) {
            if (!App.prefs.getString("email", "").trim().isEmpty()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else {
                Person person = null;
                if (mGoogleApiClient.hasConnectedApi(Plus.API))
                    person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Login")
                        .setAction("Login Success")
                        .setLabel("Google")
                        .build());
                newLogin(acct.getEmail(), acct.getDisplayName(), String.valueOf(acct.getPhotoUrl()), (person != null) ? String.valueOf(person.getGender()) : "");
            }

            Toast.makeText(context, R.string.msg_login_success, Toast.LENGTH_SHORT).show();
        } else {
            //TODO
            Toast.makeText(context, R.string.msg_login_fail, Toast.LENGTH_SHORT).show();
        }
    }

    void newLogin(String email, String name, String profilepic, String gender) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("name", name);
        intent.putExtra("pic", profilepic);
        intent.putExtra("gender", gender);
        startActivity(intent);
        finish();
    }


}

