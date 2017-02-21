package com.beenet.jose.beenet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {
    public Receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Servicio iniciado desde BroadcastReceiver", Toast.LENGTH_SHORT).show();
        //context.startService(new Intent(context, MyService.class));
    }
}
