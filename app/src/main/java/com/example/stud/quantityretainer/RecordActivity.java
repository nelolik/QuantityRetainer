package com.example.stud.quantityretainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RecordActivity extends AppCompatActivity {
    public static final String TAG_NAME = "RETENTION_NAME";
    public static final String TAG_TABLE = "TABLE_NAME";

    TextView mTotalText;
    TextView mTotalCount;
    EditText mAddCount;
    Button mAddButton;
    private String retentionName;
    private String tableName;

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
        if (intent.hasExtra(TAG_NAME)) {
            retentionName = intent.getStringExtra(TAG_NAME);
            getSupportActionBar().setTitle(retentionName);
        }
        if (intent.hasExtra(TAG_TABLE)) {
            tableName = intent.getStringExtra(TAG_TABLE);

        }


        mAddCount.setText("0");

    }
}
