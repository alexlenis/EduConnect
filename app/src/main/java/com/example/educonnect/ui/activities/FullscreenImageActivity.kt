package com.example.educonnect.ui.activities

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.educonnect.R
import java.io.File
import kotlin.math.abs

class FullscreenImageActivity : AppCompatActivity() {

    private lateinit var ssiv: SubsamplingScaleImageView

    // swipe-down state
    private var downX = 0f
    private var downY = 0f
    private var tracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        ssiv = findViewById(R.id.ssiv)

        val imagePath = intent.getStringExtra("image_path")
        if (imagePath.isNullOrEmpty()) {
            finish()
            return
        }

        val file = File(imagePath)
        if (!file.exists()) {
            finish()
            return
        }


        ssiv.setImage(ImageSource.uri(Uri.fromFile(file)))

        // optional “nice” defaults
        ssiv.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
        ssiv.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
        ssiv.setDoubleTapZoomScale(2.5f) // double-tap zoom
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
                tracking = true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!tracking) return super.dispatchTouchEvent(ev)


                val isZoomed = ssiv.scale > ssiv.minScale + 0.01f
                if (isZoomed) return super.dispatchTouchEvent(ev)

                val dx = ev.rawX - downX
                val dy = ev.rawY - downY


                if (dy > 180 && abs(dy) > abs(dx) * 1.3f) {
                    finish()
                    overridePendingTransition(0, android.R.anim.fade_out)
                    tracking = false
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                tracking = false
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
