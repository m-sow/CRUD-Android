package sn.ept.social.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sn.ept.social.Models.Person;
import sn.ept.social.R;


public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

    /**
     * Create ViewHolder class to bind list item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView prenom;
        public TextView nom;
        public TextView email;

        public ViewHolder(View itemView) {
            super(itemView);

            prenom = (TextView) itemView.findViewById(R.id.tvPrenom);
            nom = (TextView) itemView.findViewById(R.id.tvNom);
            email = (TextView) itemView.findViewById(R.id.tvEmail);
            title=(TextView) itemView.findViewById(R.id.tvTitle);
        }
    }

    private List<Person> mListData;
    private Context mContext;

    public PersonAdapter(Context context, List<Person> listData){
        mListData = listData;
        mContext = context;
    }

    private Context getmContext(){return mContext;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.person_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person m = mListData.get(position);
        holder.prenom.setText(m.getPrenom());
        holder.nom.setText(m.getNom());
        holder.email.setText(m.getEmail());
        holder.title.setText(m.getNom().substring(0,1).toUpperCase());
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }


}
