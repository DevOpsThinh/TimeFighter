/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forever.bee.lets_eat.R
import com.forever.bee.lets_eat.databinding.BookmarkItemBinding
import com.forever.bee.lets_eat.ui.MapsActivity
import com.forever.bee.lets_eat.viewmodel.MapsViewModel

class BookmarkListAdapter(
    private var bookmarkData: List<MapsViewModel.BookmarkView>?,
    private val mapsActivity: MapsActivity
): RecyclerView.Adapter<BookmarkListAdapter.ViewHolder>() {
    class ViewHolder(
        val binding: BookmarkItemBinding,
        private val mapsActivity: MapsActivity
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val bookmarkView = itemView.tag as MapsViewModel.BookmarkView
                mapsActivity.moveToBookmark(bookmarkView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BookmarkItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, mapsActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bookmarkData?.let { list ->
            val bookmarkViewData = list[position]

            holder.binding.root.tag = bookmarkViewData
            holder.binding.bookmarkData = bookmarkViewData
            bookmarkViewData.categoryResourceId?.let {
                holder.binding.bookmarkIcon.setImageResource(it)
            }
        }
    }

    override fun getItemCount() = bookmarkData?.size ?:0

    fun setBookmarkData(bookmarks: List<MapsViewModel.BookmarkView>) {
        this.bookmarkData = bookmarks
        notifyDataSetChanged()
    }
}