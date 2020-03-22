package nomeGruppo.eathome.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.PlaceHomepageActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.db.FirebaseConnection;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private LoginViewModel loginLW;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Intent homepageIntent;
    private boolean signedIn = false;
    private Object userData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        loginLW = ViewModelProviders.of(this, new LoginViewModelFactory())
//                .get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        final ConstraintLayout layout = (ConstraintLayout) root.findViewById(R.id.fragment_activity);

        final EditText emailET = root.findViewById(R.id.fragment_login_et_email);
        final EditText passwordET = root.findViewById(R.id.fragment_login_et_password);
        final Button loginBtn = root.findViewById(R.id.fragment_login_btn_login);
        final TextView signInTW = root.findViewById(R.id.fragment_login_tw_signIn);
        final TextView signInPlaceTW = root.findViewById(R.id.fragment_login_tw_signInPlace);
//        final ProgressBar loadingPB = root.findViewById(R.id.fragment_login_pb_loading);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailTemp = emailET.getText().toString().trim();
                String passwordTemp = passwordET.getText().toString().trim();

                signIn(emailTemp, passwordTemp);
                if(signedIn){
                    searchInDb();
                    startActivity(homepageIntent);
                }
            }
        });

//        loginLW.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
//            @Override
//            public void onChanged(@Nullable LoginFormState loginFormState) {
//                if (loginFormState == null) {
//                    return;
//                }
//                loginBtn.setEnabled(loginFormState.isDataValid());
//                if (loginFormState.getUsernameError() != null) {
//                    emailET.setError(getString(loginFormState.getUsernameError()));
//                }
//                if (loginFormState.getPasswordError() != null) {
//                    passwordET.setError(getString(loginFormState.getPasswordError()));
//                }
//            }
//        });
//
//        loginLW.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
//            @Override
//            public void onChanged(@Nullable LoginResult loginResult) {
//                if (loginResult == null) {
//                    return;
//                }
//                loadingPB.setVisibility(View.GONE);
//                if (loginResult.getError() != null) {
//                    showLoginFailed(loginResult.getError());
//                }
//                if (loginResult.getSuccess() != null) {
//                    updateUiWithUser(loginResult.getSuccess());
//                }
//                getActivity().setResult(Activity.RESULT_OK);
//
//                //Complete and destroy login activity once successful
//                getActivity().finish();
//            }
//        });

//        TextWatcher afterTextChangedListener = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // ignore
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                loginLW.loginDataChanged(emailET.getText().toString(),
//                        passwordET.getText().toString());
//            }
//        };
//        emailET.addTextChangedListener(afterTextChangedListener);
//        passwordET.addTextChangedListener(afterTextChangedListener);
//        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginLW.login(emailET.getText().toString(),
//                            passwordET.getText().toString());
//                }
//                return false;
//            }
//        });

//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadingPB.setVisibility(View.VISIBLE);
//                loginLW.login(emailET.getText().toString(),
//                        passwordET.getText().toString());
//            }
//        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        user = mAuth.getCurrentUser();
        if(user == null){
            signedIn = false;
        }else{
            signedIn = true;
        }
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            signedIn = true;

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            signedIn = false;
                        }

                        // ...
                    }
                });
    }

    /*metodo che cerca nel l'utente nel database recuperando i dati
     */
    private void searchInDb(){
        FirebaseConnection firebaseConnection = new FirebaseConnection();

        //controlla tra gli utenti
        if(firebaseConnection.searchUser(FirebaseConnection.CLIENT_TABLE, user.getUid())){
            this.userData = firebaseConnection.getObjectFounded();
            this.homepageIntent = new Intent();
        }else{ //cerca tra i ristoratori
            this.userData = firebaseConnection.getObjectFounded();
            this.homepageIntent = new Intent(getActivity(), PlaceHomepageActivity.class);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getCurrentUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
           // Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
    }

//    private void updateUI(FirebaseUser user) {
//
//        hideProgressBar();
//
//        if (user != null) {
//
//            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
//
//                    user.getEmail(), user.isEmailVerified()));
//
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
//
//            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
//
//            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
//
//
//
//            if (user.isEmailVerified()) {
//
//                findViewById(R.id.verifyEmailButton).setVisibility(View.GONE);
//
//            } else {
//
//                findViewById(R.id.verifyEmailButton).setVisibility(View.VISIBLE);
//
//            }
//
//        } else {
//
//            mStatusTextView.setText(R.string.signed_out);
//
//            mDetailTextView.setText(null);
//
//
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
//
//            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
//
//            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
//
//        }
//
//    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getActivity().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getActivity().getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
