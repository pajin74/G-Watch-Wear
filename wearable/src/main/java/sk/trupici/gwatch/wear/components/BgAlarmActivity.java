package sk.trupici.gwatch.wear.components;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.wearable.view.WearableDialogActivity;
import android.util.Log;
import android.widget.TextView;

import sk.trupici.gwatch.wear.R;
import sk.trupici.gwatch.wear.util.CommonConstants;
import sk.trupici.gwatch.wear.util.PreferenceUtils;
import sk.trupici.gwatch.wear.util.StringUtils;

public class BgAlarmActivity extends WearableDialogActivity {

    public static final String LOG_TAG = CommonConstants.LOG_TAG;

    final public static String ACTION = "sk.trupici.gwatch.wear.BG_ALARM";

    final public static String EXTRAS_ALARM_CONFIG = "alarm_cfg";
    final public static String EXTRAS_SOUNDS_CONFIG = "sounds_cfg";
    final public static String EXTRAS_BG_VALUE = "bg_value";
    final public static String EXTRAS_ALARM_TEXT = "text";
    final public static String EXTRAS_ALARM_TEXT_COLOR = "color";

    private static final String WAKE_LOCK_TAG = "gwatch.wear:" + BgAlarmActivity.class.getSimpleName() + ".wake_lock";
    private static final long WAKE_LOCK_TIMEOUT_MS = 60000; // 60s
    private PowerManager.WakeLock wakeLock;


    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    // timer to finish the alarm after configured time
    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = () -> {
        stopAlarm();
        finish();
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PowerManager powerManager = (PowerManager)getApplicationContext().getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
        wakeLock.acquire(WAKE_LOCK_TIMEOUT_MS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alarm);

        Bundle extras = getIntent().getExtras();
        final BgAlarmController.AlarmBasicConfig sounds = (BgAlarmController.AlarmBasicConfig) extras.getSerializable(EXTRAS_SOUNDS_CONFIG);
        final BgAlarmController.AlarmConfig alarmConfig = (BgAlarmController.AlarmConfig) extras.getSerializable(EXTRAS_ALARM_CONFIG);
        final String bgValue = extras.getString(EXTRAS_BG_VALUE, null);
        final String text = extras.getString(EXTRAS_ALARM_TEXT, null);
        final int textColor = extras.getInt(EXTRAS_ALARM_TEXT_COLOR, 0);
        if (text == null) {
            Log.e(LOG_TAG, "AlarmActivity: invalid text: " + bgValue);
            return;
        }

        TextView textView = findViewById(R.id.alarm_text);
        textView.setText(text);
        textView.setTextColor(textColor);

        textView = findViewById(R.id.bg_value);
        textView.setText(StringUtils.notNullString(bgValue));
        textView.setTextColor(textColor);

        startAlarm(alarmConfig, sounds);

        findViewById(R.id.snooze_button).setOnClickListener(view -> {
            PreferenceUtils.setLongValue(this, BgAlarmController.PREF_LAST_SNOOZED_AT, System.currentTimeMillis());
            finish();
        });

        findViewById(R.id.dismiss_button).setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onStop() {
        try {
            stopAlarm();
            super.onStop();
        } finally {
            if (wakeLock != null) {
                wakeLock.release();
            }
        }
    }

    private void startAlarm(BgAlarmController.AlarmConfig alarmConfig, BgAlarmController.AlarmBasicConfig sounds) {

        PreferenceUtils.setLongValue(this, BgAlarmController.PREF_LAST_TRIGGERED_AT, System.currentTimeMillis());

        AudioAttributes aa = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .build();

        if (sounds != null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), alarmConfig.soundResId);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.setVolume(alarmConfig.volume, alarmConfig.volume);
                mediaPlayer.setAudioAttributes(aa);
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                mediaPlayer.setOnErrorListener((mp, what, extra) -> false);
            }
        }

        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect effect = createVibrationEffect(alarmConfig.vibrationResId, alarmConfig.intensity);
        vibrator.vibrate(effect, aa);

        // schedule timer to finish
        timerHandler.postDelayed(timerRunnable, alarmConfig.duration * CommonConstants.SECOND_IN_MILLIS);
    }

    protected void stopAlarm() {
        // stop timer
        timerHandler.removeCallbacks(timerRunnable);

        if (vibrator != null) {
            vibrator.cancel();
        }

        /* Your action on positive button clicked. */
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }


    private VibrationEffect createVibrationEffect(int resId, int intensity) {
        if (intensity <= 0) {
            intensity = VibrationEffect.DEFAULT_AMPLITUDE;
        }

        int[] pattern = getResources().getIntArray(resId);
        long[] timings = new long[pattern.length];
        int[] amps = new int[pattern.length];
        for (int i=0; i < pattern.length; i++) {
            timings[i] = pattern[i];
            if ((i & 0x01) == 0) {
                amps[i] = intensity;
            }
        }
        return VibrationEffect.createWaveform(timings, amps, 0);
    }

}
