package com.beenet.jose.beenet;


import android.os.NetworkOnMainThreadException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by usuario on 31/01/2017.
 */
public class SendMethod {


    public static void sendMethodNR(final String url, final HashMap<String, String> paramPost){

        sendPost(url,paramPost);
    }
    public static void sendMethodNR(final String url){

        sendGet(url);
    }


    public static String sendMethod(final String url, final HashMap<String, String> paramPost)
    {



        String result=null;
        FutureTask<String> task = new FutureTask(new PostCall(url,paramPost));


        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit (task);
        try{
            result = task.get();
            System.out.println(result);
            return result;
        }
        catch(Exception e){
            System.err.println(e);
        }
        es.shutdown();



        return null;




    }


    public static String sendMethod(final String url)
    {
        String result=null;
        FutureTask<String> task = new FutureTask(new GetCall(url));


        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit (task);
        try{
            result = task.get();
            System.out.println(result);
        return result;
        }
        catch(Exception e){
            System.err.println(e);
        }
        es.shutdown();



    return null;



    }

    private static String sendPost(String urlCad, HashMap<String, String> paramPost){
        URL url;
        String response = "";

        HashMap<String, String> postDataParams = paramPost;

        try {
            url = new URL(urlCad);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }
    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder responseResult = new StringBuilder();
        boolean firstItem = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (firstItem)
                firstItem=false;
            else
                responseResult.append("&");

            responseResult.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            responseResult.append("=");
            responseResult.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return responseResult.toString();
    }

    private static String sendGet(String urlCad){
        String body  = "";
        URL url= null;
        HttpURLConnection urlConect = null; //inicializamos a null el objeto URLconnection.
        try{
            url= new URL(urlCad);

            urlConect = (HttpURLConnection)url.openConnection();

            urlConect.setRequestMethod("GET");


           body= readStream( urlConect.getInputStream());
            Log.d("resultado",body);


        }
        catch (MalformedURLException e){}
        catch (SocketTimeoutException e){}
        catch (NetworkOnMainThreadException es){}
        catch (Exception e){}
        finally{
            if(urlConect!=null)
                urlConect.disconnect();//liberamos la conexion si existe porque no es necesaria.

        }
        return body;
    }


    private static String readStream(InputStream in) throws IOException {

        BufferedReader r = null;
        r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        if(r != null){
            r.close();
        }
        in.close();
        return total.toString();
    }


    private static class PostCall implements Callable<String> {
        private String url;
        private HashMap<String, String> paramPost;
        public PostCall(String url, HashMap<String, String> paramPost){
            this.url=url;
            this.paramPost=paramPost;
        }
        @Override
        public String call() throws Exception {
            return  sendPost(url, paramPost);

        }
    }


    private static class GetCall implements Callable<String> {
        private String url;

        public GetCall(String url){
            this.url=url;

        }
        @Override
        public String call() throws Exception {
            return  sendGet(url);

        }
    }



}
