package com.harrysilman.fortuneteller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import java.util.Random;


public class MainActivity extends Activity {

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    //accessing textViews programmatically
    public TextView answerTextView;  //displays fortune
    public EditText questionEditText; //accepts user input for question

    //intialize variables
    private String[] fortuneList; //fortunes to choose randomly
    Random rand = new Random(); //random object
    private boolean doneCheck; //checks for "done" being clicked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //capture the answerTextView and questionCompleteButton from layout
        answerTextView = (TextView)findViewById(R.id.answerTextView);
        questionEditText = (EditText)findViewById(R.id.questionEditText);

        //capture the two buttons from layout
        Button doneButton = (Button)findViewById(R.id.questionCompleteButton);
        Button resetButton = (Button)findViewById(R.id.resetButton);

        //register the onClick listener for buttons
        doneButton.setOnClickListener(doneButtonListener);
        resetButton.setOnClickListener(resetButtonListener);

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            //instructs program on what to do when device is shaken
            @Override
            public void onShake(int count) {
                //generate fortune on shake if done button has been clicked
                if (doneCheck) {
                    int random = rand.nextInt(fortuneList.length);
                    answerTextView.setText(fortuneList[random]);
                }

                //reset bool so only one fortune is generated on shake
                doneCheck = false;
            }
        });



    }

    //what happens on clicking the "done?" button
    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(!doneCheck) {
                //Toast message telling user to shake their device
                Toast toast = Toast.makeText(getApplicationContext(), "SHAKE YOUR DEVICE!!!!", Toast.LENGTH_LONG);
                toast.show();

                //set doneCheck to true to enable fortune generation on shake
                doneCheck = true;


                //populate fortuneList with fortune_list array resource
                fortuneList = getResources().getStringArray(R.array.fortune_list);
            }
        }
    };

    //what happens on clicking the "Reset" button
    private View.OnClickListener resetButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            //clear questionEditText and answerText
            questionEditText.setText("");
            answerTextView.setText(R.string.answer_text_default);

            //reset fortuneList
            fortuneList = null;

            //reset doneCheck toggle
            doneCheck = false;

        }
    };

    //re-initialize accelerometer on resume
    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    //stop accelerometer when app is in the background
    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
