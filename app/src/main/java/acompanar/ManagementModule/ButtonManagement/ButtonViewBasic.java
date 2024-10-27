package acompanar.ManagementModule.ButtonManagement;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import com.example.acompanar.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ButtonViewBasic {
    Context context;
    HashMap<String,String> options = new HashMap<>();
    BDData adminBData;
    public HashMap<String, Button> buttonsCalculator = new HashMap<>();
    public HashMap<String, TextView> setResult = new HashMap<>();

    public ButtonViewBasic(Context oldContext){
        context = oldContext;
        Archivos archivos = new Archivos(oldContext);
        options = archivos.getMapCategoriesDomicilio();
        options.putAll(archivos.getMapCategoriesPersonas());
        adminBData = new BDData(context, "BDData", null, 1);
    }

    // llevar a una clase para hacerlo general
    public View generateSpinnerMultipleSelect(String title, ArrayList<String> optionsSelect){

        LayoutInflater Inflater = LayoutInflater.from(context);
        final View view = Inflater.inflate(R.layout.basic_multiple_selection, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralMultipleSelection);
        titleCategory.setText(title);

        //Opciones del spiner
        final Spinner opciones = view.findViewById(R.id.SPDGralMultipleSelection);
        ArrayList<String> Opc = new ArrayList<>();
        Opc.add("");
        Opc.addAll(optionsSelect);

        // Cargo el spinner con los datos
        ArrayAdapter<String> comboAdapterCocinar = new ArrayAdapter<String>(context, R.layout.spiner_personalizado, Opc);
        opciones.setAdapter(comboAdapterCocinar);

        // Si hay un valor ya cargado lo precargo
        String preOption = adminBData.getValue4CodeCacheUD(options.get(title));
        int index = Opc.indexOf(options.get(preOption));
        if (index>=0){opciones.setSelection(index);}
        Log.e("title opt", title);

        return view;
    }

    public String getValueSpiner(View view){
        String value = "";

        Spinner spinner = (Spinner) view.findViewById(R.id.SPDGralMultipleSelection);
        if (spinner.getSelectedItem().toString()!=null) {
            value = spinner.getSelectedItem().toString();
        }

        if (options.get(value)!=null){
            value = options.get(value);
        }

        return value;
    }

    public View generateIntEdit(String title, Context ctxt){

        LayoutInflater Inflater = LayoutInflater.from(ctxt);
        final View view = Inflater.inflate(R.layout.basic_int_edit, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralIntEdit);
        titleCategory.setText(title);

        EditText cant = (EditText) view.findViewById(R.id.EDITGralIntEdit);
        cant.setHint("0");

        // Si hay un valor ya cargado lo precargo
        String preOption = adminBData.getValue4CodeCacheUD(options.get(title));
        if (!preOption.equals("")){cant.setText(preOption);}

        return view;
    }

    public String getValueInt(View view){
        String value = "";

        EditText edtText = (EditText) view.findViewById(R.id.EDITGralIntEdit);
        value = edtText.getText().toString();

        return value;
    }

    public View generateFloatEdit(String title, Context ctxt){

        LayoutInflater Inflater = LayoutInflater.from(ctxt);
        final View view = Inflater.inflate(R.layout.basic_float_edit, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralFloatEdit);
        titleCategory.setText(title);

        EditText cant = (EditText) view.findViewById(R.id.EDITGralFloatEdit);
        cant.setHint("0.0");

        // Si hay un valor ya cargado lo precargo
        String preOption = adminBData.getValue4CodeCacheUD(options.get(title));
        if (!preOption.equals("")){cant.setText(preOption);}

        return view;
    }

    public String getValueFloat(View view){
        String value = "";

        EditText edtText = (EditText) view.findViewById(R.id.EDITGralFloatEdit);
        value = edtText.getText().toString();

        return value;
    }

    public View generateRadioButtonYN(String title){

        LayoutInflater Inflater = LayoutInflater.from(context);
        final View view = Inflater.inflate(R.layout.basic_radio_yes_no, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralRadioYN);
        titleCategory.setText(title);

        // Si hay un valor ya cargado lo precargo
        String preOption = adminBData.getValue4CodeCacheUD(options.get(title));
        RadioButton yes = view.findViewById(R.id.RBYes);
        RadioButton no = view.findViewById(R.id.RBNo);
        if (preOption.equals("SI")){yes.setChecked(true);}
        if (preOption.equals("NO")){no.setChecked(true);}

        return view;
    }

    public String getValueRadio(View view){
        String value = "";

        RadioButton yes = view.findViewById(R.id.RBYes);
        if(yes.isChecked()){value = "SI";}
        RadioButton no = view.findViewById(R.id.RBNo);
        if(no.isChecked()){value = "NO";}

        return value;
    }

    public ArrayList<String> getSubOptions(HashMap<String,String> options, String codeCategoria){
        ArrayList<String> values = new ArrayList<>();

        Object[] opt = options.keySet().toArray();
        int cantSubOpt = 0;
        for (Object o : opt){
            if(o.toString().split("_")[0].equals(codeCategoria)){
                cantSubOpt++;
            }
        }

        for (int i=1; i<cantSubOpt; i++){
            values.add(options.get(codeCategoria+"_"+Integer.toString(i)));
        }
        return values;
    }

    public View generateDivisor(String title){

        LayoutInflater Inflater = LayoutInflater.from(context);
        final View view = Inflater.inflate(R.layout.basic_divisor, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.titleDivisor);
        titleCategory.setText(title);

        return view;
    }

    public View generateCalculator(String title){

        LayoutInflater Inflater = LayoutInflater.from(context);
        final View view = Inflater.inflate(R.layout.basic_calculator, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralResultEdit);
        titleCategory.setText(title);

        Button calcular = (Button) view.findViewById(R.id.buttonCalcular);
        buttonsCalculator.put(options.get(title), calcular);

        TextView result = (TextView) view.findViewById(R.id.TextViewGralResult);
        setResult.put(options.get(title), result);

        return view;
    }

    public View generateOptionsCheckbox(Context ctxt, String title, ArrayList<String> options1){
        LayoutInflater Inflater = LayoutInflater.from(ctxt);
        final View view = Inflater.inflate(R.layout.basic_check_box, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralCheckBox);
        titleCategory.setText(title);

        //Creo las opciones
        LinearLayout CLopts = view.findViewById(R.id.OptionsCheckBox);
        for (String opt : options1){
            CheckBox aux_opt = new CheckBox(context);
            aux_opt.setText(opt);
            String preOption = adminBData.getValue4CodeCacheUD(options.get(opt));

            if (Objects.equals(preOption, "SI")){
                aux_opt.setChecked(true);
            }else{
                aux_opt.setChecked(false);
            }

            //Log.e("txt check", aux_opt.getText()+" value" );
            CLopts.addView(aux_opt);
        }

        //Log.e("views", CLopts.getChildCount()+" views");
        return view;
    }

    public HashMap<String, String> getValueCheckBox(View view){
        HashMap<String,String> value = new HashMap<String,String>();

        LinearLayout CLopts = view.findViewById(R.id.OptionsCheckBox);
        for (int i=0; i<CLopts.getChildCount(); i++){
            CheckBox aux = (CheckBox) CLopts.getChildAt(i);
            if (aux.isChecked()){
                value.put(options.get(aux.getText()),"SI");
            }else{
                value.put(options.get(aux.getText()),"NO");
            }
        }

        return value;
    }

    public View generateDateEdit(String title, Context ctxt){

        LayoutInflater Inflater = LayoutInflater.from(ctxt);
        final View view = Inflater.inflate(R.layout.basic_date_edit, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralDateEdit);
        titleCategory.setText(title);

        final TextView date = view.findViewById(R.id.EDITGralDateEdit);
        String preOption = adminBData.getValue4CodeCacheUD(options.get(title));
        if (preOption!=null){
            if (preOption.length()!=0){
                date.setText(preOption);
            }
        }
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FechaPicker(view);
            }
        });

        return view;
    }

    private void FechaPicker (View view){
        final TextView date = view.findViewById(R.id.EDITGralDateEdit);

        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        LayoutInflater Inflater = LayoutInflater.from(context);
        View view1 = Inflater.inflate(R.layout.basic_dialog_date, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        DatePicker calendarView = view1.findViewById(R.id.calendarViewDate);

        Button save = view1.findViewById(R.id.GUARDARDATE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = calendarView.getYear();
                int month = calendarView.getMonth()+1;
                int day = calendarView.getDayOfMonth();
                //ultimoControl.setText(Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year));
                date.setText(Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year));
                //Toast.makeText(getBaseContext(), auxFecha[0] , Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        Button cancel = view1.findViewById(R.id.CANCELARDATE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public String getValueDate(View view){
        TextView date = view.findViewById(R.id.EDITGralDateEdit);

        return date.getText().toString();
    }

    public View generateTextEdit(String title, Context ctxt){

        LayoutInflater Inflater = LayoutInflater.from(ctxt);
        final View view = Inflater.inflate(R.layout.basic_text_edit, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralTextEdit);
        titleCategory.setText(title);

        EditText cant = (EditText) view.findViewById(R.id.EDITGralTextEdit);
        cant.setHint("");

        // Si hay un valor ya cargado lo precargo
        String preOption = adminBData.getValue4CodeCacheUD(options.get(title));
        if (!preOption.equals("")){cant.setText(preOption);}

        return view;
    }

    public String getValueText(View view){
        String value = "";

        EditText edtText = (EditText) view.findViewById(R.id.EDITGralTextEdit);
        value = edtText.getText().toString();

        return value;
    }

    public View generateAutocompleteEdit(String title, Context ctxt){

        LayoutInflater Inflater = LayoutInflater.from(ctxt);
        final View view = Inflater.inflate(R.layout.basic_autocomplete_edit, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralTextEdit);
        titleCategory.setText(title);

        AutoCompleteTextView cant = (AutoCompleteTextView) view.findViewById(R.id.EDITGralTextEdit);
        cant.setHint("");

        // Si hay un valor ya cargado lo precargo
        String preOption = adminBData.getValue4CodeCacheUD(options.get(title));
        if (!preOption.equals("")){cant.setText(preOption);}

        return view;
    }

    public AutoCompleteTextView getAutocomplete(View view){
        AutoCompleteTextView auto = (AutoCompleteTextView) view.findViewById(R.id.EDITGralTextEdit);
        return auto;
    }
    public String getValueAutocomplete(View view){
        String value = "";

        AutoCompleteTextView edtText = (AutoCompleteTextView) view.findViewById(R.id.EDITGralTextEdit);
        value = edtText.getText().toString();

        return value;
    }

    public View generateText(String title, Context ctxt, String txt){

        LayoutInflater Inflater = LayoutInflater.from(ctxt);
        final View view = Inflater.inflate(R.layout.basic_text_view, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.TXTGralTextEdit);
        titleCategory.setText(title);

        TextView cant = (TextView) view.findViewById(R.id.EDITGralTextEdit);
        cant.setText(txt);

        return view;
    }

    public void buttonColor(Context context, String boton, ConstraintLayout layout, TextView avanceTxt){

        //busco los valores de data
        HashMap<String, String> data = new HashMap<>();
        Archivos archivos = new Archivos(context);
        ArrayList<String> cabs = archivos.getCodeCabecerasPersonasBoton(context, boton);
        cabs.addAll(archivos.getCodeCabecerasDomicilioBoton(context, boton));

        for (String cab : cabs){

            data.put(cab, adminBData.getValue4CodeCacheUD(cab));
            //Log.e("marca"+cab, adminBData.getValue4CodeCacheUD(cab));

        }

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

    public View generateCase(String title, String status){

        LayoutInflater Inflater = LayoutInflater.from(context);
        final View view = Inflater.inflate(R.layout.basic_case, null);

        //Titulo categoria
        final TextView titleCategory = view.findViewById(R.id.titleDivisor);
        titleCategory.setText(title);

        //Titulo categoria
        final TextView titleStatus = view.findViewById(R.id.txtSTATUS);
        titleStatus.setText(status);

        return view;
    }

}
