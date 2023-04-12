# Compose long click crash demo app
A stand-alone app that isolates a crash related to Compose and the long click gesture

While working on a migration to compose, I found a bug that blocked my progress. When long-pressing an item in compose, the project was cancelling pending touch actions by creating a cancel action. This cancel action causes a crash in `androidx.compose.ui.input.pointer.SuspendingPointerInputFilter$PointerEventHandlerCoroutine.offerPointerEvent`:

```kotlin
fun offerPointerEvent(event: PointerEvent, pass: PointerEventPass) {
            if (pass == awaitPass) {
                pointerAwaiter?.run {
                    pointerAwaiter = null
                    resume(event)
                }
            }
        }
```
The culprit code is isolated in [MainActivity](https://github.com/ivanebernal/compose-long-click-crash-demo/blob/main/app/src/main/java/com/ivanebernal/composelongclickappdemo/MainActivity.kt).

## Stacktrace
```
FATAL EXCEPTION: main
Process: com.ivanebernal.composelongclickappdemo, PID: 29880
java.lang.IllegalStateException: Already resumed, but proposed with update androidx.compose.ui.input.pointer.PointerEvent@a1e5058
 	at kotlinx.coroutines.CancellableContinuationImpl.alreadyResumedError(CancellableContinuationImpl.kt:482)
 	at kotlinx.coroutines.CancellableContinuationImpl.resumeImpl(CancellableContinuationImpl.kt:447)
 	at kotlinx.coroutines.CancellableContinuationImpl.resumeImpl$default(CancellableContinuationImpl.kt:420)
 	at kotlinx.coroutines.CancellableContinuationImpl.resumeWith(CancellableContinuationImpl.kt:328)
 	at androidx.compose.ui.input.pointer.SuspendingPointerInputFilter$PointerEventHandlerCoroutine.offerPointerEvent(SuspendingPointerInputFilter.kt:566)
 	at androidx.compose.ui.input.pointer.SuspendingPointerInputFilter.dispatchPointerEvent(SuspendingPointerInputFilter.kt:456)
 	at androidx.compose.ui.input.pointer.SuspendingPointerInputFilter.onCancel(SuspendingPointerInputFilter.kt:505)
 	at androidx.compose.ui.node.BackwardsCompatNode.onCancelPointerInput(BackwardsCompatNode.kt:380)
 	at androidx.compose.ui.input.pointer.Node.dispatchCancel(HitPathTracker.kt:505)
 	at androidx.compose.ui.input.pointer.Node.dispatchCancel(HitPathTracker.kt:504)
 	at androidx.compose.ui.input.pointer.NodeParent.dispatchCancel(HitPathTracker.kt:212)
 	at androidx.compose.ui.input.pointer.HitPathTracker.processCancel(HitPathTracker.kt:120)
 	at androidx.compose.ui.input.pointer.PointerInputEventProcessor.processCancel(PointerInputEventProcessor.kt:125)
 	at androidx.compose.ui.platform.AndroidComposeView.handleMotionEvent-8iAsVTc(AndroidComposeView.android.kt:1283)
 	at androidx.compose.ui.platform.AndroidComposeView.dispatchTouchEvent(AndroidComposeView.android.kt:1246)
 	at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:3091)
 	at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2802)
 	at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:3091)
 	at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2802)
 	at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:3091)
 	at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2802)
 	at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:3091)
 	at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2802)
 	at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:3091)
 	at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2802)
 	at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:3091)
 	at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2802)
 	at com.android.internal.policy.DecorView.superDispatchTouchEvent(DecorView.java:500)
 	at com.android.internal.policy.PhoneWindow.superDispatchTouchEvent(PhoneWindow.java:1905)
 	at android.app.Activity.dispatchTouchEvent(Activity.java:4263)
 	at com.ivanebernal.composelongclickappdemo.MainActivity.dropLateEvents(MainActivity.kt:43)
 	at com.ivanebernal.composelongclickappdemo.MainActivity.access$dropLateEvents(MainActivity.kt:28)
 	at com.ivanebernal.composelongclickappdemo.MainActivity$onCreate$1$1.invoke(MainActivity.kt:34)
 	at com.ivanebernal.composelongclickappdemo.MainActivity$onCreate$1$1.invoke(MainActivity.kt:33)
 	at androidx.compose.foundation.ClickableKt$combinedClickable$4$gesture$1$1$2.invoke-k-4lQ0M(Clickable.kt:350)
 	at androidx.compose.foundation.ClickableKt$combinedClickable$4$gesture$1$1$2.invoke(Clickable.kt:343)
 	at androidx.compose.foundation.gestures.TapGestureDetectorKt$detectTapGestures$2$1.invokeSuspend(TapGestureDetector.kt:128)
 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
 	at kotlinx.coroutines.DispatchedTaskKt.resume(DispatchedTask.kt:178)
 	at kotlinx.coroutines.DispatchedTaskKt.dispatch(DispatchedTask.kt:166)
 	at kotlinx.coroutines.CancellableContinuationImpl.dispatchResume(CancellableContinuationImpl.kt:397)
  at kotlinx.coroutines.CancellableContinuationImpl.resumeImpl(CancellableContinuationImpl.kt:431)
 	at kotlinx.coroutines.CancellableContinuationImpl.resumeImpl$default(CancellableContinuationImpl.kt:420)
 	at kotlinx.coroutines.CancellableContinuationImpl.resumeWith(CancellableContinuationImpl.kt:328)
 	at androidx.compose.ui.input.pointer.SuspendingPointerInputFilter$PointerEventHandlerCoroutine$withTimeout$job$1.invokeSuspend(SuspendingPointerInputFilter.kt:624)
 	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
 	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
 	at androidx.compose.ui.platform.AndroidUiDispatcher.performTrampolineDispatch(AndroidUiDispatcher.android.kt:81)
 	at androidx.compose.ui.platform.AndroidUiDispatcher.access$performTrampolineDispatch(AndroidUiDispatcher.android.kt:41)
 	at androidx.compose.ui.platform.AndroidUiDispatcher$dispatchCallback$1.run(AndroidUiDispatcher.android.kt:57)
 	at android.os.Handler.handleCallback(Handler.java:942)
 	at android.os.Handler.dispatchMessage(Handler.java:99)
 	at android.os.Looper.loopOnce(Looper.java:201)
 	at android.os.Looper.loop(Looper.java:288)
 	at android.app.ActivityThread.main(ActivityThread.java:7872)
 	at java.lang.reflect.Method.invoke(Native Method)
 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:548)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:936)
 	Suppressed: kotlinx.coroutines.DiagnosticCoroutineContextException: [androidx.compose.ui.platform.MotionDurationScaleImpl@cd47bed, androidx.compose.runtime.BroadcastFrameClock@a892a22, StandaloneCoroutine{Cancelling}@f897cb3, AndroidUiDispatcher@6fad270]
```
## Demo
[long-press-crash-demo.webm](https://user-images.githubusercontent.com/16783519/231334579-f688d3ee-7831-4c42-9a9a-1735581e4111.webm)
