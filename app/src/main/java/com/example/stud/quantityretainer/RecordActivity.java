package com.example.stud.quantityretainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RecordActivity extends AppCompatActivity {
    public static final String TEXT_TAG = "CENTRAL_TEXT";

    TextView mTotalText;
    TextView mTotalCount;
    EditText mAddCount;
    Button mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mTotalText = findViewById(R.id.total_text);
        mTotalCount = findViewById(R.id.total_count);
        mAddCount = findViewById(R.id.add_count_input);
        mAddButton = findViewById(R.id.btn_add);

        Intent intent = getIntent();

        mTotalText.setText("Total count:");
        if (intent.hasExtra(TEXT_TAG)) {
            mTotalCount.setText(intent.getStringExtra(TEXT_TAG));
        }

        mAddCount.setText("500");

    }
}
