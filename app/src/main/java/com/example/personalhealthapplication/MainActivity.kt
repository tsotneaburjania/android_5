package com.example.personalhealthapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.personalhealthapplication.data.AppDatabase
import com.example.personalhealthapplication.data.Record
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.sign


class MainActivity : AppCompatActivity() {
    private lateinit var records: List<Record>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        val saveBtn: Button = findViewById<View>(R.id.saveBtn) as Button
        val db = createDb()

        getAllAverages(db, executor, handler)

        saveBtn.setOnClickListener {
            val record = Record(
                0,
                findViewById<EditText>(R.id.runningDistanceEt).text.toString().toInt(),
                findViewById<EditText>(R.id.swimmingDistanceEt).text.toString().toInt(),
                findViewById<EditText>(R.id.calorieIntakeEt).text.toString().toInt()
            )
            executor.execute {
                db.recordDao().insertAll(record)
                getAllAverages(db, executor, handler)
                handler.post {
                }
            }
        }


    }

    private fun createDb() : AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "records"
        ).build()
    }

    private fun getAllAverages(db: AppDatabase, executor: ExecutorService, handler: Handler){
        var avgRunDist= 0
        var avgSwimDist = 0
        var avgCalIntake = 0
        executor.execute {
            records = db.recordDao().getAll()
            val runDistRecords: List<Int?> = records.map { it.runningDistance }.toList()
            val swimDistRecords: List<Int?> = records.map { it.swimmingDistance }.toList();
            val calIntakeRecords: List<Int?> = records.map { it.calorieIntake }.toList();

            for (runDistRecord: Int? in runDistRecords) {
                if (runDistRecord != null) {
                    avgRunDist+=runDistRecord
                }
            }
            for (swimDistRecord: Int? in swimDistRecords) {
                if (swimDistRecord != null) {
                    avgSwimDist+=swimDistRecord
                }
            }
            for (calIntakeRecord: Int? in calIntakeRecords) {
                if (calIntakeRecord != null) {
                    avgCalIntake+=calIntakeRecord
                }
            }
            handler.post {
                if (runDistRecords.isNotEmpty() && swimDistRecords.isNotEmpty() && calIntakeRecords.isNotEmpty()){
                    findViewById<TextView>(R.id.runningDistanceTv).text = (avgRunDist / runDistRecords.size).toString()
                    findViewById<TextView>(R.id.swimmingDistanceTv).text = (avgSwimDist / swimDistRecords.size).toString()
                    findViewById<TextView>(R.id.calorieIntakeTv).text = (avgCalIntake / calIntakeRecords.size).toString()
                }

                findViewById<TextView>(R.id.totalRunningDistanceTv).text = avgRunDist.toString()
            }
        }
    }
}