package mquinn.sign_language.Template;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mquinn.sign_language.R;

public class Template_Adapter extends RecyclerView.Adapter<Template_Adapter.ViewHolder> {

    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;

    public Template_Adapter(Context ctx,List<String> titles,List<Integer> images)
    {
        this.images = images;
        this.titles = titles;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.template_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.Template_Icon.setImageResource(images.get(position));
        holder.Template_Title.setText(titles.get(position));

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView Template_Title;
        ImageView Template_Icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Template_Title = itemView.findViewById(R.id.Template_Title);
            Template_Icon = itemView.findViewById(R.id.Template_Icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

}
