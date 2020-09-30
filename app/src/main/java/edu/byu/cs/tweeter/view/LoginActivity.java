package edu.byu.cs.tweeter.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;
import edu.byu.cs.tweeter.presenter.LoginPresenter;
import edu.byu.cs.tweeter.view.asyncTasks.LoginTask;
import edu.byu.cs.tweeter.view.main.MainActivity;

/**
 * Contains the minimum UI required to allow the user to login with a hard-coded user. Most or all
 * of this should be replaced when the back-end is implemented.
 */
public class LoginActivity extends AppCompatActivity implements LoginPresenter.View, LoginTask.Observer {

    private static final String LOG_TAG = "LoginActivity";

    private LoginPresenter presenter;
    private Toast loginInToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenter(this);

        Button loginButton = findViewById(R.id.LoginButton);
        Button registerButton = findViewById(R.id.RegisterButton);
        EditText firstName = findViewById(R.id.firstName);
        EditText lastName = findViewById(R.id.lastName);
        EditText userNameLogin = findViewById(R.id.userNameLogin);
        EditText passwordLogin = findViewById(R.id.passwordLogin);
        EditText userNameRegister = findViewById(R.id.userNameRegister);
        EditText passwordRegister = findViewById(R.id.passwordRegister);


        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Makes a login request. The user is hard-coded, so it doesn't matter what data we put
             * in the LoginRequest object.
             *
             * @param view the view object that was clicked.
             */
            @Override
            public void onClick(View view) {
                String toastText = "";
                loginInToast = null;
                if(isEmpty(userNameLogin) || isEmpty(passwordLogin)) {
                    loginInToast = Toast.makeText(LoginActivity.this, "Username and password must be filled in" , Toast.LENGTH_LONG);
                    loginInToast.show();
                } else {
                    loginInToast = Toast.makeText(LoginActivity.this, "Logging In", Toast.LENGTH_LONG);
                    loginInToast.show();
                    // It doesn't matter what values we put here. We will be logged in with a hard-coded dummy user.
                    LoginRequest loginRequest = new LoginRequest("dummyUserName", "dummyPassword");
                    LoginTask loginTask = new LoginTask(presenter, LoginActivity.this);
                    loginTask.execute(loginRequest);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Makes a Register request. The user is hard-coded, so it doesn't matter what data we put
             * in the RegisterRequest object.
             *
             * @param view the view object that was clicked.
             */
            @Override
            public void onClick(View view) {
                String toastText = "";
                Toast registerToast = null;
                if(isEmpty(userNameRegister) || isEmpty(passwordRegister) || isEmpty(firstName) || isEmpty(lastName)) {
                    registerToast = Toast.makeText(RegisterActivity.this, "First Name, Last Name, Username, and Password values can't be empty" , Toast.LENGTH_LONG);
                    registerToast.show();
                } else {
                    registerToast = Toast.makeText(RegisterActivity.this, "Registering User", Toast.LENGTH_LONG);
                    registerToast.show();
                    // It doesn't matter what values we put here. We will be logged in with a hard-coded dummy user.
                    RegisterRequest registerRequest = new RegisterRequest("dummyUserName", "dummyPassword");
                    RegisterTask registerTask = new RegisterTask(presenter, LoginActivity.this);
                    registerTask.execute(loginRequest);
                }
            }
        });
    }



    /**
     * The callback method that gets invoked for a successful login. Displays the MainActivity.
     *
     * @param loginResponse the response from the login request.
     */
    @Override
    public void loginSuccessful(LoginResponse loginResponse) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(MainActivity.CURRENT_USER_KEY, loginResponse.getUser());
        intent.putExtra(MainActivity.AUTH_TOKEN_KEY, loginResponse.getAuthToken());

        loginInToast.cancel();
        startActivity(intent);
    }

    /**
     * The callback method that gets invoked for an unsuccessful login. Displays a toast with a
     * message indicating why the login failed.
     *
     * @param loginResponse the response from the login request.
     */
    @Override
    public void loginUnsuccessful(LoginResponse loginResponse) {
        Toast.makeText(this, "Failed to login. " + loginResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * A callback indicating that an exception was thrown in an asynchronous method called on the
     * presenter.
     *
     * @param exception the exception.
     */
    @Override
    public void handleException(Exception exception) {
        Log.e(LOG_TAG, exception.getMessage(), exception);
        Toast.makeText(this, "Failed to login because of exception: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}
