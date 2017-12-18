package com.example.hannabotar.footballcounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    public static final String FOUL = "Foul";
    public static final String YELLOW_CARD = "Yellow card";
    public static final String RED_CARD = "Red card";

    public static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

    private int scoreTeamA; // no need to initialize
    private int foulTeamA;
    private int yellowTeamA;
    private int redTeamA;
    private int scoreTeamB;
    private int foulTeamB;
    private int yellowTeamB;
    private int redTeamB;


    private List<Integer> scoreListA = new ArrayList<>();
    private List<Integer> scoreListB = new ArrayList<>();
    private SortedMap<String, String> gameEvolution = new TreeMap<>();

    private Long time = 0L;
    private Long limit = 15001L; // 45min + stoppage/injury time, 90min + stoppage/injury time, 2x15min extra time, penalty
    Thread t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayForTeamA(scoreTeamA);
        displayFoulsForTeamA(foulTeamA);
        displayYellowCardsForTeamA(yellowTeamA);
        displayRedCardsForTeamA(redTeamA);

        displayForTeamB(scoreTeamB);
        displayFoulsForTeamB(foulTeamB);
        displayYellowCardsForTeamB(yellowTeamB);
        displayRedCardsForTeamB(redTeamB);

        enableActionButtons(false);

        Button showEvolutionButton = (Button) findViewById(R.id.show_hide_button);
        showEvolutionButton.setEnabled(false);
    }

    /**
     * This method enables or disabled a given button
     * @param enabled true/false
     * @param id of the button
     */
    private void enableButton(boolean enabled, int id) {
        Button button = (Button) findViewById(id);
        button.setEnabled(enabled);
    }

    /**
     * This method enables or disabled all of the action buttons (goal, foul, yellow card, red card)
     * @param enabled true/false
     */
    private void enableActionButtons(boolean enabled) {
        enableButton(enabled, R.id.goal_button_a);
        enableButton(enabled, R.id.goal_button_b);
        enableButton(enabled, R.id.foul_button_a);
        enableButton(enabled, R.id.foul_button_b);
        enableButton(enabled, R.id.yellow_button_a);
        enableButton(enabled, R.id.yellow_button_b);
        enableButton(enabled, R.id.red_button_a);
        enableButton(enabled, R.id.red_button_b);
    }

    /**
     * Displays the given score for Team A.
     */
    public void displayForTeamA(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_a_score);
        scoreView.setText(String.valueOf(score));
    }
    public void displayFoulsForTeamA(int fouls) {
        TextView foulView = (TextView) findViewById(R.id.foul_text_a);
        foulView.setText(String.valueOf(fouls));
    }
    public void displayYellowCardsForTeamA(int yellowCards) {
        TextView yellowCardView = (TextView) findViewById(R.id.yellow_text_a);
        yellowCardView.setText(String.valueOf(yellowCards));
    }
    public void displayRedCardsForTeamA(int redCards) {
        TextView redCardView = (TextView) findViewById(R.id.red_text_a);
        redCardView.setText(String.valueOf(redCards));
    }

    /**
     * Displays the given score for Team B.
     */
    public void displayForTeamB(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_b_score);
        scoreView.setText(String.valueOf(score));
    }
    public void displayFoulsForTeamB(int fouls) {
        TextView foulView = (TextView) findViewById(R.id.foul_text_b);
        foulView.setText(String.valueOf(fouls));
    }
    public void displayYellowCardsForTeamB(int yellowCards) {
        TextView yellowCardView = (TextView) findViewById(R.id.yellow_text_b);
        yellowCardView.setText(String.valueOf(yellowCards));
    }
    public void displayRedCardsForTeamB(int redCards) {
        TextView redCardView = (TextView) findViewById(R.id.red_text_b);
        redCardView.setText(String.valueOf(redCards));
    }

    public void displayEvolution() {

        // show evolution in dialog
        StringBuilder evolution = new StringBuilder();
        evolution.append("<div>");
        for (String time : gameEvolution.keySet()) {
//            evolution.append(time).append("     ").append(gameEvolution.get(time)).append("\n");
//            evolution.append("<li>").append(time).append("<b>").append(gameEvolution.get(time)).append("</b>").append("</li>");
            evolution.append("<div>").append(time).append(" - <b><font color='#f39c12'>").append(gameEvolution.get(time)).append("</font></b>").append("</div>");
        }
        evolution.append("</div>");
        Bundle data = new Bundle();
        data.putString(String.valueOf(R.string.evolution_string), evolution.toString());

        EvolutionDialogFragment dialog = new EvolutionDialogFragment();
        dialog.setArguments(data);
        dialog.show(getSupportFragmentManager(), "EvolutionDialogFragment");
    }

    public void updateGameEvolution(String team, String detail) {
        Date date = new Date();

        String timeString = SDF.format(date);

        long millis = time;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String curTime = String.format("%02d : %02d", minutes, seconds);

        String value = team + "  -  " + detail;

        gameEvolution.put(curTime, value);
        Button showEvolutionButton = (Button) findViewById(R.id.show_hide_button);
        showEvolutionButton.setEnabled(true);
    }

    public void addGoalA(View view) {
        scoreTeamA = scoreTeamA + ONE_POINT;
        scoreListA.add(ONE_POINT);
        displayForTeamA(scoreTeamA);
        updateGameEvolution("Team A", String.valueOf(ONE_POINT));
    }
    public void addFoulA(View view) {
        foulTeamA = foulTeamA + ONE_POINT;
        displayFoulsForTeamA(foulTeamA);
        updateGameEvolution("Team A", FOUL);
    }
    public void addYellowA(View view) {
        yellowTeamA = yellowTeamA + ONE_POINT;
        displayYellowCardsForTeamA(yellowTeamA);
        updateGameEvolution("Team A", YELLOW_CARD);
    }
    public void addRedA(View view) {
        redTeamA = redTeamA + ONE_POINT;
        displayRedCardsForTeamA(redTeamA);
        updateGameEvolution("Team A", RED_CARD);
    }
    public void addGoalB(View view) {
        scoreTeamB = scoreTeamB + ONE_POINT;
        scoreListB.add(ONE_POINT);
        displayForTeamB(scoreTeamB);
        updateGameEvolution("Team B", String.valueOf(ONE_POINT));
    }
    public void addFoulB(View view) {
        foulTeamB = foulTeamB + ONE_POINT;
        displayFoulsForTeamB(foulTeamB);
        updateGameEvolution("Team B", FOUL);
    }
    public void addYellowB(View view) {
        yellowTeamB = yellowTeamB + ONE_POINT;
        displayYellowCardsForTeamB(yellowTeamB);
        updateGameEvolution("Team B", YELLOW_CARD);
    }
    public void addRedB(View view) {
        redTeamB = redTeamB + ONE_POINT;
        displayRedCardsForTeamB(redTeamB);
        updateGameEvolution("Team B", RED_CARD);
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

        foulTeamA = 0;
        foulTeamB = 0;
        displayFoulsForTeamA(foulTeamA);
        displayFoulsForTeamB(foulTeamB);

        yellowTeamA = 0;
        yellowTeamB = 0;
        displayYellowCardsForTeamA(yellowTeamA);
        displayYellowCardsForTeamB(yellowTeamB);

        redTeamA = 0;
        redTeamB = 0;
        displayRedCardsForTeamA(redTeamA);
        displayRedCardsForTeamB(redTeamB);

        gameEvolution.clear();

        scoreListA = new ArrayList<>();
        scoreListB = new ArrayList<>();
        limit = 15001L;
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

        enableActionButtons(false);
    }


    public void startTimer(View view) {
        final TextView timer = (TextView) findViewById(R.id.timer);
        final Long startTime = System.currentTimeMillis() - time;
        final Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.INVISIBLE);

        enableActionButtons(true);

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
                            enableActionButtons(false);

                            startButton.setVisibility(View.VISIBLE);
                            startButton.setText("start 2nd half");
                            limit = 30001L;
                        }
                    });

                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }
}
