package github.informramiz.progressbarbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import github.informramiz.progressbarbutton.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }
}
