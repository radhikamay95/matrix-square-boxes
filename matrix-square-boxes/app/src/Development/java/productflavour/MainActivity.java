package productflavour;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task.myapplication.R;
import com.example.task.myapplication.model.User;
import com.example.task.myapplication.viewmodel.IUpdateInterface;
import com.example.task.myapplication.viewmodel.RecyclerAdapter;
import com.example.task.myapplication.viewmodel.UserAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, IUpdateInterface {
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private Button scramble;
    private UserAdapter userAdapter;
    private User user;
    private TextView timerText;
    private String name, score, email;
    private Context context;
    private RecyclerView userRecyclerView, recyclerView;

    private ArrayList<String> scrambledColorCode;
    private ArrayList<String> colorCode;
    private RecyclerAdapter adapter;
    private HashMap<Integer, String> colorMap = new HashMap<>();
    private static int item1, item2;
    private long sec;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference().child("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scramble = findViewById(R.id.scramble_bt);
        timerText = findViewById(R.id.timer);
        userRecyclerView = findViewById(R.id.recyclerview);
        recyclerView = findViewById(R.id.color_recyclerview);
        context = this;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        colorCode = new ArrayList<>();
        colorCode.add("#ff3d00");
        colorCode.add("#ff3d00");
        colorCode.add("#ff3d00");
        colorCode.add("#29B6F6");
        colorCode.add("#29B6F6");
        colorCode.add("#29B6F6");
        colorCode.add("#76ff03");
        colorCode.add("#76ff03");
        colorCode.add("#76ff03");

        adapter = new RecyclerAdapter(MainActivity.this, colorCode);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

        // at last set adapter to recycler view.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setClickable(false);
        scramble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getScramble();
               // storeData();

            }
        });
    }

    private void timer() {
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sec = (millisUntilFinished / 1000) % 60;
                timerText.setVisibility(View.VISIBLE);
                timerText.setText(sec + " " + "Secs");
                scramble.setClickable(false);
            }

            @Override
            public void onFinish() {
                timerText.setVisibility(View.GONE);
                scramble.setClickable(true);
                storeData();
                getData();
                getScramble();
            }
        }.start();
    }

    private void storeData() {
        //store data to db
        String userId = databaseReference.push().getKey();
        user = new User();
        user.setUseriD(userId);
        user.setUserName(name);
        user.setUserScore(getScoreCalculate());
        user.setTime(String.valueOf(sec));
        databaseReference.setValue(user);
    }


    private void getData() {
        // write & Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //getter data from firebase
                name = user.getUserName();
                score = user.getUserScore();
                userAdapter = new UserAdapter(context, name, score, user.getTime());
                userRecyclerView.setAdapter(userAdapter);
                //  Log.d("TAG-----", "Value is: " + name + " " + score);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }

    private String getScoreCalculate() {
        if (scrambledColorCode.equals(colorCode)) {
            if (sec >= 10) {
                score = "10";
            } else if (sec >= 20) {
                score = "20";
            } else {
                score = "30";
            }
        } else {
            score = "0";
        }
        return score;
    }

    private void getScramble() {
        recyclerView.setClickable(true);
        //list of static color codes for 4x4 proposition
        int[] colors = getRandomNo();
        for (int i = 0; i < colors.length; i++) {
            colorMap.put(colors[i], colorCode.get(i));
        }
        scrambledColorCode = new ArrayList<>();
        for (String s : colorMap.values()) {
            scrambledColorCode.add(s);
        }
        Log.i("beforetag--", scrambledColorCode.toString());
        adapter = new RecyclerAdapter(this, scrambledColorCode, (IUpdateInterface) MainActivity.this);
        recyclerView.setAdapter(adapter);
        timer();

    }


    @Override
    public void onItemCheck(int currentPosition, int count) {
        if (count == 2) {
            item2 = currentPosition;
            Collections.swap(scrambledColorCode, item1, item2);
            Log.i("aftertag--", scrambledColorCode.toString());
            adapter.notifyDataSetChanged();
        } else {
            item1 = currentPosition;
        }
    }

    /*
       method to get random number of arr for each proposition
      */
    private int[] getRandomNo() {

        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        Random r = new Random();

        for (int i = 9 - 1; i > 0; i--) {

            // Pick a random index from 0 to i
            int j = r.nextInt(i);

            // Swap arr[i] with the element at random index
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }


    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    /*
      method to get google sign in result
      */
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /*
     sign out/log out menu in toolbar with action
      */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Intent intent = new Intent(this, LoginActivity.class);
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(2);
    }


}