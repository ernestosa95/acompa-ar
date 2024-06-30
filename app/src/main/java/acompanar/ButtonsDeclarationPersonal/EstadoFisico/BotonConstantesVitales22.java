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
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class BotonConstantesVitales22 implements Serializable {
    Context context;
    ConstraintLayout layout;
    TextView avanceTxt;
    BDData adminBData;
    Archivos mngArchivo;
    HashMap<String,String> options = new HashMap<>();
    ButtonViewBasic buttonViewBasic;

    String title_button;

    public BotonConstantesVitales22(Context originalContext, View view, String nBtn){
        context=originalContext;
        title_button = nBtn;

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

        //RE28:
        View RE28 = buttonViewBasic.generateFloatEdit(options.get("RE28_0"),context);
        lyOptions.addView(RE28);

        //RE29:
        View RE29 = buttonViewBasic.generateFloatEdit(options.get("RE29_0"),context);
        lyOptions.addView(RE29);

        //RE23:
        View RE31 = buttonViewBasic.generateCalculator(options.get("RE31_0"));
        String[] imc = new String[1];
        imc[0] = "0";
        buttonViewBasic.buttonsCalculator.get("RE31_0").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!buttonViewBasic.getValueFloat(RE29).isEmpty() && Float.parseFloat(buttonViewBasic.getValueFloat(RE29))!=0) {
                    DecimalFormat df = new DecimalFormat("#.00");
                    imc[0] = String.valueOf(df.format(Float.parseFloat(buttonViewBasic.getValueFloat(RE28)) / Math.pow((Float.parseFloat(buttonViewBasic.getValueFloat(RE29))/100), 2)));
                    buttonViewBasic.setResult.get("RE31_0").setText(imc[0]);
                }else {
                    Toast.makeText(context, "Imposible calcular", Toast.LENGTH_SHORT).show();
                    buttonViewBasic.setResult.get("RE31_0").setText("0.0");
                }
            }
        });
        lyOptions.addView(RE31);

        //RE23:
        View RE30 = buttonViewBasic.generateFloatEdit(options.get("RE30_0"),context);
        lyOptions.addView(RE30);

        //RE23:
        //View RE35 = buttonViewBasic.generateFloatEdit(options.get("RE35_0"),context);
        View RE35 = buttonViewBasic.generateCalculator(options.get("RE35_0"));
        int[] findridk = new int[1];
        findridk[0] = 0;
        buttonViewBasic.buttonsCalculator.get("RE35_0").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!buttonViewBasic.getValueFloat(RE30).equals("") && !imc[0].equals("0")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater Inflater = LayoutInflater.from(context);
                    final View view2 = Inflater.inflate(R.layout.basic_alert, null);
                    view2.setFocusable(true);
                    builder.setView(view2);
                    builder.setCancelable(false);
                    final AlertDialog dialog2 = builder.create();
                    dialog2.show();

                    //Crear el linerLayout que va a contener las diferentes categorias
                    LinearLayout lyOptions2 = view2.findViewById(R.id.LYGralOptions);

                    //Cabecera
                    TextView txtCabecera2 = view2.findViewById(R.id.TXTGralCabecera);
                    txtCabecera2.setText(options.get("RE35_0"));

                    ArrayList<String> sexo = new ArrayList<>();
                    sexo.add("MASCULINO");
                    sexo.add("FEMENINO");
                    View sexo_biologico = buttonViewBasic.generateSpinnerMultipleSelect("SEXO BIOLOGICO", sexo);
                    lyOptions2.addView(sexo_biologico);

                    View edad = buttonViewBasic.generateIntEdit("EDAD (años)", context);
                    lyOptions2.addView(edad);

                    ArrayList<String> alimentacion_saludable_opt = new ArrayList<>();
                    alimentacion_saludable_opt.add("DIARIO");
                    alimentacion_saludable_opt.add("NO DIARIAMENTE");
                    View alimentacion_saludable = buttonViewBasic.generateSpinnerMultipleSelect("¿Con què frecuencia consume frutas o verduras?", alimentacion_saludable_opt);
                    lyOptions2.addView(alimentacion_saludable);

                    View deteccion_glucosa_alta = buttonViewBasic.generateRadioButtonYN("¿Le han detectado alguna vez, en un control mèdico, un nivel muy alto de glucosa en sangre?");
                    lyOptions2.addView(deteccion_glucosa_alta);

                    View actividad_fisica = buttonViewBasic.generateRadioButtonYN("¿Hace actividad fìsica por lo menos 30 min diarios?");
                    lyOptions2.addView(actividad_fisica);

                    View medicamentos_hipertension = buttonViewBasic.generateRadioButtonYN("¿Le han recetado alguna vez medicamentos contra la hipertensiòn?");
                    lyOptions2.addView(medicamentos_hipertension);

                    ArrayList<String> diagnostico_diabetes_opt = new ArrayList<>();
                    diagnostico_diabetes_opt.add("NO");
                    diagnostico_diabetes_opt.add("SI: Abuelo/a, Tìo/a, Primo/a");
                    diagnostico_diabetes_opt.add("SI: Padre, Madre, Hermano/a, Hijo/a");
                    View diagnostico_diabetes = buttonViewBasic.generateSpinnerMultipleSelect("¿Ha habido un diagnòstico de Diabetes en, por los menos, un miembro de la familia?", diagnostico_diabetes_opt);
                    lyOptions2.addView(diagnostico_diabetes);

                    //Boton guardar
                    Button guardar2 = view2.findViewById(R.id.GUARDARGrl);

                    guardar2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!buttonViewBasic.getValueInt(edad).equals("") && !imc[0].equals("0") && !buttonViewBasic.getValueSpiner(alimentacion_saludable).equals("")
                            && !buttonViewBasic.getValueRadio(deteccion_glucosa_alta).equals("") && !buttonViewBasic.getValueSpiner(sexo_biologico).equals("")
                            && !buttonViewBasic.getValueRadio(actividad_fisica).equals("") && !buttonViewBasic.getValueRadio(medicamentos_hipertension).equals("")
                            && !buttonViewBasic.getValueSpiner(diagnostico_diabetes).equals("")){

                                //EDAD
                                if (Integer.parseInt(buttonViewBasic.getValueInt(edad))>=35 && Integer.parseInt(buttonViewBasic.getValueInt(edad))<=44){
                                    findridk[0] += 1;
                                } else if (Integer.parseInt(buttonViewBasic.getValueInt(edad))>=45 && Integer.parseInt(buttonViewBasic.getValueInt(edad))<=54) {
                                    findridk[0] += 2;
                                } else if (Integer.parseInt(buttonViewBasic.getValueInt(edad))>=55 && Integer.parseInt(buttonViewBasic.getValueInt(edad))<=64) {
                                    findridk[0] += 3;
                                } else if (Integer.parseInt(buttonViewBasic.getValueInt(edad))>=55) {
                                    findridk[0] += 4;
                                }

                                //IMC
                                String imcS = imc[0].replace(",", ".");
                                if (Float.parseFloat(imcS)>=25 && Float.parseFloat(imcS)<=30){
                                    findridk[0] += 1;
                                } else if (Float.parseFloat(imcS)>30) {
                                    findridk[0] += 3;
                                }

                                //vegetales y frutas
                                if (!buttonViewBasic.getValueSpiner(alimentacion_saludable).equals("DIARIO")){
                                    findridk[0] += 1;
                                }

                                //glucosa alta
                                if (!buttonViewBasic.getValueRadio(deteccion_glucosa_alta).equals("SI")){
                                    findridk[0] += 5;
                                }

                                //perimetro de la cintura
                                if (buttonViewBasic.getValueSpiner(sexo_biologico).equals("MASCULINO")){
                                    if (Float.parseFloat(buttonViewBasic.getValueFloat(RE30))>=90 && Float.parseFloat(buttonViewBasic.getValueFloat(RE30))<=102){
                                        findridk[0] += 3;
                                    } else if (Float.parseFloat(buttonViewBasic.getValueFloat(RE30))>102) {
                                        findridk[0] += 4;
                                    }
                                } else {
                                    if (Float.parseFloat(buttonViewBasic.getValueFloat(RE30))>=80 && Float.parseFloat(buttonViewBasic.getValueFloat(RE30))<=88){
                                        findridk[0] += 3;
                                    } else if (Float.parseFloat(buttonViewBasic.getValueFloat(RE30))>88) {
                                        findridk[0] += 4;
                                    }
                                }

                                //actividad fisica
                                if (!buttonViewBasic.getValueRadio(actividad_fisica).equals("SI")){
                                    findridk[0] += 2;
                                }

                                //medicamento hpertension
                                if (!buttonViewBasic.getValueRadio(medicamentos_hipertension).equals("SI")){
                                    findridk[0] += 2;
                                }

                                //diabetes en la familia
                                if (buttonViewBasic.getValueSpiner(diagnostico_diabetes).equals("SI: Abuelo/a, Tìo/a, Primo/a")){
                                    findridk[0] += 3;
                                } else if (buttonViewBasic.getValueSpiner(diagnostico_diabetes).equals("SI: Padre, Madre, Hermano/a, Hijo/a")) {
                                    findridk[0] += 5;
                                }

                                if (findridk[0]<7){
                                    buttonViewBasic.setResult.get("RE35_0").setText("MENOS DE 7 = RIESGO MUY BAJO");
                                } else if (findridk[0]>=7 && findridk[0]<=11) {
                                    buttonViewBasic.setResult.get("RE35_0").setText("7 - 11 = RIESGO BAJO");
                                } else if (findridk[0]>=12 && findridk[0]<=14) {
                                    buttonViewBasic.setResult.get("RE35_0").setText("12 - 14 = RIESGO BAJO");
                                } else if (findridk[0]>=15 && findridk[0]<=20) {
                                    buttonViewBasic.setResult.get("RE35_0").setText("15 - 20 = RIESGO ALTO");
                                }else if (findridk[0]>=20) {
                                    buttonViewBasic.setResult.get("RE35_0").setText("MAS DE 20 = RIESGO MUY ALTO");
                                }
                                buttonViewBasic.setResult.get("RE35_0").setTextSize(14);

                                dialog2.dismiss();
                            } else {
                                Toast.makeText(context, "Faltan datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //Boton cancelar
                    ImageButton cancelar2 = view2.findViewById(R.id.CANCELARGrl);
                    cancelar2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(context, "Debe calcular primero el IMC y registrar el perimetro abdominal", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lyOptions.addView(RE35);

        //DIVISOR
        View divisor = buttonViewBasic.generateDivisor(options.get("RE32_0"));
        lyOptions.addView(divisor);

        //RE23:
        View RE321 = buttonViewBasic.generateFloatEdit(options.get("RE32_1"),context);
        lyOptions.addView(RE321);

        //RE23:
        View RE322 = buttonViewBasic.generateFloatEdit(options.get("RE32_2"),context);
        lyOptions.addView(RE322);

        //DIVISOR
        View divisor_aux = buttonViewBasic.generateDivisor("");
        lyOptions.addView(divisor_aux);

        //RE33:
        View RE33 = buttonViewBasic.generateFloatEdit(options.get("RE33_0"),context);
        lyOptions.addView(RE33);

        //RE23:
        View RE34 = buttonViewBasic.generateFloatEdit(options.get("RE34_0"),context);
        lyOptions.addView(RE34);

        //RE23:
        View RE36 = buttonViewBasic.generateFloatEdit(options.get("RE36_0"),context);
        lyOptions.addView(RE36);

        //Boton guardar
        Button guardar = view.findViewById(R.id.GUARDARGrl);
        HashMap<String, String> data = new HashMap<>();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.put("RE28_0", buttonViewBasic.getValueFloat(RE28));
                data.put("RE29_0", buttonViewBasic.getValueFloat(RE29));
                data.put("RE30_0", buttonViewBasic.getValueFloat(RE30));
                data.put("RE31_0", imc[0]);
                data.put("RE32_1", buttonViewBasic.getValueFloat(RE321));
                data.put("RE32_2", buttonViewBasic.getValueFloat(RE322));
                data.put("RE33_0", buttonViewBasic.getValueFloat(RE33));
                data.put("RE34_0", buttonViewBasic.getValueFloat(RE34));
                //data.put("RE35_0", buttonViewBasic.getValueFloat(RE35));
                data.put("RE35_0", String.valueOf(findridk[0]));
                data.put("RE36_0", buttonViewBasic.getValueFloat(RE36));

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
