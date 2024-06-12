package com.example.ngrfitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngrfitness.Data.StepCount;

import java.util.List;

/**
 * Adapter do wyświetlania listy kroków w RecyclerView.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<StepCount> stepCounts; // Lista kroków do wyświetlenia

    /**
     * Konstruktor adaptera.
     *
     * @param stepCounts Lista obiektów StepCount do wyświetlenia.
     */
    public ListAdapter(List<StepCount> stepCounts) {
        this.stepCounts = stepCounts;
    }

    /**
     * Tworzy nowy ViewHolder, gdy nie ma istniejących, które mogą być ponownie użyte.
     *
     * @param parent   Rodzic ViewHoldera.
     * @param viewType Typ widoku.
     * @return Nowo utworzony ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_items, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Łączy dane z danym ViewHolderem.
     *
     * @param holder   ViewHolder, który ma być związany.
     * @param position Pozycja elementu w adapterze.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(stepCounts.get(position).toString());
    }

    /**
     * Zwraca liczbę elementów w adapterze.
     *
     * @return Liczba elementów w adapterze.
     */
    @Override
    public int getItemCount() {
        return stepCounts.size();
    }

    /**
     * Klasa ViewHolder do przechowywania i zarządzania widokami elementów listy.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView; // Tekstowy widok do wyświetlania liczby kroków

        /**
         * Konstruktor ViewHoldera.
         *
         * @param itemView Widok elementu listy.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        /**
         * Zwraca TextView dla danego ViewHoldera.
         *
         * @return TextView do wyświetlania liczby kroków.
         */
        public TextView getTextView() {
            return textView;
        }
    }
}
