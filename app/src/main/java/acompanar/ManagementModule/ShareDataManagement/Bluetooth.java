package acompanar.ManagementModule.ShareDataManagement;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import acompanar.BasicObjets.FamiliarUnityClass;
import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.MenuInicio;
import com.example.acompanar.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Bluetooth extends AppCompatActivity {

    private boolean CONTINUE_READ_WRITE = true;
    private boolean CONNECTION_ENSTABLISHED = false;
    private boolean DEVICES_IN_LIST = true;

    ArrayList<String> dataString = new ArrayList<>();

    private static String NAME = "cz.kostecky.bluetoothcommunicationdemo"; //id of app
    private static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //SerialPortService ID // MY_UUID is the app's UUID string, also used by the client code.

    int bufferSize = 32768;
    Button bSend, disp, conectar, guardar;
    final String[] msgCompleto = {""};
    EditText et;
    ListView lv;
    CheckBox cbServer;
    ArrayList<String> listItems;
    ArrayAdapter<String> listAdapter;

    BDData adminBDData;

    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private InputStream is;
    private OutputStream os;
    private BluetoothDevice remoteDevice;
    private Set<BluetoothDevice> pairedDevices;

    private ImageButton imgBt;
    private TextView indBt, indUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        adminBDData = new BDData(getBaseContext(), "BDData", null, 1);
        //UI
        bSend = (Button) findViewById(R.id.button6);
        disp = (Button) findViewById(R.id.button3);
        guardar = (Button) findViewById(R.id.button8);
        conectar = (Button) findViewById(R.id.button5);
        indUser = (TextView) findViewById(R.id.TEXTINDICACIONES);
        lv = (ListView) findViewById(R.id.listView);
        et = (EditText) findViewById(R.id.txtMessage);
        cbServer = (CheckBox) findViewById(R.id.cbServer);
        cbServer.setChecked(true);

        listItems = new ArrayList<String>(); //shows messages in list view
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() //list onclick selection process
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEVICES_IN_LIST) {
                    String name = (String) parent.getItemAtPosition(position);
                    selectBTdevice(name); //selected device will be set globally
                    //Toast.makeText(getApplicationContext(), "Selected " + name, Toast.LENGTH_SHORT).show();
                    //do not automatically call OpenBT(null) because makes troubles with server/client selection
                } else //message is selected
                {
                    String message = (String) parent.getItemAtPosition(position);
                    et.setText(message);
                }
            }
        });

        adapter = getDefaultAdapter();
        if (adapter == null) //If the adapter is null, then Bluetooth is not supported
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        }

        list(null);

        imgBt = (ImageButton) findViewById(R.id.BUTTONBT);
        indBt = (TextView) findViewById(R.id.INDICADORBT);
        if (!adapter.isEnabled()) {
            imgBt.setBackground(getDrawable(R.drawable.button_redondo_rojo));
            indBt.setText("APAGADO");
            disp.setVisibility(View.GONE);
            conectar.setVisibility(View.GONE);
            bSend.setVisibility(View.GONE);
            cbServer.setVisibility(View.GONE);
            guardar.setVisibility(View.GONE);
            listItems.clear();
            indUser.setText("Encienda el Bluetooth");
        } else {
            imgBt.setBackground(getDrawable(R.drawable.button_redondo_azul));
            indBt.setText("ENCENDIDO");
            disp.setVisibility(View.VISIBLE);
            indUser.setText("Vea la lista de dispositivos y seleccione con el que se quiere conectar. " +
                    "NOTA: Si el dispositivo no aparece ir a la config del bluetooth y sincronizarlo primero");
            conectar.setVisibility(View.GONE);
            bSend.setVisibility(View.GONE);
            cbServer.setVisibility(View.GONE);
            guardar.setVisibility(View.GONE);
        }
    }

    public void ONOF(View view) {
        if (adapter.isEnabled()) {
            imgBt.setBackground(getDrawable(R.drawable.button_redondo_rojo));
            indBt.setText("APAGADO");
            disp.setVisibility(View.GONE);
            conectar.setVisibility(View.GONE);
            bSend.setVisibility(View.GONE);
            cbServer.setVisibility(View.GONE);
            guardar.setVisibility(View.GONE);
            indUser.setText("Encienda el Bluetooth");
            lv.setVisibility(View.GONE);
            off(null);
        } else {
            imgBt.setBackground(getDrawable(R.drawable.button_redondo_azul));
            indBt.setText("ENCENDIDO");
            disp.setVisibility(View.VISIBLE);
            indUser.setText("Vea la lista de dispositivos y seleccione con el que se quiere conectar. " +
                    "NOTA: Si el dispositivo no aparece ir a la config del bluetooth y sincronizarlo primero");
            lv.setVisibility(View.VISIBLE);
            on(null);
        }
    }

    private String DataToSend() throws JSONException {
        String value = "";
        JSONObject AllData = new JSONObject();

        ArrayList<FamiliarUnityClass> allFamilies = new ArrayList<>();
        allFamilies = adminBDData.SearchAllFamilies(this);
        JSONArray families = new JSONArray();
        for (FamiliarUnityClass family : allFamilies) {
            Object[] keys = family.Data.keySet().toArray();
            JSONObject familyjson = new JSONObject();
            for (Object key : keys) {
                familyjson.put(key.toString(), family.Data.get(key.toString()));
            }
            families.put(familyjson);
        }
        AllData.put("FAMILIAS", families);

        ArrayList<PersonClass> allPersons = new ArrayList<>();
        allPersons = adminBDData.SearchAllPersons(this);
        JSONArray persons = new JSONArray();
        for (PersonClass person : allPersons) {
            Object[] keys = person.Data.keySet().toArray();
            JSONObject personjson = new JSONObject();
            for (Object key : keys) {
                personjson.put(key.toString(), person.Data.get(key.toString()));
            }
            persons.put(personjson);
        }
        AllData.put("PERSONAS", persons);

        value = AllData.toString();
        Toast.makeText(this, value, Toast.LENGTH_SHORT);

        return value;
    }

    public void openBT(View v) //opens right thread for server or client
    {
        if (adapter == null) {
            adapter = getDefaultAdapter();
            Log.i(TAG, "Backup way of getting adapter was used!");
        }

        if (!adapter.isEnabled()) {
            Log.i(TAG, "BT device is turned off! Turning on...");
            on(null); //chat doesnt work when BT is off...
        }

        CONTINUE_READ_WRITE = true; //writer tiebreaker
        socket = null; //resetting if was used previously
        is = null; //resetting if was used previously
        os = null; //resetting if was used previously

        if (pairedDevices.isEmpty() || remoteDevice == null) {
            Toast.makeText(this, "Paired device is not selected, choose one", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cbServer.isChecked()) {
            //Toast.makeText(getApplicationContext(), "This device is server" ,Toast.LENGTH_SHORT).show();
            new Thread(serverListener).start();
        } else //CLIENT
        {
            //Toast.makeText(getApplicationContext(), "This device is client" ,Toast.LENGTH_SHORT).show();
            new Thread(clientConnecter).start();
        }
    }

    public void closeBT(View v) //for closing opened communications, cleaning used resources
    {
        /*if(adapter == null)
            return;*/

        CONTINUE_READ_WRITE = false;
        CONNECTION_ENSTABLISHED = false;

        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {
            }
            is = null;
        }

        if (os != null) {
            try {
                os.close();
            } catch (Exception e) {
            }
            os = null;
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
            }
            socket = null;
        }

        try {
            Handler mHandler = new Handler();
            mHandler.removeCallbacksAndMessages(writter);
            mHandler.removeCallbacksAndMessages(serverListener);
            mHandler.removeCallbacksAndMessages(clientConnecter);
            Log.i(TAG, "Threads ended...");
        } catch (Exception e) {
            Log.e(TAG, "Attemp for closing threads was unsucessfull.");
        }

        Toast.makeText(getApplicationContext(), "Communication closed", Toast.LENGTH_SHORT).show();

        list(null); //shows list for reselection
        et.setText("demo text");
    }

    private Runnable serverListener = new Runnable() {
        public void run() {
            try //opening of BT connection
            {
                //problematic with older phones... HELP: Change server/client orientation...
                //but solves: BluetoothAdapter: getBluetoothService() called with no BluetoothManagerCallback

                Log.i("TrackingFlow", "Server socket: new way used...");
                socket = (BluetoothSocket) remoteDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(remoteDevice, 1);
                socket.connect();
                CONNECTION_ENSTABLISHED = true; //protect from failing

            } catch (Exception e) //obsolete way how to open BT
            {
                try {
                    Log.e("TrackingFlow", "Server socket: old way used...");
                    BluetoothServerSocket tmpsocket = adapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                    socket = tmpsocket.accept();
                    CONNECTION_ENSTABLISHED = true; //protect from failing
                    Log.i("TrackingFlow", "Listening...");
                } catch (Exception ie) {
                    Log.e(TAG, "Socket's accept method failed", ie);
                    ie.printStackTrace();
                }
            }

            /*
            if(CONNECTION_ENSTABLISHED != true)
            {
                Log.e(TAG, "Server is NOT ready for listening...");
                //"java.io.IOException: read failed, socket might closed or timeout, read ret: -1" When bluetooth is HW off
                return;
            }
            else
            */
            Log.i(TAG, "Server is ready for listening...");

            runOnUiThread(new Runnable() {
                @Override
                public void run() { //Show message on UIThread
                    listItems.clear(); //remove chat history
                    listItems.add(0, String.format("Listo para recibir datos..."));
                    disp.setVisibility(View.GONE);
                    indUser.setText("Realice el envio de datos con el otro dispositivo");
                    conectar.setVisibility(View.GONE);
                    bSend.setVisibility(View.GONE);
                    cbServer.setVisibility(View.GONE);
                    listAdapter.notifyDataSetChanged();
                }
            });

            try //reading part
            {
                is = socket.getInputStream();
                os = socket.getOutputStream();
                new Thread(writter).start();

                //int bufferSize = 1024;
                int bytesRead = -1;
                byte[] buffer = new byte[bufferSize];

                while (CONTINUE_READ_WRITE) //Keep reading the messages while connection is open...
                {
                    final StringBuilder sb = new StringBuilder();
                    bytesRead = is.read(buffer);
                    if (bytesRead != 0) {
                        String result = "";
                        while ((bytesRead == bufferSize) && (buffer[bufferSize - 1] != 0)) {
                            String aux = new String(buffer, 0, bytesRead);
                            result = result + aux;
                            dataString.add(aux);
                            bytesRead = is.read(buffer);
                        }
                        result = result + new String(buffer, 0, bytesRead);
                        sb.append(result);
                    }
                    Log.e("TrackingFlow", "Read: " + sb.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //Show message on UIThread
                            //Toast.makeText(Bluetooth.this, sb.toString(), Toast.LENGTH_SHORT).show();

                            msgCompleto[0] += sb.toString();
                            Log.e("msg completo0", msgCompleto[0]);
                            Log.e("lon completo0", Integer.toString(msgCompleto[0].length()));
                            guardar.setVisibility(View.VISIBLE);
                            bSend.setVisibility(View.GONE);
                            indUser.setText("Ya se recibieron los datos, solo falta guardar");
                            listItems.add(0, String.format("< %s", sb.toString())); //showing in history
                            listAdapter.notifyDataSetChanged();
                        }
                    });

                }

            } catch (IOException e) {
                Log.e(TAG, "Server not connected...");
                e.printStackTrace();
            }
        }
    };

    public void GuardarDataView(View view) {
        BdEfectores bdEfectores = new BdEfectores();
        bdEfectores.execute();
    }

    public void GuardarData() {
        int cont_repetidos_familia = 0;
        int cont_repetidos_persona = 0;
        int cont_guardados_familia = 0;
        int cont_guardados_personas = 0;

        try {

            String aux = msgCompleto[0] + "}";
            String[] values = aux.split("HOYO");
            for (String val : values) {
                Log.e("val txt", val);
            }
            Log.e("msg completo3", Integer.toString(aux.length()));
            JSONObject jsonObject = new JSONObject(aux);
            Log.e("json", jsonObject.toString());

            JSONArray familias = (JSONArray) jsonObject.get("FAMILIAS");
            for (int i = 0; i < familias.length(); i++) {
                FamiliarUnityClass family = new FamiliarUnityClass(Bluetooth.this);
                Map<String, String> mapObj = new Gson().fromJson(familias.get(i).toString(), new TypeToken<HashMap<String, Object>>() {
                }.getType());
                family.Data = (HashMap<String, String>) mapObj;
                Log.e("data busqueda", "data: " + family.Data.get("LONGITUD") + " " + family.Data.get("LATITUD") + " " + family.Data.get("FECHA"));
                if (!adminBDData.ExistRegisterFamily(family.Data.get("LONGITUD"), family.Data.get("LATITUD"), family.Data.get("FECHA"))) {
                    adminBDData.insert_family(family);
                    cont_guardados_familia++;
                } else {
                    cont_repetidos_familia++;
                }
            }

            JSONArray personas = (JSONArray) jsonObject.get("PERSONAS");
            for (int i = 0; i < personas.length(); i++) {
                PersonClass person = new PersonClass(Bluetooth.this);
                Map<String, String> mapObj = new Gson().fromJson(personas.get(i).toString(), new TypeToken<HashMap<String, Object>>() {
                }.getType());
                person.Data = (HashMap<String, String>) mapObj;
                if (!adminBDData.ExistRegisterPerson(person.Data.get("DNI"), person.Data.get("FECHA"), person.Data.get("LATITUD"), person.Data.get("LONGITUD"))) {
                    adminBDData.insert_person(person);
                    cont_guardados_personas++;
                } else {
                    cont_repetidos_persona++;
                }
            }
            Log.e("msg completo1", msgCompleto[0]);

            //Toast.makeText(Bluetooth.this, "DATOS GUARDADOS", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            //Toast.makeText(Bluetooth.this, "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("eror conversion", e.toString());
            //Log.e("msg completo", msgCompleto[0]);
        }
        //indUser.setText("GUARDADOS: (FAMILIAS:"+cont_guardados_familia+"), (PERSONAS:"+cont_guardados_personas+"). NO GUARDADAS REPETIDAS: " +
        //        "(FAMILIAS:"+cont_guardados_personas+"), (PERSONAS:"+cont_repetidos_persona+")");
    }

    @SuppressLint("StaticFieldLeak")
    private class BdEfectores extends AsyncTask<Void, Void, Void> {

        // Creo un progress dialog para mostrar mientras se ejecuta este codigo
        ProgressDialog pd;

        /*Antes de comenzar la ejecucion se inicia el progress dialog con los siguientes atributos*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Bluetooth.this);
            pd.setMessage("Cargando datos, aguarde");
            pd.setCancelable(false);
            pd.show();
        }

        /* Este es el codigo que se ejecuta en segundo plano mientras el usuario ve un cartel de
         * cargando datos*/
        @Override
        protected Void doInBackground(Void... voids) {
            GuardarData();
            return null;
        }

        /* Despues de la ejecucion del codigo en segundo plano debo detener el alert que me indica
         * que se estan cargando los datos*/
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

    }

    private Runnable clientConnecter = new Runnable() {
        @Override
        public void run() {
            try {
                socket = remoteDevice.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                CONNECTION_ENSTABLISHED = true; //protect from failing

                /*
                if(CONNECTION_ENSTABLISHED != true)
                {
                    Log.e(TAG, "Client is NOT ready for listening...");
                    return;
                }
                else
                */
                Log.i(TAG, "Client is connected...");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { //Show message on UIThread
                        listItems.clear(); //remove chat history
                        listItems.add(0, String.format("Listo para enviar datos..."));
                        disp.setVisibility(View.GONE);
                        indUser.setText("Si el otro dispositivo esta listo para recibir, realice el envio de datos");
                        conectar.setVisibility(View.GONE);
                        bSend.setVisibility(View.VISIBLE);
                        cbServer.setVisibility(View.GONE);
                        listAdapter.notifyDataSetChanged();
                    }
                });

                os = socket.getOutputStream();
                is = socket.getInputStream();
                new Thread(writter).start();
                Log.i(TAG, "Preparation for reading was done");

                //wint bufferSize = 1024;
                int bytesRead = -1;
                byte[] buffer = new byte[bufferSize];

                while (CONTINUE_READ_WRITE) //Keep reading the messages while connection is open...
                {
                    final StringBuilder sb = new StringBuilder();
                    bytesRead = is.read(buffer);
                    if (bytesRead != 0) {
                        String result = "";
                        while ((bytesRead == bufferSize) && (buffer[bufferSize - 1] != 0)) {
                            result = result + new String(buffer, 0, bytesRead);
                            bytesRead = is.read(buffer);
                        }
                        result = result + new String(buffer, 0, bytesRead);
                        sb.append(result);
                    }

                    Log.e("TrackingFlow", "Read: " + sb.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //Show message on UIThread
                            Toast.makeText(Bluetooth.this, sb.toString(), Toast.LENGTH_SHORT).show();
                            listItems.add(0, String.format("< %s", sb.toString()));
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Client not connected...");
                e.printStackTrace();
            }
        }
    };

    private Runnable writter = new Runnable() {

        @Override
        public void run() {
            while (CONTINUE_READ_WRITE) //reads from open stream
            {
                try {
                    os.flush();
                    Thread.sleep(2000);
                } catch (Exception e) {
                    Log.e(TAG, "Writer failed in flushing output stream...");
                    CONTINUE_READ_WRITE = false;
                }
            }
        }
    };

    public void sendBtnClick(View v) throws JSONException //sends text from text button
    {
        if (CONNECTION_ENSTABLISHED == false) {
            Toast.makeText(getApplicationContext(), "Connection between devices is not ready.", Toast.LENGTH_SHORT).show(); //usually problem server-client decision
        } else {
            //String textToSend = et.getText().toString() + "X"; //method is cutting last character, so way how to cheat it...
            String textToSend = DataToSend();
            byte[] b = textToSend.getBytes();
            try {
                os.write(b);
                listItems.add(0, "Datos enviados"); //chat history
                listAdapter.notifyDataSetChanged();
                //et.setText(""); //remove text after sending
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Not sent", Toast.LENGTH_SHORT).show(); //usually problem server-client decision
            }
        }
    }

    public void on(View v) //turning on BT when is off
    {
        if (!adapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_SHORT).show();
        }

        list(null); //list devices automatically to UI
    }

    public void off(View v) //turning off BT device on phone
    {
        adapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_SHORT).show();
    }

    public void visible(View v) //BT device discoverable
    {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void list(View v) //shows paired devices to UI
    {
        CONNECTION_ENSTABLISHED = false; //protect from failing
        listItems.clear(); //remove chat history
        listAdapter.notifyDataSetChanged();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        pairedDevices = adapter.getBondedDevices(); //list of devices

        for(BluetoothDevice bt : pairedDevices) //foreach
        {
            listItems.add(0, bt.getName());
        }
        listAdapter.notifyDataSetChanged(); //reload UI
    }

    public void selectBTdevice(String name) //for selecting device from list which is used in procedures
    {
        if(pairedDevices.isEmpty()) {
            list(null);
            Toast.makeText(getApplicationContext(), "Selecting was unsucessful, no devices in list." ,Toast.LENGTH_SHORT ).show();
        }

        for(BluetoothDevice bt : pairedDevices) //foreach
        {
            if(name.equals(bt.getName()))
            {
                remoteDevice = bt;
                Toast.makeText(getApplicationContext(), "Selected " + remoteDevice.getName(), Toast.LENGTH_SHORT ).show();
                disp.setVisibility(View.GONE);
                indUser.setText("Conecte los dispositivos para el envio, " +
                        "seleccione si va a recibir o enviar datos segun corresponda");
                conectar.setVisibility(View.VISIBLE);
                bSend.setVisibility(View.GONE);
                cbServer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() //magic for GC, works automaticaly
    {
        super.onDestroy();
        closeBT(null);
    }
}