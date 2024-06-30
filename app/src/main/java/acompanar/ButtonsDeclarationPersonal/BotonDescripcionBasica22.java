package acompanar.ButtonsDeclarationPersonal;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.StorageManagement.EfectoresSearchAdapter;
import acompanar.ManagementModule.StorageManagement.TrabajosSearchAdapter;
import com.example.acompanar.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BotonDescripcionBasica22 implements Serializable {
    public Context context;
    public ConstraintLayout layout;
    public TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    public ButtonViewBasic buttonViewBasic;

    String title_button;

    public BotonDescripcionBasica22(Context originalContext, View view, String title){
        context=originalContext;
        title_button = title;

        adminBData = new BDData(context, "BDData", null, 1);
        layout = (ConstraintLayout) view.findViewById(R.id.AVANCEGEN);
        avanceTxt = (TextView) view.findViewById(R.id.COMPLETADOGEN);
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

        //RE22: EDUCACION
        View RE22 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("RE22_0"), buttonViewBasic.getSubOptions(options, "RE22"));
        lyOptions.addView(RE22);

        //RE19_0: EFECTOR DE SALUD AL QUE ASISTE
        View RE19 = buttonViewBasic.generateAutocompleteEdit(options.get("RE19_0"), context);
        //COMPORTAMIENTO DEL AUTOCOMPLETE
        List<String> efectores_auto = new ArrayList<String>();
        EfectoresSearchAdapter searchAdapter = new EfectoresSearchAdapter(context, efectores_auto);
        buttonViewBasic.getAutocomplete(RE19).setThreshold(1);
        buttonViewBasic.getAutocomplete(RE19).setAdapter(searchAdapter);
        lyOptions.addView(RE19);

        View division_ingreso_ocupacion = buttonViewBasic.generateDivisor("INGRESO Y OCUPACION");
        lyOptions.addView(division_ingreso_ocupacion);

        //RE21_1: ¿TIENE TRABAJO?
        View RE21_1 = buttonViewBasic.generateRadioButtonYN(options.get("RE21_1"));
        lyOptions.addView(RE21_1);

        //RE21_2: TRABAJO SEGUN CLASIFICACION INTERNACIONAL
        View RE21_2 = buttonViewBasic.generateAutocompleteEdit(options.get("RE21_2"), context);
        //COMPORTAMIENTO DEL AUTOCOMPLETE
        List<String> trabajo_auto = new ArrayList<String>();
        TrabajosSearchAdapter searchAdapter_trabajo = new TrabajosSearchAdapter(context, trabajo_auto);
        buttonViewBasic.getAutocomplete(RE21_2).setThreshold(1);
        buttonViewBasic.getAutocomplete(RE21_2).setAdapter(searchAdapter_trabajo);
        lyOptions.addView(RE21_2);

        //RE21_3: Plan social o jubilacion
        View RE21_3 = buttonViewBasic.generateTextEdit(options.get("RE21_3"), context);
        lyOptions.addView(RE21_3);

        View division_observaciones = buttonViewBasic.generateDivisor("OBSERVACIONES");
        lyOptions.addView(division_observaciones);

        //RE40
        View RE40 = buttonViewBasic.generateTextEdit(options.get("RE40_0"), context);
        lyOptions.addView(RE40);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.put("RE22_0", buttonViewBasic.getValueSpiner(RE22));
                data.put("RE19_0", buttonViewBasic.getValueAutocomplete(RE19));
                data.put("RE21_1", buttonViewBasic.getValueRadio(RE21_1));
                data.put("RE21_2", buttonViewBasic.getValueAutocomplete(RE21_2));
                data.put("RE21_3", buttonViewBasic.getValueText(RE21_3));
                data.put("RE40_0", buttonViewBasic.getValueText(RE40));

                adminBData.updateCacheUD(data);
                buttonViewBasic.buttonColor(context, title_button, layout, avanceTxt);
                dialog.dismiss();
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
