package acompanar.ButtonsDeclarationPersonal;

import android.app.AlertDialog;
import android.content.Context;
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
import java.util.HashMap;

public class BotonContactoBasico22 implements Serializable {
    public Context context;
    public ConstraintLayout layout;
    public TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    public ButtonViewBasic buttonViewBasic;

    HashMap<String, String> data;

    String title_button;

    public BotonContactoBasico22(Context originalContext, View view, String title){
        context=originalContext;
        title_button = title;

        adminBData = new BDData(context, "BDData", null, 1);
        layout = (ConstraintLayout) view.findViewById(R.id.AVANCECON);
        avanceTxt = (TextView) view.findViewById(R.id.COMPLETADOCON);
        //Log.e("heiht button", Integer.toString(view.getHeight()));
        mngArchivo = new Archivos(context);
        //Recupero los datos de opciones de registro de domicilio en general
        options = mngArchivo.getMapCategoriesPersonas();
        buttonViewBasic = new ButtonViewBasic(context);
        data = new HashMap<>();

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

        //RE20_1: celular
        View RE20_1 = buttonViewBasic.generateIntEdit(options.get("RE20_1"), context);
        lyOptions.addView(RE20_1);

        //RE20_2: fijo
        View RE20_2 = buttonViewBasic.generateIntEdit(options.get("RE20_2"), context);
        lyOptions.addView(RE20_2);//CONTACTO

        //RE20_3: mail
        View RE20_3 = buttonViewBasic.generateTextEdit(options.get("RE20_3"), context);
        lyOptions.addView(RE20_3);

        View division_persona_contacto = buttonViewBasic.generateDivisor("PERSONA DE CONTACTO");
        lyOptions.addView(division_persona_contacto);

        //RE20_2: tel persona de contacto
        View RE20_5 = buttonViewBasic.generateIntEdit(options.get("RE20_5"), context);
        lyOptions.addView(RE20_5);//CONTACTO

        //RE20_3: nombre y apellido persona de contacto
        View RE20_4 = buttonViewBasic.generateTextEdit(options.get("RE20_4"), context);
        lyOptions.addView(RE20_4);

        //RE20_5: parentezco persona de contacto
        View RE20_6 = buttonViewBasic.generateTextEdit(options.get("RE20_6"), context);
        lyOptions.addView(RE20_6);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.put("RE20_1", buttonViewBasic.getValueInt(RE20_1));
                data.put("RE20_2", buttonViewBasic.getValueInt(RE20_2));
                data.put("RE20_3", buttonViewBasic.getValueText(RE20_3));
                data.put("RE20_5", buttonViewBasic.getValueInt(RE20_5));
                data.put("RE20_4", buttonViewBasic.getValueText(RE20_4));
                data.put("RE20_6", buttonViewBasic.getValueText(RE20_6));

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
