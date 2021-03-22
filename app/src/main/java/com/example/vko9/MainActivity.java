package com.example.vko9;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Spinner theaters;
    private TextView date_text;
    private TextView start_time_text;
    private TextView end_time_text;
    private ListView movie_lv;


    private ArrayList<Theatre> theatre_list = new ArrayList();
    private ArrayList<Movie> movie_list = new ArrayList();

    private int selectedTheatreIndex;
    private String date_given_by_user;
    private int timer_selected;
    private int start_time = 9999;
    private int end_time = 9999;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connecting the variables with IDs

        theaters = (Spinner) findViewById(R.id.spinner);
        date_text = (TextView) findViewById(R.id.date);
        start_time_text = (TextView) findViewById(R.id.start_time_text);
        end_time_text = (TextView) findViewById(R.id.end_time_text);
        movie_lv = (ListView) findViewById(R.id.movies);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        this.readXML();


        ArrayAdapter<Theatre> adapter = new ArrayAdapter<Theatre>(this, android.R.layout.simple_spinner_item, theatre_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theaters.setAdapter(adapter);
        theaters.setOnItemSelectedListener(this);
    }

    //XML

    public void readXML(){
        String name;
        String id;

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            String url = "https://www.finnkino.fi/xml/TheatreAreas/";
            Document doc = builder.parse(url);

            doc.getDocumentElement().normalize();
            //date_text.setText(doc.getDocumentElement().getNodeName());

            NodeList node_list = doc.getElementsByTagName("TheatreArea");


            System.out.println("moi");

            //adding theatres to theatre_list
            for (int i = 1 ; i < node_list.getLength() ; i++){
                Node node = (Node) node_list.item(i);
                if(node.getNodeType() == node.ELEMENT_NODE){

                    Element element = (Element) node;
                    if(element.getElementsByTagName("ID").item(0) != null && element.getElementsByTagName("Name").item(0) != null) {

                        //debug
                        System.out.println(element.getElementsByTagName("ID").item(0).getTextContent());
                        System.out.println(element.getElementsByTagName("Name").item(0).getTextContent());


                        id = element.getElementsByTagName("ID").item(0).getTextContent();
                        name = element.getElementsByTagName("Name").item(0).getTextContent();
                        Theatre theatre = new Theatre(id, name);
                        theatre_list.add(theatre);
                    }
                }
            }




        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //selecting theatre
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedTheatreIndex = position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    //setting date
    public void pickDate(View v){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, this, year, month, day);
        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String m;
        String d;
        month++;
        if(month < 10){
            m = "0" + month;
        } else {
            m = Integer.toString(month);
        }

        if(dayOfMonth < 10){
            d = "0" + dayOfMonth;
        } else {
            d = Integer.toString(dayOfMonth);
        }

        //date_text.setText(dayOfMonth  + "." + month + "." + year);

        date_given_by_user = d + "." + m + "." + year;
    }

    public void startTime(View v){
        timer_selected = 0;
        this.pickTime();

    }

    public void endTime(View v){
        timer_selected = 1;
        this.pickTime();

    }

    //setting time
    public void pickTime(){
        TimePickerDialog tpd = new TimePickerDialog(this, this, 12, 12, true);
        tpd.show();
    }


    //this gives the time
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String formatter = "";
        String formatter2 = "";

        String output, output2;

        if (hourOfDay < 10){
            formatter = "0";
        }
        if (minute < 10){
            formatter2 = "0";
        }

        output = formatter + hourOfDay + ":" + formatter2 + minute;
        output2 = formatter + hourOfDay + formatter2 + minute;

        //if 0, sets start_time, if 1, sets end_time
        if(timer_selected == 0){
            start_time = Integer.parseInt(output2);
            start_time_text.setText(output);

        } else {
            end_time = Integer.parseInt(output2);
            end_time_text.setText(output);
        }
    }






    //searching for the movies
    public void search(View v){
        //date_text.setText(theatre_list.get(selectedTheatreIndex).getId());
        String id = theatre_list.get(selectedTheatreIndex).getId();
        String title;
        String time;



        movie_list.clear();

        try {

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();





            String url = "https://www.finnkino.fi/xml/Schedule/?area=" + id + "&dt=" + date_given_by_user;
            //date_text.setText(url);
            Document doc = builder.parse(url);

            doc.getDocumentElement().normalize();
            //date_text.setText(doc.getDocumentElement().getNodeName());

            NodeList node_list = doc.getElementsByTagName("Show");




            //adding movies to movie_list
            for (int i = 0 ; i < node_list.getLength() ; i++){
                Node node = (Node) node_list.item(i);
                if(node.getNodeType() == node.ELEMENT_NODE){

                    Element element = (Element) node;
                    if(element.getElementsByTagName("dttmShowStart").item(0) != null && element.getElementsByTagName("Title").item(0) != null) {



                        time = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();
                        title = element.getElementsByTagName("Title").item(0).getTextContent();
                        Movie movie = new Movie(title, time);

                        /*
                        start_time_text.setText(Integer.toString(start_time));
                        end_time_text.setText(Integer.toString(end_time));
                        */

                        if(start_time != 9999){
                            if(start_time > movie.getHrs()){
                                //start_time_text.setText(Integer.toString(start_time));
                                continue;

                            }
                        }

                        if(end_time != 9999){
                            if(end_time < movie.getHrs()){
                                //end_time_text.setText(Integer.toString(end_time));
                                continue;
                            }
                        }



                        movie_list.add(movie);
                    }
                }
            }

            ArrayAdapter<Movie> arrayAdapter = new ArrayAdapter<Movie>(this, android.R.layout.simple_list_item_1, movie_list);

            movie_lv.setAdapter(arrayAdapter);




        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}



