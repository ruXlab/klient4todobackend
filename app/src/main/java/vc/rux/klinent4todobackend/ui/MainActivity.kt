package vc.rux.klinent4todobackend.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import vc.rux.klinent4todobackend.R
import vc.rux.klinent4todobackend.ui.common.ICanAddFragment
import vc.rux.klinent4todobackend.ui.todoservers.TodoServersFragment

class MainActivity : AppCompatActivity(), ICanAddFragment {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            replaceFragment(TodoServersFragment.create())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun addFragment(fragment: Fragment, fragmentId: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment, fragmentId)
            .addToBackStack(fragmentId)
            .commit()
    }

    override fun replaceFragment(fragment: Fragment, fragmentId: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment, fragmentId)
            .commit()
    }
}
