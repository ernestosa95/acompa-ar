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

public class BotonEnfermedadesCronicas22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public BotonEnfermedadesCronicas22(Context originalContext, View view, String title){
        context = originalContext;
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

        //RE18:
        View RE18 = buttonViewBasic.generateOptionsCheckbox(context, options.get("RE18_0"), buttonViewBasic.getSubOptions(options, "RE18"));
        lyOptions.addView(RE18);

        //RE38:
        View RE38 = buttonViewBasic.generateOptionsCheckbox(context, options.get("RE38_0"), buttonViewBasic.getSubOptions(options, "RE38"));
        lyOptions.addView(RE38);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.putAll(buttonViewBasic.getValueCheckBox(RE18));
                data.putAll(buttonViewBasic.getValueCheckBox(RE38));
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
