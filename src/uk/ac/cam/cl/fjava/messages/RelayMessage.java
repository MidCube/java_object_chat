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

import java.io.Serializable;
import java.util.Date;

public class RelayMessage extends Message implements Serializable {

  private static final long serialVersionUID = 1L;
  private String from;
  private String message;

  public RelayMessage(String from, ChatMessage original) {
    super(original);
    this.from = from;
    this.message = original.getMessage();
  }

  public RelayMessage(String from, String message, Date time) {
    super(time);
    this.from = from;
    this.message = message;
  }

  public String getFrom() {
    return from;
  }

  public String getMessage() {
    return message;
  }
}
