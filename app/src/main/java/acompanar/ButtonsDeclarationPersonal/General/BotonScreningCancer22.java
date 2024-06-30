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

import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

import java.io.Serializable;
import java.util.HashMap;

public class BotonScreningCancer22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public BotonScreningCancer22(Context originalContext, View view, String title){
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

        //RE23:
        View RE23 = buttonViewBasic.generateRadioButtonYN(options.get("RE23_0"));
        lyOptions.addView(RE23);

        //RE24:
        View RE24 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("RE24_0"), buttonViewBasic.getSubOptions(options,"RE24"));
        lyOptions.addView(RE24);

        //RE23:
        View RE25 = buttonViewBasic.generateRadioButtonYN(options.get("RE25_0"));
        lyOptions.addView(RE25);

        //RE26:
        View RE26 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("RE26_0"), buttonViewBasic.getSubOptions(options,"RE26"));
        lyOptions.addView(RE26);

        //RE26:
        View RE27 = buttonViewBasic.generateSpinnerMultipleSelect(options.get("RE27_0"), buttonViewBasic.getSubOptions(options,"RE27"));
        lyOptions.addView(RE27);


        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.put("RE23_0", buttonViewBasic.getValueRadio(RE23));
                data.put("RE24_0", buttonViewBasic.getValueSpiner(RE24));
                data.put("RE25_0", buttonViewBasic.getValueRadio(RE25));
                data.put("RE26_0", buttonViewBasic.getValueSpiner(RE26));
                data.put("RE27_0", buttonViewBasic.getValueSpiner(RE27));

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
