package hk.hku.cs.mldemo

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var startCamera: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startCamera = findViewById(R.id.startCamera)
        startCamera.setOnClickListener {
            requestSinglePermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getPicture.launch(null)
            } else {
                Toast.makeText(this, "Please give access to camera before proceeding.", Toast.LENGTH_SHORT).show()
            }
        }

    private val getPicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val imageClassifierHelper = ImageClassifierHelper(this)
            val results = imageClassifierHelper.getOutput(bitmap).sortedByDescending { it.score }
            val top3Results = results.subList(0, 3)
            Log.d("TEST", top3Results.toString())
            imageClassifierHelper.closeModel()

            val builder = AlertDialog.Builder(this)
            var message = ""
            top3Results.forEachIndexed { i, category ->
                message += "${i + 1}. ${category.label} with a score of ${category.score}\n"
            }
            builder
                .setTitle("Received results from model")
                .setMessage(message.trim())
                .setNeutralButton("OK!") { dialogInterface: DialogInterface, i: Int ->

                }.show()
        } else {
            Toast.makeText(this, "Unknown error. Try again.", Toast.LENGTH_LONG).show()
        }

    }

}