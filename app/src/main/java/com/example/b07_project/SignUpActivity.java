package com.example.b07_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

    }

    public void updateUserProfile(String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println(user.getDisplayName());
                        }
                    }
                });
    }


    public void signUp(View view){
        EditText last_name = (EditText) findViewById(R.id.LastName_EditText);
        EditText name = (EditText) findViewById(R.id.Name_EditText);
        EditText email = (EditText) findViewById(R.id.Email_SignUp_EditText);
        EditText password = (EditText) findViewById(R.id.Password_SignUp_EditText);

        // User data should be complete here, now validating
        if(!(validateData(view, last_name.getText().toString(),
                name.getText().toString(), email.getText().toString(), password.getText().toString()))){return;}

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                System.out.println("SIGN IN WAS SUCCESSFUL  :((((");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUserProfile(name.getText().toString() + " " + last_name.getText().toString());
                                //user.updateProfile(new UserProfileChangeRequest.Builder);

                                User newUser = new User();
                                newUser.id = user.getUid();
                                newUser.auth = 0;
                                newUser.firstName = name.getText().toString();
                                newUser.lastName = last_name.getText().toString();
                                newUser.email = email.getText().toString();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Users");

                                myRef.child(newUser.id).setValue(newUser);


                                Intent main = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(main);
                                finish();
                            } else {
                                //fail
                                System.out.println("SIGN IN FAILED   :((((");
                                TextView loginFail = (TextView) findViewById(R.id.Sign_Up_Error_Message);
                                loginFail.setText(task.getException().getMessage());
                            }
                        }
                    });
    }

/*    private void makePopUp(View view, String message){
        // inflate layout of popup window
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        // popup window creation
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(view, width, height, focusable);
        //showing popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            popupWindow.setElevation(20);
        }
        //Create textview for popup
        TextView textView = (TextView) popupView.findViewById(R.id.popup_text);
        textView.setText(message);
        // dismiss message when clicked
        popupView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });
    }*/

    private boolean validateData(View view, String lastName, String name, String email, String password){
        //Check if user has names
        if(name.trim().isEmpty() || lastName.trim().isEmpty()){
            //makePopUp(view, "Please enter a first name or last name.");
            Snackbar mySnackbar = Snackbar.make(view, "Please enter a first name or last name", BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.show();
            return false;
        }
        //Check if user has inputted an email
        if(email.trim().isEmpty()){
            //makePopUp(view, "Please enter an email");
            Snackbar mySnackbar = Snackbar.make(view, "Please enter an email", BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.show();
            return false;
        }
        //Check if user has inputted a password
        if(password.trim().isEmpty()){
            //makePopUp(view, "Please enter a password");
            Snackbar mySnackbar = Snackbar.make(view, "Please enter a password", BaseTransientBottomBar.LENGTH_SHORT);
            mySnackbar.show();
            return false;
        }
        return true;
    }
}