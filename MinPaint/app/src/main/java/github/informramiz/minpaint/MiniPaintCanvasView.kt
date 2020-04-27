package github.informramiz.minpaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import kotlin.math.abs

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
    //path contains the path of what is being drawn, we will use to hold
    //the user touch path. Path in general encapsulates compound
    // geometric paths consisting of straight line segments,
    // quadratic curves, and cubic curves
    private var path = Path()
    //variables to store current position of the motion event
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    //variables to store start of a motion event
    private var currentEventStartX = 0f
    private var currentEventStartY = 0f
    //define touch tolerance before the motion can be considered as drawing
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchDown()
            MotionEvent.ACTION_MOVE -> onTouchMove()
            MotionEvent.ACTION_UP -> onTouchUp()
        }

        return true
    }

    private fun onTouchDown() {
        //reset the path as this is start of the new drawing
        path.reset()
        //mark current event position as the start of the drawing
        currentEventStartX = motionTouchEventX
        currentEventStartY = motionTouchEventY
        //move the path to the start point
        path.moveTo(currentEventStartX, currentEventStartY)
    }

    private fun onTouchMove() {
        //calculate the change in x and y coordinates
        val dx = abs(motionTouchEventX - currentEventStartX)
        val dy = abs(motionTouchEventY - currentEventStartY)
        //check if it is a valid drawing command
        if (dx >= touchTolerance && dy >= touchTolerance) {
            //this is a valid drawing command so store it inside the path
            //quadTo(...) draws smooth arcs and that is what we are interested in
            path.quadTo(currentEventStartX, currentEventStartY,
                (currentEventStartX + motionTouchEventX) / 2, (currentEventStartY + motionTouchEventY) / 2)
            //draw the path to cache canvas which in turn will draw it on the cache bitmap
            extraCanvas.drawPath(path, paint)
            //now the command is drawn, store the new start for next command
            currentEventStartX = motionTouchEventX
            currentEventStartY = motionTouchEventY
            //invalidate the view to request redraw()
            invalidate()
        }
    }

    private fun onTouchUp() {
        //motion event has finished so let's reset the path
        path.reset()
    }
}