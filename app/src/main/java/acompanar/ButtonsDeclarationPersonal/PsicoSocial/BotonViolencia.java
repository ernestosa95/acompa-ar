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

public class BotonViolencia implements Serializable {
    PersonClass persona;
    Context context;
    ConstraintLayout layout_violencia;
    TextView avanceviolencia;

    public BotonViolencia(Context originalContext, PersonClass originalPersona, View originalview){
        context=originalContext;
        persona=originalPersona;
        layout_violencia = (ConstraintLayout) originalview.findViewById(R.id.AVANCEVIOLENCIA);
        avanceviolencia = (TextView) originalview.findViewById(R.id.COMPLETADOVIOLENCIA);
    }

    public void vista(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater Inflater = LayoutInflater.from(context);
        View view1 = Inflater.inflate(R.layout.alert_violencia, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final CheckBox fisica =  view1.findViewById(R.id.FISICA);
        final CheckBox psicologica = view1.findViewById(R.id.PSICOLOGICA);
        final CheckBox economica = view1.findViewById(R.id.ECONOMICA);
        final CheckBox sexual = view1.findViewById(R.id.SEXUAL);
        final CheckBox simbolica = view1.findViewById(R.id.SIMBOLICA);

        String[] vac = persona.TipoViolencia.split(",");
        for (int x = 0; x < vac.length; x++) {
            if (vac[x].equals(fisica.getText().toString())){
                fisica.setChecked(true);
            }
            if (vac[x].equals(psicologica.getText().toString())){
                psicologica.setChecked(true);
            }
            if (vac[x].equals(economica.getText().toString())){
                economica.setChecked(true);
            }
            if (vac[x].equals(sexual.getText().toString())){
                sexual.setChecked(true);
            }
            if (vac[x].equals(simbolica.getText().toString())){
                simbolica.setChecked(true);
            }
        }

        final CheckBox domestica =  view1.findViewById(R.id.DOMESTICA);
        final CheckBox laboral = view1.findViewById(R.id.LABORAL);
        final CheckBox institucional = view1.findViewById(R.id.INSTITUCIONAL);
        final CheckBox reproductiva = view1.findViewById(R.id.REPRODUCTIVA);
        final CheckBox obstetrica = view1.findViewById(R.id.OBSTETRICA);
        final CheckBox mediatica = view1.findViewById(R.id.MEDIATICA);
        vac = persona.ModalidadViolencia.split(",");
        for (int x = 0; x < vac.length; x++) {
            if (vac[x].equals(domestica.getText().toString())){
                domestica.setChecked(true);
            }
            if (vac[x].equals(laboral.getText().toString())){
                laboral.setChecked(true);
            }
            if (vac[x].equals(institucional.getText().toString())){
                institucional.setChecked(true);
            }
            if (vac[x].equals(reproductiva.getText().toString())){
                reproductiva.setChecked(true);
            }
            if (vac[x].equals(obstetrica.getText().toString())){
                obstetrica.setChecked(true);
            }
            if (vac[x].equals(mediatica.getText().toString())){
                mediatica.setChecked(true);
            }
        }

        final Button guardar = view1.findViewById(R.id.GUARDARVIOLENCIA);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tipoViolencia = null;
                if (fisica.isChecked()){
                    if (tipoViolencia==null){
                        tipoViolencia=fisica.getText().toString();
                    } else {
                        tipoViolencia+=","+fisica.getText().toString();}}
                if (psicologica.isChecked()){
                    if (tipoViolencia==null){
                        tipoViolencia=psicologica.getText().toString();
                    } else {
                        tipoViolencia+=","+psicologica.getText().toString();}}
                if (economica.isChecked()){
                    if (tipoViolencia==null){
                        tipoViolencia=economica.getText().toString();
                    } else {
                        tipoViolencia+=","+economica.getText().toString();}}
                if (sexual.isChecked()){
                    if (tipoViolencia==null){
                        tipoViolencia=sexual.getText().toString();
                    } else {
                        tipoViolencia+=","+sexual.getText().toString();}}
                if (simbolica.isChecked()){
                    if (tipoViolencia==null){
                        tipoViolencia=simbolica.getText().toString();
                    } else {
                        tipoViolencia+=","+simbolica.getText().toString();}}
                if(tipoViolencia!=null){persona.TipoViolencia = tipoViolencia;}
                else {persona.TipoViolencia="";}

                String modalidadViolencia = null;
                if (domestica.isChecked()){
                    if (modalidadViolencia==null){
                        modalidadViolencia=domestica.getText().toString();
                    } else {
                        modalidadViolencia+=","+domestica.getText().toString();}}
                if (laboral.isChecked()){
                    if (modalidadViolencia==null){
                        modalidadViolencia=laboral.getText().toString();
                    } else {
                        modalidadViolencia+=","+laboral.getText().toString();}}
                if (institucional.isChecked()){
                    if (modalidadViolencia==null){
                        modalidadViolencia=institucional.getText().toString();
                    } else {
                        modalidadViolencia+=","+institucional.getText().toString();}}
                if (reproductiva.isChecked()){
                    if (modalidadViolencia==null){
                        modalidadViolencia=reproductiva.getText().toString();
                    } else {
                        modalidadViolencia+=","+reproductiva.getText().toString();}}
                if (obstetrica.isChecked()){
                    if (modalidadViolencia==null){
                        modalidadViolencia=obstetrica.getText().toString();
                    } else {
                        modalidadViolencia+=","+obstetrica.getText().toString();}}
                if (mediatica.isChecked()){
                    if (modalidadViolencia==null){
                        modalidadViolencia=mediatica.getText().toString();
                    } else {
                        modalidadViolencia+=","+mediatica.getText().toString();}}
                if(modalidadViolencia!=null){persona.ModalidadViolencia = modalidadViolencia;}
                else{persona.ModalidadViolencia="";}
                ColorAvanceViolencia();
                dialog.dismiss();
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELARVIOLENCIA);
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

    public void ColorAvanceViolencia() {
        // Cambio los colores de avance
        float avance = 0;
        if (persona.TipoViolencia.length()!=0){
            avance+=1;
        }

        if (persona.ModalidadViolencia.length()!=0){
            avance+=1;
        }

        if(avance==0){
            layout_violencia.setBackgroundResource(R.drawable.rojo);
            avanceviolencia.setText(context.getString(R.string.completado)+" 00%");
        }

        if(avance>0 && avance<2){
            layout_violencia.setBackgroundResource(R.drawable.amarillo);
            double porcentaje = Math.round((avance/2)*100);
            String aux = context.getString(R.string.completado)+" "+ Double.toString(porcentaje)+"%";
            avanceviolencia.setText(aux);
        }

        if(avance==2){
            layout_violencia.setBackgroundResource(R.drawable.verde);
            avanceviolencia.setText(context.getString(R.string.completado)+" 100%");
        }
    }
}