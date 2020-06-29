package pl.edu.mimuw.students.wm382710.appa

import android.content.Context
import androidx.room.*

@Entity(indices = [Index(value = ["heroName"])])
data class Hero(
    @PrimaryKey(autoGenerate = true) val heroId: Int,
    val heroName: String,
    val strength: Int = 6,
    val dexterity: Int = 6,
    val constitution: Int = 6,
    val intelligence: Int = 6
)

@Entity(indices = [Index(value = ["ownerId"])],
    foreignKeys = [ForeignKey(entity = Hero::class, parentColumns = ["heroId"], childColumns = ["ownerId"], onDelete = ForeignKey.CASCADE)])
data class Item(
    @PrimaryKey(autoGenerate = true) val itemId: Int,
    val ownerId: Int,
    val name: String,
    var description: String?
)

data class HeroWithInventory(
    @Embedded val hero: Hero,
    @Relation(
        parentColumn = "heroId",
        entityColumn = "itemId"
    )
    val playlists: List<Item>
)

@Entity
data class AdventureMetadata(
    @PrimaryKey(autoGenerate = true) val adventureId: Int,
    val title: String,
    val description: String?
)

@Dao
abstract class AdventureMetadataDao {
    @Query("SELECT * FROM adventuremetadata WHERE adventureId = :id")
    abstract fun selectMetadata(id: Int): AdventureMetadata

    @Query("SELECT * FROM adventuremetadata ORDER BY title")
    abstract suspend fun selectAllMetadata(): List<AdventureMetadata>
}

@Dao
abstract class HeroDao {
    @Transaction
    @Query("SELECT * FROM hero WHERE heroId = :id")
    abstract fun selectHeroWithInventory(id: Int): HeroWithInventory

    @Query("SELECT * FROM hero")
    abstract fun selectAllHeroes(): List<Hero>

    @Insert
    abstract fun insertHero(hero: Hero)

    @Insert
    abstract fun updateHero(hero: Hero)

    @Delete
    abstract fun deleteHero(hero: Hero);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertItems(vararg items: Item)
}

@Database(entities = [Hero::class, Item::class, AdventureMetadata::class], version = 1)
abstract class AppaDatabase: RoomDatabase() {
    companion object {
        private var database: AppaDatabase? = null

        fun getDatabase(context: Context): AppaDatabase {
            if (database === null)
                database = Room.databaseBuilder(context, AppaDatabase::class.java, "notes.db").build()
            return database!!
        }
    }

    abstract fun heroes(): HeroDao

    abstract fun adventures(): AdventureMetadataDao
}
