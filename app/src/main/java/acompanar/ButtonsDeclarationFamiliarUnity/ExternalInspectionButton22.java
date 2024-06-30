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
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

import java.io.Serializable;
import java.util.HashMap;

public class ExternalInspectionButton22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public ExternalInspectionButton22(Context originalContext, View view, String title){
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
        View REDO14 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO14_0"), buttonViewBasic.getSubOptions(options, "REDO14"));
        lyOptions.addView(REDO14);

        //REDO2: LA VIVIENDA ES
        View REDO15 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO15_0"), buttonViewBasic.getSubOptions(options,"REDO15"));
        lyOptions.addView(REDO15);

        //REDO3: EL BAÑO TIENE
        View REDO20 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO20_0"), buttonViewBasic.getSubOptions(options,"REDO20"));
        lyOptions.addView(REDO20);

        //REDO3: EL BAÑO TIENE
        View REDO21 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO21_0"), buttonViewBasic.getSubOptions(options,"REDO21"));
        lyOptions.addView(REDO21);

        //REDO7: REVESTIMIENTO INTERIOR DEL TECHO O CIELORRAZO
        View REDO16 = buttonViewBasic.generateRadioButtonYN(options.get("REDO16_0"));
        lyOptions.addView(REDO16);

        View REDO18 = buttonViewBasic.generateRadioButtonYN(options.get("REDO18_0"));
        lyOptions.addView(REDO18);

        View REDO17 = buttonViewBasic.generateIntEdit(options.get("REDO17_0"), view.getContext());
        lyOptions.addView(REDO17);

        View REDO19 = buttonViewBasic.generateIntEdit(options.get("REDO19_0"), view.getContext());
        lyOptions.addView(REDO19);



        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data.put("REDO14_0", buttonViewBasic.getValueSpiner(REDO14));
                data.put("REDO15_0", buttonViewBasic.getValueSpiner(REDO15));
                data.put("REDO16_0", buttonViewBasic.getValueRadio(REDO16));
                data.put("REDO17_0", buttonViewBasic.getValueInt(REDO17));
                data.put("REDO18_0", buttonViewBasic.getValueRadio(REDO18));
                data.put("REDO19_0", buttonViewBasic.getValueInt(REDO19));
                data.put("REDO20_0", buttonViewBasic.getValueSpiner(REDO20));
                data.put("REDO21_0", buttonViewBasic.getValueSpiner(REDO21));

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
