package com.example.cartorioapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {

    private val viewModel: CartorioViewModel by viewModels {
        val database = CartorioDatabase.getDatabase(this)
        val fileManager = FileManager(this)
        CartorioViewModel.Factory(
            CartorioRepository(
                LocalDataSource(database.cartorioDao()),
                RemoteDataSource()
            ),
            fileManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnViewFile = findViewById<Button>(R.id.btnViewFile)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
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
            progressBar.visibility = if (isSyncing) View.VISIBLE else View.INVISIBLE
            btnStart.isEnabled = !isSyncing
            btnStop.isEnabled = isSyncing
            spinnerUf.isEnabled = !isSyncing
        }

        viewModel.statusMessage.observe(this) {
            tvStatus.text = "Status: $it"
            if (it.startsWith("Finished")) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        btnStart.setOnClickListener {
            val selectedUf = spinnerUf.selectedItem.toString()
            viewModel.startScrape(selectedUf)
        }

        btnStop.setOnClickListener {
            viewModel.stopScrape()
        }

        btnViewFile.setOnClickListener {
            val lastFile = viewModel.getLastExport()
            if (lastFile != null) {
                shareFile(lastFile)
            } else {
                Toast.makeText(this, "No data file found. Scrape first.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadData()
    }

    private fun shareFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/json")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Open Data File"))
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
