package com.maheswara660.quickquiz;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private View layoutQuizView;
    private View layoutResultView;

    private TextView tvProgressText;
    private ProgressBar progressBar;
    private TextView tvQuestionText;

    private RadioGroup radioGroupOptions;
    private RadioButton rbOption1;
    private RadioButton rbOption2;
    private RadioButton rbOption3;
    private RadioButton rbOption4;

    private MaterialButton btnNext;
    private MaterialButton btnRestart;

    private TextView tvScoreSummary;
    private TextView tvPercentage;
    private TextView tvFeedbackMessage;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Apply Window Insets to avoid UI overlapping status bar and navigation bar
        View rootLayout = findViewById(R.id.rootLayout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            int padding20dp = (int) (20 * getResources().getDisplayMetrics().density);
            v.setPadding(
                    systemBars.left + padding20dp,
                    systemBars.top + padding20dp,
                    systemBars.right + padding20dp,
                    systemBars.bottom + padding20dp);
            return windowInsets;
        });

        initViews();
        initQuestions();
        loadQuestion(currentQuestionIndex);

        btnNext.setOnClickListener(v -> handleNextQuestion());
        btnRestart.setOnClickListener(v -> restartQuiz());
    }

    private void initViews() {
        layoutQuizView = findViewById(R.id.layoutQuizView);
        layoutResultView = findViewById(R.id.layoutResultView);

        tvProgressText = findViewById(R.id.tvProgressText);
        progressBar = findViewById(R.id.progressBar);
        tvQuestionText = findViewById(R.id.tvQuestionText);

        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);

        btnNext = findViewById(R.id.btnNext);
        btnRestart = findViewById(R.id.btnRestart);

        tvScoreSummary = findViewById(R.id.tvScoreSummary);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvFeedbackMessage = findViewById(R.id.tvFeedbackMessage);
    }

    private void initQuestions() {
        questionList = new ArrayList<>();

        questionList.add(new Question(
                "Which programming language is primarily used for native Android development alongside Kotlin?",
                new String[] { "Python", "Java", "Swift", "C#" },
                1));

        questionList.add(new Question(
                "What component in Android is used to display a scrollable list of items efficiently?",
                new String[] { "ScrollView", "RecyclerView", "LinearLayout", "TableLayout" },
                1));

        questionList.add(new Question(
                "Which Android component executes long-running background tasks without a user interface?",
                new String[] { "Activity", "BroadcastReceiver", "Service", "ContentProvider" },
                2));

        questionList.add(new Question(
                "What layout positions UI elements relative to each other or to the parent container?",
                new String[] { "FrameLayout", "ConstraintLayout", "AbsoluteLayout", "GridLayout" },
                1));

        questionList.add(new Question(
                "Which XML file contains app configuration metadata such as permissions and launcher Activity?",
                new String[] { "build.gradle", "settings.gradle", "AndroidManifest.xml", "styles.xml" },
                2));

        questionList.add(new Question(
                "Which Material Component widget is commonly used for multiple-choice single selection?",
                new String[] { "CheckBox", "RadioButton", "Switch", "ToggleButton" },
                1));
    }

    private void loadQuestion(int index) {
        Question currentQuestion = questionList.get(index);

        tvProgressText.setText(String.format(Locale.US, "Question %d of %d", index + 1, questionList.size()));

        int progressPercent = (int) (((index + 1) / (float) questionList.size()) * 100);
        progressBar.setProgress(progressPercent);

        tvQuestionText.setText(currentQuestion.getQuestionText());

        String[] options = currentQuestion.getOptions();
        rbOption1.setText(options[0]);
        rbOption2.setText(options[1]);
        rbOption3.setText(options[2]);
        rbOption4.setText(options[3]);

        radioGroupOptions.clearCheck();

        if (index == questionList.size() - 1) {
            btnNext.setText(R.string.finish_quiz);
        } else {
            btnNext.setText(R.string.next_question);
        }
    }

    private void handleNextQuestion() {
        int checkedId = radioGroupOptions.getCheckedRadioButtonId();

        // Input validation: Ensure user selects an option
        if (checkedId == -1) {
            Toast.makeText(this, R.string.please_select_option, Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedOptionIndex = -1;
        if (checkedId == R.id.rbOption1) {
            selectedOptionIndex = 0;
        } else if (checkedId == R.id.rbOption2) {
            selectedOptionIndex = 1;
        } else if (checkedId == R.id.rbOption3) {
            selectedOptionIndex = 2;
        } else if (checkedId == R.id.rbOption4) {
            selectedOptionIndex = 3;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);
        if (selectedOptionIndex == currentQuestion.getCorrectAnswerIndex()) {
            score++;
        }

        currentQuestionIndex++;

        if (currentQuestionIndex < questionList.size()) {
            loadQuestion(currentQuestionIndex);
        } else {
            showResultScreen();
        }
    }

    private void showResultScreen() {
        layoutQuizView.setVisibility(View.GONE);
        layoutResultView.setVisibility(View.VISIBLE);

        int totalQuestions = questionList.size();
        double percentage = ((double) score / totalQuestions) * 100;

        tvScoreSummary.setText(String.format(Locale.US, "%d / %d", score, totalQuestions));
        tvPercentage.setText(String.format(Locale.US, "%.1f%%", percentage));

        if (percentage >= 80.0) {
            tvFeedbackMessage.setText("Outstanding! Master Level 🌟");
        } else if (percentage >= 50.0) {
            tvFeedbackMessage.setText("Good Job! Solid Performance 👍");
        } else {
            tvFeedbackMessage.setText("Keep Practicing! You'll Get There 💪");
        }
    }

    private void restartQuiz() {
        score = 0;
        currentQuestionIndex = 0;

        layoutResultView.setVisibility(View.GONE);
        layoutQuizView.setVisibility(View.VISIBLE);

        loadQuestion(currentQuestionIndex);
    }
}
