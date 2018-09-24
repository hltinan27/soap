package com.example.inan.soaptest;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final String NAMESPACE = "https://www.w3schools.com/xml/";  //SONDAKİ '/' ÖNEMLİDİR!
    final String URL= "https://www.w3schools.com/xml/tempconvert.asmx";   //'?' alanından öncesini alıyoruz.
    final String SOAP_ACTION = "https://www.w3schools.com/xml/CelsiusToFahrenheit"; //soap action alanının tamamını alıyoruz.
    final String METHOD_NAME= "CelsiusToFahrenheit";  //namespace + methodname = soap_Action

    EditText edtGiris;
    Button btnConvert;
    TextView txtSonuc;
    String celcius;  //istek
    String fahrenheit;  //dönen cevap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtGiris=(EditText)findViewById(R.id.edtGiris);
        btnConvert=(Button)findViewById(R.id.btnConvert);
        txtSonuc=(TextView)findViewById(R.id.tvSonuc);

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtGiris.getText().toString() !=null && edtGiris.getText().length() != 0){
                    celcius=edtGiris.getText().toString();
                    //asynctask.execute;
                    AsyncCallsWS asyncCallsWS = new AsyncCallsWS();
                    asyncCallsWS.execute();
                }
                else{
                    txtSonuc.setText("Lütfen celsius değerini giriniz...");
                }
            }
        });
    }
    //asyntask islemi
    private class AsyncCallsWS extends AsyncTask<String,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtSonuc.setText("Hesaplanıyor");
        }

        @Override
        protected Void doInBackground(String... objects) {  //result olarak ws den bize String döneceği için String[] yazdık.
            getFahrenheit(celcius);
            return null;
        }
        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            txtSonuc.setText(fahrenheit + " F");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public void getFahrenheit(String celsius){
        SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
        PropertyInfo celsiusPi = new PropertyInfo();
        celsiusPi.setName("Celsius");  //mesajı gonderirken kullandıgımız etiket
        celsiusPi.setValue(celsius);  //etiketin içerisindeki değer alanına da yukarıda kodda tanımlamış oldugumuz değeri gir.
        celsiusPi.setType(double.class);  //gonderilen verinin tipi
        request.addProperty(celsiusPi);  //soap request e olusturdugumuz property ekle.
        //request i soap mektubumuzun içerisine ekleme;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        envelope.setOutputSoapObject(request);
        //WS'ye gönderme kısmı;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {
            androidHttpTransport.call(SOAP_ACTION,envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();  //Ws den dönen cevap.
            fahrenheit = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
