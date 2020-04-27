package github.informramiz.minpaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

/**
 * Created by Ramiz Raja on 27/04/2020
 */
class MiniPaintCanvasView(context: Context) : View(context) {
    private lateinit var canvasBitmap: Bitmap
    private lateinit var extraCanvas: Canvas
    @ColorInt
    private val backgroundColor = ContextCompat.getColor(context, R.color.colorBackground)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //called when size is available so we need a new bitmap and canvas for new size
        //this method is also called when view is first rendered so good time for initialization
        if (this::canvasBitmap.isInitialized && !canvasBitmap.isRecycled) {
            canvasBitmap.recycle()
        }

        canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(canvasBitmap)
        extraCanvas.drawColor(backgroundColor)
    }
}