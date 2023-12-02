package hk.hku.cs.mldemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.Display.Mode
import hk.hku.cs.mldemo.ml.ImageClassifier
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.model.Model.Options
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class ImageClassifierHelper(val context: Context) {
    private var model: ImageClassifier? = null
    private val options: Options
    init {
        val compatList = CompatibilityList()

        options = if(compatList.isDelegateSupportedOnThisDevice) {
            // if the device has a supported GPU, add the GPU delegate
            Options.Builder().setDevice(Model.Device.GPU).build()
        } else {
            // if the GPU is not supported, run on 4 threads
            Options.Builder().setNumThreads(4).build()
        }

        // Initialize the model as usual feeding in the options object
        initModel()
    }

    fun closeModel() {
        model!!.close()
    }

    fun getOutput(bitmap: Bitmap): List<Category> {
        Log.d("TEST", "Getting output")
        if (model == null) {
            initModel()
        }
        val image = TensorImage.fromBitmap(bitmap)
        val outputs = model!!.process(image)
        return outputs.probabilityAsCategoryList
    }

    fun initModel() {
        model = ImageClassifier.newInstance(context, options)
    }

}