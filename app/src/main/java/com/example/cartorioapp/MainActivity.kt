package com.example.cartorioapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val viewModel: CartorioViewModel by viewModels {
        val database = CartorioDatabase.getDatabase(this)
        CartorioViewModel.Factory(
            CartorioRepository(
                LocalDataSource(database.cartorioDao()),
                RemoteDataSource()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val btnSync = findViewById<Button>(R.id.btnSync)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvLogs = findViewById<TextView>(R.id.tvLogs)
        val logScrollView = findViewById<ScrollView>(R.id.logScrollView)

        val adapter = CartorioAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.cartorios.observe(this) {
            adapter.updateData(it)
        }

        viewModel.isSyncing.observe(this) { isSyncing ->
            progressBar.visibility = if (isSyncing) View.VISIBLE else View.GONE
            btnSync.isEnabled = !isSyncing
        }

        viewModel.statusMessage.observe(this) {
            tvStatus.text = "Status: $it"
        }

        viewModel.logs.observe(this) {
            tvLogs.text = it
            logScrollView.post {
                logScrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        btnSync.setOnClickListener {
            viewModel.syncData()
        }

        viewModel.loadData()
    }
}
