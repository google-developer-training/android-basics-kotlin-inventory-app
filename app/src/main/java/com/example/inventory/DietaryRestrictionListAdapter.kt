package com.example.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DietaryRestrictionListAdapter(private val dietaryRestriction: List<String>) : RecyclerView.Adapter<DietaryRestrictionListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dietary_restriction_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dietaryRestriction.text = dietaryRestriction[position]
    }

    override fun getItemCount(): Int {
        return dietaryRestriction.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dietaryRestriction: TextView = itemView.findViewById(R.id.dietary_restriction_name)
    }
}





