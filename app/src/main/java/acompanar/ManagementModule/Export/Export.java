package acompanar.ManagementModule.Export;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

public class Export extends AppCompatActivity {

    BDData adminBDData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        adminBDData = new BDData(getBaseContext(), "BDData", null, 1);

        Button btn = findViewById(R.id.BTNEXPORT);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getBaseContext(), adminBDData.getValues() + "valido", Toast.LENGTH_SHORT).show();
            }
        });
    }
}