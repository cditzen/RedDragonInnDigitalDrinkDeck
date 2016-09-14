package cs309.reddragoninndigitaldrinkdeck.PreGameActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import cs309.reddragoninndigitaldrinkdeck.R;
import cs309.reddragoninndigitaldrinkdeck.UserData.User;


public class SignInActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Firebase myFirebaseRef;

    //private Button signInButton;
    private EditText nameEditText;

    private String userName;
    private static User user;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Track whether the sign-in button has been clicked so that we know to resolve
 * all issues preventing sign-in without waiting.
 */
    private boolean mSignInClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;

    private boolean mIntentInProgress;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private final int LOGIN_REQUEST = 1;

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        System.out.print("SignInActivity.resolveSignInError() hasResolution" + mConnectionResult.hasResolution());
        //if(userName.length() > 0 && userName.length() < 25)
        {
            user = new User("Taylor Welter");
            myFirebaseRef.child("Players/" + user.getName() + "/Name").setValue(user.getName());

            Intent intent = new Intent(this, FindLobbyActivity.class);
            startActivity(intent);
        }

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                System.out.println("SignInActivity.resolveSignInError()");
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void onConnected(Bundle connectionHint) {
        System.out.println("SignInActivity.onConnected() -> is called");

        mSignInClicked = false;
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();

            user = new User(personName);
            myFirebaseRef.child("Players/" + user.getName() + "/Name").setValue(user.getName());

            Intent intent = new Intent(this, FindLobbyActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                System.out.println("SignInActivity.onConnectedFailed: " + mSignInClicked);
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://reddragoninn.firebaseio.com/");

        //signInButton = (Button) findViewById(R.id.signInButton);
        nameEditText = (EditText) findViewById(R.id.editText);

        findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_sign_in
                        && !mGoogleApiClient.isConnecting()) {
                    mSignInClicked = true;
                    resolveSignInError();
                }
            }
         });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(Plus.API)
            .addScope(Plus.SCOPE_PLUS_LOGIN)
            .build();

        userName = "";
        nameEditText.addTextChangedListener(nameTextWatcher);
    }

    public static User getUser()
    {
        return user;
    }

    private TextWatcher nameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            userName = s.toString();
        }
    };

    public void submitName(View view)
    {
        if(userName.length() > 0 && userName.length() < 25)
        {
            user = new User(userName);
            myFirebaseRef.child("Players/" + user.getName() + "/Name").setValue(user.getName());

            Intent intent = new Intent(this, FindLobbyActivity.class);
            startActivity(intent);
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

        else if (requestCode == LOGIN_REQUEST)
        {
            if(responseCode == RESULT_FIRST_USER)
            {
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

//        if (mGoogleApiClient.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        if (mGoogleApiClient.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            mGoogleApiClient.disconnect();
//        }
        finish();
    }
}
