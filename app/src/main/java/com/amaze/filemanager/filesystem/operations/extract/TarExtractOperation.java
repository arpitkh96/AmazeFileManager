/*
 * TarExtractOperation.java
 *
 * Copyright (C) 2018 Emmanuel Messulam<emmanuelbendavid@gmail.com>,
 * Raymond Lai <airwave209gt@gmail.com>.
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

package com.amaze.filemanager.filesystem.operations.extract;

import android.content.Context;
import androidx.annotation.NonNull;

import com.amaze.filemanager.filesystem.FileUtil;
import com.amaze.filemanager.filesystem.compressed.CompressedHelper;
import com.amaze.filemanager.asynchronous.management.ServiceWatcherUtil;
import com.amaze.filemanager.filesystem.operations.AbstractExtractOperation;
import com.amaze.filemanager.utils.files.GenericCopyUtil;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TarExtractOperation extends AbstractExtractOperation {

    public TarExtractOperation(@NonNull Context context, @NonNull String filePath,
                               @NonNull String outputPath, @NonNull OnUpdate listener,
                               Filter filter) {
        super(context, filePath, outputPath, listener, filter);
    }

    @Override
    protected void extractWithFilter(@NonNull Filter filter) throws IOException {
        long totalBytes = 0;
        List<TarArchiveEntry> archiveEntries = new ArrayList<>();
        TarArchiveInputStream inputStream = new TarArchiveInputStream(new FileInputStream(filePath));

        TarArchiveEntry tarArchiveEntry;

        while ((tarArchiveEntry = inputStream.getNextTarEntry()) != null) {
            if(CompressedHelper.isEntryPathValid(tarArchiveEntry.getName())) {
                if (filter.shouldExtract(tarArchiveEntry.getName(), tarArchiveEntry.isDirectory())) {
                    archiveEntries.add(tarArchiveEntry);
                    totalBytes += tarArchiveEntry.getSize();
                }
            } else {
                invalidArchiveEntries.add(tarArchiveEntry.getName());
            }
        }

        listener.onStart(totalBytes, archiveEntries.get(0).getName());

        inputStream.close();
        inputStream = new TarArchiveInputStream(new FileInputStream(filePath));

        for (TarArchiveEntry entry : archiveEntries) {
            crashOnCancelled();

            listener.onUpdate(entry.getName());
            //TAR is sequential, you need to walk all the way to the file you want
            while (entry.hashCode() != inputStream.getNextTarEntry().hashCode()) ;
            extractEntry(context, inputStream, entry, outputPath);
        }

        inputStream.close();

        listener.onFinish();
    }

    private void extractEntry(@NonNull final Context context, TarArchiveInputStream inputStream,
                              TarArchiveEntry entry, String outputDir) throws IOException {
        File outputFile = new File(outputDir, fixEntryName(entry.getName()));

        if (!outputFile.getCanonicalPath().startsWith(outputDir)){
            throw new IOException("Incorrect TarArchiveEntry path!");
        }

        if (entry.isDirectory()) {
            FileUtil.mkdir(outputFile, context);
            return;
        }

        if (!outputFile.getParentFile().exists()) {
            FileUtil.mkdir(outputFile.getParentFile(), context);
        }

        BufferedOutputStream outputStream = new BufferedOutputStream(
                FileUtil.getOutputStream(outputFile, context));
        try {
            int len;
            byte buf[] = new byte[GenericCopyUtil.DEFAULT_BUFFER_SIZE];
            while ((len = inputStream.read(buf)) != -1) {
                crashOnCancelled();

                outputStream.write(buf, 0, len);
                ServiceWatcherUtil.position += len;
            }
        } finally {
            outputStream.close();
        }
    }

}