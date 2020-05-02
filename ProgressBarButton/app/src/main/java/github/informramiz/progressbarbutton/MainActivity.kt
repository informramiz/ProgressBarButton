package github.informramiz.progressbarbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private val progressBarButton: ProgressBarButton by lazy { findViewById<ProgressBarButton>(R.id.progress_button) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
