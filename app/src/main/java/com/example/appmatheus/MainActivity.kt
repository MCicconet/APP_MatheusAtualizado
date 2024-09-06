package com.example.appmatheus

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var bAdapter:BluetoothAdapter
    private lateinit var listView: ListView
    private lateinit var arrayAdapter: ArrayAdapter<String>

    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bAdapter = BluetoothAdapter.getDefaultAdapter()
        listView = findViewById(R.id.device_list)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = arrayAdapter

        checkPermissions()
    }

    private fun checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startBluetoothEnableIntent()
            }else{
                Toast.makeText(this, "Permissão Negada!", Toast.LENGTH_SHORT).show()
            }
        }



    }

    fun enableBluetooth(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                startBluetoothEnableIntent()

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_REQUEST_CODE)
            }
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                startBluetoothEnableIntent()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), PERMISSION_REQUEST_CODE)
            }
        }
        /*
        if(!bAdapter.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
            Toast.makeText(this, "Bluetooth ligado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Bluetooth está ligado", Toast.LENGTH_SHORT).show()
        }
        */
    }

    fun disableBluetooth(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                performBluetoothDisable()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_REQUEST_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                performBluetoothDisable()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), PERMISSION_REQUEST_CODE)
            }
        }

        /*
        if(bAdapter.isEnabled){
            bAdapter.disable()
            Toast.makeText(this, "Bluetooth desligado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Bluetooth está desligado", Toast.LENGTH_SHORT).show()
        }
         */
    }
    fun pairDevice(view: View){
        requestBluetoothPermissions()
    }
    fun requestBluetoothPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                listPairedDevices()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_REQUEST_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                listPairedDevices()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), PERMISSION_REQUEST_CODE)
            }
        }
        /*
        if (bAdapter.isEnabled){
            val pairedDevices: Set<BluetoothDevice> = bAdapter.bondedDevices
            if (pairedDevices.isNotEmpty()){
                for (device in pairedDevices){
                    arrayAdapter.add("${device.name} (${device.address})")
                }
            }else{
                Toast.makeText(this, "Nehum dispositivo encontrado", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this, "Bluetooth precisa estar ligado!", Toast.LENGTH_SHORT).show()
        }
         */
    }

    fun discoverDevices(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                startDiscovery()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN), PERMISSION_REQUEST_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                startDiscovery()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), PERMISSION_REQUEST_CODE)
            }
        }


        /*
        if (bAdapter.isEnabled){
            arrayAdapter.clear()
            val discoverDevicesIntent = Intent(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            startActivity(discoverDevicesIntent)
            val devices = bAdapter.bondedDevices
            for (device in devices){
                arrayAdapter.add("${device.name} - ${device.address}")
            }
        }else{
            Toast.makeText(this, "Bluetooth precisar estar ligado!", Toast.LENGTH_SHORT).show()
        }
         */
    }

    private fun startBluetoothEnableIntent() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        try {
            startActivityForResult(enableBtIntent, 1)
            Toast.makeText(this, "Solicitando ativação do Bluetooth", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erro de permissão ao ativar Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performBluetoothDisable() {
        if (bAdapter.isEnabled) {
            try {
                val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                registerReceiver(bluetoothStateReceiver, filter)

                bAdapter.disable()
                Toast.makeText(this, "Desligando Bluetooth...", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!bAdapter.isEnabled) {
                        Toast.makeText(this, "Bluetooth desligado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Falha ao desligar o Bluetooth", Toast.LENGTH_SHORT).show()
                    }

                    unregisterReceiver(bluetoothStateReceiver)

                }, 1000) //1s
            } catch (e: SecurityException) {
                Toast.makeText(this, "Erro ao tentar desligar o Bluetooth", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Bluetooth já está desligado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun listPairedDevices() {
        try {
            if (bAdapter.isEnabled) {
                val pairedDevices: Set<BluetoothDevice> = bAdapter.bondedDevices
                if (pairedDevices.isNotEmpty()) {
                    arrayAdapter.clear()
                    for (device in pairedDevices) {
                        arrayAdapter.add("${device.name} (${device.address})")
                    }
                } else {
                    Toast.makeText(this, "Nenhum dispositivo encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Bluetooth precisa estar ligado!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erro de permissão ao acessar dispositivos Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }
    private fun startDiscovery() {
        try {
            if (bAdapter.isEnabled) {
                arrayAdapter.clear()
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(receiver, filter)
                bAdapter.startDiscovery()
                Toast.makeText(this,"Scan Iniciado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bluetooth precisa estar ligado!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException){
            Toast.makeText(this, "Erro ao iniciar escaneamento de Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                arrayAdapter.add("${device.name} - ${device.address}")
            }
        }
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        Toast.makeText(context, "Bluetooth desligado", Toast.LENGTH_SHORT).show()
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Toast.makeText(context, "Bluetooth está ligado", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Estado do Bluetooth desconhecido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}