package com.example.hannabotar.footballcounter;

import android.support.v4.content.res.ResourcesCompat;
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

    public static final int ONE_POINT = 1;
    public static final String FOUL = "Foul";
    public static final String YELLOW_CARD = "Yellow card";
    public static final String RED_CARD = "Red card";
    public static final String START_SECOND_HALF = "start 2nd half";
    public static final String START_FIRST_HALF = "start 1st half";

    public static final Long FIRST_HALF_END = 15001L; // 45 min = 2700001L;
    public static final Long SECOND_HALF_END = 30001L; // 90 min = 5400001L;

    public static final String GOAL_COLOR_MAIN = "#c1c0c0";
    public static final String GOAL_COLOR_DARK = "#616161";
    public static final String FOUL_COLOR_MAIN = "#CF8003";
    public static final String YELLOW_COLOR_MAIN = "#E1E112";
    public static final String RED_COLOR_MAIN = "#C82211";

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
    private Long limit = FIRST_HALF_END; // 45min + stoppage/injury time, 90min + stoppage/injury time, 2x15min extra time, penalty
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
//            evolution.append("<div>").append(time).append(" - <b><font color='#f39c12'>").append(gameEvolution.get(time)).append("</font></b>").append("</div>");
            evolution.append("<div>").append(time).append(" - ").append(gameEvolution.get(time)).append("</div>");
        }
        evolution.append("</div>");
        Bundle data = new Bundle();
        data.putString(String.valueOf(R.string.evolution_string), evolution.toString());

        EvolutionDialogFragment dialog = new EvolutionDialogFragment();
        dialog.setArguments(data);
        dialog.show(getSupportFragmentManager(), "EvolutionDialogFragment");
    }

    public void updateGameEvolution(String team, String detail, String color) {
        Date date = new Date();

        String timeString = SDF.format(date);

        long millis = time;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String curTime = String.format("%02d : %02d", minutes, seconds);

//        String value = team + "  -  " + detail;

        String value = "<b>" + team + "</b>" + " - " + "<font color='" + color + "'>" + detail + "</font>";


        gameEvolution.put(curTime, value);
        Button showEvolutionButton = (Button) findViewById(R.id.show_hide_button);
        showEvolutionButton.setEnabled(true);
    }

    public void addGoalA(View view) {
        scoreTeamA = scoreTeamA + ONE_POINT;
        scoreListA.add(ONE_POINT);
        displayForTeamA(scoreTeamA);
        updateGameEvolution("Team A", "Goal (" + scoreTeamA + ")", GOAL_COLOR_DARK);
    }
    public void addFoulA(View view) {
        foulTeamA = foulTeamA + ONE_POINT;
        displayFoulsForTeamA(foulTeamA);
        updateGameEvolution("Team A", FOUL, FOUL_COLOR_MAIN);
    }
    public void addYellowA(View view) {
        yellowTeamA = yellowTeamA + ONE_POINT;
        displayYellowCardsForTeamA(yellowTeamA);
        updateGameEvolution("Team A", YELLOW_CARD, YELLOW_COLOR_MAIN);
    }
    public void addRedA(View view) {
        redTeamA = redTeamA + ONE_POINT;
        displayRedCardsForTeamA(redTeamA);
        updateGameEvolution("Team A", RED_CARD, RED_COLOR_MAIN);
    }
    public void addGoalB(View view) {
        scoreTeamB = scoreTeamB + ONE_POINT;
        scoreListB.add(ONE_POINT);
        displayForTeamB(scoreTeamB);
        updateGameEvolution("Team B", "Goal (" + scoreTeamB + ")", GOAL_COLOR_DARK);
    }
    public void addFoulB(View view) {
        foulTeamB = foulTeamB + ONE_POINT;
        displayFoulsForTeamB(foulTeamB);
        updateGameEvolution("Team B", FOUL, FOUL_COLOR_MAIN);
    }
    public void addYellowB(View view) {
        yellowTeamB = yellowTeamB + ONE_POINT;
        displayYellowCardsForTeamB(yellowTeamB);
        updateGameEvolution("Team B", YELLOW_CARD, YELLOW_COLOR_MAIN);
    }
    public void addRedB(View view) {
        redTeamB = redTeamB + ONE_POINT;
        displayRedCardsForTeamB(redTeamB);
        updateGameEvolution("Team B", RED_CARD, RED_COLOR_MAIN);
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
        Button showEvolutionButton = (Button) findViewById(R.id.show_hide_button);
        showEvolutionButton.setEnabled(false);

        scoreListA = new ArrayList<>();
        scoreListB = new ArrayList<>();
        limit = FIRST_HALF_END;
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
        startButton.setText(START_FIRST_HALF);

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

                            if (!startButton.getText().equals(START_SECOND_HALF)) {
                                startButton.setVisibility(View.VISIBLE);
                                startButton.setText(START_SECOND_HALF);
                            }

                            limit = SECOND_HALF_END;
                        }
                    });

                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }
}
