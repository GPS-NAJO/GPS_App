package com.example.gpssmsr

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment


class PermissionDialog: DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
            .setMessage("Es necesario tener los permisos de Internet y localización para que la app pueda funcionar")
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Ok") { _, _ ->
                ActivityCompat.requestPermissions(
                    this.requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1
                )
            }
        return builder.create()
    }
}