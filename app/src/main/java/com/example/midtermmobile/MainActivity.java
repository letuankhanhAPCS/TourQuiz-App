package com.example.midtermmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<MyLocation> locations, unlockedLocations;
    private ArrayList<MyAnswer> inputAnswer;
    private ArrayList<String> keyboardItem;
    private int currentLocation = 0, currentIndex = -1;

    private int[] locationPicture, questionPicture;

    private String[] keyboards;

    //private boolean[] isLockedList;

    private GridView gridViewAnswer, gridViewKeyboard;
    private MyAnswerAdapter answerAdapter;
    private MyKeyboardAdapter keyboardAdapter;
    private GridView.OnItemClickListener itemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            TextView textView = view.findViewById(R.id.textView_keyboard);

            do {
                currentIndex = currentIndex + 1;
            } while (inputAnswer.get(currentIndex).getCorrect() == ' ');

            inputAnswer.get(currentIndex).setInput(textView.getText());
            initComponent(R.layout.question_and_answer);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 44);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);

        keyboards = getResources().getStringArray(R.array.keyboards);

        loadPicture();
        loadData();

        initComponent(R.layout.question_and_answer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        String string = "";
        for(int i = 0; i < locations.size(); i++) {
            string = string + locations.get(i).isUnlock() + " ";
        }
        SaveData(string, "progress.txt");
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
            Toast.makeText(MainActivity.this,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        MenuItem item = menu.findItem(R.id.action_direction);
        item.setVisible(false);
        MenuItem home_Item = menu.findItem(R.id.action_home);
        home_Item.setVisible(false);
        MenuItem map_Item = menu.findItem(R.id.action_map);
        map_Item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_myCollection:
                Intent intent_collection = new Intent(getApplicationContext(), MyCollection.class);
                intent_collection.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                ArrayList<MyLocation> unlockedLocations = new ArrayList<>();

                for(MyLocation tmpLocation : locations)
                    if(tmpLocation.isUnlock()) unlockedLocations.add(tmpLocation);

                intent_collection.putExtra("locations", unlockedLocations);
                startActivity(intent_collection);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadPicture() {
        locationPicture = new int[] {
                R.drawable.picture_dinh_doc_lap,
                R.drawable.picture_nha_tho_duc_ba,
                R.drawable.picture_cho_ben_thanh,
                R.drawable.picture_pho_di_bo,
                R.drawable.picture_dia_dao_cu_chi,
                R.drawable.picture_bitexco,
                R.drawable.picture_bao_tang_ctct,
                R.drawable.picture_ben_nha_rong,
                R.drawable.picture_landmark,
                R.drawable.picture_nha_hat_tp
        };

        questionPicture = new int[] {
                R.drawable.question_dinh_doc_lap,
                R.drawable.question_nha_tho_duc_ba,
                R.drawable.question_cho_ben_thanh,
                R.drawable.question_pho_di_bo,
                R.drawable.question_dia_dao_cu_chi,
                R.drawable.question_bitexco,
                R.drawable.question_bao_tang_ctct,
                R.drawable.question_ben_nha_rong,
                R.drawable.question_landmark,
                R.drawable.question_nha_hat
        };
    }

    private void loadData() {
        locations = new ArrayList<>();
        for (int i = 0; i < locationPicture.length; i++) {
            String filename = "location" + i + ".txt";
            loadLocations(filename, i);
        }

        if(!fileExists(this, "progress.txt")) {
            String unlocked = "false false false false false false false false false false ";
            SaveData(unlocked, "progress.txt");
        }
        String[] unlocks = ReadData("progress.txt").split(" ");
        for(int i = 0; i < locations.size(); i++){
            boolean isLocked = Boolean.parseBoolean(unlocks[i]);
            System.out.println(i + unlocks[i]);
            locations.get(i).setUnlock(isLocked);
        }

        inputAnswer = new ArrayList<>();
        String myAnswer = locations.get(currentLocation).getAnswer();
        for (int i = 0; i < myAnswer.length(); i++){
            inputAnswer.add(new MyAnswer(" ", myAnswer.charAt(i)));
        }
    }

    private void loadLocations(String filename, int position) {
        String[] input = ReadDataFromAssert(filename).split(";");
        MyLocation location = new MyLocation(input[0], input[1], locationPicture[position],
                Double.parseDouble(input[2]), Double.parseDouble(input[3]), false,
                input[4], input[5], questionPicture[position]);
        locations.add(location);
    }

    private String ReadDataFromAssert(String filename) {
        try{
            InputStream inputStream = getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    private void SaveData(String string, String filename) {
        if(isExternalStorageAvailable()) {
            FileOutputStream fos;
            try {
                fos = openFileOutput(filename, Context.MODE_PRIVATE);
                fos.write(string.getBytes());
                //Toast.makeText(MainActivity.this, "Saving Successful", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "Saving - File not found Error", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Saving Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isExternalStorageAvailable() {
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        else return false;
    }

    private String ReadData(String filename) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();

            for (String text; (text = bufferedReader.readLine()) != null;) {
                stringBuilder.append(text);
            }
            //Toast.makeText(MainActivity.this, "Reading Successful", Toast.LENGTH_SHORT).show();
            return stringBuilder.toString();

        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, "Reading - File not found Error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Reading Error", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void initComponent(int layout) {
        LinearLayout placeHolder = (LinearLayout) findViewById(R.id.layoutQNA);
        getLayoutInflater().inflate(layout, placeHolder);
        getQuestionAndAnswer();
    }

    private void getQuestionAndAnswer() { //i fix this function
        MyLocation myLocation = locations.get(currentLocation);

        keyboardItem = new ArrayList<>();
        String[] strings = keyboards[currentLocation].split(",");
        for(int i = 0; i < strings.length; i++) {
            keyboardItem.add(strings[i]);
        }

        TextView question = findViewById(R.id.textView_question);
        question.setText(myLocation.getQuestionDescription());

        ImageView questionPic = findViewById(R.id.imageView_question);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), myLocation.getQuestionPicture());
        questionPic.setImageBitmap(bmp);

        if(myLocation.isUnlock())
        {
            TextView description = findViewById(R.id.textView_unlockAnswer);
            description.setText(myLocation.getLocationName());

            TextView pass = findViewById(R.id.textView_pass);
            pass.setText("You passed !");

            LinearLayout keyboard = findViewById(R.id.linearLayout_keyboard);
            keyboard.setBackgroundColor(getResources().getColor(R.color.color_BackgroundQNA));
        }
        else
        {
            gridViewAnswer = findViewById(R.id.gridView_answer);
            answerAdapter = new MyAnswerAdapter(this, R.layout.answer_item, inputAnswer);
            gridViewAnswer.setAdapter(answerAdapter);
            gridViewAnswer.setNumColumns(inputAnswer.size());

            gridViewKeyboard = findViewById(R.id.gridView_keyboard);
            keyboardAdapter = new MyKeyboardAdapter(this, R.layout.keyboard_item, keyboardItem);
            gridViewKeyboard.setAdapter(keyboardAdapter);
            gridViewKeyboard.setOnItemClickListener(itemClickListener);
        }
    }

    public void onClick_checkAnswer(View view) {
        setContentView(R.layout.activity_main);
        LinearLayout placeHolder = (LinearLayout) findViewById(R.id.layoutQNA);
        if(checkAnswer()) {
            getLayoutInflater().inflate(R.layout.answer_correct, placeHolder);
            locations.get(currentLocation).setUnlock(true);
            //isLockedList[currentLocation] = locations.get(currentLocation).isUnlock();
        }
        else
            getLayoutInflater().inflate(R.layout.answer_incorrect, placeHolder);
    }

    private boolean checkAnswer() {
        String answer = "";
        for (int i = 0; i < inputAnswer.size(); i++){
            answer = answer + inputAnswer.get(i).getInput();
        }
        if(answer.equals(locations.get(currentLocation).getAnswer()))
            return true;
        else
            return false;
    }

    public void onClick_prevQuestion(View view) {
        if(currentLocation == 0)
            currentLocation = locations.size() - 1;
        else {
            currentLocation = currentLocation - 1;
        }

        String myAnswer = locations.get(currentLocation).getAnswer();
        currentIndex = -1;
        inputAnswer = new ArrayList<>();
        for (int i = 0; i < myAnswer.length(); i++){
            inputAnswer.add(new MyAnswer(" ", myAnswer.charAt(i)));
        }

        setContentView(R.layout.activity_main);
        initComponent(R.layout.question_and_answer);
    }

    public void onClick_nextQuestion(View view) {
        if(currentLocation == locations.size() - 1)
            currentLocation = 0;
        else{
            currentLocation = currentLocation + 1;
        }

        String myAnswer = locations.get(currentLocation).getAnswer();
        currentIndex = -1;
        inputAnswer = new ArrayList<>();
        for (int i = 0; i < myAnswer.length(); i++){
            inputAnswer.add(new MyAnswer(" ", myAnswer.charAt(i)));
        }

        setContentView(R.layout.activity_main);
        initComponent(R.layout.question_and_answer);
    }

    public void onClickPlayAgain(View view) {
        setContentView(R.layout.activity_main);

        String myAnswer = locations.get(currentLocation).getAnswer();
        for (int i = 0; i < myAnswer.length(); i++){
            inputAnswer.get(i).setInput(" ");
        }

        currentIndex = -1;
        initComponent(R.layout.question_and_answer);
    }
}