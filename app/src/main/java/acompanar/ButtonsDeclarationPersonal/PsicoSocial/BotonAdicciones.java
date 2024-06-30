package acompanar.ButtonsDeclarationPersonal.PsicoSocial;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

import java.io.Serializable;

public class BotonAdicciones implements Serializable {
    PersonClass persona;
    Context context;
    ConstraintLayout layout_adicciones;
    TextView avanceadicciones;

    public BotonAdicciones(Context originalContext, PersonClass originalPersona, View originalview){
        context=originalContext;
        persona=originalPersona;
        layout_adicciones = (ConstraintLayout) originalview.findViewById(R.id.AVANCEADICCIONES);
        avanceadicciones = (TextView) originalview.findViewById(R.id.COMPLETADOADICCIONES);
    }

    public void vista(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater Inflater = LayoutInflater.from(context);
        View view1 = Inflater.inflate(R.layout.alert_adicciones, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final CheckBox alcohol =  view1.findViewById(R.id.ALCOHOL);
        final CheckBox drogas = view1.findViewById(R.id.DROGAS);
        final CheckBox tabaco = view1.findViewById(R.id.TABACO);

        String[] vac = persona.Adicciones.split(",");
        for (int x = 0; x < vac.length; x++) {
            if (vac[x].equals(alcohol.getText().toString())){
                alcohol.setChecked(true);
            }
            if (vac[x].equals(drogas.getText().toString())){
                drogas.setChecked(true);
            }
            if (vac[x].equals(tabaco.getText().toString())){
                tabaco.setChecked(true);
            }
        }

        final Button guardar = view1.findViewById(R.id.GUARDARADICCIONES);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adicciones = null;
                if (alcohol.isChecked()){
                    if (adicciones==null){
                        adicciones=alcohol.getText().toString();
                    } else {
                        adicciones+=","+alcohol.getText().toString();}}
                if (drogas.isChecked()){
                    if (adicciones==null){
                        adicciones=drogas.getText().toString();
                    } else {
                        adicciones+=","+drogas.getText().toString();}}
                if (tabaco.isChecked()){
                    if (adicciones==null){
                        adicciones=tabaco.getText().toString();
                    } else {
                        adicciones+=","+tabaco.getText().toString();}}

                if(adicciones!=null){persona.Adicciones = adicciones;}
                else{persona.Adicciones="";}

                ColorAvanceAdicciones();
                dialog.dismiss();
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELARADICCIONES);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public PersonClass returnPersona(){
        return persona;
    }

    public void ColorAvanceAdicciones() {
        // Cambio los colores de avance
        float avance = 0;
        if (persona.Adicciones.length()!=0){
            avance+=1;
        }

        if(avance==0){
            layout_adicciones.setBackgroundResource(R.drawable.rojo);
            avanceadicciones.setText(context.getString(R.string.completado)+" 00%");
        }

        /*if(avance>0 && avance<2){
            layout_acompañamiento.setBackgroundResource(R.drawable.amarillo);
            double porcentaje = Math.round((avance/2)*100);
            String aux = getString(R.string.completado)+" "+ Double.toString(porcentaje)+"%";
            avancediscapacidad.setText(aux);
        }*/

        if(avance==1){
            layout_adicciones.setBackgroundResource(R.drawable.verde);
            avanceadicciones.setText(context.getString(R.string.completado)+" 100%");
        }
    }
}