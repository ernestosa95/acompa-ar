package acompanar.ManagementModule.StorageManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.acompanar.R;

import java.util.ArrayList;
import java.util.List;

public class EfectoresSearchAdapter extends ArrayAdapter<String> {

    private Context context;
    private int LIMIT = 5;
    private List<String> strings;

    @NonNull
    @Override
    public Filter getFilter() {
        return new stringsFilter(this, context);
    }

    private class stringsFilter extends Filter {
        private EfectoresSearchAdapter searchAdapter;
        private Context context;

        public stringsFilter(EfectoresSearchAdapter searchAdapter, Context context){
            super();
            this.searchAdapter = searchAdapter;
            this.context = context;
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return super.convertResultToString(resultValue);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            this.searchAdapter.strings.clear();
            FilterResults filterResults = new FilterResults();
            if(constraint == null || constraint.length()==0){
                filterResults.values = new ArrayList<String>();
                filterResults.count = 0;
            }
            else {
                SQLitePpal admin = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
                List<String> resultadosBD = admin.Buscar_Registros(constraint.toString());
                filterResults.values = resultadosBD;
                filterResults.count = resultadosBD.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            this.searchAdapter.strings.clear();
            this.searchAdapter.strings.addAll((List<String>)results.values);
            this.searchAdapter.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_search_layout, null);
        String string = strings.get(position);
        TextView textview = view.findViewById(R.id.txt1);
        textview.setText(string);
        return view;
    }

    @Override
    public int getCount() {
        return Math.min(LIMIT, strings.size());
    }

    public EfectoresSearchAdapter(@NonNull Context context, List<String> strings) {
        super(context, R.layout.contact_search_layout, strings);
        this.context=context;
        this.strings = strings;
    }
}
