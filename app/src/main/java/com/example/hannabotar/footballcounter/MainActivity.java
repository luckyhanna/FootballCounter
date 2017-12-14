package com.example.hannabotar.footballcounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    // TODO: add timer
    // TODO: keep track of goals and show the game's evolution:
    //  01:03 - Team A - 3
    //  03:12 - Team B - 3
    //  03:43 - Team A - 2
    //  04:13 - Team A - 2

    public static final int THREE_POINTS = 3;
    public static final int TWO_POINTS = 2;
    public static final int ONE_POINT = 1;

    public static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

    private int scoreTeamA; // no need to initialize
    private int scoreTeamB;

    private List<Integer> scoreListA = new ArrayList<>();
    private List<Integer> scoreListB = new ArrayList<>();
    private SortedMap<String, String> gameEvolution = new TreeMap<>();
    private boolean evolutionVisible = false;

    private Long time = 0L;
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayForTeamA(scoreTeamA);
        displayForTeamB(scoreTeamB);
    }

    /**
     * Displays the given score for Team A.
     */
    public void displayForTeamA(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_a_score);
        scoreView.setText(String.valueOf(score));
    }

    /**
     * Displays the given score for Team B.
     */
    public void displayForTeamB(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_b_score);
        scoreView.setText(String.valueOf(score));
    }

    public void displayEvolution(boolean visible) {
        TextView evolutionView = (TextView) findViewById(R.id.game_evolution);
        if (visible) {
            StringBuilder evolution = new StringBuilder();
            for (String time : gameEvolution.keySet()) {
                evolution.append(time).append("   ").append(gameEvolution.get(time)).append("\n");
            }
            evolutionView.setText(evolution.toString());
            ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
            scrollView.fullScroll(View.FOCUS_DOWN);
//            scrollView.scrollTo(0, scrollView.getHeight());
        } else {
            evolutionView.setText("");
        }
        Button button = (Button) findViewById(R.id.show_hide_button);
        if (evolutionVisible) {
            button.setText(R.string.hide_evolution);
        } else {
            button.setText(R.string.show_evolution);
        }

    }

    public void updateGameEvolution(String team, int points) {
        Date date = new Date();

        String timeString = SDF.format(date);
        gameEvolution.put(timeString, team + " - " + points);
    }

    public void addThreePointsA(View view) {
        scoreTeamA = scoreTeamA + THREE_POINTS;
        scoreListA.add(THREE_POINTS);
        displayForTeamA(scoreTeamA);
        updateGameEvolution("Team A", THREE_POINTS);
        displayEvolution(evolutionVisible);
    }

    public void addTwoPointsA(View view) {
        scoreTeamA = scoreTeamA + TWO_POINTS;
        scoreListA.add(TWO_POINTS);
        displayForTeamA(scoreTeamA);
        updateGameEvolution("Team A", TWO_POINTS);
        displayEvolution(evolutionVisible);
    }

    public void addFreeThrowA(View view) {
        scoreTeamA = scoreTeamA + ONE_POINT;
        scoreListA.add(ONE_POINT);
        displayForTeamA(scoreTeamA);
        updateGameEvolution("Team A", ONE_POINT);
        displayEvolution(evolutionVisible);
    }

    public void addThreePointsB(View view) {
        scoreTeamB = scoreTeamB + THREE_POINTS;
        scoreListB.add(THREE_POINTS);
        displayForTeamB(scoreTeamB);
        updateGameEvolution("Team B", THREE_POINTS);
        displayEvolution(evolutionVisible);
    }

    public void addTwoPointsB(View view) {
        scoreTeamB = scoreTeamB + TWO_POINTS;
        scoreListB.add(TWO_POINTS);
        displayForTeamB(scoreTeamB);
        updateGameEvolution("Team B", TWO_POINTS);
        displayEvolution(evolutionVisible);
    }

    public void addFreeThrowB(View view) {
        scoreTeamB = scoreTeamB + ONE_POINT;
        scoreListB.add(ONE_POINT);
        displayForTeamB(scoreTeamB);
        updateGameEvolution("Team B", ONE_POINT);
        displayEvolution(evolutionVisible);
    }

    public void showOrHideEvolution(View view) {
        if (!gameEvolution.isEmpty()) {
            evolutionVisible = !evolutionVisible;
            displayEvolution(evolutionVisible);
        }




    }

    public void resetScore(View view) {
        stopTimer(view);

        scoreTeamA = 0;
        scoreTeamB = 0;
        displayForTeamA(scoreTeamA);
        displayForTeamB(scoreTeamB);
        gameEvolution.clear();
        evolutionVisible = false;
        displayEvolution(false);
        scoreListA = new ArrayList<>();
        scoreListB = new ArrayList<>();
    }

    public void stopTimer(View view) {
        time = 0L;
        t.interrupt();
        TextView timer = (TextView) findViewById(R.id.timer);
        timer.setText("00 : 00");
        final Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.VISIBLE);
    }


    public void startTimer(View view) {
        final TextView timer = (TextView) findViewById(R.id.timer);
        final Long startTime = System.currentTimeMillis() + time;
        final Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.INVISIBLE);
        t = new Thread() {

            @Override
            public void run() {
                try {
//                    while (!isInterrupted() && time < 2700001) {
                    while (!isInterrupted() && time < 5001) {
//                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long millis = System.currentTimeMillis() - startTime;
                                int seconds = (int) (millis / 1000);
                                int minutes = seconds / 60;
                                seconds     = seconds % 60;

                                time = millis;
                                String curTime = String.format("%02d : %02d", minutes, seconds);
                                timer.setText(curTime); //change clock to your textview
                            }
                        });
                        Thread.sleep(1000);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t.interrupt();

                            startButton.setVisibility(View.VISIBLE);
                            startButton.setText("start 2nd half");
                        }
                    });

                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }
}
