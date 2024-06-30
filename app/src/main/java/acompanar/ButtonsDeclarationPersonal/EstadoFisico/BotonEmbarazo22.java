package acompanar.ButtonsDeclarationPersonal.EstadoFisico;

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

public class BotonEmbarazo22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public BotonEmbarazo22(Context originalContext, View view, String title){
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

        //RE15_1
        View RE15_1 = buttonViewBasic.generateDateEdit(options.get("RE15_1"), context);
        lyOptions.addView(RE15_1);

        //RE15_2
        View RE15_2 = buttonViewBasic.generateDateEdit(options.get("RE15_2"), context);
        lyOptions.addView(RE15_2);

        //RE15_3
        View RE15_3 = buttonViewBasic.generateTextEdit(options.get("RE15_3"), context);
        lyOptions.addView(RE15_3);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.put("RE15_1", buttonViewBasic.getValueDate(RE15_1));
                data.put("RE15_2", buttonViewBasic.getValueDate(RE15_2));
                data.put("RE15_3", buttonViewBasic.getValueText(RE15_3));

                adminBData.updateCacheUD(data);
                ColorAvance(data);
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

    // NO EDITAR
    public void ColorAvance(HashMap<String,String> data) {
        Object[] objs = data.keySet().toArray();
        int total = 0;
        int llenados = 0;
        for (Object obj : objs){
            if (options.containsKey(obj.toString())){
                total++;
                if (data.get(obj.toString())!=null) {
                    if (!data.get(obj.toString()).equals("")) {
                        llenados++;
                    }
                }
            }
        }

        double porcentaje = 0.0;
        if (llenados != 0) {
            porcentaje = Math.round(((double) llenados / total) * 100);
            Log.e(porcentaje+"porc", llenados+"lle "+total+"tot");
        }


        if (porcentaje == 0){
            Log.e("porc", porcentaje+"%rojo");
            layout.setBackgroundResource(R.drawable.rojo);
            avanceTxt.setText(context.getString(R.string.completado) + " " + Double.toString(porcentaje) + "%");
        }else {
            if (porcentaje == 100){
                layout.setBackgroundResource(R.drawable.verde);
                avanceTxt.setText(context.getString(R.string.completado) + " " + Double.toString(porcentaje) + "%");
                Log.e("porc", porcentaje+"%verde");
            }else{
                layout.setBackgroundResource(R.drawable.amarillo);
                String aux = context.getString(R.string.completado) + " " + Double.toString(porcentaje) + "%";
                avanceTxt.setText(aux);
                Log.e("porc", porcentaje+"%amarillo");
            }
        }
    }
}
