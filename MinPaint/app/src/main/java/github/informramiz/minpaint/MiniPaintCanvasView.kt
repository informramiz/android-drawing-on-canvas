package github.informramiz.minpaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

/**
 * Created by Ramiz Raja on 27/04/2020
 */

private const val STROKE_WIDTH = 12f

class MiniPaintCanvasView(context: Context) : View(context) {
    //we will do our drawing on this bitmap and just supply it to system canvas when needed
    //so it basically serves a cache for us to make changes when next frame of this view is rendered
    private lateinit var canvasBitmap: Bitmap
    //canvas is used to draw over the bitmap so we need this for easy drawing
    private lateinit var extraCanvas: Canvas
    @ColorInt
    private val backgroundColor = ContextCompat.getColor(context, R.color.colorBackground)
    @ColorInt
    private val drawColor = ContextCompat.getColor(context, R.color.colorPaint)
    private val paint = Paint().apply {
        //smooth out the edges of any shape drawn
        isAntiAlias = true
        //color to use for drawing with this paint
        color = drawColor
        //handle down-sampling of a color precision for devices not capable to handle
        // high precision colors
        isDither = true
        //we want the style to be a line and don't want the shape to be filled so
        //set it to stroke
        style = Paint.Style.STROKE
        //how to join line segments, we want it to be round
        strokeJoin = Paint.Join.ROUND
        //how the start and end of lines look
        strokeCap = Paint.Cap.ROUND
        //width of the stroke line
        strokeWidth = STROKE_WIDTH
    }

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //supply our cache bitmap to system canvas, set pain param to null, we will come back to it
        //later
        canvas.drawBitmap(canvasBitmap, 0f, 0f, null)
    }
}