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
import java.util.Objects;

public class BotonColon22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public BotonColon22(Context originalContext, View view, String title){
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

        View edad = buttonViewBasic.generateRadioButtonYN("¿Tiene entre 50 y 75 años?");
        lyOptions.addView(edad);

        View s1 = buttonViewBasic.generateDivisor("");
        lyOptions.addView(s1);

        View sintomas = buttonViewBasic.generateOptionsCheckbox(context, options.get("RE43_0"), buttonViewBasic.getSubOptions(options,"RE43"));
        lyOptions.addView(sintomas);

        View s2 = buttonViewBasic.generateDivisor("");
        lyOptions.addView(s2);

        View antecedentes = buttonViewBasic.generateRadioButtonYN(options.get("RE44_0"));
        lyOptions.addView(antecedentes);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        guardar.setText("SIGUIENTE");

        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonViewBasic.getValueRadio(edad).equals("SI")){
                    HashMap<String, String> values_sintomas = buttonViewBasic.getValueCheckBox(sintomas);
                    if (Objects.equals(values_sintomas.get("RE43_1"), "NO") && Objects.equals(values_sintomas.get("RE43_2"), "NO") &&
                            Objects.equals(values_sintomas.get("RE43_3"), "NO") && buttonViewBasic.getValueRadio(antecedentes).equals("NO")){
                            edad.setVisibility(View.GONE);
                            s1.setVisibility(View.GONE);
                            sintomas.setVisibility(View.GONE);
                            s2.setVisibility(View.GONE);
                            antecedentes.setVisibility(View.GONE);
                            View RE45 = buttonViewBasic.generateRadioButtonYN(options.get("RE45_0"));
                            lyOptions.addView(RE45);

                            guardar.setText("GUARDAR");
                            guardar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    data.put("RE45_0",buttonViewBasic.getValueRadio(RE45));
                                    adminBData.updateCacheUD(data);
                                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show();
                                    buttonViewBasic.buttonColor(context, title_button, layout, avanceTxt);
                                    //ColorAvance(data);
                                    dialog.dismiss();
                                }
                            });
                        }else {
                            data.put("RE45_0","NO");
                            data.put("RE44_0", buttonViewBasic.getValueRadio(antecedentes));
                            data.putAll(values_sintomas);
                            adminBData.updateCacheUD(data);
                            Toast.makeText(context, "La persona no cumple con los requisitos para este test", Toast.LENGTH_SHORT).show();
                            buttonViewBasic.buttonColor(context, title_button, layout, avanceTxt);
                            //ColorAvance(data);
                            dialog.dismiss();
                        }
                    }else {
                    Toast.makeText(context, "La persona no cumple con el requisito de edad para este test", Toast.LENGTH_SHORT).show();
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
