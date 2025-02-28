package com.nta.newsappwithmvvm.ui

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nta.newsappwithmvvm.R
import com.nta.newsappwithmvvm.databinding.ActivityNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private var nfcAdapter: NfcAdapter? = null

    private var intentFilters = arrayOf<IntentFilter>()
    private lateinit var pendingIntent: PendingIntent

    override fun onResume() {
        super.onResume()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        checkNfcStatusAndPrompt()
        handleNfcIntent(intent)
//        intent.data!!.getQueryParameter()
        // Get the default NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        intentFilters = arrayOf<IntentFilter>(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        )

        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        // Set up BottomNavigationView with NavController
        val navView: BottomNavigationView = binding.bottomNavigationView
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null

        // Configure AppBar with NavController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.breakingNewsFragment -> {
                    navController.navigate(R.id.breakingNewsFragment)
                }

                R.id.savedNewsFragment -> {
                    navController.navigate(R.id.savedNewsFragment)
                }

                R.id.searchNewsFragment -> {
                    navController.navigate(R.id.searchNewsFragment)
                }
            }
            true
        }

    }

    private fun checkNfcStatusAndPrompt() {
        val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device does not support NFC.", Toast.LENGTH_LONG).show()
        } else {
            if (!nfcAdapter.isEnabled) {
                Toast.makeText(
                    this,
                    "NFC is not enabled. Redirecting to settings...",
                    Toast.LENGTH_SHORT
                ).show()

                // Redirect to NFC settings page
                val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                startActivity(intent)
            } else {
                Toast.makeText(this, "NFC is enabled and ready to use!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent) {
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED
        ) {
            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            tag?.let {
                Toast.makeText(this, "NFC tag detected", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
//                    readFromNfc(tag)
                    writeToNfc(tag)
                }, 200)
            }
        }
    }

    private fun readFromNfc(tag: Tag) {
        val ndef = Ndef.get(tag)
        try {
            if (ndef != null) {
                ndef.connect()
                Log.d("DEBUG", "Successfully connected to NFC tag")

                val ndefMessage = ndef.ndefMessage
                if (ndefMessage != null) {
                    for (record in ndefMessage.records) {
                        val payload = record.payload
                        val text = String(payload)
                        Log.d("DEBUG", "Read from NFC tag: $text")
                        Toast.makeText(this, "Read from NFC tag: $text", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("DEBUG", "No NDEF message found on tag")
                    Toast.makeText(this, "No NDEF message on this tag", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("DEBUG", "NDEF is null for this tag")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("DEBUG", "Error connecting to NFC tag: ${e.message}")
        } finally {
            try {
                ndef?.close()
                Log.d("DEBUG", "NFC connection closed")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("DEBUG", "Error closing NFC connection: ${e.message}")
            }
        }
    }

    private fun writeToNfc(tag: Tag, text: String? = "") {
        val ndef = Ndef.get(tag)
        try {
            if (ndef != null) {
                val appUri = "vizcard://carddetails"
                val playStoreUri =
                    "https://play.google.com/store/apps/datasafety?id=com.facebook.katana"
                val ndefMessage = NdefMessage(
                    arrayOf(
                        NdefRecord.createUri(appUri), // Custom URI scheme
                        NdefRecord.createUri(playStoreUri) // Fallback to Play Store
                    )
                )
                ndef.connect()
                ndef.writeNdefMessage(ndefMessage)
                Toast.makeText(this, "Data written to NFC tag", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("DEBUG", "NDEF is null for this tag")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("DEBUG", "Error connecting to NFC tag: ${e.message}")
        } finally {
            try {
                ndef?.close()
                Log.d("DEBUG", "NFC connection closed")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("DEBUG", "Error closing NFC connection: ${e.message}")
            }
        }

//        if (ndef != null) {
//            ndef.connect()
//            val mimeType = "text/plain"
//            val mimeBytes = mimeType.toByteArray(Charsets.US_ASCII)
//            val textBytes = text?.toByteArray()
//            val record = NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, ByteArray(0), textBytes)
//            val message = NdefMessage(arrayOf(record))
//
//            val appUri = "vizcard://carddetails"
//            val playStoreUri = "https://play.google.com/store/apps/datasafety?id=com.facebook.katana"
//            val ndefMessage = NdefMessage(
//                arrayOf(
//                    NdefRecord.createUri(appUri), // Custom URI scheme
//                    NdefRecord.createUri(playStoreUri) // Fallback to Play Store
//                )
//            )
//            try {
//                ndef.writeNdefMessage(ndefMessage)
//                Toast.makeText(this, "Data written to NFC tag", Toast.LENGTH_SHORT).show()
//            } catch (e: IOException) {
//                Log.e("DEBUG", "Error connecting to NFC tag: ${e.message}")
//            } finally {
//                ndef.close()
//            }
//        } else {
//            Toast.makeText(this, "NDEF is not supported by this tag", Toast.LENGTH_SHORT).show()
//        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}