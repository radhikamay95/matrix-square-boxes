package productflavour.viewmodel;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.task.myapplication.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AdapterViewHolder> {
    private Context context;
    private ArrayList<String> colorList;
    private boolean isClicked;
    private ArrayList<String> colorListnonScrambled;
    private int count = 0;
    private IUpdateInterface listener;

    public RecyclerAdapter(ArrayList<String> colorList, Context context) {
        this.context = context;
        this.colorList = colorList;
    }
    public RecyclerAdapter(ArrayList<String> colorList, Context context, IUpdateInterface iUpdateInterface) {
        this.context = context;
        this.colorList = colorList;
        this.listener = iUpdateInterface;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
       // final String currentItem = colorList.get(position);
        colorListnonScrambled = new ArrayList<>(colorList);
        holder.color.setBackgroundColor(Color.parseColor(colorList.get(position)));
        holder.color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                listener.onItemCheck(position, count);
                count = count < 2 ?  count : 0 ;
                if (colorListnonScrambled.equals(colorList)){
                    //checking the scrambled list with colorlist to calculate score
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        private Button color;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.color_button);
        }
    }


}
