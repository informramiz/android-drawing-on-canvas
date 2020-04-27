# android-drawing-on-canvas
A small MiniPaint to show how to draw directly on canvas. The app uses the `MiniPaintCanvasView` directly as the content view of the activity. 

1. A frame is drawn on the canvas
2. The drawing follows the arc of user touch event (touch down -> touch move -> touch up)
3. Drawing commands are stored inside a `Path` object and then cached to a bitmap before they can be drawn collectively for one motion event in the `onDraw()` method.

![demo](demo/demo.gif)
