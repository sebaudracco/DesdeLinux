package sebaudracco.proyectocoop;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static com.firebase.ui.auth.AuthUI.TAG;


public  class LoginWG {
    private static GoogleSignInClient mGoogleSignInClient;

    public static GoogleSignInClient getGoogleSignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken (context.getString (R.string.default_web_client_id))
                .requestEmail ()
                .build ();
        return mGoogleSignInClient = GoogleSignIn.getClient (context, gso);
    }

    public  static GoogleSignInAccount getGoogleSignInAccount(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent (data);
        GoogleSignInAccount account = null;
        try {
            // Google Sign In was successful, authenticate with Firebase
            account = task.getResult (ApiException.class);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
              Log.w(TAG, "Google sign in failed", e);
        }
        return account;
    }
}




