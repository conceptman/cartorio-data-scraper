package com.example.cartorioapp

import android.os.Bundle
import android.view.View
import android.widget.*
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
        val btnScrape = findViewById<Button>(R.id.btnScrape)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvLogs = findViewById<TextView>(R.id.tvLogs)
        val logScrollView = findViewById<ScrollView>(R.id.logScrollView)
        val spinnerUf = findViewById<Spinner>(R.id.spinnerUf)

        val ufs = listOf("AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR", "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ufs)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUf.adapter = spinnerAdapter

        val adapter = CartorioAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.cartorios.observe(this) {
            adapter.updateData(it)
        }

        viewModel.isSyncing.observe(this) { isSyncing ->
            progressBar.visibility = if (isSyncing) View.VISIBLE else View.GONE
            btnScrape.isEnabled = !isSyncing
            spinnerUf.isEnabled = !isSyncing
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

        btnScrape.setOnClickListener {
            val selectedUf = spinnerUf.selectedItem.toString()
            viewModel.scrapeState(selectedUf)
        }

        viewModel.loadData()
    }
}
