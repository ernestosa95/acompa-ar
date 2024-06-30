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

public class BotonSeguimiento22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public BotonSeguimiento22(Context originalContext, View view, String title){
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

        View RE60 = buttonViewBasic.generateRadioButtonYN(options.get("RE60_0"));
        lyOptions.addView(RE60);

        View RE56 = buttonViewBasic.generateTextEdit(options.get("RE56_0"), context);
        lyOptions.addView(RE56);

        View RE57 = buttonViewBasic.generateTextEdit(options.get("RE57_0"), context);
        lyOptions.addView(RE57);

        View RE58 = buttonViewBasic.generateTextEdit(options.get("RE58_0"), context);
        lyOptions.addView(RE58);

        View RE59 = buttonViewBasic.generateTextEdit(options.get("RE59_0"), context);
        lyOptions.addView(RE59);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);

        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.put("RE56_0", buttonViewBasic.getValueText(RE56));
                data.put("RE57_0", buttonViewBasic.getValueText(RE57));
                data.put("RE58_0", buttonViewBasic.getValueText(RE58));
                data.put("RE59_0", buttonViewBasic.getValueText(RE59));
                data.put("RE60_0", buttonViewBasic.getValueRadio(RE60));

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
