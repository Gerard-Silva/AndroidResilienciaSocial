package resiliencia.jc.androidresilienciasocial.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import resiliencia.jc.androidresilienciasocial.R;

public class ShowScoreActivity extends AppCompatActivity {
    TextView TxtScore;
    TextView TxtStatus;
    MediaPlayer audio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);

        TxtScore = findViewById(R.id.txtscore);
        TxtStatus = findViewById(R.id.txtStatus);
        Intent intent = getIntent();
        String scores = String.valueOf(intent.getIntExtra("score", 0));

        TxtScore.setText(scores);
        TxtStatus.setText(setStatus(scores));
        audio.start();
    }

    private String setStatus(String scores){
        int score = Integer.parseInt(scores);

        if(score >= 7){
            audio = MediaPlayer.create(this, R.raw.high_score);
            return "¡Felicidades! Has aprobado, ¿qué opción te satisface más?";
        }

        audio = MediaPlayer.create(this,  R.raw.low_score);
        return "No has aprobado pero no te preocupes, puedes intentarlo nuevamente.";

    }


    public void goToHome(View v){
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
        finish();
    }

}
