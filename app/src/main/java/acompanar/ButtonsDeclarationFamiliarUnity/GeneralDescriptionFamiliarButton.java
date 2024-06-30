package acompanar.ButtonsDeclarationFamiliarUnity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

//import com.example.relevar.BasicObjets.FamiliarUnityClass;
import com.example.acompanar.R;

import java.io.Serializable;

import acompanar.BasicObjets.FamiliarUnityClass;

public class GeneralDescriptionFamiliarButton implements Serializable {
    FamiliarUnityClass familia;
    Context context;
    ConstraintLayout CLGeneral;
    TextView avanceGeneral;
    int NumerosPersonas = 0;
    Boolean renuente, deshabitada, vacia_habitada;


    public GeneralDescriptionFamiliarButton(Context originalContext, FamiliarUnityClass originalFamilia, View originalview, Boolean new_renuente, Boolean new_deshabitada, Boolean new_vacia_habitada){
        context=originalContext;
        familia=originalFamilia;

        renuente = new_renuente;
        deshabitada = new_deshabitada;
        vacia_habitada = new_vacia_habitada;

        CLGeneral = (ConstraintLayout) originalview.findViewById(R.id.AVANCEGENERAL);
        avanceGeneral = (TextView) originalview.findViewById(R.id.COMPLETADOGENERAL);

    }

    public void vista(){

        //NumerosPersonas = MiembrosFamiliares.size();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater Inflater = LayoutInflater.from(context);
        //LayoutInflater Inflater = getLayoutInflater();
        final View view_alert = Inflater.inflate(R.layout.alert_guardar_familia, null);
        builder.setView(view_alert);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        if (familia.codigoColor[0].length()==0){
            familia.codigoColor[0]="V";
        }

        final ImageView rojo = view_alert.findViewById(R.id.ICONOROJO);
        final ImageView amarillo = view_alert.findViewById(R.id.ICONOAMARILLO);
        final ImageView verde = view_alert.findViewById(R.id.ICONOVERDE);

        if (familia.codigoColor[0].equals("V")){
            verde.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_blanco));
            amarillo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
            rojo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
        }

        if (familia.codigoColor[0].equals("R")){
            rojo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_blanco));
            amarillo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
            verde.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
        }

        if (familia.codigoColor[0].equals("A")){
            amarillo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_blanco));
            rojo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
            verde.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
        }

        rojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                familia.codigoColor[0] = "R";
                rojo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_blanco));
                amarillo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
                verde.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
            }
        });

        amarillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                familia.codigoColor[0] = "A";
                amarillo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_blanco));
                rojo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
                verde.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
            }
        });

        verde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                familia.codigoColor[0] = "V";
                verde.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_blanco));
                amarillo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
                rojo.setBackground(context.getResources().getDrawable(R.drawable.button_redondo_gris));
            }
        });

        final EditText edtCalle = view_alert.findViewById(R.id.CALLE);
        if(familia.calle.length()!=0){
            edtCalle.setText(familia.calle);
        }
        final EditText edtNumero = view_alert.findViewById(R.id.NUMERO);
        if(familia.numero.length()!=0){
            edtNumero.setText(familia.numero);
        }
        //final EditText cantidadintegrantes = view_alert.findViewById(R.id.CANTIDADMIEMBROSFAMILIARES);
        final EditText edtnumerocartografia = view_alert.findViewById(R.id.NUMEROSEGUNCARTOGRAFIA);
        if(familia.numeroCartografia.length()!=0){
            edtnumerocartografia.setText(familia.numeroCartografia);
        }

        final EditText edtTelefonoFamiliar = view_alert.findViewById(R.id.TELEFONOFAMILIAR);
        if(familia.TelefonoFamiliar.length()!=0){
            edtTelefonoFamiliar.setText(familia.TelefonoFamiliar);
        }

        final EditText menores = view_alert.findViewById(R.id.EDTXTMENORES);
        if (familia.cantidadMenores!=0) {
            menores.setText(Integer.toString(familia.cantidadMenores));
        }
        final EditText mayores = view_alert.findViewById(R.id.EDTXTMAYORES);
        if (familia.cantidadMayores!=0) {
            mayores.setText(Integer.toString(familia.cantidadMayores));
        }

        final EditText observaciones = view_alert.findViewById(R.id.TEXTOBSERVATION);
        if (familia.ObservacionesVivienda.length()!=0){
            observaciones.setText(familia.ObservacionesVivienda);
        }

        final Button guardar = view_alert.findViewById(R.id.GUARDARFAMILIA);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(renuente || deshabitada || vacia_habitada || (edtTelefonoFamiliar.getText().toString().length()!=0)) {
                    //NumerosPersonas = Integer.parseInt(cantidadintegrantes.getText().toString());
                    if(observaciones.getText().toString().length()!=0){
                        familia.ObservacionesVivienda = observaciones.getText().toString();
                    }
                    NumerosPersonas = 0;
                    if (menores.getText().toString().length() != 0) {
                        familia.cantidadMenores = Integer.parseInt(menores.getText().toString());
                        NumerosPersonas += familia.cantidadMenores;
                    }
                    if (mayores.getText().toString().length() != 0) {
                        familia.cantidadMayores = Integer.parseInt(mayores.getText().toString());
                        NumerosPersonas += familia.cantidadMayores;
                    }
                    familia.calle = edtCalle.getText().toString();
                    familia.numero = edtNumero.getText().toString();
                    familia.numeroCartografia = edtnumerocartografia.getText().toString();
                    familia.TelefonoFamiliar = edtTelefonoFamiliar.getText().toString();
                    if ((familia.cantidadMayores + familia.cantidadMenores) == NumerosPersonas || (familia.cantidadMayores + familia.cantidadMenores) == 0) {
                        ColorAvanceGeneralFamilia();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "LA SUMA DE MENORES Y MAYORES NO COINCIDE CON EL TOTAL", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, context.getString(R.string.obligatorio_tel_fam), Toast.LENGTH_SHORT).show();
                    edtTelefonoFamiliar.requestFocus();
                }
            }
        });

        final Button cancelar = view_alert.findViewById(R.id.CANCELAR1);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public FamiliarUnityClass returnFamilia(){
        return familia;
    }

    public void ColorAvanceGeneralFamilia() {
        // Cambio los colores de avance
        float avance = 0;
        if (familia.calle.length()!=0){
            avance+=1;
        }
        if (familia.numero.length()!=0){
            avance+=1;
        }
        if (familia.numeroCartografia.length()!=0){
            avance+=1;
        }

        if (familia.TelefonoFamiliar.length()!=0){
            avance+=1;
        }
        if (familia.cantidadMayores!=0){
            avance+=1;
        }
        if (familia.cantidadMenores!=0){
            avance+=1;
        }

        if(avance>0 && avance<6){
            CLGeneral.setBackgroundResource(R.drawable.amarillo);
            double porcentaje = Math.round((avance/3)*100);
            //Toast.makeText(getApplicationContext(), Double.toString(porcentaje), Toast.LENGTH_SHORT).show();
            String aux = context.getString(R.string.completado)+" "+ Double.toString(porcentaje)+"%";
            avanceGeneral.setText(aux);
        }
        if(avance==6){
            CLGeneral.setBackgroundResource(R.drawable.verde);
            avanceGeneral.setText(context.getString(R.string.completado)+" 100%");
        }
        if(avance==0){
            CLGeneral.setBackgroundResource(R.drawable.rojo);
            avanceGeneral.setText(context.getString(R.string.completado)+" 00%");
        }
    }
}