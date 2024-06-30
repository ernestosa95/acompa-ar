package acompanar.ButtonsDeclarationPersonal.General;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

import java.io.Serializable;
import java.util.HashMap;

public class BotonHpv22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public BotonHpv22(Context originalContext, View view, String title){
        context=originalContext;
        title_button = title;

        adminBData = new BDData(context, "BDData", null, 1);
        layout = (ConstraintLayout) view.findViewById(R.id.AVANCEGNRL);
        avanceTxt = (TextView) view.findViewById(R.id.COMPLETEGNRL);
        Log.e("heiht button", Integer.toString(view.getHeight()));
        mngArchivo = new Archivos(context);
        //Recupero los datos de opciones de registro de domicilio en general
        options = mngArchivo.getMapCategoriesPersonas();
        buttonViewBasic = new ButtonViewBasic(context);

        buttonViewBasic.buttonColor(context, title_button, layout, avanceTxt);
    }

    public void vista(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater Inflater = LayoutInflater.from(context);
        final View view = Inflater.inflate(R.layout.basic_alert, null);
        view.setFocusable(true);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        //Cabecera
        TextView txtCabecera = view.findViewById(R.id.TXTGralCabecera);
        txtCabecera.setText(title_button);

        //Crear el linerLayout que va a contener las diferentes categorias
        LinearLayout lyOptions = view.findViewById(R.id.LYGralOptions);

        View pregunta_edad = buttonViewBasic.generateRadioButtonYN("¿Tiene entre 30 a 64 años?");
        lyOptions.addView(pregunta_edad);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        guardar.setText("SIGUIENTE");

        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonViewBasic.getValueRadio(pregunta_edad).equals("SI")){
                    pregunta_edad.setVisibility(View.GONE);
                    //RE41_0: REALIZO TEST DE HPV
                    View RE41 = buttonViewBasic.generateRadioButtonYN(options.get("RE41_0"));
                    lyOptions.addView(RE41);
                    guardar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (buttonViewBasic.getValueRadio(RE41).equals("SI")) {
                                RE41.setVisibility(View.GONE);
                                View RE42 = buttonViewBasic.generateIntEdit(options.get("RE42_0"), context);
                                lyOptions.addView(RE42);
                                guardar.setText("GUARDAR");

                                guardar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!buttonViewBasic.getValueInt(RE42).equals("")) {
                                            data.put("RE41_0", buttonViewBasic.getValueRadio(RE41));
                                            data.put("RE42_0", buttonViewBasic.getValueInt(RE42));
                                            Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show();
                                            adminBData.updateCacheUD(data);
                                            buttonViewBasic.buttonColor(context, title_button, layout, avanceTxt);
                                            //ColorAvance(data);
                                            dialog.dismiss();
                                        }else{
                                            Toast.makeText(context, "Es obligatorio ingresar un codigo", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else{
                                data.put("RE41_0", "NO");
                                data.put("RE42_0", "NO");
                                adminBData.updateCacheUD(data);
                                buttonViewBasic.buttonColor(context, title_button, layout, avanceTxt);
                                //ColorAvance(data);
                                Toast.makeText(context, "La persona no se realiza el test", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                }else {
                    data.put("RE41_0", "NO");
                    data.put("RE42_0", "NO");
                    adminBData.updateCacheUD(data);
                    buttonViewBasic.buttonColor(context, title_button, layout, avanceTxt);
                    Toast.makeText(context, "La persona no esta comprendida en esta campaña", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        //Boton cancelar
        ImageButton cancelar = view.findViewById(R.id.CANCELARGrl);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
