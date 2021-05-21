package net.syntessense.app.gandiforwardmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class ListAdapter<T>(ctx: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var inflater: LayoutInflater = LayoutInflater.from(ctx)
    abstract fun onItemClick(v: View, p: Int)
    abstract fun setRepresentation(v: RecyclerView.ViewHolder, p: Int)
    abstract fun getItemView(): Int
    abstract fun getData(): List<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(this, inflater.inflate(getItemView(), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setRepresentation(holder, position)
    }

    class ViewHolder<T> constructor(private var adapter: ListAdapter<T>, itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View) {
            adapter.onItemClick(v, adapterPosition)
        }
    }

}
