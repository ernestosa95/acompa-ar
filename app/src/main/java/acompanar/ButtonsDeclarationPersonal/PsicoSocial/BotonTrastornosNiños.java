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

public class BotonTrastornosNiños implements Serializable {
    PersonClass persona;
    Context context;
    ConstraintLayout layout_trastornosniños;
    TextView avancetrastornosniños;

    public BotonTrastornosNiños(Context originalContext, PersonClass originalPersona, View originalview){
        context=originalContext;
        persona=originalPersona;
        layout_trastornosniños = (ConstraintLayout) originalview.findViewById(R.id.AVANCETRANSTORNOSENNIÑOS);
        avancetrastornosniños = (TextView) originalview.findViewById(R.id.COMPLETADOTRANSTORNOSENNIÑOS);
    }

    public void vista(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater Inflater = LayoutInflater.from(context);
        View view1 = Inflater.inflate(R.layout.alert_trastornos_ninos, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final CheckBox deficitAtencion =  view1.findViewById(R.id.DEFICITATENCION);
        final CheckBox trastornosConducta = view1.findViewById(R.id.TRASTORNOSCONDUCTA);

        String[] vac = persona.TrastornoNiños.split(",");
        for (int x = 0; x < vac.length; x++) {
            if (vac[x].equals(deficitAtencion.getText().toString())){
                deficitAtencion.setChecked(true);
            }
            if (vac[x].equals(trastornosConducta.getText().toString())){
                trastornosConducta.setChecked(true);
            }
        }

        final Button guardar = view1.findViewById(R.id.GUARDARTRANSTORNOSNIÑOS);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trastornosNiños = null;
                if (deficitAtencion.isChecked()){
                    if (trastornosNiños==null){
                        trastornosNiños=deficitAtencion.getText().toString();
                    } else {
                        trastornosNiños+=","+deficitAtencion.getText().toString();}}
                if (trastornosConducta.isChecked()){
                    if (trastornosNiños==null){
                        trastornosNiños=trastornosConducta.getText().toString();
                    } else {
                        trastornosNiños+=","+trastornosConducta.getText().toString();}}

                if(trastornosNiños!=null){persona.TrastornoNiños = trastornosNiños;}
                else{persona.TrastornoNiños = "";}
                ColorAvanceTrastornosNiños();
                dialog.dismiss();
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELARTRASTORNOSNIÑOS);
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

    public void ColorAvanceTrastornosNiños() {
        // Cambio los colores de avance
        float avance = 0;
        if (persona.TrastornoNiños.length()!=0){
            avance+=1;
        }

        if(avance==0){
            layout_trastornosniños.setBackgroundResource(R.drawable.rojo);
            avancetrastornosniños.setText(context.getString(R.string.completado)+" 00%");
        }

        /*if(avance>0 && avance<2){
            layout_acompañamiento.setBackgroundResource(R.drawable.amarillo);
            double porcentaje = Math.round((avance/2)*100);
            String aux = getString(R.string.completado)+" "+ Double.toString(porcentaje)+"%";
            avancediscapacidad.setText(aux);
        }*/

        if(avance==1){
            layout_trastornosniños.setBackgroundResource(R.drawable.verde);
            avancetrastornosniños.setText(context.getString(R.string.completado)+" 100%");
        }
    }
}