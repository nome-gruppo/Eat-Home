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

import nomeGruppo.eathome.PlaceRegistrationActivity;
import nomeGruppo.eathome.R;

public class LoginFragment extends Fragment {

    private LoginViewModel loginLW;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        loginLW = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        final ConstraintLayout layout = (ConstraintLayout) root.findViewById(R.id.fragment_activity);

        final EditText usernameET = root.findViewById(R.id.fragment_login_et_username);
        final EditText passwordET = root.findViewById(R.id.fragment_login_et_password);
        final Button loginBtn = root.findViewById(R.id.fragment_login_btn_login);
        final TextView signInTW = root.findViewById(R.id.fragment_login_tw_signIn);
        final TextView signInPlaceTW = root.findViewById(R.id.fragment_login_tw_signInPlace);
        final ProgressBar loadingPB = root.findViewById(R.id.fragment_login_pb_loading);


        loginLW.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginBtn.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameET.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordET.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginLW.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingPB.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                getActivity().setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                getActivity().finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginLW.loginDataChanged(usernameET.getText().toString(),
                        passwordET.getText().toString());
            }
        };
        usernameET.addTextChangedListener(afterTextChangedListener);
        passwordET.addTextChangedListener(afterTextChangedListener);
        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginLW.login(usernameET.getText().toString(),
                            passwordET.getText().toString());
                }
                return false;
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                loginLW.login(usernameET.getText().toString(),
                        passwordET.getText().toString());
            }
        });

        return root;
    }


    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getActivity().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getActivity().getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
