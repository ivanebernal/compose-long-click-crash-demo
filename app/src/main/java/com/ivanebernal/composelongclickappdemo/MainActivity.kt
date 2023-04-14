package com.ivanebernal.composelongclickappdemo

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val activity2Intent = Intent(this, Activity2::class.java)
    val startActivityWithDelay = {
      allowEvents = false
      runBlocking {
        delay(200)
        startActivity(activity2Intent)
      }
    }
    setContent {
      CrashButton(
        onClick = startActivityWithDelay,
        onLongClick = startActivityWithDelay
      )
    }
  }

  override fun onResume() {
    super.onResume()
    allowEvents = true
  }

  private var allowEvents = true
  set(value) {
    val was = field
    field = value
    if (was != value) {
      dropLateEvents()
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    return !allowEvents || super.dispatchTouchEvent(ev)
  }

  override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
    return !allowEvents || super.dispatchKeyEvent(event)
  }

  private fun dropLateEvents() {
    val now = SystemClock.uptimeMillis()
    MotionEvent.obtain(now, now, ACTION_CANCEL, 0.0f, 0.0f, 0).also { cancelEvent ->
      super.dispatchTouchEvent(cancelEvent)
      cancelEvent.recycle()
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CrashButton(
  onClick: () -> Unit = {},
  onLongClick: () -> Unit = {},
) {
  Box(
    modifier = Modifier
      .size(120.dp, 40.dp)
      .background(color = Color.Gray)
      .combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
      )
  ) {
    Text(text = "Long press here!")
  }
}

@Preview
@Composable
fun Preview() {
  CrashButton()
}