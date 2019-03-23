package sebaudracco.proyectocoop;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

    public class LogGoog extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

        private ImageView photoImageView;
        private TextView nameTextView;
        private TextView emailTextView;
        private TextView idTextView;
        private FirebaseAuth firebaseAuth;
        private FirebaseAuth.AuthStateListener firebaseAuthListener;
        private GoogleApiClient googleApiClient;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_log_goog);

            photoImageView = (ImageView) findViewById(R.id.photoImageView1);
            nameTextView = (TextView) findViewById(R.id.nameTextView);
            emailTextView = (TextView) findViewById(R.id.emailTextView);
            idTextView = (TextView) findViewById(R.id.idTextView);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                    .requestEmail()
                    .build();

            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user != null) {
                        setUserData(user);


                    } else {
                        user=null;
                        goLogInScreen ();
                        signOut ();


                    }
                }

                public void signOut() {


                    // [START auth_fui_signout]
                    AuthUI.getInstance()
                            .signOut(LogGoog.this)
                            .addOnCompleteListener(new OnCompleteListener<Void> () {
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    // [END auth_fui_signout]
                }


            };




        }
        private void setUserData(FirebaseUser user) {
            nameTextView.setText(user.getDisplayName());
            emailTextView.setText(user.getEmail());
            idTextView.setText(user.getUid());
            Glide.with(this).load(user.getPhotoUrl()).into(photoImageView);
        }



        @Override
        protected void onStart() {
            super.onStart();

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult> () {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }

        public void handleSignInResult(GoogleSignInResult result) {
            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();

                nameTextView.setText(account.getDisplayName());
                emailTextView.setText(account.getEmail());
                 idTextView.setText(account.getId());

                Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);

            } else {
                goLogInScreen();
            }
        }

        private void goLogInScreen() {
            Intent intentz = new Intent (this, LoginActivity.class);
            intentz.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentz);
        }



        public void revoke(View view) {


            Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        goLogInScreen();
                        Intent myIntenth = new Intent(LogGoog.this, LoginActivity.class);
                        startActivity(myIntenth);


                    } else {
                        Toast.makeText(getApplicationContext(), R.string.not_revoke, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        public void isityou(View view) {

            Intent myIntentg = new Intent(LogGoog.this, MainActivityLecturista.class);
            startActivity(myIntentg);
        }





}
