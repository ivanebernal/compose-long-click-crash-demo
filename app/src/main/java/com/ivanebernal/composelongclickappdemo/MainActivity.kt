package com.ivanebernal.composelongclickappdemo

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val activity2Intent = Intent(this, Activity2::class.java)
    setContent {
      CrashButton {
        dropLateEvents()
        startActivity(activity2Intent)
      }
    }
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
fun CrashButton(onLongClick: () -> Unit) {
  val context = LocalContext.current
  Box(
    modifier = Modifier
      .size(120.dp, 40.dp)
      .background(color = Color.Gray)
      .combinedClickable(
        onClick = {
          Toast
            .makeText(context, "Click", Toast.LENGTH_SHORT)
            .show()
        },
        onLongClick = onLongClick
      )
  ) {
    Text(text = "Long press here!")
  }
}

@Preview
@Composable
fun Preview() {
  CrashButton {
    // nothing
  }
}