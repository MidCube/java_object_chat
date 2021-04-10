/*
 * Copyright 2020 Andrew Rice <acr31@cam.ac.uk>, Alastair Beresford <arb33@cam.ac.uk>, C.I. Griffiths
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.cl.fjava.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class DynamicObjectInputStream extends ObjectInputStream {

  private ClassLoader current = ClassLoader.getSystemClassLoader();

  public DynamicObjectInputStream(InputStream in) throws IOException {
    super(in);
  }

  @Override
  protected Class<?> resolveClass(ObjectStreamClass desc)
      throws IOException, ClassNotFoundException {
    try {
      return current.loadClass(desc.getName());
    } catch (ClassNotFoundException e) {
      return super.resolveClass(desc);
    }
  }

  public void addClass(final String name, final byte[] defn) {
    current =
        new ClassLoader(current) {
          @Override
          protected Class<?> findClass(String className) throws ClassNotFoundException {
            if (className.equals(name)) {
              Class<?> result = defineClass(name, defn, 0, defn.length);
              return result;
            } else {
              throw new ClassNotFoundException();
            }
          }
        };
  }
}
