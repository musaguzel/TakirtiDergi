package com.musaguzel.takirtidergi.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.musaguzel.takirtidergi.R
import com.musaguzel.takirtidergi.adapter.PostAdapter
import com.musaguzel.takirtidergi.model.Posts
import com.musaguzel.takirtidergi.viewmodel.AnaSayfaViewModel
import kotlinx.android.synthetic.main.fragment_ana_sayfa.*

class AnaSayfaFragment : Fragment() {

    private lateinit var viewModel: AnaSayfaViewModel
    private var postAdapter = PostAdapter(arrayListOf())

    private val viewModell by navGraphViewModels<AnaSayfaViewModel>(R.id.screen_navigation)

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ana_sayfa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AnaSayfaViewModel::class.java)
        viewModel.refreshData()

        sharedPreferences = context?.applicationContext?.getSharedPreferences("com.musaguzel.takirtidergi", Context.MODE_PRIVATE)


        //Recycler view bağlama
        recyclerViewAnaSayfa.layoutManager = LinearLayoutManager(context)
        recyclerViewAnaSayfa.adapter = postAdapter


        //Datayı gözlemleme
        observeLiveData()
        //swiperefresh ayarları
        swipeRefreshLayout.setOnRefreshListener {
            recyclerViewAnaSayfa.visibility = View.GONE
            postError.visibility = View.GONE
            anaSayfaLoading.visibility = View.VISIBLE
            viewModel.refreshData()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewModell.listState != null) {
            recyclerViewAnaSayfa.layoutManager?.onRestoreInstanceState(viewModell.listState)
            viewModell.listState = null
        } else {
            //load data normally
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModell.listState = recyclerViewAnaSayfa.layoutManager?.onSaveInstanceState()
    }



    fun observeLiveData() {
        viewModel.fetchPostData().observe(viewLifecycleOwner, Observer { posts ->
            posts?.let {
                recyclerViewAnaSayfa.visibility = View.VISIBLE
                postAdapter.updatePostlist(posts as ArrayList<Posts>)
                /*println("post 0 : " + posts[0].selectCommentIndex)
                println("post 1 : " + posts[1].selectCommentIndex)*/
            }
        })


        viewModel.postError.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                if (it) {
                    postError.visibility = View.VISIBLE
                } else {
                    postError.visibility = View.GONE
                }
            }
        })

        viewModel.postLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (it) {
                    anaSayfaLoading.visibility = View.INVISIBLE
                    recyclerViewAnaSayfa.visibility = View.GONE
                    postError.visibility = View.GONE

                } else {
                    anaSayfaLoading.visibility = View.GONE
                }
            }
        })
    }
}
