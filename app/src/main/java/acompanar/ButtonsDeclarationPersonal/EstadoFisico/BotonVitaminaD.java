package acompanar.ButtonsDeclarationPersonal.EstadoFisico;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

import java.io.Serializable;

public class BotonVitaminaD implements Serializable {
    PersonClass persona;
    Context context;
    ConstraintLayout layout_vitamina;
    TextView avancevitamina;

    public BotonVitaminaD(Context originalContext, PersonClass originalPersona, View originalview){
        context=originalContext;
        persona=originalPersona;
        layout_vitamina = (ConstraintLayout) originalview.findViewById(R.id.AVANCEVITAMINA);
        avancevitamina = (TextView) originalview.findViewById(R.id.COMPLETADOVITAMINA);
    }

    public void vista(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater Inflater = LayoutInflater.from(context);
        View view1 = Inflater.inflate(R.layout.alert_vitamina, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final RadioGroup vitamina = view1.findViewById(R.id.GROUPVITAMINA);
        final RadioButton siVitamina = view1.findViewById(R.id.SIVITAMINA);
        final RadioButton noVitamina = view1.findViewById(R.id.NOVITAMINA);
        if(persona.Vitamina.equals("SI")){siVitamina.setChecked(true);}
        if(persona.Vitamina.equals("NO")){noVitamina.setChecked(true);}

        final Button guardar = view1.findViewById(R.id.GUARDARVITAMINA);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selec = vitamina.findViewById(vitamina.getCheckedRadioButtonId());
                if(selec!=null){
                    String seleccionado = selec.getText().toString();
                    persona.Vitamina = seleccionado;}
                //Toast.makeText(getApplicationContext(), seleccionado, Toast.LENGTH_SHORT).show();
                ColorAvanceVitamina();
                dialog.dismiss();
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELARVITAMINA);
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

    public void ColorAvanceVitamina() {
        // Cambio los colores de avance
        float avance = 0;
        if (persona.Vitamina.length()!=0){
            avance+=1;
        }

        if(avance==0){
            layout_vitamina.setBackgroundResource(R.drawable.rojo);
            avancevitamina.setText(context.getString(R.string.completado)+" 00%");
        }

        if(avance==1){
            layout_vitamina.setBackgroundResource(R.drawable.verde);
            avancevitamina.setText(context.getString(R.string.completado)+" 100%");
        }
    }
}