package com.example.stud.quantityretainer.Utilyties;

public class RecordsProvider {
    static String topics[] = {"one", "two", "three", "fore", "five", "six", "seven", "eight", "nine",
            "one", "two", "three", "fore", "five", "six", "seven", "eight", "nine",
            "one", "two", "three", "fore", "five", "six", "seven", "eight", "nine"};

    public int getRecordsCount() {
        return topics.length;
    }

    public String getRecord(int position) {
        if (position < 0 || position >= topics.length) {
            return "";
        }

        return topics[position];
    }
}
