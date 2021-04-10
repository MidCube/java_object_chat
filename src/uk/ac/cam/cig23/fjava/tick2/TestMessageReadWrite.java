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

package uk.ac.cam.cig23.fjava.tick2;


import java.io.*;
import java.net.URL;

class TestMessageReadWrite {

  static boolean writeMessage(String message, String filename) {

    TestMessage myTest = new TestMessage();
    myTest.setMessage(message);
    try {
      FileOutputStream fos = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(myTest);
      out.close();
      return true;
    } catch (IOException noFile) {
      return false;
    }
  }

  static String readMessage(String location) {

    TestMessage myMessage = null;
    if(location.startsWith("http://") || location.startsWith("https://")) {
      try {
        InputStream urlstream = new URL(location).openStream();
        ObjectInputStream inputStream = new ObjectInputStream(urlstream);
        myMessage = (TestMessage) inputStream.readObject();
        inputStream.close();
      } catch (IOException | ClassNotFoundException ex) {
        return null;
      }
    } else {
      try {
        FileInputStream fis = new FileInputStream(location);
        ObjectInputStream inputStream = new ObjectInputStream(fis);
        myMessage = (TestMessage) inputStream.readObject();
        inputStream.close();
      } catch (IOException | ClassNotFoundException ex) {
        return null;
      }

    }
    return myMessage.getMessage();
  }

  public static void main(String args[]) {
    System.out.println(readMessage("https://www.cl.cam.ac.uk/teaching/current/FJava/testmessage/cig23.jobj"));
  }
}
