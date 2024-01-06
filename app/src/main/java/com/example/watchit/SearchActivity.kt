package com.example.watchit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.ComponentActivity

class SearchActivity : ComponentActivity() {

    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_main)

        searchView = findViewById(R.id.searchView)

        searchView.setOnFocusChangeListener { v, hasFocus ->
            Log.d("SearchViewFocus", "SearchView has focus: $hasFocus")
            if(!hasFocus) {
                hideKeyboard(v)
            }
        }
    }

    fun hideKeyboard(view: View?) {
        view?.let {
            val inputMethodManager = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
}