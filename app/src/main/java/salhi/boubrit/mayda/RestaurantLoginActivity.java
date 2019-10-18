package salhi.boubrit.mayda;

//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class RestaurantLoginActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_restaurant_login);
//    }
//}
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RestaurantLoginActivity extends AppCompatActivity {

    private EditText email_restaurant_et,password_restaurant_et;
    private Button login_btn,register_btn,forget_btn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_login);



        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!= null){
                    Intent intent = new Intent(RestaurantLoginActivity.this, AcceuilRestaurantActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        email_restaurant_et = findViewById(R.id.email_et);
        password_restaurant_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_restaurant_et.getText().toString();
                final String password = password_restaurant_et.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(RestaurantLoginActivity.this, "sign in failed Username/password is incorrect", Toast.LENGTH_SHORT).show();
                                }else {
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    String user_id = FirebaseAuth.getInstance().getUid();
                                    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                    mDatabaseReference.child("users").child("restaurants").child(user_id);
                                    //mDatabaseReference.setValue(true);
                                }
                            }
                        });
            }
        });


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_restaurant_et.getText().toString();
                final String password = password_restaurant_et.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RestaurantLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(RestaurantLoginActivity.this, "sing up fail", Toast.LENGTH_SHORT).show();
                                }else {
                                    //String user_id = FirebaseAuth.getInstance().getUid();
                                    //DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                    //mDatabaseReference.child("users").child("restaurants").child(user_id);
                                    // mDatabaseReference.setValue(true);
                                }
                            }

                        });
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();

//        // Check if user is signed in (non-null) and update UI accordingly.
        if (email_restaurant_et != null){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            mAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().signOut();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}