/*
 * Copyright (C) 2014-2020 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com> and Contributors.
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.filemanager.database.daos;

import static com.amaze.filemanager.database.UtilitiesDatabase.COLUMN_PATH;
import static com.amaze.filemanager.database.UtilitiesDatabase.TABLE_GRID;

import com.amaze.filemanager.database.models.utilities.Grid;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * {@link Dao} interface definition for {@link Grid}. Concrete class is generated by Room during
 * build.
 *
 * @see Dao
 * @see Grid
 * @see com.amaze.filemanager.database.UtilitiesDatabase
 */
@Dao
public interface GridEntryDao {

  @Insert
  Completable insert(Grid instance);

  @Update
  Completable update(Grid instance);

  @Query("SELECT " + COLUMN_PATH + " FROM " + TABLE_GRID)
  Observable<String> listPaths();

  @Query("DELETE FROM " + TABLE_GRID + " WHERE " + COLUMN_PATH + " = :path")
  Completable deleteByPath(String path);
}
