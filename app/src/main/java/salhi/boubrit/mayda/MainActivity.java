package salhi.boubrit.mayda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button rest_btn, client_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rest_btn = findViewById(R.id.restaurant_btn);
        client_btn = findViewById(R.id.client_btn);

        rest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RestaurantLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        client_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RestaurantMapActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }


}
