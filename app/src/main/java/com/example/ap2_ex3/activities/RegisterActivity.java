package com.example.ap2_ex3.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ap2_ex3.AppDatabase;
import com.example.ap2_ex3.R;
import com.example.ap2_ex3.api.UserApi;
import com.example.ap2_ex3.daos.UserDao;
import com.example.ap2_ex3.adapters.db_entities.AppSettings;
import com.example.ap2_ex3.adapters.db_entities.User;
import com.example.ap2_ex3.interfaces.AppCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText displayNameField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button uploadImage;
    private String base64Pic = "";
    private Button registerButton;
    private AppDatabase database;
    private UserDao userDao;
    private Button helpButton;
    private FrameLayout helpFrame;
    private TextView registerInstructions;
    private UserApi userApi;
    private CompletableFuture<User> userFuture;

    @SuppressLint("WrongThread")
    protected void onCreate(Bundle savedInstanceState) {
        AppSettings settings = MainActivity.database.getAppSettingsDao().get(1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button loginButton = findViewById(R.id.back_to_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the RegisterActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Initialize database and UserDao
        database = AppDatabase.getInstance(getApplicationContext());
        userDao = database.getUserDao();

        // Initialize views
        usernameField = findViewById(R.id.username_field);
        displayNameField = findViewById(R.id.displayName_field);
        passwordField = findViewById(R.id.password_field);
        confirmPasswordField = findViewById(R.id.confirmPassword_field);
        registerButton = findViewById(R.id.register_button);
        uploadImage = findViewById(R.id.upload_image_button);
        helpButton = findViewById(R.id.help_button);
        helpFrame = findViewById(R.id.help_frame);
        registerInstructions = findViewById(R.id.register_instructions);

        Drawable drawable = getResources().getDrawable(R.drawable.default_pic);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        base64Pic = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        userApi = new UserApi();
        userFuture = new CompletableFuture<>();

        addListeners();
    }

    private void registerUser() {
        String username = usernameField.getText().toString().trim();
        String displayName = displayNameField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (username.isEmpty() || displayName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validatePassword(password)) {
            Toast.makeText(this, "Password should be 8-16 chars and have a letter, number, special !@#$%.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!validateConfirmPassword(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateUsername(username)) {
            Toast.makeText(this, "Username must be between 3-16 characters and contain no special characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateDisplayName(displayName)) {
            Toast.makeText(this, "Display name must be between 3-12 characters and contain no special characters", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(username, displayName, password, "data:image/png;base64," + base64Pic);

        userApi.addUser(user, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.context, "User added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 409){
                    Toast.makeText(MainActivity.context, "Username taken!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.context, "Server response error", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Server response error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {
        this.helpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        showMessage("Username and Display name must be between 3-16 characters and contain no special characters\n" +
                                "Password must be 8-16 chars and have a letter, number, special character:\n!@#$%.");
                        return true;
                    case MotionEvent.ACTION_UP:
                        hideMessage();
                        return true;
                }
                return false;
            }
        });
        this.passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword(s.toString());
            }
        });

        this.confirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword(s.toString());
            }
        });

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
            }
        });
    }

    private void showMessage(String message) {
        registerInstructions.setText(message);
        helpFrame.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        helpFrame.setVisibility(View.INVISIBLE);
    }

    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$");
        Matcher matcher = pattern.matcher(password);
        if (TextUtils.isEmpty(password)) {
            // Clear border for empty password field
            passwordField.setBackgroundResource(R.drawable.rounded_text_box);
            return false;
        } else if (!matcher.matches()) {
            // Set red border color for invalid password field
            passwordField.setBackgroundResource(R.drawable.invalid_border);
            return false;
        } else {
            // Set green border color for valid password field
            passwordField.setBackgroundResource(R.drawable.valid_border);
            return true;
        }
    }

    private boolean validateConfirmPassword(String confirmPassword) {
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            // Clear border for empty confirm password field
            confirmPasswordField.setBackgroundResource(R.drawable.rounded_text_box);
            return false;
        } else if (!confirmPassword.equals(password)) {
            // Set red border color for mismatched confirm password field
            confirmPasswordField.setBackgroundResource(R.drawable.invalid_border);
            return false;
        } else {
            // Set green border color for matched confirm password field
            confirmPasswordField.setBackgroundResource(R.drawable.valid_border);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            base64Pic = getBase64FromImageUri(selectedImageUri, this);

            if (base64Pic != null) {
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String getBase64FromImageUri(Uri imageUri, Context context) {
        byte[] imageBytes = getStreamByteFromImage(imageUri, context);
        if (imageBytes != null) {
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return null;
    }

    public static byte[] getStreamByteFromImage(final Uri imageUri, final Context context) {
        Bitmap photoBitmap = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                photoBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (photoBitmap != null) {
            int imageRotation = getImageRotation(imageUri, context);
            if (imageRotation != 0)
                photoBitmap = getBitmapRotatedByDegree(photoBitmap, imageRotation);

            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        }

        return stream.toByteArray();
    }

    private static int getImageRotation(final Uri imageUri, final Context context) {
        ExifInterface exif = null;
        int exifRotation = 0;

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                exif = new ExifInterface(inputStream);
                exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif == null)
            return 0;
        else
            return exifToDegrees(exifRotation);
    }

    private static Bitmap getBitmapRotatedByDegree(Bitmap bitmap, int rotationDegree) {
        Matrix matrix = new Matrix();
        matrix.preRotate(rotationDegree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static int exifToDegrees(int rotation) {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;

        return 0;
    }

    private boolean validateUsername(String username) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{3,16}$");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private boolean validateDisplayName(String username) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{3,12}$");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

}
