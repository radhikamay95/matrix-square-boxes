package productflavour.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task.myapplication.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private String userName, userScore, time;

    public UserAdapter(Context context, String name, String score, String time) {
        this.context = context;
        this.userName = name;
        this.userScore = score;
        this.time = time;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.nameText.setText(userName);
        holder.timeText.setText("Timer: " + time);
        if (userScore != null) {
            holder.scoreText.setText("Score: " + userScore);
        }else{
            holder.scoreText.setText("Score: 0" );
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText, scoreText, timeText;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.user_name_tv);
            scoreText = itemView.findViewById(R.id.user_score_tv);
            timeText = itemView.findViewById(R.id.user_time_tv);
        }

    }
}
