/*
 * Copyright 2018 John "topjohnwu" Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.topjohnwu.superuser.io;

import android.support.annotation.NonNull;

import com.topjohnwu.superuser.internal.Factory;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class SuRandomAccessFile implements DataInput, DataOutput, Closeable {

    public static SuRandomAccessFile open(String path) throws FileNotFoundException {
        return open(new SuFile(path));
    }

    public static SuRandomAccessFile open(File file) throws FileNotFoundException {
        SuFile f;
        if (file instanceof SuFile)
            f = (SuFile) file;
        else
            f = new SuFile(file);
        if (f.useShell()) {
            // Use shell file io
            return Factory.createShellFileIO(f);
        } else {
            // Create a wrapper over normal RandomAccessFile
            return Factory.createRandomAccessFileWrapper(f);
        }
    }

    @Override
    public void readFully(@NonNull byte[] b, int off, int len) throws IOException {
        if (read(b, off, len) != len)
            throw new EOFException();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }

    public int read() throws IOException {
        byte[] b = new byte[1];
        try {
            readFully(b);
        } catch (EOFException e) {
            return -1;
        }
        return b[0] & 0xFF;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public abstract int read(byte[] b, int off, int len) throws IOException;

    public abstract void seek(long pos) throws IOException;

    public abstract void setLength (long newLength) throws IOException;

    public abstract long length() throws IOException;

    public abstract long getFilePointer() throws IOException;
}
