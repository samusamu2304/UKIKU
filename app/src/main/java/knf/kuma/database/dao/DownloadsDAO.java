package knf.kuma.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import knf.kuma.pojos.DownloadObject;

@Dao
public interface DownloadsDAO {
    @Query("SELECT * FROM downloadobject")
    DataSource.Factory<Integer, DownloadObject> getAll();

    @Query("SELECT * FROM downloadobject WHERE eid LIKE :eid")
    DownloadObject getByEid(String eid);

    @Query("SELECT * FROM downloadobject WHERE did = :did")
    DownloadObject getByDid(int did);

    @Query("SELECT * FROM downloadobject WHERE file LIKE :name")
    DownloadObject getByFile(String name);

    @Query("SELECT * FROM downloadobject WHERE eid = :eid")
    LiveData<DownloadObject> getLiveByEid(String eid);

    @Query("SELECT * FROM downloadobject WHERE `key` LIKE :key")
    LiveData<DownloadObject> getLiveByKey(int key);

    @Query("SELECT * FROM downloadobject WHERE state<=0")
    LiveData<List<DownloadObject>> getActive();

    @Query("SELECT count(*) FROM downloadobject WHERE state=-1")
    int countPending();

    @Query("SELECT count(*) FROM downloadobject WHERE state=-1 OR state=0")
    int countActive();

    @Query("DELETE FROM downloadobject WHERE eid LIKE :eid")
    void deleteByEid(String eid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DownloadObject object);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(DownloadObject object);

    @Delete
    void delete(DownloadObject object);
}
