package com.example.hannabotar.footballcounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static final String SCORE_A = "score_a";
    public static final String FOUL_A = "foul_a";
    public static final String YELLOW_A = "yellow_a";
    public static final String RED_A = "red_a";
    public static final String SCORE_B = "score_b";
    public static final String FOUL_B = "foul_b";
    public static final String YELLOW_B = "yellow_b";
    public static final String RED_B = "red_b";

    public static final String START_BUTTON_TEXT = "start_button_text";

    public static final String GAME_EVOLUTION = "game_evolution";
    public static final String SHOW_MENU = "show_menu";

    public static final String RESTORE_TIMER = "restore_timer";
    public static final String TIME = "time";
    public static final String LIMIT = "limit";

    public static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

    private int scoreTeamA; // no need to initialize
    private int foulTeamA;
    private int yellowTeamA;
    private int redTeamA;
    private int scoreTeamB;
    private int foulTeamB;
    private int yellowTeamB;
    private int redTeamB;

    private static final int DEFAULT_SCORE = 0;


    private List<Integer> scoreListA = new ArrayList<>();
    private List<Integer> scoreListB = new ArrayList<>();
    private SortedMap<String, String> gameEvolution = new TreeMap<>();

    private Long time = 0L;
    private Long limit = FIRST_HALF_END; // 45min + stoppage/injury time, 90min + stoppage/injury time, 2x15min extra time, penalty
    Thread t;

    TextView scoreViewA;
    TextView foulViewA;
    TextView yellowCardViewA;
    TextView redCardViewA;
    TextView scoreViewB;
    TextView foulViewB;
    TextView yellowCardViewB;
    TextView redCardViewB;

    boolean showMenu = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scoreViewA = (TextView) findViewById(R.id.team_a_score);
        foulViewA = (TextView) findViewById(R.id.foul_text_a);
        yellowCardViewA = (TextView) findViewById(R.id.yellow_text_a);
        redCardViewA = (TextView) findViewById(R.id.red_text_a);

        scoreViewB = (TextView) findViewById(R.id.team_b_score);
        foulViewB = (TextView) findViewById(R.id.foul_text_b);
        yellowCardViewB = (TextView) findViewById(R.id.yellow_text_b);
        redCardViewB = (TextView) findViewById(R.id.red_text_b);

        displayForTeamA(scoreTeamA);
        displayFoulsForTeamA(foulTeamA);
        displayYellowCardsForTeamA(yellowTeamA);
        displayRedCardsForTeamA(redTeamA);

        displayForTeamB(scoreTeamB);
        displayFoulsForTeamB(foulTeamB);
        displayYellowCardsForTeamB(yellowTeamB);
        displayRedCardsForTeamB(redTeamB);

        enableActionButtons(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (!showMenu) {
            Toast showEvolutionToast = Toast.makeText(this, "No evolution to show", Toast.LENGTH_SHORT);
            showEvolutionToast.show();
            closeOptionsMenu();
            return true;
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_show_evolution) {
            showOrHideEvolution(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SCORE_A, scoreTeamA);
        outState.putInt(FOUL_A, foulTeamA);
        outState.putInt(YELLOW_A, yellowTeamA);
        outState.putInt(RED_A, redTeamA);
        outState.putInt(SCORE_B, scoreTeamB);
        outState.putInt(FOUL_B, foulTeamB);
        outState.putInt(YELLOW_B, yellowTeamB);
        outState.putInt(RED_B, redTeamB);

        Button startButton = (Button) findViewById(R.id.start_button);
        outState.putString(START_BUTTON_TEXT, startButton.getText().toString());

        outState.putSerializable(GAME_EVOLUTION, (TreeMap) gameEvolution);
        outState.putBoolean(SHOW_MENU, showMenu);

        if (t != null && t.isAlive()) {
            outState.putBoolean(RESTORE_TIMER, true);
        } else {
            outState.putBoolean(RESTORE_TIMER, false);
        }

        outState.putLong(TIME, time);
        outState.putLong(LIMIT, limit);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        scoreTeamA = savedInstanceState.getInt(SCORE_A);
        foulTeamA = savedInstanceState.getInt(FOUL_A);
        yellowTeamA = savedInstanceState.getInt(YELLOW_A);
        redTeamA = savedInstanceState.getInt(RED_A);
        scoreTeamB = savedInstanceState.getInt(SCORE_B);
        foulTeamB = savedInstanceState.getInt(FOUL_B);
        yellowTeamB = savedInstanceState.getInt(YELLOW_B);
        redTeamB = savedInstanceState.getInt(RED_B);

        displayForTeamA(scoreTeamA);
        displayFoulsForTeamA(foulTeamA);
        displayYellowCardsForTeamA(yellowTeamA);
        displayRedCardsForTeamA(redTeamA);

        displayForTeamB(scoreTeamB);
        displayFoulsForTeamB(foulTeamB);
        displayYellowCardsForTeamB(yellowTeamB);
        displayRedCardsForTeamB(redTeamB);

        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setText(savedInstanceState.getString(START_BUTTON_TEXT));

        gameEvolution = (TreeMap) savedInstanceState.getSerializable(GAME_EVOLUTION);
        showMenu = savedInstanceState.getBoolean(SHOW_MENU);

        time = savedInstanceState.getLong(TIME);
        limit = savedInstanceState.getLong(LIMIT);
        boolean restoreTimer = savedInstanceState.getBoolean(RESTORE_TIMER);
        if (restoreTimer) {
            startTimer(null);
        }

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
        scoreViewA.setText(String.valueOf(score));
    }
    public void displayFoulsForTeamA(int fouls) {
        foulViewA.setText(String.valueOf(fouls));
    }
    public void displayYellowCardsForTeamA(int yellowCards) {
        yellowCardViewA.setText(String.valueOf(yellowCards));
    }
    public void displayRedCardsForTeamA(int redCards) {
        redCardViewA.setText(String.valueOf(redCards));
    }

    /**
     * Displays the given score for Team B.
     */
    public void displayForTeamB(int score) {
        scoreViewB.setText(String.valueOf(score));
    }
    public void displayFoulsForTeamB(int fouls) {
        foulViewB.setText(String.valueOf(fouls));
    }
    public void displayYellowCardsForTeamB(int yellowCards) {
        yellowCardViewB.setText(String.valueOf(yellowCards));
    }
    public void displayRedCardsForTeamB(int redCards) {
        redCardViewB.setText(String.valueOf(redCards));
    }

    public void displayEvolution() {

        // show evolution in dialog
        StringBuilder evolution = new StringBuilder();
        evolution.append("<div>");for (String time : gameEvolution.keySet()) {
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
        long millis = time;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String curTime = String.format("%02d : %02d", minutes, seconds);

        String value = "<b>" + team + "</b>" + " - " + "<font color='" + color + "'>" + detail + "</font>";

        gameEvolution.put(curTime, value);
        showMenu = true;
    }

    public void addGoalA(View view) {
        scoreTeamA += ONE_POINT;
        scoreListA.add(ONE_POINT);
        displayForTeamA(scoreTeamA);
        updateGameEvolution("Team A", "Goal (" + scoreTeamA + ")", GOAL_COLOR_DARK);
    }
    public void addFoulA(View view) {
        foulTeamA += ONE_POINT;
        displayFoulsForTeamA(foulTeamA);
        updateGameEvolution("Team A", FOUL, FOUL_COLOR_MAIN);
    }
    public void addYellowA(View view) {
        yellowTeamA += ONE_POINT;
        displayYellowCardsForTeamA(yellowTeamA);
        updateGameEvolution("Team A", YELLOW_CARD, YELLOW_COLOR_MAIN);
    }
    public void addRedA(View view) {
        redTeamA += ONE_POINT;
        displayRedCardsForTeamA(redTeamA);
        updateGameEvolution("Team A", RED_CARD, RED_COLOR_MAIN);
    }
    public void addGoalB(View view) {
        scoreTeamB += ONE_POINT;
        scoreListB.add(ONE_POINT);
        displayForTeamB(scoreTeamB);
        updateGameEvolution("Team B", "Goal (" + scoreTeamB + ")", GOAL_COLOR_DARK);
    }
    public void addFoulB(View view) {
        foulTeamB += ONE_POINT;
        displayFoulsForTeamB(foulTeamB);
        updateGameEvolution("Team B", FOUL, FOUL_COLOR_MAIN);
    }
    public void addYellowB(View view) {
        yellowTeamB += ONE_POINT;
        displayYellowCardsForTeamB(yellowTeamB);
        updateGameEvolution("Team B", YELLOW_CARD, YELLOW_COLOR_MAIN);
    }
    public void addRedB(View view) {
        redTeamB += ONE_POINT;
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

        scoreTeamA = DEFAULT_SCORE;
        scoreTeamB = DEFAULT_SCORE;
        displayForTeamA(scoreTeamA);
        displayForTeamB(scoreTeamB);

        foulTeamA = DEFAULT_SCORE;
        foulTeamB = DEFAULT_SCORE;
        displayFoulsForTeamA(foulTeamA);
        displayFoulsForTeamB(foulTeamB);

        yellowTeamA = DEFAULT_SCORE;
        yellowTeamB = DEFAULT_SCORE;
        displayYellowCardsForTeamA(yellowTeamA);
        displayYellowCardsForTeamB(yellowTeamB);

        redTeamA = DEFAULT_SCORE;
        redTeamB = DEFAULT_SCORE;
        displayRedCardsForTeamA(redTeamA);
        displayRedCardsForTeamB(redTeamB);

        gameEvolution.clear();
        showMenu = false;

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
