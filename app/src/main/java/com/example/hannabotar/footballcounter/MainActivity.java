package com.example.hannabotar.footballcounter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

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

    private Long time = 0L;
    private Long limit = 5001L; // 45min, 90min, extra time
    Thread t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayForTeamA(scoreTeamA);
        displayForTeamB(scoreTeamB);
        Button showEvolutionButton = (Button) findViewById(R.id.show_hide_button);
        showEvolutionButton.setEnabled(false);

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(new File()))
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

    public void displayEvolution() {

        // show evolution in dialog
        StringBuilder evolution = new StringBuilder();
        for (String time : gameEvolution.keySet()) {
            evolution.append(time).append("     ").append(gameEvolution.get(time)).append("\n");
        }
        Bundle data = new Bundle();
        data.putString(String.valueOf(R.string.evolution_string), evolution.toString());

        EvolutionDialogFragment dialog = new EvolutionDialogFragment();
        dialog.setArguments(data);
        dialog.show(getSupportFragmentManager(), "EvolutionDialogFragment");
    }

    public void updateGameEvolution(String team, int points) {
        Date date = new Date();

        String timeString = SDF.format(date);

        long millis = time;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String curTime = String.format("%02d : %02d", minutes, seconds);

//        gameEvolution.put(timeString, team + " - " + points);
        gameEvolution.put(curTime, team + "  -  " + points);
        Button showEvolutionButton = (Button) findViewById(R.id.show_hide_button);
        showEvolutionButton.setEnabled(true);
    }

    public void addGoalA(View view) {
        scoreTeamA = scoreTeamA + ONE_POINT;
        scoreListA.add(ONE_POINT);
        displayForTeamA(scoreTeamA);
        updateGameEvolution("Team A", ONE_POINT);
    }
    public void addFoulA(View view) {
        System.out.println("foul_a pressed");
    }
    public void addYellowA(View view) {
        System.out.println("yellow_a pressed");
    }
    public void addRedA(View view) {
        System.out.println("red_a pressed");
    }
    public void addGoalB(View view) {
        scoreTeamB = scoreTeamB + ONE_POINT;
        scoreListB.add(ONE_POINT);
        displayForTeamB(scoreTeamB);
        updateGameEvolution("Team B", ONE_POINT);
    }
    public void addFoulB(View view) {
        System.out.println("foul_b pressed");
    }
    public void addYellowB(View view) {
        System.out.println("yellow_b pressed");
    }
    public void addRedB(View view) {
        System.out.println("red_b pressed");
    }

    public void showOrHideEvolution(View view) {
        if (!gameEvolution.isEmpty()) {
            displayEvolution();
        }
    }

    public void resetScore(View view) {
        ResetDialogFragment dialog = new ResetDialogFragment();
        dialog.show(getSupportFragmentManager(), "ResetDialogFragment");
    }

    public void resetConfirmed() {
        stopTimer();

        scoreTeamA = 0;
        scoreTeamB = 0;
        displayForTeamA(scoreTeamA);
        displayForTeamB(scoreTeamB);
        gameEvolution.clear();

        scoreListA = new ArrayList<>();
        scoreListB = new ArrayList<>();
        limit = 5001L;
    }

    public void stopTimer() {
        time = 0L;
        if (t != null) {
            t.interrupt();
        }

        TextView timer = (TextView) findViewById(R.id.timer);
        timer.setText(R.string.initial_time);
        final Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.VISIBLE);
    }


    public void startTimer(View view) {
        final TextView timer = (TextView) findViewById(R.id.timer);
        final Long startTime = System.currentTimeMillis() - time;
        final Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.INVISIBLE);
        t = new Thread() {

            @Override
            public void run() {
                try {
//                    while (!isInterrupted() && time < 2700001) {
                    while (!isInterrupted() && time < limit) {
//                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long millis = System.currentTimeMillis() - startTime;
                                int seconds = (int) (millis / 1000);
                                int minutes = seconds / 60;
                                seconds = seconds % 60;

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
                            limit = 10001L;
                        }
                    });

                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }
}
