package acompanar.ManagementModule.UbicationManagement;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Ubicacion {

    Context context;
    public Ubicacion(Context newContext){
        context = newContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkUbicacionGPS(ContentResolver contentResolver){
        //String provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_MODE);
        //String provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        //System.out.println("Provider contains=> " + provider);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return true;
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            return false;
            //showGPSDisabledAlertToUser();
        }
        /*if (provider.contains("gps")){//|| provider.contains("network")){
            return true;
        }
        return false;*/
    }

    public boolean funcionaServicioGPS(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public double DistanceMeters(double latitude1, double longitude1, double latitude2, double longitude2){
        double distance = 0.0;

        Location locationA = new Location("punto A");
        locationA.setLongitude(longitude1);
        locationA.setLatitude(latitude1);
        Location locationB = new Location("punto B");
        locationB.setLongitude(longitude2);
        locationB.setLatitude(latitude2);

        distance = locationA.distanceTo(locationB);
        return distance;
    }

}
