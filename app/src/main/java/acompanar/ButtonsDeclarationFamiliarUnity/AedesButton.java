package acompanar.ButtonsDeclarationFamiliarUnity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import acompanar.MenuFamilia;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.BasicObjets.FamiliarUnityClass;
import com.example.acompanar.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class AedesButton extends AppCompatActivity {

    FamiliarUnityClass datosDengue;
    FamiliarUnityClass familia;
    EditText destruidos;
    Archivos mangerArchivos;
    HashMap <String, String> mapCabeceras = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_aedes);

        // Seteo el titulo de la action bar del activity
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.aedes_aegipty);

        datosDengue = new FamiliarUnityClass(this);
        familia = new FamiliarUnityClass(this);

        //------------------------------------------------------------------------------------------
        mangerArchivos = new Archivos(getBaseContext());

        mapCabeceras = mangerArchivos.getMapCategoriesAedes();


        if(getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                datosDengue = (FamiliarUnityClass) bundle.getSerializable("DATOSDENGUE");
                familia = (FamiliarUnityClass) bundle.getSerializable("DATOSDENGUE");
        }}

        // Tipo de trabajo -------------------------------------------------------------------------
        LinearLayout lyTipoTrabajo = (LinearLayout) findViewById(R.id.LINEARTIPOTRABAJOAEDES);

        CreateCheckOptions(lyTipoTrabajo, "REAE1");
        //------------------------------------------------------------------------------------------
        //Situacion de la casa ---------------------------------------------------------------------
        LinearLayout lySituacionCasa = (LinearLayout) findViewById(R.id.LINEARSITUACIONCASA);

        CreateCheckOptions(lySituacionCasa, "REAE2");
        //------------------------------------------------------------------------------------------
        //Larvicidas -------------------------------------------------------------------------------
        LinearLayout lyLarvicida = (LinearLayout) findViewById(R.id.LINEARLARVICIDA);

        CreateCheckOptions(lyLarvicida, "REAE3");
        //------------------------------------------------------------------------------------------
        // Destruidos ------------------------------------------------------------------------------
        destruidos = findViewById(R.id.EDTDESTRUIDOS);

        if (familia.dataAedes.get("REAE4") != null){
            destruidos.setText(familia.dataAedes.get("REAE4"));
        }

        //------------------------------------------------------------------------------------------
        // Tipos y cantidades de recipientes tratados ----------------------------------------------
        LinearLayout lyTiposRecipientes = (LinearLayout) findViewById(R.id.LINEARRECIPIENTE);

        for (int i=5; i<14; i++) {
            TextView childTipoRecipiente = new TextView(getBaseContext());
            childTipoRecipiente.setText(mapCabeceras.get("REAE"+i+"_1").split("_")[0]);
            if (familia.dataAedes.get("REAE"+i+"_1") != null || familia.dataAedes.get("REAE"+i+"_2") != null || familia.dataAedes.get("REAE"+i+"_3") != null) {
                childTipoRecipiente.setBackgroundResource(R.drawable.cuadrado_verde);
            }else{
                childTipoRecipiente.setBackgroundResource(R.drawable.cuadrado_celeste);
            }
            childTipoRecipiente.setTextSize(24);
            childTipoRecipiente.setPadding(20, 30, 20, 30);
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(10, 10, 10, 0);
            childTipoRecipiente.setLayoutParams(buttonLayoutParams);
            childTipoRecipiente.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            childTipoRecipiente.setTextColor(getResources().getColor(R.color.colorPar));
            final int[] index = {i};
            childTipoRecipiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreateOptionsCantidadTratados(childTipoRecipiente, "REAE"+ index[0]);
                }
            });
            lyTiposRecipientes.addView(childTipoRecipiente);
        }
        //------------------------------------------------------------------------------------------

    }

    public void Guardar(View view){

        // Tipo trabajo ----------------------------------------------------------------------------
        LinearLayout lyTipoTrabajo = (LinearLayout) findViewById(R.id.LINEARTIPOTRABAJOAEDES);

        SaveCheckSelects(lyTipoTrabajo, "REAE1");
        //------------------------------------------------------------------------------------------
        // Situacion de la casa --------------------------------------------------------------------
        LinearLayout lySituacionCasa = (LinearLayout) findViewById(R.id.LINEARSITUACIONCASA);

        SaveCheckSelects(lySituacionCasa, "REAE2");
        //------------------------------------------------------------------------------------------
        // Larvicida -------------------------------------------------------------------------------
        LinearLayout lyLarvicida = (LinearLayout) findViewById(R.id.LINEARLARVICIDA);

        SaveCheckSelects(lyLarvicida, "REAE3");
        //------------------------------------------------------------------------------------------
        // Destruidos ------------------------------------------------------------------------------
        if(destruidos.getText().toString().length()!=0) {
            familia.dataAedes.put("REAE4", destruidos.getText().toString());
        }
        else {familia.dataAedes.put("REAE4",null);}
        //------------------------------------------------------------------------------------------

        if(familia.TipoTrabajo.length()!=0) {
            Intent Modif1 = new Intent(this, MenuFamilia.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("DENGUE", familia);
            Modif1.putExtras(bundle);
            setResult(3, Modif1);
            finish();
        }
        else {
            Toast.makeText(this, "FALTA CARGAR EL TIPO DE TRABAJO", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        // Defino los contenedores
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
        TextView textView = new TextView(this);
        textView.setText("RelevAr");
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(22F);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        // Defino el Layaout que va a contener a los Check
        final LinearLayout mainLayout       = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        // Defino los parametros
        int TamañoLetra =18;

        // Telefono Celular
        LinearLayout layout0       = new LinearLayout(this);
        layout0.setOrientation(LinearLayout.HORIZONTAL);
        layout0.setVerticalGravity(Gravity.CENTER_VERTICAL);
        final TextView descripcion = new TextView(getApplicationContext());
        descripcion.setText("Salir y eliminar registro");
        descripcion.setGravity(Gravity.CENTER_HORIZONTAL);
        descripcion.setTextSize(TamañoLetra);
        descripcion.setTextColor(Color.WHITE);
        descripcion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout0.setMinimumHeight(100);
        layout0.addView(descripcion);

        mainLayout.addView(layout0);

        // Add OK and Cancel buttons
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user clicked OK
                //if(Persona.DNI!=""){
                //GuardarPersona();}
                finish();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //finish();
            }
        });

        builder.setView(mainLayout);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ArrayList<String> opcCabecera(String codeCabecera){
        ArrayList<String> values = new ArrayList<>();

        Set<String> keys = mapCabeceras.keySet();
        for (String key : keys){
            if(Objects.equals(key.split("_")[0], codeCabecera)){
                values.add(mapCabeceras.get(key).split("_")[1]);
            }
        }

        return values;
    }

    private void CreateCheckOptions(LinearLayout parent, String cabParent){
        ArrayList<String> opcTipoTrabajo = opcCabecera(cabParent);

        for (String opc : opcTipoTrabajo){
            CheckBox aux = new CheckBox(getBaseContext());
            aux.setText(opc);
            aux.setTextSize(18);
            parent.addView(aux);
        }

        ArrayList <View> childTipoTrabajo = parent.getTouchables();

        for (View child : childTipoTrabajo){
            CheckBox aux = (CheckBox) child;
            if (familia.dataAedes.get(mapCabeceras.get(mapCabeceras.get(cabParent+"_1").split("_")[0]+"_"+aux.getText().toString())) != null){
                aux.setChecked(true);
                Log.e("text", mapCabeceras.get(mapCabeceras.get(cabParent+"_1").split("_")[0]+"_"+aux.getText().toString()));
            }

        }
    }

    private void SaveCheckSelects(LinearLayout parent, String cabparent){
        ArrayList <View> childTipoTrabajo = parent.getTouchables();

        for (View child : childTipoTrabajo){
            CheckBox check = (CheckBox) child;
            String key_save = mapCabeceras.get(mapCabeceras.get(cabparent+"_1").split("_")[0]+"_"+check.getText().toString());
            if (check.isChecked()){
                familia.dataAedes.put(key_save, "s");
                familia.TipoTrabajo = "si";
            }else{
                familia.dataAedes.put(key_save, null);
            }
        }
    }

    private void CreateOptionsCantidadTratados(View viewParent, String codeParent){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.alert_opciones_dengue, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final TextView cabecera = view1.findViewById(R.id.TXTOPCDENGUE);
        cabecera.setText(mapCabeceras.get(codeParent+"_1").split("_")[0]);

        Set<String> keys = mapCabeceras.keySet();
        ArrayList<String> codeOptions = new ArrayList<>();
        for (String key : keys){
            if(key.split("_")[0].equals(codeParent)){
                codeOptions.add(key);
            }
        }

        LinearLayout lyCantidadRecipientes = (LinearLayout) view1.findViewById(R.id.LINEARCANTIDADRECIPIENTE);
        for(String option : codeOptions){
            EditText editText = new EditText(getBaseContext());
            editText.setHint(mapCabeceras.get(option).split("_")[1]);
            editText.setBackgroundResource(R.drawable.cuadrado_celeste);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setPadding(20, 30, 20, 30);
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(10, 10, 10, 0);
            editText.setLayoutParams(buttonLayoutParams);
            editText.setTextSize(20);
            lyCantidadRecipientes.addView(editText);
        }

        ArrayList<View> options = lyCantidadRecipientes.getTouchables();
        for(View child : options){
            EditText values = (EditText) child;
            String cadenapadre = mapCabeceras.get(codeParent+"_1").split("_")[0];
            String cadenaopcion = mapCabeceras.get(cadenapadre+"_"+values.getHint().toString());
            if(familia.dataAedes.get(cadenaopcion)!=null && cadenaopcion!=null){
                values.setText(familia.dataAedes.get(cadenaopcion));
            }
        }

        final Button guardar = view1.findViewById(R.id.GUARDAROPCIONESDENGUE);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                ArrayList<View> options = lyCantidadRecipientes.getTouchables();
                for(View view:options){
                    EditText values = (EditText) view;
                    if (values.getText().length()!=0){
                        familia.dataAedes.put(mapCabeceras.get(mapCabeceras.get(codeParent+"_1").split("_")[0] +"_"+values.getHint().toString()),values.getText().toString());
                        Log.e("cantidades", values.getText().toString());
                        viewParent.setBackgroundResource(R.drawable.cuadrado_verde);
                    }
                    else {
                        familia.dataAedes.put(mapCabeceras.get(mapCabeceras.get(codeParent+"_1").split("_")[0] +"_"+values.getHint().toString()), null);
                    }
                }
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELAROPCIONESDENGUE);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}