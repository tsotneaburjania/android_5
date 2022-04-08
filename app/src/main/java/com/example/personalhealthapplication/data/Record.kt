package com.example.personalhealthapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "running_distance") val runningDistance: Int?,
    @ColumnInfo(name = "swimming_distance") val swimmingDistance: Int?,
    @ColumnInfo(name = "calorie_intake") val calorieIntake: Int?
)
