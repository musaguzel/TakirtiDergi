package com.musaguzel.takirtidergi.adapter

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.musaguzel.takirtidergi.R
import com.musaguzel.takirtidergi.firestore.Repository
import com.musaguzel.takirtidergi.model.Posts
import com.musaguzel.takirtidergi.util.getImageFromFirebase
import com.musaguzel.takirtidergi.util.placeholderShimmer
import com.musaguzel.takirtidergi.viewmodel.AnaSayfaViewModel
import kotlinx.android.synthetic.main.recycler_posts.view.*

class PostAdapter(val postList: ArrayList<Posts>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    }


    //recycler xml ile adaptörü bağlama işlemi burada yapılıyor
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_posts, parent, false)
        return PostViewHolder(view)

    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        val post: Posts = postList[position]
        val repo = Repository()
        var fieldstring: String
        val selectedIndex = AnaSayfaViewModel.SelectedPosition
        val selectedList = selectedIndex.selectedIndexList
        val sharedPreferences = holder.view.context.applicationContext.getSharedPreferences(
            "com.musaguzel.takirtidergi",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()


        holder.setIsRecyclable(false);

        val commentList = ArrayList<TextView>()
        commentList.add(holder.view.firstCommentText)
        commentList.add(holder.view.secondCommentText)                        //Textviewleri listeye koyma
        commentList.add(holder.view.thirdCommentText)


        for (n in postList.indices) {
            if (selectedList.contains(postList[n].documentId)) {               //tıklamaları kontrol etme
                postList[n].selectCommentIndex = true
            }
        }


        for (i in commentList.indices) {
            commentList[i].text = post.CommentList[i]                        //yorumları gösterme
        }


        if (post.selectCommentIndex == true) {                                //yorum tıklandıgında tüm yorumları tıklanamaz yapma
            for (all in commentList.indices) {
                commentList[all].isEnabled = false
            }
        }

        //Resim bağlama
        holder.view.recyclerImageView.getImageFromFirebase(
            postList[position].imageUrl,
            placeholderShimmer(holder.view.context)
        )


        val commentClickList = ArrayList<Double>()
        commentClickList.add(post.CommentClick0)
        commentClickList.add(post.CommentClick1)                              //Tıklanma sayılarını listeye alma
        commentClickList.add(post.CommentClick2)


        val docUUID: String = postList[position].documentId                   //Döküman id alma

        calculatePercent(
            post.CommentClick0,
            post.CommentClick1,
            post.CommentClick2,
            holder,
            position,
            selectedList
        )   //Yüzdelikleri Hesapla

        for (i in commentList.indices) {

            commentList[i].setOnClickListener {


                selectedIndex.selectedIndexList.add(docUUID)


                if (!selectedList.isEmpty()) {

                    editor.putStringSet("uuidset", selectedList).apply()
                    editor.commit()

                    /*editor.putInt("post" + t , selectedList[t])?.apply( )
                    editor.commit()*/
                    //}
                }


                for (all in commentList.indices) {
                    commentList[all].isEnabled = false
                }

                //veritabanına veri gönderme
                fieldstring =
                    "CommentClick" + i        //tıklanma sayısını güncellemek için field ismini alma
                val newCount = commentClickList[i] + 1 //yeni yorum sayısını belirleme
                repo.updateField(
                    docUUID,
                    fieldstring,
                    newCount
                ) // yeni tıklanma sayısını güncelleme

                //click sonrası görünüm ve tıklama oranları hesaplama
                //setViewAfterClicking(holder, it.context.applicationContext)

                calculatePercent(
                    post.CommentClick0,
                    post.CommentClick1,
                    post.CommentClick2,
                    holder,
                    position,
                    selectedList
                )  //yüzdeleri hesaplama

            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return position  //return super.getItemViewType(position)
    }


    //Kaç satır olacağını istiyor
    override fun getItemCount(): Int {
        return postList.size
    }


    fun updatePostlist(newPostList: ArrayList<Posts>) {
        postList.clear()
        postList.addAll(newPostList)
        notifyDataSetChanged()
    }

    fun calculatePercent(
        count1: Double,
        count2: Double,
        count3: Double,
        holder: PostViewHolder,
        position: Int,
        selectedList: HashSet<String>
    ) {
        //tıklama sayılarını toplama
        val total: Double = count1 + count2 + count3
        // yüzdelerini alma
        val percent1: Double = (count1 / total) * 100
        val percent2: Double = (count2 / total) * 100
        val percent3: Double = (count3 / total) * 100
        //textleri ve progressleri ayarlama
        holder.view.firstCommentPercent.text = String.format("%.0f%%", percent1)
        holder.view.seek_bar1.progress = percent1.toInt()

        holder.view.secondCommentPercent.text = String.format("%.0f%%", percent2)
        holder.view.seek_bar2.progress = percent2.toInt()

        holder.view.thirdCommentPercent.text = String.format("%.0f%%", percent3)
        holder.view.seek_bar3.progress = percent3.toInt()
    }

    fun setViewAfterClicking(holder: PostViewHolder, position: Int, selectedList: HashSet<String>) {
        if (selectedList.contains((postList[position].documentId))) {
            var progressTrack2: Drawable? = ContextCompat.getDrawable(
                holder.view.context.applicationContext,
                R.drawable.progress_track_2
            )

            holder.view.seek_bar1.progressDrawable = progressTrack2
            holder.view.seek_bar2.progressDrawable = progressTrack2
            holder.view.seek_bar3.progressDrawable = progressTrack2
        }
    }
}