package acompanar.ButtonsDeclarationPersonal.EstadoFisico;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

import java.io.Serializable;

public class BotonFactoresRiesgo implements Serializable {
    PersonClass persona;
    Context context;
    ConstraintLayout factores;
    TextView avancefactores;

    CheckBox calendario, embarazo, puerperio, personalsalud, personalesencial, viajeros,
            inmunocomprometidos, cadiologicos, respiratorios, diabeticos, prematuros, asplenicos,
            obesidad, inmunodeficiencia, conviviente,HTA, oncologicos, mayor60, otros;

    public BotonFactoresRiesgo(Context originalContext, PersonClass originalPersona, View originalview){
        context=originalContext;
        persona=originalPersona;
        factores = (ConstraintLayout) originalview.findViewById(R.id.AVANCEFACTORES);
        avancefactores = (TextView) originalview.findViewById(R.id.COMPLETADOFACTORES);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void vista(){
        // Defino las caracteristicas
        int TamañoLetra = 20;
        String ColorPares = "#4A4A4A";
        String ColorImpares = "#4588BC";
        int AltoContenedor = 80;

        // Defino los contenedores
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MiEstiloAlert);
        TextView textView = new TextView(context);
        textView.setText("FACTORES DE RIESGO");
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(22F);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        // Defino el Layaout que va a contener a los Check
        LinearLayout mainLayout       = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        calendario = PersonalCheck(mainLayout,"POR CALENDARIO", TamañoLetra, ColorPares, AltoContenedor);
        embarazo = PersonalCheck(mainLayout,"EMBARAZO", TamañoLetra, ColorImpares, AltoContenedor);
        puerperio = PersonalCheck(mainLayout,"PUERPERIO", TamañoLetra, ColorPares, AltoContenedor);
        personalsalud = PersonalCheck(mainLayout,"PERSONAL DE SALUD", TamañoLetra, ColorImpares, AltoContenedor);
        personalesencial = PersonalCheck(mainLayout,"PERSONAL ESENCIAL", TamañoLetra, ColorPares, AltoContenedor);
        viajeros = PersonalCheck(mainLayout,"VIAJEROS", TamañoLetra, ColorImpares, AltoContenedor);
        inmunocomprometidos = PersonalCheck(mainLayout,"INMUNOCOMPROMETIDOS", TamañoLetra, ColorPares, AltoContenedor);
        cadiologicos = PersonalCheck(mainLayout,"CARDIOLOGICOS", TamañoLetra, ColorImpares, AltoContenedor);
        respiratorios = PersonalCheck(mainLayout,"RESPIRATORIOS", TamañoLetra, ColorPares, AltoContenedor);
        diabeticos = PersonalCheck(mainLayout,"DIABÉTICOS", TamañoLetra, ColorImpares, AltoContenedor);
        prematuros = PersonalCheck(mainLayout,"PREMATUROS", TamañoLetra, ColorPares, AltoContenedor);
        asplenicos = PersonalCheck(mainLayout,"ASPLÉNICOS", TamañoLetra, ColorImpares, AltoContenedor);
        obesidad = PersonalCheck(mainLayout,"OBESIDAD MORBIDA", TamañoLetra, ColorPares, AltoContenedor);
        inmunodeficiencia = PersonalCheck(mainLayout,"INMUNODEFICIENCIA", TamañoLetra, ColorImpares, AltoContenedor);
        conviviente = PersonalCheck(mainLayout,"CONVIVIENTE INMUNOCOMPROMETIDOS", TamañoLetra, ColorPares, AltoContenedor);
        HTA = PersonalCheck(mainLayout,"HIPERTENSO", TamañoLetra, ColorImpares, AltoContenedor);
        oncologicos = PersonalCheck(mainLayout,"ONCOLOGICOS", TamañoLetra, ColorPares, AltoContenedor);
        mayor60 = PersonalCheck(mainLayout,"MAYOR DE 60", TamañoLetra, ColorImpares, AltoContenedor);
        otros = PersonalCheck(mainLayout,"OTROS", TamañoLetra, ColorPares, AltoContenedor);

        // En el caso de editar un registro
        // Necesito que me ponga seleccionado todos aquellos que ya se habian seleeccionado antes
        // para que los pueda editar

        //Necesito activar los check que ya habia seleccionado antes
        CheckSeleccionadosFactores(persona.FactoresDeRiesgo);

        // Add OK and Cancel buttons
        builder.setPositiveButton("LISTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The user clicked OK
                String riesgos=null;
                String codigoriesgo=null;
                if (calendario.isChecked()){
                    if (riesgos==null){
                        riesgos="POR CALENDARIO";
                        codigoriesgo="1";
                    } else {
                        riesgos+=",POR CALENDARIO";
                        codigoriesgo+=",1";}}
                if (embarazo.isChecked()){
                    if (riesgos==null){
                        riesgos="EMBARAZO";
                        codigoriesgo="2";
                    } else {
                        riesgos+=",EMBARAZO";
                        codigoriesgo+=",2";}}
                if (puerperio.isChecked()){
                    if (riesgos==null){
                        riesgos="PUERPERIO";
                        codigoriesgo="3";
                    } else {
                        riesgos+=",PUERPERIO";
                        codigoriesgo+=",3";}}
                if (personalsalud.isChecked()){
                    if (riesgos==null){
                        riesgos="PERSONAL DE SALUD";
                        codigoriesgo="4";
                    } else {
                        riesgos+=",PERSONAL DE SALUD";
                        codigoriesgo+=",4";}}
                if (personalesencial.isChecked()){
                    if (riesgos==null){
                        riesgos="PERSONAL ESENCIAL";
                        codigoriesgo="5";
                    } else {
                        riesgos+=",PERSONAL ESENCIAL";
                        codigoriesgo+=",5";}}
                if (viajeros.isChecked()){
                    if (riesgos==null){
                        riesgos="VIAJEROS";
                        codigoriesgo="6";
                    } else {
                        riesgos+=",VIAJEROS";
                        codigoriesgo+=",6";}}
                if (inmunocomprometidos.isChecked()){
                    if (riesgos==null){
                        riesgos="INMUNOCOMPROMETIDOS";
                        codigoriesgo="7";
                    } else {
                        riesgos+=",INMUNOCOMPROMETIDOS";
                        codigoriesgo+=",7";}}
                if (cadiologicos.isChecked()){
                    if (riesgos==null){
                        riesgos="CARDIOLOGICOS";
                        codigoriesgo="8";
                    } else {
                        riesgos+=",CARDIOLOGICOS";
                        codigoriesgo+=",8";}}
                if (respiratorios.isChecked()){
                    if (riesgos==null){
                        riesgos="RESPIRATORIOS";
                        codigoriesgo="9";
                    } else {
                        riesgos+=",RESPIRATORIOS";
                        codigoriesgo+=",9";}}
                if (diabeticos.isChecked()){
                    if (riesgos==null){
                        riesgos="DIABETICOS";
                        codigoriesgo="10";
                    } else {
                        riesgos+=",DIABETICOS";
                        codigoriesgo+=",10";}}
                if (prematuros.isChecked()){
                    if (riesgos==null){
                        riesgos="PREMATURO";
                        codigoriesgo="11";
                    } else {
                        riesgos+=",PREMATURO";
                        codigoriesgo+=",11";}}
                if (asplenicos.isChecked()){
                    if (riesgos==null){
                        riesgos="ASPLENICOS";
                        codigoriesgo="12";
                    } else {
                        riesgos+=",ASPLENICOS";
                        codigoriesgo+=",12";}}
                if (obesidad.isChecked()){
                    if (riesgos==null){
                        riesgos="OBESIDAD MORBIDA";
                        codigoriesgo="13";
                    } else {
                        riesgos+=",OBESIDAD MORBIDA";
                        codigoriesgo+=",13";}}
                if (inmunodeficiencia.isChecked()){
                    if (riesgos==null){
                        riesgos="INMUNODEFICIENCIA";
                        codigoriesgo="14";
                    } else {
                        riesgos+=",INMUNODEFICIENCIA";
                        codigoriesgo+=",14";}}
                if (conviviente.isChecked()){
                    if (riesgos==null){
                        riesgos="CONVIVIENTE INMUNOCOMPROMETIDOS";
                        codigoriesgo="15";
                    } else {
                        riesgos+=",CONVIVIENTE INMUNOCOMPROMETIDOS";
                        codigoriesgo+=",15";}}
                if (HTA.isChecked()){
                    if (riesgos==null){
                        riesgos="HIPERTENSO";
                        //codigoriesgo="";
                    } else {
                        riesgos+=",HIPERTENSO";
                        //codigoriesgo+="";
                    }}
                if (oncologicos.isChecked()){
                    if (riesgos==null){
                        riesgos="ONCOLOGICO";
                        //codigoriesgo="";
                    } else {
                        riesgos+=",ONCOLOGICO";
                        //codigoriesgo+="";
                    }}
                if (mayor60.isChecked()){
                    if (riesgos==null){
                        riesgos="MAYOR DE 60";
                        //codigoriesgo="";
                    } else {
                        riesgos+=",MAYOR DE 60";
                        //codigoriesgo+="";
                    }}
                if (otros.isChecked()){
                    if (riesgos==null){
                        riesgos="OTROS";
                        codigoriesgo="16";
                    } else {
                        riesgos+=",OTROS";
                        codigoriesgo+=",16";}}
                if(riesgos==null){
                    persona.FactoresDeRiesgo="";
                    persona.CodfigoFactorRiesgo="";
                }
                else{
                    persona.FactoresDeRiesgo=riesgos;
                    persona.CodfigoFactorRiesgo=codigoriesgo;}
                //Toast.makeText(getApplicationContext(), Persona.FactoresDeRiesgo, Toast.LENGTH_SHORT).show();
                ColorAvanceFactores();
            }
        });
        builder.setNegativeButton("CANCELAR", null);

        // Defino un ScrollView para visualizar todos
        ScrollView sv = new ScrollView(context);
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sv.setVerticalScrollBarEnabled(true);
        sv.addView(mainLayout);

        builder.setView(sv);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public PersonClass returnPersona(){
        return persona;
    }

    public void ColorAvanceFactores() {
        float avance = 0;
        if (persona.FactoresDeRiesgo.length()!=0){
            avance+=1;
        }

        if(avance==0){
            factores.setBackgroundResource(R.drawable.rojo);
            avancefactores.setText(context.getString(R.string.completado)+" 00%");
        }

        if(avance==1){
            factores.setBackgroundResource(R.drawable.verde);
            avancefactores.setText(context.getString(R.string.completado)+" 100%");
        }
    }

    //----------------------------------------------------------------------------------------------
    // Defino los Layout para los Check que se añaden a los AlertDialog
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private final CheckBox PersonalCheck (LinearLayout Contenedor, String Texto, int TamañoLetra, String ColorPares, int AltoContenedor){
        // 16 POLIO - SABIN ORAL
        LinearLayout layout16       = new LinearLayout(context);
        layout16.setOrientation(LinearLayout.HORIZONTAL);
        layout16.setVerticalGravity(Gravity.CENTER_VERTICAL);
        final CheckBox sabin = new CheckBox(context);
        sabin.setText(Texto);
        sabin.setTextSize(TamañoLetra);
        sabin.setTextColor(Color.WHITE);
        sabin.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
        layout16.addView(sabin);
        layout16.setBackgroundColor(Color.parseColor(ColorPares));
        layout16.setMinimumHeight(AltoContenedor);
        Contenedor.addView(layout16);
        return sabin;}

    // Detecto los Check seleccionados anteriormente
    private void CheckSeleccionadosFactores(String Vacunas){
        if(Vacunas!=null){
            String[] vac =Vacunas.split(",");
            for (int x = 0; x < vac.length; x++) {
                if (vac[x].equals("POR CALENDARIO")){
                    calendario.setChecked(true);
                }
                if (vac[x].equals("EMBARAZO")){
                    embarazo.setChecked(true);
                }
                if (vac[x].equals("PUERPERIO")){
                    puerperio.setChecked(true);
                }
                if (vac[x].equals("PERSONAL DE SALUD")){
                    personalsalud.setChecked(true);
                }
                if (vac[x].equals("PERSONAL ESENCIAL")){
                    personalesencial.setChecked(true);
                }
                if (vac[x].equals("VIAJEROS")){
                    viajeros.setChecked(true);
                }
                if (vac[x].equals("INMUNOCOMPROMETIDOS")){
                    inmunocomprometidos.setChecked(true);
                }
                if (vac[x].equals("CARDIOLOGICOS")){
                    cadiologicos.setChecked(true);
                }
                if (vac[x].equals("RESPIRATORIOS")){
                    respiratorios.setChecked(true);
                }
                if (vac[x].equals("DIABETICOS")){
                    diabeticos.setChecked(true);
                }
                if (vac[x].equals("PREMATUROS")){
                    prematuros.setChecked(true);
                }
                if (vac[x].equals("ASPLÉNICOS")){
                    asplenicos.setChecked(true);
                }
                if (vac[x].equals("OBESIDAD MORBIDA")){
                    obesidad.setChecked(true);
                }
                if (vac[x].equals("INMUNODEFICIENCIA")){
                    inmunodeficiencia.setChecked(true);
                }
                if (vac[x].equals("CONVIVIENTE INMUNOCOMPROMETIDOS")){
                    conviviente.setChecked(true);
                }
                if (vac[x].equals("OTROS")){
                    otros.setChecked(true);
                }
                if (vac[x].equals("PREMATUROS")){
                    prematuros.setChecked(true);
                }
                if (vac[x].equals("ONCOLOGICO")){
                    oncologicos.setChecked(true);
                }
                if (vac[x].equals("MAYOR DE 60")){
                    mayor60.setChecked(true);
                }
                if (vac[x].equals("HIPERTENSO")){
                    HTA.setChecked(true);
                }
            }
        }}
}