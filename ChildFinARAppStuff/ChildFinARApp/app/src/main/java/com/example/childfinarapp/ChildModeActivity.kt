package com.example.childfinarapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


internal class ChildModeActivity : AppCompatActivity() {

    val remoteModelUrl =
        "https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf"

    val localModel = "model.sfb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_mode)

    }
}
