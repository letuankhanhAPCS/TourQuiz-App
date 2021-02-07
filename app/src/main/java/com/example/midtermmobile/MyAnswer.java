package com.example.midtermmobile;

public class MyAnswer {
    CharSequence input;
    char correct;

    public MyAnswer(CharSequence input, char correct) {
        this.input = input;
        this.correct = correct;
    }

    public CharSequence getInput() {
        return input;
    }

    public void setInput(CharSequence input) {
        this.input = input;
    }

    public char getCorrect() {
        return correct;
    }

    public void setCorrect(char correct) {
        this.correct = correct;
    }
}
