package com.maheswara660.chrono;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Views
    private View mainRoot;
    private TextView tvTimerMain;
    private TextView tvTimerMs;
    private TextView tvEmptyLaps;
    private MaterialButton btnStart;
    private MaterialButton btnPause;
    private MaterialButton btnLap;
    private MaterialButton btnReset;
    private RecyclerView rvLaps;

    // Lap List & Adapter
    private final List<LapItem> lapList = new ArrayList<>();
    private LapAdapter lapAdapter;

    // Timer State
    private boolean isRunning = false;
    private boolean isPaused = false;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private long lastLapTotalTime = 0L;

    // Handler & Runnable
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long now = SystemClock.elapsedRealtime();
                long currentTotalTime = elapsedTime + (now - startTime);
                updateTimerDisplay(currentTotalTime);
                handler.postDelayed(this, 16); // ~60 FPS smooth updates
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        setupEdgeToEdgePadding();
        setupRecyclerView();
        setupClickListeners();
        updateButtonStates();
        updateTimerDisplay(0L);
    }

    private void initViews() {
        mainRoot = findViewById(R.id.main_root);
        tvTimerMain = findViewById(R.id.tv_timer_main);
        tvTimerMs = findViewById(R.id.tv_timer_ms);
        tvEmptyLaps = findViewById(R.id.tv_empty_laps);
        btnStart = findViewById(R.id.btn_start);
        btnPause = findViewById(R.id.btn_pause);
        btnLap = findViewById(R.id.btn_lap);
        btnReset = findViewById(R.id.btn_reset);
        rvLaps = findViewById(R.id.rv_laps);
    }

    private void setupEdgeToEdgePadding() {
        if (mainRoot != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainRoot, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
                return WindowInsetsCompat.CONSUMED;
            });
        }
    }

    private void setupRecyclerView() {
        lapAdapter = new LapAdapter(lapList);
        rvLaps.setLayoutManager(new LinearLayoutManager(this));
        rvLaps.setAdapter(lapAdapter);
    }

    private void setupClickListeners() {
        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnLap.setOnClickListener(v -> recordLap());
        btnReset.setOnClickListener(v -> resetTimer());
    }

    /**
     * Begins or resumes the stopwatch timer.
     * Prevents duplicate starts if already running.
     */
    private void startTimer() {
        if (isRunning) {
            return;
        }

        startTime = SystemClock.elapsedRealtime();
        isRunning = true;
        isPaused = false;

        handler.removeCallbacks(updateTimerRunnable);
        handler.post(updateTimerRunnable);

        updateButtonStates();
    }

    /**
     * Pauses the running timer temporarily.
     */
    private void pauseTimer() {
        if (!isRunning) {
            return;
        }

        long now = SystemClock.elapsedRealtime();
        elapsedTime += (now - startTime);
        isRunning = false;
        isPaused = true;

        handler.removeCallbacks(updateTimerRunnable);
        updateTimerDisplay(elapsedTime);
        updateButtonStates();
    }

    /**
     * Clears both timer elapsed time and recorded lap times.
     */
    private void resetTimer() {
        isRunning = false;
        isPaused = false;

        handler.removeCallbacks(updateTimerRunnable);

        startTime = 0L;
        elapsedTime = 0L;
        lastLapTotalTime = 0L;

        updateTimerDisplay(0L);

        lapList.clear();
        lapAdapter.notifyDataSetChanged();
        updateEmptyLapsVisibility();

        updateButtonStates();
    }

    /**
     * Records the split and total time for a new lap.
     */
    private void recordLap() {
        if (!isRunning) {
            return;
        }

        long now = SystemClock.elapsedRealtime();
        long currentTotalTime = elapsedTime + (now - startTime);
        long splitTime = currentTotalTime - lastLapTotalTime;
        lastLapTotalTime = currentTotalTime;

        int lapNumber = lapList.size() + 1;
        LapItem lapItem = new LapItem(lapNumber, splitTime, currentTotalTime);

        // Add newest lap at the top of the list for better UX
        lapList.add(0, lapItem);
        lapAdapter.notifyItemInserted(0);
        rvLaps.scrollToPosition(0);

        updateEmptyLapsVisibility();
    }

    /**
     * Updates the digital clock display with minutes, seconds, and milliseconds.
     */
    private void updateTimerDisplay(long totalMillis) {
        long minutes = (totalMillis / 1000) / 60;
        long seconds = (totalMillis / 1000) % 60;
        long hundredths = (totalMillis % 1000) / 10;

        String mainTime = String.format(Locale.US, "%02d:%02d", minutes, seconds);
        String msTime = String.format(Locale.US, ".%02d", hundredths);

        tvTimerMain.setText(mainTime);
        tvTimerMs.setText(msTime);
    }

    /**
     * Manages button enabled/disabled states dynamically.
     */
    private void updateButtonStates() {
        if (isRunning) {
            // Running State
            setButtonEnabled(btnStart, false);
            btnStart.setText(R.string.btn_start);

            setButtonEnabled(btnPause, true);
            setButtonEnabled(btnLap, true);
            setButtonEnabled(btnReset, true);
        } else if (isPaused) {
            // Paused State
            setButtonEnabled(btnStart, true);
            btnStart.setText(R.string.btn_resume);

            setButtonEnabled(btnPause, false);
            setButtonEnabled(btnLap, false);
            setButtonEnabled(btnReset, true);
        } else {
            // Stopped / Initial Reset State
            setButtonEnabled(btnStart, true);
            btnStart.setText(R.string.btn_start);

            setButtonEnabled(btnPause, false);
            setButtonEnabled(btnLap, false);
            setButtonEnabled(btnReset, false);
        }
    }

    private void setButtonEnabled(MaterialButton button, boolean enabled) {
        button.setEnabled(enabled);
        button.setAlpha(enabled ? 1.0f : 0.4f);
    }

    private void updateEmptyLapsVisibility() {
        if (tvEmptyLaps != null) {
            tvEmptyLaps.setVisibility(lapList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimerRunnable);
    }
}
