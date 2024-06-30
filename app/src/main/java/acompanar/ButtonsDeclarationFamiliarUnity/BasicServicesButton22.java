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

public class BasicServicesButton22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public BasicServicesButton22(Context originalContext, View view, String title){
        context=originalContext;
        title_button = title;

        adminBData = new BDData(context, "BDData", null, 1);
        layout = (ConstraintLayout) view.findViewById(R.id.AVANCEGNRL);
        avanceTxt = (TextView) view.findViewById(R.id.COMPLETEGNRL);
        mngArchivo = new Archivos(context);
        //Recupero los datos de opciones de registro de domicilio en general
        options = mngArchivo.getMapCategoriesDomicilio();
        buttonViewBasic = new ButtonViewBasic(context);

        buttonViewBasic.buttonColor(context, title, layout, avanceTxt);
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
        View REDO8 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO8_0"), buttonViewBasic.getSubOptions(options, "REDO8"));
        lyOptions.addView(REDO8);

        //REDO2: LA VIVIENDA ES
        View REDO9 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO9_0"), buttonViewBasic.getSubOptions(options,"REDO9"));
        lyOptions.addView(REDO9);

        //REDO3: EL BAÑO TIENE
        View REDO11 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO11_0"), buttonViewBasic.getSubOptions(options,"REDO11"));
        lyOptions.addView(REDO11);

        //REDO3: EL BAÑO TIENE
        View REDO13 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("REDO13_0"), buttonViewBasic.getSubOptions(options,"REDO13"));
        lyOptions.addView(REDO13);

        //REDO7: REVESTIMIENTO INTERIOR DEL TECHO O CIELORRAZO
        View REDO10 = buttonViewBasic.generateRadioButtonYN(options.get("REDO10_0"));
        lyOptions.addView(REDO10);

        View REDO12 = buttonViewBasic.generateRadioButtonYN(options.get("REDO12_0"));
        lyOptions.addView(REDO12);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data.put("REDO8_0", buttonViewBasic.getValueSpiner(REDO8));
                data.put("REDO9_0", buttonViewBasic.getValueSpiner(REDO9));
                data.put("REDO10_0", buttonViewBasic.getValueRadio(REDO10));
                data.put("REDO11_0", buttonViewBasic.getValueSpiner(REDO11));
                data.put("REDO12_0", buttonViewBasic.getValueRadio(REDO12));
                data.put("REDO13_0", buttonViewBasic.getValueSpiner(REDO13));

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
