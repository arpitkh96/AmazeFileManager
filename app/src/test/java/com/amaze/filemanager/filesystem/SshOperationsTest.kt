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

package com.amaze.filemanager.filesystem

import com.amaze.filemanager.filesystem.ssh.test.MockSshConnectionPools
import com.amaze.filemanager.shadows.ShadowMultiDex
import com.amaze.filemanager.utils.OpenMode
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Test
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAsyncTask

@Config(shadows = [ShadowMultiDex::class, ShadowAsyncTask::class])
class SshOperationsTest : AbstractOperationsTestBase() {

    companion object {
        /**
         * Custom bootstrap function to turn RxJava to fit better in Robolectric tests.
         */
        @BeforeClass
        fun bootstrap() {
            RxJavaPlugins.reset()
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    /**
     * Test case to verify rename SSH file failure scenario.
     *
     * @see AbstractOperationsTestBase.testRenameFileAccessDenied
     */
    @Test
    fun testRenameFileAccessDenied() {
        MockSshConnectionPools.prepareCannotDeleteScenario()
        super.testRenameFileAccessDenied(OpenMode.SFTP,
        "ssh://user:password@127.0.0.1:22222/tmp/old.file",
        "ssh://user:password@127.0.0.1:22222/tmp/new.file")
    }
}