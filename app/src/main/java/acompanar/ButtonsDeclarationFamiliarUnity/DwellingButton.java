package acompanar.ButtonsDeclarationFamiliarUnity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.BasicObjets.FamiliarUnityClass;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import com.example.acompanar.R;

import java.io.Serializable;
import java.util.HashMap;

public class DwellingButton implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public DwellingButton(Context originalContext, View view, String title){
        context=originalContext;
        title_button = title;

        adminBData = new BDData(context, "BDData", null, 1);
        layout = (ConstraintLayout) view.findViewById(R.id.AVANCEGNRL);
        avanceTxt = (TextView) view.findViewById(R.id.COMPLETEGNRL);
        mngArchivo = new Archivos(context);
        //Recupero los datos de opciones de registro de domicilio en general
        options = mngArchivo.getMapCategoriesDomicilio();
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

        //REDO1: CANTIDAD DE AMBIENTES
        View REDO1 = buttonViewBasic.generateIntEdit(options.get("REDO1_0"), view.getContext());
        lyOptions.addView(REDO1);

        //REDO2: LA VIVIENDA ES
        View REDO2 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO2_0"), buttonViewBasic.getSubOptions(options,"REDO2"));
        lyOptions.addView(REDO2);

        //REDO3: EL BAÑO TIENE
        View REDO3 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO3_0"), buttonViewBasic.getSubOptions(options,"REDO3"));
        lyOptions.addView(REDO3);

        //REDO4: LUGAR PARA COCINAR
        View REDO4 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO4_0"), buttonViewBasic.getSubOptions(options,"REDO4"));
        lyOptions.addView(REDO4);

        //REDO5: USA PARA COCINAR
        View REDO5 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO5_0"), buttonViewBasic.getSubOptions(options,"REDO5"));
        lyOptions.addView(REDO5);

        //REDO6: MATERIAL DE LOS PISOS
        View REDO6 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO6_0"), buttonViewBasic.getSubOptions(options,"REDO6"));
        lyOptions.addView(REDO6);

        //REDO7: REVESTIMIENTO INTERIOR DEL TECHO O CIELORRAZO
        View REDO7 = buttonViewBasic.generateRadioButtonYN(options.get("REDO7_0"));
        lyOptions.addView(REDO7);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data.put("REDO1_0", buttonViewBasic.getValueInt(REDO1));
                data.put("REDO2_0", buttonViewBasic.getValueSpiner(REDO2));
                data.put("REDO3_0", buttonViewBasic.getValueSpiner(REDO3));
                data.put("REDO4_0", buttonViewBasic.getValueSpiner(REDO4));
                data.put("REDO5_0", buttonViewBasic.getValueSpiner(REDO5));
                data.put("REDO6_0", buttonViewBasic.getValueSpiner(REDO6));
                data.put("REDO7_0", buttonViewBasic.getValueRadio(REDO7));

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
