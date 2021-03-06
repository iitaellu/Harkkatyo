package com.example.harjoitustyo_ida_viia;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class FoodFragment extends Fragment {

    String person;

    TextView textTotal;
    Spinner spinnerPrefer, spinnerDiet;
    EditText beefLevel, fishLevel, pork_PoultryLevel, dairyLevel, cheeseLevel, riceLevel, eggLevel, saladLevel, restaurantLevel;
    String date;
    String name = ".FoodCounter.csv";
    String result = null;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        person = fAuth.getCurrentUser().getUid();

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());
        SimpleDateFormat format = new SimpleDateFormat("d.M.yyyy");
        date = format.format(Date.parse(currentDate));

        makeFile(person);

        beefLevel = (EditText) rootView.findViewById(R.id.beefLevel);
        fishLevel = (EditText) rootView.findViewById(R.id.fishLevel);
        pork_PoultryLevel = (EditText) rootView.findViewById(R.id.pork_PoultryLevel);
        dairyLevel = (EditText) rootView.findViewById(R.id.dairyLevel);
        cheeseLevel = (EditText) rootView.findViewById(R.id.cheeseLevel);
        riceLevel = (EditText) rootView.findViewById(R.id.riceLevel);
        eggLevel = (EditText) rootView.findViewById(R.id.eggLevel);
        saladLevel = (EditText) rootView.findViewById(R.id.saladLevel);
        restaurantLevel = (EditText) rootView.findViewById(R.id.restaurantLevel);

        spinnerPrefer = (Spinner) rootView.findViewById(R.id.spinnerPrefer);
        spinnerDiet = (Spinner) rootView.findViewById(R.id.spinnerDiet);

        textTotal = (TextView) rootView.findViewById(R.id.textTotal);


        Button calculateButton = (Button) rootView.findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String prefer = spinnerPrefer.getSelectedItem().toString();
                String diet = spinnerDiet.getSelectedItem().toString();

                String beef = beefLevel.getText().toString();
                String fish = fishLevel.getText().toString();
                String pork_poultry = pork_PoultryLevel.getText().toString();
                String dairy = dairyLevel.getText().toString();
                String cheese = cheeseLevel.getText().toString();
                String rice = riceLevel.getText().toString();
                String egg = eggLevel.getText().toString();
                String salad = saladLevel.getText().toString();
                String restaurant = restaurantLevel.getText().toString();

                if (beef.equals("")) {
                    beef = "0";
                }
                if (fish.equals("")) {
                    fish = "0";
                }
                if (pork_poultry.equals("")) {
                    pork_poultry = "0";
                }
                if (dairy.equals("")) {
                    dairy = "0";
                }
                if (cheese.equals("")) {
                    cheese = "0";
                }
                if (rice.equals("")) {
                    rice = "0";
                }
                if (egg.equals("")) {
                    egg = "0";
                }
                if (salad.equals("")) {
                    salad = "0";
                }
                if (restaurant.equals("")) {
                    restaurant = "0";
                }

                try {

                    result = total(diet, prefer, beef, fish, pork_poultry, dairy, cheese, rice, egg, salad, restaurant);
                    textTotal.setText(result+" kg CO2/week");
                    writeFile(result, person);
                    Toast.makeText(getActivity(),"Saved",Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    // This method read url-page and find total/week

    public String total(String diet, String prefer, String beef, String fish, String pork_poultry, String dairy, String cheese, String rice, String egg, String salad, String restaurant) throws IOException{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String url ="https://ilmastodieetti.ymparisto.fi/ilmastodieetti/calculatorapi/v1/FoodCalculator?query.diet="
                +diet+"&query.lowCarbonPreference="+prefer+"&query.beefLevel="+beef+"&query.fishLevel="
                +fish+"&query.porkPoultryLevel="+pork_poultry+"&query.dairyLevel="+dairy+"&query.cheeseLevel="+cheese
                +"&query.riceLevel=" +rice+"&query.eggLevel="+egg+"&query.winterSaladLevel="+salad+"&query.restaurantSpending="+restaurant;
        URL page = new URL(url);
        Scanner scan = new Scanner(page.openStream());
        StringBuffer buffer = new StringBuffer();
        while (scan.hasNext()){
            buffer.append(scan.next());
        }
        String result = buffer.toString();

        String[] list = result.split(":");
        String wanted = list[5];
        wanted = wanted.replace("}", "");
        double total = Double.parseDouble(wanted);
        total = total/52;
        String resulttotal = String.valueOf(total);
        resulttotal = String.format("%.2f", total);


        return resulttotal;
    }
    //This method make file, if there is not one yet
    public void makeFile(String person) {
        try {
            String content = "Date;kg CO2/week\n";
            File file = new File(getActivity().getFilesDir().getPath() + "/" + person+name);

            if (!file.exists()) {
                file.createNewFile();
                System.out.println(file);
                OutputStreamWriter writer = new OutputStreamWriter(getActivity().openFileOutput(person+name, Context.MODE_PRIVATE));
                writer.write(content);
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //This method write data in file.
    public void writeFile(String total, String person) {
        try (FileWriter fw = new FileWriter(getActivity().getFilesDir().getPath() + "/" + person+name, true)) {
            BufferedWriter writer = new BufferedWriter(fw);
            writer.append(date+ ";"+ total + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
