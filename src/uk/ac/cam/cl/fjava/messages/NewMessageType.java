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

public class NewMessageType extends Message {

  private static final long serialVersionUID = 1L;
  private String name;
  private byte[] classData;

  public NewMessageType(String name, byte[] classData) {
    super();
    this.name = name;
    this.classData = classData;
  }

  public String getName() {
    return name;
  }

  public byte[] getClassData() {
    return classData;
  }
}
