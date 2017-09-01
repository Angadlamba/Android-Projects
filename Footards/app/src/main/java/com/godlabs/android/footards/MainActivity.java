package com.godlabs.android.footards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private String mOpponentName;
    private String mUserName;
    private String mUserCardValue;
    private String mOpponentCardValue;

    private EditText mName;

    private LinearLayout mLauncherFrame;
    private RelativeLayout mHeadlineFrame;
    private LinearLayout mCreateFrame;
    private LinearLayout mJoinedFrame;
    private RelativeLayout mLeaveFrame;
    private LinearLayout mComparisonFrame;

    //Socket variable
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.5:3000");
        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLauncherFrame = (LinearLayout) findViewById(R.id.launcherframe);
        mHeadlineFrame = (RelativeLayout) findViewById(R.id.headlineframe);
        mCreateFrame = (LinearLayout) findViewById(R.id.createframe);
        mJoinedFrame = (LinearLayout) findViewById(R.id.joinframe);
        mLeaveFrame = (RelativeLayout) findViewById(R.id.leaveframe);
        mComparisonFrame = (LinearLayout) findViewById(R.id.comparisonframe);

        final Button createButton = (Button) findViewById(R.id.button1);
        final Button joinButton = (Button) findViewById(R.id.button2);
        final Button leaveButton = (Button) findViewById(R.id.button3);

        mName = (EditText) findViewById(R.id.editText);
        mSocket.connect();

        //Listeners
        mSocket.on("joined", onNewMessageJoin);
        mSocket.on("leftroom", onNewMessageLeft);

        if (createButton != null) {
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLauncherFrame.setVisibility(View.INVISIBLE);
                    mCreateFrame.setVisibility(View.VISIBLE);
                    mHeadlineFrame.setVisibility(View.VISIBLE);
                    mLeaveFrame.setVisibility(View.VISIBLE);

                    mUserName = mName.getText().toString();
                    attemptSendCreate();
                    mSocket.on("created", onNewMessageCreate);

                    /*
                    * Todo: Check this Part, When Server is up.
                    * */
                    /*if(mOpponentName != null) {
                        mCreateFrame.setVisibility(View.INVISIBLE);
                        mJoinedFrame.setVisibility(View.VISIBLE);
                    }*/
                }
            });
        }

        if (joinButton != null) {
            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLauncherFrame.setVisibility(View.INVISIBLE);
                    mJoinedFrame.setVisibility(View.VISIBLE);
                    mHeadlineFrame.setVisibility(View.VISIBLE);
                    mLeaveFrame.setVisibility(View.VISIBLE);

                    mUserName = mName.getText().toString();
                    ((TextView) findViewById(R.id.textView15)).setText(mUserName);
                    attemptSendJoin();
                }
            });
        }

        if (leaveButton != null) {
            leaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLauncherFrame.setVisibility(View.VISIBLE);
                    mLeaveFrame.setVisibility(View.INVISIBLE);
                    mHeadlineFrame.setVisibility(View.INVISIBLE);

                    if(mCreateFrame.getVisibility() == View.VISIBLE)
                        mCreateFrame.setVisibility(View.INVISIBLE);
                    else
                        mJoinedFrame.setVisibility(View.INVISIBLE);
                    attemptSendCreate();
                }
            });
        }
    }

    private void attemptSendCreate() {
        String message = mName.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mName.setText("");
        mSocket.emit("create", message);
    }

    private void attemptSendJoin() {
        String message = mName.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mName.setText("");
        mSocket.emit("join", message);
    }

    private Emitter.Listener onNewMessageCreate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    int roomId;
                    try {
                        roomId = data.getInt("str");
                    } catch (JSONException e) {
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "room created with ID: " + roomId , Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessageJoin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        mOpponentName = data.getString("str");
                    } catch (JSONException e) {
                        return;
                    }
                    Toast.makeText(getApplicationContext(), mOpponentName, Toast.LENGTH_LONG).show();
                }
            });
        }

    };

    private Emitter.Listener onNewMessageLeft = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = "Your Opponent Left the Room";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("created", onNewMessageJoin);
        mSocket.off("joined", onNewMessageCreate);
        mSocket.off("leftroom", onNewMessageLeft);
    }

    public void onClickTextView(View view) {
        mUserCardValue = ((TextView) view).getText().toString();
        mLeaveFrame.setVisibility(View.INVISIBLE);
        mHeadlineFrame.setVisibility(View.INVISIBLE);
        mJoinedFrame.setVisibility(View.INVISIBLE);
        mComparisonFrame.setVisibility(View.VISIBLE);

        mComparisonFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLeaveFrame.setVisibility(View.VISIBLE);
                mHeadlineFrame.setVisibility(View.VISIBLE);
                mJoinedFrame.setVisibility(View.VISIBLE);
                mComparisonFrame.setVisibility(View.INVISIBLE);
            }
        }, 1000);
       /*if(mUserCardValue.compareTo(mOpponentCardValue) > 0) {
            //winner!
       }
        else if(mUserCardValue.compareTo(mOpponentCardValue) < 0){
           //loser!
       }
        else {
           //draw!
       }*/
    }
}


