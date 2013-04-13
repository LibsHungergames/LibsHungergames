package me.libraryaddict.Hungergames.Types;

/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * FileUtils is a collection of routines for common file system operations.
 * 
 * @author Dan Jemiolo (danj)
 * 
 */

public final class FileUtils {

  /**
   * 
   * The application's current working directory.
   * 
   */
  public static final File CURRENT_DIR = new File(".");

  void log(String st) {
    System.out.println(st);
  }

  /**
   * 
   * Copies the first file or directory to the second file or directory. <br>
   * <br>
   * If the first parameter is a file and the second is a file, then the method
   * copies the contents of the first file into the second. If the second file
   * does not exist, it is created. <br>
   * <br>
   * If the first parameter is a file and the second is a directory, the file is
   * copied to the directory, overwriting any existing copy. <br>
   * <br>
   * If the first parameter is a directory and the second is a directory, the
   * first is copied underneath the second. <br>
   * <br>
   * If the first parameter is a directory and the second is a file name or does
   * not exist, a directory with that name is created, and the contents of the
   * first directory are copied there.
   * 
   * @param source
   * @param destination
   * 
   * @throws IOException
   *           <ul>
   *           <li>If the source does not exist.</li>
   *           <li>If the user does not have permission to modify the
   *           destination.</li>
   *           <li>If the copy fails for some reason related to system I/O.</li>
   *           </ul>
   * 
   */
  public static void copy(File source, File destination) throws IOException {
    if (source == null)
      throw new NullPointerException("NullSource");

    if (destination == null)
      throw new NullPointerException("NullDestination");

    if (source.isDirectory())
      copyDirectory(source, destination);

    else
      copyFile(source, destination);
  }

  public static void copyDirectory(File source, File destination)
      throws IOException {
    copyDirectory(source, destination, null);
  }

  public static void copyDirectory(File source, File destination,
      FileFilter filter) throws IOException {
    File nextDirectory = new File(destination, source.getName());

    //
    // create the directory if necessary...
    //
    if (!nextDirectory.exists() && !nextDirectory.mkdirs()) {
      String message = "DirCopyFailed";
      throw new IOException(message);
    }

    File[] files = source.listFiles();

    //
    // and then all the items below the directory...
    //
    for (int n = 0; n < files.length; ++n) {
      if (filter == null || filter.accept(files[n])) {
        if (files[n].isDirectory())
          copyDirectory(files[n], nextDirectory, filter);

        else
          copyFile(files[n], nextDirectory);
      }
    }
  }

  public static void copyFile(File source, File destination) throws IOException {
    //
    // if the destination is a dir, what we really want to do is create
    // a file with the same name in that dir
    //
    if (destination.isDirectory())
      destination = new File(destination, source.getName());
    if (source.getName().equalsIgnoreCase("uid.dat"))
      return;
    FileInputStream input = new FileInputStream(source);
    copyFile(input, destination);
  }

  public static void copyFile(InputStream input, File destination)
      throws IOException {
    OutputStream output = null;

    output = new FileOutputStream(destination);

    byte[] buffer = new byte[1024];

    int bytesRead = input.read(buffer);

    while (bytesRead >= 0) {
      output.write(buffer, 0, bytesRead);
      bytesRead = input.read(buffer);
    }

    input.close();

    output.close();
  }

  public static void clear(File file) {
    if (!file.isDirectory()) {
      System.err.println("Can't clear something that isn't a directory!");
      return;
    }
    File[] files = file.listFiles();
    for (File f : files) {
      if (f.isDirectory()) {
        clear(f);
        f.delete();
      } else {
        f.delete();
      }
    }
  }

}