package acompanar.ManagementModule.ShareDataManagement.FHIR;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FHIRresources {
    //Resources FHIR
    public JSONObject patientResourceFHIR(String id,
                                           String[] name_data,
                                           String surname,
                                           String gender_data,
                                           String birthdate,
                                           String line_data,
                                           String city_data,
                                           String postalcode,
                                           String country_data,
                                           String district_data,
                                           Double latitude,
                                           Double longitude,
                                           String number_house,
                                           String cell_number){
        JSONObject obj = new JSONObject();
        if (gender_data.equals("M")){
            gender_data = "male";
        } else if (gender_data.equals("F")){
            gender_data = "female";
        } else { gender_data = "other"; }

        try {
            obj.put("resourceType", "Patient");

            JSONArray identifier = new JSONArray();
            identifier.put(new JSONObject()
                    .put("system", "RelevAr")
                    .put("value", id));
            identifier.put(new JSONObject()
                    .put("system", "http://www.renaper.gob.ar/dni")
                    .put("value", id));
            obj.put("identifier", identifier);

            JSONArray name = new JSONArray();
            JSONArray given = new JSONArray();
            for (int i=0; i<name_data.length;i++){
                given.put(name_data[i]);}
            name.put(new JSONObject()
                    .put("use","official")
                    .put("family", surname)
                    .put("given", given));
            obj.put("name", name);

            obj.put("gender", gender_data);
            obj.put("birthDate", birthdate);

            JSONArray telecom = new JSONArray()
                    .put(new JSONObject()
                            .put("system", "phone")
                            .put("value", cell_number)
                            .put("use", "mobile"));
            obj.put("telecom", telecom);

            JSONArray address = new JSONArray();
            JSONArray line = new JSONArray();
            line.put(line_data);
            JSONArray extension = new JSONArray();
            JSONObject objlatitude = new JSONObject();
            objlatitude.put("url","latitude");
            objlatitude.put("valueDecimal", latitude);
            JSONObject objlongitude = new JSONObject();
            objlongitude.put("url", "longitude");
            objlongitude.put("valueDecimal", longitude);
            JSONObject geolocation = new JSONObject();
            geolocation.put("url","http://hl7.org/fhir/StructureDefinition/geolocation");
            geolocation.put("extension", new JSONArray()
                    .put(objlatitude)
                    .put(objlongitude));

            JSONObject number = new JSONObject();
            number.put("url","houseNumber");
            number.put("valueString", number_house);
            JSONObject houseNumber = new JSONObject();
            houseNumber.put("url", "http://hl7.org/fhir/StructureDefinition/iso21090-ADXP-houseNumber");
            houseNumber.put("extension", new JSONArray()
                    .put(number));

            extension.put(geolocation);
            extension.put(houseNumber);
            address.put(new JSONObject()
                    .put("use", "home")
                    .put("line", line)
                    .put("city", city_data)
                    //.put("state", state_data)
                    .put("postalCode", postalcode)
                    .put("country", country_data)
                    .put("district", district_data)
                    .put("extension", extension));
            obj.put("address", address);
        }catch(JSONException e){}
        return obj;
    }

    public JSONObject observationIndecResourceFHIR( String id_du,
                                                     String code_category,
                                                     String text_category,
                                                     String date,
                                                     String name_prof,
                                                     String dni_prof,
                                                     String code_value,
                                                     String text_value)  {
        JSONObject obj = new JSONObject();
        try {
            obj.put("resourceType", "Observation");

            JSONArray identifier = new JSONArray();
            identifier.put(new JSONObject()
                    .put("use","official")
                    .put("system", "RelevAr")
                    .put("value",id_du));
            obj.put("identifier", identifier);

            obj.put("status", "final");

            JSONObject code = new JSONObject()
                    .put("coding", new JSONArray()
                            .put(new JSONObject()
                                    .put("system", "INDEC")
                                    .put("code", code_category)
                                    .put("display", text_category)));
            obj.put("code", code);

            obj.put("effectiveDateTime", date);

            JSONArray performer = new JSONArray()
                    .put(new JSONObject()
                            .put("display", name_prof)
                            .put("identifier", new JSONObject()
                                    .put("use", "official")
                                    .put("system", "RelevAr")
                                    .put("value", dni_prof)));
            obj.put("performer", performer);

            JSONObject valueCodeableConcept = new JSONObject()
                    .put("coding", new JSONArray().put(new JSONObject()
                            .put("system", "https://redatam.indec.gob.ar/argbin/RpWebEngine.exe/PortalAction?&MODE=MAIN&BASE=CPV2010B&MAIN=WebServerMain.inl&_ga=2.11744892.977170785.1640611973-17329590.1640611973")
                            .put("code", code_category)
                            .put("display", text_value)));
            obj.put("valueCodeableConcept", valueCodeableConcept);
        }catch(JSONException e){}
        return obj;
    }

    /*public JSONObject observationIpcResourceFHIR(   String dni,
                                                     String name_institution,
                                                     String code_category,
                                                     String text_category,
                                                     String date,
                                                     String name_prof,
                                                     String dni_prof,
                                                     String code_value,
                                                     String text_value)  {
        JSONObject obj = new JSONObject();
        try {
            obj.put("resourceType", "Observation");

            JSONArray identifier = new JSONArray();
            identifier.put(new JSONObject()
                    .put("use","official")
                    .put("system", "RelevAr")
                    .put("value", dni));
            identifier.put(new JSONObject()
                    .put("use", "official")
                    .put("system", "REFES")
                    .put("value", name_institution));
            obj.put("identifier", identifier);

            obj.put("status", "final");

            JSONObject code = new JSONObject()
                    .put("coding", new JSONArray()
                            .put(new JSONObject()
                                    .put("system", "https://redatam.indec.gob.ar/argbin/RpWebEngine.exe/PortalAction?&MODE=MAIN&BASE=CPV2010B&MAIN=WebServerMain.inl&_ga=2.11744892.977170785.1640611973-17329590.1640611973")
                                    .put("code", code_category)
                                    .put("display", text_category)));
            obj.put("code", code);

            obj.put("effectiveDateTime", date);

            JSONArray performer = new JSONArray()
                    .put(new JSONObject()
                            .put("display", name_prof)
                            .put("identifier", new JSONArray().put(new JSONObject()
                                    .put("use", "official")
                                    .put("system", "RelevAr")
                                    .put("value", dni_prof))));
            obj.put("performer", performer);

            JSONObject valueCodeableConcept = new JSONObject()
                    .put("coding", new JSONArray().put(new JSONObject()
                            .put("system", "https://redatam.indec.gob.ar/argbin/RpWebEngine.exe/PortalAction?&MODE=MAIN&BASE=CPV2010B&MAIN=WebServerMain.inl&_ga=2.11744892.977170785.1640611973-17329590.1640611973")
                            .put("code", code_value)
                            .put("display", text_value)));
            obj.put("valueCodeableConcept", valueCodeableConcept);
        }catch(JSONException e){}
        return obj;
    }*/

    /*public JSONObject serviceRequestResourceFHIR(  String id,
                                                    String code_value,
                                                    String date,
                                                    String dni_prof,
                                                    String dni_patient){
        JSONObject obj = new JSONObject();

        try {
            obj.put("resourceType", "ServiceRequest");

            JSONArray identifier = new JSONArray();
            identifier.put(new JSONObject()
                    .put("system", "http://www.renaper.gob.ar/dni")
                    .put("value", id));
            obj.put("identifier", identifier);

            obj.put("status", "active");

            JSONObject code = new JSONObject()
                    .put("system", "https://browser.ihtsdotools.org/?")
                    .put("values", code_value);
            obj.put("code", code);

            obj.put("ocurrenceDateTime", date);

            JSONObject requester = new JSONObject()
                    .put("type","Practitioner")
                    .put("indentifier", dni_prof);
            obj.put("requester", requester);

            JSONObject encounter = new JSONObject()
                    .put("type", "Autotoma");
            obj.put("encounter", encounter);

            JSONObject subject = new JSONObject()
                    .put("reference", "http://www.renaper.gob.ar/dni")
                    .put("identifier", dni_patient);
            obj.put("subject",subject);

        }catch(JSONException e){}

        return obj;
    }

    public JSONObject locationResourceFHIR(    String id,
                                                String latitude_data,
                                                String longitude_data){
        JSONObject obj = new JSONObject();
        try {
            obj.put("resourceType", "Location");

            JSONArray identifier = new JSONArray();
            identifier.put(new JSONObject().put("value",id));
            obj.put("identifier", identifier);

            obj.put("position",new JSONObject()
                    .put("longitude", longitude_data)
                    .put("latiude", latitude_data));

        }catch(JSONException e){}
        return obj;
    }*/

    //Multiple Request: Bundle
    public JSONObject Bundle(ArrayList<JSONObject> entries){
        JSONObject bundle = new JSONObject();

        try {
            bundle.put("resourceType", "Bundle");
            bundle.put("type", "transaction");

            JSONArray entry = new JSONArray();
            for (int i=0; i<entries.size(); i++){

                entry.put(new JSONObject().put("resource", entries.get(i)));

            }

            bundle.put("entry",entry);

        }catch(JSONException e){}

        return bundle;
    }
}
