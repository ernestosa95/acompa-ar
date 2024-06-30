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

public class BotonTrastornosMentales implements Serializable {
    PersonClass persona;
    Context context;
    ConstraintLayout layout_trastornosmentales;
    TextView avancetrastornosmentales;

    public BotonTrastornosMentales(Context originalContext, PersonClass originalPersona, View originalview){
        context=originalContext;
        persona=originalPersona;
        layout_trastornosmentales = (ConstraintLayout) originalview.findViewById(R.id.AVANCETRANSTORNOSMENTALES);
        avancetrastornosmentales = (TextView) originalview.findViewById(R.id.COMPLETADOTRANSTORNOSMENTALES);
    }

    public void vista(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater Inflater = LayoutInflater.from(context);
        View view1 = Inflater.inflate(R.layout.alert_trastornos_mentales, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final CheckBox organicos =  view1.findViewById(R.id.TRASTORNOORGANICO);
        final CheckBox psicoactivas = view1.findViewById(R.id.SUSTANCIAPSICOACTIVAS);
        final CheckBox esquizofrenia = view1.findViewById(R.id.ESQUISOFRENIA);
        final CheckBox humor =  view1.findViewById(R.id.HUMOR);
        final CheckBox estres = view1.findViewById(R.id.ESTRES);
        final CheckBox sindromes_comportamiento = view1.findViewById(R.id.SINDROMESCOMPORTAMIENTO);
        final CheckBox personalidad =  view1.findViewById(R.id.TRASTORNOSPERSONALIDAD);
        final CheckBox retraso_mental = view1.findViewById(R.id.RETRASOMENTAL);
        final CheckBox desarrollo_psicosocial = view1.findViewById(R.id.DESARROLLOPSICOLOGICO);
        final CheckBox emocionales =  view1.findViewById(R.id.EMOCIONALES);
        final CheckBox no_especificados = view1.findViewById(R.id.TRASTORNOSNOESPECIFICADO);
        final CheckBox lesiones_autoinflijidas = view1.findViewById(R.id.LESIONESAUTOINFLIJIDAS);

        String[] vac = persona.TrastornosMentales.split(",");
        for (int x = 0; x < vac.length; x++) {
            //Toast.makeText(getApplicationContext(), vac[0], Toast.LENGTH_SHORT).show();
            if (vac[x].equals(organicos.getText().toString())){
                organicos.setChecked(true);
            }
            if (vac[x].equals(psicoactivas.getText().toString())){
                psicoactivas.setChecked(true);
            }
            if (vac[x].equals(esquizofrenia.getText().toString())){
                esquizofrenia.setChecked(true);
            }
            if (vac[x].equals(humor.getText().toString())){
                humor.setChecked(true);
            }
            if (vac[x].equals(estres.getText().toString())){
                estres.setChecked(true);
            }
            if (vac[x].equals(sindromes_comportamiento.getText().toString())){
                sindromes_comportamiento.setChecked(true);
            }
            if (vac[x].equals(personalidad.getText().toString())){
                personalidad.setChecked(true);
            }
            if (vac[x].equals(retraso_mental.getText().toString())){
                retraso_mental.setChecked(true);
            }
            if (vac[x].equals(desarrollo_psicosocial.getText().toString())){
                desarrollo_psicosocial.setChecked(true);
            }
            if (vac[x].equals(emocionales.getText().toString())){
                emocionales.setChecked(true);
            }
            if (vac[x].equals(no_especificados.getText().toString())){
                no_especificados.setChecked(true);
            }
            if (vac[x].equals(lesiones_autoinflijidas.getText().toString())){
                lesiones_autoinflijidas.setChecked(true);
            }
        }

        final Button guardar = view1.findViewById(R.id.GUARDARTRANSTORNOSMENTALES);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trastornosMentales = null;
                if (organicos.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=organicos.getText().toString();
                    } else {
                        trastornosMentales+=","+organicos.getText().toString();}}
                if (psicoactivas.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=psicoactivas.getText().toString();
                    } else {
                        trastornosMentales+=","+psicoactivas.getText().toString();}}
                if (esquizofrenia.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=esquizofrenia.getText().toString();
                    } else {
                        trastornosMentales+=","+esquizofrenia.getText().toString();}}
                if (humor.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=humor.getText().toString();
                    } else {
                        trastornosMentales+=","+humor.getText().toString();}}
                if (estres.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=estres.getText().toString();
                    } else {
                        trastornosMentales+=","+estres.getText().toString();}}
                if (sindromes_comportamiento.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=sindromes_comportamiento.getText().toString();
                    } else {
                        trastornosMentales+=","+sindromes_comportamiento.getText().toString();}}
                if (personalidad.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=personalidad.getText().toString();
                    } else {
                        trastornosMentales+=","+personalidad.getText().toString();}}
                if (retraso_mental.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=retraso_mental.getText().toString();
                    } else {
                        trastornosMentales+=","+retraso_mental.getText().toString();}}
                if (desarrollo_psicosocial.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=desarrollo_psicosocial.getText().toString();
                    } else {
                        trastornosMentales+=","+desarrollo_psicosocial.getText().toString();}}
                if (emocionales.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=emocionales.getText().toString();
                    } else {
                        trastornosMentales+=","+emocionales.getText().toString();}}
                if (no_especificados.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=no_especificados.getText().toString();
                    } else {
                        trastornosMentales+=","+no_especificados.getText().toString();}}
                if (lesiones_autoinflijidas.isChecked()){
                    if (trastornosMentales==null){
                        trastornosMentales=lesiones_autoinflijidas.getText().toString();
                    } else {
                        trastornosMentales+=","+lesiones_autoinflijidas.getText().toString();}}


                if(trastornosMentales!=null){persona.TrastornosMentales = trastornosMentales;}
                else{persona.TrastornosMentales="";}


                ColorAvanceTrastornosMentales();
                dialog.dismiss();
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELARTRASTORNOSMENTALES);
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

    public void ColorAvanceTrastornosMentales() {
        // Cambio los colores de avance
        float avance = 0;
        if (persona.TrastornosMentales.length()!=0){
            avance+=1;
        }

        if(avance==0){
            layout_trastornosmentales.setBackgroundResource(R.drawable.rojo);
            avancetrastornosmentales.setText(context.getString(R.string.completado)+" 00%");
        }

        /*if(avance>0 && avance<2){
            layout_acompañamiento.setBackgroundResource(R.drawable.amarillo);
            double porcentaje = Math.round((avance/2)*100);
            String aux = getString(R.string.completado)+" "+ Double.toString(porcentaje)+"%";
            avancediscapacidad.setText(aux);
        }*/

        if(avance==1){
            layout_trastornosmentales.setBackgroundResource(R.drawable.verde);
            avancetrastornosmentales.setText(context.getString(R.string.completado)+" 100%");
        }
    }
}