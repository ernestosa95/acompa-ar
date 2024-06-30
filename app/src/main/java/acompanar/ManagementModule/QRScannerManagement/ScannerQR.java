package acompanar.ManagementModule.QRScannerManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import acompanar.MenuFamilia;
import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;
import com.google.zxing.Result;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerQR extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScanner;
    BDData adminBData;
    private EditText number_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_qr);

        mScanner = new ZXingScannerView(this);
        setContentView(mScanner);
        adminBData = new BDData(this, "BDData", null, 1);
        mScanner.setResultHandler(this);
        mScanner.startCamera();
    }

    public void SimpleCode(EditText code){
        number_code = code;
    }

    @Override
    public void handleResult(Result result) {
        //txt.setText(result.getText().toString());
        //mScanner.stopCamera();
        //mScanner.resumeCameraPreview(this);
        String escaneado = result.getText().toString();
        String[] datos = escaneado.split("@");
        Intent intent = new Intent(this, MenuFamilia.class);
        int tamaño = datos.length;
        if(tamaño == 9 || tamaño == 8){
        intent.putExtra("APELLIDO_ESCANEADO", datos[1]);
        intent.putExtra("NOMBRE_ESCANEADO", datos[2]);
        intent.putExtra("SEXO_ESCANEADO", datos[3]);
        intent.putExtra("DNI_ESCANEADO", datos[4]);
        intent.putExtra("FECHA_NACIMIENTO_ESCANEADO", datos[6]);
        intent.putExtra("ESCANEADO", escaneado);
        setResult(RESULT_OK, intent);
        finish();}
        else {
            if (tamaño == 17){
                intent.putExtra("APELLIDO_ESCANEADO", datos[4]);
                intent.putExtra("NOMBRE_ESCANEADO", datos[5]);
                intent.putExtra("SEXO_ESCANEADO", datos[8]);
                intent.putExtra("DNI_ESCANEADO", datos[1]);
                intent.putExtra("FECHA_NACIMIENTO_ESCANEADO", datos[7]);
                intent.putExtra("ESCANEADO", escaneado);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                if(tamaño == 1){

                    //number_code.setText(datos[0]);
                    HashMap<String, String> data = new HashMap<>();
                    data.put(getString(R.string.code_hpv), datos[0]);
                    adminBData.insertDataCache(data);
                    /*Intent qrIntent = new Intent(this, MenuPersona.class);
                    qrIntent.putExtra("CODEQR", datos[0]);
                    Toast.makeText(getBaseContext(), datos[0] + Integer.toString(tamaño), Toast.LENGTH_SHORT).show();
                    setResult(10, qrIntent);*/

                    finish();
                }else {
                    Toast.makeText(getBaseContext(), "EL QR NO CORRESPONDE A UN DNI O NO SE PUEDE LEER" + Integer.toString(tamaño), Toast.LENGTH_SHORT).show();
                    finish();
                }
                }
            }

    }
}
