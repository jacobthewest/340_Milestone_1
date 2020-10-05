package edu.byu.cs.tweeter.view;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.InputStream;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.service.response.RegisterResponse;
import edu.byu.cs.tweeter.presenter.RegisterPresenter;
import edu.byu.cs.tweeter.view.asyncTasks.RegisterTask;
import edu.byu.cs.tweeter.view.main.MainActivity;
import edu.byu.cs.tweeter.view.util.ImageUtils;

import static android.app.Activity.RESULT_OK;

/**
 * The fragment that displays on the 'Register' tab.
 */
public class RegisterFragment extends Fragment implements RegisterPresenter.View, RegisterTask.Observer {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String LOG_TAG = "RegisterFragment";
    private static final String USER_KEY = "UserKey";
    private static final String AUTH_TOKEN_KEY = "AuthTokenKey";

    private User user;
    private String imageUrl;
    private AuthToken authToken;
    private RegisterPresenter presenter;
    private Toast registerToast;
    private ImageView imageToUpload;
    private byte [] imageBytes;


    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();

        Bundle args = new Bundle(0);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RegisterPresenter(this);

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button registerButton = view.findViewById(R.id.RegisterButton);
        Button uploadImageButton = view.findViewById(R.id.uploadImageButton);
        imageToUpload = (ImageView) view.findViewById(R.id.imageToUpload);
        EditText firstName = view.findViewById(R.id.firstName);
        EditText lastName = view.findViewById(R.id.lastName);
        EditText userNameRegister = view.findViewById(R.id.userNameRegister);
        EditText passwordRegister = view.findViewById(R.id.passwordRegister);

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
                if(isEmpty(userNameRegister) || isEmpty(passwordRegister) || isEmpty(firstName) || isEmpty(lastName) || imageBytes == null) {
                    registerToast = Toast.makeText(getActivity(), "First Name, Last Name, Username, Password, " +
                            "and Profile Pic must all be filled out" , Toast.LENGTH_LONG);
                    registerToast.show();
                } else if (!hasAtSymbol(userNameRegister)) {
                    registerToast = Toast.makeText(getActivity(), "Username must start with the @ symbol" , Toast.LENGTH_LONG);
                    registerToast.show();
                } else {
                    registerToast = Toast.makeText(getActivity(), "Registering User", Toast.LENGTH_LONG);
                    registerToast.show();

                    RegisterRequest registerRequest = getRegisterRequest(userNameRegister, passwordRegister, firstName, lastName);
                    RegisterTask registerTask = new RegisterTask(presenter, getObserver());

                    registerTask.execute(registerRequest);
                }
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Enables the user to select their profile picture
             *
             * @param view the view object that was clicked.
             */
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
            try {
                InputStream iStream = getContext().getContentResolver().openInputStream(selectedImage);
                this.imageBytes = ImageUtils.byteArrayFromUri(iStream);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getContext(), "Image upload failed, try a different image.", Toast.LENGTH_SHORT).show();
        }
    }

    private RegisterTask.Observer getObserver() {
        return this;
    }

    private RegisterRequest getRegisterRequest(EditText userName, EditText password, EditText first, EditText last) {
        String userNameString = editTextToString(userName);
        String passwordString = editTextToString(password);
        String firstNameString = editTextToString(first);
        String lastNameString = editTextToString(last);
        imageUrl = "https://i.imgur.com/VZQQiQ1.jpg";
        return new RegisterRequest(userNameString, passwordString, firstNameString, lastNameString, imageUrl, imageBytes);
    }

    /**
     * Checks to make sure the username is of the correct format
     * @param text The username
     * @return True if the format works, false if not.
     */
    private boolean hasAtSymbol(EditText text) {
        CharSequence str = text.getText().toString();
        char result = str.charAt(0);
        if (result == '@') {
            return true;
        }
        return false;
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private String editTextToString(EditText text) {
        return text.getText().toString();
    }

    /**
     * The callback method that gets invoked for a successful registration. Displays the MainActivity.
     *
     * @param registerResponse the response from the register request.
     */
    @Override
    public void registerSuccessful(RegisterResponse registerResponse) {
        Intent intent = new Intent(getActivity(), MainActivity.class);

        intent.putExtra(MainActivity.CURRENT_USER_KEY, registerResponse.getUser());
        intent.putExtra(MainActivity.AUTH_TOKEN_KEY, registerResponse.getAuthToken());

        registerToast.cancel();
        startActivity(intent);
    }

    /**
     * The callback method that gets invoked for an unsuccessful registration. Displays a toast with a
     * message indicating why the registration failed.
     *
     * @param registerResponse the response from the register request.
     */
    @Override
    public void registerUnsuccessful(RegisterResponse registerResponse) {
        Toast.makeText(getActivity(), "Failed to register. " + registerResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * A callback indicating that an exception was thrown in an asynchronous method called on the
     * presenter.
     *
     * @param ex the exception.
     */
    @Override
    public void handleException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
        Toast.makeText(getActivity(), "Failed to register because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
    }
}

